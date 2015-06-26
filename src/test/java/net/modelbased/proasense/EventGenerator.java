/**
 * Copyright 2014-2015 SINTEF
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
package net.modelbased.proasense;

import eu.proasense.internal.*;

import org.apache.commons.math3.random.MersenneTwister;
import org.apache.commons.math3.random.RandomDataGenerator;
import org.apache.commons.math3.random.RandomGenerator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;


public class EventGenerator {
    private RandomDataGenerator randomData;
    private RandomGenerator randomNumber;


    public EventGenerator() {
        this.randomData = new RandomDataGenerator();
        this.randomNumber = new MersenneTwister();
    }


    public SimpleEvent generateSimpleEvent(String sensorId) {
        // Define complex value
        ComplexValue value = new ComplexValue();
        value.setValue(Objects.toString(randomNumber.nextLong()));
        value.setType(VariableType.LONG);

        // Define properties
        Map<String, ComplexValue> properties = new HashMap<String, ComplexValue>();
        properties.put("raw.value", value);

        // Define simple event
        SimpleEvent event = new SimpleEvent();
        event.setTimestamp(System.currentTimeMillis());
        event.setSensorId(sensorId);
        event.setEventProperties(properties);

        return event;
    }


    public DerivedEvent generateDerivedEvent(String componentId) {
        // Define complex value
        ComplexValue value = new ComplexValue();
        value.setValue(Objects.toString(randomNumber.nextLong()));
        value.setType(VariableType.LONG);

        // Define properties
        Map<String, ComplexValue> properties = new HashMap<String, ComplexValue>();
        properties.put("derived.value", value);

        // Define derived event
        DerivedEvent event = new DerivedEvent();
        event.setTimestamp(System.currentTimeMillis());
        event.setComponentId(componentId);
        event.setEventName(randomData.nextHexString(10));
        event.setEventProperties(properties);

        return event;
    }


    public PredictedEvent generatePredictedEvent(String collectionId) {
        // Define complex value
        ComplexValue value = new ComplexValue();
        value.setValue(Objects.toString(randomNumber.nextLong()));
        value.setType(VariableType.LONG);

        // Define properties
        Map<String, ComplexValue> properties = new HashMap<String, ComplexValue>();
        properties.put("predicted.value", value);

        // Define params
        List<Double> params = new ArrayList<Double>();
        params.add(randomNumber.nextDouble());
        params.add(randomNumber.nextDouble());

        // Define timestamps
        List<Long> timestamps = new ArrayList<Long>();
        timestamps.add(System.currentTimeMillis());
        timestamps.add(System.currentTimeMillis());

        // Define predicted event
        PredictedEvent event = new PredictedEvent();
        event.setTimestamp(System.currentTimeMillis());
        event.setPdfType(PDFType.EXPONENTIAL);
        event.setEventProperties(properties);
        event.setParams(params);
        event.setTimestamps(timestamps);
        event.setEventName(randomData.nextHexString(10));

        return event;
    }


    public AnomalyEvent generateAnomalyEvent(String collectionId) {
        // Define anomaly event
        AnomalyEvent event = new AnomalyEvent();
        event.setTimestamp(System.currentTimeMillis());
        event.setAnomalyType(randomData.nextHexString(10));
        event.setBlob(randomData.nextHexString(1000));

        return event;
    }


    public RecommendationEvent generateRecommendationEvent(String collectionId) {
        // Define complex value
        ComplexValue value = new ComplexValue();
        value.setValue(Objects.toString(randomNumber.nextLong()));
        value.setType(VariableType.LONG);

        // Define properties
        Map<String, ComplexValue> properties = new HashMap<String, ComplexValue>();
        properties.put("recommendation.value", value);

        // Define recommendation event
        RecommendationEvent event = new RecommendationEvent();
        event.setRecommendationId(Objects.toString(System.currentTimeMillis()));
        event.setAction(randomData.nextHexString(100));
        event.setTimestamp(System.currentTimeMillis());
        event.setActor(randomData.nextHexString(10));
        event.setEventProperties(properties);
        event.setEventName(randomData.nextHexString(10));

        return event;
    }


    public FeedbackEvent generateFeedbackEvent(String collectionId) {
        // Define complex value
        ComplexValue value = new ComplexValue();
        value.setValue(Objects.toString(randomNumber.nextLong()));
        value.setType(VariableType.LONG);

        // Define properties
        Map<String, ComplexValue> properties = new HashMap<String, ComplexValue>();
        properties.put("recommendation.value", value);

        // Define feedback event
        FeedbackEvent event = new FeedbackEvent();
        event.setActor(randomData.nextHexString(10));
        event.setTimestamp(System.currentTimeMillis());
        event.setStatus(Status.SUGGESTED);
        event.setComments(randomData.nextHexString(100));
        event.setRecommendationId(Objects.toString(System.currentTimeMillis()));

        return event;
    }
}
