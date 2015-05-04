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
package net.modelbased.sensapp.proasense;

import eu.proasense.internal.AnomalyEvent;
import eu.proasense.internal.DerivedEvent;
import eu.proasense.internal.PredictedEvent;
import eu.proasense.internal.RecommendationEvent;
import eu.proasense.internal.SimpleEvent;
import net.modelbased.sensapp.proasense.storage.EventDocument;
import net.modelbased.sensapp.proasense.storage.EventWriterMongoAsync;
import net.modelbased.sensapp.proasense.storage.EventWriterMongoSync;
import net.modelbased.sensapp.proasense.storage.EventHeartbeat;

import java.util.ArrayList;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public class StorageWriterServiceMongoLocalBenchmark {

    public StorageWriterServiceMongoLocalBenchmark() {

    }


    public static void main(String[] args) {
        // Kafka properties
//        String zooKeeper = "89.216.116.44:2181";
        String zooKeeper = "192.168.11.20:2181";
        String groupId = "SimpleEventLocalMongoBenchmark";

        // Mongo properties
//        String mongoURL = "mongodb://127.0.0.1:27017";
//        String mongoULR = "mongodb://89.216.116.44:27017";
        String mongoURL = "mongodb://192.168.11.25:27017";

        int NO_SIMPLEEVENTS_THREADS = 10;
        int NO_SIMPLEEVENTS_RATE = 20;
        int NO_SIMPLEEVENTS_MESSAGES = 1000;

        int NO_DERIVEDEVENTS_THREADS = 1;
        int NO_DERIVEDEVENTS_RATE = 100;
        int NO_DERIVEDEVENTS_MESSAGES = 1000;

        int NO_PREDICTEDEVENTS_THREADS = 1;
        int NO_PREDICTEDEVENTS_RATE = 1000;
        int NO_PREDICTEDEVENTS_MESSAGES = 100;

        int NO_ANOMALYEVENTS_THREADS = 1;
        int NO_ANOMALYEVENTS_RATE = 1000;
        int NO_ANOMALYEVENTS_MESSAGES = 100;

        int NO_RECOMMENDATIONEVENTS_THREADS = 1;
        int NO_RECOMMENDATIONEVENTS_RATE = 1000;
        int NO_RECOMMENDATIONEVENTS_MESSAGES = 100;

        int NO_MONGOSTORAGE_THREADS = 1;
        int NO_MONGOSTORAGE_BULKSIZE = 1000;
        int NO_MONGOSTORAGE_MAXWAIT = 1000;
        int NO_MONGOSTORAGE_HEARTBEAT = NO_MONGOSTORAGE_MAXWAIT*2;

        int NO_TOTAL_THREADS = NO_SIMPLEEVENTS_THREADS + NO_DERIVEDEVENTS_THREADS + NO_PREDICTEDEVENTS_THREADS + NO_ANOMALYEVENTS_THREADS + NO_RECOMMENDATIONEVENTS_THREADS + NO_MONGOSTORAGE_THREADS;

        // Define blocking queue
        BlockingQueue<EventDocument> queue = new ArrayBlockingQueue<EventDocument>(1000000);

        // Create executor environment for threads
        ArrayList<Runnable> workers = new ArrayList<Runnable>(NO_TOTAL_THREADS);
        ExecutorService executor = Executors.newFixedThreadPool(NO_TOTAL_THREADS);

        // Create threads for random simple event generators
        for (int i = 0; i < NO_SIMPLEEVENTS_THREADS; i++) {
            workers.add(new RandomEventLocalGenerator<SimpleEvent>(SimpleEvent.class, queue, zooKeeper, groupId, "proasense.simpleevent.mhwirth." + i, "proasense.simpleevent.mhwirth." + i, NO_SIMPLEEVENTS_RATE, NO_SIMPLEEVENTS_MESSAGES));
        }

        // Create threads for random derived event generators
        for (int i = 0; i < NO_DERIVEDEVENTS_THREADS; i++) {
            workers.add(new RandomEventLocalGenerator<DerivedEvent>(DerivedEvent.class, queue, zooKeeper, groupId, "proasense.derivedevent.mhwirth." + i, "proasense.derivedevent.mhwirth." + i, NO_DERIVEDEVENTS_RATE, NO_DERIVEDEVENTS_MESSAGES));
        }

        // Create threads for random predicted event generators
        for (int i = 0; i < NO_PREDICTEDEVENTS_THREADS; i++) {
            workers.add(new RandomEventLocalGenerator<PredictedEvent>(PredictedEvent.class, queue, zooKeeper, groupId, "proasense.predictedevent.mhwirth." + i, "proasense.predictedevent.mhwirth." + i, NO_PREDICTEDEVENTS_RATE, NO_PREDICTEDEVENTS_MESSAGES));
        }

        // Create threads for random anomaly event generators
        for (int i = 0; i < NO_ANOMALYEVENTS_THREADS; i++) {
            workers.add(new RandomEventLocalGenerator<AnomalyEvent>(AnomalyEvent.class, queue, zooKeeper, groupId, "proasense.anomalyevent.mhwirth." + i, "proasense.anomalyevent.mhwirth." + i, NO_ANOMALYEVENTS_RATE, NO_ANOMALYEVENTS_MESSAGES));
        }

        // Create threads for random recommendation event generators
        for (int i = 0; i < NO_RECOMMENDATIONEVENTS_THREADS; i++) {
            workers.add(new RandomEventLocalGenerator<RecommendationEvent>(RecommendationEvent.class, queue, zooKeeper, groupId, "proasense.recommendationevent.mhwirth." + i, "proasense.recommendationevent.mhwirth." + i, NO_RECOMMENDATIONEVENTS_RATE, NO_RECOMMENDATIONEVENTS_MESSAGES));
        }

        // Create threads for Mongo storage event writers
        for (int i = 0; i < NO_MONGOSTORAGE_THREADS; i++) {
//            workers.add(new EventWriterMongoAsync(queue, mongoURL, NO_MONGOSTORAGE_BULKSIZE, NO_MONGOSTORAGE_MAXWAIT));
            workers.add(new EventWriterMongoSync(queue, mongoURL, NO_MONGOSTORAGE_BULKSIZE, NO_MONGOSTORAGE_MAXWAIT));
        }

        // Create thread for Mongo storage heartbeat
        workers.add(new EventHeartbeat(queue, NO_MONGOSTORAGE_HEARTBEAT));

        // Execute all threads
        for (int i = 0; i < NO_TOTAL_THREADS + 1; i++) {
            executor.execute(workers.get(i));
        }

        // Shut down executor
        executor.shutdown();
    }

}
