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
package net.modelbased.proasense.storage.reader;

import eu.proasense.internal.SimpleEvent;
import net.modelbased.proasense.storage.EventConverter;
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


public class StorageReaderMongoServiceLocalBenchmark {
    private Properties clientProperties;


    public StorageReaderMongoServiceLocalBenchmark() {
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
        StorageReaderMongoServiceLocalBenchmark benchmark = new StorageReaderMongoServiceLocalBenchmark();
        benchmark.loadClientProperties();

        // Kafka broker configuration properties
        String zooKeeper = benchmark.clientProperties.getProperty("zookeeper.connect");
        String groupId = "StorageReaderServiceMongoLocalBenchmark";

        // MongoDB storage configuration properties
        String MONGODB_URL = benchmark.clientProperties.getProperty("proasense.storage.mongodb.url");
        String MONGODB_DATABASE = benchmark.clientProperties.getProperty("proasense.storage.mongodb.database");

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

        String propertyKey = "value";

        // Default query for simple events
        Callable<List<Document>> query11 = new EventReaderMongoSync(MONGODB_URL, MONGODB_DATABASE, EventQueryType.SIMPLE, "simple.mhwirth.0", NO_QUERY_SIMPLE_STARTTIME, NO_QUERY_SIMPLE_ENDTIME, propertyKey, EventQueryOperation.DEFAULT, null);
        executor.submit(query11);
        try {
            List<Document> queryResult = query11.call();
            int i = 0;
            for (Document doc : queryResult) {
                i++;
                if (i % 1000 == 0) {
//                    System.out.println("SIMPLE.DEFAULT(" + i + "): " + doc.toString());
                    System.out.println("SIMPLE.DEFAULT(" + i + "): " + new EventConverter<SimpleEvent>(SimpleEvent.class, doc).getEvent().toString());
                }
            }
        } catch (Exception e) {
            System.out.println(e.getClass().getName() + ": " + e.getMessage());
        }

        // Average query for simple events
        Callable<List<Document>> query12 = new EventReaderMongoSync(MONGODB_URL, MONGODB_DATABASE, EventQueryType.SIMPLE, "simple.mhwirth.0", NO_QUERY_SIMPLE_STARTTIME, NO_QUERY_SIMPLE_ENDTIME, propertyKey, EventQueryOperation.AVERAGE, null);
        executor.submit(query12);
        try {
            List<Document> queryResult = query12.call();
            for (Document doc : queryResult) {
                System.out.println("SIMPLE.AVERAGE: " + doc.toString());
            }
        } catch (Exception e) {
            System.out.println(e.getClass().getName() + ": " + e.getMessage());
        }

        // Maximum query for simple events
        Callable<List<Document>> query13 = new EventReaderMongoSync(MONGODB_URL, MONGODB_DATABASE, EventQueryType.SIMPLE, "simple.mhwirth.0", NO_QUERY_SIMPLE_STARTTIME, NO_QUERY_SIMPLE_ENDTIME, propertyKey, EventQueryOperation.MAXIMUM, null);
        executor.submit(query13);
        try {
            List<Document> queryResult = query13.call();
            for (Document doc : queryResult) {
                System.out.println("SIMPLE.MAXIMUM: " + doc.toString());
            }
        } catch (Exception e) {
            System.out.println(e.getClass().getName() + ": " + e.getMessage());
        }

        // Minimum query for simple events
        Callable<List<Document>> query14 = new EventReaderMongoSync(MONGODB_URL, MONGODB_DATABASE, EventQueryType.SIMPLE, "simple.mhwirth.0", NO_QUERY_SIMPLE_STARTTIME, NO_QUERY_SIMPLE_ENDTIME, propertyKey, EventQueryOperation.MINUMUM, null);
        executor.submit(query14);
        try {
            List<Document> queryResult = query14.call();
            for (Document doc : queryResult) {
                System.out.println("SIMPLE.MINIMUM: " + doc.toString());
            }
        } catch (Exception e) {
            System.out.println(e.getClass().getName() + ": " + e.getMessage());
        }

        // Default query for derived events
        Callable<List<Document>> query21 = new EventReaderMongoSync(MONGODB_URL, MONGODB_DATABASE, EventQueryType.DERIVED, "derived.mhwirth.0", NO_QUERY_DERIVED_STARTTIME, NO_QUERY_DERIVED_ENDTIME, propertyKey, EventQueryOperation.DEFAULT, null);
        executor.submit(query21);
        try {
            List<Document> queryResult = query21.call();
            int i = 0;
            for (Document doc : queryResult) {
                i++;
                if (i % 1000 == 0)
                    System.out.println("DERIVED.DEFAULT(" + i + "): " + doc.toString());
            }
        } catch (Exception e) {
            System.out.println(e.getClass().getName() + ": " + e.getMessage());
        }

        // Average query for derived events
        Callable<List<Document>> query22 = new EventReaderMongoSync(MONGODB_URL, MONGODB_DATABASE, EventQueryType.DERIVED, "derived.mhwirth.0", NO_QUERY_SIMPLE_STARTTIME, NO_QUERY_SIMPLE_ENDTIME, propertyKey, EventQueryOperation.AVERAGE, null);
        executor.submit(query22);
        try {
            List<Document> queryResult = query22.call();
            for (Document doc : queryResult) {
                System.out.println("DERIVED.AVERAGE: " + doc.toString());
            }
        } catch (Exception e) {
            System.out.println(e.getClass().getName() + ": " + e.getMessage());
        }

        // Maximum query for derived events
        Callable<List<Document>> query23 = new EventReaderMongoSync(MONGODB_URL, MONGODB_DATABASE, EventQueryType.DERIVED, "derived.mhwirth.0", NO_QUERY_SIMPLE_STARTTIME, NO_QUERY_SIMPLE_ENDTIME, propertyKey, EventQueryOperation.MAXIMUM, null);
        executor.submit(query23);
        try {
            List<Document> queryResult = query23.call();
            for (Document doc : queryResult) {
                System.out.println("DERIVED.MAXIMUM: " + doc.toString());
            }
        } catch (Exception e) {
            System.out.println(e.getClass().getName() + ": " + e.getMessage());
        }

        // Minimum query for derived events
        Callable<List<Document>> query24 = new EventReaderMongoSync(MONGODB_URL, MONGODB_DATABASE, EventQueryType.DERIVED, "derived.mhwirth.0", NO_QUERY_SIMPLE_STARTTIME, NO_QUERY_SIMPLE_ENDTIME, propertyKey, EventQueryOperation.MINUMUM, null);
        executor.submit(query24);
        try {
            List<Document> queryResult = query24.call();
            for (Document doc : queryResult) {
                System.out.println("DERIVED.MINIMUM: " + doc.toString());
            }
        } catch (Exception e) {
            System.out.println(e.getClass().getName() + ": " + e.getMessage());
        }

        // Default query for predicted events
        Callable<List<Document>> query31 = new EventReaderMongoSync(MONGODB_URL, MONGODB_DATABASE, EventQueryType.PREDICTED, EventProperties.PREDICTEDEVENT_STORAGE_COLLECTION_NAME, NO_QUERY_PREDICTED_STARTTIME, NO_QUERY_PREDICTED_ENDTIME, propertyKey, EventQueryOperation.DEFAULT, null);
        executor.submit(query31);
        try {
            List<Document> queryResult = query31.call();
            int i = 0;
            for (Document doc : queryResult) {
                i++;
                System.out.println("PREDICTED.DEFAULT(" + i + "): " + doc.toString());
            }
        } catch (Exception e) {
            System.out.println(e.getClass().getName() + ": " + e.getMessage());
        }

        // Average query for predicted events
        Callable<List<Document>> query32 = new EventReaderMongoSync(MONGODB_URL, MONGODB_DATABASE, EventQueryType.PREDICTED, EventProperties.PREDICTEDEVENT_STORAGE_COLLECTION_NAME, NO_QUERY_SIMPLE_STARTTIME, NO_QUERY_SIMPLE_ENDTIME, propertyKey, EventQueryOperation.AVERAGE, null);
        executor.submit(query32);
        try {
            List<Document> queryResult = query32.call();
            for (Document doc : queryResult) {
                System.out.println("PREDICTED.AVERAGE: " + doc.toString());
            }
        } catch (Exception e) {
            System.out.println(e.getClass().getName() + ": " + e.getMessage());
        }

        // Maximum query for predicted events
        Callable<List<Document>> query33 = new EventReaderMongoSync(MONGODB_URL, MONGODB_DATABASE, EventQueryType.PREDICTED, EventProperties.PREDICTEDEVENT_STORAGE_COLLECTION_NAME, NO_QUERY_SIMPLE_STARTTIME, NO_QUERY_SIMPLE_ENDTIME, propertyKey, EventQueryOperation.MAXIMUM, null);
        executor.submit(query33);
        try {
            List<Document> queryResult = query33.call();
            for (Document doc : queryResult) {
                System.out.println("PREDICTED.MAXIMUM: " + doc.toString());
            }
        } catch (Exception e) {
            System.out.println(e.getClass().getName() + ": " + e.getMessage());
        }

        // Minimum query for derived events
        Callable<List<Document>> query34 = new EventReaderMongoSync(MONGODB_URL, MONGODB_DATABASE, EventQueryType.PREDICTED, EventProperties.PREDICTEDEVENT_STORAGE_COLLECTION_NAME, NO_QUERY_SIMPLE_STARTTIME, NO_QUERY_SIMPLE_ENDTIME, propertyKey, EventQueryOperation.MINUMUM, null);
        executor.submit(query34);
        try {
            List<Document> queryResult = query34.call();
            for (Document doc : queryResult) {
                System.out.println("PREDICTED.MINIMUM: " + doc.toString());
            }
        } catch (Exception e) {
            System.out.println(e.getClass().getName() + ": " + e.getMessage());
        }

        // Default query for anomaly events
        Callable<List<Document>> query41 = new EventReaderMongoSync(MONGODB_URL, MONGODB_DATABASE, EventQueryType.ANOMALY, EventProperties.ANOMALYEVENT_STORAGE_COLLECTION_NAME, NO_QUERY_ANOMALY_STARTTIME, NO_QUERY_ANOMALY_ENDTIME, propertyKey, EventQueryOperation.DEFAULT, null);
        executor.submit(query41);
        try {
            List<Document> queryResult = query41.call();
            int i = 0;
            for (Document doc : queryResult) {
                i++;
                System.out.println("ANOMALY.DEFAULT(" + i + "): " + doc.toString());
            }
        } catch (Exception e) {
            System.out.println(e.getClass().getName() + ": " + e.getMessage());
        }

        // Default query for recommendation events
        Callable<List<Document>> query51 = new EventReaderMongoSync(MONGODB_URL, MONGODB_DATABASE, EventQueryType.RECOMMENDATION, EventProperties.RECOMMENDATIONEVENT_STORAGE_COLLECTION_NAME, NO_QUERY_RECOMMENDATION_STARTTIME, NO_QUERY_RECOMMENDATION_ENDTIME, propertyKey, EventQueryOperation.DEFAULT, null);
        executor.submit(query51);
        try {
            List<Document> queryResult = query51.call();
            int i = 0;
            for (Document doc : queryResult) {
                i++;
                System.out.println("RECOMMENDATION.DEFAULT(" + i + "): " + doc.toString());
            }
        } catch (Exception e) {
            System.out.println(e.getClass().getName() + ": " + e.getMessage());
        }

        // Average query for recommendation events
        Callable<List<Document>> query52 = new EventReaderMongoSync(MONGODB_URL, MONGODB_DATABASE, EventQueryType.RECOMMENDATION, EventProperties.RECOMMENDATIONEVENT_STORAGE_COLLECTION_NAME, NO_QUERY_SIMPLE_STARTTIME, NO_QUERY_SIMPLE_ENDTIME, propertyKey, EventQueryOperation.AVERAGE, null);
        executor.submit(query52);
        try {
            List<Document> queryResult = query52.call();
            for (Document doc : queryResult) {
                System.out.println("RECOMMENDATION.AVERAGE: " + doc.toString());
            }
        } catch (Exception e) {
            System.out.println(e.getClass().getName() + ": " + e.getMessage());
        }

        // Maximum query for derived events
        Callable<List<Document>> query53 = new EventReaderMongoSync(MONGODB_URL, MONGODB_DATABASE, EventQueryType.RECOMMENDATION, EventProperties.RECOMMENDATIONEVENT_STORAGE_COLLECTION_NAME, NO_QUERY_SIMPLE_STARTTIME, NO_QUERY_SIMPLE_ENDTIME, propertyKey, EventQueryOperation.MAXIMUM, null);
        executor.submit(query53);
        try {
            List<Document> queryResult = query53.call();
            for (Document doc : queryResult) {
                System.out.println("RECOMMENDATION.MAXIMUM: " + doc.toString());
            }
        } catch (Exception e) {
            System.out.println(e.getClass().getName() + ": " + e.getMessage());
        }

        // Minimum query for derived events
        Callable<List<Document>> query54 = new EventReaderMongoSync(MONGODB_URL, MONGODB_DATABASE, EventQueryType.RECOMMENDATION, EventProperties.RECOMMENDATIONEVENT_STORAGE_COLLECTION_NAME, NO_QUERY_SIMPLE_STARTTIME, NO_QUERY_SIMPLE_ENDTIME, propertyKey, EventQueryOperation.MINUMUM, null);
        executor.submit(query54);
        try {
            List<Document> queryResult = query54.call();
            for (Document doc : queryResult) {
                System.out.println("RECOMMENDATION.MINIMUM: " + doc.toString());
            }
        } catch (Exception e) {
            System.out.println(e.getClass().getName() + ": " + e.getMessage());
        }

        // Default query for feedback events
        Callable<List<Document>> query61 = new EventReaderMongoSync(MONGODB_URL, MONGODB_DATABASE, EventQueryType.FEEDBACK, EventProperties.FEEDBACKEVENT_STORAGE_COLLECTION_NAME, NO_QUERY_FEEDBACK_STARTTIME, NO_QUERY_FEEDBACK_ENDTIME, propertyKey, EventQueryOperation.DEFAULT, null);
        executor.submit(query61);
        try {
            List<Document> queryResult = query61.call();
            int i = 0;
            for (Document doc : queryResult) {
                i++;
                System.out.println("FEEDBACK.DEFAULT(" + i + "): " + doc.toString());
            }
        } catch (Exception e) {
            System.out.println(e.getClass().getName() + ": " + e.getMessage());
        }

        // Shut down executor
        executor.shutdown();
    }

}
