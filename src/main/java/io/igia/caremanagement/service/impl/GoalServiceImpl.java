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

import io.igia.caremanagement.domain.Goal;
import io.igia.caremanagement.repository.GoalRepository;
import io.igia.caremanagement.service.GoalService;
import io.igia.caremanagement.service.dto.GoalDTO;
import io.igia.caremanagement.service.mapper.GoalMapper;
import io.igia.caremanagement.web.rest.errors.CustomParameterizedException;

/**
 * Service Implementation for managing Goal.
 */
@Service
@Transactional
public class GoalServiceImpl implements GoalService {

    private final Logger log = LoggerFactory.getLogger(GoalServiceImpl.class);

    private final GoalRepository goalRepository;

    private final GoalMapper goalMapper;

    public GoalServiceImpl(GoalRepository goalRepository, GoalMapper goalMapper) {
        this.goalRepository = goalRepository;
        this.goalMapper = goalMapper;
    }

    /**
     * Save a goal.
     *
     * @param goalDTO the entity to save
     * @return the persisted entity
     */
    @Override
    public GoalDTO save(GoalDTO goalDTO) {
        log.debug("Request to save Goal : {}", goalDTO);
        if (goalDTO.getId() != null) {
            Optional<GoalDTO> dto = findOne(goalDTO.getId());
            if (!dto.isPresent()) {
                throw new CustomParameterizedException("Goal does not exist", "");
            }
        }
        Goal goal = goalMapper.toEntity(goalDTO);
        goal = goalRepository.save(goal);
        return goalMapper.toDto(goal);
    }

    /**
     * Get all the goals.
     *
     * @param pageable the pagination information
     * @return the list of entities
     */
    @Override
    @Transactional(readOnly = true)
    public Page<GoalDTO> findAll(Pageable pageable) {
        log.debug("Request to get all Goals");
        return goalRepository.findAll(pageable)
            .map(goalMapper::toDto);
    }


    /**
     * Get one goal by id.
     *
     * @param id the id of the entity
     * @return the entity
     */
    @Override
    @Transactional(readOnly = true)
    public Optional<GoalDTO> findOne(Long id) {
        log.debug("Request to get Goal : {}", id);
        return goalRepository.findById(id)
            .map(goalMapper::toDto);
    }

    /**
     * Delete the goal by id.
     *
     * @param id the id of the entity
     */
    @Override
    public void delete(Long id) {
        log.debug("Request to delete Goal : {}", id);
        goalRepository.deleteById(id);
    }
    
    /**
     * Get one goal by episodeId.
     *
     * @param episodeId the episodeId of the entity
     * @return the entity
     */
    @Override
    @Transactional(readOnly = true)
    public List<GoalDTO> findAllByEpisodeId(Long episodeId) {
        log.debug("Request to get Goal by episodeId : {}", episodeId);
        List<Goal> goalList = goalRepository.findAllByEpisodeId(episodeId);
        return goalMapper.toDto(goalList);
    }

    /**
     * Get one goal by name and episodeId.
     *
     * @param name the name of the entity
     * @param episodeId the episodeId of the entity
     * @return the entity
     */
    @Override
    @Transactional(readOnly = true)
    public Optional<GoalDTO> findOneByNameIgnoreCaseAndEpisodeId(String name, Long episodeId) {
        log.debug("Request to get Goal by name : {} and episodeId : {}", name, episodeId);
        return goalRepository.findOneByNameIgnoreCaseAndEpisodeId(name, episodeId).map(goalMapper::toDto);
    }
}
