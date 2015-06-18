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
package net.modelbased.proasense.storage;

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

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;


@Path("/StorageReaderServiceMongoSync")
public class StorageReaderServiceMongoServer {
    private Properties serverProperties;


    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes({MediaType.TEXT_PLAIN, MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public Response StorageReaderServiceMongoSync(String text) {
        // Get server properties
        serverProperties = loadServerProperties();

        // Kafka broker configuration properties
        String zooKeeper = serverProperties.getProperty("zookeeper.connect");
        String groupId = "StorageReaderServiceMongoServer";

        // SensApp registry configuration properties
        String sensappURL = serverProperties.getProperty("proasense.storage.sensapp.url");

        // MongoDB event writers configuration properties
        String MONGODB_URL = serverProperties.getProperty("proasense.storage.mongodb.url");

        // Process GET request
        String collectionId = "proasense.simpleevent.mhwirth.0";
        EventQueryType queryType = EventQueryType.SIMPLE;
        long startTime = 0L;
        long endTime = 0L;
        EventQueryOperation queryOperation = EventQueryOperation.DEFAULT;

        // Create executor environment for threads
        ArrayList<Callable> workers = new ArrayList<Callable>(1);
        ExecutorService executor = Executors.newFixedThreadPool(1);
        Callable<List<Document>> query = new EventReaderMongoSync(MONGODB_URL, queryType, collectionId, startTime, endTime, queryOperation, null);
        executor.submit(query);

        List<Document> queryResult = new ArrayList<Document>();
        try {
            queryResult = query.call();
        } catch (Exception e) {
            System.out.println(e.getClass().getName() + ": " + e.getMessage());
        }

        // Shut down executor
        executor.shutdown();

        return Response.status(201).entity(queryResult.toString()).build();
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

}
