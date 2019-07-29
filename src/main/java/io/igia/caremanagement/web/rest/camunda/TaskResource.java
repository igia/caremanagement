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
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.codahale.metrics.annotation.Timed;

import io.igia.caremanagement.service.camunda.TaskService;
import io.igia.caremanagement.service.dto.camunda.TaskRequest;
import io.igia.caremanagement.service.dto.camunda.TaskResponse;
import io.igia.caremanagement.web.rest.errors.CustomParameterizedException;

@RestController("camundaTaskResource")
@RequestMapping("/api")
public class TaskResource {
	private final Logger log = LoggerFactory.getLogger(TaskResource.class);

	private final TaskService taskService;

	public TaskResource(TaskService taskService) {
		this.taskService = taskService;
	}

	@PostMapping("/tasks/{id}/complete")
	@Timed
	public ResponseEntity<Object> completeTask(@PathVariable String id) {
		log.info("REST request to complete task in camunda for id : {}", id);
		taskService.completeTask(id);
		return new ResponseEntity<>("", HttpStatus.OK);
	}

	@PostMapping("/tasks")
	@Timed
	public ResponseEntity<List<TaskResponse>> getTask(@RequestBody TaskRequest task) {
		log.info("REST request to get {} task in camunda", task.getTaskCategory());
		List<TaskResponse> taskList = taskService.getTasks(task);
		return new ResponseEntity<>(taskList, HttpStatus.OK);

	}

	@PutMapping("/tasks/start")
	@Timed
	public ResponseEntity<Object> startTask(@RequestBody TaskRequest taskRequest) {
		log.info("REST request to start task in camunda for caseInstanceId: {}", taskRequest.getCaseInstanceId());
		taskService.startTask(taskRequest);
		return new ResponseEntity<>("", HttpStatus.OK);
	}
	
	@PutMapping("/tasks/reassign")
	@Timed
	public ResponseEntity<Object> updateTaskAssignee(@RequestBody TaskRequest task) {
		log.debug("REST request to reassign task in camunda for taskId :{}", task.getTaskId());
		taskService.updateTaskAssignee(task);
		return new ResponseEntity<>("", HttpStatus.OK);
	}
	
	@PostMapping("/tasks/create")
	@Timed
	public ResponseEntity<Object> createAdHocTask(@RequestBody TaskRequest task){
		log.debug("REST request to create ad hoc task in camunda for mrn : {}", task.getMrn());
		taskService.createAdHocTask(task);
		return new ResponseEntity<>("", HttpStatus.OK);
	}

	@ExceptionHandler()
	void handleExceptionCustom(CustomParameterizedException e, HttpServletResponse response) throws IOException{
		response.sendError(HttpServletResponse.SC_BAD_REQUEST,e.getParameters().get("message").toString());
	}

	@ExceptionHandler
	void handleException(HttpMessageNotReadableException e, HttpServletResponse response) throws IOException {
		response.sendError(HttpServletResponse.SC_BAD_REQUEST, e.getMessage());
	}

	@ExceptionHandler
	void handleException(MethodArgumentNotValidException e, HttpServletResponse response) throws IOException {
		response.sendError(HttpServletResponse.SC_BAD_REQUEST, e.getMessage());
	}
	
	@ExceptionHandler
	void handleException(Exception e, HttpServletResponse response) throws IOException {
		response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getCause().getMessage());
	}

}
