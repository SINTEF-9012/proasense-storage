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

import net.modelbased.proasense.storage.EventDocument;
import net.modelbased.proasense.storage.EventProperties;

import java.util.concurrent.BlockingQueue;


public class EventHeartbeat implements Runnable {
    private BlockingQueue<EventDocument> queue;
    private int sleep;


    public EventHeartbeat(BlockingQueue<EventDocument> queue, int sleep) {
        this.queue = queue;
        this.sleep = sleep;
    }


    public void run() {
        try {
            while (true) {
                Thread.sleep(this.sleep);

                // Generate heartbeat event
                EventDocument eventDocument = new EventDocument(EventProperties.STORAGE_HEARTBEAT, null);

                queue.put(eventDocument);
            }
        } catch (InterruptedException e) {
            System.out.println(e.getClass().getName() + ": " + e.getMessage());
        } finally {
        }
    }

}
