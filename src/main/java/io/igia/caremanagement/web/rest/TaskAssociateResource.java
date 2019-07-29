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
import io.igia.caremanagement.service.TaskAssociateService;
import io.igia.caremanagement.service.dto.TaskAssociateDTO;
import io.igia.caremanagement.web.rest.errors.BadRequestAlertException;
import io.igia.caremanagement.web.rest.util.HeaderUtil;
import io.igia.caremanagement.web.rest.util.PaginationUtil;

/**
 * REST controller for managing TaskAssociate.
 */
@RestController
@RequestMapping("/api/definitions")
public class TaskAssociateResource {

    private final Logger log = LoggerFactory.getLogger(TaskAssociateResource.class);

    private static final String ENTITY_NAME = "caremanagementTaskAssociate";

    private final TaskAssociateService taskAssociateService;

    public TaskAssociateResource(TaskAssociateService taskAssociateService) {
        this.taskAssociateService = taskAssociateService;
    }

    /**
     * POST  /task-associates : Create a new taskAssociate.
     *
     * @param taskAssociateDTO the taskAssociateDTO to create
     * @return the ResponseEntity with status 201 (Created) and with body the new taskAssociateDTO, or with status 400 (Bad Request) if the taskAssociate has already an ID
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PostMapping("/task-associates")
    @Timed
    public ResponseEntity<TaskAssociateDTO> createTaskAssociate(@Valid @RequestBody TaskAssociateDTO taskAssociateDTO) throws URISyntaxException {
        log.debug("REST request to save TaskAssociate : {}", taskAssociateDTO);
        if (taskAssociateDTO.getId() != null) {
            throw new BadRequestAlertException("A new taskAssociate cannot already have an ID", ENTITY_NAME, "idexists");
        }
        TaskAssociateDTO result = taskAssociateService.save(taskAssociateDTO);
        return ResponseEntity.created(new URI("/api/definitions/task-associates/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * PUT  /task-associates : Updates an existing taskAssociate.
     *
     * @param taskAssociateDTO the taskAssociateDTO to update
     * @return the ResponseEntity with status 200 (OK) and with body the updated taskAssociateDTO,
     * or with status 400 (Bad Request) if the taskAssociateDTO is not valid,
     * or with status 500 (Internal Server Error) if the taskAssociateDTO couldn't be updated
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PutMapping("/task-associates")
    @Timed
    public ResponseEntity<TaskAssociateDTO> updateTaskAssociate(@RequestBody TaskAssociateDTO taskAssociateDTO) throws URISyntaxException {
        log.debug("REST request to update TaskAssociate : {}", taskAssociateDTO);
        if (taskAssociateDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        TaskAssociateDTO result = taskAssociateService.save(taskAssociateDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(ENTITY_NAME, taskAssociateDTO.getId().toString()))
            .body(result);
    }

    /**
     * GET  /task-associates : get all the taskAssociates.
     *
     * @param pageable the pagination information
     * @return the ResponseEntity with status 200 (OK) and the list of taskAssociates in body
     */
    @GetMapping("/task-associates")
    @Timed
    public ResponseEntity<List<TaskAssociateDTO>> getAllTaskAssociates(Pageable pageable) {
        log.debug("REST request to get a page of TaskAssociates");
        Page<TaskAssociateDTO> page = taskAssociateService.findAll(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/task-associates");
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }

    /**
     * GET  /task-associates/:id : get the "id" taskAssociate.
     *
     * @param id the id of the taskAssociateDTO to retrieve
     * @return the ResponseEntity with status 200 (OK) and with body the taskAssociateDTO, or with status 404 (Not Found)
     */
    @GetMapping("/task-associates/{id}")
    @Timed
    public ResponseEntity<TaskAssociateDTO> getTaskAssociate(@PathVariable Long id) {
        log.debug("REST request to get TaskAssociate : {}", id);
        Optional<TaskAssociateDTO> taskAssociateDTO = taskAssociateService.findOne(id);
        return ResponseUtil.wrapOrNotFound(taskAssociateDTO);
    }

    /**
     * DELETE  /task-associates/:id : delete the "id" taskAssociate.
     *
     * @param id the id of the taskAssociateDTO to delete
     * @return the ResponseEntity with status 200 (OK)
     */
    @DeleteMapping("/task-associates/{id}")
    @Timed
    public ResponseEntity<Void> deleteTaskAssociate(@PathVariable Long id) {
        log.debug("REST request to delete TaskAssociate : {}", id);
        taskAssociateService.delete(id);
        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert(ENTITY_NAME, id.toString())).build();
    }
}
