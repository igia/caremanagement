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

import io.igia.caremanagement.service.dto.GoalAssociateDTO;

/**
 * Service Interface for managing GoalAssociate.
 */
public interface GoalAssociateService {

    /**
     * Save a goalAssociate.
     *
     * @param goalAssociateDTO the entity to save
     * @return the persisted entity
     */
    GoalAssociateDTO save(GoalAssociateDTO goalAssociateDTO);

    /**
     * Get all the goalAssociates.
     *
     * @param pageable the pagination information
     * @return the list of entities
     */
    Page<GoalAssociateDTO> findAll(Pageable pageable);


    /**
     * Get the "id" goalAssociate.
     *
     * @param id the id of the entity
     * @return the entity
     */
    Optional<GoalAssociateDTO> findOne(Long id);

    /**
     * Delete the "id" goalAssociate.
     *
     * @param id the id of the entity
     */
    void delete(Long id);
    
    /**
     * Get the "goalId" goalAssociate.
     *
     * @param goalId the goalId of the entity
     * @return the entity
     */
    List<GoalAssociateDTO> findAllByGoalId(Long goalId);
    
    /**
     * Get the "associateOn" goalAssociate.
     *
     * @param associateOn the associateOn of the entity
     * @return the entity
     */
    List<GoalAssociateDTO> findAllByAssociateOn(Long associateOn);
}
