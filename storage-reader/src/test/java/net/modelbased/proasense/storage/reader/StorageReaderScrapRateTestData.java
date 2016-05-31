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

import eu.proasense.internal.ComplexValue;
import eu.proasense.internal.SimpleEvent;
import eu.proasense.internal.VariableType;
import org.apache.thrift.TDeserializer;
import org.apache.thrift.TException;
import org.apache.thrift.TSerializer;
import org.apache.thrift.protocol.TJSONProtocol;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;


public class StorageReaderScrapRateTestData {
    private Properties clientProperties;
    private Properties testDataProperties;


    public StorageReaderScrapRateTestData() {
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


    private Properties loadTestDataProperties() {
         testDataProperties = new Properties();
        String propFilename = "testdata.properties";

        InputStream inputStream = getClass().getClassLoader().getResourceAsStream(propFilename);

        try {
            if (inputStream != null) {
                testDataProperties.load(inputStream);
            } else
                throw new FileNotFoundException("Property file: '" + propFilename + "' not found in classpath.");
        }
        catch (IOException e) {
            System.out.println("Exception:" + e.getMessage());
        }

        return testDataProperties;
    }


    public static void main(String[] args) {
        // Get client properties from properties file
        StorageReaderScrapRateTestData client = new StorageReaderScrapRateTestData();
        client.loadClientProperties();
        client.loadTestDataProperties();

        // Convert test data properties to Simple Events
        List<SimpleEvent> events = new ArrayList<SimpleEvent>();
        String[] S_TESTDATA_PROPERTIES = client.testDataProperties.getProperty("proasense.storage.scrap.testdata").split(",");
        if ((S_TESTDATA_PROPERTIES.length % 7) == 0) {
            int i = 0;
            while (i < S_TESTDATA_PROPERTIES.length) {
                SimpleEvent event = new SimpleEvent();
                event.setTimestamp(new Long(S_TESTDATA_PROPERTIES[i]));

                event.setSensorId(S_TESTDATA_PROPERTIES[i+1]);

                Map<String, ComplexValue> properties = new HashMap<String, ComplexValue>();

                ComplexValue complexValue = new ComplexValue();
                complexValue.setValue(S_TESTDATA_PROPERTIES[i+2]);
                complexValue.setType(VariableType.STRING);
                properties.put("machineId", complexValue);

                complexValue = new ComplexValue();
                complexValue.setValue(S_TESTDATA_PROPERTIES[i+3]);
                complexValue.setType(VariableType.LONG);
                properties.put("quantity", complexValue);

                complexValue = new ComplexValue();
                complexValue.setValue(S_TESTDATA_PROPERTIES[i+4]);
                complexValue.setType(VariableType.STRING);
                properties.put("designation", complexValue);

                complexValue = new ComplexValue();
                complexValue.setValue(S_TESTDATA_PROPERTIES[i+5]);
                complexValue.setType(VariableType.BOOLEAN);
                properties.put("goodPart", complexValue);

                complexValue = new ComplexValue();
                complexValue.setValue(S_TESTDATA_PROPERTIES[i+6]);
                complexValue.setType(VariableType.STRING);
                properties.put("finalArticle", complexValue);

                event.setEventProperties(properties);

                events.add(event);

                i = i + 7;
            }
        }

        // Print list of test data 1
        for (int i = 0; i < events.size(); i++) {
            SimpleEvent event = events.get(i);
            System.out.println("SimpleEvent(" + i + ") message         : " + event.toString());

            // Serialize message as JSON
//            byte[] bytes_serialized = null;
            String json_serialized = null;
            try {
                TSerializer serializer = new TSerializer(new TJSONProtocol.Factory());
//                bytes_serialized = serializer.serialize(event);
//                System.out.println("SimpleEvent(" + i + ") bytes_serialized: " + bytes_serialized);
                json_serialized = serializer.toString(event);
                System.out.println("SimpleEvent(" + i + ") json_serialized : " + json_serialized);
            } catch (TException e) {
                System.out.println(e.getClass().getName() + ": " + e.getMessage());
            }

            // Transfer byte as string
            String json_transfer = json_serialized;

            // Deserialize message
            try {
                System.out.println("SimpleEvent(" + i + ") json_transfer   : " + json_transfer);
                byte[] bytes2 = json_transfer.getBytes();
//                System.out.println("bytes2 = " + bytes2);
                TDeserializer deserializer = new TDeserializer(new TJSONProtocol.Factory());
                SimpleEvent event2 = new SimpleEvent();
                deserializer.deserialize(event2, bytes2);
                System.out.println("SimpleEvent(" + i + ") deserialized    : " + event2.toString());
            } catch (TException e) {
                System.out.println(e.getClass().getName() + ": " + e.getMessage());
            }
        }

        // Print list of test data 2 (part 1)
        StringBuilder json_transfer_string = new StringBuilder("[");
        for (int i = 0; i < events.size(); i++) {
            SimpleEvent event = events.get(i);
            System.out.println("SimpleEvent2(" + i + ") message        : " + event.toString());

            // Serialize messages as JSON string
            String json_serialized = null;
            try {
                TSerializer serializer = new TSerializer(new TJSONProtocol.Factory());
                json_serialized = serializer.toString(event);
                System.out.println("SimpleEvent2(" + i + ") json_serialized: " + json_serialized);
            } catch (TException e) {
                System.out.println(e.getClass().getName() + ": " + e.getMessage());
            }

            // Transfer byte as string
            json_transfer_string.append(json_serialized);
            json_transfer_string.append(",");
        }

        // Convert to string and remove trailing ,
        int json_length = json_transfer_string.length();
        if (json_length > 1)
            json_transfer_string.deleteCharAt(json_length - 1);
        json_transfer_string.append("]");

        String result = json_transfer_string.toString();
//        result = result.substring(0, result.length()-1);
 //       System.out.println("SimpleEvent2(*) json_transfer_string: " + result);

       // Deserialize messages (part 2)
        try {
            System.out.println("SimpleEvent2(*) result : " + result);

            ObjectMapper mapper = new ObjectMapper();
            JsonNode nodeArray = mapper.readTree(result);

            for (JsonNode node : nodeArray) {
                byte[] bytes = node.toString().getBytes();
                System.out.println("bytes = " + bytes);
                TDeserializer deserializer = new TDeserializer(new TJSONProtocol.Factory());
                SimpleEvent event = new SimpleEvent();
                deserializer.deserialize(event, bytes);
                System.out.println("deserizalised = " + event.toString());
            }
/**
            byte[] bytes2 = result.getBytes();
            System.out.println("bytes2 = " + bytes2);
            TDeserializer deserializer = new TDeserializer(new TJSONProtocol.Factory());
            SimpleEvent event2 = new SimpleEvent();
            deserializer.deserialize(event2, bytes2);
            System.out.println("SimpleEvent2(*) deserialized: " + event2.toString());
            byte[] bytes3 = result.getBytes();
            System.out.println("bytes3 = " + bytes3);
            SimpleEvent event3 = new SimpleEvent();
            deserializer.deserialize(event3, bytes3);
            System.out.println("SimpleEvent2(*) deserialized: " + event3.toString());
**/
        } catch (IOException e) {
            System.out.println(e.getClass().getName() + ": " + e.getMessage());
        } catch (TException e) {
            System.out.println(e.getClass().getName() + ": " + e.getMessage());
        }
    }
}
