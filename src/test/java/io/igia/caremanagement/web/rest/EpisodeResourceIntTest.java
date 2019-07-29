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
package io.igia.caremanagement.web.rest;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;

import io.igia.caremanagement.CaremanagementApp;
import io.igia.caremanagement.domain.Episode;
import io.igia.caremanagement.repository.EpisodeRepository;
import io.igia.caremanagement.service.EpisodeService;
import io.igia.caremanagement.service.dto.EpisodeDTO;
import io.igia.caremanagement.service.mapper.EpisodeMapper;
import io.igia.caremanagement.web.rest.EpisodeResource;
import io.igia.caremanagement.web.rest.errors.ExceptionTranslator;

import javax.persistence.EntityManager;
import java.util.List;

import static io.igia.caremanagement.web.rest.TestUtil.createFormattingConversionService;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Test class for the EpisodeResource REST controller.
 *
 * @see EpisodeResource
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = CaremanagementApp.class)
public class EpisodeResourceIntTest {

    private static final String DEFAULT_EPISODE_ID_U = "AAAAAAAAAA";
    private static final String UPDATED_EPISODE_ID_U = "BBBBBBBBBB";

    private static final String DEFAULT_NAME = "AAAAAAAAAA";
    private static final String UPDATED_NAME = "BBBBBBBBBB";

    private static final String DEFAULT_DESCRIPTION = "AAAAAAAAAA";
    private static final String UPDATED_DESCRIPTION = "BBBBBBBBBB";

    private static final String DEFAULT_ENTRY_CRITERIA = "AAAAAAAAAA";
    private static final String UPDATED_ENTRY_CRITERIA = "BBBBBBBBBB";

    private static final String DEFAULT_LOOKUP = "AAAAAAAAAA";
    private static final String UPDATED_LOOKUP = "BBBBBBBBBB";

    @Autowired
    private EpisodeRepository episodeRepository;

    @Autowired
    private EpisodeMapper episodeMapper;
    
    @Autowired
    private EpisodeService episodeService;

    @Autowired
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Autowired
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    @Autowired
    private ExceptionTranslator exceptionTranslator;

    @Autowired
    private EntityManager em;

    private MockMvc restEpisodeMockMvc;

    private Episode episode;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        final EpisodeResource episodeResource = new EpisodeResource(episodeService);
        this.restEpisodeMockMvc = MockMvcBuilders.standaloneSetup(episodeResource)
            .setCustomArgumentResolvers(pageableArgumentResolver)
            .setControllerAdvice(exceptionTranslator)
            .setConversionService(createFormattingConversionService())
            .setMessageConverters(jacksonMessageConverter).build();
    }

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Episode createEntity(EntityManager em) {
        Episode episode = new Episode()
            .episodeIdU(DEFAULT_EPISODE_ID_U)
            .name(DEFAULT_NAME)
            .description(DEFAULT_DESCRIPTION)
            .entryCriteria(DEFAULT_ENTRY_CRITERIA)
            .lookup(DEFAULT_LOOKUP);
        return episode;
    }

    @Before
    public void initTest() {
        episode = createEntity(em);
    }

    @Test
    @Transactional
    public void createEpisode() throws Exception {
        int databaseSizeBeforeCreate = episodeRepository.findAll().size();

        // Create the Episode
        EpisodeDTO episodeDTO = episodeMapper.toDto(episode);
        restEpisodeMockMvc.perform(post("/api/definitions/episodes")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(episodeDTO)))
            .andExpect(status().isCreated());

        // Validate the Episode in the database
        List<Episode> episodeList = episodeRepository.findAll();
        assertThat(episodeList).hasSize(databaseSizeBeforeCreate + 1);
        Episode testEpisode = episodeList.get(episodeList.size() - 1);
        assertThat(testEpisode.getEpisodeIdU()).isEqualTo(DEFAULT_EPISODE_ID_U);
        assertThat(testEpisode.getName()).isEqualTo(DEFAULT_NAME);
        assertThat(testEpisode.getDescription()).isEqualTo(DEFAULT_DESCRIPTION);
        assertThat(testEpisode.getEntryCriteria()).isEqualTo(DEFAULT_ENTRY_CRITERIA);
        assertThat(testEpisode.getLookup()).isEqualTo(DEFAULT_LOOKUP);
    }

    @Test
    @Transactional
    public void createEpisodeWithExistingId() throws Exception {
        int databaseSizeBeforeCreate = episodeRepository.findAll().size();

        // Create the Episode with an existing ID
        episode.setId(1L);
        EpisodeDTO episodeDTO = episodeMapper.toDto(episode);

        // An entity with an existing ID cannot be created, so this API call must fail
        restEpisodeMockMvc.perform(post("/api/definitions/episodes")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(episodeDTO)))
            .andExpect(status().isBadRequest());

        // Validate the Episode in the database
        List<Episode> episodeList = episodeRepository.findAll();
        assertThat(episodeList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    public void checkEpisodeIdUIsRequired() throws Exception {
        int databaseSizeBeforeTest = episodeRepository.findAll().size();
        // set the field null
        episode.setEpisodeIdU(null);

        // Create the Episode, which fails.
        EpisodeDTO episodeDTO = episodeMapper.toDto(episode);

        restEpisodeMockMvc.perform(post("/api/definitions/episodes")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(episodeDTO)))
            .andExpect(status().isBadRequest());

        List<Episode> episodeList = episodeRepository.findAll();
        assertThat(episodeList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void checkNameIsRequired() throws Exception {
        int databaseSizeBeforeTest = episodeRepository.findAll().size();
        // set the field null
        episode.setName(null);

        // Create the Episode, which fails.
        EpisodeDTO episodeDTO = episodeMapper.toDto(episode);

        restEpisodeMockMvc.perform(post("/api/definitions/episodes")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(episodeDTO)))
            .andExpect(status().isBadRequest());

        List<Episode> episodeList = episodeRepository.findAll();
        assertThat(episodeList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void checkLookupIsRequired() throws Exception {
        int databaseSizeBeforeTest = episodeRepository.findAll().size();
        // set the field null
        episode.setLookup(null);

        // Create the Episode, which fails.
        EpisodeDTO episodeDTO = episodeMapper.toDto(episode);

        restEpisodeMockMvc.perform(post("/api/definitions/episodes")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(episodeDTO)))
            .andExpect(status().isBadRequest());

        List<Episode> episodeList = episodeRepository.findAll();
        assertThat(episodeList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void getAllEpisodes() throws Exception {
        // Initialize the database
        episodeRepository.saveAndFlush(episode);

        // Get all the episodeList
        restEpisodeMockMvc.perform(get("/api/definitions/episodes?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(episode.getId().intValue())))
            .andExpect(jsonPath("$.[*].episodeIdU").value(hasItem(DEFAULT_EPISODE_ID_U.toString())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME.toString())))
            .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION.toString())))
            .andExpect(jsonPath("$.[*].entryCriteria").value(hasItem(DEFAULT_ENTRY_CRITERIA.toString())))
            .andExpect(jsonPath("$.[*].lookup").value(hasItem(DEFAULT_LOOKUP.toString())));
    }
    
    @Test
    @Transactional
    public void getEpisode() throws Exception {
        // Initialize the database
        episodeRepository.saveAndFlush(episode);

        // Get the episode
        restEpisodeMockMvc.perform(get("/api/definitions/episodes/{id}", episode.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.id").value(episode.getId().intValue()))
            .andExpect(jsonPath("$.episodeIdU").value(DEFAULT_EPISODE_ID_U.toString()))
            .andExpect(jsonPath("$.name").value(DEFAULT_NAME.toString()))
            .andExpect(jsonPath("$.description").value(DEFAULT_DESCRIPTION.toString()))
            .andExpect(jsonPath("$.entryCriteria").value(DEFAULT_ENTRY_CRITERIA.toString()))
            .andExpect(jsonPath("$.lookup").value(DEFAULT_LOOKUP.toString()));
    }

    @Test
    @Transactional
    public void getNonExistingEpisode() throws Exception {
        // Get the episode
        restEpisodeMockMvc.perform(get("/api/definitions/episodes/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateEpisode() throws Exception {
        // Initialize the database
        episodeRepository.saveAndFlush(episode);

        int databaseSizeBeforeUpdate = episodeRepository.findAll().size();

        // Update the episode
        Episode updatedEpisode = episodeRepository.findById(episode.getId()).get();
        // Disconnect from session so that the updates on updatedEpisode are not directly saved in db
        em.detach(updatedEpisode);
        updatedEpisode
            .episodeIdU(UPDATED_EPISODE_ID_U)
            .name(UPDATED_NAME)
            .description(UPDATED_DESCRIPTION)
            .entryCriteria(UPDATED_ENTRY_CRITERIA)
            .lookup(UPDATED_LOOKUP);
        EpisodeDTO episodeDTO = episodeMapper.toDto(updatedEpisode);

        restEpisodeMockMvc.perform(put("/api/definitions/episodes")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(episodeDTO)))
            .andExpect(status().isOk());

        // Validate the Episode in the database
        List<Episode> episodeList = episodeRepository.findAll();
        assertThat(episodeList).hasSize(databaseSizeBeforeUpdate);
        Episode testEpisode = episodeList.get(episodeList.size() - 1);
        assertThat(testEpisode.getEpisodeIdU()).isEqualTo(UPDATED_EPISODE_ID_U);
        assertThat(testEpisode.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testEpisode.getDescription()).isEqualTo(UPDATED_DESCRIPTION);
        assertThat(testEpisode.getEntryCriteria()).isEqualTo(UPDATED_ENTRY_CRITERIA);
        assertThat(testEpisode.getLookup()).isEqualTo(UPDATED_LOOKUP);
    }

    @Test
    @Transactional
    public void updateNonExistingEpisode() throws Exception {
        int databaseSizeBeforeUpdate = episodeRepository.findAll().size();

        // Create the Episode
        EpisodeDTO episodeDTO = episodeMapper.toDto(episode);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restEpisodeMockMvc.perform(put("/api/definitions/episodes")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(episodeDTO)))
            .andExpect(status().isBadRequest());

        // Validate the Episode in the database
        List<Episode> episodeList = episodeRepository.findAll();
        assertThat(episodeList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    public void deleteEpisode() throws Exception {
        // Initialize the database
        episodeRepository.saveAndFlush(episode);

        int databaseSizeBeforeDelete = episodeRepository.findAll().size();

        // Get the episode
        restEpisodeMockMvc.perform(delete("/api/definitions/episodes/{id}", episode.getId())
            .accept(TestUtil.APPLICATION_JSON_UTF8))
            .andExpect(status().isOk());

        // Validate the database is empty
        List<Episode> episodeList = episodeRepository.findAll();
        assertThat(episodeList).hasSize(databaseSizeBeforeDelete - 1);
    }

    @Test
    @Transactional
    public void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Episode.class);
        Episode episode1 = new Episode();
        episode1.setId(1L);
        Episode episode2 = new Episode();
        episode2.setId(episode1.getId());
        assertThat(episode1).isEqualTo(episode2);
        episode2.setId(2L);
        assertThat(episode1).isNotEqualTo(episode2);
        episode1.setId(null);
        assertThat(episode1).isNotEqualTo(episode2);
    }

    @Test
    @Transactional
    public void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(EpisodeDTO.class);
        EpisodeDTO episodeDTO1 = new EpisodeDTO();
        episodeDTO1.setId(1L);
        EpisodeDTO episodeDTO2 = new EpisodeDTO();
        assertThat(episodeDTO1).isNotEqualTo(episodeDTO2);
        episodeDTO2.setId(episodeDTO1.getId());
        assertThat(episodeDTO1).isEqualTo(episodeDTO2);
        episodeDTO2.setId(2L);
        assertThat(episodeDTO1).isNotEqualTo(episodeDTO2);
        episodeDTO1.setId(null);
        assertThat(episodeDTO1).isNotEqualTo(episodeDTO2);
    }

    @Test
    @Transactional
    public void testEntityFromId() {
        assertThat(episodeMapper.fromId(42L).getId()).isEqualTo(42);
        assertThat(episodeMapper.fromId(null)).isNull();
    }
}
