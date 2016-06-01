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
package net.modelbased.proasense.storage.registry;

import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.query.ResultSetFormatter;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;

import org.apache.http.message.BasicNameValuePair;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;

public class StorageRegistryScrapRateTestClient {
    private Properties clientProperties;


    public StorageRegistryScrapRateTestClient() {
    }


    private Properties loadClientProperties() {
        clientProperties = new Properties();
        String propFilename = "client.properties";
        InputStream inputStream = getClass().getClassLoader().getResourceAsStream(propFilename);

        try {
            if (inputStream != null) {
                clientProperties.load(inputStream);
            } else
                throw new FileNotFoundException("Property file: '" + propFilename + "' not found in classpath.");
        } catch (IOException e) {
            System.out.println("Exception:" + e.getMessage());
        }

        return clientProperties;
    }


    public static void main(String[] args) {
        // Get client properties from properties file
//        StorageReaderMongoServiceTestClient client = new StorageReaderMongoServiceTestClient();
//        client.loadClientProperties();

        // Hardcoded client properties (simple test client)
        String STORAGE_REGISTRY_SERVICE_URL = "http://192.168.84.34:8080/storage-registry";

        // Default HTTP client and common properties for requests
        HttpClient client = new DefaultHttpClient();
        StringBuilder requestUrl = null;
        List<NameValuePair> params = null;
        String queryString = null;

        // Default HTTP response and common properties for responses
        HttpResponse response = null;
        ResponseHandler<String> handler = null;
        int status = 0;
        String body = null;

        // Query for machine list
        requestUrl = new StringBuilder(STORAGE_REGISTRY_SERVICE_URL);
        requestUrl.append("/query/machine/list");

        try {
            HttpGet query11 = new HttpGet(requestUrl.toString());
            query11.setHeader("Content-type", "application/json");
            response = client.execute(query11);

            // Check status code
            status = response.getStatusLine().getStatusCode();
            if (status != 200) {
                throw new RuntimeException("Failed! HTTP error code: " + status);
            }

            // Get body
            handler = new BasicResponseHandler();
            body = handler.handleResponse(response);

            System.out.println("MACHINE LIST: " + body);
        } catch (Exception e) {
            System.out.println(e.getClass().getName() + ": " + e.getMessage());
        }

        // Query for machine properties
        requestUrl = new StringBuilder(STORAGE_REGISTRY_SERVICE_URL);
        requestUrl.append("/query/machine/list");

        params = new LinkedList<NameValuePair>();
        params.add(new BasicNameValuePair("machineId", "IMM1"));

        queryString = URLEncodedUtils.format(params, "utf-8");
        requestUrl.append("?");
        requestUrl.append(queryString);

        try {
            HttpGet query12 = new HttpGet(requestUrl.toString());
            query12.setHeader("Content-type", "application/json");
            response = client.execute(query12);

            // Check status code
            status = response.getStatusLine().getStatusCode();
            if (status != 200) {
                throw new RuntimeException("Failed! HTTP error code: " + status);
            }

            // Get body
            handler = new BasicResponseHandler();
            body = handler.handleResponse(response);

            System.out.println("MACHINE PROPERTIES: " + body);
        } catch (Exception e) {
            System.out.println(e.getClass().getName() + ": " + e.getMessage());
        }

        // Query for sensor list
        requestUrl = new StringBuilder(STORAGE_REGISTRY_SERVICE_URL);
        requestUrl.append("/query/sensor/list");

        try {
            HttpGet query21 = new HttpGet(requestUrl.toString());
            query21.setHeader("Content-type", "application/json");
            response = client.execute(query21);

            // Check status code
            status = response.getStatusLine().getStatusCode();
            if (status != 200) {
                throw new RuntimeException("Failed! HTTP error code: " + status);
            }

            // Get body
            handler = new BasicResponseHandler();
            body = handler.handleResponse(response);

            System.out.println("SENSOR LIST: " + body);
        } catch (Exception e) {
            System.out.println(e.getClass().getName() + ": " + e.getMessage());
        }

        // Query for sensor properties
        requestUrl = new StringBuilder(STORAGE_REGISTRY_SERVICE_URL);
        requestUrl.append("/query/sensor/properties");

        params = new LinkedList<NameValuePair>();
        params.add(new BasicNameValuePair("sensorId", "dustParticleSensor"));

        queryString = URLEncodedUtils.format(params, "utf-8");
        requestUrl.append("?");
        requestUrl.append(queryString);

        try {
            HttpGet query22 = new HttpGet(requestUrl.toString());
            query22.setHeader("Content-type", "application/json");
            response = client.execute(query22);

            // Check status code
            status = response.getStatusLine().getStatusCode();
            if (status != 200) {
                throw new RuntimeException("Failed! HTTP error code: " + status);
            }

            // Get body
            handler = new BasicResponseHandler();
            body = handler.handleResponse(response);

            System.out.println("SENSOR PROPERTIES: " + body);
        } catch (Exception e) {
            System.out.println(e.getClass().getName() + ": " + e.getMessage());
        }

        // Query for product list
        requestUrl = new StringBuilder(STORAGE_REGISTRY_SERVICE_URL);
        requestUrl.append("/query/product/list");

        try {
            HttpGet query31 = new HttpGet(requestUrl.toString());
            query31.setHeader("Content-type", "application/json");
            response = client.execute(query31);

            // Check status code
            status = response.getStatusLine().getStatusCode();
            if (status != 200) {
                throw new RuntimeException("Failed! HTTP error code: " + status);
            }

            // Get body
            handler = new BasicResponseHandler();
            body = handler.handleResponse(response);

            System.out.println("PRODUCT LIST: " + body);
        } catch (Exception e) {
            System.out.println(e.getClass().getName() + ": " + e.getMessage());
        }

        // Query for product properties
        requestUrl = new StringBuilder(STORAGE_REGISTRY_SERVICE_URL);
        requestUrl.append("/query/product/properties");

        params = new LinkedList<NameValuePair>();
        params.add(new BasicNameValuePair("productId", "Astra_3300"));

        queryString = URLEncodedUtils.format(params, "utf-8");
        requestUrl.append("?");
        requestUrl.append(queryString);

        try {
            HttpGet query22 = new HttpGet(requestUrl.toString());
            query22.setHeader("Content-type", "application/json");
            response = client.execute(query22);

            // Check status code
            status = response.getStatusLine().getStatusCode();
            if (status != 200) {
                throw new RuntimeException("Failed! HTTP error code: " + status);
            }

            // Get body
            handler = new BasicResponseHandler();
            body = handler.handleResponse(response);

            System.out.println("PRODUCT PROPERTIES: " + body);
        } catch (Exception e) {
            System.out.println(e.getClass().getName() + ": " + e.getMessage());
        }

        /****************************************************/
        // Hardcoded client properties (simple test client)
        String FUSEKI_SPARQL_ENDPOINT = "http://192.168.84.88:8080/fuseki/ProaSenseV8/query";

        // Default Fuseki client and common properties for requests
        QueryExecution qe = null;

        // Common properties for responses
        ResultSet results = null;

        // Query for mould list
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
        qe = QueryExecutionFactory.sparqlService(FUSEKI_SPARQL_ENDPOINT, SPARQL_MOULD_LIST);
        results = qe.execSelect();
        ResultSetFormatter.out(System.out, results);
        qe.close();

        // Query for mould properties
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
        qe = QueryExecutionFactory.sparqlService(FUSEKI_SPARQL_ENDPOINT, SPARQL_MOULD_PROPERTIES);
        results = qe.execSelect();
        ResultSetFormatter.out(System.out, results);
        qe.close();
    }
}