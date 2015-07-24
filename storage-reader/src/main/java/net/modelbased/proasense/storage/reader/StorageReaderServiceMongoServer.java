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

import net.modelbased.proasense.storage.EventConverter;

import eu.proasense.internal.AnomalyEvent;
import eu.proasense.internal.DerivedEvent;
import eu.proasense.internal.FeedbackEvent;
import eu.proasense.internal.PredictedEvent;
import eu.proasense.internal.RecommendationEvent;
import eu.proasense.internal.SimpleEvent;

import org.bson.Document;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


@Path("/")
public class StorageReaderServiceMongoServer {
    private Properties serverProperties;
    private String MONGODB_URL;
    private String MONGODB_DATABASE;


    public StorageReaderServiceMongoServer() {
        // Get server properties
        serverProperties = loadServerProperties();

        // MongoDB event reader configuration properties
        this.MONGODB_URL = serverProperties.getProperty("proasense.storage.mongodb.url");
        this.MONGODB_DATABASE = serverProperties.getProperty("proasense.storage.mongodb.database");
    }


    @GET
    @Path("/simple/default")
    @Produces(MediaType.APPLICATION_JSON)
    public Response queryDefaultSimpleEvents(
            @QueryParam("sensorId") String sensorId,
            @QueryParam("startTime") long startTime,
            @QueryParam("endTime") long endTime)
    {
        String collectionId = "simple." + sensorId;

        ExecutorService executor = Executors.newFixedThreadPool(1);
        Callable<List<Document>> query = new EventReaderMongoSync(MONGODB_URL, MONGODB_DATABASE, EventQueryType.SIMPLE, collectionId, startTime, endTime, null, EventQueryOperation.DEFAULT, null);
        executor.submit(query);

        List<Document> queryResult = null;
        List<SimpleEvent> responseResult = new ArrayList<SimpleEvent>();
        try {
            queryResult = query.call();

            for (Document doc : queryResult) {
                responseResult.add(new EventConverter<SimpleEvent>(SimpleEvent.class, doc).getEvent());
            }
        } catch (Exception e) {
            System.out.println(e.getClass().getName() + ": " + e.getMessage());
        }

        String result = responseResult.toString();

        // Return HTTP response 200 in case of success
        return Response.status(200).entity(result).build();
    }


    @GET
    @Path("/simple/average")
    @Produces(MediaType.APPLICATION_JSON)
    public Response queryAverageSimpleEvents(
            @QueryParam("sensorId") String sensorId,
            @QueryParam("startTime") long startTime,
            @QueryParam("endTime") long endTime,
            @QueryParam("propertyKey") String propertyKey)
    {
        String collectionId = "simple." + sensorId;

        ExecutorService executor = Executors.newFixedThreadPool(1);
        Callable<List<Document>> query = new EventReaderMongoSync(MONGODB_URL, MONGODB_DATABASE, EventQueryType.SIMPLE, collectionId, startTime, endTime, propertyKey, EventQueryOperation.AVERAGE, null);
        executor.submit(query);

        List<Document> queryResult = null;
        try {
            queryResult = query.call();
        } catch (Exception e) {
            System.out.println(e.getClass().getName() + ": " + e.getMessage());
        }

        String result = queryResult.get(0).get("RESULT").toString();

        // Return HTTP response 200 in case of success
        return Response.status(200).entity(result).build();
    }


    @GET
    @Path("/simple/maximum")
    @Produces(MediaType.APPLICATION_JSON)
    public Response queryMaximumSimpleEvents(
            @QueryParam("sensorId") String sensorId,
            @QueryParam("startTime") long startTime,
            @QueryParam("endTime") long endTime,
            @QueryParam("propertyKey") String propertyKey)
    {
        String collectionId = "simple." + sensorId;

        ExecutorService executor = Executors.newFixedThreadPool(1);
        Callable<List<Document>> query = new EventReaderMongoSync(MONGODB_URL, MONGODB_DATABASE, EventQueryType.SIMPLE, collectionId, startTime, endTime, propertyKey, EventQueryOperation.MAXIMUM, null);
        executor.submit(query);

        List<Document> queryResult = null;
        try {
            queryResult = query.call();
        } catch (Exception e) {
            System.out.println(e.getClass().getName() + ": " + e.getMessage());
        }

        String result = queryResult.get(0).get("RESULT").toString();

        // Return HTTP response 200 in case of success
        return Response.status(200).entity(result).build();
    }


    @GET
    @Path("/simple/minimum")
    @Produces(MediaType.APPLICATION_JSON)
    public Response queryMinimumSimpleEvents(
            @QueryParam("sensorId") String sensorId,
            @QueryParam("startTime") long startTime,
            @QueryParam("endTime") long endTime,
            @QueryParam("propertyKey") String propertyKey)
    {
        String collectionId = "simple." + sensorId;

        ExecutorService executor = Executors.newFixedThreadPool(1);
        Callable<List<Document>> query = new EventReaderMongoSync(MONGODB_URL, MONGODB_DATABASE, EventQueryType.SIMPLE, collectionId, startTime, endTime, propertyKey, EventQueryOperation.MINUMUM, null);
        executor.submit(query);

        List<Document> queryResult = null;
        try {
            queryResult = query.call();
        } catch (Exception e) {
            System.out.println(e.getClass().getName() + ": " + e.getMessage());
        }

        String result = queryResult.get(0).get("RESULT").toString();

        // Return HTTP response 200 in case of success
        return Response.status(200).entity(result).build();
    }


    @GET
    @Path("/derived/default")
    @Produces(MediaType.APPLICATION_JSON)
    public Response queryDefaultDerivedEvents(
            @QueryParam("componentId") String componentId,
            @QueryParam("startTime") long startTime,
            @QueryParam("endTime") long endTime)
    {
        String collectionId = "derived." + componentId;

        ExecutorService executor = Executors.newFixedThreadPool(1);
        Callable<List<Document>> query = new EventReaderMongoSync(MONGODB_URL, MONGODB_DATABASE, EventQueryType.DERIVED, collectionId, startTime, endTime, null, EventQueryOperation.DEFAULT, null);
        executor.submit(query);

        List<Document> queryResult = null;
        List<DerivedEvent> responseResult = new ArrayList<DerivedEvent>();
        try {
            queryResult = query.call();

            for (Document doc : queryResult) {
                responseResult.add(new EventConverter<DerivedEvent>(DerivedEvent.class, doc).getEvent());
            }
        } catch (Exception e) {
            System.out.println(e.getClass().getName() + ": " + e.getMessage());
        }

        String result = responseResult.toString();

        // Return HTTP response 200 in case of success
        return Response.status(200).entity(result).build();
    }


    @GET
    @Path("/derived/average")
    @Produces(MediaType.APPLICATION_JSON)
    public Response queryAverageDerivedEvents(
            @QueryParam("componentId") String componentId,
            @QueryParam("startTime") long startTime,
            @QueryParam("endTime") long endTime,
            @QueryParam("propertyKey") String propertyKey)
    {
        String collectionId = "derived." + componentId;

        ExecutorService executor = Executors.newFixedThreadPool(1);
        Callable<List<Document>> query = new EventReaderMongoSync(MONGODB_URL, MONGODB_DATABASE, EventQueryType.DERIVED, collectionId, startTime, endTime, propertyKey, EventQueryOperation.AVERAGE, null);
        executor.submit(query);

        List<Document> queryResult = null;
        try {
            queryResult = query.call();
        } catch (Exception e) {
            System.out.println(e.getClass().getName() + ": " + e.getMessage());
        }

        String result = queryResult.get(0).get("RESULT").toString();

        // Return HTTP response 200 in case of success
        return Response.status(200).entity(result).build();
    }


    @GET
    @Path("/derived/maximum")
    @Produces(MediaType.APPLICATION_JSON)
    public Response queryMaximumDerivedEvents(
            @QueryParam("componentId") String componentId,
            @QueryParam("startTime") long startTime,
            @QueryParam("endTime") long endTime,
            @QueryParam("propertyKey") String propertyKey)
    {
        String collectionId = "derived." + componentId;

        ExecutorService executor = Executors.newFixedThreadPool(1);
        Callable<List<Document>> query = new EventReaderMongoSync(MONGODB_URL, MONGODB_DATABASE, EventQueryType.DERIVED, collectionId, startTime, endTime, propertyKey, EventQueryOperation.MAXIMUM, null);
        executor.submit(query);

        List<Document> queryResult = null;
        try {
            queryResult = query.call();
        } catch (Exception e) {
            System.out.println(e.getClass().getName() + ": " + e.getMessage());
        }

        String result = queryResult.get(0).get("RESULT").toString();

        // Return HTTP response 200 in case of success
        return Response.status(200).entity(result).build();
    }


    @GET
    @Path("/derived/minimum")
    @Produces(MediaType.APPLICATION_JSON)
    public Response queryMinimumDerivedEvents(
            @QueryParam("componentId") String componentId,
            @QueryParam("startTime") long startTime,
            @QueryParam("endTime") long endTime,
            @QueryParam("propertyKey") String propertyKey)
    {
        String collectionId = "derived." + componentId;

        ExecutorService executor = Executors.newFixedThreadPool(1);
        Callable<List<Document>> query = new EventReaderMongoSync(MONGODB_URL, MONGODB_DATABASE, EventQueryType.DERIVED, collectionId, startTime, endTime, propertyKey, EventQueryOperation.MINUMUM, null);
        executor.submit(query);

        List<Document> queryResult = null;
        try {
            queryResult = query.call();
        } catch (Exception e) {
            System.out.println(e.getClass().getName() + ": " + e.getMessage());
        }

        String result = queryResult.get(0).get("RESULT").toString();

        // Return HTTP response 200 in case of success
        return Response.status(200).entity(result).build();
    }


    @GET
    @Path("/predicted/default")
    @Produces(MediaType.APPLICATION_JSON)
    public Response queryDefaultPredictedEvents(
            @QueryParam("startTime") long startTime,
            @QueryParam("endTime") long endTime)
    {
        String collectionId = "predicted.system";

        ExecutorService executor = Executors.newFixedThreadPool(1);
        Callable<List<Document>> query = new EventReaderMongoSync(MONGODB_URL, MONGODB_DATABASE, EventQueryType.PREDICTED, collectionId, startTime, endTime, null, EventQueryOperation.DEFAULT, null);
        executor.submit(query);

        List<Document> queryResult = null;
        List<PredictedEvent> responseResult = new ArrayList<PredictedEvent>();
        try {
            queryResult = query.call();

            for (Document doc : queryResult) {
                responseResult.add(new EventConverter<PredictedEvent>(PredictedEvent.class, doc).getEvent());
            }
        } catch (Exception e) {
            System.out.println(e.getClass().getName() + ": " + e.getMessage());
        }

        String result = responseResult.toString();

        // Return HTTP response 200 in case of success
        return Response.status(200).entity(result).build();
    }


    @GET
    @Path("/anomaly/default")
    @Produces(MediaType.APPLICATION_JSON)
    public Response queryDefaultAnomalyEvents(
        @QueryParam("startTime") long startTime,
        @QueryParam("endTime") long endTime)
    {
        String collectionId = "anomaly.system";

        ExecutorService executor = Executors.newFixedThreadPool(1);
        Callable<List<Document>> query = new EventReaderMongoSync(MONGODB_URL, MONGODB_DATABASE, EventQueryType.ANOMALY, collectionId, startTime, endTime, null, EventQueryOperation.DEFAULT, null);
        executor.submit(query);

        List<Document> queryResult = null;
        List<AnomalyEvent> responseResult = new ArrayList<AnomalyEvent>();
        try {
            queryResult = query.call();

            for (Document doc : queryResult) {
                responseResult.add(new EventConverter<AnomalyEvent>(AnomalyEvent.class, doc).getEvent());
            }
        } catch (Exception e) {
            System.out.println(e.getClass().getName() + ": " + e.getMessage());
        }

        String result = responseResult.toString();

        // Return HTTP response 200 in case of success
        return Response.status(200).entity(result).build();
    }


    @GET
    @Path("/recommendation/default")
    @Produces(MediaType.APPLICATION_JSON)
    public Response queryDefaultRecommendationEvents(
            @QueryParam("startTime") long startTime,
            @QueryParam("endTime") long endTime)
    {
        String collectionId = "recommendation.system";

        ExecutorService executor = Executors.newFixedThreadPool(1);
        Callable<List<Document>> query = new EventReaderMongoSync(MONGODB_URL, MONGODB_DATABASE, EventQueryType.RECOMMENDATION, collectionId, startTime, endTime, null, EventQueryOperation.DEFAULT, null);
        executor.submit(query);

        List<Document> queryResult = null;
        List<RecommendationEvent> responseResult = new ArrayList<RecommendationEvent>();
        try {
            queryResult = query.call();

            for (Document doc : queryResult) {
                responseResult.add(new EventConverter<RecommendationEvent>(RecommendationEvent.class, doc).getEvent());
            }
        } catch (Exception e) {
            System.out.println(e.getClass().getName() + ": " + e.getMessage());
        }

        String result = responseResult.toString();

        // Return HTTP response 200 in case of success
        return Response.status(200).entity(result).build();
    }


    @GET
    @Path("/feedback/default")
    @Produces(MediaType.APPLICATION_JSON)
    public Response queryDefaultFeedbackEvents(
            @QueryParam("startTime") long startTime,
            @QueryParam("endTime") long endTime)
    {
        String collectionId = "feedback.system";

        ExecutorService executor = Executors.newFixedThreadPool(1);
        Callable<List<Document>> query = new EventReaderMongoSync(MONGODB_URL, MONGODB_DATABASE, EventQueryType.FEEDBACK, collectionId, startTime, endTime, null, EventQueryOperation.DEFAULT, null);
        executor.submit(query);

        List<Document> queryResult = null;
        List<FeedbackEvent> responseResult = new ArrayList<FeedbackEvent>();
        try {
            queryResult = query.call();

            for (Document doc : queryResult) {
                responseResult.add(new EventConverter<FeedbackEvent>(FeedbackEvent.class, doc).getEvent());
            }
        } catch (Exception e) {
            System.out.println(e.getClass().getName() + ": " + e.getMessage());
        }

        String result = responseResult.toString();

        // Return HTTP response 200 in case of success
        return Response.status(200).entity(result).build();
    }


    @GET
    @Path("/serverproperties")
    @Produces(MediaType.TEXT_PLAIN)
    public Response printServerProperties() {
        String result = "MONGODB_URL = " + this.MONGODB_URL + ", MONGODB_DATABASE = " + this.MONGODB_DATABASE;

        // Return HTTP response 200 in case of success
        return Response.status(200).entity(result).build();
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
