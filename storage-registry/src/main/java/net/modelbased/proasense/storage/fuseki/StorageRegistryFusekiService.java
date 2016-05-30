/**
 * Copyright (C) 2014-2016 SINTEF
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
package net.modelbased.proasense.storage.fuseki;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import java.util.List;
import java.util.Properties;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


@Path("/")
public class StorageRegistryFusekiService {
    private Properties serverProperties;

    private String FUSEKI_SPARQL_ENDPOINT;


    public StorageRegistryFusekiService() {
        // Get server properties
        serverProperties = loadServerProperties();

        // Apache Fuseki registry configuration properties
        this.FUSEKI_SPARQL_ENDPOINT = serverProperties.getProperty("proasense.storage.fuseki.sparql.endpoint");
    }


    @GET
    @Path("/query/machine/list")
    @Produces(MediaType.APPLICATION_JSON)
    public Response queryMachineList()
    {
        ExecutorService executor = Executors.newFixedThreadPool(1);
        Callable<List<String>> query = new SparqlQueryCall(FUSEKI_SPARQL_ENDPOINT, null);
        executor.submit(query);

        List<String> queryResult = null;
        try {
            queryResult = query.call();

//            for (Document doc : queryResult) {
//                responseResult.add(new EventConverter<SimpleEvent>(SimpleEvent.class, doc).getEvent());
//            }
        } catch (Exception e) {
            System.out.println(e.getClass().getName() + ": " + e.getMessage());
        }

        String result = queryResult.toString();

        // Return HTTP response 200 in case of success
        return Response.status(200).entity(result).build();
    }


    @GET
    @Path("/query/machine/properties")
    @Produces(MediaType.APPLICATION_JSON)
    public Response queryMachineProperties(
            @QueryParam("machineId") String machineId
    )
    {
        ExecutorService executor = Executors.newFixedThreadPool(1);
        Callable<List<String>> query = new SparqlQueryCall(FUSEKI_SPARQL_ENDPOINT, null);
        executor.submit(query);

        List<String> queryResult = null;
        try {
            queryResult = query.call();

//            for (Document doc : queryResult) {
//                responseResult.add(new EventConverter<SimpleEvent>(SimpleEvent.class, doc).getEvent());
//            }
        } catch (Exception e) {
            System.out.println(e.getClass().getName() + ": " + e.getMessage());
        }

        String result = queryResult.toString();

        // Return HTTP response 200 in case of success
        return Response.status(200).entity(result).build();
    }


    @GET
    @Path("/query/sensor/list")
    @Produces(MediaType.APPLICATION_JSON)
    public Response querySensorList()
    {
        List<String> queryResult = null;

        //

        String result = queryResult.toString();

        // Return HTTP response 200 in case of success
        return Response.status(200).entity(result).build();
    }


    @GET
    @Path("/query/sensor/properties")
    @Produces(MediaType.APPLICATION_JSON)
    public Response querySensorProperties(
            @QueryParam("sensorId") String sensorId
    )
    {
        List<String> queryResult = null;

        //

        String result = queryResult.toString();

        // Return HTTP response 200 in case of success
        return Response.status(200).entity(result).build();
    }


    @GET
    @Path("/server/status")
    @Produces(MediaType.TEXT_PLAIN)
    public Response getServerStatus() {
        String result = "ProaSense Storage Registry Service running...";

        // Return HTTP response 200 in case of success
        return Response.status(200).entity(result).build();
    }


    @GET
    @Path("/server/properties")
    @Produces(MediaType.TEXT_PLAIN)
    public Response printServerProperties() {
        String result = this.serverProperties.toString();

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
