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
package net.modelbased.proasense.storage.writer;

import eu.proasense.internal.SimpleEvent;
import net.modelbased.proasense.storage.EventProperties;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.thrift.TException;
import org.apache.thrift.TSerializer;
import org.apache.thrift.protocol.TBinaryProtocol;

import java.util.Properties;


public class LoadTestingKafkaGenerator<T> implements Runnable {
    private Class<T> eventType;
    private String bootstrapServers;
    private String groupId;
    private String topic;
    private String collectionId;
    private int messages_per_second;
    private int max_messages;
    private EventGenerator eventGenerator;


    public LoadTestingKafkaGenerator(Class<T> eventType, String bootstrapServers, String groupId, String topic, String collectionId, int messages_per_second, int max_messages) {
        this.eventType = eventType;
        this.bootstrapServers = bootstrapServers;
        this.groupId = groupId;
        this.topic = topic;
        this.collectionId = collectionId;
        this.messages_per_second = messages_per_second;
        this.max_messages = max_messages;
        this.eventGenerator = new EventGenerator();
    }


    public void run() {
        KafkaProducer<String, byte[]> producer = createProducer(bootstrapServers);

        int cnt = 0;
        try {
            while (cnt < this.max_messages) {
                Thread.sleep(1000);

                String eventTypeName = eventType.getName();

                for (int i = 0; i < this.messages_per_second; i++) {
                    // Generate simple event with random values
                    if (eventTypeName.matches(EventProperties.SIMPLEEVENT_CLASS_NAME)) {
                        T event = (T) eventGenerator.generateSimpleEvent(this.collectionId);

                        // Serialize message
                        TSerializer serializer = new TSerializer(new TBinaryProtocol.Factory());
                        byte[] bytes = serializer.serialize((SimpleEvent) event);

                        // Publish message
                        ProducerRecord<String, byte[]> message = new ProducerRecord<String, byte[]>(this.topic, bytes);
                        producer.send(message);
                    }
                }
                cnt = cnt + this.messages_per_second;
            }
        } catch (InterruptedException e) {
            System.out.println(e.getClass().getName() + ": " + e.getMessage());
        } catch (TException e) {
            System.out.println(e.getClass().getName() + ": " + e.getMessage());
        } finally {
            producer.close();
        }
    }


    private static KafkaProducer<String, byte[]> createProducer(String bootstrapServers) {
        // Specify producer properties
        Properties props = new Properties();
        props.put("bootstrap.servers", bootstrapServers);
        props.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        props.put("value.serializer", "org.apache.kafka.common.serialization.ByteArraySerializer");

        // Define the producer object
        KafkaProducer<String, byte[]> producer = new KafkaProducer<String, byte[]>(props);

        return producer;
    }

}
