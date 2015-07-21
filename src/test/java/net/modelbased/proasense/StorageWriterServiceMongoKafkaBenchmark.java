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

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public class StorageWriterServiceMongoKafkaBenchmark {
    private Properties clientProperties;


    public StorageWriterServiceMongoKafkaBenchmark() {
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
        StorageWriterServiceMongoKafkaBenchmark benchmark = new StorageWriterServiceMongoKafkaBenchmark();
        benchmark.loadClientProperties();

        // Benchmark load testing properties
        boolean IS_LOAD_TESTING_ENABLED = new Boolean(benchmark.clientProperties.getProperty("proasense.benchmark.load.testing")).booleanValue();
        int NO_LOAD_TESTING_SENSORS = new Integer(benchmark.clientProperties.getProperty("proasense.benchmark.load.sensors")).intValue();
        int NO_LOAD_TESTING_RATE = new Integer(benchmark.clientProperties.getProperty("proasense.benchmark.load.rate")).intValue();
        int NO_LOAD_TESTING_MESSAGES = new Integer(benchmark.clientProperties.getProperty("proasense.benchmark.load.messages")).intValue();

        // Kafka broker configuration properties
        String boostrapServers = benchmark.clientProperties.getProperty("kafka.bootstrap.servers");
        String groupId = "StorageWriterServiceMongoKafkaBenchmark";

        // Kafka event generators configuration properties
        String SIMPLEEVENT_TOPIC = benchmark.clientProperties.getProperty("proasense.benchmark.kafka.simple.topic");
        String DERIVEDEVENT_TOPIC = benchmark.clientProperties.getProperty("proasense.benchmark.kafka.derived.topic");
        String PREDICTEDEVENT_TOPIC = benchmark.clientProperties.getProperty("proasense.benchmark.kafka.predicted.topic");
        String ANOMALYEVENT_TOPIC = benchmark.clientProperties.getProperty("proasense.benchmark.kafka.anomaly.topic");
        String RECOMMENDATIONEVENT_TOPIC = benchmark.clientProperties.getProperty("proasense.benchmark.kafka.recommendation.topic");
        String FEEDBACKEVENT_TOPIC = benchmark.clientProperties.getProperty("proasense.benchmark.kafka.feedback.topic");

        boolean IS_SIMPLEEVENT_FILTER = new Boolean(benchmark.clientProperties.getProperty("proasense.benchmark.kafka.simple.filter")).booleanValue();
        boolean IS_DERIVEDEVENT_FILTER = new Boolean(benchmark.clientProperties.getProperty("proasense.benchmark.kafka.derived.filter")).booleanValue();
        boolean IS_PREDICTEDEVENT_FILTER = new Boolean(benchmark.clientProperties.getProperty("proasense.benchmark.kafka.predicted.filter")).booleanValue();
        boolean IS_ANOMALYEVENT_FILTER = new Boolean(benchmark.clientProperties.getProperty("proasense.benchmark.kafka.anomaly.filter")).booleanValue();
        boolean IS_RECOMMENDATIONEVENT_FILTER = new Boolean(benchmark.clientProperties.getProperty("proasense.benchmark.kafka.recommendation.filter")).booleanValue();
        boolean IS_FEEDBACKEVENT_FILTER = new Boolean(benchmark.clientProperties.getProperty("proasense.benchmark.kafka.feedback.filter")).booleanValue();

        int NO_SIMPLEEVENT_GENERATORS = new Integer(benchmark.clientProperties.getProperty("proasense.benchmark.kafka.simple.generators")).intValue();
        int NO_SIMPLEEVENT_RATE = new Integer(benchmark.clientProperties.getProperty("proasense.benchmark.kafka.simple.rate")).intValue();
        int NO_SIMPLEEVENT_MESSAGES = new Integer(benchmark.clientProperties.getProperty("proasense.benchmark.kafka.simple.messages")).intValue();

        int NO_DERIVEDEVENT_GENERATORS = new Integer(benchmark.clientProperties.getProperty("proasense.benchmark.kafka.derived.generators")).intValue();
        int NO_DERIVEDEVENT_RATE = new Integer(benchmark.clientProperties.getProperty("proasense.benchmark.kafka.derived.rate")).intValue();
        int NO_DERIVEDEVENT_MESSAGES = new Integer(benchmark.clientProperties.getProperty("proasense.benchmark.kafka.derived.messages")).intValue();

        int NO_PREDICTEDEVENT_GENERATORS = new Integer(benchmark.clientProperties.getProperty("proasense.benchmark.kafka.predicted.generators")).intValue();
        int NO_PREDICTEDEVENT_RATE = new Integer(benchmark.clientProperties.getProperty("proasense.benchmark.kafka.predicted.rate")).intValue();
        int NO_PREDICTEDEVENT_MESSAGES = new Integer(benchmark.clientProperties.getProperty("proasense.benchmark.kafka.predicted.messages")).intValue();

        int NO_ANOMALYEVENT_GENERATORS = new Integer(benchmark.clientProperties.getProperty("proasense.benchmark.kafka.anomaly.generators")).intValue();
        int NO_ANOMALYEVENT_RATE = new Integer(benchmark.clientProperties.getProperty("proasense.benchmark.kafka.anomaly.rate")).intValue();
        int NO_ANOMALYEVENT_MESSAGES = new Integer(benchmark.clientProperties.getProperty("proasense.benchmark.kafka.anomaly.messages")).intValue();

        int NO_RECOMMENDATIONEVENT_GENERATORS = new Integer(benchmark.clientProperties.getProperty("proasense.benchmark.kafka.recommendation.generators")).intValue();
        int NO_RECOMMENDATIONEVENT_RATE = new Integer(benchmark.clientProperties.getProperty("proasense.benchmark.kafka.recommendation.rate")).intValue();
        int NO_RECOMMENDATIONEVENT_MESSAGES = new Integer(benchmark.clientProperties.getProperty("proasense.benchmark.kafka.recommendation.messages")).intValue();

        int NO_FEEDBACKEVENT_GENERATORS = new Integer(benchmark.clientProperties.getProperty("proasense.benchmark.kafka.feedback.generators")).intValue();
        int NO_FEEDBACKEVENT_RATE = new Integer(benchmark.clientProperties.getProperty("proasense.benchmark.kafka.feedback.rate")).intValue();
        int NO_FEEDBACKEVENT_MESSAGES = new Integer(benchmark.clientProperties.getProperty("proasense.benchmark.kafka.feedback.messages")).intValue();

        // Total number of threads
        int NO_TOTAL_THREADS = 0;

        if (IS_LOAD_TESTING_ENABLED)
            NO_TOTAL_THREADS = 1;
        else
            NO_TOTAL_THREADS = NO_SIMPLEEVENT_GENERATORS + NO_DERIVEDEVENT_GENERATORS
                + NO_PREDICTEDEVENT_GENERATORS + NO_ANOMALYEVENT_GENERATORS + NO_RECOMMENDATIONEVENT_GENERATORS + NO_FEEDBACKEVENT_GENERATORS;

        // Create executor environment for threads
        ArrayList<Runnable> workers = new ArrayList<Runnable>(NO_TOTAL_THREADS);
        ExecutorService executor = Executors.newFixedThreadPool(NO_TOTAL_THREADS);

        if (IS_LOAD_TESTING_ENABLED) {
            int NO_MESSAGES_PER_SECOND = NO_LOAD_TESTING_SENSORS * (1000/NO_LOAD_TESTING_RATE);
            int NO_MAX_MESSAGES = NO_LOAD_TESTING_SENSORS * NO_LOAD_TESTING_MESSAGES;
            if (IS_SIMPLEEVENT_FILTER)
                workers.add(new LoadTestingKafkaGenerator<SimpleEvent>(SimpleEvent.class, boostrapServers, groupId, SIMPLEEVENT_TOPIC + ".0", "load_testing", NO_MESSAGES_PER_SECOND, NO_MAX_MESSAGES));
            else
                workers.add(new LoadTestingKafkaGenerator<SimpleEvent>(SimpleEvent.class, boostrapServers, groupId, SIMPLEEVENT_TOPIC, "load_testing", NO_MESSAGES_PER_SECOND, NO_MAX_MESSAGES));
        }
        else {
            // Create threads for random simple event generators
            for (int i = 0; i < NO_SIMPLEEVENT_GENERATORS; i++) {
                if (IS_SIMPLEEVENT_FILTER)
                    workers.add(new RandomEventKafkaGenerator<SimpleEvent>(SimpleEvent.class, boostrapServers, groupId, SIMPLEEVENT_TOPIC + "." + i, "mhwirth." + i, NO_SIMPLEEVENT_RATE, NO_SIMPLEEVENT_MESSAGES));
                else
                    workers.add(new RandomEventKafkaGenerator<SimpleEvent>(SimpleEvent.class, boostrapServers, groupId, SIMPLEEVENT_TOPIC, "mhwirth." + i, NO_SIMPLEEVENT_RATE, NO_SIMPLEEVENT_MESSAGES));
            }

            // Create threads for random derived event generators
            for (int i = 0; i < NO_DERIVEDEVENT_GENERATORS; i++) {
                if (IS_DERIVEDEVENT_FILTER)
                    workers.add(new RandomEventKafkaGenerator<DerivedEvent>(DerivedEvent.class, boostrapServers, groupId, DERIVEDEVENT_TOPIC + "." + i, "mhwirth." + i, NO_DERIVEDEVENT_RATE, NO_DERIVEDEVENT_MESSAGES));
                else
                    workers.add(new RandomEventKafkaGenerator<DerivedEvent>(DerivedEvent.class, boostrapServers, groupId, DERIVEDEVENT_TOPIC, "mhwirth." + i, NO_DERIVEDEVENT_RATE, NO_DERIVEDEVENT_MESSAGES));
            }

            // Create threads for random predicted event generators
            for (int i = 0; i < NO_PREDICTEDEVENT_GENERATORS; i++) {
                if (IS_PREDICTEDEVENT_FILTER)
                    workers.add(new RandomEventKafkaGenerator<PredictedEvent>(PredictedEvent.class, boostrapServers, groupId, PREDICTEDEVENT_TOPIC + "." + i, "mhwirth." + i, NO_PREDICTEDEVENT_RATE, NO_PREDICTEDEVENT_MESSAGES));
                else
                    workers.add(new RandomEventKafkaGenerator<PredictedEvent>(PredictedEvent.class, boostrapServers, groupId, PREDICTEDEVENT_TOPIC, "mhwirth." + i, NO_PREDICTEDEVENT_RATE, NO_PREDICTEDEVENT_MESSAGES));
            }

            // Create threads for random anomaly event generators
            for (int i = 0; i < NO_ANOMALYEVENT_GENERATORS; i++) {
                if (IS_ANOMALYEVENT_FILTER)
                    workers.add(new RandomEventKafkaGenerator<AnomalyEvent>(AnomalyEvent.class, boostrapServers, groupId, ANOMALYEVENT_TOPIC + "." + i, "mhwirth." + i, NO_ANOMALYEVENT_RATE, NO_ANOMALYEVENT_MESSAGES));
                else
                    workers.add(new RandomEventKafkaGenerator<AnomalyEvent>(AnomalyEvent.class, boostrapServers, groupId, ANOMALYEVENT_TOPIC, "mhwirth." + i, NO_ANOMALYEVENT_RATE, NO_ANOMALYEVENT_MESSAGES));
            }

            // Create threads for random recommendation event generators
            for (int i = 0; i < NO_RECOMMENDATIONEVENT_GENERATORS; i++) {
                if (IS_RECOMMENDATIONEVENT_FILTER)
                    workers.add(new RandomEventKafkaGenerator<RecommendationEvent>(RecommendationEvent.class, boostrapServers, groupId, RECOMMENDATIONEVENT_TOPIC + "." + i, "mhwirth." + i, NO_RECOMMENDATIONEVENT_RATE, NO_RECOMMENDATIONEVENT_MESSAGES));
                else
                    workers.add(new RandomEventKafkaGenerator<RecommendationEvent>(RecommendationEvent.class, boostrapServers, groupId, RECOMMENDATIONEVENT_TOPIC, "mhwirth." + i, NO_RECOMMENDATIONEVENT_RATE, NO_RECOMMENDATIONEVENT_MESSAGES));
            }

            // Create threads for random feedback event generators
            for (int i = 0; i < NO_FEEDBACKEVENT_GENERATORS; i++) {
                if (IS_FEEDBACKEVENT_FILTER)
                    workers.add(new RandomEventKafkaGenerator<FeedbackEvent>(FeedbackEvent.class, boostrapServers, groupId, FEEDBACKEVENT_TOPIC + "." + i, "mhwirth." + i, NO_FEEDBACKEVENT_RATE, NO_FEEDBACKEVENT_MESSAGES));
                else
                    workers.add(new RandomEventKafkaGenerator<FeedbackEvent>(FeedbackEvent.class, boostrapServers, groupId, FEEDBACKEVENT_TOPIC, "mhwirth." + i, NO_FEEDBACKEVENT_RATE, NO_FEEDBACKEVENT_MESSAGES));
            }
        }

        // Execute all threads
        for (int i = 0; i < NO_TOTAL_THREADS; i++) {
            executor.execute(workers.get(i));
        }

        // Shut down executor
        executor.shutdown();
    }

}
