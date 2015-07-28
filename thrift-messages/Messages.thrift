//Last edited: 24.03.2015
//by Technical meeting @ Karlsruhe


namespace java eu.proasense.internal
namespace js eu.proasense.internal

typedef i64 long

enum VariableType {
	LONG,
	STRING,
	DOUBLE,
	BLOB,
	BOOLEAN
}

struct ComplexValue {

	1: optional string value;
	2: optional list<string> values;
	3: required VariableType type;
}

//enum SensorParameter
//{
//	FREQUENCY
//}

//### Sensing layer
//struct SensorConfigureParameter 
//{
//	1: required long timestamp;
//	2: string componentId;
//	3: string sensorId;
//	4: SensorParameter parameter;
//	5: double value;
//}

//### input enricher and sensing layer
struct SimpleEvent {

	1:	required long timestamp;
	2:	required string sensorId;
	3:	required map<string,ComplexValue> eventProperties;

}

//### CEP
struct DerivedEvent {

	1:	required long timestamp;
	2:	required string componentId;
	3:	required string eventName;
	4:	required map<string,ComplexValue> eventProperties;

}


struct ModelRequest {

	1:	required string modelType;
	2:	required string componentId;
	3: 	string blob;

}

//Also a part of offline analytics
struct ModelResponse {

	1:	required string modelType;
	2:	required string componentId;
	3:	required string blob;
}

//### online processing
enum PDFType {
	EXPONENTIAL,
	HISTOGRAM
}

//also should be a tuple
struct PredictedEvent {
		1:	required long timestamp;
		2: 	required PDFType pdfType; 		//probability density function type
		3:	required map<string,ComplexValue> eventProperties; //metadata, context, prediction subject...
		4:	required list<double> params;    //define the parameters of the pdf 
		5:	optional list<long> timestamps; //define the parameters of the pdf 
		6:  	required string eventName;	//predicted event name
}

struct AnomalyEvent {

	1:	required long timestamp;
	2:	required string anomalyType;
	3:	required string blob;
}


//### Online Decision Making (ICCS)

enum Status {
	SUGGESTED,
	IMPLEMENTED,
	SUCCESSFUL,
	UNSUCCESSFUL
}

struct FeedbackEvent { 
	1: required string actor;
	2: required long timestamp;
	3: required Status status;
	4: optional string comments;
	5: required string recommendationId;
} 


//also be a tuple
struct RecommendationEvent {
	1: required string recommendationId;
	2: required string action;
	3: required long timestamp;
	4: required string actor;	
	5: required map<string,ComplexValue> eventProperties; 
	6: required string eventName;	

}