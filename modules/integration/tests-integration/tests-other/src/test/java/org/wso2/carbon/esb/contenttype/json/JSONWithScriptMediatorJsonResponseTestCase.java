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

import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.wso2.esb.integration.common.clients.registry.ResourceAdminServiceClient;
import org.wso2.esb.integration.common.utils.ESBIntegrationTest;

import javax.activation.DataHandler;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import static org.testng.Assert.*;

/**
 * This class can be used to test JSON payloads transformation within the ESB using Script mediator. basically we are
 * testing here the usage of getPayloadJSON and setPayloadJSON to test transform JSON payloads to transform
 * from one JSON format to another JSON format
 * <p/>
 * Note : That this tests is disabled due to : https://wso2.org/jira/browse/ESBJAVA-3423
 */
public class JSONWithScriptMediatorJsonResponseTestCase extends ESBIntegrationTest {

    private ResourceAdminServiceClient resourceAdminServiceClient;

    @BeforeClass(alwaysRun = true)
    public void setEnvironment() throws Exception {
        super.init();
        loadESBConfigurationFromClasspath("/artifacts/ESB/json/jsonwithscriptmediator.xml");
        resourceAdminServiceClient = new ResourceAdminServiceClient
                (contextUrls.getBackEndUrl(), context.getContextTenant().getContextUser().getUserName(),
                        context.getContextTenant().getContextUser().getPassword());

    }

    @AfterClass(alwaysRun = true)
    public void stop() throws Exception {
        resourceAdminServiceClient.deleteResource("/_system/config/repository/esb/transform.js");
        super.cleanup();
    }

    @Test(groups = {"wso2.esb"}, description = "Tests content transformation within the ESB using Script mediator",
            enabled = false)
    public void testWithScriptMediatorJSONGettersAndSettersScenario() throws Exception {

        resourceAdminServiceClient.addResource(
                "/_system/config/repository/esb/transform.js", "application/x-javascript", "js files",
                new DataHandler(new URL("file:///" + getClass().getResource(
                        "/artifacts/ESB/js/transform.js").getPath())));

        URL url = new URL(getProxyServiceURLHttp("locations"));

        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setDoOutput(true);
        conn.setRequestMethod("GET");
        conn.setRequestProperty("Content-Type", "application/json");

        conn.getOutputStream();

        assertTrue(conn.getResponseCode() == HttpURLConnection.HTTP_OK,
                "Response Code Mismatch. Expected 200 : Received " + conn.getResponseCode());

        BufferedReader br = new BufferedReader(new InputStreamReader(
                (conn.getInputStream()), "UTF-8"));

        String getResponse = br.readLine();

        br.close();  // closing the BufferedReader-Stream

        assertNotNull(getResponse, "Received Null response as the JSON response");
        assertEquals(getResponse, "" +
                "[" +
                "{\"id\":\"ID:7eaf7\"," +
                " \"tags\":[\"bar\", \"restaurant\", \"food\", \"establishment\"]," +
                " \"name\":\"Biaggio Cafe\"" +
                "}, " +
                "{\"id\":\"ID:3ef98\"," +
                " \"tags\":[\"food\", \"establishment\"]," +
                " \"name\":\"Doltone House\"}]",
                "Response mismatch. Expected JSON response not received after transformation with script mediator.");
    }
}
