/**
 * Copyright (C) 2014-2015 SINTEF
 *
 *     Brian Elvesæter <brian.elvesater@sintef.no>
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


import eu.proasense.internal.ComplexValue;
import eu.proasense.internal.SimpleEvent;
import eu.proasense.internal.VariableType;

import kafka.consumer.Consumer;
import kafka.consumer.ConsumerConfig;
import kafka.consumer.ConsumerIterator;
import kafka.consumer.ConsumerTimeoutException;
import kafka.consumer.KafkaStream;

import kafka.javaapi.consumer.ConsumerConnector;

import org.joda.time.DateTime;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ExecutorService;


public class MeasurementEventKafkaConsumer {
	private final ConsumerConnector consumer;
	private final String topic;
	private ExecutorService executor;

	public MeasurementEventKafkaConsumer(String a_zookeeper, String a_groupId, String a_topic) {
        topic = a_topic;

        // Specify some consumer properties
        Properties props = new Properties();
        props.put("zookeeper.connect", a_zookeeper);
        props.put("zookeeper.connection.timeout.ms", "1000000");
        props.put("group.id", a_groupId);

        // Create the connection to the cluster
        ConsumerConfig config = new ConsumerConfig(props);
        consumer = Consumer.createJavaConsumerConnector(config);

        // Consume message
        Map<String, Integer> topicCountMap = new HashMap<String, Integer>();
        topicCountMap.put(a_topic, 1);
        Map<String, List<KafkaStream<byte[], byte[]>>> streams = consumer.createMessageStreams(topicCountMap);
        KafkaStream<byte[], byte[]> stream = streams.get(a_topic).get(0);

        ConsumerIterator<byte[], byte[]> it = stream.iterator();
        String msg;

        try {
            while (it.hasNext()) {
                msg = new String(it.next().message());
                System.out.println("Message: " + msg);
            }
        } catch (ConsumerTimeoutException ignore) {
        } finally {
            consumer.commitOffsets();
            consumer.shutdown();
        }
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

        long timestamp = new DateTime(parts[2]).getMillis();
        String sensorId = parts[0];

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

        return event;

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

        return sensorId;
    };

}
