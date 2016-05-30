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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URISyntaxException;

import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.query.ResultSetFormatter;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.RDFNode;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.Callable;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;


public class SparqlQueryCall implements Callable {
    private String sparqlEndpoint;
    private String sparqlQuery;


    public SparqlQueryCall(String sparqlEndpoint, String sparqlQuery) {
        this.sparqlEndpoint = sparqlEndpoint;
        this.sparqlQuery = sparqlQuery;
    }


    public String call() {
        // Connect to Apache Fuseki
/**
        URI target = null;
        try {
            target = new URI(sensor.getUri().toString() + SENSOR_PATH + "/" + sensor.getName());
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
**/
        HttpClient client = new DefaultHttpClient();
        HttpGet request = new HttpGet(sparqlEndpoint);
        request.setHeader("Content-type", "application/json");
        StatusLine status = null;
        try {
            status = client.execute(request).getStatusLine();
        } catch (Exception e) {
        }

        //Query the collection, dump output
        QueryExecution qe = QueryExecutionFactory.sparqlService(
                "http://localhost:3030/ds/query", "SELECT * WHERE {?x ?r ?y}");
        ResultSet results = qe.execSelect();
        ResultSetFormatter.out(System.out, results);
        qe.close();

        // Create string list for query result
//        String queryResults = new ArrayList<String>();

        return results.toString();
    }


    private static String convertStreamToString(InputStream is) {
        BufferedReader reader = new BufferedReader(new InputStreamReader(is, Charset.forName("UTF-8")));
        StringBuilder sb = new StringBuilder();
        String line = null;
        try {
            while ((line = reader.readLine()) != null) {
                sb.append(line + "\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return sb.toString();
    }


    private static String resolveResponse(HttpResponse response) {
        StatusLine status = response.getStatusLine();
        HttpEntity entity = response.getEntity();
        String result = null;
        if (entity != null) {
            InputStream inputStream = null;
            try {
                inputStream = entity.getContent();
                result = convertStreamToString(inputStream);
            } catch (IllegalStateException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (inputStream != null) {
                    try {
                        inputStream.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }

        return result;
    }

}
