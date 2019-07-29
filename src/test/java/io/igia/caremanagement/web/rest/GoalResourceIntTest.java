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
import io.igia.caremanagement.domain.Goal;
import io.igia.caremanagement.domain.enumeration.TimeUnit;
import io.igia.caremanagement.repository.EpisodeRepository;
import io.igia.caremanagement.repository.GoalRepository;
import io.igia.caremanagement.service.EpisodeService;
import io.igia.caremanagement.service.GoalService;
import io.igia.caremanagement.service.dto.EpisodeDTO;
import io.igia.caremanagement.service.dto.GoalDTO;
import io.igia.caremanagement.service.mapper.EpisodeMapper;
import io.igia.caremanagement.service.mapper.GoalMapper;
import io.igia.caremanagement.web.rest.EpisodeResource;
import io.igia.caremanagement.web.rest.GoalResource;
import io.igia.caremanagement.web.rest.errors.ExceptionTranslator;

import javax.persistence.EntityManager;
import java.util.List;

import static io.igia.caremanagement.web.rest.TestUtil.createFormattingConversionService;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
/**
 * Test class for the GoalResource REST controller.
 *
 * @see GoalResource
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = CaremanagementApp.class)
public class GoalResourceIntTest {

    private static final String DEFAULT_GOAL_ID_U = "AAAAAAAAAA";
    private static final String UPDATED_GOAL_ID_U = "BBBBBBBBBB";

    private static final String DEFAULT_NAME = "AAAAAAAAAA";
    private static final String UPDATED_NAME = "BBBBBBBBBB";

    private static final String DEFAULT_DESCRIPTION = "AAAAAAAAAA";
    private static final String UPDATED_DESCRIPTION = "BBBBBBBBBB";

    private static final String DEFAULT_ENTRY_CRITERIA = "AAAAAAAAAA";
    private static final String UPDATED_ENTRY_CRITERIA = "BBBBBBBBBB";

    private static final Integer DEFAULT_ETA_VALUE = 1;
    private static final Integer UPDATED_ETA_VALUE = 2;

    private static final TimeUnit DEFAULT_ETA_UNIT = TimeUnit.DAY;
    private static final TimeUnit UPDATED_ETA_UNIT = TimeUnit.DAYS;

    private static final String DEFAULT_LOOKUP = "AAAAAAAAAA";
    private static final String UPDATED_LOOKUP = "BBBBBBBBBB";

    @Autowired
    private GoalRepository goalRepository;

    @Autowired
    private GoalMapper goalMapper;
    
    @Autowired
    private GoalService goalService;

    @Autowired
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Autowired
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    @Autowired
    private ExceptionTranslator exceptionTranslator;

    @Autowired
    private EntityManager em;

    private MockMvc restGoalMockMvc;

    private Goal goal;
    
    @Autowired
    private EpisodeService episodeService;
    
    private MockMvc restEpisodeMockMvc;
    
    private Episode episode;
    
    @Autowired
    private EpisodeMapper episodeMapper;

    @Autowired
    private EpisodeRepository episodeRepository;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        final GoalResource goalResource = new GoalResource(goalService);
        this.restGoalMockMvc = MockMvcBuilders.standaloneSetup(goalResource)
            .setCustomArgumentResolvers(pageableArgumentResolver)
            .setControllerAdvice(exceptionTranslator)
            .setConversionService(createFormattingConversionService())
            .setMessageConverters(jacksonMessageConverter).build();
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
    public static Goal createEntity(EntityManager em) {
        Goal goal = new Goal()
            .goalIdU(DEFAULT_GOAL_ID_U)
            .name(DEFAULT_NAME)
            .description(DEFAULT_DESCRIPTION)
            .entryCriteria(DEFAULT_ENTRY_CRITERIA)
            .etaValue(DEFAULT_ETA_VALUE)
            .etaUnit(DEFAULT_ETA_UNIT)
            .lookup(DEFAULT_LOOKUP);
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
        goal = createEntity(em);
        episode = createEntityEpisode(em);
    }

    @Test
    @Transactional
    public void createGoal() throws Exception {
        EpisodeDTO episodeDTO = episodeMapper.toDto(episode);
        restEpisodeMockMvc.perform(post("/api/definitions/episodes")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(episodeDTO)))
            .andExpect(status().isCreated());
        List<Episode> episodeList = episodeRepository.findAll();
        Episode testEpisode = episodeList.get(episodeList.size() - 1);

        int databaseSizeBeforeCreate = goalRepository.findAll().size();

        // Create the Goal
        GoalDTO goalDTO = goalMapper.toDto(goal);
        goalDTO.setEpisodeId(testEpisode.getId());
        restGoalMockMvc.perform(post("/api/definitions/goals")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(goalDTO)))
            .andExpect(status().isCreated());

        // Validate the Goal in the database
        List<Goal> goalList = goalRepository.findAll();
        assertThat(goalList).hasSize(databaseSizeBeforeCreate + 1);
        Goal testGoal = goalList.get(goalList.size() - 1);
        assertThat(testGoal.getGoalIdU()).isEqualTo(DEFAULT_GOAL_ID_U);
        assertThat(testGoal.getName()).isEqualTo(DEFAULT_NAME);
        assertThat(testGoal.getDescription()).isEqualTo(DEFAULT_DESCRIPTION);
        assertThat(testGoal.getEntryCriteria()).isEqualTo(DEFAULT_ENTRY_CRITERIA);
        assertThat(testGoal.getEtaValue()).isEqualTo(DEFAULT_ETA_VALUE);
        assertThat(testGoal.getEtaUnit()).isEqualTo(DEFAULT_ETA_UNIT);
        assertThat(testGoal.getLookup()).isEqualTo(DEFAULT_LOOKUP);
    }

    @Test
    @Transactional
    public void createGoalWithExistingId() throws Exception {
        int databaseSizeBeforeCreate = goalRepository.findAll().size();

        // Create the Goal with an existing ID
        goal.setId(1L);
        GoalDTO goalDTO = goalMapper.toDto(goal);

        // An entity with an existing ID cannot be created, so this API call must fail
        restGoalMockMvc.perform(post("/api/definitions/goals")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(goalDTO)))
            .andExpect(status().isBadRequest());

        // Validate the Goal in the database
        List<Goal> goalList = goalRepository.findAll();
        assertThat(goalList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    public void checkGoalIdUIsRequired() throws Exception {
        int databaseSizeBeforeTest = goalRepository.findAll().size();
        // set the field null
        goal.setGoalIdU(null);

        // Create the Goal, which fails.
        GoalDTO goalDTO = goalMapper.toDto(goal);

        restGoalMockMvc.perform(post("/api/definitions/goals")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(goalDTO)))
            .andExpect(status().isBadRequest());

        List<Goal> goalList = goalRepository.findAll();
        assertThat(goalList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void checkNameIsRequired() throws Exception {
        int databaseSizeBeforeTest = goalRepository.findAll().size();
        // set the field null
        goal.setName(null);

        // Create the Goal, which fails.
        GoalDTO goalDTO = goalMapper.toDto(goal);

        restGoalMockMvc.perform(post("/api/definitions/goals")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(goalDTO)))
            .andExpect(status().isBadRequest());

        List<Goal> goalList = goalRepository.findAll();
        assertThat(goalList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void checkEtaValueIsRequired() throws Exception {
        int databaseSizeBeforeTest = goalRepository.findAll().size();
        // set the field null
        goal.setEtaValue(null);

        // Create the Goal, which fails.
        GoalDTO goalDTO = goalMapper.toDto(goal);

        restGoalMockMvc.perform(post("/api/definitions/goals")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(goalDTO)))
            .andExpect(status().isBadRequest());

        List<Goal> goalList = goalRepository.findAll();
        assertThat(goalList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void checkEtaUnitIsRequired() throws Exception {
        int databaseSizeBeforeTest = goalRepository.findAll().size();
        // set the field null
        goal.setEtaUnit(null);

        // Create the Goal, which fails.
        GoalDTO goalDTO = goalMapper.toDto(goal);

        restGoalMockMvc.perform(post("/api/definitions/goals")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(goalDTO)))
            .andExpect(status().isBadRequest());

        List<Goal> goalList = goalRepository.findAll();
        assertThat(goalList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void checkLookupIsRequired() throws Exception {
        int databaseSizeBeforeTest = goalRepository.findAll().size();
        // set the field null
        goal.setLookup(null);

        // Create the Goal, which fails.
        GoalDTO goalDTO = goalMapper.toDto(goal);

        restGoalMockMvc.perform(post("/api/definitions/goals")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(goalDTO)))
            .andExpect(status().isBadRequest());

        List<Goal> goalList = goalRepository.findAll();
        assertThat(goalList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void getAllGoals() throws Exception {
        // Initialize the database
        goalRepository.saveAndFlush(goal);

        // Get all the goalList
        restGoalMockMvc.perform(get("/api/definitions/goals?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(goal.getId().intValue())))
            .andExpect(jsonPath("$.[*].goalIdU").value(hasItem(DEFAULT_GOAL_ID_U.toString())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME.toString())))
            .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION.toString())))
            .andExpect(jsonPath("$.[*].entryCriteria").value(hasItem(DEFAULT_ENTRY_CRITERIA.toString())))
            .andExpect(jsonPath("$.[*].etaValue").value(hasItem(DEFAULT_ETA_VALUE)))
            .andExpect(jsonPath("$.[*].etaUnit").value(hasItem(DEFAULT_ETA_UNIT.toString())))
            .andExpect(jsonPath("$.[*].lookup").value(hasItem(DEFAULT_LOOKUP.toString())));
    }
    
    @Test
    @Transactional
    public void getGoal() throws Exception {
        // Initialize the database
        goalRepository.saveAndFlush(goal);

        // Get the goal
        restGoalMockMvc.perform(get("/api/definitions/goals/{id}", goal.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.id").value(goal.getId().intValue()))
            .andExpect(jsonPath("$.goalIdU").value(DEFAULT_GOAL_ID_U.toString()))
            .andExpect(jsonPath("$.name").value(DEFAULT_NAME.toString()))
            .andExpect(jsonPath("$.description").value(DEFAULT_DESCRIPTION.toString()))
            .andExpect(jsonPath("$.entryCriteria").value(DEFAULT_ENTRY_CRITERIA.toString()))
            .andExpect(jsonPath("$.etaValue").value(DEFAULT_ETA_VALUE))
            .andExpect(jsonPath("$.etaUnit").value(DEFAULT_ETA_UNIT.toString()))
            .andExpect(jsonPath("$.lookup").value(DEFAULT_LOOKUP.toString()));
    }

    @Test
    @Transactional
    public void getNonExistingGoal() throws Exception {
        // Get the goal
        restGoalMockMvc.perform(get("/api/definitions/goals/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateGoal() throws Exception {
      
        // Initialize the database
        goalRepository.saveAndFlush(goal);

        int databaseSizeBeforeUpdate = goalRepository.findAll().size();

        
        // Update the goal
        Goal updatedGoal = goalRepository.findById(goal.getId()).get();
        // Disconnect from session so that the updates on updatedGoal are not directly saved in db
        em.detach(updatedGoal);
        
        EpisodeDTO episodeDTO = episodeMapper.toDto(episode);
        restEpisodeMockMvc.perform(post("/api/definitions/episodes")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(episodeDTO)))
            .andExpect(status().isCreated());
        List<Episode> episodeList = episodeRepository.findAll();
        Episode testEpisode = episodeList.get(episodeList.size() - 1);

        updatedGoal
            .goalIdU(UPDATED_GOAL_ID_U)
            .name(UPDATED_NAME)
            .description(UPDATED_DESCRIPTION)
            .entryCriteria(UPDATED_ENTRY_CRITERIA)
            .etaValue(UPDATED_ETA_VALUE)
            .etaUnit(UPDATED_ETA_UNIT)
            .lookup(UPDATED_LOOKUP);
        GoalDTO goalDTO = goalMapper.toDto(updatedGoal);
        goalDTO.setEpisodeId(testEpisode.getId());

        restGoalMockMvc.perform(put("/api/definitions/goals")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(goalDTO)))
            .andExpect(status().isOk());

        // Validate the Goal in the database
        List<Goal> goalList = goalRepository.findAll();
        assertThat(goalList).hasSize(databaseSizeBeforeUpdate);
        Goal testGoal = goalList.get(goalList.size() - 1);
        assertThat(testGoal.getGoalIdU()).isEqualTo(UPDATED_GOAL_ID_U);
        assertThat(testGoal.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testGoal.getDescription()).isEqualTo(UPDATED_DESCRIPTION);
        assertThat(testGoal.getEntryCriteria()).isEqualTo(UPDATED_ENTRY_CRITERIA);
        assertThat(testGoal.getEtaValue()).isEqualTo(UPDATED_ETA_VALUE);
        assertThat(testGoal.getEtaUnit()).isEqualTo(UPDATED_ETA_UNIT);
        assertThat(testGoal.getLookup()).isEqualTo(UPDATED_LOOKUP);
    }

    @Test
    @Transactional
    public void updateNonExistingGoal() throws Exception {
        int databaseSizeBeforeUpdate = goalRepository.findAll().size();

        // Create the Goal
        GoalDTO goalDTO = goalMapper.toDto(goal);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restGoalMockMvc.perform(put("/api/definitions/goals")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(goalDTO)))
            .andExpect(status().isBadRequest());

        // Validate the Goal in the database
        List<Goal> goalList = goalRepository.findAll();
        assertThat(goalList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    public void deleteGoal() throws Exception {
        // Initialize the database
        goalRepository.saveAndFlush(goal);

        int databaseSizeBeforeDelete = goalRepository.findAll().size();

        // Get the goal
        restGoalMockMvc.perform(delete("/api/definitions/goals/{id}", goal.getId())
            .accept(TestUtil.APPLICATION_JSON_UTF8))
            .andExpect(status().isOk());

        // Validate the database is empty
        List<Goal> goalList = goalRepository.findAll();
        assertThat(goalList).hasSize(databaseSizeBeforeDelete - 1);
    }

    @Test
    @Transactional
    public void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Goal.class);
        Goal goal1 = new Goal();
        goal1.setId(1L);
        Goal goal2 = new Goal();
        goal2.setId(goal1.getId());
        assertThat(goal1).isEqualTo(goal2);
        goal2.setId(2L);
        assertThat(goal1).isNotEqualTo(goal2);
        goal1.setId(null);
        assertThat(goal1).isNotEqualTo(goal2);
    }

    @Test
    @Transactional
    public void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(GoalDTO.class);
        GoalDTO goalDTO1 = new GoalDTO();
        goalDTO1.setId(1L);
        GoalDTO goalDTO2 = new GoalDTO();
        assertThat(goalDTO1).isNotEqualTo(goalDTO2);
        goalDTO2.setId(goalDTO1.getId());
        assertThat(goalDTO1).isEqualTo(goalDTO2);
        goalDTO2.setId(2L);
        assertThat(goalDTO1).isNotEqualTo(goalDTO2);
        goalDTO1.setId(null);
        assertThat(goalDTO1).isNotEqualTo(goalDTO2);
    }

    @Test
    @Transactional
    public void testEntityFromId() {
        assertThat(goalMapper.fromId(42L).getId()).isEqualTo(42);
        assertThat(goalMapper.fromId(null)).isNull();
    }
}
