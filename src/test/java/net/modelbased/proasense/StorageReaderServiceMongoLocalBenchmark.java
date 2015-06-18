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

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public class StorageReaderServiceMongoLocalBenchmark {

    public StorageReaderServiceMongoLocalBenchmark() {
    }


    public static void main(String[] args) {
        // Kafka properties
//        String zooKeeper = "89.216.116.44:2181";
        String zooKeeper = "192.168.11.20:2181";
        String groupId = "SimpleEventLocalMongoBenchmark";

        // MongoDB properties
        String mongoURL = "mongodb://127.0.0.1:27017";
//        String mongoURL = "mongodb://89.216.116.44:27017";
//        String mongoURL = "mongodb://192.168.11.25:27017";

        int NO_SIMPLEEVENTS_THREADS = 1;
        int NO_DERIVEDEVENTS_THREADS = 0;
        int NO_PREDICTEDEVENTS_THREADS = 0;
        int NO_ANOMALYEVENTS_THREADS = 0;
        int NO_RECOMMENDATIONEVENTS_THREADS = 0;
        int NO_FEEDBACKEVENTS_THREADS = 0;

        int NO_TOTAL_THREADS = NO_SIMPLEEVENTS_THREADS + NO_DERIVEDEVENTS_THREADS + NO_PREDICTEDEVENTS_THREADS + NO_ANOMALYEVENTS_THREADS + NO_RECOMMENDATIONEVENTS_THREADS;

        // Create executor environment for threads
        ArrayList<Callable> workers = new ArrayList<Callable>(NO_TOTAL_THREADS);
        ExecutorService executor = Executors.newFixedThreadPool(NO_TOTAL_THREADS);

        long NO_QUERY_SIMPLE_STARTTIME = 1432798130753L;
        long NO_QUERY_SIMPLE_ENDTIME = 1432798131956L;
        long NO_QUERY_DERIVED_STARTTIME = 1432807975740L;
        long NO_QUERY_DERIVED_ENDTIME = 1432807977094L;
        long NO_QUERY_PREDICTED_STARTTIME = 1432807976707L;
        long NO_QUERY_PREDICTED_ENDTIME = 1432808027877L;
        long NO_QUERY_ANOMALY_STARTTIME = 1432807976603L;
        long NO_QUERY_ANOMALY_ENDTIME = 1432798131956L;
        long NO_QUERY_RECOMMENDATION_STARTTIME = 1432807976678L;
        long NO_QUERY_RECOMMENDATION_ENDTIME = 1432808028864L;
        long NO_QUERY_FEEDBACK_STARTTIME = 1432807976678L;
        long NO_QUERY_FEEDBACK_ENDTIME = 1432808028864L;

        // Default query for simple events
        Callable<List<Document>> query01 = new EventReaderMongoSync(mongoURL, EventQueryType.SIMPLE, "simpleevent.mhwirth.0", NO_QUERY_SIMPLE_STARTTIME, NO_QUERY_SIMPLE_ENDTIME, EventQueryOperation.DEFAULT, null);
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
        Callable<List<Document>> query02 = new EventReaderMongoSync(mongoURL, EventQueryType.SIMPLE, "simpleevent.mhwirth.0", NO_QUERY_SIMPLE_STARTTIME, NO_QUERY_SIMPLE_ENDTIME, EventQueryOperation.AVERAGE, null);
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
        Callable<List<Document>> query03 = new EventReaderMongoSync(mongoURL, EventQueryType.SIMPLE, "simpleevent.mhwirth.0", NO_QUERY_SIMPLE_STARTTIME, NO_QUERY_SIMPLE_ENDTIME, EventQueryOperation.MAXIMUM, null);
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
        Callable<List<Document>> query04 = new EventReaderMongoSync(mongoURL, EventQueryType.SIMPLE, "simpleevent.mhwirth.0", NO_QUERY_SIMPLE_STARTTIME, NO_QUERY_SIMPLE_ENDTIME, EventQueryOperation.MINUMUM, null);
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
        Callable<List<Document>> query05 = new EventReaderMongoSync(mongoURL, EventQueryType.DERIVED, "derivedevent.mhwirth.0", NO_QUERY_DERIVED_STARTTIME, NO_QUERY_DERIVED_ENDTIME, EventQueryOperation.DEFAULT, null);
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
        Callable<List<Document>> query06 = new EventReaderMongoSync(mongoURL, EventQueryType.PREDICTED, EventProperties.PREDICTEDEVENT_STORAGE_COLLECTION_NAME, NO_QUERY_PREDICTED_STARTTIME, NO_QUERY_PREDICTED_ENDTIME, EventQueryOperation.DEFAULT, null);
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
        Callable<List<Document>> query07 = new EventReaderMongoSync(mongoURL, EventQueryType.ANOMALY, EventProperties.ANOMALYEVENT_STORAGE_COLLECTION_NAME, NO_QUERY_ANOMALY_STARTTIME, NO_QUERY_ANOMALY_ENDTIME, EventQueryOperation.DEFAULT, null);
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
        Callable<List<Document>> query08 = new EventReaderMongoSync(mongoURL, EventQueryType.RECOMMENDATION, EventProperties.RECOMMENDATIONEVENT_STORAGE_COLLECTION_NAME, NO_QUERY_RECOMMENDATION_STARTTIME, NO_QUERY_RECOMMENDATION_ENDTIME, EventQueryOperation.DEFAULT, null);
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
        Callable<List<Document>> query09 = new EventReaderMongoSync(mongoURL, EventQueryType.FEEDBACK, EventProperties.FEEDBACKEVENT_STORAGE_COLLECTION_NAME, NO_QUERY_FEEDBACK_STARTTIME, NO_QUERY_FEEDBACK_ENDTIME, EventQueryOperation.DEFAULT, null);
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
