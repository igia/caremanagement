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

import io.igia.caremanagement.service.dto.TaskAssociateDTO;

/**
 * Service Interface for managing TaskAssociate.
 */
public interface TaskAssociateService {

    /**
     * Save a taskAssociate.
     *
     * @param taskAssociateDTO the entity to save
     * @return the persisted entity
     */
    TaskAssociateDTO save(TaskAssociateDTO taskAssociateDTO);

    /**
     * Get all the taskAssociates.
     *
     * @param pageable the pagination information
     * @return the list of entities
     */
    Page<TaskAssociateDTO> findAll(Pageable pageable);


    /**
     * Get the "id" taskAssociate.
     *
     * @param id the id of the entity
     * @return the entity
     */
    Optional<TaskAssociateDTO> findOne(Long id);

    /**
     * Delete the "id" taskAssociate.
     *
     * @param id the id of the entity
     */
    void delete(Long id);
    
    /**
     * Get the "taskId" taskAssociate.
     *
     * @param taskId the taskId of the entity
     * @return the entity
     */
    List<TaskAssociateDTO> findAllByTaskId(Long taskId);
    
    /**
     * Get the "associateOn" taskAssociate.
     *
     * @param associateOn the associateOn of the entity
     * @return the entity
     */
    List<TaskAssociateDTO> findAllByAssociateOn(Long associateOn);
}
