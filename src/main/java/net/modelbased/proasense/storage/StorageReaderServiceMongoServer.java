/**
 * Copyright 2015 Brian Elvesæter <${email}>
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
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;


@Path("/StorageReaderServiceMongoSync")
public class StorageReaderServiceMongoServer {
    private Properties kafkaProperties;


    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes({MediaType.TEXT_PLAIN, MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public Response StorageReaderServiceMongoSync(String text) {
        // Kafka properties
//        String zooKeeper = "89.216.116.44:2181";
        String zooKeeper = "192.168.11.20:2181";
        String groupId = "StorageWriterServiceMongoSync";
        String topic = "proasense.simpleevent.mhwirth.*";

        // Mongo properties
//        String mongoURL = "mongodb://127.0.0.1:27017";
//        String mongoULR = "mongodb://89.216.116.44:27017";
        String mongoURL = "mongodb://192.168.11.25:27017";

        //SensApp properties
        String sensappURL = "http://127.0.0.1:8090";
        String sensorName = "MHWirth.DDM.Hookload";

        // Process GET request
        String collectionId = "proasense.simpleevent.mhwirth.0";
        EventQueryType queryType = EventQueryType.SIMPLE;
        long startTime = 0L;
        long endTime = 0L;
        EventQueryOperation queryOperation = EventQueryOperation.DEFAULT;

        // Create executor environment for threads
        ArrayList<Callable> workers = new ArrayList<Callable>(1);
        ExecutorService executor = Executors.newFixedThreadPool(1);
        Callable<List<Document>> query = new EventReaderMongoSync(mongoURL, queryType, collectionId, startTime, endTime, queryOperation, null);
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


    private Properties getDefaultProperties() {
        kafkaProperties = new Properties();
        String propFilename = "/resources/kafka.properties";
        InputStream inputStream = getClass().getClassLoader().getResourceAsStream(propFilename);

        try {
            if (inputStream != null) {
                kafkaProperties.load(inputStream);
            } else
                throw new FileNotFoundException("Property file: '" + propFilename + "' not found in classpath.");
        }
        catch (IOException e) {
            System.out.println("Exception:" + e.getMessage());
        }

        return kafkaProperties;
    }

}
