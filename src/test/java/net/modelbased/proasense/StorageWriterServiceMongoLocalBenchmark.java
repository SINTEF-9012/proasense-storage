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
package net.modelbased.proasense;

import eu.proasense.internal.AnomalyEvent;
import eu.proasense.internal.DerivedEvent;
import eu.proasense.internal.FeedbackEvent;
import eu.proasense.internal.PredictedEvent;
import eu.proasense.internal.RecommendationEvent;
import eu.proasense.internal.SimpleEvent;

import net.modelbased.proasense.storage.EventDocument;
import net.modelbased.proasense.storage.EventWriterMongoAsync;
import net.modelbased.proasense.storage.EventWriterMongoSync;
import net.modelbased.proasense.storage.EventHeartbeat;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Properties;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public class StorageWriterServiceMongoLocalBenchmark {
    private Properties clientProperties;


    public StorageWriterServiceMongoLocalBenchmark() {
    }


    private Properties loadClientProperties() {
        clientProperties = new Properties();
        String propFilename = "client.properties";
        InputStream inputStream = getClass().getClassLoader().getResourceAsStream(propFilename);

        try {
            if (inputStream != null) {
                clientProperties.load(inputStream);
            } else
                throw new FileNotFoundException("Property file: '" + propFilename + "' not found in classpath.");
        }
        catch (IOException e) {
            System.out.println("Exception:" + e.getMessage());
        }

        return clientProperties;
    }


    public static void main(String[] args) {
        // Get benchmark properties
        StorageWriterServiceMongoLocalBenchmark benchmark = new StorageWriterServiceMongoLocalBenchmark();
        benchmark.loadClientProperties();

        // Benchmark common properties
        boolean IS_BENCHMARK_LOGFILE = new Boolean(benchmark.clientProperties.getProperty("proasense.benchmark.common.logfile")).booleanValue();

        // Benchmark load testing properties
        boolean IS_LOAD_TESTING_ENABLED = new Boolean(benchmark.clientProperties.getProperty("proasense.benchmark.load.testing")).booleanValue();
        int NO_LOAD_TESTING_SENSORS = new Integer(benchmark.clientProperties.getProperty("proasense.benchmark.load.sensors")).intValue();
        int NO_LOAD_TESTING_RATE = new Integer(benchmark.clientProperties.getProperty("proasense.benchmark.load.rate")).intValue();
        int NO_LOAD_TESTING_MESSAGES = new Integer(benchmark.clientProperties.getProperty("proasense.benchmark.load.messages")).intValue();

        // Local event generators configuration properties
        int NO_SIMPLEEVENT_GENERATORS = new Integer(benchmark.clientProperties.getProperty("proasense.benchmark.local.simple.generators")).intValue();
        int NO_SIMPLEEVENT_RATE = new Integer(benchmark.clientProperties.getProperty("proasense.benchmark.local.simple.rate")).intValue();
        int NO_SIMPLEEVENT_MESSAGES = new Integer(benchmark.clientProperties.getProperty("proasense.benchmark.local.simple.messages")).intValue();

        int NO_DERIVEDEVENT_GENERATORS = new Integer(benchmark.clientProperties.getProperty("proasense.benchmark.local.derived.generators")).intValue();
        int NO_DERIVEDEVENT_RATE = new Integer(benchmark.clientProperties.getProperty("proasense.benchmark.local.derived.rate")).intValue();
        int NO_DERIVEDEVENT_MESSAGES = new Integer(benchmark.clientProperties.getProperty("proasense.benchmark.local.derived.messages")).intValue();

        int NO_PREDICTEDEVENT_GENERATORS = new Integer(benchmark.clientProperties.getProperty("proasense.benchmark.local.predicted.generators")).intValue();
        int NO_PREDICTEDEVENT_RATE = new Integer(benchmark.clientProperties.getProperty("proasense.benchmark.local.predicted.rate")).intValue();
        int NO_PREDICTEDEVENT_MESSAGES = new Integer(benchmark.clientProperties.getProperty("proasense.benchmark.local.predicted.messages")).intValue();

        int NO_ANOMALYEVENT_GENERATORS = new Integer(benchmark.clientProperties.getProperty("proasense.benchmark.local.anomaly.generators")).intValue();
        int NO_ANOMALYEVENT_RATE = new Integer(benchmark.clientProperties.getProperty("proasense.benchmark.local.anomaly.rate")).intValue();
        int NO_ANOMALYEVENT_MESSAGES = new Integer(benchmark.clientProperties.getProperty("proasense.benchmark.local.anomaly.messages")).intValue();

        int NO_RECOMMENDATIONEVENT_GENERATORS = new Integer(benchmark.clientProperties.getProperty("proasense.benchmark.local.recommendation.generators")).intValue();
        int NO_RECOMMENDATIONEVENT_RATE = new Integer(benchmark.clientProperties.getProperty("proasense.benchmark.local.recommendation.rate")).intValue();
        int NO_RECOMMENDATIONEVENT_MESSAGES = new Integer(benchmark.clientProperties.getProperty("proasense.benchmark.local.recommendation.messages")).intValue();

        int NO_FEEDBACKEVENT_GENERATORS = new Integer(benchmark.clientProperties.getProperty("proasense.benchmark.local.feedback.generators")).intValue();
        int NO_FEEDBACKEVENT_RATE = new Integer(benchmark.clientProperties.getProperty("proasense.benchmark.local.feedback.rate")).intValue();
        int NO_FEEDBACKEVENT_MESSAGES = new Integer(benchmark.clientProperties.getProperty("proasense.benchmark.local.feedback.messages")).intValue();

        // MongoDB event writer configuration properties
        String MONGODB_URL = benchmark.clientProperties.getProperty("proasense.storage.mongodb.url");

        boolean IS_MONGODB_SYNCDRIVER = new Boolean(benchmark.clientProperties.getProperty("proasense.storage.mongodb.syncdriver")).booleanValue();

        int NO_MONGODB_WRITERS = new Integer(benchmark.clientProperties.getProperty("proasense.storage.mongodb.writers")).intValue();
        int NO_MONGODB_BULKSIZE = new Integer(benchmark.clientProperties.getProperty("proasense.storage.mongodb.bulksize")).intValue();
        int NO_MONGODB_MAXWAIT = new Integer(benchmark.clientProperties.getProperty("proasense.storage.mongodb.maxwait")).intValue();
        int NO_MONGOSTORAGE_HEARTBEAT = NO_MONGODB_MAXWAIT*2;

        // Blocking queue for multi-threaded application
        int NO_BLOCKINGQUEUE_SIZE = 1000000;
        BlockingQueue<EventDocument> queue = new ArrayBlockingQueue<EventDocument>(NO_BLOCKINGQUEUE_SIZE);

        // Total number of threads
        int NO_TOTAL_THREADS = 0;

        if (IS_LOAD_TESTING_ENABLED)
            NO_TOTAL_THREADS = 1 + NO_MONGODB_WRITERS + 1;
        else
            NO_TOTAL_THREADS = NO_SIMPLEEVENT_GENERATORS + NO_DERIVEDEVENT_GENERATORS
                + NO_PREDICTEDEVENT_GENERATORS + NO_ANOMALYEVENT_GENERATORS + NO_RECOMMENDATIONEVENT_GENERATORS + NO_FEEDBACKEVENT_GENERATORS
                + NO_MONGODB_WRITERS + 1;

        // Create executor environment for threads
        ArrayList<Runnable> workers = new ArrayList<Runnable>(NO_TOTAL_THREADS);
        ExecutorService executor = Executors.newFixedThreadPool(NO_TOTAL_THREADS);

        if (IS_LOAD_TESTING_ENABLED) {
            int NO_MESSAGES_PER_SECOND = NO_LOAD_TESTING_SENSORS * (1000/NO_LOAD_TESTING_RATE);
            workers.add(new SimpleEventLocalGenerator<SimpleEvent>(SimpleEvent.class, queue, "load_testing", NO_MESSAGES_PER_SECOND, NO_LOAD_TESTING_MESSAGES));
        }
        else {
            // Create threads for random simple event generators
            for (int i = 0; i < NO_SIMPLEEVENT_GENERATORS; i++) {
                workers.add(new RandomEventLocalGenerator<SimpleEvent>(SimpleEvent.class, queue, "mhwirth." + i, NO_SIMPLEEVENT_RATE, NO_SIMPLEEVENT_MESSAGES));
            }

            // Create threads for random derived event generators
            for (int i = 0; i < NO_DERIVEDEVENT_GENERATORS; i++) {
                workers.add(new RandomEventLocalGenerator<DerivedEvent>(DerivedEvent.class, queue, "mhwirth." + i, NO_DERIVEDEVENT_RATE, NO_DERIVEDEVENT_MESSAGES));
            }

            // Create threads for random predicted event generators
            for (int i = 0; i < NO_PREDICTEDEVENT_GENERATORS; i++) {
                workers.add(new RandomEventLocalGenerator<PredictedEvent>(PredictedEvent.class, queue, "mhwirth." + i, NO_PREDICTEDEVENT_RATE, NO_PREDICTEDEVENT_MESSAGES));
            }

            // Create threads for random anomaly event generators
            for (int i = 0; i < NO_ANOMALYEVENT_GENERATORS; i++) {
                workers.add(new RandomEventLocalGenerator<AnomalyEvent>(AnomalyEvent.class, queue, "mhwirth." + i, NO_ANOMALYEVENT_RATE, NO_ANOMALYEVENT_MESSAGES));
            }

            // Create threads for random recommendation event generators
            for (int i = 0; i < NO_RECOMMENDATIONEVENT_GENERATORS; i++) {
                workers.add(new RandomEventLocalGenerator<RecommendationEvent>(RecommendationEvent.class, queue, "mhwirth." + i, NO_RECOMMENDATIONEVENT_RATE, NO_RECOMMENDATIONEVENT_MESSAGES));
            }

            // Create threads for random feedback event generators
            for (int i = 0; i < NO_FEEDBACKEVENT_GENERATORS; i++) {
                workers.add(new RandomEventLocalGenerator<FeedbackEvent>(FeedbackEvent.class, queue, "mhwirth." + i, NO_FEEDBACKEVENT_RATE, NO_FEEDBACKEVENT_MESSAGES));
            }
        }

        // Create threads for Mongo storage event writers
        for (int i = 0; i < NO_MONGODB_WRITERS; i++) {
            if (IS_MONGODB_SYNCDRIVER)
                workers.add(new EventWriterMongoSync(queue, MONGODB_URL, NO_MONGODB_BULKSIZE, NO_MONGODB_MAXWAIT, IS_BENCHMARK_LOGFILE, i));
            else
                workers.add(new EventWriterMongoAsync(queue, MONGODB_URL, NO_MONGODB_BULKSIZE, NO_MONGODB_MAXWAIT, IS_BENCHMARK_LOGFILE, i));
        }

        // Create thread for MongoDB heartbeat
        workers.add(new EventHeartbeat(queue, NO_MONGOSTORAGE_HEARTBEAT));

        // Execute all threads
        for (int i = 0; i < NO_TOTAL_THREADS; i++) {
            executor.execute(workers.get(i));
        }

        // Shut down executor
        executor.shutdown();
    }

}
