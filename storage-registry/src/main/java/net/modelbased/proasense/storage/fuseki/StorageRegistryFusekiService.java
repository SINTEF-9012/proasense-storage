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
import org.json.JSONArray;
import org.json.JSONObject;

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


@Path("/")
public class StorageRegistryFusekiService {
    private Properties serverProperties;

    private String FUSEKI_SPARQL_URL;
    private String FUSEKI_DATASET_DEFAULT;
    private String FUSEKI_DATASET_HELLA;
    private String FUSEKI_DATASET_MHWIRTH;

    private String FUSEKI_SPARQL_ENDPOINT_DEFAULT;
    private String FUSEKI_SPARQL_ENDPOINT_HELLA;
    private String FUSEKI_SPARQL_ENDPOINT_MHWIRTH;

    public StorageRegistryFusekiService() {
        // Get server properties
        serverProperties = loadServerProperties();

        // Get Apache Fuseki configuration properties
        this.FUSEKI_SPARQL_URL      = serverProperties.getProperty("proasense.storage.fuseki.sparql.url");
        this.FUSEKI_DATASET_DEFAULT = serverProperties.getProperty("proasense.storage.fuseki.dataset.default");
        this.FUSEKI_DATASET_HELLA   = serverProperties.getProperty("proasense.storage.fuseki.dataset.hella");
        this.FUSEKI_DATASET_MHWIRTH = serverProperties.getProperty("proasense.storage.fuseki.dataset.mhwirth");

        // Set Apache Fuseki endpoints
        this.FUSEKI_SPARQL_ENDPOINT_DEFAULT = this.FUSEKI_SPARQL_URL + "/" + this.FUSEKI_DATASET_DEFAULT + "/query";
        this.FUSEKI_SPARQL_ENDPOINT_HELLA   = this.FUSEKI_SPARQL_URL + "/" + this.FUSEKI_DATASET_HELLA + "/query";
        this.FUSEKI_SPARQL_ENDPOINT_MHWIRTH = this.FUSEKI_SPARQL_URL + "/" + this.FUSEKI_DATASET_MHWIRTH + "/query";
    }


    @GET
    @Path("/query/sensor/list")
    @Produces(MediaType.APPLICATION_JSON)
    public Response querySensorList(
            @QueryParam("dataset") String dataset
    )
    {
        String FUSEKI_SPARQL_ENDPOINT_URL = getFusekiSparqlEndpointUrl(dataset);

        String SPARQL_SENSOR_LIST = "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>\n" +
                "PREFIX owl: <http://www.w3.org/2002/07/owl#>\n" +
                "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>\n" +
                "PREFIX xsd: <http://www.w3.org/2001/XMLSchema#>\n" +
                "PREFIX pssn: <http://www.sintef.no/pssn#>\n" +
                "\n" +
                "SELECT DISTINCT *\n" +
                "  WHERE {\n" +
                "    ?sensorId rdf:type <http://purl.oclc.org/NET/ssnx/ssn#Sensor>.\n" +
                "  }\n" +
                "ORDER BY ASC (?sensorId)";

        QueryExecution qe = QueryExecutionFactory.sparqlService(FUSEKI_SPARQL_ENDPOINT_URL, SPARQL_SENSOR_LIST);
        ResultSet results = qe.execSelect();

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ResultSetFormatter.outputAsJSON(baos, results);

        qe.close();

        String jsonResults = baos.toString();
        jsonResults = jsonResults.replaceAll("http://www.sintef.no/pssn#", "");

        JSONObject jsonResponse = new JSONObject();
        JSONArray sensorArray = new JSONArray();

        ObjectMapper mapper = new ObjectMapper();
        try {
            JsonNode rootNode = mapper.readTree(jsonResults);
            JsonNode resultsNode = rootNode.path("results");
            JsonNode bindingsNode = resultsNode.path("bindings");
            Iterator<JsonNode> iterator = bindingsNode.getElements();
            while (iterator.hasNext()) {
                JsonNode xNode = iterator.next();
                List<String> valueNode = xNode.findValuesAsText("value");

                sensorArray.put(valueNode.get(0));
            }
        } catch (IOException e) {
            System.out.println(e.getClass().getName() + ": " + e.getMessage());
        }

        jsonResponse.put("sensor", sensorArray);

        String result = jsonResponse.toString(2);

        // Return HTTP response 200 in case of success
        return Response.status(200).entity(result).build();
    }


    @GET
    @Path("/query/sensor/list2")
    @Produces(MediaType.APPLICATION_JSON)
    public Response querySensorList2(
            @QueryParam("dataset") String dataset
    )
    {
        String FUSEKI_SPARQL_ENDPOINT_URL = getFusekiSparqlEndpointUrl(dataset);

        String SPARQL_SENSOR_LIST = "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>\n" +
                "PREFIX owl: <http://www.w3.org/2002/07/owl#>\n" +
                "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>\n" +
                "PREFIX xsd: <http://www.w3.org/2001/XMLSchema#>\n" +
                "PREFIX pssn: <http://www.sintef.no/pssn#>\n" +
                "\n" +
                "SELECT DISTINCT *\n" +
                "  WHERE {\n" +
                "    ?sensorId rdf:type <http://purl.oclc.org/NET/ssnx/ssn#Sensor>.\n" +
                "  }\n" +
                "ORDER BY ASC (?sensorId)";

        QueryExecution qe = QueryExecutionFactory.sparqlService(FUSEKI_SPARQL_ENDPOINT_URL, SPARQL_SENSOR_LIST);
        ResultSet results = qe.execSelect();

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ResultSetFormatter.outputAsJSON(baos, results);

        qe.close();

        String jsonResults = baos.toString();
        jsonResults = jsonResults.replaceAll("http://www.sintef.no/pssn#", "");

        JSONObject jsonResponse = new JSONObject(jsonResults);

        String result = jsonResponse.toString(2);

        // Return HTTP response 200 in case of success
        return Response.status(200).entity(result).build();
    }


    @GET
    @Path("/query/sensor/properties")
    @Produces(MediaType.APPLICATION_JSON)
    public Response querySensorProperties(
            @QueryParam("dataset") String dataset,
            @QueryParam("sensorId") String sensorId
    )
    {
        String FUSEKI_SPARQL_ENDPOINT_URL = getFusekiSparqlEndpointUrl(dataset);

        String SPARQL_SENSOR_PROPERTIES = "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>\n" +
                "PREFIX owl: <http://www.w3.org/2002/07/owl#>\n" +
                "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>\n" +
                "PREFIX xsd: <http://www.w3.org/2001/XMLSchema#>\n" +
                "PREFIX ssn: <http://purl.oclc.org/NET/ssnx/ssn#>\n" +
                "PREFIX pssn: <http://www.sintef.no/pssn#>\n" +
                "\n" +
                "SELECT DISTINCT ?property ?value\n" +
                "  WHERE {\n" +
                "    pssn:SENSORID ?property ?value .\n" +
                "}";

        SPARQL_SENSOR_PROPERTIES = SPARQL_SENSOR_PROPERTIES.replaceAll("SENSORID", sensorId);

        QueryExecution qe = QueryExecutionFactory.sparqlService(FUSEKI_SPARQL_ENDPOINT_URL, SPARQL_SENSOR_PROPERTIES);
        ResultSet results = qe.execSelect();

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ResultSetFormatter.outputAsJSON(baos, results);

        qe.close();

        String jsonResults = baos.toString();
        jsonResults = jsonResults.replaceAll("http://www.sintef.no/pssn#", "");

        JSONObject jsonResponse = new JSONObject();
        JSONObject eventPropertiesNode = new JSONObject();
        JSONArray eventPropertiesArray = new JSONArray();

        ObjectMapper mapper = new ObjectMapper();
        try {
            JsonNode rootNode = mapper.readTree(jsonResults);
            JsonNode resultsNode = rootNode.path("results");
            JsonNode bindingsNode = resultsNode.path("bindings");

            Iterator<JsonNode> iterator = bindingsNode.getElements();
            while (iterator.hasNext()) {
                JsonNode propertyNode = iterator.next();
                JsonNode nameNode = propertyNode.get("property");
                JsonNode valueNode = propertyNode.get("value");

                String propertyName = nameNode.findValuesAsText("value").get(0);
                String propertyValue = valueNode.findValuesAsText("value").get(0);

                if (propertyName.equals("eventProperty")) {
                    JSONObject eventPropertyNode = parseEventProperty(propertyValue);
                    eventPropertiesNode.put("eventProperty", eventPropertyNode);
                    eventPropertiesArray.put(eventPropertiesNode);
                }
                else if (propertyName.equals("http://www.w3.org/1999/02/22-rdf-syntax-ns#type")) {
                    // Ignore
                }
                else
                    jsonResponse.put(propertyName, propertyValue);
            }
        } catch (IOException e) {
            System.out.println(e.getClass().getName() + ": " + e.getMessage());
        }

        jsonResponse.put("eventProperties", eventPropertiesArray);

        String result = jsonResponse.toString(2);

        // Return HTTP response 200 in case of success
        return Response.status(200).entity(result).build();
    }


    @GET
    @Path("/query/sensor/properties2")
    @Produces(MediaType.APPLICATION_JSON)
    public Response querySensorProperties2(
            @QueryParam("dataset") String dataset,
            @QueryParam("sensorId") String sensorId
    )
    {
        String FUSEKI_SPARQL_ENDPOINT_URL = getFusekiSparqlEndpointUrl(dataset);

        String SPARQL_SENSOR_PROPERTIES = "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>\n" +
                "PREFIX owl: <http://www.w3.org/2002/07/owl#>\n" +
                "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>\n" +
                "PREFIX xsd: <http://www.w3.org/2001/XMLSchema#>\n" +
                "PREFIX ssn: <http://purl.oclc.org/NET/ssnx/ssn#>\n" +
                "PREFIX pssn: <http://www.sintef.no/pssn#>\n" +
                "\n" +
                "SELECT DISTINCT ?property ?value\n" +
                "  WHERE {\n" +
                "    pssn:SENSORID ?property ?value .\n" +
                "}";

        SPARQL_SENSOR_PROPERTIES = SPARQL_SENSOR_PROPERTIES.replaceAll("SENSORID", sensorId);

        QueryExecution qe = QueryExecutionFactory.sparqlService(FUSEKI_SPARQL_ENDPOINT_URL, SPARQL_SENSOR_PROPERTIES);
        ResultSet results = qe.execSelect();

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ResultSetFormatter.outputAsJSON(baos, results);

        qe.close();

        String jsonResults = baos.toString();
        jsonResults = jsonResults.replaceAll("http://www.sintef.no/pssn#", "");

        JSONObject jsonResponse = new JSONObject(jsonResults);

        String result = jsonResponse.toString(2);

        // Return HTTP response 200 in case of success
        return Response.status(200).entity(result).build();
    }


    @GET
    @Path("/query/machine/list")
    @Produces(MediaType.APPLICATION_JSON)
    public Response queryMachineList(
        @QueryParam("dataset") String dataset
    )
    {
        String FUSEKI_SPARQL_ENDPOINT_URL = getFusekiSparqlEndpointUrl(dataset);

        String SPARQL_MACHINE_LIST = "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>\n" +
                "PREFIX owl: <http://www.w3.org/2002/07/owl#>\n" +
                "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>\n" +
                "PREFIX xsd: <http://www.w3.org/2001/XMLSchema#>\n" +
                "PREFIX ssn: <http://purl.oclc.org/NET/ssnx/ssn#>\n" +
                "PREFIX pssn: <http://www.sintef.no/pssn#>\n" +
                "\n" +
                "SELECT DISTINCT ?machineId\n" +
                "  WHERE {\n" +
                "    ?subject rdfs:subClassOf+ pssn:Machine .\n" +
                "    ?machineId rdf:type ?subject\n" +
                "  }\n" +
                "ORDER BY ASC (?machineId)";

        QueryExecution qe = QueryExecutionFactory.sparqlService(FUSEKI_SPARQL_ENDPOINT_URL, SPARQL_MACHINE_LIST);
        ResultSet results = qe.execSelect();

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ResultSetFormatter.outputAsJSON(baos, results);

        String jsonResults = baos.toString();
        jsonResults = jsonResults.replaceAll("http://www.sintef.no/pssn#", "");

        qe.close();

        JSONObject jsonResponse = new JSONObject();
        JSONArray machineArray = new JSONArray();

        ObjectMapper mapper = new ObjectMapper();
        try {
            JsonNode rootNode = mapper.readTree(jsonResults);
            JsonNode resultsNode = rootNode.path("results");
            JsonNode bindingsNode = resultsNode.path("bindings");
            Iterator<JsonNode> iterator = bindingsNode.getElements();
            while (iterator.hasNext()) {
                JsonNode xNode = iterator.next();
                List<String> valueNode = xNode.findValuesAsText("value");

                machineArray.put(valueNode.get(0));
            }
        } catch (IOException e) {
            System.out.println(e.getClass().getName() + ": " + e.getMessage());
        }

        jsonResponse.put("machine", machineArray);

        String result = jsonResponse.toString(2);

        // Return HTTP response 200 in case of success
        return Response.status(200).entity(result).build();
    }


    @GET
    @Path("/query/machine/list2")
    @Produces(MediaType.APPLICATION_JSON)
    public Response queryMachineList2(
        @QueryParam("dataset") String dataset
    )
    {
        String FUSEKI_SPARQL_ENDPOINT_URL = getFusekiSparqlEndpointUrl(dataset);

        String SPARQL_MACHINE_LIST = "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>\n" +
                "PREFIX owl: <http://www.w3.org/2002/07/owl#>\n" +
                "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>\n" +
                "PREFIX xsd: <http://www.w3.org/2001/XMLSchema#>\n" +
                "PREFIX ssn: <http://purl.oclc.org/NET/ssnx/ssn#>\n" +
                "PREFIX pssn: <http://www.sintef.no/pssn#>\n" +
                "\n" +
                "SELECT DISTINCT ?machineId\n" +
                "  WHERE {\n" +
                "    ?subject rdfs:subClassOf+ pssn:Machine .\n" +
                "    ?machineId rdf:type ?subject\n" +
                "  }\n" +
                "ORDER BY ASC (?machineId)";

        QueryExecution qe = QueryExecutionFactory.sparqlService(FUSEKI_SPARQL_ENDPOINT_URL, SPARQL_MACHINE_LIST);
        ResultSet results = qe.execSelect();

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ResultSetFormatter.outputAsJSON(baos, results);

        qe.close();

        String jsonResults = baos.toString();
        jsonResults = jsonResults.replaceAll("http://www.sintef.no/pssn#", "");

        JSONObject jsonResponse = new JSONObject(jsonResults);

        String result = jsonResponse.toString(2);

        // Return HTTP response 200 in case of success
        return Response.status(200).entity(result).build();
    }


    @GET
    @Path("/query/machine/properties")
    @Produces(MediaType.APPLICATION_JSON)
    public Response queryMachineProperties(
            @QueryParam("dataset") String dataset,
            @QueryParam("machineId") String machineId
    )
    {
        String FUSEKI_SPARQL_ENDPOINT_URL = getFusekiSparqlEndpointUrl(dataset);

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

        QueryExecution qe = QueryExecutionFactory.sparqlService(FUSEKI_SPARQL_ENDPOINT_URL, SPARQL_MACHINE_PROPERTIES);
        ResultSet results = qe.execSelect();

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ResultSetFormatter.outputAsJSON(baos, results);

        qe.close();

        String jsonResults = baos.toString();
        jsonResults = jsonResults.replaceAll("http://www.sintef.no/pssn#", "");

        JSONObject jsonResponse = new JSONObject(jsonResults);

        String result = jsonResponse.toString(2);

        // Return HTTP response 200 in case of success
        return Response.status(200).entity(result).build();
    }


    @GET
    @Path("/query/product/list")
    @Produces(MediaType.APPLICATION_JSON)
    public Response queryProductList(
        @QueryParam("dataset") String dataset
    )
    {
        String FUSEKI_SPARQL_ENDPOINT_URL = getFusekiSparqlEndpointUrl(dataset);

        String SPARQL_PRODUCT_LIST = "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>\n" +
                "PREFIX owl: <http://www.w3.org/2002/07/owl#>\n" +
                "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>\n" +
                "PREFIX xsd: <http://www.w3.org/2001/XMLSchema#>\n" +
                "PREFIX ssn: <http://purl.oclc.org/NET/ssnx/ssn#>\n" +
                "PREFIX pssn: <http://www.sintef.no/pssn#>\n" +
                "\n" +
                "SELECT DISTINCT ?productId\n" +
                "  WHERE {\n" +
                "    ?productId rdf:type pssn:Product .\n" +
                "  }\n" +
                "ORDER BY ASC (?productId)";

        QueryExecution qe = QueryExecutionFactory.sparqlService(FUSEKI_SPARQL_ENDPOINT_URL, SPARQL_PRODUCT_LIST);
        ResultSet results = qe.execSelect();

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ResultSetFormatter.outputAsJSON(baos, results);

        qe.close();

        String jsonResults = baos.toString();
        jsonResults = jsonResults.replaceAll("http://www.sintef.no/pssn#", "");

        JSONObject jsonResponse = new JSONObject();
        JSONArray productArray = new JSONArray();

        ObjectMapper mapper = new ObjectMapper();
        try {
            JsonNode rootNode = mapper.readTree(jsonResults);
            JsonNode resultsNode = rootNode.path("results");
            JsonNode bindingsNode = resultsNode.path("bindings");
            Iterator<JsonNode> iterator = bindingsNode.getElements();
            while (iterator.hasNext()) {
                JsonNode xNode = iterator.next();
                List<String> valueNode = xNode.findValuesAsText("value");

                productArray.put(valueNode.get(0));
            }
        } catch (IOException e) {
            System.out.println(e.getClass().getName() + ": " + e.getMessage());
        }

        jsonResponse.put("product", productArray);

        String result = jsonResponse.toString(2);

        // Return HTTP response 200 in case of success
        return Response.status(200).entity(result).build();
    }


    @GET
    @Path("/query/product/list2")
    @Produces(MediaType.APPLICATION_JSON)
    public Response queryProductList2(
        @QueryParam("dataset") String dataset
    )
    {
        String FUSEKI_SPARQL_ENDPOINT_URL = getFusekiSparqlEndpointUrl(dataset);

        String SPARQL_PRODUCT_LIST = "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>\n" +
                "PREFIX owl: <http://www.w3.org/2002/07/owl#>\n" +
                "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>\n" +
                "PREFIX xsd: <http://www.w3.org/2001/XMLSchema#>\n" +
                "PREFIX ssn: <http://purl.oclc.org/NET/ssnx/ssn#>\n" +
                "PREFIX pssn: <http://www.sintef.no/pssn#>\n" +
                "\n" +
                "SELECT DISTINCT ?productId\n" +
                "  WHERE {\n" +
                "    ?productId rdf:type pssn:Product .\n" +
                "  }\n" +
                "ORDER BY ASC (?productId)";

        QueryExecution qe = QueryExecutionFactory.sparqlService(FUSEKI_SPARQL_ENDPOINT_URL, SPARQL_PRODUCT_LIST);
        ResultSet results = qe.execSelect();

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ResultSetFormatter.outputAsJSON(baos, results);

        qe.close();

        String jsonResults = baos.toString();
        jsonResults = jsonResults.replaceAll("http://www.sintef.no/pssn#", "");

        JSONObject jsonResponse = new JSONObject(jsonResults);

        String result = jsonResponse.toString(2);

        // Return HTTP response 200 in case of success
        return Response.status(200).entity(result).build();
    }


    @GET
    @Path("/query/product/properties")
    @Produces(MediaType.APPLICATION_JSON)
    public Response queryProductProperties(
            @QueryParam("dataset") String dataset,
            @QueryParam("productId") String productId
    )
    {
        String FUSEKI_SPARQL_ENDPOINT_URL = getFusekiSparqlEndpointUrl(dataset);

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

        QueryExecution qe = QueryExecutionFactory.sparqlService(FUSEKI_SPARQL_ENDPOINT_URL, SPARQL_PRODUCT_PROPERTIES);
        ResultSet results = qe.execSelect();

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ResultSetFormatter.outputAsJSON(baos, results);

        qe.close();

        String jsonResults = baos.toString();
        jsonResults = jsonResults.replaceAll("http://www.sintef.no/pssn#", "");

        JSONObject jsonResponse = new JSONObject(jsonResults);

        String result = jsonResponse.toString(2);

        // Return HTTP response 200 in case of success
        return Response.status(200).entity(result).build();
    }


    @GET
    @Path("/query/mould/list")
    @Produces(MediaType.APPLICATION_JSON)
    public Response queryMouldList(
        @QueryParam("dataset") String dataset
    )
    {
        String FUSEKI_SPARQL_ENDPOINT_URL = getFusekiSparqlEndpointUrl(dataset);

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
                "ORDER BY ASC (?mouldId)";

        QueryExecution qe = QueryExecutionFactory.sparqlService(FUSEKI_SPARQL_ENDPOINT_URL, SPARQL_MOULD_LIST);
        ResultSet results = qe.execSelect();

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ResultSetFormatter.outputAsJSON(baos, results);

        qe.close();

        String jsonResults = baos.toString();
        jsonResults = jsonResults.replaceAll("http://www.sintef.no/pssn#", "");

        JSONObject jsonResponse = new JSONObject();
        JSONArray mouldArray = new JSONArray();

        ObjectMapper mapper = new ObjectMapper();
        try {
            JsonNode rootNode = mapper.readTree(jsonResults);
            JsonNode resultsNode = rootNode.path("results");
            JsonNode bindingsNode = resultsNode.path("bindings");
            Iterator<JsonNode> iterator = bindingsNode.getElements();
            while (iterator.hasNext()) {
                JsonNode xNode = iterator.next();
                List<String> valueNode = xNode.findValuesAsText("value");

                mouldArray.put(valueNode.get(0));
            }
        } catch (IOException e) {
            System.out.println(e.getClass().getName() + ": " + e.getMessage());
        }

        jsonResponse.put("mould", mouldArray);

        String result = jsonResponse.toString(2);

        // Return HTTP response 200 in case of success
        return Response.status(200).entity(result).build();
    }


    @GET
    @Path("/query/mould/list2")
    @Produces(MediaType.APPLICATION_JSON)
    public Response queryMouldList2(
        @QueryParam("dataset") String dataset
    )
    {
        String FUSEKI_SPARQL_ENDPOINT_URL = getFusekiSparqlEndpointUrl(dataset);

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
                "ORDER BY ASC (?mouldId)";

        QueryExecution qe = QueryExecutionFactory.sparqlService(FUSEKI_SPARQL_ENDPOINT_URL, SPARQL_MOULD_LIST);
        ResultSet results = qe.execSelect();

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ResultSetFormatter.outputAsJSON(baos, results);

        qe.close();

        String jsonResults = baos.toString();
        jsonResults = jsonResults.replaceAll("http://www.sintef.no/pssn#", "");

        JSONObject jsonResponse = new JSONObject(jsonResults);

        String result = jsonResponse.toString(2);

        // Return HTTP response 200 in case of success
        return Response.status(200).entity(result).build();
    }


    @GET
    @Path("/query/mould/properties")
    @Produces(MediaType.APPLICATION_JSON)
    public Response queryMouldProperties(
            @QueryParam("dataset") String dataset,
            @QueryParam("mouldId") String productId
    )
    {
        String FUSEKI_SPARQL_ENDPOINT_URL = getFusekiSparqlEndpointUrl(dataset);

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

        QueryExecution qe = QueryExecutionFactory.sparqlService(FUSEKI_SPARQL_ENDPOINT_URL, SPARQL_MOULD_PROPERTIES);
        ResultSet results = qe.execSelect();

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ResultSetFormatter.outputAsJSON(baos, results);

        qe.close();

        String jsonResults = baos.toString();
        jsonResults = jsonResults.replaceAll("http://www.sintef.no/pssn#", "");

        JSONObject jsonResponse = new JSONObject(jsonResults);

        String result = jsonResponse.toString(2);

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


    private String getFusekiSparqlEndpointUrl(String dataset) {
        String FUSEKI_SPARQL_ENDPOINT_URL;

        if (dataset == null)
            FUSEKI_SPARQL_ENDPOINT_URL = this.FUSEKI_SPARQL_ENDPOINT_DEFAULT;
        else if (dataset.equals(this.FUSEKI_DATASET_HELLA))
            FUSEKI_SPARQL_ENDPOINT_URL = this.FUSEKI_SPARQL_ENDPOINT_HELLA;
        else if (dataset.equals(this.FUSEKI_DATASET_MHWIRTH))
            FUSEKI_SPARQL_ENDPOINT_URL = this.FUSEKI_SPARQL_ENDPOINT_MHWIRTH;
        else
            FUSEKI_SPARQL_ENDPOINT_URL = this.FUSEKI_SPARQL_ENDPOINT_DEFAULT;

        return FUSEKI_SPARQL_ENDPOINT_URL;
    }


    private JSONObject parseEventProperty(String valueString) {
        String[] properties = valueString.split(",");
        String name = properties[0];
        String type = properties[1];
        String partition = properties[2];

        JSONObject eventPropertyNode = new JSONObject();
        eventPropertyNode.put("name", name);
        eventPropertyNode.put("type", type);
        eventPropertyNode.put("partition", partition);

        return eventPropertyNode;
    }

}
