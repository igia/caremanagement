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
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.codahale.metrics.annotation.Timed;

import io.igia.caremanagement.service.camunda.GoalService;
import io.igia.caremanagement.service.dto.camunda.GoalRequest;
import io.igia.caremanagement.service.dto.camunda.GoalResponse;
import io.igia.caremanagement.web.rest.errors.CustomParameterizedException;

@RestController("camundaGoalResource")
@RequestMapping("/api")
public class GoalResource {

    private final Logger log = LoggerFactory.getLogger(GoalResource.class);

    private final GoalService goalService;

    public GoalResource(GoalService goalService) {
        this.goalService = goalService;
    }

    @PostMapping("/goals")
    @Timed
    public ResponseEntity<List<GoalResponse>> getGoals(@RequestBody GoalRequest goal) {
        log.info("REST request to get goals in camunda for mrn:{}", goal.getMrn());
        List<GoalResponse> goalList = goalService.getGoals(goal);
        return new ResponseEntity<>(goalList, HttpStatus.OK);
    }

    @PutMapping("/goals/start")
    @Timed
    public ResponseEntity<Object> startTask(@RequestBody GoalRequest goalRequest) {
        log.info("REST request to start goal in camunda for caseInstanceId: {} ", goalRequest.getCaseInstanceId());
        Map<String, String> result = goalService.startGoal(goalRequest);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @ExceptionHandler()
    void handleExceptionCustom(CustomParameterizedException e, HttpServletResponse response) throws IOException {
        response.sendError(HttpServletResponse.SC_BAD_REQUEST, e.getParameters().get("message").toString());
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
