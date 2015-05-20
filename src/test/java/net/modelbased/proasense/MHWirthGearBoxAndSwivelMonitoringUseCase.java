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
package net.modelbased.proasense;

import net.modelbased.proasense.model.Sensor;
import net.modelbased.proasense.rest.RestRequest;

import java.net.URI;
import java.net.URISyntaxException;


public class MHWirthGearBoxAndSwivelMonitoringUseCase {

    public MHWirthGearBoxAndSwivelMonitoringUseCase(String url) {
        try {
            URI uriSensApp = new URI(url);

            // Set up virtual sensors
	    	Sensor[] sensorArray = new Sensor[8];

            sensorArray[0] = new Sensor();
            sensorArray[0].setUri(uriSensApp);
            sensorArray[0].setBackend("raw");
            sensorArray[0].setTemplate("Numerical");
            sensorArray[0].setSource("1000693");
            sensorArray[0].setName("MHWirth.DDM.DrillingRPM");
            sensorArray[0].setDescription("The variable denotes the rotation speed of the DDM’s main shaft in revolutions per minute (RPM).");
            sensorArray[0].setUnit("RPM");

            if(!RestRequest.isSensorRegistred(sensorArray[0]))
                RestRequest.postSensor(sensorArray[0]);

            sensorArray[1] = new Sensor();
            sensorArray[1].setUri(uriSensApp);
            sensorArray[1].setBackend("raw");
            sensorArray[1].setTemplate("Numerical");
            sensorArray[1].setSource("1000700");
		    sensorArray[1].setName("MHWirth.DDM.DrillingTorque");
            sensorArray[1].setDescription("The variable denotes the drilling torque readout of the DDM’s main shaft in kilo Newton meters (kNm).");
            sensorArray[1].setUnit("kNm");

            if(!RestRequest.isSensorRegistred(sensorArray[1]))
                RestRequest.postSensor(sensorArray[1]);

    		sensorArray[2] = new Sensor();
            sensorArray[2].setUri(uriSensApp);
            sensorArray[2].setBackend("raw");
            sensorArray[2].setTemplate("Numerical");
            sensorArray[2].setSource("1002311");
		    sensorArray[2].setName("MHWirth.DDM.HookLoad");
            sensorArray[2].setDescription("The hook load denotes the weight carried by the derrick drilling machine in metric tons (t) in any given point in time when the machine weight is filtered out.");
            sensorArray[2].setUnit("t");

            if(!RestRequest.isSensorRegistred(sensorArray[2]))
                RestRequest.postSensor(sensorArray[2]);

    		sensorArray[3] = new Sensor();
            sensorArray[3].setUri(uriSensApp);
            sensorArray[3].setBackend("raw");
            sensorArray[3].setTemplate("Numerical");
            sensorArray[3].setSource("1000695");
		    sensorArray[3].setName("MHWirth.DDM.GearLubeOilTemp");
            sensorArray[3].setDescription("The variable denotes the lube oil temperature in degrees Celsius present in the DDM gear box.");
            sensorArray[3].setUnit("C");

            if(!RestRequest.isSensorRegistred(sensorArray[3]))
                RestRequest.postSensor(sensorArray[3]);

            sensorArray[4] = new Sensor();
            sensorArray[4].setUri(uriSensApp);
            sensorArray[4].setBackend("raw");
            sensorArray[4].setTemplate("Numerical");
            sensorArray[4].setSource("1000692");
		    sensorArray[4].setName("MHWirth.DDM.GearBoxPressure");
            sensorArray[4].setDescription("The variable represents the lube oil pressure of the DDM’s gear box.");
            sensorArray[4].setUnit("?");

            if(!RestRequest.isSensorRegistred(sensorArray[4]))
                RestRequest.postSensor(sensorArray[4]);

    		sensorArray[5] = new Sensor();
            sensorArray[5].setUri(uriSensApp);
            sensorArray[5].setBackend("raw");
            sensorArray[5].setTemplate("Numerical");
            sensorArray[5].setSource("1000696");
		    sensorArray[5].setName("MHWirth.DDM.SwivelOilTemp");
            sensorArray[5].setDescription("The variable denotes the lube oil temperature in degrees Celsius present in the main swivel bearing oil bath of the machine.");
            sensorArray[5].setUnit("C");

            if(!RestRequest.isSensorRegistred(sensorArray[5]))
                RestRequest.postSensor(sensorArray[5]);

		    sensorArray[6] = new Sensor();
            sensorArray[6].setUri(uriSensApp);
            sensorArray[6].setBackend("raw");
            sensorArray[6].setTemplate("Numerical");
            sensorArray[6].setSource("1002123");
		    sensorArray[6].setName("MHWirth.DrillBit.WeightOnBit");
            sensorArray[6].setDescription("The parameter denotes the load in tons (put on the drill bit during drilling.");
            sensorArray[6].setUnit("t");

            if(!RestRequest.isSensorRegistred(sensorArray[6]))
                RestRequest.postSensor(sensorArray[6]);

		    sensorArray[7] = new Sensor();
            sensorArray[7].setUri(uriSensApp);
            sensorArray[7].setBackend("raw");
            sensorArray[7].setTemplate("Numerical");
            sensorArray[7].setSource("1033619");
		    sensorArray[7].setName("MHWirth.Env.OutdoorTemp");
            sensorArray[7].setDescription("This is a derived temperature of the surroundings at the rig in degrees Celcius.");
            sensorArray[7].setUnit("C");

            if(!RestRequest.isSensorRegistred(sensorArray[7]))
                RestRequest.postSensor(sensorArray[7]);

        } catch (URISyntaxException e) {
            e.printStackTrace();
        }

    }


    public static void main(String[] args) {
        // Connection properties
        String urlSensApp = "http://127.0.0.1:8090";

        MHWirthGearBoxAndSwivelMonitoringUseCase example = new MHWirthGearBoxAndSwivelMonitoringUseCase(urlSensApp);
    }

}
