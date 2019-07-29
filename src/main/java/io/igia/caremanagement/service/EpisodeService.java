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
package io.igia.caremanagement.service;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import io.igia.caremanagement.service.dto.EpisodeDTO;

/**
 * Service Interface for managing Episode.
 */
public interface EpisodeService {

    /**
     * Save a episode.
     *
     * @param episodeDTO the entity to save
     * @return the persisted entity
     */
    EpisodeDTO save(EpisodeDTO episodeDTO);

    /**
     * Get all the episodes.
     *
     * @param pageable the pagination information
     * @return the list of entities
     */
    Page<EpisodeDTO> findAll(Pageable pageable);


    /**
     * Get the "id" episode.
     *
     * @param id the id of the entity
     * @return the entity
     */
    Optional<EpisodeDTO> findOne(Long id);

    /**
     * Delete the "id" episode.
     *
     * @param id the id of the entity
     */
    void delete(Long id);
    
    /**
     * Get the "programId" episode.
     *
     * @param programId the programId of the entity
     * @return the entity
     */
    List<EpisodeDTO> findAllByProgramId(Long programId);
}
