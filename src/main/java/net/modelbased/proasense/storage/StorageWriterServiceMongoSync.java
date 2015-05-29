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
package net.modelbased.proasense.storage;

import eu.proasense.internal.AnomalyEvent;
import eu.proasense.internal.DerivedEvent;
import eu.proasense.internal.PredictedEvent;
import eu.proasense.internal.RecommendationEvent;
import eu.proasense.internal.RecommendationStatus;
import eu.proasense.internal.SimpleEvent;

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


    public static void main(String[] args) {
        // Kafka properties
        String zooKeeper = "89.216.116.44:2181";
//        String zooKeeper = "192.168.11.20:2181";
        String groupId = "StorageWriterServiceMongoSync";
        String topic = "proasense.simpleevent.mhwirth.*";

        // Mongo properties
        String mongoURL = "mongodb://127.0.0.1:27017";
//        String mongoURL = "mongodb://89.216.116.44:27017";
//        String mongoURL = "mongodb://192.168.11.25:27017";

        // SensApp properties
        String sensappURL = "http://127.0.0.1:8090";
        String sensorName = "MHWirth.DDM.Hookload";

        // Storage writer properties
        int NO_SIMPLEEVENT_LISTENERS = 1;
        int NO_DERIVEDEVENT_LISTENERS = 1;
        int NO_PREDICTEDEVENT_LISTENERS = 1;
        int NO_ANOMALYEVENT_LISTENERS = 1;
        int NO_RECOMMENDATIONEVENT_LISTENERS = 1;
        int NO_RECOMMENDATIONSTATUS_LISTENERS = 0;

        int NO_MONGOSTORAGE_WRITERS = 1;
        int NO_MONGOSTORAGE_BULKSIZE = 1000;
        int NO_MONGOSTORAGE_MAXWAIT = 100;
        int NO_MONGOSTORAGE_HEARTBEAT = NO_MONGOSTORAGE_MAXWAIT*2;

        int NO_BLOCKINGQUEUE_SIZE = 1000000;

        int NO_TOTAL_THREADS = NO_SIMPLEEVENT_LISTENERS + NO_DERIVEDEVENT_LISTENERS
                + NO_PREDICTEDEVENT_LISTENERS + NO_ANOMALYEVENT_LISTENERS
                + NO_RECOMMENDATIONEVENT_LISTENERS + NO_RECOMMENDATIONSTATUS_LISTENERS
                + NO_MONGOSTORAGE_WRITERS + 1;

        // Define blocking queue
        BlockingQueue<EventDocument> queue = new ArrayBlockingQueue<EventDocument>(NO_BLOCKINGQUEUE_SIZE);

        // Create executor environment for threads
        ArrayList<Runnable> workers = new ArrayList<Runnable>(NO_TOTAL_THREADS);
        ExecutorService executor = Executors.newFixedThreadPool(NO_TOTAL_THREADS);

        // Create thread for Kafka event listeners
        workers.add(new EventListenerKafkaFilter<SimpleEvent>(SimpleEvent.class, queue, zooKeeper, groupId, "sintef.eu.proasense.internal.sensing.mhwirth.simple.*"));
        workers.add(new EventListenerKafkaFilter<DerivedEvent>(DerivedEvent.class, queue, zooKeeper, groupId, "eu.proasense.internal.(enricher.mhwirth.derived|sp.internal.incoming)"));
//        workers.add(new EventListenerKafkaTopic<DerivedEvent>(DerivedEvent.class, queue, zooKeeper, groupId, "eu.proasense.internal.enricher.mhwirth.derived"));
//        workers.add(new EventListenerKafkaTopic<DerivedEvent>(DerivedEvent.class, queue, zooKeeper, groupId, "eu.proasense.internal.sp.mhwirth.derived"));
        // Temporarily topic for StreamPipes component used during the Nis workshop
//        workers.add(new EventListenerKafkaTopic<DerivedEvent>(DerivedEvent.class, queue, zooKeeper, groupId, "eu.proasense.internal.sp.internal.incoming"));
        workers.add(new EventListenerKafkaTopic<PredictedEvent>(PredictedEvent.class, queue, zooKeeper, groupId, "eu.proasense.internal.oa.mhwirth.predicted"));
        workers.add(new EventListenerKafkaTopic<AnomalyEvent>(AnomalyEvent.class, queue, zooKeeper, groupId, "eu.proasense.internal.oa.mhwirth.anomaly"));
        workers.add(new EventListenerKafkaTopic<RecommendationEvent>(RecommendationEvent.class, queue, zooKeeper, groupId, "eu.proasense.internal.pandda.mhwirth.recommendation"));
//        workers.add(new EventListenerKafkaTopic<RecommendationStatus>(RecommendationStatus.class, queue, zooKeeper, groupId, "eu.proasense.internal.bia.mhwirth.feedback"));

        // Create threads for Mongo storage event writers
        for (int i = 0; i < NO_MONGOSTORAGE_WRITERS; i++) {
            workers.add(new EventWriterMongoSync(queue, mongoURL, NO_MONGOSTORAGE_BULKSIZE, NO_MONGOSTORAGE_MAXWAIT));
        }

        // Create thread for Mongo storage heartbeat
        workers.add(new EventHeartbeat(queue, NO_MONGOSTORAGE_HEARTBEAT));

        // Execute all threads
        for (int i = 0; i < NO_TOTAL_THREADS; i++) {
            executor.execute(workers.get(i));
        }

        // Shut down executor
        executor.shutdown();
    }

}
