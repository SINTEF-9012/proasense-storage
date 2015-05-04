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
package net.modelbased.sensapp.proasense.storage;

import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import eu.proasense.internal.AnomalyEvent;
import eu.proasense.internal.ComplexValue;
import eu.proasense.internal.DerivedEvent;
import eu.proasense.internal.PredictedEvent;
import eu.proasense.internal.RecommendationEvent;
import eu.proasense.internal.SimpleEvent;

import eu.proasense.internal.VariableType;
import org.apache.thrift.TException;
import org.apache.thrift.TSerializer;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.bson.Document;

import java.util.Iterator;
import java.util.Map;


public class EventDocumentConverter {
    private EventDocument eventDocument;

    public EventDocumentConverter(SimpleEvent event) {
        this.eventDocument = convertSimpleEventToDocument(event);
    }

    public EventDocumentConverter(DerivedEvent event) {
        this.eventDocument = convertDerivedEventToDocument(event);
    }

    public EventDocumentConverter(PredictedEvent event) {
        this.eventDocument = convertPredictedEventToDocument(event);
    }

    public EventDocumentConverter(AnomalyEvent event) {
        this.eventDocument = convertAnomalyEventToDocument(event);
    }

    public EventDocumentConverter(RecommendationEvent event) {
        this.eventDocument = convertRecommendationEventToDocument(event);
    }

    public EventDocument getEventDocument() {
        return this.eventDocument;
    }

    public String getCollectionId() {
        return this.eventDocument.getCollectionId();
    }

    public Document getDocument() {
        return this.eventDocument.getDocument();
    }

    private EventDocument convertSimpleEventToDocument(SimpleEvent event) {
        Document document = new Document("_id", event.getTimestamp());
        document.append("timestamp", event.getTimestamp());
        document.append("sensorId", event.getSensorId());

        Map<String, ComplexValue> properties = event.getEventProperties();
        Iterator it = properties.entrySet().iterator();
        DBObject propertiesObj = new BasicDBObject();
        while (it.hasNext()) {
            Map.Entry entryProperty = (Map.Entry)it.next();
            String key = (String)entryProperty.getKey();
            ComplexValue value = (ComplexValue)entryProperty.getValue();

            String valueKey = value.getValue();
            VariableType valueType = value.getType();

            if (valueType.equals(VariableType.LONG)) {
                propertiesObj.put(key.replace(".", "_"), new Long(valueKey));
            }
        }
        document.append("eventProperties", propertiesObj);

        // Serialize event message
        TSerializer serializer = new TSerializer(new TBinaryProtocol.Factory());
        try {
            byte[] bytes = serializer.serialize((SimpleEvent) event);
            document.append(EventProperties.STORAGE_SERIALIZED_EVENT_KEY, bytes);
        }
        catch (TException e) {
            System.out.println(e.getClass().getName() + ": " + e.getMessage());
        }

        return new EventDocument(event.getSensorId(), document);
    }

    private EventDocument convertDerivedEventToDocument(DerivedEvent event) {
        Document document = new Document("_id", event.getTimestamp());
        document.append("timestamp", event.getTimestamp());
        document.append("componentId", event.getComponentId());
        document.append("eventName", event.getEventName());
        document.append("eventProperties", event.getEventProperties().toString());

        // Serialize message
        TSerializer serializer = new TSerializer(new TBinaryProtocol.Factory());
        try {
            byte[] bytes = serializer.serialize((DerivedEvent) event);
            document.append(EventProperties.STORAGE_SERIALIZED_EVENT_KEY, bytes);
        }
        catch (TException e) {
            System.out.println(e.getClass().getName() + ": " + e.getMessage());
        }

        return new EventDocument(EventProperties.DERIVEDEVENT_STORAGE_COLLECTION_NAME, document);
    }

    private EventDocument convertPredictedEventToDocument(PredictedEvent event) {
        Document document = new Document("_id", event.getTimestamp());
        document.append("timestamp", event.getTimestamp());
        document.append("pdfType", event.getPdfType().toString());
        document.append("eventProperties", event.getEventProperties().toString());
        document.append("params", event.getParams().toString());
        document.append("timestamps", event.getTimestamps().toString());
        document.append("eventName", event.getEventName());

        // Serialize message
        TSerializer serializer = new TSerializer(new TBinaryProtocol.Factory());
        try {
            byte[] bytes = serializer.serialize((PredictedEvent) event);
            document.append(EventProperties.STORAGE_SERIALIZED_EVENT_KEY, bytes);
        }
        catch (TException e) {
            System.out.println(e.getClass().getName() + ": " + e.getMessage());
        }

        return new EventDocument(EventProperties.PREDICTEDEVENT_STORAGE_COLLECTION_NAME, document);
    }

    private EventDocument convertAnomalyEventToDocument(AnomalyEvent event) {
        Document document = new Document("_id", event.getTimestamp());
        document.append("timestamp", event.getTimestamp());
        document.append("anomalyType", event.getAnomalyType());
        document.append("blob", event.getBlob());

        // Serialize message
        TSerializer serializer = new TSerializer(new TBinaryProtocol.Factory());
        try {
            byte[] bytes = serializer.serialize((AnomalyEvent) event);
            document.append(EventProperties.STORAGE_SERIALIZED_EVENT_KEY, bytes);
        }
        catch (TException e) {
            System.out.println(e.getClass().getName() + ": " + e.getMessage());
        }

        return new EventDocument(EventProperties.ANOMALYEVENT_STORAGE_COLLECTION_NAME, document);
    }

    private EventDocument convertRecommendationEventToDocument(RecommendationEvent event) {
        Document document = new Document("_id", event.getTimestamp());
        document.append("recommendationId", event.getRecommendationId());
        document.append("action", event.getAction());
        document.append("timestamp", event.getTimestamp());
        document.append("actor", event.getActor());
        document.append("eventProperties", event.getEventProperties().toString());
        document.append("eventName", event.getEventName());

        // Serialize message
        TSerializer serializer = new TSerializer(new TBinaryProtocol.Factory());
        try {
            byte[] bytes = serializer.serialize((RecommendationEvent) event);
            document.append(EventProperties.STORAGE_SERIALIZED_EVENT_KEY, bytes);
        }
        catch (TException e) {
            System.out.println(e.getClass().getName() + ": " + e.getMessage());
        }

        return new EventDocument(EventProperties.RECOMMENDATIONEVENT_STORAGE_COLLECTION_NAME, document);
    }
}
