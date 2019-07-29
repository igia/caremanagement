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

import io.igia.caremanagement.domain.TaskAssociate;
import io.igia.caremanagement.repository.TaskAssociateRepository;
import io.igia.caremanagement.service.TaskAssociateService;
import io.igia.caremanagement.service.dto.TaskAssociateDTO;
import io.igia.caremanagement.service.mapper.TaskAssociateMapper;
import io.igia.caremanagement.web.rest.errors.CustomParameterizedException;

/**
 * Service Implementation for managing TaskAssociate.
 */
@Service
@Transactional
public class TaskAssociateServiceImpl implements TaskAssociateService {

    private final Logger log = LoggerFactory.getLogger(TaskAssociateServiceImpl.class);

    private final TaskAssociateRepository taskAssociateRepository;

    private final TaskAssociateMapper taskAssociateMapper;

    public TaskAssociateServiceImpl(TaskAssociateRepository taskAssociateRepository, TaskAssociateMapper taskAssociateMapper) {
        this.taskAssociateRepository = taskAssociateRepository;
        this.taskAssociateMapper = taskAssociateMapper;
    }

    /**
     * Save a taskAssociate.
     *
     * @param taskAssociateDTO the entity to save
     * @return the persisted entity
     */
    @Override
    public TaskAssociateDTO save(TaskAssociateDTO taskAssociateDTO) {
        log.debug("Request to save TaskAssociate : {}", taskAssociateDTO);
        if (taskAssociateDTO.getId() != null) {
            Optional<TaskAssociateDTO> dto = findOne(taskAssociateDTO.getId());
            if (!dto.isPresent()) {
                throw new CustomParameterizedException("Task Associate does not exist", "");
            }
        }
        TaskAssociate taskAssociate = taskAssociateMapper.toEntity(taskAssociateDTO);
        taskAssociate = taskAssociateRepository.save(taskAssociate);
        return taskAssociateMapper.toDto(taskAssociate);
    }

    /**
     * Get all the taskAssociates.
     *
     * @param pageable the pagination information
     * @return the list of entities
     */
    @Override
    @Transactional(readOnly = true)
    public Page<TaskAssociateDTO> findAll(Pageable pageable) {
        log.debug("Request to get all TaskAssociates");
        return taskAssociateRepository.findAll(pageable)
            .map(taskAssociateMapper::toDto);
    }


    /**
     * Get one taskAssociate by id.
     *
     * @param id the id of the entity
     * @return the entity
     */
    @Override
    @Transactional(readOnly = true)
    public Optional<TaskAssociateDTO> findOne(Long id) {
        log.debug("Request to get TaskAssociate : {}", id);
        return taskAssociateRepository.findById(id)
            .map(taskAssociateMapper::toDto);
    }

    /**
     * Delete the taskAssociate by id.
     *
     * @param id the id of the entity
     */
    @Override
    public void delete(Long id) {
        log.debug("Request to delete TaskAssociate : {}", id);
        taskAssociateRepository.deleteById(id);
    }
    
    /**
     * Get the "taskId" taskAssociate.
     *
     * @param taskId the taskId of the entity
     * @return the entity
     */
    public List<TaskAssociateDTO> findAllByTaskId(Long taskId){
        log.debug("Request to get TaskAssociate by taskId: {}", taskId);
        List<TaskAssociate> taskAssociateList = taskAssociateRepository.findAllByTaskId(taskId);
        return taskAssociateMapper.toDto(taskAssociateList);
    }
    
    /**
     * Get the "associateOn" taskAssociate.
     *
     * @param associateOn the associateOn of the entity
     * @return the entity
     */
    public List<TaskAssociateDTO> findAllByAssociateOn(Long associateOn){
        log.debug("Request to get TaskAssociate by associateOn: {}", associateOn);
        List<TaskAssociate> taskAssociateList = taskAssociateRepository.findAllByAssociateOn(associateOn);
        return taskAssociateMapper.toDto(taskAssociateList);
    }
}
