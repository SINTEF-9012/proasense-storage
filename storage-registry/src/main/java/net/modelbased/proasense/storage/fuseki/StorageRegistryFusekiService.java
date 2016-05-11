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
package net.modelbased.proasense.storage.fuseki;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

@Path("/")
public class StorageRegistryFusekiService {
    private Properties serverProperties;
    private String MONGODB_URL;
    private String MONGODB_DATABASE;


    public StorageRegistryFusekiService() {
        // Get server properties
        serverProperties = loadServerProperties();

        // MongoDB event reader configuration properties
        this.MONGODB_URL = serverProperties.getProperty("proasense.storage.mongodb.url");
        this.MONGODB_DATABASE = serverProperties.getProperty("proasense.storage.mongodb.database");
    }


    @GET
    @Path("/server/status")
    @Produces(MediaType.TEXT_PLAIN)
    public Response getServerStatus() {
        String result = "ProaSense Storage Reader Service running...";

        // Return HTTP response 200 in case of success
        return Response.status(200).entity(result).build();
    }


    @GET
    @Path("/server/properties")
    @Produces(MediaType.TEXT_PLAIN)
    public Response printServerProperties() {
        String result = this.serverProperties.toString();

        // Return HTTP response 200 in case of success
        return Response.status(200).entity(result).build();
    }


    private Properties loadServerProperties() {
        serverProperties = new Properties();
        String propFilename = "server.properties";
        InputStream inputStream = getClass().getClassLoader().getResourceAsStream(propFilename);

        try {
            if (inputStream != null) {
                serverProperties.load(inputStream);
            } else
                throw new FileNotFoundException("Property file: '" + propFilename + "' not found in classpath.");
        }
        catch (IOException e) {
            System.out.println(e.getClass().getName() + ": " + e.getMessage());
        }

        return serverProperties;
    }

}
