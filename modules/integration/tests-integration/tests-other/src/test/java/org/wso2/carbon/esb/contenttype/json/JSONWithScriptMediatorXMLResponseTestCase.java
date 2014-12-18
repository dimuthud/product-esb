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

import org.apache.axiom.om.OMElement;
import org.testng.annotations.*;
import org.wso2.esb.integration.common.clients.registry.ResourceAdminServiceClient;
import org.wso2.esb.integration.common.utils.ESBIntegrationTest;
import org.wso2.esb.integration.common.utils.clients.axis2client.AxisServiceClient;

import javax.activation.DataHandler;
import java.net.URL;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;

/**
 * This class can be used to test JSON payloads transformation within the ESB using Script mediator. basically we are
 * testing here the usage of getPayloadJSON and setPayloadJSON to test JSON to XML transformation performed by
 * the Script mediator
 * <p/>
 * Note : That this tests is disabled due to : https://wso2.org/jira/browse/ESBJAVA-3423
 */
public class JSONWithScriptMediatorXMLResponseTestCase extends ESBIntegrationTest {

    private ResourceAdminServiceClient resourceAdminServiceClient;

    @BeforeClass(alwaysRun = true)
    public void setEnvironment() throws Exception {
        super.init();
        loadESBConfigurationFromClasspath("/artifacts/ESB/json/jsonwithscriptmediatorxmlresponse.xml");
        resourceAdminServiceClient = new ResourceAdminServiceClient
                (contextUrls.getBackEndUrl(), context.getContextTenant().getContextUser().getUserName(),
                        context.getContextTenant().getContextUser().getPassword());

    }

    @AfterClass(alwaysRun = true)
    public void stop() throws Exception {
        resourceAdminServiceClient.deleteResource("/_system/config/repository/esb/transform.js");
        super.cleanup();
    }

    @Test(groups = {"wso2.esb"}, description = "Tests content transformation within the ESB using Script mediator " +
            "- response should be in xml format", enabled = false)
    public void testWithScriptMediatorJSONGettersAndSettersScenario() throws Exception {

        resourceAdminServiceClient.addResource(
                "/_system/config/repository/esb/transform.js", "application/x-javascript", "js files",
                new DataHandler(new URL("file:///" + getClass().getResource(
                        "/artifacts/ESB/js/transform.js").getPath())));

        AxisServiceClient axisServiceClient = new AxisServiceClient();

        String proxyServiceEP = getProxyServiceURLHttp("locations");
        OMElement response = axisServiceClient.sendReceive(null, proxyServiceEP, "urn:mediate");

        assertNotNull(response, "Received Null response for while getting Music album details");
        assertEquals(response.toString(),
                "<jsonArray>" +
                        "<jsonElement>" +
                        "<id>ID:7eaf7</id>" +
                        "<tags>bar</tags>" +
                        "<tags>restaurant</tags>" +
                        "<tags>food</tags>" +
                        "<tags>establishment</tags>" +
                        "<name>Biaggio Cafe</name>" +
                        "</jsonElement>" +
                        "<jsonElement>" +
                        "<id>ID:3ef98</id>" +
                        "<tags>food</tags>" +
                        "<tags>establishment</tags>" +
                        "<name>Doltone House</name>" +
                        "</jsonElement>" +
                        "</jsonArray>", "Response mismatch. Expected XML response not received after transformation.");

    }
}
