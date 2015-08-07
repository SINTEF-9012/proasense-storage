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

import net.modelbased.proasense.storage.EventDocument;

import eu.proasense.internal.AnomalyEvent;
import eu.proasense.internal.DerivedEvent;
import eu.proasense.internal.FeedbackEvent;
import eu.proasense.internal.PredictedEvent;
import eu.proasense.internal.RecommendationEvent;
import eu.proasense.internal.SimpleEvent;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Properties;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


@Path("/")
public class StorageWriterMongoService {
    private Properties serverProperties;


    public StorageWriterMongoService() {
        // Get server properties
        this.serverProperties = loadServerProperties();
    }


    private Properties loadServerProperties() {
        serverProperties = new Properties();
        String propFilename = "server.properties";
        InputStream inputStream = getClass().getClassLoader().getResourceAsStream(propFilename);

        try {
            if (inputStream != null) {
                serverProperties.load(inputStream);
            } else
                throw new FileNotFoundException("Property file: '" + propFilename + "' not found in classpath.");
        }
        catch (IOException e) {
            System.out.println(e.getClass().getName() + ": " + e.getMessage());
        }

        return serverProperties;
    }


    @GET
    @Path("/server/status")
    @Produces(MediaType.TEXT_PLAIN)
    public Response getServerStatus() {
        String result = "ProaSense Storage Writer Service running...";

        // Return HTTP response 200 in case of success
        return Response.status(200).entity(result).build();
    }


    @GET
    @Path("/server/properties")
    @Produces(MediaType.TEXT_PLAIN)
    public Response getServerProperties() {
        String result = this.serverProperties.toString();

        // Return HTTP response 200 in case of success
        return Response.status(200).entity(result).build();
    }


    public static void main(String[] args) {
        // Get server properties
        StorageWriterMongoService storage = new StorageWriterMongoService();
//        storage.loadServerProperties();

        // Benchmark common properties
        boolean IS_BENCHMARK_LOGFILE = new Boolean(storage.serverProperties.getProperty("proasense.benchmark.common.logfile")).booleanValue();
        int NO_BENCHMARK_LOGSIZE = new Integer(storage.serverProperties.getProperty("proasense.benchmark.common.logsize")).intValue();

        // Benchmark load testing properties
        boolean IS_LOAD_TESTING_ENABLED = new Boolean(storage.serverProperties.getProperty("proasense.benchmark.load.testing")).booleanValue();
        int NO_LOAD_TESTING_SENSORS = new Integer(storage.serverProperties.getProperty("proasense.benchmark.load.sensors")).intValue();
        int NO_LOAD_TESTING_RATE = new Integer(storage.serverProperties.getProperty("proasense.benchmark.load.rate")).intValue();
        int NO_LOAD_TESTING_MESSAGES = new Integer(storage.serverProperties.getProperty("proasense.benchmark.load.messages")).intValue();
        int NO_LOAD_TESTING_MESSAGES_PER_SECOND = NO_LOAD_TESTING_SENSORS * (1000/NO_LOAD_TESTING_RATE);
        int NO_LOAD_TESTING_MAX_MESSAGES = NO_LOAD_TESTING_SENSORS * NO_LOAD_TESTING_MESSAGES;

        // Kafka broker configuration properties
        String zooKeeper = storage.serverProperties.getProperty("zookeeper.connect");
        String groupId = "StorageWriterServiceMongoServer";

        // SensApp registry configuration properties
        String sensappURL = storage.serverProperties.getProperty("proasense.storage.sensapp.url");

        // Kafka event listeners configuration properties
        String SIMPLEEVENT_TOPIC = storage.serverProperties.getProperty("proasense.storage.event.simple.topic");
        String DERIVEDEVENT_TOPIC = storage.serverProperties.getProperty("proasense.storage.event.derived.topic");
        String PREDICTEDEVENT_TOPIC = storage.serverProperties.getProperty("proasense.storage.event.predicted.topic");
        String ANOMALYEVENT_TOPIC = storage.serverProperties.getProperty("proasense.storage.event.anomaly.topic");
        String RECOMMENDATIONEVENT_TOPIC = storage.serverProperties.getProperty("proasense.storage.event.recommendation.topic");
        String FEEDBACKEVENT_TOPIC = storage.serverProperties.getProperty("proasense.storage.event.feedback.topic");

        boolean IS_SIMPLEEVENT_FILTER = new Boolean(storage.serverProperties.getProperty("proasense.storage.event.simple.filter")).booleanValue();
        boolean IS_DERIVEDEVENT_FILTER = new Boolean(storage.serverProperties.getProperty("proasense.storage.event.derived.filter")).booleanValue();
        boolean IS_PREDICTEDEVENT_FILTER = new Boolean(storage.serverProperties.getProperty("proasense.storage.event.predicted.filter")).booleanValue();
        boolean IS_ANOMALYEVENT_FILTER = new Boolean(storage.serverProperties.getProperty("proasense.storage.event.anomaly.filter")).booleanValue();
        boolean IS_RECOMMENDATIONEVENT_FILTER = new Boolean(storage.serverProperties.getProperty("proasense.storage.event.recommendation.filter")).booleanValue();
        boolean IS_FEEDBACKEVENT_FILTER = new Boolean(storage.serverProperties.getProperty("proasense.storage.event.feedback.filter")).booleanValue();

        int NO_SIMPLEEVENT_LISTENERS = new Integer(storage.serverProperties.getProperty("proasense.storage.event.simple.listeners")).intValue();
        int NO_DERIVEDEVENT_LISTENERS = new Integer(storage.serverProperties.getProperty("proasense.storage.event.derived.listeners")).intValue();
        int NO_PREDICTEDEVENT_LISTENERS = new Integer(storage.serverProperties.getProperty("proasense.storage.event.predicted.listeners")).intValue();
        int NO_ANOMALYEVENT_LISTENERS = new Integer(storage.serverProperties.getProperty("proasense.storage.event.anomaly.listeners")).intValue();
        int NO_RECOMMENDATIONEVENT_LISTENERS = new Integer(storage.serverProperties.getProperty("proasense.storage.event.recommendation.listeners")).intValue();
        int NO_FEEDBACKEVENT_LISTENERS = new Integer(storage.serverProperties.getProperty("proasense.storage.event.feedback.listeners")).intValue();

        // MongoDB event writers configuration properties
        String MONGODB_URL = storage.serverProperties.getProperty("proasense.storage.mongodb.url");

        boolean IS_MONGODB_SYNCDRIVER = new Boolean(storage.serverProperties.getProperty("proasense.storage.mongodb.syncdriver")).booleanValue();

        int NO_MONGODB_WRITERS = new Integer(storage.serverProperties.getProperty("proasense.storage.mongodb.writers")).intValue();
        int NO_MONGODB_BULKSIZE = new Integer(storage.serverProperties.getProperty("proasense.storage.mongodb.bulksize")).intValue();
        int NO_MONGODB_MAXWAIT = new Integer(storage.serverProperties.getProperty("proasense.storage.mongodb.maxwait")).intValue();
        int NO_MONGODB_HEARTBEAT = NO_MONGODB_MAXWAIT*2;

        // Blocking queue for multi-threaded application
        int NO_BLOCKINGQUEUE_SIZE = 1000000;
        BlockingQueue<EventDocument> queue = new ArrayBlockingQueue<EventDocument>(NO_BLOCKINGQUEUE_SIZE);

        // Total number of threads
        int NO_TOTAL_THREADS = 0;

        if (IS_LOAD_TESTING_ENABLED)
            NO_TOTAL_THREADS = NO_SIMPLEEVENT_LISTENERS
                    + NO_MONGODB_WRITERS + 1;
        else
            NO_TOTAL_THREADS = NO_SIMPLEEVENT_LISTENERS + NO_DERIVEDEVENT_LISTENERS
                    + NO_PREDICTEDEVENT_LISTENERS + NO_ANOMALYEVENT_LISTENERS
                    + NO_RECOMMENDATIONEVENT_LISTENERS + NO_FEEDBACKEVENT_LISTENERS
                    + NO_MONGODB_WRITERS + 1;

        // Create executor environment for threads
        ArrayList<Runnable> workers = new ArrayList<Runnable>(NO_TOTAL_THREADS);
        ExecutorService executor = Executors.newFixedThreadPool(NO_TOTAL_THREADS);

        if (IS_LOAD_TESTING_ENABLED) {
            // Create threads for Kafka simple event listeners
            for (int i = 0; i < NO_SIMPLEEVENT_LISTENERS; i++) {
                if (IS_SIMPLEEVENT_FILTER)
                    workers.add(new EventListenerKafkaFilter<SimpleEvent>(SimpleEvent.class, queue, zooKeeper, groupId, SIMPLEEVENT_TOPIC));
                else
                    workers.add(new EventListenerKafkaTopic<SimpleEvent>(SimpleEvent.class, queue, zooKeeper, groupId, SIMPLEEVENT_TOPIC));
            }
        }
        else {
            // Create threads for Kafka simple event listeners
            for (int i = 0; i < NO_SIMPLEEVENT_LISTENERS; i++) {
                if (IS_SIMPLEEVENT_FILTER)
                    workers.add(new EventListenerKafkaFilter<SimpleEvent>(SimpleEvent.class, queue, zooKeeper, groupId, SIMPLEEVENT_TOPIC));
                else
                    workers.add(new EventListenerKafkaTopic<SimpleEvent>(SimpleEvent.class, queue, zooKeeper, groupId, SIMPLEEVENT_TOPIC));
            }

            // Create threads for Kafka derived event listeners
            for (int i = 0; i < NO_DERIVEDEVENT_LISTENERS; i++) {
                if (IS_DERIVEDEVENT_FILTER)
                    workers.add(new EventListenerKafkaFilter<DerivedEvent>(DerivedEvent.class, queue, zooKeeper, groupId, DERIVEDEVENT_TOPIC));
                else
                    workers.add(new EventListenerKafkaTopic<DerivedEvent>(DerivedEvent.class, queue, zooKeeper, groupId, DERIVEDEVENT_TOPIC));
            }

            // Create threads for Kafka predicted event listeners
            for (int i = 0; i < NO_PREDICTEDEVENT_LISTENERS; i++) {
                if (IS_PREDICTEDEVENT_FILTER)
                    workers.add(new EventListenerKafkaFilter<PredictedEvent>(PredictedEvent.class, queue, zooKeeper, groupId, PREDICTEDEVENT_TOPIC));
                else
                    workers.add(new EventListenerKafkaTopic<PredictedEvent>(PredictedEvent.class, queue, zooKeeper, groupId, PREDICTEDEVENT_TOPIC));
            }

            // Create threads for Kafka anomaly event listeners
            for (int i = 0; i < NO_ANOMALYEVENT_LISTENERS; i++) {
                if (IS_ANOMALYEVENT_FILTER)
                    workers.add(new EventListenerKafkaFilter<AnomalyEvent>(AnomalyEvent.class, queue, zooKeeper, groupId, ANOMALYEVENT_TOPIC));
                else
                    workers.add(new EventListenerKafkaTopic<AnomalyEvent>(AnomalyEvent.class, queue, zooKeeper, groupId, ANOMALYEVENT_TOPIC));
            }

            // Create threads for Kafka recommendation event listeners
            for (int i = 0; i < NO_RECOMMENDATIONEVENT_LISTENERS; i++) {
                if (IS_RECOMMENDATIONEVENT_FILTER)
                    workers.add(new EventListenerKafkaFilter<RecommendationEvent>(RecommendationEvent.class, queue, zooKeeper, groupId, RECOMMENDATIONEVENT_TOPIC));
                else
                    workers.add(new EventListenerKafkaTopic<RecommendationEvent>(RecommendationEvent.class, queue, zooKeeper, groupId, RECOMMENDATIONEVENT_TOPIC));
            }

            // Create threads for Kafka feedback event listeners
            for (int i = 0; i < NO_FEEDBACKEVENT_LISTENERS; i++) {
                if (IS_FEEDBACKEVENT_FILTER)
                    workers.add(new EventListenerKafkaFilter<FeedbackEvent>(FeedbackEvent.class, queue, zooKeeper, groupId, FEEDBACKEVENT_TOPIC));
                else
                    workers.add(new EventListenerKafkaTopic<FeedbackEvent>(FeedbackEvent.class, queue, zooKeeper, groupId, FEEDBACKEVENT_TOPIC));
            }
        }

        // Create threads for MongoDB event writers
        for (int i = 0; i < NO_MONGODB_WRITERS; i++) {
            if (IS_LOAD_TESTING_ENABLED) {
                if (IS_MONGODB_SYNCDRIVER)
                    workers.add(new EventWriterMongoSync(queue, MONGODB_URL, NO_MONGODB_BULKSIZE, NO_MONGODB_MAXWAIT, IS_BENCHMARK_LOGFILE, NO_BENCHMARK_LOGSIZE, i, NO_LOAD_TESTING_MAX_MESSAGES));
                else
                    workers.add(new EventWriterMongoAsync(queue, MONGODB_URL, NO_MONGODB_BULKSIZE, NO_MONGODB_MAXWAIT, IS_BENCHMARK_LOGFILE, NO_BENCHMARK_LOGSIZE, i, NO_LOAD_TESTING_MAX_MESSAGES));
            }
            else {
                if (IS_MONGODB_SYNCDRIVER)
                    workers.add(new EventWriterMongoSync(queue, MONGODB_URL, NO_MONGODB_BULKSIZE, NO_MONGODB_MAXWAIT, IS_BENCHMARK_LOGFILE, NO_BENCHMARK_LOGSIZE, i));
                else
                    workers.add(new EventWriterMongoAsync(queue, MONGODB_URL, NO_MONGODB_BULKSIZE, NO_MONGODB_MAXWAIT, IS_BENCHMARK_LOGFILE, NO_BENCHMARK_LOGSIZE, i));
            }
        }

        // Create thread for MongoDB heartbeat
        workers.add(new EventHeartbeat(queue, NO_MONGODB_HEARTBEAT));

        // Execute all threads
        for (int i = 0; i < NO_TOTAL_THREADS; i++) {
            executor.execute(workers.get(i));
        }

        // Shut down executor
        executor.shutdown();
    }

}
