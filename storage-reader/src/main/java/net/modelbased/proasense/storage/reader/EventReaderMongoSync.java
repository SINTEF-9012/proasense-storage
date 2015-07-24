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

import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.Callable;

import static com.mongodb.client.model.Filters.and;
import static com.mongodb.client.model.Filters.gte;
import static com.mongodb.client.model.Filters.lte;


public class EventReaderMongoSync implements Callable {
    private Properties mongoProperties;
    private String mongoURL;
    private String database;
    private EventQueryType queryType;
    private String collectionId;
    private long startTime;
    private long endTime;
    private String propertyKey;
    private EventQueryOperation queryOperation;
    private String mapKey;


    public EventReaderMongoSync(String mongoURL, String database, EventQueryType queryType, String collectionId, long startTime, long endTime, String propertyKey, EventQueryOperation queryOperation, String mapKey) {
        this.mongoURL = mongoURL;
        this.database = database;
        this.queryType = queryType;
        this.collectionId = collectionId;
        this.startTime = startTime;
        this.endTime = endTime;
        this.propertyKey = propertyKey;
        this.queryOperation = queryOperation;
        this.mapKey = mapKey;
    }


    public List<Document> call() {
        // Connect to MongoDB database
        MongoClient mongoClient = new MongoClient(new MongoClientURI(this.mongoURL));
        MongoDatabase database = mongoClient.getDatabase(this.database);

        MongoCollection<Document> collection = database.getCollection(this.collectionId);

        // Create document list for query result
        List<Document> foundDocuments = new ArrayList<Document>();

        if (queryType.equals(EventQueryType.SIMPLE) && queryOperation.equals(EventQueryOperation.DEFAULT)) {
            FindIterable<Document> it = collection.find(and(gte("timestamp", this.startTime), lte("timestamp", this.endTime)));
            MongoCursor<Document> cursor = it.iterator();
            while (cursor.hasNext()) {
                Document doc = cursor.next();
                foundDocuments.add(doc);
            }
        }

        if (queryType.equals(EventQueryType.SIMPLE) && queryOperation.equals(EventQueryOperation.AVERAGE)) {
            long resultAverage = 0;
            long collectionSize = 0;

            FindIterable<Document> it = collection.find(and(gte("timestamp", this.startTime), lte("timestamp", this.endTime)));
            MongoCursor<Document> cursor = it.iterator();
            while (cursor.hasNext()) {
                collectionSize++;
                Document doc = cursor.next();
                Document eventProps = (Document)doc.get("eventProperties");
                Long value = (Long)eventProps.get(this.propertyKey);
                resultAverage = resultAverage + value;
            }
            resultAverage = resultAverage / collectionSize;

            Document resultDoc = new Document("RESULT", resultAverage);
            foundDocuments.add(resultDoc);
        }

        if (queryType.equals(EventQueryType.SIMPLE) && queryOperation.equals(EventQueryOperation.MAXIMUM)) {
            long resultMaximum = Long.MIN_VALUE;

            FindIterable<Document> it = collection.find(and(gte("timestamp", this.startTime), lte("timestamp", this.endTime)));
            MongoCursor<Document> cursor = it.iterator();
            while (cursor.hasNext()) {
                Document doc = cursor.next();
                Document eventProps = (Document)doc.get("eventProperties");
                Long value = (Long)eventProps.get(this.propertyKey);
                if (value > resultMaximum)
                    resultMaximum = value;
            }

            Document resultDoc = new Document("RESULT", resultMaximum);
            foundDocuments.add(resultDoc);
        }

        if (queryType.equals(EventQueryType.SIMPLE) && queryOperation.equals(EventQueryOperation.MINUMUM)) {
            long resultMinimum = Long.MAX_VALUE;

            FindIterable<Document> it = collection.find(and(gte("timestamp", this.startTime), lte("timestamp", this.endTime)));
            MongoCursor<Document> cursor = it.iterator();
            while (cursor.hasNext()) {
                Document doc = cursor.next();
                Document eventProps = (Document)doc.get("eventProperties");
                Long value = (Long)eventProps.get(this.propertyKey);
                if (value < resultMinimum)
                    resultMinimum = value;
            }

            Document resultDoc = new Document("RESULT", resultMinimum);
            foundDocuments.add(resultDoc);
        }

        if (queryType.equals(EventQueryType.DERIVED) && queryOperation.equals(EventQueryOperation.DEFAULT)) {
            FindIterable<Document> it = collection.find(and(gte("timestamp", this.startTime), lte("timestamp", this.endTime)));
            MongoCursor<Document> cursor = it.iterator();
            while (cursor.hasNext()) {
                Document doc = cursor.next();
                foundDocuments.add(doc);
            }
        }

        if (queryType.equals(EventQueryType.DERIVED) && queryOperation.equals(EventQueryOperation.AVERAGE)) {
            double resultAverage = 0;
            long collectionSize = 0;

            FindIterable<Document> it = collection.find(and(gte("timestamp", this.startTime), lte("timestamp", this.endTime)));
            MongoCursor<Document> cursor = it.iterator();
            while (cursor.hasNext()) {
                collectionSize++;
                Document doc = cursor.next();
                Document eventProps = (Document)doc.get("eventProperties");
                Double value = (Double)eventProps.get(this.propertyKey);
                resultAverage = resultAverage + value;
            }
            resultAverage = resultAverage / collectionSize;

            Document resultDoc = new Document("RESULT", resultAverage);
            foundDocuments.add(resultDoc);
        }

        if (queryType.equals(EventQueryType.DERIVED) && queryOperation.equals(EventQueryOperation.MAXIMUM)) {
            double resultMaximum = Double.MIN_VALUE;

            FindIterable<Document> it = collection.find(and(gte("timestamp", this.startTime), lte("timestamp", this.endTime)));
            MongoCursor<Document> cursor = it.iterator();
            while (cursor.hasNext()) {
                Document doc = cursor.next();
                Document eventProps = (Document)doc.get("eventProperties");
                Double value = (Double)eventProps.get(this.propertyKey);
                if (value > resultMaximum)
                    resultMaximum = value;
            }

            Document resultDoc = new Document("RESULT", resultMaximum);
            foundDocuments.add(resultDoc);
        }

        if (queryType.equals(EventQueryType.DERIVED) && queryOperation.equals(EventQueryOperation.MINUMUM)) {
            double resultMinimum = Double.MAX_VALUE;

            FindIterable<Document> it = collection.find(and(gte("timestamp", this.startTime), lte("timestamp", this.endTime)));
            MongoCursor<Document> cursor = it.iterator();
            while (cursor.hasNext()) {
                Document doc = cursor.next();
                Document eventProps = (Document)doc.get("eventProperties");
                Double value = (Double)eventProps.get(this.propertyKey);
                if (value < resultMinimum)
                    resultMinimum = value;
            }

            Document resultDoc = new Document("RESULT", resultMinimum);
            foundDocuments.add(resultDoc);
        }

        if (queryType.equals(EventQueryType.PREDICTED) && queryOperation.equals(EventQueryOperation.DEFAULT)) {
            FindIterable<Document> it = collection.find(and(gte("timestamp", this.startTime), lte("timestamp", this.endTime)));
            MongoCursor<Document> cursor = it.iterator();
            while (cursor.hasNext()) {
                Document doc = cursor.next();
                foundDocuments.add(doc);
            }
        }

        if (queryType.equals(EventQueryType.PREDICTED) && queryOperation.equals(EventQueryOperation.AVERAGE)) {
            long resultAverage = 0;
            long collectionSize = 0;

            FindIterable<Document> it = collection.find(and(gte("timestamp", this.startTime), lte("timestamp", this.endTime)));
            MongoCursor<Document> cursor = it.iterator();
            while (cursor.hasNext()) {
                collectionSize++;
                Document doc = cursor.next();
                Document eventProps = (Document)doc.get("eventProperties");
                Long value = (Long)eventProps.get(this.propertyKey);
                resultAverage = resultAverage + value;
            }
            resultAverage = resultAverage / collectionSize;

            Document resultDoc = new Document("RESULT", resultAverage);
            foundDocuments.add(resultDoc);
        }

        if (queryType.equals(EventQueryType.PREDICTED) && queryOperation.equals(EventQueryOperation.MAXIMUM)) {
            long resultMaximum = Long.MIN_VALUE;

            FindIterable<Document> it = collection.find(and(gte("timestamp", this.startTime), lte("timestamp", this.endTime)));
            MongoCursor<Document> cursor = it.iterator();
            while (cursor.hasNext()) {
                Document doc = cursor.next();
                Document eventProps = (Document)doc.get("eventProperties");
                Long value = (Long)eventProps.get(this.propertyKey);
                if (value > resultMaximum)
                    resultMaximum = value;
            }

            Document resultDoc = new Document("RESULT", resultMaximum);
            foundDocuments.add(resultDoc);
        }

        if (queryType.equals(EventQueryType.PREDICTED) && queryOperation.equals(EventQueryOperation.MINUMUM)) {
            long resultMinimum = Long.MAX_VALUE;

            FindIterable<Document> it = collection.find(and(gte("timestamp", this.startTime), lte("timestamp", this.endTime)));
            MongoCursor<Document> cursor = it.iterator();
            while (cursor.hasNext()) {
                Document doc = cursor.next();
                Document eventProps = (Document)doc.get("eventProperties");
                Long value = (Long)eventProps.get(this.propertyKey);
                if (value < resultMinimum)
                    resultMinimum = value;
            }

            Document resultDoc = new Document("RESULT", resultMinimum);
            foundDocuments.add(resultDoc);
        }

        if (queryType.equals(EventQueryType.ANOMALY) && queryOperation.equals(EventQueryOperation.DEFAULT)) {
            FindIterable<Document> it = collection.find(and(gte("timestamp", this.startTime), lte("timestamp", this.endTime)));
            MongoCursor<Document> cursor = it.iterator();
            while (cursor.hasNext()) {
                Document doc = cursor.next();
                foundDocuments.add(doc);
            }
        }

        if (queryType.equals(EventQueryType.RECOMMENDATION) && queryOperation.equals(EventQueryOperation.DEFAULT)) {
            FindIterable<Document> it = collection.find(and(gte("timestamp", this.startTime), lte("timestamp", this.endTime)));
            MongoCursor<Document> cursor = it.iterator();
            while (cursor.hasNext()) {
                Document doc = cursor.next();
                foundDocuments.add(doc);
            }
        }

        if (queryType.equals(EventQueryType.RECOMMENDATION) && queryOperation.equals(EventQueryOperation.AVERAGE)) {
            long resultAverage = 0;
            long collectionSize = 0;

            FindIterable<Document> it = collection.find(and(gte("timestamp", this.startTime), lte("timestamp", this.endTime)));
            MongoCursor<Document> cursor = it.iterator();
            while (cursor.hasNext()) {
                collectionSize++;
                Document doc = cursor.next();
                Document eventProps = (Document)doc.get("eventProperties");
                Long value = (Long)eventProps.get(this.propertyKey);
                resultAverage = resultAverage + value;
            }
            resultAverage = resultAverage / collectionSize;

            Document resultDoc = new Document("RESULT", resultAverage);
            foundDocuments.add(resultDoc);
        }

        if (queryType.equals(EventQueryType.RECOMMENDATION) && queryOperation.equals(EventQueryOperation.MAXIMUM)) {
            long resultMaximum = Long.MIN_VALUE;

            FindIterable<Document> it = collection.find(and(gte("timestamp", this.startTime), lte("timestamp", this.endTime)));
            MongoCursor<Document> cursor = it.iterator();
            while (cursor.hasNext()) {
                Document doc = cursor.next();
                Document eventProps = (Document)doc.get("eventProperties");
                Long value = (Long)eventProps.get(this.propertyKey);
                if (value > resultMaximum)
                    resultMaximum = value;
            }

            Document resultDoc = new Document("RESULT", resultMaximum);
            foundDocuments.add(resultDoc);
        }

        if (queryType.equals(EventQueryType.RECOMMENDATION) && queryOperation.equals(EventQueryOperation.MINUMUM)) {
            long resultMinimum = Long.MAX_VALUE;

            FindIterable<Document> it = collection.find(and(gte("timestamp", this.startTime), lte("timestamp", this.endTime)));
            MongoCursor<Document> cursor = it.iterator();
            while (cursor.hasNext()) {
                Document doc = cursor.next();
                Document eventProps = (Document)doc.get("eventProperties");
                Long value = (Long)eventProps.get(this.propertyKey);
                if (value < resultMinimum)
                    resultMinimum = value;
            }

            Document resultDoc = new Document("RESULT", resultMinimum);
            foundDocuments.add(resultDoc);
        }

        if (queryType.equals(EventQueryType.FEEDBACK) && queryOperation.equals(EventQueryOperation.DEFAULT)) {
            FindIterable<Document> it = collection.find(and(gte("timestamp", this.startTime), lte("timestamp", this.endTime)));
            MongoCursor<Document> cursor = it.iterator();
            while (cursor.hasNext()) {
                Document doc = cursor.next();
                foundDocuments.add(doc);
            }
        }

        mongoClient.close();

        return foundDocuments;
    }

}
