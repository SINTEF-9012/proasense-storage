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
package net.modelbased.proasense.storage.reader;

import net.modelbased.proasense.storage.EventConverter;
import net.modelbased.proasense.storage.EventProperties;

import eu.proasense.internal.SimpleEvent;

import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.message.BasicNameValuePair;
import org.bson.Document;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;


public class StorageReaderMongoServiceRestBenchmark {
    private Properties clientProperties;


    public StorageReaderMongoServiceRestBenchmark() {
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
        }
        catch (IOException e) {
            System.out.println("Exception:" + e.getMessage());
        }

        return clientProperties;
    }


    private HttpGet createGetRequest(String serviceURL, LinkedList<NameValuePair> params) {
        String queryString = URLEncodedUtils.format(params, "utf-8");

        StringBuilder requestUrl = new StringBuilder(serviceURL);
        requestUrl.append("?");
        requestUrl.append(queryString);

        return new HttpGet(requestUrl.toString());
    }


    public static void main(String[] args) {
        // Get benchmark properties
        StorageReaderMongoServiceRestBenchmark benchmark = new StorageReaderMongoServiceRestBenchmark();
        benchmark.loadClientProperties();

        // ProaSense Storage Reader Service configuration properties
        String STORAGE_READER_SERVICE_URL = benchmark.clientProperties.getProperty("proasense.storage.reader.service.url");

        String QUERY_SIMPLE_COLLECTIONID = benchmark.clientProperties.getProperty("proasense.benchmark.query.simple.collectionid");
        String NO_QUERY_SIMPLE_STARTTIME = benchmark.clientProperties.getProperty("proasense.benchmark.query.simple.starttime");
        String NO_QUERY_SIMPLE_ENDTIME = benchmark.clientProperties.getProperty("proasense.benchmark.query.simple.endtime");

        String QUERY_DERIVED_COLLECTIONID = benchmark.clientProperties.getProperty("proasense.benchmark.query.derived.collectionid");
        String NO_QUERY_DERIVED_STARTTIME = benchmark.clientProperties.getProperty("proasense.benchmark.query.derived.starttime");
        String NO_QUERY_DERIVED_ENDTIME = benchmark.clientProperties.getProperty("proasense.benchmark.query.derived.endtime");

        String QUERY_PREDICTED_COLLECTIONID = benchmark.clientProperties.getProperty("proasense.benchmark.query.predicted.collectionid");
        String NO_QUERY_PREDICTED_STARTTIME = benchmark.clientProperties.getProperty("proasense.benchmark.query.predicted.starttime");
        String NO_QUERY_PREDICTED_ENDTIME = benchmark.clientProperties.getProperty("proasense.benchmark.query.predicted.endtime");

        String QUERY_ANOMALY_COLLECTIONID = benchmark.clientProperties.getProperty("proasense.benchmark.query.anomaly.collectionid");
        String NO_QUERY_ANOMALY_STARTTIME = benchmark.clientProperties.getProperty("proasense.benchmark.query.anomaly.starttime");
        String NO_QUERY_ANOMALY_ENDTIME = benchmark.clientProperties.getProperty("proasense.benchmark.query.anomaly.endtime");

        String QUERY_RECOMMENDATION_COLLECTIONID = benchmark.clientProperties.getProperty("proasense.benchmark.query.recommendation.collectionid");
        String NO_QUERY_RECOMMENDATION_STARTTIME = benchmark.clientProperties.getProperty("proasense.benchmark.query.recommendation.starttime");
        String NO_QUERY_RECOMMENDATION_ENDTIME = benchmark.clientProperties.getProperty("proasense.benchmark.query.recommendation.endtime");

        String QUERY_FEEDBACK_COLLECTIONID = benchmark.clientProperties.getProperty("proasense.benchmark.query.feedback.collectionid");
        String NO_QUERY_FEEDBACK_STARTTIME = benchmark.clientProperties.getProperty("proasense.benchmark.query.feedback.starttime");
        String NO_QUERY_FEEDBACK_ENDTIME = benchmark.clientProperties.getProperty("proasense.benchmark.query.feedback.endtime");

        String propertyKey = "value";

        // Default HTTP client and common property variables for requests
        HttpClient client = new DefaultHttpClient();
        StringBuilder requestUrl = null;
        List<NameValuePair> params = null;
        String queryString = null;
        StatusLine status = null;

        // Default query for simple events
        requestUrl = new StringBuilder(STORAGE_READER_SERVICE_URL);
        requestUrl.append("/query/simple/default");

        params = new LinkedList<NameValuePair>();
        params.add(new BasicNameValuePair("sensorId", QUERY_SIMPLE_COLLECTIONID));
        params.add(new BasicNameValuePair("startTime", NO_QUERY_SIMPLE_STARTTIME));
        params.add(new BasicNameValuePair("endTime", NO_QUERY_SIMPLE_ENDTIME));

        queryString = URLEncodedUtils.format(params, "utf-8");
        requestUrl.append("?");
        requestUrl.append(queryString);

        HttpGet query11 = new HttpGet(requestUrl.toString());
        query11.setHeader("Content-type", "application/json");
        try {
            status = client.execute(query11).getStatusLine();
            System.out.println("SIMPLE.DEFAULT:" + status.toString());
        } catch (Exception e) {
            System.out.println(e.getClass().getName() + ": " + e.getMessage());
        }

        // Average query for simple events
        requestUrl = new StringBuilder(STORAGE_READER_SERVICE_URL);
        requestUrl.append("/query/simple/value");

        params = new LinkedList<NameValuePair>();
        params.add(new BasicNameValuePair("sensorId", QUERY_SIMPLE_COLLECTIONID));
        params.add(new BasicNameValuePair("startTime", NO_QUERY_SIMPLE_STARTTIME));
        params.add(new BasicNameValuePair("endTime", NO_QUERY_SIMPLE_ENDTIME));
        params.add(new BasicNameValuePair("propertyKey", "value"));

        queryString = URLEncodedUtils.format(params, "utf-8");
        requestUrl.append("?");
        requestUrl.append(queryString);

        HttpGet query12 = new HttpGet(STORAGE_READER_SERVICE_URL);
        query12.setHeader("Content-type", "application/json");
        try {
            status = client.execute(query12).getStatusLine();
            System.out.println("SIMPLE.AVERAGE:" + status.toString());
        } catch (Exception e) {
            System.out.println(e.getClass().getName() + ": " + e.getMessage());
        }

        // Maximum query for simple events
        HttpGet query13 = new HttpGet(STORAGE_READER_SERVICE_URL);
        query13.setHeader("Content-type", "application/json");
        try {
            status = client.execute(query13).getStatusLine();
            System.out.println("SIMPLE.MAXIMUM:" + status.toString());
        } catch (Exception e) {
            System.out.println(e.getClass().getName() + ": " + e.getMessage());
        }

        // Minimum query for simple events
        HttpGet query14 = new HttpGet(STORAGE_READER_SERVICE_URL);
        query14.setHeader("Content-type", "application/json");
        try {
            status = client.execute(query14).getStatusLine();
            System.out.println("SIMPLE.MINUMUM:" + status.toString());
        } catch (Exception e) {
            System.out.println(e.getClass().getName() + ": " + e.getMessage());
        }

        // Default query for derived events
        HttpGet query21 = new HttpGet(STORAGE_READER_SERVICE_URL);
        query21.setHeader("Content-type", "application/json");
        try {
            status = client.execute(query21).getStatusLine();
            System.out.println("DERIVED.DEFAULT:" + status.toString());
        } catch (Exception e) {
            System.out.println(e.getClass().getName() + ": " + e.getMessage());
        }

        // Average query for derived events
        HttpGet query22 = new HttpGet(STORAGE_READER_SERVICE_URL);
        query22.setHeader("Content-type", "application/json");
        try {
            status = client.execute(query22).getStatusLine();
            System.out.println("DERIVED.AVERAGE:" + status.toString());
        } catch (Exception e) {
            System.out.println(e.getClass().getName() + ": " + e.getMessage());
        }

        // Maximum query for derived events
        HttpGet query23 = new HttpGet(STORAGE_READER_SERVICE_URL);
        query23.setHeader("Content-type", "application/json");
        try {
            status = client.execute(query23).getStatusLine();
            System.out.println("DERIVED.MAXIMUM: " + status.toString());
        } catch (Exception e) {
            System.out.println(e.getClass().getName() + ": " + e.getMessage());
        }

        // Minimum query for derived events
        HttpGet query24 = new HttpGet(STORAGE_READER_SERVICE_URL);
        query24.setHeader("Content-type", "application/json");
        try {
            status = client.execute(query24).getStatusLine();
            System.out.println("DERIVED.MINIMUM: " + status.toString());
        } catch (Exception e) {
            System.out.println(e.getClass().getName() + ": " + e.getMessage());
        }

        // Default query for predicted events
        HttpGet query31 = new HttpGet(STORAGE_READER_SERVICE_URL);
        query31.setHeader("Content-type", "application/json");
        try {
            status = client.execute(query31).getStatusLine();
            System.out.println("PREDICTED.DEFAULT: " + status.toString());
        } catch (Exception e) {
            System.out.println(e.getClass().getName() + ": " + e.getMessage());
        }

        // Average query for predicted events
        HttpGet query32 = new HttpGet(STORAGE_READER_SERVICE_URL);
        query32.setHeader("Content-type", "application/json");
        try {
            status = client.execute(query32).getStatusLine();
            System.out.println("PREDICTED.AVERAGE: " + status.toString());
        } catch (Exception e) {
            System.out.println(e.getClass().getName() + ": " + e.getMessage());
        }

        // Maximum query for predicted events
        HttpGet query33 = new HttpGet(STORAGE_READER_SERVICE_URL);
        query33.setHeader("Content-type", "application/json");
        try {
            status = client.execute(query33).getStatusLine();
            System.out.println("PREDICTED.MAXIMUM: " + status.toString());
        } catch (Exception e) {
            System.out.println(e.getClass().getName() + ": " + e.getMessage());
        }

        // Minimum query for derived events
        HttpGet query34 = new HttpGet(STORAGE_READER_SERVICE_URL);
        query34.setHeader("Content-type", "application/json");
        try {
            status = client.execute(query34).getStatusLine();
            System.out.println("PREDICTED.MINIMUM: " + status.toString());
        } catch (Exception e) {
            System.out.println(e.getClass().getName() + ": " + e.getMessage());
        }

        // Default query for anomaly events
        HttpGet query41 = new HttpGet(STORAGE_READER_SERVICE_URL);
        query41.setHeader("Content-type", "application/json");
        try {
            status = client.execute(query41).getStatusLine();
            System.out.println("ANOMALY.DEFAULT: " + status.toString());
        } catch (Exception e) {
            System.out.println(e.getClass().getName() + ": " + e.getMessage());
        }

        // Default query for recommendation events
        HttpGet query51 = new HttpGet(STORAGE_READER_SERVICE_URL);
        query51.setHeader("Content-type", "application/json");
        try {
            status = client.execute(query51).getStatusLine();
            System.out.println("RECOMMENDATION.DEFAULT: " + status.toString());
        } catch (Exception e) {
            System.out.println(e.getClass().getName() + ": " + e.getMessage());
        }

        // Average query for recommendation events
        HttpGet query52 = new HttpGet(STORAGE_READER_SERVICE_URL);
        query52.setHeader("Content-type", "application/json");
        try {
            status = client.execute(query52).getStatusLine();
            System.out.println("RECOMMENDATION.AVERAGE: " + status.toString());
        } catch (Exception e) {
            System.out.println(e.getClass().getName() + ": " + e.getMessage());
        }

        // Maximum query for derived events
        HttpGet query53 = new HttpGet(STORAGE_READER_SERVICE_URL);
        query53.setHeader("Content-type", "application/json");
        try {
            status = client.execute(query53).getStatusLine();
            System.out.println("RECOMMENDATION.MAXIMUM: " + status.toString());
        } catch (Exception e) {
            System.out.println(e.getClass().getName() + ": " + e.getMessage());
        }

        // Minimum query for derived events
        HttpGet query54 = new HttpGet(STORAGE_READER_SERVICE_URL);
        query54.setHeader("Content-type", "application/json");
        try {
            status = client.execute(query54).getStatusLine();
            System.out.println("RECOMMENDATION.MINIMUM: " + status.toString());
        } catch (Exception e) {
            System.out.println(e.getClass().getName() + ": " + e.getMessage());
        }

        // Default query for feedback events
        HttpGet query61 = new HttpGet(STORAGE_READER_SERVICE_URL);
        query54.setHeader("Content-type", "application/json");
        try {
            status = client.execute(query54).getStatusLine();
            System.out.println("FEEDBACK.DEFAULT: " + status.toString());
        } catch (Exception e) {
            System.out.println(e.getClass().getName() + ": " + e.getMessage());
        }
    }

}
