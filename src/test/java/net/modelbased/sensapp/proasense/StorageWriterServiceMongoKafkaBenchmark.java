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

import eu.proasense.internal.SimpleEvent;
import net.modelbased.sensapp.proasense.storage.EventDocument;
import net.modelbased.sensapp.proasense.storage.EventListenerKafkaTopic;
import net.modelbased.sensapp.proasense.storage.EventWriterMongoSync;

import java.util.ArrayList;
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
        String groupId = "SimpleEventLocalMongoBenchmark";

        // Mongo properties
//        String mongoURL = "mongodb://127.0.0.1:27017";
//        String mongoULR = "mongodb://89.216.116.44:27017";
        String mongoURL = "mongodb://192.168.11.25:27017";

        // Benchmark properties
        int NO_SIMPLEEVENTS_THREADS = 10;
        int NO_SIMPLEEVENTS_RATE = 20;
        int NO_SIMPLEEVENTS_MESSAGES = 1000;

        int NO_DERIVEDEVENTS_THREADS = 0;
        int NO_DERIVEDEVENTS_RATE = 100;
        int NO_DERIVEDEVENTS_MESSAGES = 1000;

        int NO_PREDICTEDEVENTS_THREADS = 0;
        int NO_PREDICTEDEVENTS_RATE = 1000;
        int NO_PREDICTEDEVENTS_MESSAGES = 100;

        int NO_ANOMALYEVENTS_THREADS = 0;
        int NO_ANOMALYEVENTS_RATE = 1000;
        int NO_ANOMALYEVENTS_MESSAGES = 100;

        int NO_RECOMMENDATIONEVENTS_THREADS = 0;
        int NO_RECOMMENDATIONEVENTS_RATE = 1000;
        int NO_RECOMMENDATIONEVENTS_MESSAGES = 100;

        int NO_MONGOSTORAGE_THREADS = 1;
        int NO_MONGOSTORAGE_BULKSIZE = 10000;
        int NO_MONGOSTORAGE_MAXWAIT = 1000;

        int NO_TOTAL_THREADS = NO_SIMPLEEVENTS_THREADS*2 + NO_DERIVEDEVENTS_THREADS + NO_PREDICTEDEVENTS_THREADS + NO_ANOMALYEVENTS_THREADS + NO_RECOMMENDATIONEVENTS_THREADS + NO_MONGOSTORAGE_THREADS;

        // Define blocking queue
        BlockingQueue<EventDocument> queue = new ArrayBlockingQueue<EventDocument>(1000000);

        // Create executor environment for threads
        ArrayList<Runnable> workers = new ArrayList<Runnable>(NO_TOTAL_THREADS);
        ExecutorService executor = Executors.newFixedThreadPool(NO_TOTAL_THREADS);

        // Create threads for random simple event generators
        for (int i = 0; i < NO_SIMPLEEVENTS_THREADS; i++) {
            workers.add(new RandomEventKafkaGenerator<SimpleEvent>(SimpleEvent.class, zooKeeper, groupId, "proasense.simpleevent.mhwirth." + i, "proasense.simpleevent.mhwirth." + i, NO_SIMPLEEVENTS_RATE, NO_SIMPLEEVENTS_MESSAGES));
            workers.add(new EventListenerKafkaTopic<SimpleEvent>(SimpleEvent.class, queue, zooKeeper, groupId, "proasense.simpleevent.mhwirth." + i));
        }

        // Create threads for random simple event generators
//        for (int i = 0; i < NO_SIMPLEEVENTS_THREADS; i++) {
//            workers.add(new SimpleEventKafkaGenerator(zooKeeper, groupId, "proasense.simpleevent.mhwirth." + i, "proasense.simpleevent.mhwirth." + i, NO_SIMPLEEVENTS_RATE));
//        }

        // Create threads for Mongo storage event writers
        for (int i = 0; i < NO_MONGOSTORAGE_THREADS; i++) {
            workers.add(new EventWriterMongoSync(queue, mongoURL, NO_MONGOSTORAGE_BULKSIZE, NO_MONGOSTORAGE_MAXWAIT));
        }

        // Execute all threads
        for (int i = 0; i < NO_TOTAL_THREADS; i++) {
            executor.execute(workers.get(i));
        }

        // Shut down executor
        executor.shutdown();
    }

}
