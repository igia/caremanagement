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
import io.igia.caremanagement.domain.Task;
import io.igia.caremanagement.domain.enumeration.CaseExecutionEvent;
import io.igia.caremanagement.domain.enumeration.TimeUnit;
import io.igia.caremanagement.domain.enumeration.Type;
import io.igia.caremanagement.repository.EpisodeRepository;
import io.igia.caremanagement.repository.GoalRepository;
import io.igia.caremanagement.repository.TaskRepository;
import io.igia.caremanagement.service.EpisodeService;
import io.igia.caremanagement.service.GoalService;
import io.igia.caremanagement.service.TaskService;
import io.igia.caremanagement.service.dto.EpisodeDTO;
import io.igia.caremanagement.service.dto.GoalDTO;
import io.igia.caremanagement.service.dto.TaskDTO;
import io.igia.caremanagement.service.mapper.EpisodeMapper;
import io.igia.caremanagement.service.mapper.GoalMapper;
import io.igia.caremanagement.service.mapper.TaskMapper;
import io.igia.caremanagement.web.rest.EpisodeResource;
import io.igia.caremanagement.web.rest.GoalResource;
import io.igia.caremanagement.web.rest.TaskResource;
import io.igia.caremanagement.web.rest.errors.ExceptionTranslator;

import javax.persistence.EntityManager;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;

import static io.igia.caremanagement.web.rest.TestUtil.createFormattingConversionService;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
/**
 * Test class for the TaskResource REST controller.
 *
 * @see TaskResource
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = CaremanagementApp.class)
public class TaskResourceIntTest {

    private static final String DEFAULT_TASK_ID_U = "AAAAAAAAAA";
    private static final String UPDATED_TASK_ID_U = "BBBBBBBBBB";

    private static final String DEFAULT_NAME = "AAAAAAAAAA";
    private static final String UPDATED_NAME = "BBBBBBBBBB";

    private static final String DEFAULT_DESCRIPTION = "AAAAAAAAAA";
    private static final String UPDATED_DESCRIPTION = "BBBBBBBBBB";

    private static final Type DEFAULT_TYPE = Type.DECISION;
    private static final Type UPDATED_TYPE = Type.HUMAN;

    private static final String DEFAULT_TYPE_REF = "AAAAAAAAAA";
    private static final String UPDATED_TYPE_REF = "BBBBBBBBBB";

    private static final LocalDate DEFAULT_DUE_DATE = LocalDate.ofEpochDay(0L);
    private static final LocalDate UPDATED_DUE_DATE = LocalDate.now(ZoneId.systemDefault());

    private static final String DEFAULT_ASSIGNEE = "AAAAAAAAAA";
    private static final String UPDATED_ASSIGNEE = "BBBBBBBBBB";

    private static final String DEFAULT_ENTRY_CRITERIA = "AAAAAAAAAA";
    private static final String UPDATED_ENTRY_CRITERIA = "BBBBBBBBBB";

    private static final Integer DEFAULT_REPEAT_FREQUENCY_VALUE = 1;
    private static final Integer UPDATED_REPEAT_FREQUENCY_VALUE = 2;

    private static final TimeUnit DEFAULT_REPEAT_FREQUENCY_UNIT = TimeUnit.DAY;
    private static final TimeUnit UPDATED_REPEAT_FREQUENCY_UNIT = TimeUnit.DAYS;

    private static final CaseExecutionEvent DEFAULT_REPEAT_EVENT = CaseExecutionEvent.CREATE;
    private static final CaseExecutionEvent UPDATED_REPEAT_EVENT = CaseExecutionEvent.COMPLETE;

    private static final String DEFAULT_LOOKUP = "AAAAAAAAAA";
    private static final String UPDATED_LOOKUP = "BBBBBBBBBB";

    private static final Integer DEFAULT_SLA = 1;
    private static final Integer UPDATED_SLA = 2;

    private static final Boolean DEFAULT_IS_REPEAT = false;
    private static final Boolean UPDATED_IS_REPEAT = true;

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private TaskMapper taskMapper;
    
    @Autowired
    private TaskService taskService;

    @Autowired
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Autowired
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    @Autowired
    private ExceptionTranslator exceptionTranslator;

    @Autowired
    private EntityManager em;

    private MockMvc restTaskMockMvc;

    private Task task;
    
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

    @Autowired
    private EpisodeRepository episodeRepository;


    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
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
    
    public static Episode createEntityEpisode(EntityManager em) {
        Episode episode = new Episode()
            .episodeIdU("AAAA")
            .name("AAAAAAAAAAAA")
            .description("AAAAAAAAA")
            .lookup("AAAA");
        return episode;
    }

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
    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Task createEntity(EntityManager em) {
        Task task = new Task()
            .taskIdU(DEFAULT_TASK_ID_U)
            .name(DEFAULT_NAME)
            .description(DEFAULT_DESCRIPTION)
            .type(DEFAULT_TYPE)
            .typeRef(DEFAULT_TYPE_REF)
            .dueDate(DEFAULT_DUE_DATE)
            .assignee(DEFAULT_ASSIGNEE)
            .entryCriteria(DEFAULT_ENTRY_CRITERIA)
            .repeatFrequencyValue(DEFAULT_REPEAT_FREQUENCY_VALUE)
            .repeatFrequencyUnit(DEFAULT_REPEAT_FREQUENCY_UNIT)
            .repeatEvent(DEFAULT_REPEAT_EVENT)
            .lookup(DEFAULT_LOOKUP)
            .sla(DEFAULT_SLA)
            .isRepeat(DEFAULT_IS_REPEAT);
        return task;
    }

    @Before
    public void initTest() {
        task = createEntity(em);
        goal = createEntityGoal(em);
        episode = createEntityEpisode(em);
    }

    @Test
    @Transactional
    public void createTask() throws Exception {
        EpisodeDTO episodeDTO = episodeMapper.toDto(episode);
        restEpisodeMockMvc.perform(post("/api/definitions/episodes")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(episodeDTO)))
            .andExpect(status().isCreated());
        List<Episode> episodeList = episodeRepository.findAll();
        Episode testEpisode = episodeList.get(episodeList.size() - 1);
        int databaseSizeBeforeCreate = taskRepository.findAll().size();

        // Create the Task
        TaskDTO taskDTO = taskMapper.toDto(task);
        
        GoalDTO goalDTO = goalMapper.toDto(goal);
        goalDTO.setEpisodeId(testEpisode.getId());
        restGoalMockMvc.perform(post("/api/definitions/goals")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(goalDTO)))
            .andExpect(status().isCreated());
        
        List<Goal> goalList = goalRepository.findAll();
        Goal testGoal = goalList.get(goalList.size() - 1);
        taskDTO.setGoalId(testGoal.getId());
        restTaskMockMvc.perform(post("/api/definitions/tasks")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(taskDTO)))
            .andExpect(status().isCreated());

        // Validate the Task in the database
        List<Task> taskList = taskRepository.findAll();
        assertThat(taskList).hasSize(databaseSizeBeforeCreate + 1);
        Task testTask = taskList.get(taskList.size() - 1);
        assertThat(testTask.getTaskIdU()).isEqualTo(DEFAULT_TASK_ID_U);
        assertThat(testTask.getName()).isEqualTo(DEFAULT_NAME);
        assertThat(testTask.getDescription()).isEqualTo(DEFAULT_DESCRIPTION);
        assertThat(testTask.getType()).isEqualTo(DEFAULT_TYPE);
        assertThat(testTask.getTypeRef()).isEqualTo(DEFAULT_TYPE_REF);
        assertThat(testTask.getDueDate()).isEqualTo(DEFAULT_DUE_DATE);
        assertThat(testTask.getAssignee()).isEqualTo(DEFAULT_ASSIGNEE);
        assertThat(testTask.getEntryCriteria()).isEqualTo(DEFAULT_ENTRY_CRITERIA);
        assertThat(testTask.getRepeatFrequencyValue()).isEqualTo(DEFAULT_REPEAT_FREQUENCY_VALUE);
        assertThat(testTask.getRepeatFrequencyUnit()).isEqualTo(DEFAULT_REPEAT_FREQUENCY_UNIT);
        assertThat(testTask.getRepeatEvent()).isEqualTo(DEFAULT_REPEAT_EVENT);
        assertThat(testTask.getLookup()).isEqualTo(DEFAULT_LOOKUP);
        assertThat(testTask.getSla()).isEqualTo(DEFAULT_SLA);
        assertThat(testTask.isIsRepeat()).isEqualTo(DEFAULT_IS_REPEAT);
    }

    @Test
    @Transactional
    public void createTaskWithExistingId() throws Exception {
        int databaseSizeBeforeCreate = taskRepository.findAll().size();

        // Create the Task with an existing ID
        task.setId(1L);
        TaskDTO taskDTO = taskMapper.toDto(task);

        // An entity with an existing ID cannot be created, so this API call must fail
        restTaskMockMvc.perform(post("/api/definitions/tasks")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(taskDTO)))
            .andExpect(status().isBadRequest());

        // Validate the Task in the database
        List<Task> taskList = taskRepository.findAll();
        assertThat(taskList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    public void checkTaskIdUIsRequired() throws Exception {
        int databaseSizeBeforeTest = taskRepository.findAll().size();
        // set the field null
        task.setTaskIdU(null);

        // Create the Task, which fails.
        TaskDTO taskDTO = taskMapper.toDto(task);

        restTaskMockMvc.perform(post("/api/definitions/tasks")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(taskDTO)))
            .andExpect(status().isBadRequest());

        List<Task> taskList = taskRepository.findAll();
        assertThat(taskList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void checkNameIsRequired() throws Exception {
        int databaseSizeBeforeTest = taskRepository.findAll().size();
        // set the field null
        task.setName(null);

        // Create the Task, which fails.
        TaskDTO taskDTO = taskMapper.toDto(task);

        restTaskMockMvc.perform(post("/api/definitions/tasks")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(taskDTO)))
            .andExpect(status().isBadRequest());

        List<Task> taskList = taskRepository.findAll();
        assertThat(taskList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void checkTypeIsRequired() throws Exception {
        int databaseSizeBeforeTest = taskRepository.findAll().size();
        // set the field null
        task.setType(null);

        // Create the Task, which fails.
        TaskDTO taskDTO = taskMapper.toDto(task);

        restTaskMockMvc.perform(post("/api/definitions/tasks")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(taskDTO)))
            .andExpect(status().isBadRequest());

        List<Task> taskList = taskRepository.findAll();
        assertThat(taskList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void checkAssigneeIsRequired() throws Exception {
        int databaseSizeBeforeTest = taskRepository.findAll().size();
        // set the field null
        task.setAssignee(null);

        // Create the Task, which fails.
        TaskDTO taskDTO = taskMapper.toDto(task);

        restTaskMockMvc.perform(post("/api/definitions/tasks")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(taskDTO)))
            .andExpect(status().isBadRequest());

        List<Task> taskList = taskRepository.findAll();
        assertThat(taskList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void checkLookupIsRequired() throws Exception {
        int databaseSizeBeforeTest = taskRepository.findAll().size();
        // set the field null
        task.setLookup(null);

        // Create the Task, which fails.
        TaskDTO taskDTO = taskMapper.toDto(task);

        restTaskMockMvc.perform(post("/api/definitions/tasks")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(taskDTO)))
            .andExpect(status().isBadRequest());

        List<Task> taskList = taskRepository.findAll();
        assertThat(taskList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void checkIsRepeatIsRequired() throws Exception {
        int databaseSizeBeforeTest = taskRepository.findAll().size();
        // set the field null
        task.setIsRepeat(null);

        // Create the Task, which fails.
        TaskDTO taskDTO = taskMapper.toDto(task);

        restTaskMockMvc.perform(post("/api/definitions/tasks")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(taskDTO)))
            .andExpect(status().isBadRequest());

        List<Task> taskList = taskRepository.findAll();
        assertThat(taskList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void getAllTasks() throws Exception {
        // Initialize the database
        taskRepository.saveAndFlush(task);

        // Get all the taskList
        restTaskMockMvc.perform(get("/api/definitions/tasks?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(task.getId().intValue())))
            .andExpect(jsonPath("$.[*].taskIdU").value(hasItem(DEFAULT_TASK_ID_U.toString())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME.toString())))
            .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION.toString())))
            .andExpect(jsonPath("$.[*].type").value(hasItem(DEFAULT_TYPE.toString())))
            .andExpect(jsonPath("$.[*].typeRef").value(hasItem(DEFAULT_TYPE_REF.toString())))
            .andExpect(jsonPath("$.[*].dueDate").value(hasItem(DEFAULT_DUE_DATE.toString())))
            .andExpect(jsonPath("$.[*].assignee").value(hasItem(DEFAULT_ASSIGNEE.toString())))
            .andExpect(jsonPath("$.[*].entryCriteria").value(hasItem(DEFAULT_ENTRY_CRITERIA.toString())))
            .andExpect(jsonPath("$.[*].repeatFrequencyValue").value(hasItem(DEFAULT_REPEAT_FREQUENCY_VALUE)))
            .andExpect(jsonPath("$.[*].repeatFrequencyUnit").value(hasItem(DEFAULT_REPEAT_FREQUENCY_UNIT.toString())))
            .andExpect(jsonPath("$.[*].repeatEvent").value(hasItem(DEFAULT_REPEAT_EVENT.toString())))
            .andExpect(jsonPath("$.[*].lookup").value(hasItem(DEFAULT_LOOKUP.toString())))
            .andExpect(jsonPath("$.[*].sla").value(hasItem(DEFAULT_SLA)))
            .andExpect(jsonPath("$.[*].isRepeat").value(hasItem(DEFAULT_IS_REPEAT.booleanValue())));
    }
    
    @Test
    @Transactional
    public void getTask() throws Exception {
        // Initialize the database
        taskRepository.saveAndFlush(task);

        // Get the task
        restTaskMockMvc.perform(get("/api/definitions/tasks/{id}", task.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.id").value(task.getId().intValue()))
            .andExpect(jsonPath("$.taskIdU").value(DEFAULT_TASK_ID_U.toString()))
            .andExpect(jsonPath("$.name").value(DEFAULT_NAME.toString()))
            .andExpect(jsonPath("$.description").value(DEFAULT_DESCRIPTION.toString()))
            .andExpect(jsonPath("$.type").value(DEFAULT_TYPE.toString()))
            .andExpect(jsonPath("$.typeRef").value(DEFAULT_TYPE_REF.toString()))
            .andExpect(jsonPath("$.dueDate").value(DEFAULT_DUE_DATE.toString()))
            .andExpect(jsonPath("$.assignee").value(DEFAULT_ASSIGNEE.toString()))
            .andExpect(jsonPath("$.entryCriteria").value(DEFAULT_ENTRY_CRITERIA.toString()))
            .andExpect(jsonPath("$.repeatFrequencyValue").value(DEFAULT_REPEAT_FREQUENCY_VALUE))
            .andExpect(jsonPath("$.repeatFrequencyUnit").value(DEFAULT_REPEAT_FREQUENCY_UNIT.toString()))
            .andExpect(jsonPath("$.repeatEvent").value(DEFAULT_REPEAT_EVENT.toString()))
            .andExpect(jsonPath("$.lookup").value(DEFAULT_LOOKUP.toString()))
            .andExpect(jsonPath("$.sla").value(DEFAULT_SLA))
            .andExpect(jsonPath("$.isRepeat").value(DEFAULT_IS_REPEAT.booleanValue()));
    }

    @Test
    @Transactional
    public void getNonExistingTask() throws Exception {
        // Get the task
        restTaskMockMvc.perform(get("/api/definitions/tasks/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateTask() throws Exception {
        EpisodeDTO episodeDTO = episodeMapper.toDto(episode);
        restEpisodeMockMvc.perform(post("/api/definitions/episodes")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(episodeDTO)))
            .andExpect(status().isCreated());
        List<Episode> episodeList = episodeRepository.findAll();
        Episode testEpisode = episodeList.get(episodeList.size() - 1);
        // Initialize the database
        GoalDTO goalDTO = goalMapper.toDto(goal);
        goalDTO.setEpisodeId(testEpisode.getId());
        restGoalMockMvc.perform(post("/api/definitions/goals")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(goalDTO)))
            .andExpect(status().isCreated());
        
        List<Goal> goalList = goalRepository.findAll();
        Goal testGoal = goalList.get(goalList.size() - 1);
        Goal goal = new Goal();
        goal.setId(testGoal.getId());
        task.setGoal(goal);
        taskRepository.saveAndFlush(task);

        int databaseSizeBeforeUpdate = taskRepository.findAll().size();

        // Update the task
        Task updatedTask = taskRepository.findById(task.getId()).get();
        // Disconnect from session so that the updates on updatedTask are not directly saved in db
        em.detach(updatedTask);
        updatedTask
            .taskIdU(UPDATED_TASK_ID_U)
            .name(UPDATED_NAME)
            .description(UPDATED_DESCRIPTION)
            .type(UPDATED_TYPE)
            .typeRef(UPDATED_TYPE_REF)
            .dueDate(UPDATED_DUE_DATE)
            .assignee(UPDATED_ASSIGNEE)
            .entryCriteria(UPDATED_ENTRY_CRITERIA)
            .repeatFrequencyValue(UPDATED_REPEAT_FREQUENCY_VALUE)
            .repeatFrequencyUnit(UPDATED_REPEAT_FREQUENCY_UNIT)
            .repeatEvent(UPDATED_REPEAT_EVENT)
            .lookup(UPDATED_LOOKUP)
            .sla(UPDATED_SLA)
            .isRepeat(UPDATED_IS_REPEAT);
        TaskDTO taskDTO = taskMapper.toDto(updatedTask);

        restTaskMockMvc.perform(put("/api/definitions/tasks")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(taskDTO)))
            .andExpect(status().isOk());

        // Validate the Task in the database
        List<Task> taskList = taskRepository.findAll();
        assertThat(taskList).hasSize(databaseSizeBeforeUpdate);
        Task testTask = taskList.get(taskList.size() - 1);
        assertThat(testTask.getTaskIdU()).isEqualTo(UPDATED_TASK_ID_U);
        assertThat(testTask.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testTask.getDescription()).isEqualTo(UPDATED_DESCRIPTION);
        assertThat(testTask.getType()).isEqualTo(UPDATED_TYPE);
        assertThat(testTask.getTypeRef()).isEqualTo(UPDATED_TYPE_REF);
        assertThat(testTask.getDueDate()).isEqualTo(UPDATED_DUE_DATE);
        assertThat(testTask.getAssignee()).isEqualTo(UPDATED_ASSIGNEE);
        assertThat(testTask.getEntryCriteria()).isEqualTo(UPDATED_ENTRY_CRITERIA);
        assertThat(testTask.getRepeatFrequencyValue()).isEqualTo(UPDATED_REPEAT_FREQUENCY_VALUE);
        assertThat(testTask.getRepeatFrequencyUnit()).isEqualTo(UPDATED_REPEAT_FREQUENCY_UNIT);
        assertThat(testTask.getRepeatEvent()).isEqualTo(UPDATED_REPEAT_EVENT);
        assertThat(testTask.getLookup()).isEqualTo(UPDATED_LOOKUP);
        assertThat(testTask.getSla()).isEqualTo(UPDATED_SLA);
        assertThat(testTask.isIsRepeat()).isEqualTo(UPDATED_IS_REPEAT);
    }

    @Test
    @Transactional
    public void updateNonExistingTask() throws Exception {
        int databaseSizeBeforeUpdate = taskRepository.findAll().size();

        // Create the Task
        TaskDTO taskDTO = taskMapper.toDto(task);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restTaskMockMvc.perform(put("/api/definitions/tasks")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(taskDTO)))
            .andExpect(status().isBadRequest());

        // Validate the Task in the database
        List<Task> taskList = taskRepository.findAll();
        assertThat(taskList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    public void deleteTask() throws Exception {
        // Initialize the database
        taskRepository.saveAndFlush(task);

        int databaseSizeBeforeDelete = taskRepository.findAll().size();

        // Get the task
        restTaskMockMvc.perform(delete("/api/definitions/tasks/{id}", task.getId())
            .accept(TestUtil.APPLICATION_JSON_UTF8))
            .andExpect(status().isOk());

        // Validate the database is empty
        List<Task> taskList = taskRepository.findAll();
        assertThat(taskList).hasSize(databaseSizeBeforeDelete - 1);
    }

    @Test
    @Transactional
    public void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Task.class);
        Task task1 = new Task();
        task1.setId(1L);
        Task task2 = new Task();
        task2.setId(task1.getId());
        assertThat(task1).isEqualTo(task2);
        task2.setId(2L);
        assertThat(task1).isNotEqualTo(task2);
        task1.setId(null);
        assertThat(task1).isNotEqualTo(task2);
    }

    @Test
    @Transactional
    public void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(TaskDTO.class);
        TaskDTO taskDTO1 = new TaskDTO();
        taskDTO1.setId(1L);
        TaskDTO taskDTO2 = new TaskDTO();
        assertThat(taskDTO1).isNotEqualTo(taskDTO2);
        taskDTO2.setId(taskDTO1.getId());
        assertThat(taskDTO1).isEqualTo(taskDTO2);
        taskDTO2.setId(2L);
        assertThat(taskDTO1).isNotEqualTo(taskDTO2);
        taskDTO1.setId(null);
        assertThat(taskDTO1).isNotEqualTo(taskDTO2);
    }

    @Test
    @Transactional
    public void testEntityFromId() {
        assertThat(taskMapper.fromId(42L).getId()).isEqualTo(42);
        assertThat(taskMapper.fromId(null)).isNull();
    }
}
