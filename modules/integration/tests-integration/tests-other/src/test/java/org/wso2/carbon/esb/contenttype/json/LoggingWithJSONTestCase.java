/*
*Copyright (c) 2014, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
*
*WSO2 Inc. licenses this file to you under the Apache License,
*Version 2.0 (the "License"); you may not use this file except
*in compliance with the License.
*You may obtain a copy of the License at
*
*http://www.apache.org/licenses/LICENSE-2.0
*
*Unless required by applicable law or agreed to in writing,
*software distributed under the License is distributed on an
*"AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
*KIND, either express or implied.  See the License for the
*specific language governing permissions and limitations
*under the License.
*/

package org.wso2.carbon.esb.contenttype.json;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.carbon.integration.common.admin.client.LogViewerClient;
import org.wso2.carbon.logging.view.stub.types.carbon.LogEvent;
import org.wso2.esb.integration.common.utils.ESBIntegrationTest;

import javax.ws.rs.core.Response;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertTrue;

/**
 * This test class is to verify the logging functionality of JSON payloads
 */
public class LoggingWithJSONTestCase extends ESBIntegrationTest {

    private LogViewerClient logViewer;
    private boolean isLogExists = false;
    private Client jerseyClient = Client.create();

    @BeforeClass(alwaysRun = true)
    public void setEnvironment() throws Exception {
        super.init();
        loadESBConfigurationFromClasspath("/artifacts/ESB/json/loggingwithjson.xml");
        logViewer = new LogViewerClient(context.getContextUrls().getBackEndUrl(), sessionCookie);
    }

    @AfterClass(alwaysRun = true)
    public void stop() throws Exception {
        jerseyClient.destroy();
        super.cleanup();
    }


    @Test(groups = {"wso2.esb"}, description = "Tests logging functionality of JSON payloads")
    public void testJSONLoggingTestScenario() throws Exception {

        String JSON_PAYLOAD = "{\"album\":\"Desperado\",\"singer\":\"Eagles\"}";
        String contentType = "application/json";

        WebResource webResource = jerseyClient
                .resource(getProxyServiceURLHttp("LoggingWithJSONProxy"));

        int beforeLogSize = logViewer.getAllRemoteSystemLogs().length;

        // sending post request
        ClientResponse postResponse = webResource.type(contentType)
                .post(ClientResponse.class, JSON_PAYLOAD);

        Thread.sleep(3000);

        LogEvent[] logs = logViewer.getAllRemoteSystemLogs();
        int afterLogSize = logs.length;

        String requestMessage = "Direction: request, JSON-RequestPayload = " + JSON_PAYLOAD;

        for (int i = (afterLogSize - beforeLogSize); i >= 0; i--) {
            if (logs[i].getMessage().contains(requestMessage)) {
                isLogExists = true;
                break;
            }
        }

        //This is to verify that the logging of json request was successful.
        assertTrue(isLogExists, "Logging of JSON request payload failed");

        assertTrue(postResponse.getType().toString().contains(contentType),
                "Content-Type Should be application/json");
        assertEquals(postResponse.getStatus(), Response.Status.CREATED.getStatusCode(), "Response status should be 201");

        beforeLogSize = logViewer.getAllRemoteSystemLogs().length;
        isLogExists = false;

        // Calling the GET request to verify Added album details
        ClientResponse getResponse = webResource.type(contentType)
                .get(ClientResponse.class);

        Thread.sleep(3000);

        afterLogSize = logs.length;

        String responseMessage = "<soapenv:Body><jsonObject><album>Desperado</album><singer>Eagles</singer>" +
                "</jsonObject></soapenv:Body>";

        for (int i = (afterLogSize - beforeLogSize); i >= 0; i--) {
            if (logs[i].getMessage().contains(responseMessage)) {
                isLogExists = true;
                break;
            }
        }

        //This is to verify that the logging of json response was successful.
        assertTrue(isLogExists, "Logging of JSON request payload failed");

        assertNotNull(getResponse, "Received Null response for while getting Music album details");
        assertEquals(getResponse.getEntity(String.class), JSON_PAYLOAD, "Response mismatch for HTTP Get call");
    }
}