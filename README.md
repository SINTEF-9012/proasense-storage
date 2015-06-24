# Sensing Architecture - Storage component
The storage component for the Sensing Architecture developed in the ProaSense project. The storage layer builds upon the ideas of SensApp and extends it with additional capabilities from the ProaSense project addressing specific requirements for the sensing enterprise.

## Requirements
* MongoDB 2.6 or higher

## Setup

### Property files

#### server.properties
The `server.properties` file contains the configuration for the Storage Writer and Storage Reader services.

### Compile
```
mvn clean install
```

### Run the storage writer service
```
mvn exec:java -Dexec.mainClass=net.modelbased.proasense.storage.StorageWriterServiceMongoServer
```

### Deploy the storage reader service
* TBD

## Test and benchmark

### Property files

#### client.properties
The `client.properties` file contains the configuration for the test and benchmark code.

### Run the local storage writer benchmark
```
mvn exec:java -Dexec.classpathScope=test -Dexec.mainClass=net.modelbased.proasense.StorageWriterServiceMongoLocalBenchmark
```

### Run the local storage reader benchmark
```
mvn exec:java -Dexec.classpathScope=test -Dexec.mainClass=net.modelbased.proasense.StorageReaderServiceMongoLocalBenchmark
```

### Run the Kafka storage writer benchmark
```
mvn exec:java -Dexec.classpathScope=test -Dexec.mainClass=net.modelbased.proasense.StorageWriterServiceMongoKafkaBenchmark
```

### Run the local storage reader test
```
mvn clean install
```

## User guide
* TBD
