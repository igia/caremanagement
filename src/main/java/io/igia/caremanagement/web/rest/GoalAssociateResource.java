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

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Optional;

import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.codahale.metrics.annotation.Timed;

import io.github.jhipster.web.util.ResponseUtil;
import io.igia.caremanagement.service.GoalAssociateService;
import io.igia.caremanagement.service.dto.GoalAssociateDTO;
import io.igia.caremanagement.web.rest.errors.BadRequestAlertException;
import io.igia.caremanagement.web.rest.util.HeaderUtil;
import io.igia.caremanagement.web.rest.util.PaginationUtil;

/**
 * REST controller for managing GoalAssociate.
 */
@RestController
@RequestMapping("/api/definitions")
public class GoalAssociateResource {

    private final Logger log = LoggerFactory.getLogger(GoalAssociateResource.class);

    private static final String ENTITY_NAME = "caremanagementGoalAssociate";

    private final GoalAssociateService goalAssociateService;

    public GoalAssociateResource(GoalAssociateService goalAssociateService) {
        this.goalAssociateService = goalAssociateService;
    }

    /**
     * POST  /goal-associates : Create a new goalAssociate.
     *
     * @param goalAssociateDTO the goalAssociateDTO to create
     * @return the ResponseEntity with status 201 (Created) and with body the new goalAssociateDTO, or with status 400 (Bad Request) if the goalAssociate has already an ID
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PostMapping("/goal-associates")
    @Timed
    public ResponseEntity<GoalAssociateDTO> createGoalAssociate(@Valid @RequestBody GoalAssociateDTO goalAssociateDTO) throws URISyntaxException {
        log.debug("REST request to save GoalAssociate : {}", goalAssociateDTO);
        if (goalAssociateDTO.getId() != null) {
            throw new BadRequestAlertException("A new goalAssociate cannot already have an ID", ENTITY_NAME, "idexists");
        }
        GoalAssociateDTO result = goalAssociateService.save(goalAssociateDTO);
        return ResponseEntity.created(new URI("/api/definitions/goal-associates/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * PUT  /goal-associates : Updates an existing goalAssociate.
     *
     * @param goalAssociateDTO the goalAssociateDTO to update
     * @return the ResponseEntity with status 200 (OK) and with body the updated goalAssociateDTO,
     * or with status 400 (Bad Request) if the goalAssociateDTO is not valid,
     * or with status 500 (Internal Server Error) if the goalAssociateDTO couldn't be updated
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PutMapping("/goal-associates")
    @Timed
    public ResponseEntity<GoalAssociateDTO> updateGoalAssociate(@RequestBody GoalAssociateDTO goalAssociateDTO) throws URISyntaxException {
        log.debug("REST request to update GoalAssociate : {}", goalAssociateDTO);
        if (goalAssociateDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        GoalAssociateDTO result = goalAssociateService.save(goalAssociateDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(ENTITY_NAME, goalAssociateDTO.getId().toString()))
            .body(result);
    }

    /**
     * GET  /goal-associates : get all the goalAssociates.
     *
     * @param pageable the pagination information
     * @return the ResponseEntity with status 200 (OK) and the list of goalAssociates in body
     */
    @GetMapping("/goal-associates")
    @Timed
    public ResponseEntity<List<GoalAssociateDTO>> getAllGoalAssociates(Pageable pageable) {
        log.debug("REST request to get a page of GoalAssociates");
        Page<GoalAssociateDTO> page = goalAssociateService.findAll(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/goal-associates");
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }

    /**
     * GET  /goal-associates/:id : get the "id" goalAssociate.
     *
     * @param id the id of the goalAssociateDTO to retrieve
     * @return the ResponseEntity with status 200 (OK) and with body the goalAssociateDTO, or with status 404 (Not Found)
     */
    @GetMapping("/goal-associates/{id}")
    @Timed
    public ResponseEntity<GoalAssociateDTO> getGoalAssociate(@PathVariable Long id) {
        log.debug("REST request to get GoalAssociate : {}", id);
        Optional<GoalAssociateDTO> goalAssociateDTO = goalAssociateService.findOne(id);
        return ResponseUtil.wrapOrNotFound(goalAssociateDTO);
    }

    /**
     * DELETE  /goal-associates/:id : delete the "id" goalAssociate.
     *
     * @param id the id of the goalAssociateDTO to delete
     * @return the ResponseEntity with status 200 (OK)
     */
    @DeleteMapping("/goal-associates/{id}")
    @Timed
    public ResponseEntity<Void> deleteGoalAssociate(@PathVariable Long id) {
        log.debug("REST request to delete GoalAssociate : {}", id);
        goalAssociateService.delete(id);
        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert(ENTITY_NAME, id.toString())).build();
    }
}
