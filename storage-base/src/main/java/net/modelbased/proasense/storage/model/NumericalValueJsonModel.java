/**
 * Copyright 2015 Brian Elvesæter <${email}>
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

public class NumericalValueJsonModel extends ValueJsonModel {
	
	private float v;
	
	public NumericalValueJsonModel() {
		super();
	}
	
	public NumericalValueJsonModel(float v, long t) {
		super(t);
		this.v = v;
	}
	
	public float getV() {
		return v;
	}
	
	public void setV(int v) {
		this.v = v;
	}
}
