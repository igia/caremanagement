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

import io.igia.caremanagement.service.dto.TaskDTO;

/**
 * Service Interface for managing Task.
 */
public interface TaskService {

    /**
     * Save a task.
     *
     * @param taskDTO the entity to save
     * @return the persisted entity
     */
    TaskDTO save(TaskDTO taskDTO);

    /**
     * Get all the tasks.
     *
     * @param pageable the pagination information
     * @return the list of entities
     */
    Page<TaskDTO> findAll(Pageable pageable);

    /**
     * Get the "id" task.
     *
     * @param id the id of the entity
     * @return the entity
     */
    Optional<TaskDTO> findOne(Long id);

    /**
     * Delete the "id" task.
     *
     * @param id the id of the entity
     */
    void delete(Long id);

    /**
     * Get the "goalId" task.
     *
     * @param goalId the goalId of the entity
     * @return the entity
     */
    List<TaskDTO> findAllByGoalId(Long goalId);
    
    /**
     * Get the "name and goalId" task.
     *
     * @param name the name of the entity
     * @param goalId the goalId of the entity
     * @return the entity
     */
    Optional<TaskDTO> findOneByNameIgnoreCaseAndGoalId(String name, Long goalId);
}
