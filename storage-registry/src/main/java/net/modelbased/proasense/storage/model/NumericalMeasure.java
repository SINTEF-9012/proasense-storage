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
package net.modelbased.proasense.storage.model;

public class NumericalMeasure extends Measure {
	
	private float value;
	
	public NumericalMeasure() {
		super();
	}
	
	public NumericalMeasure(int id, String sensor, float value, long time, long basetime, boolean uploaded) {
		super(id, sensor, time, basetime, uploaded);
		this.value = value;
	}

	public float getValue() {
		return value;
	}

	public void setValue(float value) {
		this.value = value;
	}
	
	@Override
	public String toString() {
		return "MEASURE/ Id: " + getId() + " - Sensor: " + getSensor() + " - Value: " + getValue() + " - Time: " + getTime() + " - Uploaded:" + isUploaded();
	}
}
