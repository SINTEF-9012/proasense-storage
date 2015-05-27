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
package net.modelbased.proasense.rest;

import net.modelbased.proasense.model.JsonPrinter;
import net.modelbased.proasense.model.Sensor;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;

import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;

import org.apache.http.entity.StringEntity;

import org.apache.http.impl.client.DefaultHttpClient;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import java.net.URI;
import java.net.URISyntaxException;

import java.nio.charset.Charset;


public class RegisterSensorSSN {
    public static final String SENSOR_PATH = "/sensapp/registry/sensors";
    public static final String COMPOSITE_PATH = "/sensapp/registry/composite/sensors";

    public RegisterSensorSSN() {
        //
    }

    public static String postSensor(Sensor sensor) throws RequestErrorException {
        String content = JsonPrinter.sensorToJson(sensor);
        URI target;
        try {
            target = new URI(sensor.getUri().toString() + SENSOR_PATH);
        } catch (URISyntaxException e1) {
            e1.printStackTrace();
            throw new RequestErrorException(e1.getMessage());
        }
        HttpClient client = new DefaultHttpClient();
        HttpPost request = new HttpPost(target);
        request.setHeader("Content-type", "application/json");
        String response = null;
        try {
            StringEntity seContent = new StringEntity(content);
            seContent.setContentType("text/json");
            request.setEntity(seContent);
            response = resolveResponse(client.execute(request));
        } catch (Exception e) {
            throw new RequestErrorException(e.getMessage());
        }
        return response;
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

    private static String resolveResponse(HttpResponse response) throws RequestErrorException {
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
        if (status.getStatusCode() == 200) {
            return result;
        } else if (status.getStatusCode() == 409) {
//            Log.w(TAG, status.toString());
//            Log.w(TAG, result);
            return result;
        } else {
            throw new RequestErrorException("Invalid response from server: " + result, new IOException(status.toString()), status.getStatusCode());
        }
    }

}
