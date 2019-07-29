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
package io.igia.caremanagement.service.impl.camunda;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.simple.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import io.igia.caremanagement.client.camunda.CamundaClient;
import io.igia.caremanagement.service.camunda.CaseInstanceService;
import io.igia.caremanagement.service.dto.camunda.CaseInstance;
import io.igia.caremanagement.service.dto.camunda.CaseInstanceRequest;
import io.igia.caremanagement.service.dto.camunda.CaseInstanceVariableRequest;
import io.igia.caremanagement.web.rest.errors.BadRequestAlertException;

@Service("camundaCaseInstanceService")
@Transactional
public class CaseInstanceServiceImpl implements CaseInstanceService {
	private final Logger log = LoggerFactory.getLogger(CaseInstanceServiceImpl.class);
	
	private static final String ENTITY_NAME = "caremanagementCaseDefinition";
	private static final String CASE = "Case";

	@Autowired
	private CamundaClient camundaClient;

	@Override
	public List<CaseInstance> getCaseInstances(String caseInstanceId, String mrn, Integer firstResult, Integer maxResults, String programId, Boolean active, Boolean completed) {
		log.info("get all case instances for mrn :{}", mrn);
		String caseProgramId = programId;
		if (programId != null) { caseProgramId = CASE + "_" + programId; }
		return camundaClient.getCaseInstances(caseInstanceId, mrn, firstResult, maxResults, caseProgramId, active, completed);
	}
	
	@Override
	public CaseInstance createCaseInstance(CaseInstanceRequest payload) {
		String programId = CASE + "_" + payload.getProgramId();
		List<CaseInstance> instances = camundaClient.getCaseInstance(payload.getMrn(), programId);
		if (instances.isEmpty()) {
			Map<String, Object> map = new HashMap<>();
		    map.put("variables", payload.getVariables());
			map.put("businessKey", payload.getMrn());
			return camundaClient.createCaseInstance(payload.getProgramId(), new JSONObject(map));
		} else {
			throw new BadRequestAlertException("Instance with mrn already present for a given program", ENTITY_NAME, "already present");
		}
		
	}

	@Override
	public void updateCaseInstance(String id, CaseInstanceVariableRequest payload) {
		log.info("modify case instance: {} ", id);
		camundaClient.updateCaseInstance(id, payload);
	}

	@Override
	public void closeCaseInstance(String mrn, String programId) {
		log.info("close case instance for mrn: {} and program Id: {}", mrn, programId);
		String id = CASE + "_" + programId;
		List<CaseInstance> instances = camundaClient.getCaseInstance(mrn, id);
		if (!instances.isEmpty()) {
			for (CaseInstance instance : instances) {
				camundaClient.closeCaseInstance(instance.getId());	
			}
		} else {
			throw new BadRequestAlertException("No case instance present for provided mrn in given program", ENTITY_NAME, "no case instance present");
		}
		
	}

	@Override
	public void terminateCaseInstance(String mrn, String programId) {
		log.info("terminate case instance for mrn: {} and program Id: {}", mrn, programId);
		String id = CASE + "_" + programId;
		List<CaseInstance> instances = camundaClient.getCaseInstance(mrn, id);
		if (!instances.isEmpty()) {
			for (CaseInstance instance : instances) {
				camundaClient.terminateCaseInstance(instance.getId());	
			}
		} else {
			throw new BadRequestAlertException("No case instance present for provided mrn in given program", ENTITY_NAME, "no case instance present");
		}
	}
}