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

import java.io.IOException;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.codahale.metrics.annotation.Timed;

import io.igia.caremanagement.service.camunda.CaseInstanceService;
import io.igia.caremanagement.service.dto.camunda.CaseInstance;
import io.igia.caremanagement.service.dto.camunda.CaseInstanceRequest;
import io.igia.caremanagement.service.dto.camunda.CaseInstanceVariableRequest;


@RestController("camundaCaseInstanceResource")
@RequestMapping("/api")
public class CaseInstanceResource {
	
	private final Logger log = LoggerFactory.getLogger(CaseInstanceResource.class);

	private final CaseInstanceService caseInstanceService;
	
	public CaseInstanceResource(CaseInstanceService caseInstanceService) {
		this.caseInstanceService = caseInstanceService;
	}
	
	@GetMapping("/case-instance")
	@Timed
	public ResponseEntity<List<CaseInstance>> getCaseInstances(@RequestParam(value = "caseInstanceId", required = false) String caseInstanceId,
			@RequestParam(value = "mrn", required = false) String mrn,
			@RequestParam(value = "firstResult", required = false) Integer firstResult,
			@RequestParam(value = "maxResults", required = false) Integer maxResults,
			@RequestParam(value = "programId", required = false) String programId,
			@RequestParam(value = "active", required = false) Boolean active,
			@RequestParam(value = "completed", required = false) Boolean completed) {
		List<CaseInstance> response = caseInstanceService.getCaseInstances(caseInstanceId, mrn, firstResult, maxResults, programId, active, completed);
		return new ResponseEntity<>(response, HttpStatus.OK);
	}
	
	@PostMapping("/case-definition/create")
	@Timed
	public ResponseEntity<CaseInstance> createCaseInstance(@Validated @RequestBody CaseInstanceRequest payload) {
		log.info("REST request to create case instance of case definition id: Case_{}", payload.getProgramId());
		CaseInstance response = caseInstanceService.createCaseInstance(payload);
		return new ResponseEntity<>(response, HttpStatus.OK);
	}

	@PostMapping("/case-instance/{id}/update")
	@Timed
	public ResponseEntity<Void> updateCaseInstance(@PathVariable String id, @Validated @RequestBody CaseInstanceVariableRequest payload) {
		log.info("REST request to modify case instance: {}", id);
		caseInstanceService.updateCaseInstance(id, payload);
		return new ResponseEntity<>(HttpStatus.OK);
	}
	
	@PostMapping("/case-instance/close")
	@Timed
	public ResponseEntity<Void> closeCaseInstance(@RequestParam("mrn") String mrn, @RequestParam("programId") String programId) {
		log.info("REST request to close case instance for mrn: {} and program Id: {}", mrn, programId);
		caseInstanceService.closeCaseInstance(mrn, programId);
		return new ResponseEntity<>(HttpStatus.OK);
	}
	
	@PostMapping("/case-instance/terminate")
	@Timed
	public ResponseEntity<Void> terminateCaseInstance(@RequestParam("mrn") String mrn, @RequestParam("programId") String programId) {
		log.info("REST request to terminate case instance for mrn: {} and program Id: {}", mrn, programId);
		caseInstanceService.terminateCaseInstance(mrn, programId);
		return new ResponseEntity<>(HttpStatus.OK);
	}

	@ExceptionHandler
	void handleException(HttpMessageNotReadableException e, HttpServletResponse response) throws IOException {
		response.sendError(HttpServletResponse.SC_BAD_REQUEST, e.getCause().getMessage());
	}

	@ExceptionHandler
	void handleException(MethodArgumentNotValidException e, HttpServletResponse response) throws IOException {
		response.sendError(HttpServletResponse.SC_BAD_REQUEST, e.getCause().getMessage());
	}
	
	@ExceptionHandler
	void handleException(Exception e, HttpServletResponse response) throws IOException {
		response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getCause().getMessage());
	}
}
