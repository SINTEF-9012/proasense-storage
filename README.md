# Sensing Architecture - Storage component
The storage component for the Sensing Architecture developed in the ProaSense project (http://www.proasense.eu). The storage layer builds upon the ideas of SensApp and extends it with additional capabilities from the ProaSense project addressing specific requirements for the sensing enterprise.

## Requirements
* MongoDB 3.0 or higher

## Setup
The storage component consists of four modules:

* **storage-base**, which contains common, shared base classes for the Storage Writer, Storage Reader and Storage Registry services.
* **storage-writer**, which contains the classes for the Storage Writer service.
* **storage-reader**, which contains the classes for the Storage Reader service.
* **storage-registry**, which contains the classes for the Storage Registry service.

### Property files

#### server.properties
The `server.properties` file contains the configuration properties for the Storage Writer and Storage Reader services.

### Compile
```
mvn clean install
```

### Run the Storage Writer service
```
cd storage-writer
mvn exec:java -Dexec.mainClass=net.modelbased.proasense.storage.writer.StorageWriterMongoService
```

### Deploy the Storage Reader service
```
cd storage-reader
mvn jetty:run
```

### Deploy the Storage Registry service
```
cd storage-reader
mvn jetty:run
```

## Test and benchmark

### Property files

#### client.properties
The `client.properties` file contains the configuration for the test and benchmark code.

### Run the local storage writer benchmark
```
mvn exec:java -Dexec.classpathScope=test -Dexec.mainClass=net.modelbased.proasense.storage.writer.StorageWriterServiceMongoLocalBenchmark
```

### Run the local storage reader benchmark
```
mvn exec:java -Dexec.classpathScope=test -Dexec.mainClass=net.modelbased.proasense.storage.reader.StorageReaderServiceMongoLocalBenchmark
```

### Run the Kafka storage writer benchmark
```
mvn exec:java -Dexec.classpathScope=test -Dexec.mainClass=net.modelbased.proasense.storage.writer.StorageWriterServiceMongoKafkaBenchmark
```

### Run the local storage reader test
```
mvn clean install
```

## User guide
* TBD
