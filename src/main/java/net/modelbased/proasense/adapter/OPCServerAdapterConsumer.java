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
package net.modelbased.proasense.adapter;

import eu.proasense.internal.ComplexValue;
import eu.proasense.internal.SimpleEvent;
import eu.proasense.internal.VariableType;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import kafka.javaapi.producer.Producer;
import kafka.producer.ProducerConfig;
import org.jinterop.dcom.common.JIException;
import org.jinterop.dcom.core.JIVariant;
import org.joda.time.DateTime;
import org.openscada.opc.lib.common.ConnectionInformation;
import org.openscada.opc.lib.da.AccessBase;
import org.openscada.opc.lib.da.DataCallback;
import org.openscada.opc.lib.da.Group;
import org.openscada.opc.lib.da.Item;
import org.openscada.opc.lib.da.ItemState;
import org.openscada.opc.lib.da.Server;
import org.openscada.opc.lib.da.SyncAccess;


public class OPCServerAdapterConsumer {

    public static void main(String[] args) throws Exception {

        // create connection information
        final ConnectionInformation ci = new ConnectionInformation();

        ci.setHost("192.168.11.33");
        ci.setDomain("");
        ci.setUser("");
        ci.setPassword("");
        // ci.setProgId("SWToolbox.TOPServer.V5");
        ci.setClsid("680DFBF7-C92D-484D-84BE-06DC3DECCD68"); // if ProgId is not working, try it using the Clsid instead
//        final String itemId = "Channel1.Device1.Tag1";
        final String itemId = "Group1";
        // create a new server
        final Server server = new Server(ci, Executors.newSingleThreadScheduledExecutor());
        try {
            // connect to server
            server.connect();
            // add sync access, poll every 500 ms
            final AccessBase access = new SyncAccess(server, 500);
            access.addItem(itemId, new DataCallback() {
                @Override
                public void changed(Item item, ItemState state) {
                    // also dump value
                    try {
                        if (state.getValue().getType() == JIVariant.VT_UI4) {
                            System.out.println("<<< " + state + " / value = " + state.getValue().getObjectAsUnsigned().getValue());
                        } else {
                            System.out.println("<<< " + state + " / value = " + state.getValue().getObject());
                        }
                    } catch (JIException e) {
                        e.printStackTrace();
                    }
                }
            });

            // Add a new group
            final Group group = server.addGroup("test");
            // Add a new item to the group
            final Item item = group.addItem(itemId);

            // start reading
            access.bind();

            // add a thread for writing a value every 3 seconds
            ScheduledExecutorService writeThread = Executors.newSingleThreadScheduledExecutor();
            final AtomicInteger i = new AtomicInteger(0);
            writeThread.scheduleWithFixedDelay(new Runnable() {
                @Override
                public void run() {
                    final JIVariant value = new JIVariant(i.incrementAndGet());
                    try {
                        System.out.println(">>> " + "writing value " + i.get());
                        item.write(value);
                    } catch (JIException e) {
                        e.printStackTrace();
                    }
                }
            }, 5, 3, TimeUnit.SECONDS);

            // wait a little bit
            Thread.sleep(20 * 1000);
            writeThread.shutdownNow();
            // stop reading
            access.unbind();
        } catch (final JIException e) {
            System.out.println(String.format("%08X: %s", e.getErrorCode(), server.getErrorMessage(e.getErrorCode())));
        }
    }


    private SimpleEvent convertToSimpleEvent(String input) {
        // Input format: "{\"variable_type\":\"1002311\",\"value\":138.2346,\"variable_timestamp\":\"2014-02-14T07:01:24.133Z\"}";

        input = input.replace("\"", "");
        input = input.replace("{", "");
        input = input.replace("variable_type:", "");
        input = input.replace("value:", "");
        input = input.replace("variable_timestamp:", "");
        input = input.replace("}", "");

        String[] parts = input.split(",");
        System.out.println("parts[0] = " + parts[0]);
        System.out.println("parts[1] = " + parts[1]);
        System.out.println("parts[2] = " + parts[2]);

        long timestamp = new DateTime(parts[2]).getMillis();
        String sensorId = parts[0];

        // Define complex value
        ComplexValue value = new ComplexValue();
        value.setValue(parts[1]);
        value.setType(VariableType.LONG);

        // Define measurement
        Map<String, ComplexValue> measurement = new HashMap<String, ComplexValue>();
        measurement.put("raw.value", value);

        SimpleEvent event = new SimpleEvent();
        event.setTimestamp(timestamp);
        event.setSensorId(convertTagToSensorId(sensorId));
        event.setEventProperties(measurement);

        return event;
    }


    private Producer<String, String> createProducer(String a_zookeeper) {
        // Specify producer properties
        Properties props = new Properties();
        props.put("metadata.broker.list", a_zookeeper);
        props.put("serializer.class", "kafka.serializer.StringEncoder");
        props.put("producer.type", "sync");
        props.put("queue.enqueue.timeout.ms", "-1");
        props.put("batch.num.messages", "200");
        props.put("compression.codec", "1");
        props.put("request.required.acks", 1);

        ProducerConfig config = new ProducerConfig(props);

        // Define the producer object
        Producer<String, String> producer = new Producer<String, String>(config);

        return producer;
    }


    private String convertTagToSensorId(String tag) {
        String sensorId = "";

        if(tag.matches("1000693"))
            sensorId = "MHWirth.DDM.DrillingRPM";
        else if(tag.matches("1000700"))
            sensorId = "MHWirth.DDM.DrillingTorque";
        else if(tag.matches("1002311"))
            sensorId = "MHWirth.DDM.HookLoad";
        else if(tag.matches("1000695"))
            sensorId = "MHWirth.DDM.GearLubeOilTemp";
        else if(tag.matches("1000692"))
            sensorId = "MHWirth.DDM.GearBoxPressure";
        else if(tag.matches("1000696"))
            sensorId = "MHWirth.DDM.SwivelOilTemp";
        else if(tag.matches("1002123"))
            sensorId = "MHWirth.DrillBit.WeightOnBit";
        else if(tag.matches("1033619"))
            sensorId = "MHWirth.DrillBit.WeightOnBit";

        return sensorId;
    };

}
