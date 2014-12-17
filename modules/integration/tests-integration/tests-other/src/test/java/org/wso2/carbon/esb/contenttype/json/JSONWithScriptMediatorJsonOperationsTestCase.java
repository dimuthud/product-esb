/*
*Copyright (c) 2005-2010, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
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
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;
import org.wso2.esb.integration.common.clients.registry.ResourceAdminServiceClient;
import org.wso2.esb.integration.common.utils.ESBIntegrationTest;

import javax.activation.DataHandler;
import java.net.URL;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;

/**
 * This class can be used to test JSON payloads transformation testing with json operations like add, delete etc.
 * within the ESB using Script mediator. basically we are  testing here the usage of getPayloadJSON and
 * setPayloadJSON to test JSON to JSON transformation performed by the Script mediator
 *
 * Note : That this tests is disabled due to : https://wso2.org/jira/browse/ESBJAVA-3423
 */
public class JSONWithScriptMediatorJsonOperationsTestCase extends ESBIntegrationTest {

    private Client client = Client.create();
    private ResourceAdminServiceClient resourceAdminServiceClient;

    @BeforeTest(alwaysRun = true)
    public void setEnvironment() throws Exception {
        super.init();
        loadESBConfigurationFromClasspath("/artifacts/ESB/json/jsonwithscriptmediatorjsonoperaions.xml");
        resourceAdminServiceClient = new ResourceAdminServiceClient
                (contextUrls.getBackEndUrl(), context.getContextTenant().getContextUser().getUserName(),
                        context.getContextTenant().getContextUser().getPassword());

    }

    @AfterTest(alwaysRun = true)
    public void stop() throws Exception {
        client.destroy();
        super.cleanup();
    }

    @Test(groups = {"wso2.esb"}, description = "Tests content transformation within the ESB using Script mediator - " +
            "Testing JSON operations - add, delete", enabled = false)
    public void testWithScriptMediatorJSONGettersAndSettersScenario() throws Exception {

        resourceAdminServiceClient.addResource(
                "/_system/config/repository/esb/transform.js", "application/x-javascript", "js files",
                new DataHandler(new URL("file:///" + getClass().getResource(
                        "/artifacts/ESB/js/transform.js").getPath())));

        WebResource webResource = client
                .resource(getProxyServiceURLHttp("locations"));

        // Calling the GET request to verify by default Added album details
        ClientResponse getResponse = webResource.type("application/json")
                .get(ClientResponse.class);

        // TODO - Once https://wso2.org/jira/browse/ESBJAVA-3423 is fixed.
        assertNotNull(getResponse, "Received Null response for while getting Music album details");
        assertEquals(getResponse, "[\n" +
                "   {\n" +
                "      \"name\":\"Biaggio Cafe\",\n" +
                "      \"tags\":[\"bar\", \"restaurant\", \"food\", \"establishment\", \"pub\"],\n" +
                "      \"id_str\":\"ID:7eaf7\"\n" +
                "   },\n" +
                "   {\n" +
                "      \"name\":\"Doltone House\",\n" +
                "      \"tags\":[\"food\", \"establishment\", \"pub\"],\n" +
                "      \"id_str\":\"ID:3ef98\"\n" +
                "   }\n" +
                "]",
                "Response mismatch. Expected JSON response not received after transformation.");
    }

}
