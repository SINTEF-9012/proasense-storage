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
import eu.proasense.internal.SimpleEvent;

import net.modelbased.proasense.storage.EventHeartbeat;
import net.modelbased.proasense.storage.EventDocument;
import net.modelbased.proasense.storage.EventListenerKafkaFilter;
import net.modelbased.proasense.storage.EventListenerKafkaTopic;
import net.modelbased.proasense.storage.EventWriterMongoSync;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public class StorageWriterServiceMongoKafkaBenchmark {

    public StorageWriterServiceMongoKafkaBenchmark() {

    }


    public static void main(String[] args) {
        // Kafka properties
//        String zooKeeper = "89.216.116.44:2181";
        String zooKeeper = "192.168.11.20:2181";
        String groupId = "StorageWriterServiceMongoKafkaBenchmark";

        // Mongo properties
        String mongoURL = "mongodb://127.0.0.1:27017";
//        String mongoURL = "mongodb://89.216.116.44:27017";
//        String mongoURL = "mongodb://192.168.11.25:27017";

        // Benchmark properties
        int NO_SIMPLEEVENT_LISTENERS = 1;
        int NO_SIMPLEEVENT_GENERATORS = 10;
        int NO_SIMPLEEVENT_RATE = 20;
        int NO_SIMPLEEVENT_MESSAGES = 10000;

        int NO_DERIVEDEVENT_LISTENERS = 1;
        int NO_DERIVEDEVENT_GENERATORS = 2;
        int NO_DERIVEDEVENT_RATE = 20;
        int NO_DERIVEDEVENT_MESSAGES = 10000;

        int NO_PREDICTEDEVENT_LISTENERS = 1;
        int NO_PREDICTEDEVENT_GENERATORS = 1;
        int NO_PREDICTEDEVENT_RATE = 1000;
        int NO_PREDICTEDEVENT_MESSAGES = 100;

        int NO_ANOMALYEVENT_LISTENERS = 1;
        int NO_ANOMALYEVENT_GENERATORS = 1;
        int NO_ANOMALYEVENT_RATE = 1000;
        int NO_ANOMALYEVENT_MESSAGES = 100;

        int NO_RECOMMENDATIONEVENT_LISTENERS = 1;
        int NO_RECOMMENDATIONEVENT_GENERATORS = 1;
        int NO_RECOMMENDATIONEVENT_RATE = 1000;
        int NO_RECOMMENDATIONEVENT_MESSAGES = 100;

        int NO_MONGOSTORAGE_WRITERS = 1;
        int NO_MONGOSTORAGE_BULKSIZE = 10000;
        int NO_MONGOSTORAGE_MAXWAIT = 1000;
        int NO_MONGOSTORAGE_HEARTBEAT = NO_MONGOSTORAGE_MAXWAIT*2;

        int NO_BLOCKINGQUEUE_SIZE = 1000000;

        int NO_TOTAL_THREADS = NO_SIMPLEEVENT_GENERATORS + NO_DERIVEDEVENT_GENERATORS + NO_PREDICTEDEVENT_GENERATORS + NO_ANOMALYEVENT_GENERATORS + NO_RECOMMENDATIONEVENT_GENERATORS
                + NO_MONGOSTORAGE_WRITERS + 1
                + NO_SIMPLEEVENT_LISTENERS + NO_DERIVEDEVENT_LISTENERS + NO_PREDICTEDEVENT_LISTENERS + NO_ANOMALYEVENT_LISTENERS + NO_RECOMMENDATIONEVENT_LISTENERS;

        // Define blocking queue
        BlockingQueue<EventDocument> queue = new ArrayBlockingQueue<EventDocument>(NO_BLOCKINGQUEUE_SIZE);

        // Create executor environment for threads
        ArrayList<Runnable> workers = new ArrayList<Runnable>(NO_TOTAL_THREADS);
        ExecutorService executor = Executors.newFixedThreadPool(NO_TOTAL_THREADS);

        // Create threads for random simple event generators
        Map<String, Integer> topicMap = new HashMap<String, Integer>();
        for (int i = 0; i < NO_SIMPLEEVENT_GENERATORS; i++) {
            workers.add(new RandomEventKafkaGenerator<SimpleEvent>(SimpleEvent.class, zooKeeper, groupId, "proasense.simpleevent.mhwirth." + i, "simpleevent.mhwirth." + i, NO_SIMPLEEVENT_RATE, NO_SIMPLEEVENT_MESSAGES));
            topicMap.put("proasense.simpleevent.mhwirth." + i, 1);
        }

        // Create thread for simple event listener (filter)
        String topicFilter = "proasense.simpleevent.mhwirth.[" + 0 + "-" + (NO_SIMPLEEVENT_GENERATORS - 1) + "]";
        workers.add(new EventListenerKafkaFilter<SimpleEvent>(SimpleEvent.class, queue, zooKeeper, groupId, topicFilter));

        // Create threads for simple event listeners (topic)
//        for (int i = 0; i < NO_SIMPLEEVENT_LISTENERS; i++) {
//            workers.add(new EventListenerKafkaTopic<SimpleEvent>(SimpleEvent.class, queue, zooKeeper, groupId, "proasense.simpleevent.mhwirth." + i));
//        }

        // Create threads for random derived event generators
        for (int i = 0; i < NO_DERIVEDEVENT_GENERATORS; i++) {
            workers.add(new RandomEventKafkaGenerator<DerivedEvent>(DerivedEvent.class, zooKeeper, groupId, "proasense.derivedevent.mhwirth." + i, "derivedevent.mhwirth." + i, NO_DERIVEDEVENT_RATE, NO_DERIVEDEVENT_MESSAGES));
        }

        // Create thread for derived event listener (filter)
        topicFilter = "proasense.derivedevent.mhwirth.[" + 0 + "-" + (NO_DERIVEDEVENT_GENERATORS - 1) + "]";
        workers.add(new EventListenerKafkaFilter<DerivedEvent>(DerivedEvent.class, queue, zooKeeper, groupId, topicFilter));

        // Create threads for derived event listeners
//        for (int i = 0; i < NO_DERIVEDEVENT_LISTENERS; i++) {
//            workers.add(new EventListenerKafkaTopic<DerivedEvent>(DerivedEvent.class, queue, zooKeeper, groupId, "proasense.derivedevent.mhwirth." + i))
//        }

        // Create threads for random predicted event generators
        for (int i = 0; i < NO_PREDICTEDEVENT_GENERATORS; i++) {
            workers.add(new RandomEventKafkaGenerator<PredictedEvent>(PredictedEvent.class, zooKeeper, groupId, "proasense.predictedevent.mhwirth." + i, "predictedevent.mhwirth." + i, NO_PREDICTEDEVENT_RATE, NO_PREDICTEDEVENT_MESSAGES));
        }

        // Create threads for predicted event listeners
        for (int i = 0; i < NO_PREDICTEDEVENT_LISTENERS; i++) {
            workers.add(new EventListenerKafkaTopic<PredictedEvent>(PredictedEvent.class, queue, zooKeeper, groupId, "proasense.predictedevent.mhwirth." + i));
        }

        // Create threads for random anomaly event generators
        for (int i = 0; i < NO_ANOMALYEVENT_GENERATORS; i++) {
            workers.add(new RandomEventKafkaGenerator<AnomalyEvent>(AnomalyEvent.class, zooKeeper, groupId, "proasense.anomalyevent.mhwirth." + i, "anomalyevent.mhwirth." + i, NO_ANOMALYEVENT_RATE, NO_ANOMALYEVENT_MESSAGES));
        }

        // Create threads for anomaly event listeners
        for (int i = 0; i < NO_ANOMALYEVENT_LISTENERS; i++) {
            workers.add(new EventListenerKafkaTopic<AnomalyEvent>(AnomalyEvent.class, queue, zooKeeper, groupId, "proasense.anomalyevent.mhwirth." + i));
        }

        // Create threads for random recommendation event generators
        for (int i = 0; i < NO_RECOMMENDATIONEVENT_GENERATORS; i++) {
            workers.add(new RandomEventKafkaGenerator<RecommendationEvent>(RecommendationEvent.class, zooKeeper, groupId, "proasense.recommendationevent.mhwirth." + i, "recommendationevent.mhwirth." + i, NO_RECOMMENDATIONEVENT_RATE, NO_RECOMMENDATIONEVENT_MESSAGES));
        }

        // Create threads for recommendation event listeners
        for (int i = 0; i < NO_ANOMALYEVENT_LISTENERS; i++) {
            workers.add(new EventListenerKafkaTopic<RecommendationEvent>(RecommendationEvent.class, queue, zooKeeper, groupId, "proasense.recommendationevent.mhwirth." + i));
        }

        // Create threads for Mongo storage event writers
        for (int i = 0; i < NO_MONGOSTORAGE_WRITERS; i++) {
//            workers.add(new EventWriterMongoAsync(queue, mongoURL, NO_MONGOSTORAGE_BULKSIZE, NO_MONGOSTORAGE_MAXWAIT));
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
