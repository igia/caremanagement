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

import static io.igia.caremanagement.web.rest.TestUtil.createFormattingConversionService;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;

import javax.persistence.EntityManager;

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
import io.igia.caremanagement.domain.Goal;
import io.igia.caremanagement.domain.GoalAssociate;
import io.igia.caremanagement.domain.enumeration.CaseExecutionEvent;
import io.igia.caremanagement.domain.enumeration.TimeUnit;
import io.igia.caremanagement.repository.EpisodeRepository;
import io.igia.caremanagement.repository.GoalAssociateRepository;
import io.igia.caremanagement.repository.GoalRepository;
import io.igia.caremanagement.service.EpisodeService;
import io.igia.caremanagement.service.GoalAssociateService;
import io.igia.caremanagement.service.GoalService;
import io.igia.caremanagement.service.dto.EpisodeDTO;
import io.igia.caremanagement.service.dto.GoalAssociateDTO;
import io.igia.caremanagement.service.dto.GoalDTO;
import io.igia.caremanagement.service.mapper.EpisodeMapper;
import io.igia.caremanagement.service.mapper.GoalAssociateMapper;
import io.igia.caremanagement.service.mapper.GoalMapper;
import io.igia.caremanagement.web.rest.EpisodeResource;
import io.igia.caremanagement.web.rest.GoalAssociateResource;
import io.igia.caremanagement.web.rest.GoalResource;
import io.igia.caremanagement.web.rest.errors.ExceptionTranslator;

/**
 * Test class for the GoalAssociateResource REST controller.
 *
 * @see GoalAssociateResource
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = CaremanagementApp.class)
public class GoalAssociateResourceIntTest {

    private static final CaseExecutionEvent DEFAULT_ASSOCIATE_EVENT = CaseExecutionEvent.CREATE;
    private static final CaseExecutionEvent UPDATED_ASSOCIATE_EVENT = CaseExecutionEvent.COMPLETE;

    private static final Long DEFAULT_ASSOCIATE_ON = 1L;
    private static final Long UPDATED_ASSOCIATE_ON = 2L;

    @Autowired
    private GoalAssociateRepository goalAssociateRepository;

    @Autowired
    private GoalAssociateMapper goalAssociateMapper;

    @Autowired
    private GoalAssociateService goalAssociateService;

    @Autowired
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Autowired
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    @Autowired
    private ExceptionTranslator exceptionTranslator;

    @Autowired
    private EntityManager em;

    private MockMvc restGoalAssociateMockMvc;

    private GoalAssociate goalAssociate;
    
    @Autowired
    private EpisodeRepository episodeRepository;


    @Autowired
    private GoalRepository goalRepository;

    @Autowired
    private GoalMapper goalMapper;

    @Autowired
    private GoalService goalService;

    private Goal goal;

    private MockMvc restGoalMockMvc;
    
    @Autowired
    private EpisodeService episodeService;
    
    private MockMvc restEpisodeMockMvc;
    
    private Episode episode;
    
    @Autowired
    private EpisodeMapper episodeMapper;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        final GoalAssociateResource goalAssociateResource = new GoalAssociateResource(goalAssociateService);
        this.restGoalAssociateMockMvc = MockMvcBuilders.standaloneSetup(goalAssociateResource)
                .setCustomArgumentResolvers(pageableArgumentResolver).setControllerAdvice(exceptionTranslator)
                .setConversionService(createFormattingConversionService()).setMessageConverters(jacksonMessageConverter)
                .build();

        final GoalResource goalResource = new GoalResource(goalService);
        this.restGoalMockMvc = MockMvcBuilders.standaloneSetup(goalResource)
                .setCustomArgumentResolvers(pageableArgumentResolver).setControllerAdvice(exceptionTranslator)
                .setConversionService(createFormattingConversionService()).setMessageConverters(jacksonMessageConverter)
                .build();
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
    public static GoalAssociate createEntity(EntityManager em) {
        GoalAssociate goalAssociate = new GoalAssociate().associateEvent(DEFAULT_ASSOCIATE_EVENT)
                .associateOn(DEFAULT_ASSOCIATE_ON);
        return goalAssociate;
    }

    public static Goal createEntityGoal(EntityManager em) {
        Goal goal = new Goal().goalIdU("AAAA").name("AAAA").description("AAAA").etaValue(1).etaUnit(TimeUnit.DAY)
                .lookup("G1");
        return goal;
    }
    
    public static Episode createEntityEpisode(EntityManager em) {
        Episode episode = new Episode()
            .episodeIdU("AAAA")
            .name("AAAAAAAAAAAA")
            .description("AAAAAAAAA")
            .lookup("AAAA");
        return episode;
    }

    @Before
    public void initTest() {
        goalAssociate = createEntity(em);
        goal = createEntityGoal(em);
        episode = createEntityEpisode(em);
    }

    @Test
    @Transactional
    public void createGoalAssociate() throws Exception {
        EpisodeDTO episodeDTO = episodeMapper.toDto(episode);
        restEpisodeMockMvc.perform(post("/api/definitions/episodes")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(episodeDTO)))
            .andExpect(status().isCreated());
        List<Episode> episodeList = episodeRepository.findAll();
        Episode testEpisode = episodeList.get(episodeList.size() - 1);

        GoalDTO goalDTO = goalMapper.toDto(goal);
        goalDTO.setEpisodeId(testEpisode.getId());
        restGoalMockMvc.perform(post("/api/definitions/goals").contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(goalDTO))).andExpect(status().isCreated());

        List<Goal> goalList = goalRepository.findAll();
        Goal testGoal = goalList.get(goalList.size() - 1);

        int databaseSizeBeforeCreate = goalAssociateRepository.findAll().size();

        // Create the GoalAssociate
        GoalAssociateDTO goalAssociateDTO = goalAssociateMapper.toDto(goalAssociate);
        goalAssociateDTO.setGoalId(testGoal.getId());
        goalAssociateDTO.setAssociateOn(testGoal.getId());
        
        restGoalAssociateMockMvc
                .perform(post("/api/definitions/goal-associates").contentType(TestUtil.APPLICATION_JSON_UTF8)
                        .content(TestUtil.convertObjectToJsonBytes(goalAssociateDTO)))
                .andExpect(status().isCreated());

        // Validate the GoalAssociate in the database
        List<GoalAssociate> goalAssociateList = goalAssociateRepository.findAll();
        assertThat(goalAssociateList).hasSize(databaseSizeBeforeCreate + 1);
        GoalAssociate testGoalAssociate = goalAssociateList.get(goalAssociateList.size() - 1);
        assertThat(testGoalAssociate.getAssociateEvent()).isEqualTo(DEFAULT_ASSOCIATE_EVENT);
    }

    @Test
    @Transactional
    public void createGoalAssociateWithExistingId() throws Exception {
        int databaseSizeBeforeCreate = goalAssociateRepository.findAll().size();

        // Create the GoalAssociate with an existing ID
        goalAssociate.setId(1L);
        GoalAssociateDTO goalAssociateDTO = goalAssociateMapper.toDto(goalAssociate);

        // An entity with an existing ID cannot be created, so this API call
        // must fail
        restGoalAssociateMockMvc
                .perform(post("/api/definitions/goal-associates").contentType(TestUtil.APPLICATION_JSON_UTF8)
                        .content(TestUtil.convertObjectToJsonBytes(goalAssociateDTO)))
                .andExpect(status().isBadRequest());

        // Validate the GoalAssociate in the database
        List<GoalAssociate> goalAssociateList = goalAssociateRepository.findAll();
        assertThat(goalAssociateList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    public void getAllGoalAssociates() throws Exception {
        EpisodeDTO episodeDTO = episodeMapper.toDto(episode);
        restEpisodeMockMvc.perform(post("/api/definitions/episodes")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(episodeDTO)))
            .andExpect(status().isCreated());
        List<Episode> episodeList = episodeRepository.findAll();
        Episode testEpisode = episodeList.get(episodeList.size() - 1);

        GoalDTO goalDTO = goalMapper.toDto(goal);
        goalDTO.setEpisodeId(testEpisode.getId());
        restGoalMockMvc.perform(post("/api/definitions/goals").contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(goalDTO))).andExpect(status().isCreated());

        List<Goal> goalList = goalRepository.findAll();
        Goal testGoal = goalList.get(goalList.size() - 1);
        goalAssociate.setAssociateOn(testGoal.getId());
        Goal goal = new Goal();
        goal.setId(testGoal.getId());
        goalAssociate.setGoal(goal);
        // Initialize the database
        goalAssociateRepository.saveAndFlush(goalAssociate);

        // Get all the goalAssociateList
        restGoalAssociateMockMvc.perform(get("/api/definitions/goal-associates?sort=id,desc"))
                .andExpect(status().isOk()).andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andExpect(jsonPath("$.[*].id").value(hasItem(goalAssociate.getId().intValue())))
                .andExpect(jsonPath("$.[*].associateEvent").value(hasItem(DEFAULT_ASSOCIATE_EVENT.toString())))
                .andExpect(jsonPath("$.[*].associateOn").value(hasItem(goalAssociate.getAssociateOn().intValue())));
    }

    @Test
    @Transactional
    public void getGoalAssociate() throws Exception {
        EpisodeDTO episodeDTO = episodeMapper.toDto(episode);
        restEpisodeMockMvc.perform(post("/api/definitions/episodes")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(episodeDTO)))
            .andExpect(status().isCreated());
        List<Episode> episodeList = episodeRepository.findAll();
        Episode testEpisode = episodeList.get(episodeList.size() - 1);

        GoalDTO goalDTO = goalMapper.toDto(goal);
        goalDTO.setEpisodeId(testEpisode.getId());
        restGoalMockMvc.perform(post("/api/definitions/goals").contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(goalDTO))).andExpect(status().isCreated());

        List<Goal> goalList = goalRepository.findAll();
        Goal testGoal = goalList.get(goalList.size() - 1);
        goalAssociate.setAssociateOn(testGoal.getId());
        Goal goal = new Goal();
        goal.setId(testGoal.getId());
        goalAssociate.setGoal(goal);

        // Initialize the database
        goalAssociateRepository.saveAndFlush(goalAssociate);

        // Get the goalAssociate
        restGoalAssociateMockMvc.perform(get("/api/definitions/goal-associates/{id}", goalAssociate.getId()))
                .andExpect(status().isOk()).andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andExpect(jsonPath("$.id").value(goalAssociate.getId().intValue()))
                .andExpect(jsonPath("$.associateEvent").value(DEFAULT_ASSOCIATE_EVENT.toString()))
                .andExpect(jsonPath("$.associateOn").value(goalAssociate.getAssociateOn().intValue()));
    }

    @Test
    @Transactional
    public void getNonExistingGoalAssociate() throws Exception {
        // Get the goalAssociate
        restGoalAssociateMockMvc.perform(get("/api/definitions/goal-associates/{id}", Long.MAX_VALUE))
                .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateGoalAssociate() throws Exception {
        EpisodeDTO episodeDTO = episodeMapper.toDto(episode);
        restEpisodeMockMvc.perform(post("/api/definitions/episodes")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(episodeDTO)))
            .andExpect(status().isCreated());
        List<Episode> episodeList = episodeRepository.findAll();
        Episode testEpisode = episodeList.get(episodeList.size() - 1);

        GoalDTO goalDTO = goalMapper.toDto(goal);
        goalDTO.setEpisodeId(testEpisode.getId());
        restGoalMockMvc.perform(post("/api/definitions/goals").contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(goalDTO))).andExpect(status().isCreated());

        List<Goal> goalList = goalRepository.findAll();
        Goal testGoal = goalList.get(goalList.size() - 1);
        goalAssociate.setAssociateOn(testGoal.getId());
        Goal goal = new Goal();
        goal.setId(testGoal.getId());
        goalAssociate.setGoal(goal);
        // Initialize the database
        goalAssociateRepository.saveAndFlush(goalAssociate);

        int databaseSizeBeforeUpdate = goalAssociateRepository.findAll().size();

        // Update the goalAssociate
        GoalAssociate updatedGoalAssociate = goalAssociateRepository.findById(goalAssociate.getId()).get();
        // Disconnect from session so that the updates on updatedGoalAssociate
        // are not directly saved in db
        em.detach(updatedGoalAssociate);
        updatedGoalAssociate.associateEvent(UPDATED_ASSOCIATE_EVENT).associateOn(testGoal.getId());
        GoalAssociateDTO goalAssociateDTO = goalAssociateMapper.toDto(updatedGoalAssociate);

        restGoalAssociateMockMvc
                .perform(put("/api/definitions/goal-associates").contentType(TestUtil.APPLICATION_JSON_UTF8)
                        .content(TestUtil.convertObjectToJsonBytes(goalAssociateDTO)))
                .andExpect(status().isOk());

        // Validate the GoalAssociate in the database
        List<GoalAssociate> goalAssociateList = goalAssociateRepository.findAll();
        assertThat(goalAssociateList).hasSize(databaseSizeBeforeUpdate);
        GoalAssociate testGoalAssociate = goalAssociateList.get(goalAssociateList.size() - 1);
        assertThat(testGoalAssociate.getAssociateEvent()).isEqualTo(UPDATED_ASSOCIATE_EVENT);
    }

    @Test
    @Transactional
    public void updateNonExistingGoalAssociate() throws Exception {
        int databaseSizeBeforeUpdate = goalAssociateRepository.findAll().size();

        // Create the GoalAssociate
        GoalAssociateDTO goalAssociateDTO = goalAssociateMapper.toDto(goalAssociate);

        // If the entity doesn't have an ID, it will throw
        // BadRequestAlertException
        restGoalAssociateMockMvc
                .perform(put("/api/definitions/goal-associates").contentType(TestUtil.APPLICATION_JSON_UTF8)
                        .content(TestUtil.convertObjectToJsonBytes(goalAssociateDTO)))
                .andExpect(status().isBadRequest());

        // Validate the GoalAssociate in the database
        List<GoalAssociate> goalAssociateList = goalAssociateRepository.findAll();
        assertThat(goalAssociateList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    public void deleteGoalAssociate() throws Exception {
        EpisodeDTO episodeDTO = episodeMapper.toDto(episode);
        restEpisodeMockMvc.perform(post("/api/definitions/episodes")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(episodeDTO)))
            .andExpect(status().isCreated());
        List<Episode> episodeList = episodeRepository.findAll();
        Episode testEpisode = episodeList.get(episodeList.size() - 1);

        GoalDTO goalDTO = goalMapper.toDto(goal);
        goalDTO.setEpisodeId(testEpisode.getId());
        restGoalMockMvc.perform(post("/api/definitions/goals")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(goalDTO)))
            .andExpect(status().isCreated());
        
        List<Goal> goalList = goalRepository.findAll();
        Goal testGoal = goalList.get(goalList.size() - 1);
        goalAssociate.setAssociateOn(testGoal.getId());
        Goal goal = new Goal();
        goal.setId(testGoal.getId());
        goalAssociate.setGoal(goal);
        // Initialize the database
        goalAssociateRepository.saveAndFlush(goalAssociate);

        int databaseSizeBeforeDelete = goalAssociateRepository.findAll().size();

        // Get the goalAssociate
        restGoalAssociateMockMvc.perform(delete("/api/definitions/goal-associates/{id}", goalAssociate.getId())
                .accept(TestUtil.APPLICATION_JSON_UTF8)).andExpect(status().isOk());

        // Validate the database is empty
        List<GoalAssociate> goalAssociateList = goalAssociateRepository.findAll();
        assertThat(goalAssociateList).hasSize(databaseSizeBeforeDelete - 1);
    }

    @Test
    @Transactional
    public void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(GoalAssociate.class);
        GoalAssociate goalAssociate1 = new GoalAssociate();
        goalAssociate1.setId(1L);
        GoalAssociate goalAssociate2 = new GoalAssociate();
        goalAssociate2.setId(goalAssociate1.getId());
        assertThat(goalAssociate1).isEqualTo(goalAssociate2);
        goalAssociate2.setId(2L);
        assertThat(goalAssociate1).isNotEqualTo(goalAssociate2);
        goalAssociate1.setId(null);
        assertThat(goalAssociate1).isNotEqualTo(goalAssociate2);
    }

    @Test
    @Transactional
    public void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(GoalAssociateDTO.class);
        GoalAssociateDTO goalAssociateDTO1 = new GoalAssociateDTO();
        goalAssociateDTO1.setId(1L);
        GoalAssociateDTO goalAssociateDTO2 = new GoalAssociateDTO();
        assertThat(goalAssociateDTO1).isNotEqualTo(goalAssociateDTO2);
        goalAssociateDTO2.setId(goalAssociateDTO1.getId());
        assertThat(goalAssociateDTO1).isEqualTo(goalAssociateDTO2);
        goalAssociateDTO2.setId(2L);
        assertThat(goalAssociateDTO1).isNotEqualTo(goalAssociateDTO2);
        goalAssociateDTO1.setId(null);
        assertThat(goalAssociateDTO1).isNotEqualTo(goalAssociateDTO2);
    }

    @Test
    @Transactional
    public void testEntityFromId() {
        assertThat(goalAssociateMapper.fromId(42L).getId()).isEqualTo(42);
        assertThat(goalAssociateMapper.fromId(null)).isNull();
    }
}
