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
package net.modelbased.sensapp.proasense.storage;

import com.mongodb.async.SingleResultCallback;
import com.mongodb.async.client.MongoClient;
import com.mongodb.async.client.MongoClients;
import com.mongodb.async.client.MongoCollection;
import com.mongodb.async.client.MongoDatabase;

import eu.proasense.internal.SimpleEvent;

import org.apache.thrift.TDeserializer;
import org.apache.thrift.TException;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.bson.Document;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.BlockingQueue;


public class EventWriterMongoAsync implements Runnable {
    private Properties mongoProperties;
    private com.mongodb.MongoClient mongoClient;
    private BlockingQueue<EventDocument> queue;
    private String mongoURL;


    public EventWriterMongoAsync(BlockingQueue<EventDocument> queue, String mongoURL) {
        this.queue = queue;
        this.mongoURL = mongoURL;
    }

    public void run() {
        // Connect to MongoDB database
        MongoClient mongoClient = MongoClients.create(mongoURL);

        MongoDatabase database = mongoClient.getDatabase("proasense_db");

        // Create hash map of collections
        Map<String, MongoCollection<Document>> collectionMap = new HashMap<String, MongoCollection<Document>>();

        int cnt = 0;
        long timer1 = System.currentTimeMillis();
        long timer2 = System.currentTimeMillis();
        try {
            while (true) {
                cnt++;

                EventDocument eventDocument = queue.take();
                String collectionId = eventDocument.getCollectionId();
                Document document = eventDocument.getDocument();

                if (!collectionMap.containsKey(collectionId)) {
                    collectionMap.put(collectionId, database.getCollection(collectionId));
                }

                collectionMap.get(collectionId).insertOne(document, new SingleResultCallback<Void>() {
//                    @Override
                    public void onResult(final Void result, final Throwable t) {
                    }
                });

//                MongoCollection<Document> collection = database.getCollection(eventDocument.getCollectionId());
//                Document doc = eventDocument.getDocument();
//                collection.insertOne(doc);

                if (cnt % 10000 == 0) {
                    timer2 = System.currentTimeMillis();
                    long average = 10000/(timer2 - timer1);
                    System.out.println("Benchmark: ");
                    System.out.println("  Total records written: " + cnt);
                    System.out.println("  Average records/ms: " + average);
//                    System.out.println("Document(" + cnt + "): " + document.toString());
                    timer1 = timer2;
                }
            }
        } catch (InterruptedException e) {
            System.out.println(e.getClass().getName() + ": " + e.getMessage());
        } finally {
        }

        mongoClient.close();
    }

}
