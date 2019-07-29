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

import io.igia.caremanagement.service.dto.EpisodeAssociateDTO;

/**
 * Service Interface for managing EpisodeAssociate.
 */
public interface EpisodeAssociateService {

    /**
     * Save a episodeAssociate.
     *
     * @param episodeAssociateDTO the entity to save
     * @return the persisted entity
     */
    EpisodeAssociateDTO save(EpisodeAssociateDTO episodeAssociateDTO);

    /**
     * Get all the episodeAssociates.
     *
     * @param pageable the pagination information
     * @return the list of entities
     */
    Page<EpisodeAssociateDTO> findAll(Pageable pageable);


    /**
     * Get the "id" episodeAssociate.
     *
     * @param id the id of the entity
     * @return the entity
     */
    Optional<EpisodeAssociateDTO> findOne(Long id);

    /**
     * Delete the "id" episodeAssociate.
     *
     * @param id the id of the entity
     */
    void delete(Long id);
    
    /**
     * Get the "episodeId" episodeAssociate.
     *
     * @param episodeId the episodeId of the entity
     * @return the entity
     */
    List<EpisodeAssociateDTO> findAllByEpisodeId(Long episodeId);
    
    /**
     * Get the "associateOn" episodeAssociate.
     *
     * @param associateOn the associateOn of the entity
     * @return the entity
     */
    List<EpisodeAssociateDTO> findAllByAssociateOn(Long associateOn);
}
