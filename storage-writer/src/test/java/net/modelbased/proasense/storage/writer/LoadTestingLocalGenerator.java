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
package net.modelbased.proasense.storage.writer;

import eu.proasense.internal.SimpleEvent;
import net.modelbased.proasense.storage.EventDocument;
import net.modelbased.proasense.storage.EventDocumentConverter;
import net.modelbased.proasense.storage.EventProperties;
import org.apache.thrift.TException;
import org.apache.thrift.TSerializer;
import org.apache.thrift.protocol.TBinaryProtocol;

import java.util.concurrent.BlockingQueue;


public class LoadTestingLocalGenerator<T> implements Runnable {
    private Class<T> eventType;
    private BlockingQueue<EventDocument> queue;
    private String collectionId;
    private int messages_per_second;
    private int max_messages;
    private EventGenerator eventGenerator;


    public LoadTestingLocalGenerator(Class<T> eventType, BlockingQueue<EventDocument> queue, String collectionId, int messages_per_second, int max_messages) {
        this.eventType = eventType;
        this.queue = queue;
        this.collectionId = collectionId;
        this.messages_per_second = messages_per_second;
        this.max_messages = max_messages;
        this.eventGenerator = new EventGenerator();
    }


    public void run() {
        int cnt = 0;
        try {
            while (cnt < this.max_messages) {
                Thread.sleep(1000);

                String eventTypeName = eventType.getName();

                for (int i = 0; i < this.messages_per_second; i++) {
                    // Generate simple event with random values
                    if (eventTypeName.matches(EventProperties.SIMPLEEVENT_CLASS_NAME)) {
                        T event = (T) eventGenerator.generateSimpleEvent(this.collectionId);

                        // Serialize message
                        TSerializer serializer = new TSerializer(new TBinaryProtocol.Factory());
                        byte[] bytes = serializer.serialize((SimpleEvent) event);

                        EventDocumentConverter converter = new EventDocumentConverter((SimpleEvent) event);
                        EventDocument eventDocument = new EventDocument(converter.getCollectionId(), converter.getDocument());

                        queue.put(eventDocument);
                    }
                }
                cnt = cnt + this.messages_per_second;
            }
        } catch (InterruptedException e) {
            System.out.println(e.getClass().getName() + ": " + e.getMessage());
        } catch (TException e) {
            System.out.println(e.getClass().getName() + ": " + e.getMessage());
        } finally {
        }
    }

}
