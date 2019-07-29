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
package io.igia.caremanagement.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import io.igia.caremanagement.domain.Program;
import io.igia.caremanagement.repository.ProgramRepository;
import io.igia.caremanagement.service.ProgramService;
import io.igia.caremanagement.service.dto.ProgramDTO;
import io.igia.caremanagement.service.mapper.ProgramMapper;
import io.igia.caremanagement.web.rest.errors.CustomParameterizedException;

import java.util.Optional;

/**
 * Service Implementation for managing Program.
 */
@Service
@Transactional
public class ProgramServiceImpl implements ProgramService {

    private final Logger log = LoggerFactory.getLogger(ProgramServiceImpl.class);

    private final ProgramRepository programRepository;

    private final ProgramMapper programMapper;

    public ProgramServiceImpl(ProgramRepository programRepository, ProgramMapper programMapper) {
        this.programRepository = programRepository;
        this.programMapper = programMapper;
    }

    /**
     * Save a program.
     *
     * @param programDTO the entity to save
     * @return the persisted entity
     */
    @Override
    public ProgramDTO save(ProgramDTO programDTO) {
        log.debug("Request to save Program : {}", programDTO);
        if (programDTO.getId() != null) {
            Optional<ProgramDTO> dto = findOne(programDTO.getId());
            if (!dto.isPresent()) {
                throw new CustomParameterizedException("Program does not exist", "");
            }
        }
        Program program = programMapper.toEntity(programDTO);
        program = programRepository.save(program);
        return programMapper.toDto(program);
    }

    /**
     * Get all the programs.
     *
     * @param pageable the pagination information
     * @return the list of entities
     */
    @Override
    @Transactional(readOnly = true)
    public Page<ProgramDTO> findAll(Pageable pageable) {
        log.debug("Request to get all Programs");
        return programRepository.findAll(pageable)
            .map(programMapper::toDto);
    }


    /**
     * Get one program by id.
     *
     * @param id the id of the entity
     * @return the entity
     */
    @Override
    @Transactional(readOnly = true)
    public Optional<ProgramDTO> findOne(Long id) {
        log.debug("Request to get Program : {}", id);
        return programRepository.findById(id)
            .map(programMapper::toDto);
    }

    /**
     * Delete the program by id.
     *
     * @param id the id of the entity
     */
    @Override
    public void delete(Long id) {
        log.debug("Request to delete Program : {}", id);
        programRepository.deleteById(id);
    }
    
    /**
     * Get one program by programIdU.
     *
     * @param programIdU the programIdU of the entity
     * @return the entity
     */
    @Override
    @Transactional(readOnly = true)
    public Optional<ProgramDTO> findOneByProgramIdU(String programIdU) {
        log.debug("Request to get Program by programIdU: {}", programIdU);
        return programRepository.findOneByProgramIdU(programIdU)
            .map(programMapper::toDto);
    }

    /**
     * Get program count.
     *
     * @return the entity count
     */
    @Override
    public Long count() {
        return programRepository.count();
    }
    
    /**
     * Get one program by name.
     *
     * @param name the name of the entity
     * @return the entity
     */
    @Override
    @Transactional(readOnly = true)
    public Optional<ProgramDTO> findOneByName(String name) {
        log.debug("Request to get Program by name: {}", name);
        return programRepository.findOneByName(name)
            .map(programMapper::toDto);
    }
}
