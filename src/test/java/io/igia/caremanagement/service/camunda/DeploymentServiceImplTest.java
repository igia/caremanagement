/**
 * This Source Code Form is subject to the terms of the Mozilla Public License, v.
 * 2.0 with a Healthcare Disclaimer.
 * A copy of the Mozilla Public License, v. 2.0 with the Healthcare Disclaimer can
 * be found under the top level directory, named LICENSE.
 * If a copy of the MPL was not distributed with this file, You can obtain one at
 * http://mozilla.org/MPL/2.0/.
 * If a copy of the Healthcare Disclaimer was not distributed with this file, You
 * can obtain one at the project website https://github.com/igia.
 *
 * Copyright (C) 2018-2019 Persistent Systems, Inc.
 */
package io.igia.caremanagement.service.camunda;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.doNothing;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import io.igia.caremanagement.client.camunda.CamundaClient;
import io.igia.caremanagement.service.dto.camunda.DeploymentResponse;
import io.igia.caremanagement.service.impl.camunda.DeploymentServiceImpl;

public class DeploymentServiceImplTest {
	@InjectMocks
	private DeploymentServiceImpl deploymentServiceImpl;

    @Mock
    private CamundaClient camundaClient;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
   	public void testGetDeployment() throws Exception {
    	Date date = new Date();
    	DeploymentResponse res = new DeploymentResponse();
    	res.setId("1");
    	res.setName("camunda");
    	res.setDeploymentTime(date);
    	
    	List<DeploymentResponse> list = new ArrayList<DeploymentResponse>();
    	list.add(res);
   		Mockito.when(camundaClient.getDeployments(null, null, null, null)).thenReturn(list);
   		List<DeploymentResponse> result = deploymentServiceImpl.getDeployments(null, null, null, null);
   		assertEquals(1, result.size());
   		if (!result.isEmpty()) {
			assertEquals("1", result.get(0).getId());
			assertEquals(date, result.get(0).getDeploymentTime());
			assertEquals("camunda", result.get(0).getName());
		}
   	}
    
    @Test
   	public void testDeleteDeployment() throws Exception {
    	DeploymentResponse res = new DeploymentResponse();
    	res.setId("1");
    	res.setName("camunda");
    	res.setDeploymentTime(new Date());
    	
    	List<DeploymentResponse> list = new ArrayList<DeploymentResponse>();
    	list.add(res);
   		Mockito.when(camundaClient.getDeployments(null, null, null, null)).thenReturn(list);
   		assertEquals(1, list.size());
   		
   		doNothing().when(camundaClient).deleteDeployment("1", null);
   		deploymentServiceImpl.deleteDeployment("1", null);
   		
   		List<DeploymentResponse> updatedList = new ArrayList<DeploymentResponse>();
   		Mockito.when(camundaClient.getDeployments(null, null, null, null)).thenReturn(updatedList);
   		List<DeploymentResponse> result = camundaClient.getDeployments(null, null, null, null);
   		assertEquals(0, result.size());
   	}
}
