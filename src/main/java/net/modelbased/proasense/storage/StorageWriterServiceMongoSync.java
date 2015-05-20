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
package net.modelbased.proasense.storage;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public class StorageWriterServiceMongoSync {
    private Properties kafkaProperties;

    public StorageWriterServiceMongoSync() {
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


    private Map<String, Integer> createTopicCountMap() {
        Map<String, Integer> topicCountMap = new HashMap<String, Integer>();

        topicCountMap.put("proasense.simpleevent.mhwirth.1000693", 1);
        topicCountMap.put("proasense.simpleevent.mhwirth.1000700", 1);
        topicCountMap.put("proasense.simpleevent.mhwirth.1002311", 1);
        topicCountMap.put("proasense.simpleevent.mhwirth.1000695", 1);
        topicCountMap.put("proasense.simpleevent.mhwirth.1000692", 1);
        topicCountMap.put("proasense.simpleevent.mhwirth.1000696", 1);
        topicCountMap.put("proasense.simpleevent.mhwirth.1002123", 1);
        topicCountMap.put("proasense.simpleevent.mhwirth.1033619", 1);

        topicCountMap.put("proasense.simpleevent.mhwirth.1002113", 1);
        topicCountMap.put("proasense.simpleevent.mhwirth.1002115", 1);
        topicCountMap.put("proasense.simpleevent.mhwirth.1002114", 1);
        topicCountMap.put("proasense.simpleevent.mhwirth.1002116", 1);
//        topicCountMap.put("proasense.simpleevent.mhwirth.1002311", 1);
        topicCountMap.put("proasense.simpleevent.mhwirth.1002127", 1);
        topicCountMap.put("proasense.simpleevent.mhwirth.1002128", 1);

        return topicCountMap;
    }


    public static void main(String[] args) {
        // Kafka properties
//        String zooKeeper = "89.216.116.44:2181";
        String zooKeeper = "192.168.11.20:2181";
        String groupId = "StorageWriterServiceMongoSync";
        String topic = "proasense.simpleevent.mhwirth.*";

        // Mongo properties
//        String mongoURL = "mongodb://127.0.0.1:27017";
//        String mongoULR = "mongodb://89.216.116.44:27017";
        String mongoURL = "mongodb://192.168.11.25:27017";

        //SensApp properties
        String sensappURL = "http://127.0.0.1:8090";
        String sensorName = "MHWirth.DDM.Hookload";

        // Define blocking queue
        BlockingQueue<EventDocument> queue = new ArrayBlockingQueue<EventDocument>(1000000);

        // Create executor environment for threads
        ArrayList<Runnable> workers = new ArrayList<Runnable>(2);
        ExecutorService executor = Executors.newFixedThreadPool(3);

        // Create thread for Kafka event listener
        for (int i = 0; i < 2; i++) {
//            workers.add(new EventListenerKafkaTopic<SimpleEvent>(SimpleEvent.class, queue, zooKeeper, groupId, "proasense.simpleevent.mhwirth." + i));
            workers.add(new SimpleEventListenerKafkaTopic(queue, zooKeeper, groupId, "proasense.simpleevent.mhwirth." + i));
            executor.execute(workers.get(i));
        }
//        Runnable kafkaWorker = new EventListenerKafkaFilter<SimpleEvent>(SimpleEvent.class, queue, zooKeeper, groupId, topic);
//        executor.execute(kafkaWorker);

        // Create thread for Mongo event storage
        Runnable mongoWorker = new EventWriterMongoSync(queue, mongoURL, 10000, 1000);
        executor.execute(mongoWorker);

        // Shut down executor
        executor.shutdown();
    }

}
