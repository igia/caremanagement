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

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import io.igia.caremanagement.service.dto.ProgramDTO;

import java.util.Optional;

/**
 * Service Interface for managing Program.
 */
public interface ProgramService {

    /**
     * Save a program.
     *
     * @param programDTO the entity to save
     * @return the persisted entity
     */
    ProgramDTO save(ProgramDTO programDTO);

    /**
     * Get all the programs.
     *
     * @param pageable the pagination information
     * @return the list of entities
     */
    Page<ProgramDTO> findAll(Pageable pageable);


    /**
     * Get the "id" program.
     *
     * @param id the id of the entity
     * @return the entity
     */
    Optional<ProgramDTO> findOne(Long id);

    /**
     * Delete the "id" program.
     *
     * @param id the id of the entity
     */
    void delete(Long id);
    
    /**
     * Get the "programIdU" program.
     *
     * @param programIdU the programIdU of the entity
     * @return the entity
     */
    Optional<ProgramDTO> findOneByProgramIdU(String programIdU);
    
    /**
     * Get the "count" program.
     *
     * @return the entity count
     */
    Long count();

    /**
     * Get the "name" program.
     *
     * @param name the name of the entity
     * @return the entity
     */
    Optional<ProgramDTO> findOneByName(String name);
}
