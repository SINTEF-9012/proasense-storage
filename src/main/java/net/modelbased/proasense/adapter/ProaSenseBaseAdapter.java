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

import eu.proasense.internal.SimpleEvent;

import kafka.producer.ProducerConfig;

import org.apache.kafka.clients.producer.KafkaProducer;

import java.util.Properties;


public abstract class ProaSenseBaseAdapter {
	protected KafkaProducer<String, byte[]> producer;

	public ProaSenseBaseAdapter() {}

    public ProaSenseBaseAdapter(String a_zookeeper) {
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
        producer = createProducer(a_zookeeper);
    }

    protected KafkaProducer<String, byte[]> createProducer(String a_zookeeper) {
        // Specify producer properties
        Properties props = new Properties();
        props.put("bootstrap.servers", "89.216.116.44:9092");
        props.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        props.put("value.serializer", "org.apache.kafka.common.serialization.ByteArraySerializer");

        // Define the producer object
        KafkaProducer<String, byte[]> producer = new KafkaProducer<String, byte[]>(props);

        return producer;
    }

}