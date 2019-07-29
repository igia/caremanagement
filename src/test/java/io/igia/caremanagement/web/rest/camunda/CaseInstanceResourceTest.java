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

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
import io.igia.caremanagement.service.dto.camunda.CaseInstanceRequest;
import io.igia.caremanagement.service.dto.camunda.CaseInstanceVariableRequest;
import io.igia.caremanagement.service.dto.camunda.Variable;
import io.igia.caremanagement.service.impl.camunda.CaseInstanceServiceImpl;
import io.igia.caremanagement.web.rest.TestUtil;
import io.igia.caremanagement.web.rest.camunda.CaseInstanceResource;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = CaremanagementApp.class)
public class CaseInstanceResourceTest {
	@MockBean
	private CaseInstanceServiceImpl caseInstanceServiceImpl;
    
	private MockMvc mvc;
	
	@Before
	public void setup() {
		MockitoAnnotations.initMocks(this);
		CaseInstanceResource resource = new CaseInstanceResource(caseInstanceServiceImpl);
		this.mvc = MockMvcBuilders.standaloneSetup(resource).build();
	}
	
	@Test
	public void testGetCaseInstance() throws Exception {
		mvc.perform(get("/api/case-instance"))
				.andExpect(status().isOk());
	}
	
	@Test
	public void testCreateCaseInstance() throws Exception {
		CaseInstanceRequest req = new CaseInstanceRequest();
		String key = "P1";
		
		Variable var = new Variable();
		var.setType("String");
		var.setValue("80");
		
		Variable var2 = new Variable();
		var2.setType("String");
		var2.setValue("90");
		
		Map<String, Variable> variables = new HashMap<>();
		variables.put("Navigator", var);
		variables.put("PCP", var2);
		req.setMrn("mrn005");
		req.setVariables(variables);
		req.setProgramId(key);
		
		mvc.perform(post("/api/case-definition/create", key).contentType(TestUtil.APPLICATION_JSON_UTF8)
				.content(TestUtil.convertObjectToJsonBytes(req))).andExpect(status().isOk());
	}

	@Test
	public void testUpdateCaseInstance() throws Exception {
		Variable var1 = new Variable();
    	var1.setType("Double");
    	var1.setValue(10.99);
    	
    	Variable var2 = new Variable();
    	var2.setType("String");
    	var2.setValue("PSACE-5342");
    	
    	Map<String, Variable> map = new HashMap<String, Variable>();
    	map.put("amount", var1);
    	map.put("invoiceNumber", var2);
    	
    	String id = "1";
    	CaseInstanceVariableRequest req = new CaseInstanceVariableRequest();
		List<String> deletions = new ArrayList<String>();
		deletions.add("amount");
		
		mvc.perform(post("/api/case-instance/{id}/update", id).contentType(TestUtil.APPLICATION_JSON_UTF8)
				.content(TestUtil.convertObjectToJsonBytes(req))).andExpect(status().isOk());
	}

	@Test
	public void testCloseCaseInstance() throws Exception {
		mvc.perform(post("/api/case-instance/close").param("mrn", "mrn004").param("programId", "P1")).andExpect(status().isOk());
	}

	@Test
	public void testTerminateCaseInstance() throws Exception {
		mvc.perform(post("/api/case-instance/terminate").param("mrn", "mrn004").param("programId", "P1")).andExpect(status().isOk());
	}
}
