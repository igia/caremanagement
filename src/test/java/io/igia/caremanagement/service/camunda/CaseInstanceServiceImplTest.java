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
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.doNothing;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.simple.JSONObject;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import io.igia.caremanagement.client.camunda.CamundaClient;
import io.igia.caremanagement.service.dto.camunda.CaseInstance;
import io.igia.caremanagement.service.dto.camunda.CaseInstanceRequest;
import io.igia.caremanagement.service.dto.camunda.CaseInstanceVariableRequest;
import io.igia.caremanagement.service.dto.camunda.Variable;
import io.igia.caremanagement.service.impl.camunda.CaseInstanceServiceImpl;

public class CaseInstanceServiceImplTest {
	@InjectMocks
	private CaseInstanceServiceImpl caseInstanceServiceImpl;

    @Mock
    private CamundaClient camundaClient;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
   	public void testCaseInstances() throws Exception {
    	CaseInstance caseInstance1 = new CaseInstance();
    	caseInstance1.setId("1");
    	caseInstance1.setBusinessKey("mrn004");
    	caseInstance1.setActive("true");
    	caseInstance1.setCompleted("false");
    	caseInstance1.setTerminated("false");
    	
    	CaseInstance caseInstance2 = new CaseInstance();
    	caseInstance2.setId("2");
    	caseInstance2.setBusinessKey("mrn003");
    	caseInstance2.setActive("true");
    	caseInstance2.setCompleted("false");
    	caseInstance2.setTerminated("false");
    	
    	List<CaseInstance> list = new ArrayList<CaseInstance>();
    	list.add(caseInstance1);
    	list.add(caseInstance2);
       	
   		Mockito.when(camundaClient.getCaseInstances(null, null, null, null, null, null, null)).thenReturn(list);
   		List<CaseInstance> result = caseInstanceServiceImpl.getCaseInstances(null, null, null, null, null, null, null);
   		assertEquals(2, result.size());
   		if (!result.isEmpty()) {
			assertEquals("mrn004", result.get(0).getBusinessKey());
			assertEquals("1", result.get(0).getId());
			assertEquals("true", result.get(0).getActive());
			assertEquals("false", result.get(0).getTerminated());
			
			assertEquals("mrn003", result.get(1).getBusinessKey());
			assertEquals("2", result.get(1).getId());
			assertEquals("true", result.get(1).getActive());
			assertEquals("false", result.get(1).getTerminated());
		}
   	}
    
    @Test
	public void testCaseInstancesByMrn() throws Exception {
    	CaseInstance caseInstance1 = new CaseInstance();
    	caseInstance1.setId("1");
    	caseInstance1.setBusinessKey("mrn004");
    	caseInstance1.setActive("true");
    	caseInstance1.setCompleted("false");
    	caseInstance1.setTerminated("false");
    	
    	CaseInstance caseInstance2 = new CaseInstance();
    	caseInstance2.setId("2");
    	caseInstance2.setBusinessKey("mrn003");
    	caseInstance2.setActive("true");
    	caseInstance2.setCompleted("false");
    	caseInstance2.setTerminated("false");
    	
    	List<CaseInstance> list = new ArrayList<CaseInstance>();
    	list.add(caseInstance1);
    	list.add(caseInstance2);
    	
		Mockito.when(camundaClient.getCaseInstances(null, null, null, null, null, null, null)).thenReturn(list);
		List<CaseInstance> result = caseInstanceServiceImpl.getCaseInstances(null, null, null, null, null, null, null);
		assertEquals(2, result.size());
		
		List<CaseInstance> newList = new ArrayList<CaseInstance>();
    	list.add(caseInstance1);
    	
    	Mockito.when(camundaClient.getCaseInstances(null, "mrn004", null, null, null, null, null)).thenReturn(newList);
		List<CaseInstance> result2 = caseInstanceServiceImpl.getCaseInstances(null, "mrn004", null, null, null, null, null);
		if (!result2.isEmpty()) {
			assertEquals(1, result2.size());
			assertEquals("mrn004", result2.get(0).getBusinessKey());
			assertEquals("1", result2.get(0).getId());
			assertEquals("true", result2.get(0).getActive());
			assertEquals("false", result2.get(0).getTerminated());
		}
	}
    
    @Test
   	public void testCaseInstanceById() throws Exception {
    	String caseInstanceId = "1";
       	CaseInstance caseInstance1 = new CaseInstance();
       	caseInstance1.setId(caseInstanceId);
       	caseInstance1.setBusinessKey("mrn004");
       	caseInstance1.setActive("true");
       	caseInstance1.setCompleted("false");
       	caseInstance1.setTerminated("false");
       	
       	List<CaseInstance> list = new ArrayList<CaseInstance>();
    	list.add(caseInstance1);
       	
   		Mockito.when(camundaClient.getCaseInstances(caseInstanceId, null, null, null, null, null, null)).thenReturn(list);
   		List<CaseInstance> result = caseInstanceServiceImpl.getCaseInstances(caseInstanceId, null, null, null, null, null, null);
		assertEquals("mrn004", result.get(0).getBusinessKey());
		assertEquals("1", result.get(0).getId());
		assertEquals("true", result.get(0).getActive());
		assertEquals("false", result.get(0).getTerminated());
   	}
    
    @Test
   	public void testCreateCaseInstance() throws Exception {
    	String mrn = "mrn004";
    	String programId = "P1";
    	
    	Variable var1 = new Variable();
    	var1.setType("String");
    	var1.setValue("service-account-internal");
    	
    	Variable var2 = new Variable();
    	var2.setType("Integer");
    	var2.setValue(91);
    	
    	Map<String, Variable> map = new HashMap<String, Variable>();
    	map.put("Navigator", var1);
    	map.put("GFR", var2);
    	
    	CaseInstanceRequest payload = new CaseInstanceRequest();
    	payload.setMrn(mrn);
    	payload.setVariables(map);
    	payload.setProgramId(programId);
    	List<CaseInstance> list = new ArrayList<CaseInstance>();
    	
   		Mockito.when(camundaClient.getCaseInstance(mrn, programId)).thenReturn(list);
   		
   		Map<String, Object> map2 = new HashMap<>();
	    map2.put("variables", payload.getVariables());
		map2.put("businessKey", payload.getMrn());
//		map2.put("programId", payload.getProgramId());
		
		CaseInstance caseInstance1 = new CaseInstance();
       	caseInstance1.setId("1");
       	caseInstance1.setBusinessKey(mrn);
       	caseInstance1.setActive("true");
       	caseInstance1.setCompleted("false");
       	caseInstance1.setTerminated("false");
		
   		Mockito.when(camundaClient.createCaseInstance(payload.getProgramId(), new JSONObject(map2))).thenReturn(caseInstance1);
		CaseInstance instance = caseInstanceServiceImpl.createCaseInstance(payload);
   		
   		assertNotNull(instance.getId());
   		assertEquals("1", instance.getId());
   		assertEquals(mrn, instance.getBusinessKey());
   		assertEquals("true", instance.getActive());
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
    	Mockito.when(camundaClient.getCaseInstanceVariables("1")).thenReturn(map);
    	Map<String, Variable> vars = camundaClient.getCaseInstanceVariables("1");
    	assertEquals(2, vars.size());
    	
    	String id = "1";
    	CaseInstanceVariableRequest payload = new CaseInstanceVariableRequest();
		List<String> deletions = new ArrayList<String>();
		deletions.add("amount");
		payload.setDeletions(deletions);
		
    	doNothing().when(camundaClient).updateCaseInstance(id, payload);
    	caseInstanceServiceImpl.updateCaseInstance(id, payload);
    	
    	map.remove("amount");
    	
    	Mockito.when(camundaClient.getCaseInstanceVariables("1")).thenReturn(map);
    	Map<String, Variable> updatedVars = camundaClient.getCaseInstanceVariables("1");
    	assertEquals(1, updatedVars.size());
   	}
    
    @Test
   	public void testCloseCaseInstanceByMrn() throws Exception {
    	String mrn = "mrn004";
    	String programId = "P1";
       	CaseInstance caseInstance1 = new CaseInstance();
       	caseInstance1.setId("1");
       	caseInstance1.setBusinessKey("mrn004");
       	caseInstance1.setActive("true");
       	caseInstance1.setCompleted("false");
       	caseInstance1.setTerminated("false");
       	
       	List<CaseInstance> list = new ArrayList<CaseInstance>();
    	list.add(caseInstance1);
    	
   		Mockito.when(camundaClient.getCaseInstance(mrn, "Case_" + programId)).thenReturn(list);
   		caseInstanceServiceImpl.closeCaseInstance(mrn, programId);
   		
   		List<CaseInstance> updatedList = new ArrayList<CaseInstance>();
    	
   		Mockito.when(camundaClient.getCaseInstances(null, mrn, null, null, null, null, null)).thenReturn(updatedList);
   		List<CaseInstance> newList = caseInstanceServiceImpl.getCaseInstances(null, mrn, null, null, null, null, null);
   		assertEquals(0, newList.size());
   	}
    
    @Test
   	public void testTerminateCaseInstanceByMrn() throws Exception {
    	String mrn = "mrn004";
    	String programId = "P1";
       	CaseInstance caseInstance1 = new CaseInstance();
       	caseInstance1.setId("1");
       	caseInstance1.setBusinessKey("mrn004");
       	caseInstance1.setActive("true");
       	caseInstance1.setCompleted("false");
       	caseInstance1.setTerminated("false");
       	
       	List<CaseInstance> list = new ArrayList<CaseInstance>();
    	list.add(caseInstance1);
    	
   		Mockito.when(camundaClient.getCaseInstance(mrn, "Case_" + programId)).thenReturn(list);
   		assertEquals("false", list.get(0).getTerminated());
   		caseInstanceServiceImpl.terminateCaseInstance(mrn, programId);
   		
   		CaseInstance updatedCaseInstance = new CaseInstance();
   		updatedCaseInstance.setId("1");
   		updatedCaseInstance.setBusinessKey("mrn004");
   		updatedCaseInstance.setActive("true");
   		updatedCaseInstance.setCompleted("false");
   		updatedCaseInstance.setTerminated("true");
   		List<CaseInstance> updatedList = new ArrayList<CaseInstance>();
   		updatedList.add(updatedCaseInstance);
    	
   		Mockito.when(camundaClient.getCaseInstances(null, mrn, null, null, null, null, null)).thenReturn(updatedList);
   		List<CaseInstance> newList = caseInstanceServiceImpl.getCaseInstances(null, mrn, null, null, null, null, null);
   		assertEquals(1, newList.size());
   		assertEquals("true", updatedList.get(0).getTerminated());
   	}
}
