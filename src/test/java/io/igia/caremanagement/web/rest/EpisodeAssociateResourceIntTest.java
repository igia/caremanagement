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
import io.igia.caremanagement.domain.EpisodeAssociate;
import io.igia.caremanagement.domain.enumeration.CaseExecutionEvent;
import io.igia.caremanagement.repository.EpisodeAssociateRepository;
import io.igia.caremanagement.service.EpisodeAssociateService;
import io.igia.caremanagement.service.dto.EpisodeAssociateDTO;
import io.igia.caremanagement.service.mapper.EpisodeAssociateMapper;
import io.igia.caremanagement.web.rest.EpisodeAssociateResource;
import io.igia.caremanagement.web.rest.errors.ExceptionTranslator;

import javax.persistence.EntityManager;
import java.util.List;

import static io.igia.caremanagement.web.rest.TestUtil.createFormattingConversionService;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
/**
 * Test class for the EpisodeAssociateResource REST controller.
 *
 * @see EpisodeAssociateResource
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = CaremanagementApp.class)
public class EpisodeAssociateResourceIntTest {

    private static final CaseExecutionEvent DEFAULT_ASSOCIATE_EVENT = CaseExecutionEvent.CREATE;
    private static final CaseExecutionEvent UPDATED_ASSOCIATE_EVENT = CaseExecutionEvent.COMPLETE;

    private static final Long DEFAULT_ASSOCIATE_ON = 1L;
    private static final Long UPDATED_ASSOCIATE_ON = 2L;

    @Autowired
    private EpisodeAssociateRepository episodeAssociateRepository;

    @Autowired
    private EpisodeAssociateMapper episodeAssociateMapper;
    
    @Autowired
    private EpisodeAssociateService episodeAssociateService;

    @Autowired
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Autowired
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    @Autowired
    private ExceptionTranslator exceptionTranslator;

    @Autowired
    private EntityManager em;

    private MockMvc restEpisodeAssociateMockMvc;

    private EpisodeAssociate episodeAssociate;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        final EpisodeAssociateResource episodeAssociateResource = new EpisodeAssociateResource(episodeAssociateService);
        this.restEpisodeAssociateMockMvc = MockMvcBuilders.standaloneSetup(episodeAssociateResource)
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
    public static EpisodeAssociate createEntity(EntityManager em) {
        EpisodeAssociate episodeAssociate = new EpisodeAssociate()
            .associateEvent(DEFAULT_ASSOCIATE_EVENT)
            .associateOn(DEFAULT_ASSOCIATE_ON);
        return episodeAssociate;
    }

    @Before
    public void initTest() {
        episodeAssociate = createEntity(em);
    }

    @Test
    @Transactional
    public void createEpisodeAssociate() throws Exception {
        int databaseSizeBeforeCreate = episodeAssociateRepository.findAll().size();

        // Create the EpisodeAssociate
        EpisodeAssociateDTO episodeAssociateDTO = episodeAssociateMapper.toDto(episodeAssociate);
        restEpisodeAssociateMockMvc.perform(post("/api/definitions/episode-associates")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(episodeAssociateDTO)))
            .andExpect(status().isCreated());

        // Validate the EpisodeAssociate in the database
        List<EpisodeAssociate> episodeAssociateList = episodeAssociateRepository.findAll();
        assertThat(episodeAssociateList).hasSize(databaseSizeBeforeCreate + 1);
        EpisodeAssociate testEpisodeAssociate = episodeAssociateList.get(episodeAssociateList.size() - 1);
        assertThat(testEpisodeAssociate.getAssociateEvent()).isEqualTo(DEFAULT_ASSOCIATE_EVENT);
        assertThat(testEpisodeAssociate.getAssociateOn()).isEqualTo(DEFAULT_ASSOCIATE_ON);
    }

    @Test
    @Transactional
    public void createEpisodeAssociateWithExistingId() throws Exception {
        int databaseSizeBeforeCreate = episodeAssociateRepository.findAll().size();

        // Create the EpisodeAssociate with an existing ID
        episodeAssociate.setId(1L);
        EpisodeAssociateDTO episodeAssociateDTO = episodeAssociateMapper.toDto(episodeAssociate);

        // An entity with an existing ID cannot be created, so this API call must fail
        restEpisodeAssociateMockMvc.perform(post("/api/definitions/episode-associates")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(episodeAssociateDTO)))
            .andExpect(status().isBadRequest());

        // Validate the EpisodeAssociate in the database
        List<EpisodeAssociate> episodeAssociateList = episodeAssociateRepository.findAll();
        assertThat(episodeAssociateList).hasSize(databaseSizeBeforeCreate);
    }


    @Test
    @Transactional
    public void getAllEpisodeAssociates() throws Exception {
        // Initialize the database
        episodeAssociateRepository.saveAndFlush(episodeAssociate);

        // Get all the episodeAssociateList
        restEpisodeAssociateMockMvc.perform(get("/api/definitions/episode-associates?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(episodeAssociate.getId().intValue())))
            .andExpect(jsonPath("$.[*].associateEvent").value(hasItem(DEFAULT_ASSOCIATE_EVENT.toString())))
            .andExpect(jsonPath("$.[*].associateOn").value(hasItem(DEFAULT_ASSOCIATE_ON.intValue())));
    }
    
    @Test
    @Transactional
    public void getEpisodeAssociate() throws Exception {
        // Initialize the database
        episodeAssociateRepository.saveAndFlush(episodeAssociate);

        // Get the episodeAssociate
        restEpisodeAssociateMockMvc.perform(get("/api/definitions/episode-associates/{id}", episodeAssociate.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.id").value(episodeAssociate.getId().intValue()))
            .andExpect(jsonPath("$.associateEvent").value(DEFAULT_ASSOCIATE_EVENT.toString()))
            .andExpect(jsonPath("$.associateOn").value(DEFAULT_ASSOCIATE_ON.intValue()));
    }

    @Test
    @Transactional
    public void getNonExistingEpisodeAssociate() throws Exception {
        // Get the episodeAssociate
        restEpisodeAssociateMockMvc.perform(get("/api/definitions/episode-associates/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateEpisodeAssociate() throws Exception {
        // Initialize the database
        episodeAssociateRepository.saveAndFlush(episodeAssociate);

        int databaseSizeBeforeUpdate = episodeAssociateRepository.findAll().size();

        // Update the episodeAssociate
        EpisodeAssociate updatedEpisodeAssociate = episodeAssociateRepository.findById(episodeAssociate.getId()).get();
        // Disconnect from session so that the updates on updatedEpisodeAssociate are not directly saved in db
        em.detach(updatedEpisodeAssociate);
        updatedEpisodeAssociate
            .associateEvent(UPDATED_ASSOCIATE_EVENT)
            .associateOn(UPDATED_ASSOCIATE_ON);
        EpisodeAssociateDTO episodeAssociateDTO = episodeAssociateMapper.toDto(updatedEpisodeAssociate);

        restEpisodeAssociateMockMvc.perform(put("/api/definitions/episode-associates")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(episodeAssociateDTO)))
            .andExpect(status().isOk());

        // Validate the EpisodeAssociate in the database
        List<EpisodeAssociate> episodeAssociateList = episodeAssociateRepository.findAll();
        assertThat(episodeAssociateList).hasSize(databaseSizeBeforeUpdate);
        EpisodeAssociate testEpisodeAssociate = episodeAssociateList.get(episodeAssociateList.size() - 1);
        assertThat(testEpisodeAssociate.getAssociateEvent()).isEqualTo(UPDATED_ASSOCIATE_EVENT);
        assertThat(testEpisodeAssociate.getAssociateOn()).isEqualTo(UPDATED_ASSOCIATE_ON);
    }

    @Test
    @Transactional
    public void updateNonExistingEpisodeAssociate() throws Exception {
        int databaseSizeBeforeUpdate = episodeAssociateRepository.findAll().size();

        // Create the EpisodeAssociate
        EpisodeAssociateDTO episodeAssociateDTO = episodeAssociateMapper.toDto(episodeAssociate);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restEpisodeAssociateMockMvc.perform(put("/api/definitions/episode-associates")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(episodeAssociateDTO)))
            .andExpect(status().isBadRequest());

        // Validate the EpisodeAssociate in the database
        List<EpisodeAssociate> episodeAssociateList = episodeAssociateRepository.findAll();
        assertThat(episodeAssociateList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    public void deleteEpisodeAssociate() throws Exception {
        // Initialize the database
        episodeAssociateRepository.saveAndFlush(episodeAssociate);

        int databaseSizeBeforeDelete = episodeAssociateRepository.findAll().size();

        // Get the episodeAssociate
        restEpisodeAssociateMockMvc.perform(delete("/api/definitions/episode-associates/{id}", episodeAssociate.getId())
            .accept(TestUtil.APPLICATION_JSON_UTF8))
            .andExpect(status().isOk());

        // Validate the database is empty
        List<EpisodeAssociate> episodeAssociateList = episodeAssociateRepository.findAll();
        assertThat(episodeAssociateList).hasSize(databaseSizeBeforeDelete - 1);
    }

    @Test
    @Transactional
    public void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(EpisodeAssociate.class);
        EpisodeAssociate episodeAssociate1 = new EpisodeAssociate();
        episodeAssociate1.setId(1L);
        EpisodeAssociate episodeAssociate2 = new EpisodeAssociate();
        episodeAssociate2.setId(episodeAssociate1.getId());
        assertThat(episodeAssociate1).isEqualTo(episodeAssociate2);
        episodeAssociate2.setId(2L);
        assertThat(episodeAssociate1).isNotEqualTo(episodeAssociate2);
        episodeAssociate1.setId(null);
        assertThat(episodeAssociate1).isNotEqualTo(episodeAssociate2);
    }

    @Test
    @Transactional
    public void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(EpisodeAssociateDTO.class);
        EpisodeAssociateDTO episodeAssociateDTO1 = new EpisodeAssociateDTO();
        episodeAssociateDTO1.setId(1L);
        EpisodeAssociateDTO episodeAssociateDTO2 = new EpisodeAssociateDTO();
        assertThat(episodeAssociateDTO1).isNotEqualTo(episodeAssociateDTO2);
        episodeAssociateDTO2.setId(episodeAssociateDTO1.getId());
        assertThat(episodeAssociateDTO1).isEqualTo(episodeAssociateDTO2);
        episodeAssociateDTO2.setId(2L);
        assertThat(episodeAssociateDTO1).isNotEqualTo(episodeAssociateDTO2);
        episodeAssociateDTO1.setId(null);
        assertThat(episodeAssociateDTO1).isNotEqualTo(episodeAssociateDTO2);
    }

    @Test
    @Transactional
    public void testEntityFromId() {
        assertThat(episodeAssociateMapper.fromId(42L).getId()).isEqualTo(42);
        assertThat(episodeAssociateMapper.fromId(null)).isNull();
    }
}
