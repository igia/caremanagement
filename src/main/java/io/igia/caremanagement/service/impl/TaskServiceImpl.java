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

import io.igia.caremanagement.domain.Task;
import io.igia.caremanagement.repository.TaskRepository;
import io.igia.caremanagement.service.TaskService;
import io.igia.caremanagement.service.dto.TaskDTO;
import io.igia.caremanagement.service.mapper.TaskMapper;
import io.igia.caremanagement.web.rest.errors.CustomParameterizedException;

/**
 * Service Implementation for managing Task.
 */
@Service
@Transactional
public class TaskServiceImpl implements TaskService {

    private final Logger log = LoggerFactory.getLogger(TaskServiceImpl.class);

    private final TaskRepository taskRepository;

    private final TaskMapper taskMapper;

    public TaskServiceImpl(TaskRepository taskRepository, TaskMapper taskMapper) {
        this.taskRepository = taskRepository;
        this.taskMapper = taskMapper;
    }

    /**
     * Save a task.
     *
     * @param taskDTO
     *            the entity to save
     * @return the persisted entity
     */
    @Override
    public TaskDTO save(TaskDTO taskDTO) {
        log.debug("Request to save Task : {}", taskDTO);
        if (taskDTO.getId() != null) {
            Optional<TaskDTO> dto = findOne(taskDTO.getId());
            if (!dto.isPresent()) {
                throw new CustomParameterizedException("Task does not exist", "");
            }
        }        
        Task task = taskMapper.toEntity(taskDTO);
        task = taskRepository.save(task);
        return taskMapper.toDto(task);
    }

    /**
     * Get all the tasks.
     *
     * @param pageable
     *            the pagination information
     * @return the list of entities
     */
    @Override
    @Transactional(readOnly = true)
    public Page<TaskDTO> findAll(Pageable pageable) {
        log.debug("Request to get all Tasks");
        return taskRepository.findAll(pageable).map(taskMapper::toDto);
    }

    /**
     * Get one task by id.
     *
     * @param id
     *            the id of the entity
     * @return the entity
     */
    @Override
    @Transactional(readOnly = true)
    public Optional<TaskDTO> findOne(Long id) {
        log.debug("Request to get Task : {}", id);
        return taskRepository.findById(id).map(taskMapper::toDto);
    }

    /**
     * Delete the task by id.
     *
     * @param id
     *            the id of the entity
     */
    @Override
    public void delete(Long id) {
        log.debug("Request to delete Task : {}", id);
        taskRepository.deleteById(id);
    }

    /**
     * Get one task by goalId.
     *
     * @param goalId
     *            the goalId of the entity
     * @return the entity
     */
    @Override
    @Transactional(readOnly = true)
    public List<TaskDTO> findAllByGoalId(Long goalId) {
        log.debug("Request to get Task by goalId : {}", goalId);
        List<Task> taskList =  taskRepository.findAllByGoalId(goalId);
        return taskMapper.toDto(taskList);
    }
    
    /**
     * Get one task by name and goalId.
     *
     * @param name the name of the entity
     * @param goalId the goalId of the entity
     * @return the entity
     */
    @Override
    @Transactional(readOnly = true)
    public Optional<TaskDTO> findOneByNameIgnoreCaseAndGoalId(String name, Long goalId) {
        log.debug("Request to get Task by name : {} and goalId : {}", name, goalId);
        return taskRepository.findOneByNameIgnoreCaseAndGoalId(name, goalId).map(taskMapper::toDto);
    }
}
