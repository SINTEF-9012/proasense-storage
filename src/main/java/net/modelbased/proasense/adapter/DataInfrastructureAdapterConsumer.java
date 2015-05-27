/**
 * Copyright 2015 Brian Elvesæter <${email}>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package net.modelbased.proasense.adapter;

import net.modelbased.proasense.model.Sensor;
import net.modelbased.proasense.rest.RestRequest;
import net.modelbased.proasense.model.JsonPrinter;

import eu.proasense.internal.ComplexValue;
import eu.proasense.internal.SimpleEvent;
import eu.proasense.internal.VariableType;

import kafka.consumer.Consumer;
import kafka.consumer.ConsumerConfig;
import kafka.consumer.ConsumerIterator;
import kafka.consumer.ConsumerTimeoutException;
import kafka.consumer.KafkaStream;

import kafka.javaapi.consumer.ConsumerConnector;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.thrift.TException;
import org.apache.thrift.TSerializer;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.joda.time.DateTime;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public class DataInfrastructureAdapterConsumer {
	private final ConsumerConnector consumer;
    private final KafkaProducer<String, byte[]> producer;
    private final Sensor sensor;
	private String topic;
	private ExecutorService executor;


	public DataInfrastructureAdapterConsumer(String a_zookeeper, String a_groupId, String a_topic) {
        topic = a_topic;

        consumer = createConsumer(a_zookeeper, a_groupId);
        producer = createProducer(a_zookeeper);
        sensor = createSensAppSensor("http://127.0.0.1:8090", "MHWirth/DDM.HookLoad");

        // Consume message
        Map<String, Integer> topicCountMap = new HashMap<String, Integer>();
        topicCountMap.put(a_topic, 1);
        Map<String, List<KafkaStream<byte[], byte[]>>> streams = consumer.createMessageStreams(topicCountMap);
        KafkaStream<byte[], byte[]> stream = streams.get(a_topic).get(0);

        ConsumerIterator<byte[], byte[]> it = stream.iterator();
        String msg;
        int cnt = 0;

        try {
            while (it.hasNext()) {
                cnt++;
                msg = new String(it.next().message());
//                System.out.println("Message: " + msg);

                // Convert to Apache Thrift message
                SimpleEvent event = convertToSimpleEvent(msg);
                if (cnt % 100 == 0)
                    System.out.println("SimpleEvent(" + cnt + "): " + event.toString());

                // Serialize message
                TSerializer serializer = new TSerializer(new TBinaryProtocol.Factory());
                byte[] bytes = serializer.serialize(event);

                // Publish message
                ProducerRecord<String, byte[]> measurement = new ProducerRecord<String, byte[]>(this.topic, bytes);
                producer.send(measurement);
            }
        } catch (ConsumerTimeoutException ignore) {
        } catch (TException ignore) {
        } finally {
            consumer.commitOffsets();
            consumer.shutdown();
            producer.close();
        }
	}


	public void shutdown() {
        if (consumer != null) consumer.shutdown();
        if (executor != null) executor.shutdown();
	}
	

    public void run(int a_numThreads) {
        Map<String, Integer> topicCountMap = new HashMap<String, Integer>();
        topicCountMap.put(topic, new Integer(a_numThreads));
        Map<String, List<KafkaStream<byte[], byte[]>>> consumerMap = consumer.createMessageStreams(topicCountMap);
        List<KafkaStream<byte[], byte[]>> streams = consumerMap.get(topic);
 
        // now launch all the threads
        executor = Executors.newFixedThreadPool(a_numThreads);
 
        // now create an object to consume the messages
        int threadNumber = 0;
        for (final KafkaStream stream : streams) {
//            executor.submit(new ConsumerTest(stream, threadNumber));
            threadNumber++;
        }
    }
    

    public static void main(String[] args) {
//        String zooKeeper = args[0];
//        String groupId = args[1];
//        String topic = args[2];
//        int threads = Integer.parseInt(args[3]);

        String zooKeeper = "89.216.116.44:2181";
        String groupId = "MHWirthGroup1";
        String topic = "proasense.datainfrastructure.mhwirth.all3";
        int threads = 4;

        DataInfrastructureAdapterConsumer example = new DataInfrastructureAdapterConsumer(zooKeeper, groupId, topic);
/**
        example.run(threads);
 
        try {
            Thread.sleep(10000);
        } catch (InterruptedException ie) {
 
        }
        example.shutdown();
 **/
    }


    private SimpleEvent convertToSimpleEvent(String input) {
        // Input format: "{\"variable_type\":\"1002311\",\"value\":138.2346,\"variable_timestamp\":\"2014-02-14T07:01:24.133Z\"}";

        input = input.replace("\"", "");
        input = input.replace("{", "");
        input = input.replace("variable_type:", "");
        input = input.replace("value:", "");
        input = input.replace("variable_timestamp:", "");
        input = input.replace("}", "");

        String[] parts = input.split(",");
//        System.out.println("parts[0] = " + parts[0]);
//        System.out.println("parts[1] = " + parts[1]);
//        System.out.println("parts[2] = " + parts[2]);

        long timestamp = new DateTime(parts[2]).getMillis();
        String sensorId = parts[0];

        // Set topic
        this.topic = convertTagToTopic(sensorId);
//        System.out.println("Topic: " + this.topic);

        // Define complex value
        ComplexValue value = new ComplexValue();
        value.setValue(parts[1]);
        value.setType(VariableType.LONG);

        // Define measurement
        Map<String, ComplexValue> measurement = new HashMap<String, ComplexValue>();
        measurement.put("raw.value", value);

        SimpleEvent event = new SimpleEvent();
        event.setTimestamp(timestamp);
        event.setSensorId(convertTagToSensorId(sensorId));
        event.setEventProperties(measurement);

//        System.out.println("SimpleEvent: " + storage.toString());

        return event;
    }


    private ConsumerConnector createConsumer(String a_zookeeper, String a_groupId) {
        // Specify consumer properties
        Properties props = new Properties();
        props.put("zookeeper.connect", a_zookeeper);
        props.put("zookeeper.connection.timeout.ms", "1000000");
        props.put("group.id", a_groupId);

        // Create the connection to the cluster
        ConsumerConfig config = new ConsumerConfig(props);
        ConsumerConnector consumer = Consumer.createJavaConsumerConnector(config);

        return consumer;
    }


    private KafkaProducer<String, byte[]> createProducer(String a_zookeeper) {
        // Specify producer properties
        Properties props = new Properties();
        props.put("bootstrap.servers", "89.216.116.44:9092");
        props.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        props.put("value.serializer", "org.apache.kafka.common.serialization.ByteArraySerializer");

        // Define the producer object
        KafkaProducer<String, byte[]> producer = new KafkaProducer<String, byte[]>(props);

        return producer;
    }


    public Sensor createSensAppSensor(String url, String sensorName) {
        Sensor sensor = null;

        try {
            sensor = new Sensor(sensorName, new URI(url), "", "raw", "Numerical","", false);
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }

        String js = JsonPrinter.sensorToJson(sensor);
        if(!RestRequest.isSensorRegistred(sensor))
            RestRequest.postSensor(sensor);

        return sensor;
    }


    private String convertTagToSensorId(String tag) {
        String sensorId = "";

        if(tag.matches("1000693"))
            sensorId = "MHWirth.DDM.DrillingRPM";
        else if(tag.matches("1000700"))
            sensorId = "MHWirth.DDM.DrillingTorque";
        else if(tag.matches("1002311"))
            sensorId = "MHWirth.DDM.HookLoad";
        else if(tag.matches("1000695"))
            sensorId = "MHWirth.DDM.GearLubeOilTemp";
        else if(tag.matches("1000692"))
            sensorId = "MHWirth.DDM.GearBoxPressure";
        else if(tag.matches("1000696"))
            sensorId = "MHWirth.DDM.SwivelOilTemp";
        else if(tag.matches("1002123"))
            sensorId = "MHWirth.DrillBit.WeightOnBit";
        else if(tag.matches("1033619"))
            sensorId = "MHWirth.DrillBit.WeightOnBit";

        else if(tag.matches("1002113"))
            sensorId = "MHWirth.Ram.PositionSetPoint";
        else if(tag.matches("1002115"))
            sensorId = "MHWirth.Ram.PositionMeasuredValue";
        else if(tag.matches("1002114"))
            sensorId = "MHWirth.Ram.VelocitySetPoint";
        else if(tag.matches("1002116"))
            sensorId = "MHWirth.Ram.VelocityMeasuredValue";
        else if(tag.matches("1002127"))
            sensorId = "MHWirth.Rig.MRUPosition";
        else if(tag.matches("1002128"))
            sensorId = "MHWirth.Rig.MRUVelocity";

        return sensorId;
    };


    private String convertTagToTopic(String tag) {
        String topic = "proasense.simpleevent.mhwirth." + tag;

        return topic;
    }

}
