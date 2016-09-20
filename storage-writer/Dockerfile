FROM anapsix/alpine-java
MAINTAINER Brian Elvesæter <brian.elvesater@sintef.no>

ENV ZOOKEEPER_CONNECT 191.238.106.229:2181
ENV KAFKA_BOOTSTRAP_SERVERS 191.238.106.229:9092
ENV MONGODB_URL mongodb://127.0.0.1:27017

COPY target/storage-writer-1.0-SNAPSHOT.jar /home/storage-writer.jar
CMD ["java", "-jar", "/home/storage-writer.jar"]
