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

import io.igia.caremanagement.service.dto.GoalDTO;

/**
 * Service Interface for managing Goal.
 */
public interface GoalService {

    /**
     * Save a goal.
     *
     * @param goalDTO the entity to save
     * @return the persisted entity
     */
    GoalDTO save(GoalDTO goalDTO);

    /**
     * Get all the goals.
     *
     * @param pageable the pagination information
     * @return the list of entities
     */
    Page<GoalDTO> findAll(Pageable pageable);


    /**
     * Get the "id" goal.
     *
     * @param id the id of the entity
     * @return the entity
     */
    Optional<GoalDTO> findOne(Long id);

    /**
     * Delete the "id" goal.
     *
     * @param id the id of the entity
     */
    void delete(Long id);
    
    /**
     * Get the "episodeId" goal.
     *
     * @param episodeId the episodeId of the entity
     * @return the entity
     */
    List<GoalDTO> findAllByEpisodeId(Long episodeId);

    /**
     * Get the "name and episodeId" goal.
     *
     * @param name the name of the entity
     * @param episodeId the episodeId of the entity
     * @return the entity
     */
    Optional<GoalDTO> findOneByNameIgnoreCaseAndEpisodeId(String name, Long episodeId);
}
