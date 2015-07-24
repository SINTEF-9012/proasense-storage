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
package net.modelbased.proasense.storage;

import eu.proasense.internal.AnomalyEvent;
import eu.proasense.internal.DerivedEvent;
import eu.proasense.internal.FeedbackEvent;
import eu.proasense.internal.PredictedEvent;
import eu.proasense.internal.RecommendationEvent;
import eu.proasense.internal.SimpleEvent;
import org.apache.thrift.TDeserializer;
import org.apache.thrift.TException;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.bson.Document;
import org.bson.types.Binary;


public class EventConverter<T> {
    private T event;


    public EventConverter(Class<T> eventType, Document document) {
        String eventTypeName = eventType.getName();

        if (eventTypeName.matches("eu.proasense.internal.SimpleEvent"))
            this.event = (T)convertDocumentToSimpleEvent(document);

        if (eventTypeName.matches("eu.proasense.internal.DerivedEvent"))
            this.event = (T)convertDocumentToDerivedEvent(document);

        if (eventTypeName.matches("eu.proasense.internal.PredictedEvent"))
            this.event = (T)convertDocumentToPredictedEvent(document);

        if (eventTypeName.matches("eu.proasense.internal.AnomalyEvent"))
            this.event = (T)convertDocumentToAnomalyEvent(document);

        if (eventTypeName.matches("eu.proasense.internal.RecommendationEvent"))
            this.event = (T)convertDocumentToRecommendationEvent(document);

        if (eventTypeName.matches("eu.proasense.internal.FeedbackEvent"))
            this.event = (T)convertDocumentToFeedbackEvent(document);
    }


    public T getEvent() {
        return this.event;
    }


    private SimpleEvent convertDocumentToSimpleEvent(Document document) {
        Binary serializedEvent = (Binary)document.get(EventProperties.STORAGE_SERIALIZED_EVENT_KEY);
        byte[] bytes = serializedEvent.getData();

        // Deserialize event message
        TDeserializer deserializer = new TDeserializer(new TBinaryProtocol.Factory());
        SimpleEvent event = new SimpleEvent();

        try {
            deserializer.deserialize(event, bytes);
        }
        catch (TException e) {
            System.out.println(e.getClass().getName() + ": " + e.getMessage());
        }

        return event;
    }


    private DerivedEvent convertDocumentToDerivedEvent(Document document) {
        Binary serializedEvent = (Binary)document.get(EventProperties.STORAGE_SERIALIZED_EVENT_KEY);
        byte[] bytes = serializedEvent.getData();

        // Deserialize event message
        TDeserializer deserializer = new TDeserializer(new TBinaryProtocol.Factory());
        DerivedEvent event = new DerivedEvent();

        try {
            deserializer.deserialize(event, bytes);
        }
        catch (TException e) {
            System.out.println(e.getClass().getName() + ": " + e.getMessage());
        }

        return event;
    }


    private PredictedEvent convertDocumentToPredictedEvent(Document document) {
        Binary serializedEvent = (Binary)document.get(EventProperties.STORAGE_SERIALIZED_EVENT_KEY);
        byte[] bytes = serializedEvent.getData();

        // Deserialize event message
        TDeserializer deserializer = new TDeserializer(new TBinaryProtocol.Factory());
        PredictedEvent event = new PredictedEvent();

        try {
            deserializer.deserialize(event, bytes);
        }
        catch (TException e) {
            System.out.println(e.getClass().getName() + ": " + e.getMessage());
        }

        return event;
    }


    private AnomalyEvent convertDocumentToAnomalyEvent(Document document) {
        Binary serializedEvent = (Binary)document.get(EventProperties.STORAGE_SERIALIZED_EVENT_KEY);
        byte[] bytes = serializedEvent.getData();

        // Deserialize event message
        TDeserializer deserializer = new TDeserializer(new TBinaryProtocol.Factory());
        AnomalyEvent event = new AnomalyEvent();

        try {
            deserializer.deserialize(event, bytes);
        }
        catch (TException e) {
            System.out.println(e.getClass().getName() + ": " + e.getMessage());
        }

        return event;
    }


    private RecommendationEvent convertDocumentToRecommendationEvent(Document document) {
        Binary serializedEvent = (Binary)document.get(EventProperties.STORAGE_SERIALIZED_EVENT_KEY);
        byte[] bytes = serializedEvent.getData();

        // Deserialize event message
        TDeserializer deserializer = new TDeserializer(new TBinaryProtocol.Factory());
        RecommendationEvent event = new RecommendationEvent();

        try {
            deserializer.deserialize(event, bytes);
        }
        catch (TException e) {
            System.out.println(e.getClass().getName() + ": " + e.getMessage());
        }

        return event;
    }


    private FeedbackEvent convertDocumentToFeedbackEvent(Document document) {
        Binary serializedEvent = (Binary)document.get(EventProperties.STORAGE_SERIALIZED_EVENT_KEY);
        byte[] bytes = serializedEvent.getData();

        // Deserialize event message
        TDeserializer deserializer = new TDeserializer(new TBinaryProtocol.Factory());
        FeedbackEvent event = new FeedbackEvent();

        try {
            deserializer.deserialize(event, bytes);
        }
        catch (TException e) {
            System.out.println(e.getClass().getName() + ": " + e.getMessage());
        }

        return event;
    }

}
