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

import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.query.ResultSetFormatter;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import java.util.Iterator;
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
        String SPARQL_MACHINE_LIST = "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>\n" +
                "PREFIX owl: <http://www.w3.org/2002/07/owl#>\n" +
                "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>\n" +
                "PREFIX xsd: <http://www.w3.org/2001/XMLSchema#>\n" +
                "PREFIX ssn: <http://purl.oclc.org/NET/ssnx/ssn#>\n" +
                "PREFIX pssn: <http://www.sintef.no/pssn#>\n" +
                "\n" +
                "SELECT DISTINCT ?x\n" +
                "  WHERE {\n" +
                "    ?subject rdfs:subClassOf+ pssn:Machine .\n" +
                "    ?x rdf:type ?subject\n" +
                "  }\n" +
                "ORDER BY ASC (?x)";

        QueryExecution qe = QueryExecutionFactory.sparqlService(FUSEKI_SPARQL_ENDPOINT, SPARQL_MACHINE_LIST);
        ResultSet results = qe.execSelect();

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ResultSetFormatter.outputAsJSON(baos, results);

        String resultsJson = baos.toString();
        resultsJson = resultsJson.replaceAll("http://www.sintef.no/pssn#", "");

        StringBuilder responseResult = new StringBuilder();
        ObjectMapper mapper = new ObjectMapper();
        try {
            JsonNode rootNode = mapper.readTree(resultsJson);
            JsonNode resultsNode = rootNode.path("results");
            JsonNode bindingsNode = resultsNode.path("bindings");
            Iterator<JsonNode> iterator = bindingsNode.getElements();
            while (iterator.hasNext()) {
                JsonNode xNode = iterator.next();
                List<String> valueNode = xNode.findValuesAsText("value");

                responseResult.append(valueNode.get(0));
                responseResult.append(",");
            }
        } catch (IOException e) {
            System.out.println(e.getClass().getName() + ": " + e.getMessage());
        }
        qe.close();

        // Convert to string and remove trailing ","
        int responseLength = responseResult.length();
        if (responseLength > 1)
            responseResult.deleteCharAt(responseLength - 1);
        String result = responseResult.toString();

        // Return HTTP response 200 in case of success
        return Response.status(200).entity(result).build();
    }


    @GET
    @Path("/query/machine/list2")
    @Produces(MediaType.APPLICATION_JSON)
    public Response queryMachineList2()
    {
        String SPARQL_MACHINE_LIST = "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>\n" +
                "PREFIX owl: <http://www.w3.org/2002/07/owl#>\n" +
                "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>\n" +
                "PREFIX xsd: <http://www.w3.org/2001/XMLSchema#>\n" +
                "PREFIX ssn: <http://purl.oclc.org/NET/ssnx/ssn#>\n" +
                "PREFIX pssn: <http://www.sintef.no/pssn#>\n" +
                "\n" +
                "SELECT DISTINCT ?x\n" +
                "  WHERE {\n" +
                "    ?subject rdfs:subClassOf+ pssn:Machine .\n" +
                "    ?x rdf:type ?subject\n" +
                "  }\n" +
                "ORDER BY ASC (?x)";

        QueryExecution qe = QueryExecutionFactory.sparqlService(FUSEKI_SPARQL_ENDPOINT, SPARQL_MACHINE_LIST);
        ResultSet results = qe.execSelect();

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ResultSetFormatter.outputAsJSON(baos, results);

        String resultsJson = baos.toString();
        resultsJson = resultsJson.replaceAll("http://www.sintef.no/pssn#", "");

        String result = resultsJson;

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
        String SPARQL_MACHINE_PROPERTIES = "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>\n" +
                "PREFIX owl: <http://www.w3.org/2002/07/owl#>\n" +
                "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>\n" +
                "PREFIX xsd: <http://www.w3.org/2001/XMLSchema#>\n" +
                "PREFIX ssn: <http://purl.oclc.org/NET/ssnx/ssn#>\n" +
                "PREFIX pssn: <http://www.sintef.no/pssn#>\n" +
                "\n" +
                "SELECT DISTINCT ?property ?value\n" +
                "  WHERE {\n" +
                "    pssn:IMM1 ?property ?value .\n" +
                "}";

        SPARQL_MACHINE_PROPERTIES = SPARQL_MACHINE_PROPERTIES.replaceAll("IMM1", machineId);

        QueryExecution qe = QueryExecutionFactory.sparqlService(FUSEKI_SPARQL_ENDPOINT, SPARQL_MACHINE_PROPERTIES);
        ResultSet results = qe.execSelect();
        qe.close();

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ResultSetFormatter.outputAsJSON(baos, results);

        String resultsJson = baos.toString();
        resultsJson = resultsJson.replaceAll("http://www.sintef.no/pssn#", "");

        String result = resultsJson.toString();

        // Return HTTP response 200 in case of success
        return Response.status(200).entity(result).build();
    }


    @GET
    @Path("/query/sensor/list")
    @Produces(MediaType.APPLICATION_JSON)
    public Response querySensorList()
    {
        String SPARQL_SENSOR_LIST = "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>\n" +
                "PREFIX owl: <http://www.w3.org/2002/07/owl#>\n" +
                "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>\n" +
                "PREFIX xsd: <http://www.w3.org/2001/XMLSchema#>\n" +
                "PREFIX ssn: <http://purl.oclc.org/NET/ssnx/ssn#>\n" +
                "PREFIX pssn: <http://www.sintef.no/pssn#>\n" +
                "\n" +
                "SELECT DISTINCT ?x\n" +
                "  WHERE {\n" +
                "    ?subject rdfs:subClassOf+ ssn:Sensor .\n" +
                "    ?x rdf:type ?subject.\n" +
                "  }\n" +
                "ORDER BY ASC (?x)";

        QueryExecution qe = QueryExecutionFactory.sparqlService(FUSEKI_SPARQL_ENDPOINT, SPARQL_SENSOR_LIST);
        ResultSet results = qe.execSelect();

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ResultSetFormatter.outputAsJSON(baos, results);

        String resultsJson = baos.toString();
        resultsJson = resultsJson.replaceAll("http://www.sintef.no/pssn#", "");

        StringBuilder responseResult = new StringBuilder();
        ObjectMapper mapper = new ObjectMapper();
        try {
            JsonNode rootNode = mapper.readTree(resultsJson);
            JsonNode resultsNode = rootNode.path("results");
            JsonNode bindingsNode = resultsNode.path("bindings");
            Iterator<JsonNode> iterator = bindingsNode.getElements();
            while (iterator.hasNext()) {
                JsonNode xNode = iterator.next();
                List<String> valueNode = xNode.findValuesAsText("value");

                responseResult.append(valueNode.get(0));
                responseResult.append(",");
            }
        } catch (IOException e) {
            System.out.println(e.getClass().getName() + ": " + e.getMessage());
        }
        qe.close();

        // Convert to string and remove trailing ","
        int responseLength = responseResult.length();
        if (responseLength > 1)
            responseResult.deleteCharAt(responseLength - 1);

        String result = responseResult.toString();

        // Return HTTP response 200 in case of success
        return Response.status(200).entity(result).build();
    }


    @GET
    @Path("/query/sensor/list2")
    @Produces(MediaType.APPLICATION_JSON)
    public Response querySensorList2()
    {
        String SPARQL_SENSOR_LIST = "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>\n" +
                "PREFIX owl: <http://www.w3.org/2002/07/owl#>\n" +
                "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>\n" +
                "PREFIX xsd: <http://www.w3.org/2001/XMLSchema#>\n" +
                "PREFIX ssn: <http://purl.oclc.org/NET/ssnx/ssn#>\n" +
                "PREFIX pssn: <http://www.sintef.no/pssn#>\n" +
                "\n" +
                "SELECT DISTINCT ?x\n" +
                "  WHERE {\n" +
                "    ?subject rdfs:subClassOf+ ssn:Sensor .\n" +
                "    ?x rdf:type ?subject.\n" +
                "  }\n" +
                "ORDER BY ASC (?x)";

        QueryExecution qe = QueryExecutionFactory.sparqlService(FUSEKI_SPARQL_ENDPOINT, SPARQL_SENSOR_LIST);
        ResultSet results = qe.execSelect();

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ResultSetFormatter.outputAsJSON(baos, results);

        String resultsJson = baos.toString();
        resultsJson = resultsJson.replaceAll("http://www.sintef.no/pssn#", "");

        String result = resultsJson;

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
        String SPARQL_SENSOR_PROPERTIES = "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>\n" +
                "PREFIX owl: <http://www.w3.org/2002/07/owl#>\n" +
                "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>\n" +
                "PREFIX xsd: <http://www.w3.org/2001/XMLSchema#>\n" +
                "PREFIX ssn: <http://purl.oclc.org/NET/ssnx/ssn#>\n" +
                "PREFIX pssn: <http://www.sintef.no/pssn#>\n" +
                "\n" +
                "SELECT DISTINCT ?property ?value\n" +
                "  WHERE {\n" +
                "    pssn:dustParticleSensor ?property ?value .\n" +
                "}";

        SPARQL_SENSOR_PROPERTIES = SPARQL_SENSOR_PROPERTIES.replaceAll("dustParticleSensor", sensorId);

        QueryExecution qe = QueryExecutionFactory.sparqlService(FUSEKI_SPARQL_ENDPOINT, SPARQL_SENSOR_PROPERTIES);
        ResultSet results = qe.execSelect();

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ResultSetFormatter.outputAsJSON(baos, results);

        String resultsJson = baos.toString();
        resultsJson = resultsJson.replaceAll("http://www.sintef.no/pssn#", "");

        String result = resultsJson;

        // Return HTTP response 200 in case of success
        return Response.status(200).entity(result).build();
    }


    @GET
    @Path("/query/product/list")
    @Produces(MediaType.APPLICATION_JSON)
    public Response queryProductList()
    {
        String SPARQL_PRODUCT_LIST = "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>\n" +
                "PREFIX owl: <http://www.w3.org/2002/07/owl#>\n" +
                "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>\n" +
                "PREFIX xsd: <http://www.w3.org/2001/XMLSchema#>\n" +
                "PREFIX ssn: <http://purl.oclc.org/NET/ssnx/ssn#>\n" +
                "PREFIX pssn: <http://www.sintef.no/pssn#>\n" +
                "\n" +
                "SELECT DISTINCT ?x\n" +
                "  WHERE {\n" +
                "    ?x rdf:type pssn:Product .\n" +
                "  }\n" +
                "ORDER BY ASC (?x)";

        QueryExecution qe = QueryExecutionFactory.sparqlService(FUSEKI_SPARQL_ENDPOINT, SPARQL_PRODUCT_LIST);
        ResultSet results = qe.execSelect();

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ResultSetFormatter.outputAsJSON(baos, results);

        String resultsJson = baos.toString();
        resultsJson = resultsJson.replaceAll("http://www.sintef.no/pssn#", "");

        StringBuilder responseResult = new StringBuilder();
        ObjectMapper mapper = new ObjectMapper();
        try {
            JsonNode rootNode = mapper.readTree(resultsJson);
            JsonNode resultsNode = rootNode.path("results");
            JsonNode bindingsNode = resultsNode.path("bindings");
            Iterator<JsonNode> iterator = bindingsNode.getElements();
            while (iterator.hasNext()) {
                JsonNode xNode = iterator.next();
                List<String> valueNode = xNode.findValuesAsText("value");

                responseResult.append(valueNode.get(0));
                responseResult.append(",");
            }
        } catch (IOException e) {
            System.out.println(e.getClass().getName() + ": " + e.getMessage());
        }
        qe.close();

        // Convert to string and remove trailing ","
        int responseLength = responseResult.length();
        if (responseLength > 1)
            responseResult.deleteCharAt(responseLength - 1);

        String result = responseResult.toString();

        // Return HTTP response 200 in case of success
        return Response.status(200).entity(result).build();
    }


    @GET
    @Path("/query/product/list2")
    @Produces(MediaType.APPLICATION_JSON)
    public Response queryProductList2()
    {
        String SPARQL_PRODUCT_LIST = "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>\n" +
                "PREFIX owl: <http://www.w3.org/2002/07/owl#>\n" +
                "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>\n" +
                "PREFIX xsd: <http://www.w3.org/2001/XMLSchema#>\n" +
                "PREFIX ssn: <http://purl.oclc.org/NET/ssnx/ssn#>\n" +
                "PREFIX pssn: <http://www.sintef.no/pssn#>\n" +
                "\n" +
                "SELECT DISTINCT ?x\n" +
                "  WHERE {\n" +
                "    ?x rdf:type pssn:Product .\n" +
                "  }\n" +
                "ORDER BY ASC (?x)";

        QueryExecution qe = QueryExecutionFactory.sparqlService(FUSEKI_SPARQL_ENDPOINT, SPARQL_PRODUCT_LIST);
        ResultSet results = qe.execSelect();

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ResultSetFormatter.outputAsJSON(baos, results);

        String resultsJson = baos.toString();
        resultsJson = resultsJson.replaceAll("http://www.sintef.no/pssn#", "");

        String result = resultsJson;

        // Return HTTP response 200 in case of success
        return Response.status(200).entity(result).build();
    }


    @GET
    @Path("/query/product/properties")
    @Produces(MediaType.APPLICATION_JSON)
    public Response queryProductProperties(
            @QueryParam("productId") String productId
    )
    {
        String SPARQL_PRODUCT_PROPERTIES = "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>\n" +
                "PREFIX owl: <http://www.w3.org/2002/07/owl#>\n" +
                "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>\n" +
                "PREFIX xsd: <http://www.w3.org/2001/XMLSchema#>\n" +
                "PREFIX ssn: <http://purl.oclc.org/NET/ssnx/ssn#>\n" +
                "PREFIX pssn: <http://www.sintef.no/pssn#>\n" +
                "\n" +
                "SELECT DISTINCT ?property ?value\n" +
                "  WHERE {\n" +
                "    pssn:Astra_3300 ?property ?value .\n" +
                "}";

        SPARQL_PRODUCT_PROPERTIES = SPARQL_PRODUCT_PROPERTIES.replaceAll("Astra_3300", productId);

        QueryExecution qe = QueryExecutionFactory.sparqlService(FUSEKI_SPARQL_ENDPOINT, SPARQL_PRODUCT_PROPERTIES);
        ResultSet results = qe.execSelect();

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ResultSetFormatter.outputAsJSON(baos, results);

        String resultsJson = baos.toString();
        resultsJson = resultsJson.replaceAll("http://www.sintef.no/pssn#", "");

        String result = resultsJson;

        // Return HTTP response 200 in case of success
        return Response.status(200).entity(result).build();
    }


    @GET
    @Path("/query/mould/list")
    @Produces(MediaType.APPLICATION_JSON)
    public Response queryMouldList()
    {
        String SPARQL_MOULD_LIST = "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>\n" +
                "PREFIX owl: <http://www.w3.org/2002/07/owl#>\n" +
                "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>\n" +
                "PREFIX xsd: <http://www.w3.org/2001/XMLSchema#>\n" +
                "PREFIX ssn: <http://purl.oclc.org/NET/ssnx/ssn#>\n" +
                "PREFIX pssn: <http://www.sintef.no/pssn#>\n" +
                "\n" +
                "SELECT DISTINCT *\n" +
                "  WHERE {\n" +
                "    ?subject rdf:type pssn:Mould .\n" +
                "  }\n" +
                "ORDER BY ASC (?x)";

        QueryExecution qe = QueryExecutionFactory.sparqlService(FUSEKI_SPARQL_ENDPOINT, SPARQL_MOULD_LIST);
        ResultSet results = qe.execSelect();

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ResultSetFormatter.outputAsJSON(baos, results);

        String resultsJson = baos.toString();
        resultsJson = resultsJson.replaceAll("http://www.sintef.no/pssn#", "");

        String result = resultsJson;

        // Return HTTP response 200 in case of success
        return Response.status(200).entity(result).build();
    }


    @GET
    @Path("/query/mould/properties")
    @Produces(MediaType.APPLICATION_JSON)
    public Response queryMouldProperties(
            @QueryParam("mouldId") String productId
    )
    {
        String SPARQL_MOULD_PROPERTIES = "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>\n" +
                "PREFIX owl: <http://www.w3.org/2002/07/owl#>\n" +
                "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>\n" +
                "PREFIX xsd: <http://www.w3.org/2001/XMLSchema#>\n" +
                "PREFIX ssn: <http://purl.oclc.org/NET/ssnx/ssn#>\n" +
                "PREFIX pssn: <http://www.sintef.no/pssn#>\n" +
                "\n" +
                "SELECT DISTINCT ?property ?value\n" +
                "  WHERE {\n" +
                "    pssn:MouldID_KSP156.013-02U010 ?property ?value .\n" +
                "}";

        SPARQL_MOULD_PROPERTIES = SPARQL_MOULD_PROPERTIES.replaceAll("MouldID_KSP156.013-02U010", productId);

        QueryExecution qe = QueryExecutionFactory.sparqlService(FUSEKI_SPARQL_ENDPOINT, SPARQL_MOULD_PROPERTIES);
        ResultSet results = qe.execSelect();

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ResultSetFormatter.outputAsJSON(baos, results);

        String resultsJson = baos.toString();
        resultsJson = resultsJson.replaceAll("http://www.sintef.no/pssn#", "");

        String result = resultsJson;

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
