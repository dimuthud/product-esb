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
import org.wso2.esb.integration.common.utils.ESBIntegrationTest;

import javax.ws.rs.core.Response;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;

/**
 * This class tests filter mediator behaviour using json path - on JSON request payloads
 */
public class FilterFromJSONPathTestCase extends ESBIntegrationTest {

    private Client jerseyClient = Client.create();
    private String contentType = "application/json";

    @BeforeClass(alwaysRun = true)
    public void setEnvironment() throws Exception {
        super.init();
        loadESBConfigurationFromClasspath("/artifacts/ESB/json/jsonfilterfromjsonpath.xml");
    }

    @AfterClass(alwaysRun = true)
    public void stop() throws Exception {
        jerseyClient.destroy();
        super.cleanup();
    }

    @Test(groups = {"wso2.esb"}, description = "Tests with filter mediator - JSON path Scenario - Filter condition true")
    public void testJSONFilterFromJSONPathConditionTrueTestScenario() throws Exception {

        String JSON_PAYLOAD = "{\"album\":\"Hello\",\"singer\":\"Peter\"}";

        WebResource webResource = jerseyClient
                .resource(getProxyServiceURLHttp("JSONPathFilterWithJSONProxy"));

        // sending post request
        ClientResponse postResponse = webResource.type(contentType)
                .post(ClientResponse.class, JSON_PAYLOAD);

        assertEquals(postResponse.getType().toString(), contentType, "Content-Type Should be application/json");
        assertEquals(postResponse.getStatus(), Response.Status.CREATED.getStatusCode(), "Response status should be 201");

        // Calling the GET request to verify Added album details
        ClientResponse getResponse = webResource.type(contentType)
                .get(ClientResponse.class);

        assertNotNull(getResponse, "Received Null response for while getting Music album details");
        assertEquals(getResponse.getEntity(String.class), JSON_PAYLOAD, "Response mismatch for HTTP Get call");
    }

    @Test(groups = {"wso2.esb"}, description = "Tests with filter mediator - JSON path Scenario Filter condition False",
            dependsOnMethods = "testJSONFilterFromJSONPathConditionTrueTestScenario")
    public void testJSONFilterFromJSONPathConditionFalseTestScenario() throws Exception {

        String JSON_PAYLOAD = "{\"album\":\"Moon\",\"singer\":\"Peter\"}";

        WebResource webResource = jerseyClient
                .resource(getProxyServiceURLHttp("FilterWithJSONProxy"));

        // sending post request
        ClientResponse postResponse = webResource.type(contentType)
                .post(ClientResponse.class, JSON_PAYLOAD);

        assertEquals(postResponse.getStatus(), Response.Status.ACCEPTED.getStatusCode(), "Response status should be 202");
    }
}
