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

import io.igia.caremanagement.domain.Episode;
import io.igia.caremanagement.repository.EpisodeRepository;
import io.igia.caremanagement.service.EpisodeService;
import io.igia.caremanagement.service.dto.EpisodeDTO;
import io.igia.caremanagement.service.mapper.EpisodeMapper;
import io.igia.caremanagement.web.rest.errors.CustomParameterizedException;

/**
 * Service Implementation for managing Episode.
 */
@Service
@Transactional
public class EpisodeServiceImpl implements EpisodeService {

    private final Logger log = LoggerFactory.getLogger(EpisodeServiceImpl.class);

    private final EpisodeRepository episodeRepository;

    private final EpisodeMapper episodeMapper;

    public EpisodeServiceImpl(EpisodeRepository episodeRepository, EpisodeMapper episodeMapper) {
        this.episodeRepository = episodeRepository;
        this.episodeMapper = episodeMapper;
    }

    /**
     * Save a episode.
     *
     * @param episodeDTO
     *            the entity to save
     * @return the persisted entity
     */
    @Override
    public EpisodeDTO save(EpisodeDTO episodeDTO) {
        log.debug("Request to save Episode : {}", episodeDTO);
        if (episodeDTO.getId() != null) {
            Optional<EpisodeDTO> dto = findOne(episodeDTO.getId());
            if (!dto.isPresent()) {
                throw new CustomParameterizedException("Episode does not exist", "");
            }
        }
        Episode episode = episodeMapper.toEntity(episodeDTO);
        episode = episodeRepository.save(episode);
        return episodeMapper.toDto(episode);
    }

    /**
     * Get all the episodes.
     *
     * @param pageable
     *            the pagination information
     * @return the list of entities
     */
    @Override
    @Transactional(readOnly = true)
    public Page<EpisodeDTO> findAll(Pageable pageable) {
        log.debug("Request to get all Episodes");
        return episodeRepository.findAll(pageable).map(episodeMapper::toDto);
    }

    /**
     * Get one episode by id.
     *
     * @param id
     *            the id of the entity
     * @return the entity
     */
    @Override
    @Transactional(readOnly = true)
    public Optional<EpisodeDTO> findOne(Long id) {
        log.debug("Request to get Episode : {}", id);
        return episodeRepository.findById(id).map(episodeMapper::toDto);
    }

    /**
     * Delete the episode by id.
     *
     * @param id
     *            the id of the entity
     */
    @Override
    public void delete(Long id) {
        log.debug("Request to delete Episode : {}", id);
        episodeRepository.deleteById(id);
    }

    /**
     * Get one episode by programId.
     *
     * @param programId
     *            the programId of the entity
     * @return the entity
     */
    @Override
    @Transactional(readOnly = true)
    public List<EpisodeDTO> findAllByProgramId(Long programId) {
        log.debug("Request to get Episode by programId : {}", programId);
        List<Episode> episodeList = episodeRepository.findAllByProgramId(programId);
        return episodeMapper.toDto(episodeList);
    }

}
