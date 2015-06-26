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
package net.modelbased.proasense.storage;

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
    private EventQueryType queryType;
    private String collectionId;
    private long startTime;
    private long endTime;
    private EventQueryOperation queryOperation;
    private String mapKey;


    public EventReaderMongoSync(String mongoURL, EventQueryType queryType, String collectionId, long startTime, long endTime, EventQueryOperation queryOperation, String mapKey) {
        this.mongoURL = mongoURL;
        this.queryType = queryType;
        this.collectionId = collectionId;
        this.startTime = startTime;
        this.endTime = endTime;
        this.queryOperation = queryOperation;
        this.mapKey = mapKey;
    }


    public List<Document> call() {
        // Connect to MongoDB database
        MongoClient mongoClient = new MongoClient(new MongoClientURI(this.mongoURL));
        MongoDatabase database = mongoClient.getDatabase(EventProperties.STORAGE_DATABASE_NAME);

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
                Long rawValue = (Long)eventProps.get("raw_value");
                resultAverage = resultAverage + rawValue;
            }
            resultAverage = resultAverage / collectionSize;

            Document resultDoc = new Document("RESULT", resultAverage);
            foundDocuments.add(resultDoc);
        }

        if (queryType.equals(EventQueryType.SIMPLE) && queryOperation.equals(EventQueryOperation.MAXIMUM)) {
            long resultMaximum = 0;

            FindIterable<Document> it = collection.find(and(gte("timestamp", this.startTime), lte("timestamp", this.endTime)));
            MongoCursor<Document> cursor = it.iterator();
            while (cursor.hasNext()) {
                Document doc = cursor.next();
                Document eventProps = (Document)doc.get("eventProperties");
                Long rawValue = (Long)eventProps.get("raw_value");
                if (rawValue > resultMaximum)
                    resultMaximum = rawValue;
            }

            Document resultDoc = new Document("RESULT", resultMaximum);
            foundDocuments.add(resultDoc);
        }

        if (queryType.equals(EventQueryType.SIMPLE) && queryOperation.equals(EventQueryOperation.MINUMUM)) {
            long resultMinimum = 0;

            FindIterable<Document> it = collection.find(and(gte("timestamp", this.startTime), lte("timestamp", this.endTime)));
            MongoCursor<Document> cursor = it.iterator();
            while (cursor.hasNext()) {
                Document doc = cursor.next();
                Document eventProps = (Document)doc.get("eventProperties");
                Long rawValue = (Long)eventProps.get("raw_value");
                if (rawValue < resultMinimum)
                    resultMinimum = rawValue;
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

        if (queryType.equals(EventQueryType.PREDICTED) && queryOperation.equals(EventQueryOperation.DEFAULT)) {
            FindIterable<Document> it = collection.find(and(gte("timestamp", this.startTime), lte("timestamp", this.endTime)));
            MongoCursor<Document> cursor = it.iterator();
            while (cursor.hasNext()) {
                Document doc = cursor.next();
                foundDocuments.add(doc);
            }
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
