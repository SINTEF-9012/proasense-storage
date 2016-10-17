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
import static com.mongodb.client.model.Filters.in;
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
            FindIterable<Document> it = collection.find(and(gte("timestamp", this.startTime), lte("timestamp", this.endTime)));

            boolean longProperty = false;
            boolean doubleProperty = false;
            MongoCursor<Document> firstCursor = it.iterator();
            if (firstCursor.hasNext()) {
                Document doc = firstCursor.next();
                Document eventProps = (Document)doc.get("eventProperties");
                Object valueObj = eventProps.get(this.propertyKey);
                if (valueObj instanceof Long)
                    longProperty = true;
                else if (valueObj instanceof Double)
                    doubleProperty = true;
            }

            long resultAverageLong = 0;
            double resultAverageDouble = 0;
            long collectionSize = 0;
            MongoCursor<Document> cursor = it.iterator();
            while (cursor.hasNext()) {
                collectionSize++;
                Document doc = cursor.next();
                Document eventProps = (Document)doc.get("eventProperties");
                if (longProperty) {
                    Long value = (Long)eventProps.get(this.propertyKey);
                    resultAverageLong = resultAverageLong + value;
                }
                else if (doubleProperty) {
                    Double value = (Double)eventProps.get(this.propertyKey);
                    resultAverageDouble = resultAverageDouble + value;
                    doubleProperty = true;
                }
            }

            if (longProperty) {
                Long resultAverage = resultAverageLong / collectionSize;
                Document resultDoc = new Document("RESULT", resultAverage);
                foundDocuments.add(resultDoc);
            }
            else if (doubleProperty) {
                Double resultAverage = resultAverageDouble / collectionSize;
                Document resultDoc = new Document("RESULT", resultAverage);
                foundDocuments.add(resultDoc);
            }
        }

        if (queryType.equals(EventQueryType.SIMPLE) && queryOperation.equals(EventQueryOperation.MAXIMUM)) {
            FindIterable<Document> it = collection.find(and(gte("timestamp", this.startTime), lte("timestamp", this.endTime)));

            boolean longProperty = false;
            boolean doubleProperty = false;
            MongoCursor<Document> firstCursor = it.iterator();
            if (firstCursor.hasNext()) {
                Document doc = firstCursor.next();
                Document eventProps = (Document)doc.get("eventProperties");
                Object valueObj = eventProps.get(this.propertyKey);
                if (valueObj instanceof Long)
                    longProperty = true;
                else if (valueObj instanceof Double)
                    doubleProperty = true;
            }

            long resultMaximumLong = Long.MIN_VALUE;
            double resultMaximumDouble = Double.MIN_VALUE;
            MongoCursor<Document> cursor = it.iterator();
            while (cursor.hasNext()) {
                Document doc = cursor.next();
                Document eventProps = (Document)doc.get("eventProperties");
                if (longProperty) {
                    long value = (Long)eventProps.get(this.propertyKey);
                    if (value > resultMaximumLong)
                        resultMaximumLong = value;
                }
                else if (doubleProperty) {
                    double value = (Double)eventProps.get(this.propertyKey);
                    if (value > resultMaximumDouble)
                        resultMaximumDouble = value;
                }
            }

            if (longProperty) {
                Long resultMaximum = resultMaximumLong;
                Document resultDoc = new Document("RESULT", resultMaximum);
                foundDocuments.add(resultDoc);
            }
            else if (doubleProperty) {
                Double resultMaximum = resultMaximumDouble;
                Document resultDoc = new Document("RESULT", resultMaximum);
                foundDocuments.add(resultDoc);
            }
        }

        if (queryType.equals(EventQueryType.SIMPLE) && queryOperation.equals(EventQueryOperation.MINUMUM)) {
            FindIterable<Document> it = collection.find(and(gte("timestamp", this.startTime), lte("timestamp", this.endTime)));

            boolean longProperty = false;
            boolean doubleProperty = false;
            MongoCursor<Document> firstCursor = it.iterator();
            if (firstCursor.hasNext()) {
                Document doc = firstCursor.next();
                Document eventProps = (Document)doc.get("eventProperties");
                Object valueObj = eventProps.get(this.propertyKey);
                if (valueObj instanceof Long)
                    longProperty = true;
                else if (valueObj instanceof Double)
                    doubleProperty = true;
            }

            long resultMinimumLong = Long.MAX_VALUE;
            double resultMinimumDouble = Double.MAX_VALUE;
            MongoCursor<Document> cursor = it.iterator();
            while (cursor.hasNext()) {
                Document doc = cursor.next();
                Document eventProps = (Document)doc.get("eventProperties");
                if (longProperty) {
                    long value = (Long)eventProps.get(this.propertyKey);
                    if (value < resultMinimumLong)
                        resultMinimumLong = value;
                }
                else if (doubleProperty) {
                    Double value = (Double)eventProps.get(this.propertyKey);
                    if (value < resultMinimumDouble)
                        resultMinimumDouble = value;
                }
            }

            if (longProperty) {
                long resultMinimum = resultMinimumLong;
                Document resultDoc = new Document("RESULT", resultMinimum);
                foundDocuments.add(resultDoc);
            }
            else if (doubleProperty) {
                double resultMinimum = resultMinimumDouble;
                Document resultDoc = new Document("RESULT", resultMinimum);
                foundDocuments.add(resultDoc);
            }
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
            FindIterable<Document> it = collection.find(and(gte("timestamp", this.startTime), lte("timestamp", this.endTime)));

            boolean longProperty = false;
            boolean doubleProperty = false;
            MongoCursor<Document> firstCursor = it.iterator();
            if (firstCursor.hasNext()) {
                Document doc = firstCursor.next();
                Document eventProps = (Document)doc.get("eventProperties");
                Object valueObj = eventProps.get(this.propertyKey);
                if (valueObj instanceof Long)
                    longProperty = true;
                else if (valueObj instanceof Double)
                    doubleProperty = true;
            }

            long resultAverageLong = 0;
            double resultAverageDouble = 0;
            long collectionSize = 0;
            MongoCursor<Document> cursor = it.iterator();
            while (cursor.hasNext()) {
                collectionSize++;
                Document doc = cursor.next();
                Document eventProps = (Document)doc.get("eventProperties");
                if (longProperty) {
                    Long value = (Long)eventProps.get(this.propertyKey);
                    resultAverageLong = resultAverageLong + value;
                }
                else if (doubleProperty) {
                    Double value = (Double)eventProps.get(this.propertyKey);
                    resultAverageDouble = resultAverageDouble + value;
                }
            }

            if (longProperty) {
                Long resultAverage = resultAverageLong / collectionSize;
                Document resultDoc = new Document("RESULT", resultAverage);
                foundDocuments.add(resultDoc);
            }
            else if (doubleProperty) {
                Double resultAverage = resultAverageDouble / collectionSize;
                Document resultDoc = new Document("RESULT", resultAverage);
                foundDocuments.add(resultDoc);
            }
        }

        if (queryType.equals(EventQueryType.DERIVED) && queryOperation.equals(EventQueryOperation.MAXIMUM)) {
            FindIterable<Document> it = collection.find(and(gte("timestamp", this.startTime), lte("timestamp", this.endTime)));

            boolean longProperty = false;
            boolean doubleProperty = false;
            MongoCursor<Document> firstCursor = it.iterator();
            if (firstCursor.hasNext()) {
                Document doc = firstCursor.next();
                Document eventProps = (Document)doc.get("eventProperties");
                Object valueObj = eventProps.get(this.propertyKey);
                if (valueObj instanceof Long)
                    longProperty = true;
                else if (valueObj instanceof Double)
                    doubleProperty = true;
            }

            long resultMaximumLong = Long.MIN_VALUE;
            double resultMaximumDouble = Double.MIN_VALUE;
            MongoCursor<Document> cursor = it.iterator();
            while (cursor.hasNext()) {
                Document doc = cursor.next();
                Document eventProps = (Document)doc.get("eventProperties");
                if (longProperty) {
                    long value = (Long)eventProps.get(this.propertyKey);
                    if (value > resultMaximumLong)
                        resultMaximumLong = value;
                }
                else if (doubleProperty) {
                    double value = (Double)eventProps.get(this.propertyKey);
                    if (value > resultMaximumDouble)
                        resultMaximumDouble = value;
                    doubleProperty = true;
                }
            }

            if (longProperty) {
                long resultMaximum = resultMaximumLong;
                Document resultDoc = new Document("RESULT", resultMaximum);
                foundDocuments.add(resultDoc);
            }
            else if (doubleProperty) {
                double resultMaximum = resultMaximumDouble;
                Document resultDoc = new Document("RESULT", resultMaximum);
                foundDocuments.add(resultDoc);
            }
        }

        if (queryType.equals(EventQueryType.DERIVED) && queryOperation.equals(EventQueryOperation.MINUMUM)) {
            FindIterable<Document> it = collection.find(and(gte("timestamp", this.startTime), lte("timestamp", this.endTime)));

            boolean longProperty = false;
            boolean doubleProperty = false;
            MongoCursor<Document> firstCursor = it.iterator();
            if (firstCursor.hasNext()) {
                Document doc = firstCursor.next();
                Document eventProps = (Document)doc.get("eventProperties");
                Object valueObj = eventProps.get(this.propertyKey);
                if (valueObj instanceof Long)
                    longProperty = true;
                else if (valueObj instanceof Double)
                    doubleProperty = true;
            }

            long resultMinimumLong = Long.MAX_VALUE;
            double resultMinimumDouble = Double.MAX_VALUE;
            MongoCursor<Document> cursor = it.iterator();
            while (cursor.hasNext()) {
                Document doc = cursor.next();
                Document eventProps = (Document)doc.get("eventProperties");
                if (longProperty) {
                    long value = (Long)eventProps.get(this.propertyKey);
                    if (value < resultMinimumLong)
                        resultMinimumLong = value;
                }
                else if (doubleProperty) {
                    double value = (Double)eventProps.get(this.propertyKey);
                    if (value < resultMinimumDouble)
                        resultMinimumDouble = value;
                }
            }

            if (longProperty) {
                long resultMinimum = resultMinimumLong;
                Document resultDoc = new Document("RESULT", resultMinimum);
                foundDocuments.add(resultDoc);
            }
            else if (doubleProperty) {
                double resultMinimum = resultMinimumDouble;
                Document resultDoc = new Document("RESULT", resultMinimum);
                foundDocuments.add(resultDoc);
            }
        }

        if (queryType.equals(EventQueryType.KPI) && queryOperation.equals(EventQueryOperation.DEFAULT)) {
            FindIterable<Document> it = collection.find(and(gte("timestamp", this.startTime), lte("timestamp", this.endTime), in("eventName", this.propertyKey)));

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

        if (queryType.equals(EventQueryType.PREDICTED) && queryOperation.equals(EventQueryOperation.AVERAGE)) {
            FindIterable<Document> it = collection.find(and(gte("timestamp", this.startTime), lte("timestamp", this.endTime)));

            boolean longProperty = false;
            boolean doubleProperty = false;
            MongoCursor<Document> firstCursor = it.iterator();
            if (firstCursor.hasNext()) {
                Document doc = firstCursor.next();
                Document eventProps = (Document)doc.get("eventProperties");
                Object valueObj = eventProps.get(this.propertyKey);
                if (valueObj instanceof Long)
                    longProperty = true;
                else if (valueObj instanceof Double)
                    doubleProperty = true;
            }

            long resultAverageLong = 0;
            double resultAverageDouble = 0;
            long collectionSize = 0;
            MongoCursor<Document> cursor = it.iterator();
            while (cursor.hasNext()) {
                collectionSize++;
                Document doc = cursor.next();
                Document eventProps = (Document)doc.get("eventProperties");
                if (longProperty) {
                    long value = (Long)eventProps.get(this.propertyKey);
                    resultAverageLong = resultAverageLong + value;
                }
                else if (doubleProperty) {
                    double value = (Double)eventProps.get(this.propertyKey);
                    resultAverageDouble = resultAverageDouble + value;
                }
            }

            if (longProperty) {
                long resultAverage = resultAverageLong / collectionSize;
                Document resultDoc = new Document("RESULT", resultAverage);
                foundDocuments.add(resultDoc);
            }
            else if (doubleProperty) {
                double resultAverage = resultAverageDouble / collectionSize;
                Document resultDoc = new Document("RESULT", resultAverage);
                foundDocuments.add(resultDoc);
            }
        }

        if (queryType.equals(EventQueryType.PREDICTED) && queryOperation.equals(EventQueryOperation.MAXIMUM)) {
            FindIterable<Document> it = collection.find(and(gte("timestamp", this.startTime), lte("timestamp", this.endTime)));

            boolean longProperty = false;
            boolean doubleProperty = false;
            MongoCursor<Document> firstCursor = it.iterator();
            if (firstCursor.hasNext()) {
                Document doc = firstCursor.next();
                Document eventProps = (Document)doc.get("eventProperties");
                Object valueObj = eventProps.get(this.propertyKey);
                if (valueObj instanceof Long)
                    longProperty = true;
                else if (valueObj instanceof Double)
                    doubleProperty = true;
            }

            long resultMaximumLong = Long.MIN_VALUE;
            double resultMaximumDouble = Double.MIN_VALUE;
            MongoCursor<Document> cursor = it.iterator();
            while (cursor.hasNext()) {
                Document doc = cursor.next();
                Document eventProps = (Document)doc.get("eventProperties");
                if (longProperty) {
                    long value = (Long)eventProps.get(this.propertyKey);
                    if (value > resultMaximumLong)
                        resultMaximumLong = value;
                }
                else if (doubleProperty) {
                    double value = (Double)eventProps.get(this.propertyKey);
                    if (value > resultMaximumDouble)
                        resultMaximumDouble = value;
                }
            }

            if (longProperty) {
                long resultMaximum = resultMaximumLong;
                Document resultDoc = new Document("RESULT", resultMaximum);
                foundDocuments.add(resultDoc);
            }
            else if (doubleProperty) {
                double resultMaximum = resultMaximumDouble;
                Document resultDoc = new Document("RESULT", resultMaximum);
                foundDocuments.add(resultDoc);
            }
        }

        if (queryType.equals(EventQueryType.PREDICTED) && queryOperation.equals(EventQueryOperation.MINUMUM)) {
            FindIterable<Document> it = collection.find(and(gte("timestamp", this.startTime), lte("timestamp", this.endTime)));

            boolean longProperty = false;
            boolean doubleProperty = false;
            MongoCursor<Document> firstCursor = it.iterator();
            if (firstCursor.hasNext()) {
                Document doc = firstCursor.next();
                Document eventProps = (Document)doc.get("eventProperties");
                Object valueObj = eventProps.get(this.propertyKey);
                if (valueObj instanceof Long)
                    longProperty = true;
                else if (valueObj instanceof Double)
                    doubleProperty = true;
            }

            long resultMinimumLong = Long.MAX_VALUE;
            double resultMinimumDouble = Double.MAX_VALUE;
            MongoCursor<Document> cursor = it.iterator();
            while (cursor.hasNext()) {
                Document doc = cursor.next();
                Document eventProps = (Document)doc.get("eventProperties");
                if (longProperty) {
                    long value = (Long)eventProps.get(this.propertyKey);
                    if (value < resultMinimumLong)
                        resultMinimumLong = value;
                }
                else if (doubleProperty) {
                    double value = (Long)eventProps.get(this.propertyKey);
                    if (value < resultMinimumDouble)
                        resultMinimumDouble = value;
                }
            }

            if (longProperty) {
                long resultMinimum = resultMinimumLong;
                Document resultDoc = new Document("RESULT", resultMinimum);
                foundDocuments.add(resultDoc);
            }
            else if (doubleProperty) {
                double resultMinimum = resultMinimumDouble;
                Document resultDoc = new Document("RESULT", resultMinimum);
                foundDocuments.add(resultDoc);
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

        if (queryType.equals(EventQueryType.RECOMMENDATION) && queryOperation.equals(EventQueryOperation.AVERAGE)) {
            FindIterable<Document> it = collection.find(and(gte("timestamp", this.startTime), lte("timestamp", this.endTime)));

            boolean longProperty = false;
            boolean doubleProperty = false;
            MongoCursor<Document> firstCursor = it.iterator();
            if (firstCursor.hasNext()) {
                Document doc = firstCursor.next();
                Document eventProps = (Document)doc.get("eventProperties");
                Object valueObj = eventProps.get(this.propertyKey);
                if (valueObj instanceof Long)
                    longProperty = true;
                else if (valueObj instanceof Double)
                    doubleProperty = true;
            }

            long resultAverageLong = 0;
            double resultAverageDouble = 0;
            long collectionSize = 0;
            MongoCursor<Document> cursor = it.iterator();
            while (cursor.hasNext()) {
                collectionSize++;
                Document doc = cursor.next();
                Document eventProps = (Document)doc.get("eventProperties");
                if (longProperty) {
                    long value = (Long)eventProps.get(this.propertyKey);
                    resultAverageLong = resultAverageLong + value;
                }
                else if (doubleProperty) {
                    double value = (Double)eventProps.get(this.propertyKey);
                    resultAverageDouble = resultAverageDouble + value;
                }
            }

            if (longProperty) {
                long resultAverage = resultAverageLong / collectionSize;
                Document resultDoc = new Document("RESULT", resultAverage);
                foundDocuments.add(resultDoc);
            }
            else if (doubleProperty) {
                double resultAverage = resultAverageDouble / collectionSize;
                Document resultDoc = new Document("RESULT", resultAverage);
                foundDocuments.add(resultDoc);
            }
        }

        if (queryType.equals(EventQueryType.RECOMMENDATION) && queryOperation.equals(EventQueryOperation.MAXIMUM)) {
            FindIterable<Document> it = collection.find(and(gte("timestamp", this.startTime), lte("timestamp", this.endTime)));

            boolean longProperty = false;
            boolean doubleProperty = false;
            MongoCursor<Document> firstCursor = it.iterator();
            if (firstCursor.hasNext()) {
                Document doc = firstCursor.next();
                Document eventProps = (Document)doc.get("eventProperties");
                Object valueObj = eventProps.get(this.propertyKey);
                if (valueObj instanceof Long)
                    longProperty = true;
                else if (valueObj instanceof Double)
                    doubleProperty = true;
            }

            long resultMaximumLong = Long.MIN_VALUE;
            double resultMaximumDouble = Double.MIN_VALUE;
            MongoCursor<Document> cursor = it.iterator();
            while (cursor.hasNext()) {
                Document doc = cursor.next();
                Document eventProps = (Document)doc.get("eventProperties");
                if (longProperty) {
                    long value = (Long)eventProps.get(this.propertyKey);
                    if (value > resultMaximumLong)
                        resultMaximumLong = value;
                }
                else if (doubleProperty) {
                    double value = (Double)eventProps.get(this.propertyKey);
                    if (value > resultMaximumDouble)
                        resultMaximumDouble = value;
                }
            }

            if (longProperty) {
                long resultMaximum = resultMaximumLong;
                Document resultDoc = new Document("RESULT", resultMaximum);
                foundDocuments.add(resultDoc);
            }
            else if (doubleProperty) {
                double resultMaximum = resultMaximumDouble;
                Document resultDoc = new Document("RESULT", resultMaximum);
                foundDocuments.add(resultDoc);
            }
        }

        if (queryType.equals(EventQueryType.RECOMMENDATION) && queryOperation.equals(EventQueryOperation.MINUMUM)) {
            FindIterable<Document> it = collection.find(and(gte("timestamp", this.startTime), lte("timestamp", this.endTime)));

            boolean longProperty = false;
            boolean doubleProperty = false;
            MongoCursor<Document> firstCursor = it.iterator();
            if (firstCursor.hasNext()) {
                Document doc = firstCursor.next();
                Document eventProps = (Document)doc.get("eventProperties");
                Object valueObj = eventProps.get(this.propertyKey);
                if (valueObj instanceof Long)
                    longProperty = true;
                else if (valueObj instanceof Double)
                    doubleProperty = true;
            }

            long resultMinimumLong = Long.MAX_VALUE;
            double resultMinimumDouble = Double.MAX_VALUE;
            MongoCursor<Document> cursor = it.iterator();
            while (cursor.hasNext()) {
                Document doc = cursor.next();
                Document eventProps = (Document)doc.get("eventProperties");
                if (longProperty) {
                    long value = (Long)eventProps.get(this.propertyKey);
                    if (value < resultMinimumLong)
                        resultMinimumLong = value;
                }
                else if (doubleProperty) {
                    double value = (Double)eventProps.get(this.propertyKey);
                    if (value < resultMinimumDouble)
                        resultMinimumDouble = value;
                }
            }

            if (longProperty) {
                long resultMinimum = resultMinimumLong;
                Document resultDoc = new Document("RESULT", resultMinimum);
                foundDocuments.add(resultDoc);
            }
            else if (doubleProperty) {
                double resultMinimum = resultMinimumDouble;
                Document resultDoc = new Document("RESULT", resultMinimum);
                foundDocuments.add(resultDoc);
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
