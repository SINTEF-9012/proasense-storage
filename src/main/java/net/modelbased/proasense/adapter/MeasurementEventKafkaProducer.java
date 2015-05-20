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
import eu.proasense.internal.VariableType;

import kafka.javaapi.producer.Producer;
import kafka.producer.KeyedMessage;
import kafka.producer.ProducerConfig;

import java.util.Properties;


/**
 * Factory creating Kafka producers.
 * @param <K> Type of the message key
 * @param <V> Type of the message payload
 */
public class MeasurementEventKafkaProducer<K, V> {
	private Producer<K, V> producer;

	public MeasurementEventKafkaProducer(String a_zookeeper) {
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
        producer = new Producer<K, V>(config);
	}

}
