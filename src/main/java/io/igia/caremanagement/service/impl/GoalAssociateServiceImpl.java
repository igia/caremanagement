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

import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import io.igia.caremanagement.domain.GoalAssociate;
import io.igia.caremanagement.repository.GoalAssociateRepository;
import io.igia.caremanagement.service.GoalAssociateService;
import io.igia.caremanagement.service.dto.GoalAssociateDTO;
import io.igia.caremanagement.service.mapper.GoalAssociateMapper;
import io.igia.caremanagement.web.rest.errors.CustomParameterizedException;

/**
 * Service Implementation for managing GoalAssociate.
 */
@Service
@Transactional
public class GoalAssociateServiceImpl implements GoalAssociateService {

    private final Logger log = LoggerFactory.getLogger(GoalAssociateServiceImpl.class);

    private final GoalAssociateRepository goalAssociateRepository;

    private final GoalAssociateMapper goalAssociateMapper;

    public GoalAssociateServiceImpl(GoalAssociateRepository goalAssociateRepository, GoalAssociateMapper goalAssociateMapper) {
        this.goalAssociateRepository = goalAssociateRepository;
        this.goalAssociateMapper = goalAssociateMapper;
    }

    /**
     * Save a goalAssociate.
     *
     * @param goalAssociateDTO the entity to save
     * @return the persisted entity
     */
    @Override
    public GoalAssociateDTO save(GoalAssociateDTO goalAssociateDTO) {
        log.debug("Request to save GoalAssociate : {}", goalAssociateDTO);
        if (goalAssociateDTO.getId() != null) {
            Optional<GoalAssociateDTO> dto = findOne(goalAssociateDTO.getId());
            if (!dto.isPresent()) {
                throw new CustomParameterizedException("Goal Associate does not exist", "");
            }
        }
        GoalAssociate goalAssociate = goalAssociateMapper.toEntity(goalAssociateDTO);
        goalAssociate = goalAssociateRepository.save(goalAssociate);
        return goalAssociateMapper.toDto(goalAssociate);
    }

    /**
     * Get all the goalAssociates.
     *
     * @param pageable the pagination information
     * @return the list of entities
     */
    @Override
    @Transactional(readOnly = true)
    public Page<GoalAssociateDTO> findAll(Pageable pageable) {
        log.debug("Request to get all GoalAssociates");
        return goalAssociateRepository.findAll(pageable)
            .map(goalAssociateMapper::toDto);
    }


    /**
     * Get one goalAssociate by id.
     *
     * @param id the id of the entity
     * @return the entity
     */
    @Override
    @Transactional(readOnly = true)
    public Optional<GoalAssociateDTO> findOne(Long id) {
        log.debug("Request to get GoalAssociate : {}", id);
        return goalAssociateRepository.findById(id)
            .map(goalAssociateMapper::toDto);
    }

    /**
     * Delete the goalAssociate by id.
     *
     * @param id the id of the entity
     */
    @Override
    public void delete(Long id) {
        log.debug("Request to delete GoalAssociate : {}", id);
        goalAssociateRepository.deleteById(id);
    }
    
    
    /**
     * Get the "goalId" goalAssociate.
     *
     * @param goalId the goalId of the entity
     * @return the entity
     */
    public List<GoalAssociateDTO> findAllByGoalId(Long goalId){
        log.debug("Request to get GoalAssociate by goalId: {}", goalId);
        List<GoalAssociate> goalAssociateList = goalAssociateRepository.findAllByGoalId(goalId);
        return goalAssociateMapper.toDto(goalAssociateList);
    }
    
    /**
     * Get the "associateOn" goalAssociate.
     *
     * @param associateOn the associateOn of the entity
     * @return the entity
     */
    public List<GoalAssociateDTO> findAllByAssociateOn(Long associateOn){
        log.debug("Request to get GoalAssociate by associateOn: {}", associateOn);
        List<GoalAssociate> goalAssociateList = goalAssociateRepository.findAllByAssociateOn(associateOn);
        return goalAssociateMapper.toDto(goalAssociateList);
    }
}
