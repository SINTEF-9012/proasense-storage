/**
 * Copyright (C) 2014-2015 SINTEF
 *
 *     Brian Elvesæter <brian.elvesater@sintef.no>
 *     Shahzad Karamat <shazad.karamat@gmail.com>
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

import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.thrift.TException;
import org.apache.thrift.TSerializer;
import org.apache.thrift.protocol.TBinaryProtocol;
import twitter4j.*;
import twitter4j.conf.ConfigurationBuilder;

import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class ProaSenseTwitterAdapter extends ProaSenseBaseAdapter {
    private Twitter twitter;
    private String topic;

    public ProaSenseTwitterAdapter(String a_zookeeper, String a_topic) {
        topic = a_topic;

        // Create the Kakfa producer
        producer = createProducer(a_zookeeper);

        // Create Twitter connection
        twitter = createTwitterConnection();

        // Search for #hashtag
        System.out.println("Write the search-word.");
        Scanner sc = new Scanner(System.in);
        String search = sc.next();

        Query query = new Query(search);

        try {
            QueryResult result = twitter.search(query);
            int cnt = 0;

            for (Status status : result.getTweets()) {
                cnt++;

                // Convert to simple storage
                SimpleEvent event = convertToSimpleEvent(status);
                System.out.println("SimpleEvent(" + cnt + "): " + event.toString());

                // Serialize message
                TSerializer serializer = new TSerializer(new TBinaryProtocol.Factory());
                byte[] bytes = serializer.serialize(event);

                // Publish message
                ProducerRecord<String, byte[]> message = new ProducerRecord<String, byte[]>(this.topic, bytes);
                producer.send(message);
            }
        }
        catch (TwitterException ignore) {
        }
        catch (TException ignore) {
        }
        finally {
            producer.close();
        }
    }

    private Twitter createTwitterConnection() {
        ConfigurationBuilder cb = new ConfigurationBuilder();
        cb.setUseSSL(true);
        cb.setDebugEnabled(true)
                .setOAuthConsumerKey("k9AVPhdChIpvnhTY0k4f7m9nn")
                .setOAuthConsumerSecret("flNGrS18usTrYLKgjNCvLzFMIEeWsoBuMurGhipyqzo2tmiLPs")
                .setOAuthAccessToken("3095414087-zibVqOi9rHpFoTqUsFryErb6JFQVxgRYv8ENvf4")
                .setOAuthAccessTokenSecret("yXNZnOlfgVthLQ7G5UIPw7VLQvb5ogeWBB3fGYTSnQ9vZ");

        TwitterFactory tf = new TwitterFactory(cb.build());
        Twitter twitter = tf.getInstance();

        return twitter;
    }

    private SimpleEvent convertToSimpleEvent(Status status) {
        // Define complex value
        ComplexValue value = new ComplexValue();
        value.setValue(status.getText());
        value.setType(VariableType.STRING);

        // Define properties
        Map<String, ComplexValue> properties = new HashMap<String, ComplexValue>();
        properties.put(status.getText(), value);

        // Define simple storage
        SimpleEvent event = new SimpleEvent();
        event.setTimestamp(status.getCreatedAt().getTime());
        event.setSensorId(status.getUser().toString());
        event.setEventProperties(properties);

        return event;
    }

    public static void main(String[] args) throws TwitterException {
        String zooKeeper = "89.216.116.44:2181";
        String topic = "proasense.datainfrastructure.mhwirth.all";

        ProaSenseTwitterAdapter adapter = new ProaSenseTwitterAdapter(zooKeeper, topic);
    }
}
