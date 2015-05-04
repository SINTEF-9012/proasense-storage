/**
 * Copyright (C) 2014-2015 SINTEF
 *
 *     Nicolas Ferry <nicolas.ferry@sintef.no>
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
package net.modelbased.sensapp.proasense.model;

import java.io.IOException;
import java.util.ArrayList;

import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;

import java.net.URI;

public final class JsonPrinter {

	private static ObjectMapper mapper = new ObjectMapper();
	
	private JsonPrinter() {}
	
	public static String measuresToJson(MeasureJsonModel jsonModel) {
		String jsonString = null;
		try {
			jsonString = mapper.writeValueAsString(jsonModel);
		} catch (JsonGenerationException e) {
			e.printStackTrace();
		} catch (JsonMappingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return jsonString;
	}
	
	public static String stringMeasuresToJson(StringMeasureJsonModel jsonModel) {
		String jsonString = null;
		try {
			jsonString = mapper.writeValueAsString(jsonModel);
		} catch (JsonGenerationException e) {
			e.printStackTrace();
		} catch (JsonMappingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return jsonString;
	}
	
	public static String sensorToJson(Sensor sensor) {
		String jsonString = null;
		SensorJsonModel.Schema schema = new SensorJsonModel.Schema(sensor.getBackend(), sensor.getTemplate());
		SensorJsonModel jsonSensor = new SensorJsonModel(sensor.getName(), sensor.getDescription(), schema);
		try {
			jsonString = mapper.writeValueAsString(jsonSensor);
		} catch (JsonGenerationException e) {
			e.printStackTrace();
		} catch (JsonMappingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return jsonString;
	}
	
	public static String compositeToJson(Composite composite) {
		String jsonString = null;
		ArrayList<String> sensors = new ArrayList<String>();
		for (URI uri : composite.getSensors()) {
			sensors.add(uri.toString());
		}
		ComposititeJsonModel jsonComposite = new ComposititeJsonModel(composite.getName(), composite.getDescription(), sensors);
		try {
			jsonString = mapper.writeValueAsString(jsonComposite);
		} catch (JsonGenerationException e) {
			e.printStackTrace();
		} catch (JsonMappingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return jsonString;
	}
}
