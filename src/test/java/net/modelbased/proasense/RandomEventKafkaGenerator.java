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
package net.modelbased.proasense;

import eu.proasense.internal.AnomalyEvent;
import eu.proasense.internal.DerivedEvent;
import eu.proasense.internal.PredictedEvent;
import eu.proasense.internal.RecommendationEvent;
import eu.proasense.internal.RecommendationStatus;
import eu.proasense.internal.SimpleEvent;
import net.modelbased.proasense.storage.EventProperties;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.thrift.TException;
import org.apache.thrift.TSerializer;
import org.apache.thrift.protocol.TBinaryProtocol;

import java.util.Properties;


public class RandomEventKafkaGenerator<T> implements Runnable {
    private Class<T> eventType;
    private String zooKeeper;
    private String groupId;
    private String topic;
    private String collectionId;
    private int sleep;
    private int max;
    private EventGenerator eventGenerator;


    public RandomEventKafkaGenerator(Class<T> eventType, String zooKeeper, String groupId, String topic, String collectionId, int sleep, int max) {
        this.eventType = eventType;
        this.zooKeeper = zooKeeper;
        this.groupId = groupId;
        this.topic = topic;
        this.collectionId = collectionId;
        this.sleep = sleep;
        this.max = max;
        this.eventGenerator = new EventGenerator();
    }


    public void run() {
        KafkaProducer<String, byte[]> producer = createProducer(zooKeeper);

        int cnt = 0;
        try {
            while (cnt < this.max) {
                cnt++;
                Thread.sleep(this.sleep);

                String eventTypeName = eventType.getName();

                // Generate simple event with random values
                if (eventTypeName.matches(EventProperties.SIMPLEEVENT_CLASS_NAME)) {
                    T event = (T) eventGenerator.generateSimpleEvent(this.collectionId);

                    // Serialize message
                    TSerializer serializer = new TSerializer(new TBinaryProtocol.Factory());
                    byte[] bytes = serializer.serialize((SimpleEvent) event);

                    // Publish message
                    ProducerRecord<String, byte[]> message = new ProducerRecord<String, byte[]>(this.topic, bytes);
                    producer.send(message);

//                    if (cnt % 1000 == 0)
//                        System.out.println("SimpleEvent(" + cnt + "): " + event.toString());
                }

                // Generate derived event with random values
                if (eventTypeName.matches(EventProperties.DERIVEDEVENT_CLASS_NAME)) {
//                    T event = (T) eventGenerator.generateDerivedEvent(EventProperties.DERIVEDEVENT_STORAGE_COLLECTION_NAME);
                    T event = (T) eventGenerator.generateDerivedEvent(this.collectionId);

                    // Serialize message
                    TSerializer serializer = new TSerializer(new TBinaryProtocol.Factory());
                    byte[] bytes = serializer.serialize((DerivedEvent) event);

                    // Publish message
                    ProducerRecord<String, byte[]> message = new ProducerRecord<String, byte[]>(this.topic, bytes);
                    producer.send(message);

//                    if (cnt % 1000 == 0)
//                        System.out.println("DerivedEvent(" + cnt + "): " + event.toString());
                }

                // Generate predicted event with random values
                if (eventTypeName.matches(EventProperties.PREDICTEDEVENT_CLASS_NAME)) {
                    T event = (T) eventGenerator.generatePredictedEvent(EventProperties.PREDICTEDEVENT_STORAGE_COLLECTION_NAME);

                    // Serialize message
                    TSerializer serializer = new TSerializer(new TBinaryProtocol.Factory());
                    byte[] bytes = serializer.serialize((PredictedEvent) event);

                    // Publish message
                    ProducerRecord<String, byte[]> message = new ProducerRecord<String, byte[]>(this.topic, bytes);
                    producer.send(message);

//                    if (cnt % 1000 == 0)
//                        System.out.println("PredictedEvent(" + cnt + "): " + event.toString());
                }

                // Generate anomaly event with random values
                if (eventTypeName.matches(EventProperties.ANOMALYEVENT_CLASS_NAME)) {
                    T event = (T) eventGenerator.generateAnomalyEvent(EventProperties.ANOMALYEVENT_STORAGE_COLLECTION_NAME);

                    // Serialize message
                    TSerializer serializer = new TSerializer(new TBinaryProtocol.Factory());
                    byte[] bytes = serializer.serialize((AnomalyEvent) event);

                    // Publish message
                    ProducerRecord<String, byte[]> message = new ProducerRecord<String, byte[]>(this.topic, bytes);
                    producer.send(message);

//                    if (cnt % 1000 == 0)
//                        System.out.println("AnomalyEvent(" + cnt + "): " + event.toString());
                }

                // Generate recommendation event with random values
                if (eventTypeName.matches(EventProperties.RECOMMENDATIONEVENT_CLASS_NAME)) {
                    T event = (T) eventGenerator.generateRecommendationEvent(EventProperties.RECOMMENDATIONEVENT_STORAGE_COLLECTION_NAME);

                    // Serialize message
                    TSerializer serializer = new TSerializer(new TBinaryProtocol.Factory());
                    byte[] bytes = serializer.serialize((RecommendationEvent) event);

                    // Publish message
                    ProducerRecord<String, byte[]> message = new ProducerRecord<String, byte[]>(this.topic, bytes);
                    producer.send(message);

//                    if (cnt % 1000 == 0)
//                        System.out.println("RecommendationEvent(" + cnt + "): " + event.toString());
                }

                // Generate feedback event with random values
                if (eventTypeName.matches(EventProperties.FEEDBACKEVENT_CLASS_NAME)) {
                    T event = (T)eventGenerator.generateFeedbackEvent(EventProperties.FEEDBACKEVENT_STORAGE_COLLECTION_NAME);

                    // Serialize message
                    TSerializer serializer = new TSerializer(new TBinaryProtocol.Factory());
                    byte[] bytes = serializer.serialize((RecommendationStatus)event);

                    // Publish message
                    ProducerRecord<String, byte[]> message = new ProducerRecord<String, byte[]>(this.topic, bytes);
                    producer.send(message);

//                    if (cnt % 1000 == 0)
//                        System.out.println("RecommendationEvent(" + cnt + "): " + event.toString());
                }

            }
        } catch (InterruptedException e) {
            System.out.println(e.getClass().getName() + ": " + e.getMessage());
        } catch (TException e) {
            System.out.println(e.getClass().getName() + ": " + e.getMessage());
        } finally {
            producer.close();
        }
    }


    private static KafkaProducer<String, byte[]> createProducer(String a_zookeeper) {
        // Specify producer properties
        Properties props = new Properties();
//        props.put("bootstrap.servers", "89.216.116.44:9092");
        props.put("bootstrap.servers", "192.168.11.20:9092");
        props.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        props.put("value.serializer", "org.apache.kafka.common.serialization.ByteArraySerializer");

        // Define the producer object
        KafkaProducer<String, byte[]> producer = new KafkaProducer<String, byte[]>(props);

        return producer;
    }

}
