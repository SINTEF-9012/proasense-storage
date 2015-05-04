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

public class ValueJsonModel {
	
	private long t;
	
	public ValueJsonModel() {
	}
	
	public ValueJsonModel(long t) {
		this.t = t;
	}
	
	public long getT() {
		return t;
	}
	
	public void setT(long t) {
		this.t = t;
	}
}