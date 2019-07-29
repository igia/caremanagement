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
import io.igia.caremanagement.service.EpisodeAssociateService;
import io.igia.caremanagement.service.dto.EpisodeAssociateDTO;
import io.igia.caremanagement.web.rest.errors.BadRequestAlertException;
import io.igia.caremanagement.web.rest.util.HeaderUtil;
import io.igia.caremanagement.web.rest.util.PaginationUtil;

/**
 * REST controller for managing EpisodeAssociate.
 */
@RestController
@RequestMapping("/api/definitions")
public class EpisodeAssociateResource {

    private final Logger log = LoggerFactory.getLogger(EpisodeAssociateResource.class);

    private static final String ENTITY_NAME = "caremanagementEpisodeAssociate";

    private final EpisodeAssociateService episodeAssociateService;

    public EpisodeAssociateResource(EpisodeAssociateService episodeAssociateService) {
        this.episodeAssociateService = episodeAssociateService;
    }

    /**
     * POST  /episode-associates : Create a new episodeAssociate.
     *
     * @param episodeAssociateDTO the episodeAssociateDTO to create
     * @return the ResponseEntity with status 201 (Created) and with body the new episodeAssociateDTO, or with status 400 (Bad Request) if the episodeAssociate has already an ID
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PostMapping("/episode-associates")
    @Timed
    public ResponseEntity<EpisodeAssociateDTO> createEpisodeAssociate(@Valid @RequestBody EpisodeAssociateDTO episodeAssociateDTO) throws URISyntaxException {
        log.debug("REST request to save EpisodeAssociate : {}", episodeAssociateDTO);
        if (episodeAssociateDTO.getId() != null) {
            throw new BadRequestAlertException("A new episodeAssociate cannot already have an ID", ENTITY_NAME, "idexists");
        }
        EpisodeAssociateDTO result = episodeAssociateService.save(episodeAssociateDTO);
        return ResponseEntity.created(new URI("/api/definitions/episode-associates/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * PUT  /episode-associates : Updates an existing episodeAssociate.
     *
     * @param episodeAssociateDTO the episodeAssociateDTO to update
     * @return the ResponseEntity with status 200 (OK) and with body the updated episodeAssociateDTO,
     * or with status 400 (Bad Request) if the episodeAssociateDTO is not valid,
     * or with status 500 (Internal Server Error) if the episodeAssociateDTO couldn't be updated
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PutMapping("/episode-associates")
    @Timed
    public ResponseEntity<EpisodeAssociateDTO> updateEpisodeAssociate(@RequestBody EpisodeAssociateDTO episodeAssociateDTO) throws URISyntaxException {
        log.debug("REST request to update EpisodeAssociate : {}", episodeAssociateDTO);
        if (episodeAssociateDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        EpisodeAssociateDTO result = episodeAssociateService.save(episodeAssociateDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(ENTITY_NAME, episodeAssociateDTO.getId().toString()))
            .body(result);
    }

    /**
     * GET  /episode-associates : get all the episodeAssociates.
     *
     * @param pageable the pagination information
     * @return the ResponseEntity with status 200 (OK) and the list of episodeAssociates in body
     */
    @GetMapping("/episode-associates")
    @Timed
    public ResponseEntity<List<EpisodeAssociateDTO>> getAllEpisodeAssociates(Pageable pageable) {
        log.debug("REST request to get a page of EpisodeAssociates");
        Page<EpisodeAssociateDTO> page = episodeAssociateService.findAll(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/episode-associates");
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }

    /**
     * GET  /episode-associates/:id : get the "id" episodeAssociate.
     *
     * @param id the id of the episodeAssociateDTO to retrieve
     * @return the ResponseEntity with status 200 (OK) and with body the episodeAssociateDTO, or with status 404 (Not Found)
     */
    @GetMapping("/episode-associates/{id}")
    @Timed
    public ResponseEntity<EpisodeAssociateDTO> getEpisodeAssociate(@PathVariable Long id) {
        log.debug("REST request to get EpisodeAssociate : {}", id);
        Optional<EpisodeAssociateDTO> episodeAssociateDTO = episodeAssociateService.findOne(id);
        return ResponseUtil.wrapOrNotFound(episodeAssociateDTO);
    }

    /**
     * DELETE  /episode-associates/:id : delete the "id" episodeAssociate.
     *
     * @param id the id of the episodeAssociateDTO to delete
     * @return the ResponseEntity with status 200 (OK)
     */
    @DeleteMapping("/episode-associates/{id}")
    @Timed
    public ResponseEntity<Void> deleteEpisodeAssociate(@PathVariable Long id) {
        log.debug("REST request to delete EpisodeAssociate : {}", id);
        episodeAssociateService.delete(id);
        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert(ENTITY_NAME, id.toString())).build();
    }
}
