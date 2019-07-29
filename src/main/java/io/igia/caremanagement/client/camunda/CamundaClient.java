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
package io.igia.caremanagement.client.camunda;

import java.util.List;
import java.util.Map;

import org.json.simple.JSONObject;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import io.igia.caremanagement.client.AuthorizedFeignClient;
import io.igia.caremanagement.service.dto.camunda.CaseActivityInstance;
import io.igia.caremanagement.service.dto.camunda.CaseExecution;
import io.igia.caremanagement.service.dto.camunda.CaseInstance;
import io.igia.caremanagement.service.dto.camunda.CaseInstanceVariableRequest;
import io.igia.caremanagement.service.dto.camunda.DeploymentResponse;
import io.igia.caremanagement.service.dto.camunda.TaskResponse;
import io.igia.caremanagement.service.dto.camunda.Variable;

@AuthorizedFeignClient(name = "camundaproxy")
public interface CamundaClient {

	@PostMapping(value = "engine-rest/task/{id}/complete", consumes = "application/json")
	void completeTask(@PathVariable(value = "id") String id, @RequestBody JSONObject request);
	
	@PostMapping(value = "engine-rest/task/{id}/complete", consumes = "application/json")
	void completeTask(@PathVariable(value = "id") String id);

	@PostMapping(value = "engine-rest/task", consumes = "application/json")
	List<TaskResponse> getTasks(@RequestBody JSONObject request, @RequestParam(value = "firstResult") int firstResult, @RequestParam(value = "maxResults") int maxResults);

	@GetMapping(value = "engine-rest/case-instance/{id}/variables")
	Map<String, Variable> getVariables(@PathVariable(value = "id") String id);
	
	@GetMapping(value = "engine-rest/case-execution")
	List<CaseExecution> getCaseExecutions(@RequestParam(value= "businessKey") String businessKey);
	
	@GetMapping(value = "engine-rest/case-execution")
	List<CaseExecution> getCaseExecutionsByCaseInstance(@RequestParam(value= "businessKey") String businessKey,@RequestParam(value= "caseInstanceId") String caseInstanceId);
	
	@GetMapping(value = "engine-rest/case-execution")
	List<CaseExecution> getCaseExecutions(@RequestParam(value= "businessKey") String businessKey,@RequestParam(value= "caseDefinitionKey") String caseDefinitionKey);

    @GetMapping(value = "engine-rest/case-execution")
    List<CaseExecution> getCaseExecutions(@RequestParam(value = "businessKey") String businessKey, @RequestParam(value = "caseDefinitionKey") String caseDefinitionKey, @RequestParam(value= "caseInstanceId") String caseInstanceId);
	
	@GetMapping(value = "engine-rest/case-instance")
	List<CaseInstance> getCaseInstance(@RequestParam(value= "businessKey") String businessKey);
	
	@GetMapping(value = "engine-rest/case-instance")
	List<CaseInstance> getCaseInstance(@RequestParam(value= "businessKey") String businessKey,@RequestParam(value= "caseDefinitionKey") String caseDefinitionKey);

	@PostMapping(value = "engine-rest/history/task", consumes = "application/json")
	List<TaskResponse> getHistoryTasks(@RequestBody JSONObject request,@RequestParam(value = "firstResult") int firstResult, @RequestParam(value = "maxResults") int maxResults);

	@PutMapping(value = "engine-rest/case-instance/{id}/variables/{variable}")
	void updateCaseInstanceVariable(@PathVariable(value = "id") String id,@PathVariable(value = "variable") String variable,@RequestBody JSONObject request);

	@PostMapping(value = "engine-rest/task/{id}/assignee")
	void updateAssignee(@PathVariable(value = "id") String id,@RequestBody JSONObject request);
	
	@PostMapping(value = "engine-rest/task/create")
	void createAdHocTask(@RequestBody JSONObject request);
	
	@GetMapping(value = "engine-rest/task/{id}")
	TaskResponse getTask(@PathVariable(value= "id") String id);
	
	
	@PostMapping(value = "engine-rest/task", consumes = "application/json")
	List<TaskResponse> getTasks(@RequestBody JSONObject request, @RequestParam(value = "firstResult") int firstResult);

	@PostMapping(value = "engine-rest/history/task", consumes = "application/json")
	List<TaskResponse> getHistoryTasks(@RequestBody JSONObject request, @RequestParam(value = "firstResult") int firstResult);

	@GetMapping(value="engine-rest/deployment")
	List<DeploymentResponse> getDeployments(@RequestParam(value = "id") String id, @RequestParam(value = "nameLike") String nameLike, @RequestParam(value = "name") String name, @RequestParam(value = "source") String source);
	
	@DeleteMapping(value = "engine-rest/deployment/{id}")
	void deleteDeployment(@PathVariable(value= "id") String id, @RequestParam(value = "cascade") Boolean cascade);
	
	@GetMapping(value="engine-rest/case-instance")
	List<CaseInstance> getCaseInstances(@RequestParam(value = "caseInstanceId") String caseInstanceId,
			@RequestParam(value = "businessKey") String mrn,
			@RequestParam(value = "firstResult") Integer firstResult,
			@RequestParam(value = "maxResults") Integer maxResults,
			@RequestParam(value = "caseDefinitionKey") String programId,
			@RequestParam(value = "active") Boolean active,
			@RequestParam(value = "completed") Boolean completed);
	
	@GetMapping(value = "engine-rest/case-instance/{id}/variables")
	Map<String, Variable> getCaseInstanceVariables(@PathVariable("id") String id);

	@PostMapping(value = "engine-rest/case-definition/key/Case_{key}/create", consumes = "application/json")
	CaseInstance createCaseInstance(@PathVariable(value = "key") String key, @RequestBody JSONObject request);

	@PostMapping(value = "engine-rest/case-instance/{id}/variables", consumes = "application/json")
	void updateCaseInstance(@PathVariable("id") String id, @RequestBody CaseInstanceVariableRequest payload);
	
	@PostMapping(value = "engine-rest/case-instance/{id}/close", consumes="application/json")
	void closeCaseInstance(@PathVariable("id") String id);
	
	@PostMapping(value = "engine-rest/case-instance/{id}/terminate", consumes="application/json")
	void terminateCaseInstance(@PathVariable("id") String id);
	
	@GetMapping(value = "engine-rest/history/case-activity-instance")
	List<CaseActivityInstance> getCaseActivityInstance(@RequestParam(value = "completed") boolean completed,
	        @RequestParam(value = "caseInstanceId") String caseInstanceId,
	        @RequestParam(value = "createdBefore",required=false) String createdBefore,
	        @RequestParam(value = "createdAfter",required=false) String createdAfter,
	        @RequestParam(value = "endedBefore",required=false) String endedBefore,
	        @RequestParam(value = "endedAfter",required=false) String endedAfter);
}
