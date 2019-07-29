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

import io.igia.caremanagement.domain.EpisodeAssociate;
import io.igia.caremanagement.repository.EpisodeAssociateRepository;
import io.igia.caremanagement.service.EpisodeAssociateService;
import io.igia.caremanagement.service.dto.EpisodeAssociateDTO;
import io.igia.caremanagement.service.mapper.EpisodeAssociateMapper;
import io.igia.caremanagement.web.rest.errors.CustomParameterizedException;

/**
 * Service Implementation for managing EpisodeAssociate.
 */
@Service
@Transactional
public class EpisodeAssociateServiceImpl implements EpisodeAssociateService {

    private final Logger log = LoggerFactory.getLogger(EpisodeAssociateServiceImpl.class);

    private final EpisodeAssociateRepository episodeAssociateRepository;

    private final EpisodeAssociateMapper episodeAssociateMapper;

    public EpisodeAssociateServiceImpl(EpisodeAssociateRepository episodeAssociateRepository, EpisodeAssociateMapper episodeAssociateMapper) {
        this.episodeAssociateRepository = episodeAssociateRepository;
        this.episodeAssociateMapper = episodeAssociateMapper;
    }

    /**
     * Save a episodeAssociate.
     *
     * @param episodeAssociateDTO the entity to save
     * @return the persisted entity
     */
    @Override
    public EpisodeAssociateDTO save(EpisodeAssociateDTO episodeAssociateDTO) {
        log.debug("Request to save EpisodeAssociate : {}", episodeAssociateDTO);
        if (episodeAssociateDTO.getId() != null) {
            Optional<EpisodeAssociateDTO> dto = findOne(episodeAssociateDTO.getId());
            if (!dto.isPresent()) {
                throw new CustomParameterizedException("Episode Associate does not exist", "");
            }
        }
        EpisodeAssociate episodeAssociate = episodeAssociateMapper.toEntity(episodeAssociateDTO);
        episodeAssociate = episodeAssociateRepository.save(episodeAssociate);
        return episodeAssociateMapper.toDto(episodeAssociate);
    }

    /**
     * Get all the episodeAssociates.
     *
     * @param pageable the pagination information
     * @return the list of entities
     */
    @Override
    @Transactional(readOnly = true)
    public Page<EpisodeAssociateDTO> findAll(Pageable pageable) {
        log.debug("Request to get all EpisodeAssociates");
        return episodeAssociateRepository.findAll(pageable)
            .map(episodeAssociateMapper::toDto);
    }


    /**
     * Get one episodeAssociate by id.
     *
     * @param id the id of the entity
     * @return the entity
     */
    @Override
    @Transactional(readOnly = true)
    public Optional<EpisodeAssociateDTO> findOne(Long id) {
        log.debug("Request to get EpisodeAssociate : {}", id);
        return episodeAssociateRepository.findById(id)
            .map(episodeAssociateMapper::toDto);
    }

    /**
     * Delete the episodeAssociate by id.
     *
     * @param id the id of the entity
     */
    @Override
    public void delete(Long id) {
        log.debug("Request to delete EpisodeAssociate : {}", id);
        episodeAssociateRepository.deleteById(id);
    }
    

    /**
     * Get the "episodeId" episodeAssociate.
     *
     * @param episodeId the episodeId of the entity
     * @return the entity
     */
    @Override
    @Transactional(readOnly = true)
    public List<EpisodeAssociateDTO> findAllByEpisodeId(Long episodeId) {
        log.debug("Request to get EpisodeAssociate by episodeId : {}", episodeId);
        List<EpisodeAssociate> episodeAssociateList = episodeAssociateRepository.findAllByEpisodeId(episodeId);
        return episodeAssociateMapper.toDto(episodeAssociateList);
    }
    
    /**
     * Get the "associateOn" episodeAssociate.
     *
     * @param associateOn the associateOn of the entity
     * @return the entity
     */
    public List<EpisodeAssociateDTO> findAllByAssociateOn(Long associateOn){
        log.debug("Request to get EpisodeAssociate by associateOn : {}", associateOn);
        List<EpisodeAssociate> episodeAssociateList = episodeAssociateRepository.findAllByAssociateOn(associateOn);
        return episodeAssociateMapper.toDto(episodeAssociateList);
    }

}
