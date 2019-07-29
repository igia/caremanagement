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
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.codahale.metrics.annotation.Timed;

import io.igia.caremanagement.service.camunda.DeploymentService;
import io.igia.caremanagement.service.dto.camunda.DeploymentResponse;
import io.igia.caremanagement.web.rest.util.HeaderUtil;


@RestController("camundaDeploymentResource")
@RequestMapping("/api/deployment")
public class DeploymentResource {
	
	private static final String ENTITY_NAME = "caremanagementDeployment";

	private final Logger log = LoggerFactory.getLogger(DeploymentResource.class);
	
	private final DeploymentService deploymentService;
	
	public DeploymentResource(DeploymentService deploymentService) {
		this.deploymentService = deploymentService;
	}
	
	@GetMapping(value = "")
	@Timed
	public ResponseEntity<List<DeploymentResponse>> getDeployments(@RequestParam(value = "id", required = false) String id, @RequestParam(value = "nameLike", required = false) String nameLike, @RequestParam(value = "name", required = false) String name, @RequestParam(value = "source", required = false) String source) {
		log.info("REST request to get list of deployments in camunda - id = {}, nameLike = {}, name = {}, source = {}", id, nameLike, name, source);
		List<DeploymentResponse> response = deploymentService.getDeployments(id, nameLike, name, source);
		return new ResponseEntity<>(response, HttpStatus.OK);
	}
	
	@DeleteMapping("/{id}")
	@Timed
	public ResponseEntity<Object> deleteDeployment(@PathVariable String id, @RequestParam(value = "cascade", required=false) Boolean cascade) {
		log.info("REST request to delete deployment in camunda - id = {}, cascade = {}", id, cascade);
		deploymentService.deleteDeployment(id, cascade);
		return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert(ENTITY_NAME, id)).build();
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
