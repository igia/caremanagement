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
package io.igia.caremanagement.web.rest;

import com.codahale.metrics.annotation.Timed;

import io.github.jhipster.web.util.ResponseUtil;
import io.igia.caremanagement.service.GoalService;
import io.igia.caremanagement.service.dto.GoalDTO;
import io.igia.caremanagement.web.rest.errors.BadRequestAlertException;
import io.igia.caremanagement.web.rest.util.HeaderUtil;
import io.igia.caremanagement.web.rest.util.PaginationUtil;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.net.URI;
import java.net.URISyntaxException;

import java.util.List;
import java.util.Optional;

/**
 * REST controller for managing Goal.
 */
@RestController
@RequestMapping("/api/definitions")
public class GoalResource {

    private final Logger log = LoggerFactory.getLogger(GoalResource.class);

    private static final String ENTITY_NAME = "caremanagementGoal";

    private final GoalService goalService;

    public GoalResource(GoalService goalService) {
        this.goalService = goalService;
    }

    /**
     * POST  /goals : Create a new goal.
     *
     * @param goalDTO the goalDTO to create
     * @return the ResponseEntity with status 201 (Created) and with body the new goalDTO, or with status 400 (Bad Request) if the goal has already an ID
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PostMapping("/goals")
    @Timed
    public ResponseEntity<GoalDTO> createGoal(@Valid @RequestBody GoalDTO goalDTO) throws URISyntaxException {
        log.debug("REST request to save Goal : {}", goalDTO);
        if (goalDTO.getId() != null) {
            throw new BadRequestAlertException("A new goal cannot already have an ID", ENTITY_NAME, "idexists");
        }
        GoalDTO result = goalService.save(goalDTO);
        return ResponseEntity.created(new URI("/api/definitions/goals/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * PUT  /goals : Updates an existing goal.
     *
     * @param goalDTO the goalDTO to update
     * @return the ResponseEntity with status 200 (OK) and with body the updated goalDTO,
     * or with status 400 (Bad Request) if the goalDTO is not valid,
     * or with status 500 (Internal Server Error) if the goalDTO couldn't be updated
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PutMapping("/goals")
    @Timed
    public ResponseEntity<GoalDTO> updateGoal(@Valid @RequestBody GoalDTO goalDTO) throws URISyntaxException {
        log.debug("REST request to update Goal : {}", goalDTO);
        if (goalDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        GoalDTO result = goalService.save(goalDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(ENTITY_NAME, goalDTO.getId().toString()))
            .body(result);
    }

    /**
     * GET  /goals : get all the goals.
     *
     * @param pageable the pagination information
     * @return the ResponseEntity with status 200 (OK) and the list of goals in body
     */
    @GetMapping("/goals")
    @Timed
    public ResponseEntity<List<GoalDTO>> getAllGoals(Pageable pageable) {
        log.debug("REST request to get a page of Goals");
        Page<GoalDTO> page = goalService.findAll(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/goals");
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }

    /**
     * GET  /goals/:id : get the "id" goal.
     *
     * @param id the id of the goalDTO to retrieve
     * @return the ResponseEntity with status 200 (OK) and with body the goalDTO, or with status 404 (Not Found)
     */
    @GetMapping("/goals/{id}")
    @Timed
    public ResponseEntity<GoalDTO> getGoal(@PathVariable Long id) {
        log.debug("REST request to get Goal : {}", id);
        Optional<GoalDTO> goalDTO = goalService.findOne(id);
        return ResponseUtil.wrapOrNotFound(goalDTO);
    }

    /**
     * DELETE  /goals/:id : delete the "id" goal.
     *
     * @param id the id of the goalDTO to delete
     * @return the ResponseEntity with status 200 (OK)
     */
    @DeleteMapping("/goals/{id}")
    @Timed
    public ResponseEntity<Void> deleteGoal(@PathVariable Long id) {
        log.debug("REST request to delete Goal : {}", id);
        goalService.delete(id);
        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert(ENTITY_NAME, id.toString())).build();
    }
}
