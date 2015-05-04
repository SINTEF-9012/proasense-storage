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
package net.modelbased.sensapp.proasense;

import net.modelbased.sensapp.proasense.model.Sensor;
import net.modelbased.sensapp.proasense.rest.RestRequest;

import java.net.URI;
import java.net.URISyntaxException;

public class MHWirthSetPointDeviationDetectionUseCase {

	public MHWirthSetPointDeviationDetectionUseCase(String url) {
        try {
            URI uriSensApp = new URI(url);

    		// Set up virtual sensors
	    	Sensor[] sensorArray = new Sensor[7];

		    sensorArray[0] = new Sensor();
            sensorArray[0].setUri(uriSensApp);
            sensorArray[0].setBackend("raw");
            sensorArray[0].setTemplate("Numerical");
            sensorArray[0].setSource("1002113");
            sensorArray[0].setName("MHWirth.Ram.PositionSetPoint");
            sensorArray[0].setDescription("The variable denotes the calculated position from a machine’s control system, which is a result of an operator command given by the driller/assistant driller to reposition the machine.");
            sensorArray[0].setUnit("?");

            if(!RestRequest.isSensorRegistred(sensorArray[0]))
                RestRequest.postSensor(sensorArray[0]);

		    sensorArray[1] = new Sensor();
            sensorArray[1].setUri(uriSensApp);
            sensorArray[1].setBackend("raw");
            sensorArray[1].setTemplate("Numerical");
            sensorArray[1].setSource("1002115");
	    	sensorArray[1].setName("MHWirth.Ram.PositionMeasuredValue");
            sensorArray[1].setDescription("The variable denotes the actual position the machine has moved to as a result of the change in position set point.");
            sensorArray[1].setUnit("?");

            if(!RestRequest.isSensorRegistred(sensorArray[1]))
                RestRequest.postSensor(sensorArray[1]);

    		sensorArray[2] = new Sensor();
            sensorArray[2].setUri(uriSensApp);
            sensorArray[2].setBackend("raw");
            sensorArray[2].setTemplate("Numerical");
            sensorArray[2].setSource("1002114");
	    	sensorArray[2].setName("MHWirth.Ram.VelocitySetPoint");
            sensorArray[2].setDescription("The variable denotes the calculated velocity from a machine’s control system, which is a result of an operator command given by the driller/assistant driller to reposition the machine.");
            sensorArray[2].setUnit("?");

            if(!RestRequest.isSensorRegistred(sensorArray[2]))
                RestRequest.postSensor(sensorArray[2]);

	    	sensorArray[3] = new Sensor();
            sensorArray[3].setUri(uriSensApp);
            sensorArray[3].setBackend("raw");
            sensorArray[3].setTemplate("Numerical");
            sensorArray[3].setSource("1002116");
    		sensorArray[3].setName("MHWirth.Ram.VelocityMeasuredValue");
            sensorArray[3].setDescription("The variable denotes the actual speed of machine movement as a result of the change in velocity set point.");
            sensorArray[3].setUnit("?");

            if(!RestRequest.isSensorRegistred(sensorArray[3]))
                RestRequest.postSensor(sensorArray[3]);

    		sensorArray[4] = new Sensor();
            sensorArray[4].setUri(uriSensApp);
            sensorArray[4].setBackend("raw");
            sensorArray[4].setTemplate("Numerical");
            sensorArray[4].setSource("1002311");
    		sensorArray[4].setName("MHWirth.DDM.HookLoad");
            sensorArray[4].setDescription("The hook load denotes the weight carried by the derrick drilling machine in metric tons (t) in any given point in time when the machine weight is filtered out.");
            sensorArray[4].setUnit("t");

            if(!RestRequest.isSensorRegistred(sensorArray[4]))
                RestRequest.postSensor(sensorArray[4]);

    		sensorArray[5] = new Sensor();
            sensorArray[5].setUri(uriSensApp);
            sensorArray[5].setBackend("raw");
            sensorArray[5].setTemplate("Numerical");
            sensorArray[5].setSource("1002127");
    		sensorArray[5].setName("MHWirth.Rig.MRUPosition");
            sensorArray[5].setDescription("The Motion Reference Unit (MRU) position denotes the relative, vertical position of the rig due to wave motions in mm.");
            sensorArray[5].setUnit("mm");

            if(!RestRequest.isSensorRegistred(sensorArray[5]))
                RestRequest.postSensor(sensorArray[5]);

    		sensorArray[6] = new Sensor();
            sensorArray[6].setUri(uriSensApp);
            sensorArray[6].setBackend("raw");
            sensorArray[6].setTemplate("Numerical");
            sensorArray[6].setSource("1002128");
	    	sensorArray[6].setName("MHWirth.Rig.MRUVelocity");
            sensorArray[6].setDescription("The Motion Reference Unit (MRU) velocity denotes the rig’s vertical velocity in mm/s.");
            sensorArray[6].setUnit("mm/s");

            if(!RestRequest.isSensorRegistred(sensorArray[6]))
                RestRequest.postSensor(sensorArray[6]);

        } catch (URISyntaxException e) {
            e.printStackTrace();
        }

    }


    public static void main(String[] args) {
        // Connection properties
        String urlSensApp = "http://127.0.0.1:8090";

        MHWirthSetPointDeviationDetectionUseCase example = new MHWirthSetPointDeviationDetectionUseCase(urlSensApp);
    }

}
