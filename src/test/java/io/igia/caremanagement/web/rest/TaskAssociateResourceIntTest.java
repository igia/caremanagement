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
import io.igia.caremanagement.domain.Task;
import io.igia.caremanagement.domain.TaskAssociate;
import io.igia.caremanagement.domain.enumeration.CaseExecutionEvent;
import io.igia.caremanagement.domain.enumeration.TimeUnit;
import io.igia.caremanagement.domain.enumeration.Type;
import io.igia.caremanagement.repository.EpisodeRepository;
import io.igia.caremanagement.repository.GoalRepository;
import io.igia.caremanagement.repository.TaskAssociateRepository;
import io.igia.caremanagement.repository.TaskRepository;
import io.igia.caremanagement.service.EpisodeService;
import io.igia.caremanagement.service.GoalService;
import io.igia.caremanagement.service.TaskAssociateService;
import io.igia.caremanagement.service.TaskService;
import io.igia.caremanagement.service.dto.EpisodeDTO;
import io.igia.caremanagement.service.dto.GoalDTO;
import io.igia.caremanagement.service.dto.TaskAssociateDTO;
import io.igia.caremanagement.service.dto.TaskDTO;
import io.igia.caremanagement.service.mapper.EpisodeMapper;
import io.igia.caremanagement.service.mapper.GoalMapper;
import io.igia.caremanagement.service.mapper.TaskAssociateMapper;
import io.igia.caremanagement.service.mapper.TaskMapper;
import io.igia.caremanagement.web.rest.EpisodeResource;
import io.igia.caremanagement.web.rest.GoalResource;
import io.igia.caremanagement.web.rest.TaskAssociateResource;
import io.igia.caremanagement.web.rest.TaskResource;
import io.igia.caremanagement.web.rest.errors.ExceptionTranslator;
/**
 * Test class for the TaskAssociateResource REST controller.
 *
 * @see TaskAssociateResource
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = CaremanagementApp.class)
public class TaskAssociateResourceIntTest {

    private static final CaseExecutionEvent DEFAULT_ASSOCIATE_EVENT = CaseExecutionEvent.CREATE;
    private static final CaseExecutionEvent UPDATED_ASSOCIATE_EVENT = CaseExecutionEvent.COMPLETE;

    @Autowired
    private TaskAssociateRepository taskAssociateRepository;

    @Autowired
    private TaskAssociateMapper taskAssociateMapper;
    
    @Autowired
    private TaskAssociateService taskAssociateService;

    @Autowired
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Autowired
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    @Autowired
    private ExceptionTranslator exceptionTranslator;

    @Autowired
    private EntityManager em;


    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private TaskMapper taskMapper;
    
    @Autowired
    private TaskService taskService;
    
    private MockMvc restGoalMockMvc;

    private MockMvc restTaskAssociateMockMvc;

    private TaskAssociate taskAssociate;
    
    private MockMvc restTaskMockMvc;
    
    private Task task;
    
    @Autowired
    private GoalRepository goalRepository;
    
    @Autowired
    private GoalMapper goalMapper;
    
    @Autowired
    private GoalService goalService;

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
        final TaskAssociateResource taskAssociateResource = new TaskAssociateResource(taskAssociateService);
        this.restTaskAssociateMockMvc = MockMvcBuilders.standaloneSetup(taskAssociateResource)
            .setCustomArgumentResolvers(pageableArgumentResolver)
            .setControllerAdvice(exceptionTranslator)
            .setConversionService(createFormattingConversionService())
            .setMessageConverters(jacksonMessageConverter).build();
        
        final TaskResource taskResource = new TaskResource(taskService);
        this.restTaskMockMvc = MockMvcBuilders.standaloneSetup(taskResource)
            .setCustomArgumentResolvers(pageableArgumentResolver)
            .setControllerAdvice(exceptionTranslator)
            .setConversionService(createFormattingConversionService())
            .setMessageConverters(jacksonMessageConverter).build();
        
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
    
    public static Goal createEntityGoal(EntityManager em) {
        Goal goal = new Goal()
            .goalIdU("AAAA")
            .name("AAAA")
            .description("AAAA")
            .etaValue(1)
            .etaUnit(TimeUnit.DAY)
            .lookup("G1");
        return goal;
    }

    public static Task createEntityTask(EntityManager em) {
        Task task = new Task()
            .taskIdU("T1")
            .name("AAAAA")
            .type(Type.DECISION)
            .assignee("AAAAAA")
            .lookup("AAAA")
            .isRepeat(false);
        
        return task;
    }
    public static Episode createEntityEpisode(EntityManager em) {
        Episode episode = new Episode()
            .episodeIdU("AAAA")
            .name("AAAAAAAAAAAA")
            .description("AAAAAAAAA")
            .lookup("AAAA");
        return episode;
    }
    public static TaskAssociate createEntity(EntityManager em) {
        TaskAssociate taskAssociate = new TaskAssociate()
            .associateEvent(DEFAULT_ASSOCIATE_EVENT)
            .associateOn(10L);
        return taskAssociate;
    }

    @Before
    public void initTest() {
        taskAssociate = createEntity(em);
        task = createEntityTask(em);
        episode = createEntityEpisode(em);
        goal = createEntityGoal(em);
    }

    @Test
    @Transactional
    public void createTaskAssociate() throws Exception {    
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
        TaskDTO taskDTO = taskMapper.toDto(task);
        taskDTO.setGoalId(testGoal.getId());
        restTaskMockMvc.perform(post("/api/definitions/tasks")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(taskDTO)))
            .andExpect(status().isCreated());
       
        int databaseSizeBeforeCreate = taskAssociateRepository.findAll().size();

        // Create the TaskAssociate
        TaskAssociateDTO taskAssociateDTO = taskAssociateMapper.toDto(taskAssociate);
        List<Task> taskList = taskRepository.findAll();
        Task testTask = taskList.get(taskList.size() - 1);
        
        taskAssociateDTO.setTaskId(testTask.getId());
        taskAssociateDTO.setAssociateOn(testTask.getId());
        restTaskAssociateMockMvc.perform(post("/api/definitions/task-associates")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(taskAssociateDTO)))
            .andExpect(status().isCreated());

        // Validate the TaskAssociate in the database
        List<TaskAssociate> taskAssociateList = taskAssociateRepository.findAll();
        assertThat(taskAssociateList).hasSize(databaseSizeBeforeCreate + 1);
        TaskAssociate testTaskAssociate = taskAssociateList.get(taskAssociateList.size() - 1);
        assertThat(testTaskAssociate.getAssociateEvent()).isEqualTo(DEFAULT_ASSOCIATE_EVENT);
    }

    @Test
    @Transactional
    public void createTaskAssociateWithExistingId() throws Exception {
        int databaseSizeBeforeCreate = taskAssociateRepository.findAll().size();

        // Create the TaskAssociate with an existing ID
        taskAssociate.setId(1L);
        TaskAssociateDTO taskAssociateDTO = taskAssociateMapper.toDto(taskAssociate);

        // An entity with an existing ID cannot be created, so this API call must fail
        restTaskAssociateMockMvc.perform(post("/api/definitions/task-associates")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(taskAssociateDTO)))
            .andExpect(status().isBadRequest());

        // Validate the TaskAssociate in the database
        List<TaskAssociate> taskAssociateList = taskAssociateRepository.findAll();
        assertThat(taskAssociateList).hasSize(databaseSizeBeforeCreate);
    }


    @Test
    @Transactional
    public void getAllTaskAssociates() throws Exception {
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
        TaskDTO taskDTO = taskMapper.toDto(task);
        taskDTO.setGoalId(testGoal.getId());
        restTaskMockMvc.perform(post("/api/definitions/tasks")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(taskDTO)))
            .andExpect(status().isCreated());
        
        List<Task> taskList = taskRepository.findAll();
        Task testTask = taskList.get(taskList.size() - 1);
        Task task = new Task();
        task.setId(testTask.getId());
        taskAssociate.setTask(task);
        taskAssociate.setAssociateOn(testTask.getId());

        // Initialize the database
        taskAssociateRepository.saveAndFlush(taskAssociate);

        // Get all the taskAssociateList
        restTaskAssociateMockMvc.perform(get("/api/definitions/task-associates?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(taskAssociate.getId().intValue())))
            .andExpect(jsonPath("$.[*].associateEvent").value(hasItem(DEFAULT_ASSOCIATE_EVENT.toString())))
            .andExpect(jsonPath("$.[*].associateOn").value(hasItem(taskAssociate.getAssociateOn().intValue())));
    }
    
    @Test
    @Transactional
    public void getTaskAssociate() throws Exception {
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
        TaskDTO taskDTO = taskMapper.toDto(task);
        taskDTO.setGoalId(testGoal.getId());
        restTaskMockMvc.perform(post("/api/definitions/tasks")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(taskDTO)))
            .andExpect(status().isCreated());
        
        List<Task> taskList = taskRepository.findAll();
        Task testTask = taskList.get(taskList.size() - 1);
        Task task = new Task();
        task.setId(testTask.getId());
        taskAssociate.setTask(task);
        taskAssociate.setAssociateOn(testTask.getId());

        // Initialize the database
        taskAssociateRepository.saveAndFlush(taskAssociate);

        // Get the taskAssociate
        restTaskAssociateMockMvc.perform(get("/api/definitions/task-associates/{id}", taskAssociate.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.id").value(taskAssociate.getId().intValue()))
            .andExpect(jsonPath("$.associateEvent").value(DEFAULT_ASSOCIATE_EVENT.toString()))
            .andExpect(jsonPath("$.associateOn").value(taskAssociate.getAssociateOn().intValue()));
    }

    @Test
    @Transactional
    public void getNonExistingTaskAssociate() throws Exception {
        // Get the taskAssociate
        restTaskAssociateMockMvc.perform(get("/api/definitions/task-associates/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateTaskAssociate() throws Exception {
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
        TaskDTO taskDTO = taskMapper.toDto(task);
        taskDTO.setGoalId(testGoal.getId());
        restTaskMockMvc.perform(post("/api/definitions/tasks")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(taskDTO)))
            .andExpect(status().isCreated());
        
        List<Task> taskList = taskRepository.findAll();
        Task testTask = taskList.get(taskList.size() - 1);
        Task task = new Task();
        task.setId(testTask.getId());
        taskAssociate.setTask(task);
        taskAssociate.setAssociateOn(testTask.getId());

        // Initialize the database
        taskAssociateRepository.saveAndFlush(taskAssociate);

        int databaseSizeBeforeUpdate = taskAssociateRepository.findAll().size();

        // Update the taskAssociate
        TaskAssociate updatedTaskAssociate = taskAssociateRepository.findById(taskAssociate.getId()).get();
        // Disconnect from session so that the updates on updatedTaskAssociate are not directly saved in db
        em.detach(updatedTaskAssociate);
        updatedTaskAssociate
            .associateEvent(UPDATED_ASSOCIATE_EVENT)
            .associateOn(testTask.getId());
        TaskAssociateDTO taskAssociateDTO = taskAssociateMapper.toDto(updatedTaskAssociate);

        restTaskAssociateMockMvc.perform(put("/api/definitions/task-associates")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(taskAssociateDTO)))
            .andExpect(status().isOk());

        // Validate the TaskAssociate in the database
        List<TaskAssociate> taskAssociateList = taskAssociateRepository.findAll();
        assertThat(taskAssociateList).hasSize(databaseSizeBeforeUpdate);
        TaskAssociate testTaskAssociate = taskAssociateList.get(taskAssociateList.size() - 1);
        assertThat(testTaskAssociate.getAssociateEvent()).isEqualTo(UPDATED_ASSOCIATE_EVENT);
    }

    @Test
    @Transactional
    public void updateNonExistingTaskAssociate() throws Exception {
        int databaseSizeBeforeUpdate = taskAssociateRepository.findAll().size();

        // Create the TaskAssociate
        TaskAssociateDTO taskAssociateDTO = taskAssociateMapper.toDto(taskAssociate);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restTaskAssociateMockMvc.perform(put("/api/definitions/task-associates")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(taskAssociateDTO)))
            .andExpect(status().isBadRequest());

        // Validate the TaskAssociate in the database
        List<TaskAssociate> taskAssociateList = taskAssociateRepository.findAll();
        assertThat(taskAssociateList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    public void deleteTaskAssociate() throws Exception {
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
        TaskDTO taskDTO = taskMapper.toDto(task);
        taskDTO.setGoalId(testGoal.getId());
        restTaskMockMvc.perform(post("/api/definitions/tasks")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(taskDTO)))
            .andExpect(status().isCreated());
        
        List<Task> taskList = taskRepository.findAll();
        Task testTask = taskList.get(taskList.size() - 1);
        Task task = new Task();
        task.setId(testTask.getId());
        taskAssociate.setTask(task);
        taskAssociate.setAssociateOn(testTask.getId());

        // Initialize the database
        taskAssociateRepository.saveAndFlush(taskAssociate);

        int databaseSizeBeforeDelete = taskAssociateRepository.findAll().size();

        // Get the taskAssociate
        restTaskAssociateMockMvc.perform(delete("/api/definitions/task-associates/{id}", taskAssociate.getId())
            .accept(TestUtil.APPLICATION_JSON_UTF8))
            .andExpect(status().isOk());

        // Validate the database is empty
        List<TaskAssociate> taskAssociateList = taskAssociateRepository.findAll();
        assertThat(taskAssociateList).hasSize(databaseSizeBeforeDelete - 1);
    }

    @Test
    @Transactional
    public void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(TaskAssociate.class);
        TaskAssociate taskAssociate1 = new TaskAssociate();
        taskAssociate1.setId(1L);
        TaskAssociate taskAssociate2 = new TaskAssociate();
        taskAssociate2.setId(taskAssociate1.getId());
        assertThat(taskAssociate1).isEqualTo(taskAssociate2);
        taskAssociate2.setId(2L);
        assertThat(taskAssociate1).isNotEqualTo(taskAssociate2);
        taskAssociate1.setId(null);
        assertThat(taskAssociate1).isNotEqualTo(taskAssociate2);
    }

    @Test
    @Transactional
    public void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(TaskAssociateDTO.class);
        TaskAssociateDTO taskAssociateDTO1 = new TaskAssociateDTO();
        taskAssociateDTO1.setId(1L);
        TaskAssociateDTO taskAssociateDTO2 = new TaskAssociateDTO();
        assertThat(taskAssociateDTO1).isNotEqualTo(taskAssociateDTO2);
        taskAssociateDTO2.setId(taskAssociateDTO1.getId());
        assertThat(taskAssociateDTO1).isEqualTo(taskAssociateDTO2);
        taskAssociateDTO2.setId(2L);
        assertThat(taskAssociateDTO1).isNotEqualTo(taskAssociateDTO2);
        taskAssociateDTO1.setId(null);
        assertThat(taskAssociateDTO1).isNotEqualTo(taskAssociateDTO2);
    }

    @Test
    @Transactional
    public void testEntityFromId() {
        assertThat(taskAssociateMapper.fromId(42L).getId()).isEqualTo(42);
        assertThat(taskAssociateMapper.fromId(null)).isNull();
    }
}
