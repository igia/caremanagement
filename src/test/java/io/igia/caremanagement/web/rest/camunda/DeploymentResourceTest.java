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
package io.igia.caremanagement.web.rest.camunda;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import io.igia.caremanagement.CaremanagementApp;
import io.igia.caremanagement.service.impl.camunda.DeploymentServiceImpl;
import io.igia.caremanagement.web.rest.camunda.DeploymentResource;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = CaremanagementApp.class)
public class DeploymentResourceTest {
	@MockBean
	private DeploymentServiceImpl deploymentServiceImpl;
    
	private MockMvc mvc;
	
	@Before
	public void setup() {
		MockitoAnnotations.initMocks(this);
		DeploymentResource resource = new DeploymentResource(deploymentServiceImpl);
		this.mvc = MockMvcBuilders.standaloneSetup(resource).build();
	}
	
	@Test
	public void testGetDeployment() throws Exception {
		mvc.perform(get("/api/deployment"))
				.andExpect(status().isOk());
	}
	
	@Test
	public void testDeleteDeployment() throws Exception {
		mvc.perform(delete("/api/deployment/{id}", "1"))
				.andExpect(status().isOk());
	}
}
