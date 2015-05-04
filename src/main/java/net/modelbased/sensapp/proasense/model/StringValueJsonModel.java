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

public class StringValueJsonModel extends ValueJsonModel {
	
	private String sv;
	
	public StringValueJsonModel() {
	}
	
	public StringValueJsonModel(String sv, long t) {
		super(t);
		this.sv = sv;
	}
	
	public String getSv() {
		return sv;
	}
	
	public void setSv(String sv) {
		this.sv = sv;
	}
}
