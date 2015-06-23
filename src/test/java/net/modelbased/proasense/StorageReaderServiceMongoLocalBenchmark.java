/**
 * Copyright 2015 Brian Elvesæter <brian.elvesater@sintef.no>
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

import eu.proasense.internal.SimpleEvent;
import net.modelbased.proasense.storage.EventConverter;
import net.modelbased.proasense.storage.EventQueryOperation;
import net.modelbased.proasense.storage.EventQueryType;
import net.modelbased.proasense.storage.EventReaderMongoSync;
import net.modelbased.proasense.storage.EventProperties;
import org.bson.Document;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public class StorageReaderServiceMongoLocalBenchmark {
    private Properties clientProperties;


    public StorageReaderServiceMongoLocalBenchmark() {
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
        StorageReaderServiceMongoLocalBenchmark benchmark = new StorageReaderServiceMongoLocalBenchmark();
        benchmark.loadClientProperties();

        // Kafka broker configuration properties
        String zooKeeper = benchmark.clientProperties.getProperty("zookeeper.connect");
        String groupId = "StorageReaderServiceMongoLocalBenchmark";

        // MongoDB storage configuration properties
        String MONGODB_URL = benchmark.clientProperties.getProperty("proasense.storage.mongodb.url");

        // Total number of threads
        int NO_TOTAL_THREADS = 1;

        // Create executor environment for threads
        ArrayList<Callable> workers = new ArrayList<Callable>(NO_TOTAL_THREADS);
        ExecutorService executor = Executors.newFixedThreadPool(NO_TOTAL_THREADS);

        long NO_QUERY_SIMPLE_STARTTIME = new Long(benchmark.clientProperties.getProperty("proasense.benchmark.query.simple.starttime")).longValue();
        long NO_QUERY_SIMPLE_ENDTIME = new Long(benchmark.clientProperties.getProperty("proasense.benchmark.query.simple.endtime")).longValue();
        long NO_QUERY_DERIVED_STARTTIME = new Long(benchmark.clientProperties.getProperty("proasense.benchmark.query.derived.starttime")).longValue();
        long NO_QUERY_DERIVED_ENDTIME = new Long(benchmark.clientProperties.getProperty("proasense.benchmark.query.derived.endtime")).longValue();
        long NO_QUERY_PREDICTED_STARTTIME = new Long(benchmark.clientProperties.getProperty("proasense.benchmark.query.predicted.starttime")).longValue();
        long NO_QUERY_PREDICTED_ENDTIME = new Long(benchmark.clientProperties.getProperty("proasense.benchmark.query.predicted.endtime")).longValue();
        long NO_QUERY_ANOMALY_STARTTIME = new Long(benchmark.clientProperties.getProperty("proasense.benchmark.query.anomaly.starttime")).longValue();
        long NO_QUERY_ANOMALY_ENDTIME = new Long(benchmark.clientProperties.getProperty("proasense.benchmark.query.anomaly.endtime")).longValue();
        long NO_QUERY_RECOMMENDATION_STARTTIME = new Long(benchmark.clientProperties.getProperty("proasense.benchmark.query.recommendation.starttime")).longValue();
        long NO_QUERY_RECOMMENDATION_ENDTIME = new Long(benchmark.clientProperties.getProperty("proasense.benchmark.query.recommendation.endtime")).longValue();
        long NO_QUERY_FEEDBACK_STARTTIME = new Long(benchmark.clientProperties.getProperty("proasense.benchmark.query.feedback.starttime")).longValue();
        long NO_QUERY_FEEDBACK_ENDTIME = new Long(benchmark.clientProperties.getProperty("proasense.benchmark.query.feedback.endtime")).longValue();

        // Default query for simple events
        Callable<List<Document>> query01 = new EventReaderMongoSync(MONGODB_URL, EventQueryType.SIMPLE, "simple.mhwirth.0", NO_QUERY_SIMPLE_STARTTIME, NO_QUERY_SIMPLE_ENDTIME, EventQueryOperation.DEFAULT, null);
        executor.submit(query01);
        try {
            List<Document> queryResult = query01.call();
            int i = 0;
            for (Document doc : queryResult) {
                i++;
                System.out.println("DEBUG(SIMPLE.DEFAULT(" + i + ")): " + doc.toString());
                System.out.println("DEBUG(SIMPLEEVENT(" + i + ")): " + new EventConverter<SimpleEvent>(SimpleEvent.class, doc).getEvent().toString());
            }
        } catch (Exception e) {
            System.out.println(e.getClass().getName() + ": " + e.getMessage());
        }

        // Average query for simple events
        Callable<List<Document>> query02 = new EventReaderMongoSync(MONGODB_URL, EventQueryType.SIMPLE, "simple.mhwirth.0", NO_QUERY_SIMPLE_STARTTIME, NO_QUERY_SIMPLE_ENDTIME, EventQueryOperation.AVERAGE, null);
        executor.submit(query02);
        try {
            List<Document> queryResult = query02.call();
            for (Document doc : queryResult) {
                System.out.println("DEBUG(SIMPLE.AVERAGE): " + doc.toString());
            }
        } catch (Exception e) {
            System.out.println(e.getClass().getName() + ": " + e.getMessage());
        }

        // Maximum query for simple events
        Callable<List<Document>> query03 = new EventReaderMongoSync(MONGODB_URL, EventQueryType.SIMPLE, "simple.mhwirth.0", NO_QUERY_SIMPLE_STARTTIME, NO_QUERY_SIMPLE_ENDTIME, EventQueryOperation.MAXIMUM, null);
        executor.submit(query03);
        try {
            List<Document> queryResult = query03.call();
            for (Document doc : queryResult) {
                System.out.println("DEBUG(SIMPLE.MAXIMUM): " + doc.toString());
            }
        } catch (Exception e) {
            System.out.println(e.getClass().getName() + ": " + e.getMessage());
        }

        // Minimum query for simple events
        Callable<List<Document>> query04 = new EventReaderMongoSync(MONGODB_URL, EventQueryType.SIMPLE, "simple.mhwirth.0", NO_QUERY_SIMPLE_STARTTIME, NO_QUERY_SIMPLE_ENDTIME, EventQueryOperation.MINUMUM, null);
        executor.submit(query04);
        try {
            List<Document> queryResult = query04.call();
            for (Document doc : queryResult) {
                System.out.println("DEBUG(SIMPLE.MINIMUM): " + doc.toString());
            }
        } catch (Exception e) {
            System.out.println(e.getClass().getName() + ": " + e.getMessage());
        }

        // Default query for derived events
        Callable<List<Document>> query05 = new EventReaderMongoSync(MONGODB_URL, EventQueryType.DERIVED, "derived.mhwirth.0", NO_QUERY_DERIVED_STARTTIME, NO_QUERY_DERIVED_ENDTIME, EventQueryOperation.DEFAULT, null);
        executor.submit(query05);
        try {
            List<Document> queryResult = query05.call();
            int i = 0;
            for (Document doc : queryResult) {
                i++;
                System.out.println("DEBUG(DERIVED.DEFAULT(" + i + ")): " + doc.toString());
            }
        } catch (Exception e) {
            System.out.println(e.getClass().getName() + ": " + e.getMessage());
        }

        // Default query for predicted events
        Callable<List<Document>> query06 = new EventReaderMongoSync(MONGODB_URL, EventQueryType.PREDICTED, EventProperties.PREDICTEDEVENT_STORAGE_COLLECTION_NAME, NO_QUERY_PREDICTED_STARTTIME, NO_QUERY_PREDICTED_ENDTIME, EventQueryOperation.DEFAULT, null);
        executor.submit(query06);
        try {
            List<Document> queryResult = query06.call();
            int i = 0;
            for (Document doc : queryResult) {
                i++;
                System.out.println("DEBUG(PREDICTED.DEFAULT(" + i + ")): " + doc.toString());
            }
        } catch (Exception e) {
            System.out.println(e.getClass().getName() + ": " + e.getMessage());
        }

        // Default query for anomaly events
        Callable<List<Document>> query07 = new EventReaderMongoSync(MONGODB_URL, EventQueryType.ANOMALY, EventProperties.ANOMALYEVENT_STORAGE_COLLECTION_NAME, NO_QUERY_ANOMALY_STARTTIME, NO_QUERY_ANOMALY_ENDTIME, EventQueryOperation.DEFAULT, null);
        executor.submit(query07);
        try {
            List<Document> queryResult = query07.call();
            int i = 0;
            for (Document doc : queryResult) {
                i++;
                System.out.println("DEBUG(ANOMALY.DEFAULT(" + i + ")): " + doc.toString());
            }
        } catch (Exception e) {
            System.out.println(e.getClass().getName() + ": " + e.getMessage());
        }

        // Default query for recommendation events
        Callable<List<Document>> query08 = new EventReaderMongoSync(MONGODB_URL, EventQueryType.RECOMMENDATION, EventProperties.RECOMMENDATIONEVENT_STORAGE_COLLECTION_NAME, NO_QUERY_RECOMMENDATION_STARTTIME, NO_QUERY_RECOMMENDATION_ENDTIME, EventQueryOperation.DEFAULT, null);
        executor.submit(query08);
        try {
            List<Document> queryResult = query08.call();
            int i = 0;
            for (Document doc : queryResult) {
                i++;
                System.out.println("DEBUG(RECOMMENDATION.DEFAULT(" + i + ")): " + doc.toString());
            }
        } catch (Exception e) {
            System.out.println(e.getClass().getName() + ": " + e.getMessage());
        }

        // Default query for feedback events
        Callable<List<Document>> query09 = new EventReaderMongoSync(MONGODB_URL, EventQueryType.FEEDBACK, EventProperties.FEEDBACKEVENT_STORAGE_COLLECTION_NAME, NO_QUERY_FEEDBACK_STARTTIME, NO_QUERY_FEEDBACK_ENDTIME, EventQueryOperation.DEFAULT, null);
        executor.submit(query09);
        try {
            List<Document> queryResult = query09.call();
            int i = 0;
            for (Document doc : queryResult) {
                i++;
                System.out.println("DEBUG(FEEDBACK.DEFAULT(" + i + ")): " + doc.toString());
            }
        } catch (Exception e) {
            System.out.println(e.getClass().getName() + ": " + e.getMessage());
        }

        // Shut down executor
        executor.shutdown();
    }

}
