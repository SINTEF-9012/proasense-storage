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
import kafka.consumer.Consumer;
import kafka.consumer.ConsumerConfig;
import kafka.consumer.ConsumerIterator;
import kafka.consumer.ConsumerTimeoutException;
import kafka.consumer.KafkaStream;
import kafka.consumer.Whitelist;
import kafka.javaapi.consumer.ConsumerConnector;
import net.modelbased.proasense.storage.EventDocument;
import net.modelbased.proasense.storage.EventDocumentConverter;
import org.apache.thrift.TDeserializer;
import org.apache.thrift.TException;
import org.apache.thrift.protocol.TBinaryProtocol;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.BlockingQueue;


public class SimpleEventListenerKafkaFilter implements Runnable {
    private Properties kafkaProperties;
    private BlockingQueue<EventDocument> queue;
    private String zooKeeper;
    private String groupId;
    private String topicFilter;

    public SimpleEventListenerKafkaFilter(BlockingQueue<EventDocument> queue, String zooKeeper, String groupId, String topicFilter) {
        this.queue = queue;
        this.zooKeeper = zooKeeper;
        this.groupId = groupId;
        this.topicFilter = topicFilter;
    }

    public void run() {
        ConsumerConnector kafkaConsumer = createKafkaConsumer(zooKeeper, groupId);

        // Consume message
        List<KafkaStream<byte[], byte[]>> streams = kafkaConsumer.createMessageStreamsByFilter(new Whitelist(this.topicFilter));
        KafkaStream<byte[], byte[]> messageAndMetadatas = streams.get(0);
        ConsumerIterator<byte[], byte[]> it = messageAndMetadatas.iterator();

        int cnt = 0;
        try {
            while (it.hasNext()) {
                cnt++;
                byte[] bytes = it.next().message();

                // Convert message to Apache Thrift struct
                TDeserializer deserializer = new TDeserializer(new TBinaryProtocol.Factory());
                SimpleEvent event = new SimpleEvent();
                deserializer.deserialize(event, bytes);

                if (cnt % 1000 == 0)
                    System.out.println("SimpleEvent(" + cnt + "): " + event.toString());

                // Convert SimpleEvent to Document
                EventDocumentConverter converter = new EventDocumentConverter(event);
                EventDocument eventDocument = new EventDocument(converter.getCollectionId(), converter.getDocument());

                queue.put(eventDocument);
            }
        } catch (ConsumerTimeoutException e) {
            System.out.println(e.getClass().getName() + ": " + e.getMessage());
        } catch (TException e) {
            System.out.println(e.getClass().getName() + ": " + e.getMessage());
        } catch (InterruptedException e) {
            System.out.println(e.getClass().getName() + ": " + e.getMessage());
        } finally {
            kafkaConsumer.commitOffsets();
            kafkaConsumer.shutdown();
        }
    }


    private static ConsumerConnector createKafkaConsumer(String a_zookeeper, String a_groupId) {
        // Specify consumer properties
        Properties props = new Properties();
        props.put("zookeeper.connect", a_zookeeper);
        props.put("group.id", a_groupId);
        props.put("zookeeper.connection.timeout.ms", "1000000");
        props.put("zookeeper.session.timeout.ms", "400");
        props.put("zookeeper.sync.time.ms", "200");
        props.put("auto.commit.interval.ms", "1000");

        // Create the connection to the cluster
        ConsumerConfig config = new ConsumerConfig(props);
        ConsumerConnector consumer = Consumer.createJavaConsumerConnector(config);

        return consumer;
    }


    private Properties getDefaultProperties() {
        kafkaProperties = new Properties();
        String propFilename = "/resources/kafka.properties";
        InputStream inputStream = getClass().getClassLoader().getResourceAsStream(propFilename);

        try {
            if (inputStream != null) {
                kafkaProperties.load(inputStream);
            } else
                throw new FileNotFoundException("Property file: '" + propFilename + "' not found in classpath.");
        }
        catch (IOException e) {
            System.out.println("Exception:" + e.getMessage());
        }

        return kafkaProperties;
    }

}
