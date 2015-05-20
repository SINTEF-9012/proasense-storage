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
import java.util.Properties;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public class StorageWriterServiceMongoAsync {
    private Properties kafkaProperties;

    public StorageWriterServiceMongoAsync() {
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
        ExecutorService executor = Executors.newFixedThreadPool(3);

        // Create thread for Kafka event listener
        Runnable kafkaWorker0 = new SimpleEventListenerKafkaFilter(queue, zooKeeper, groupId, "proasense.simpleevent.mhwirth.0");
        Runnable kafkaWorker1 = new SimpleEventListenerKafkaFilter(queue, zooKeeper, groupId, "proasense.simpleevent.mhwirth.1");
        executor.execute(kafkaWorker0);
        executor.execute(kafkaWorker1);

        // Create thread for Mongo event storage
        Runnable mongoWorker = new EventWriterMongoAsync(queue, mongoURL, 10000, 1000);
        executor.execute(mongoWorker);

        // Shut down executor
        executor.shutdown();
    }

}
