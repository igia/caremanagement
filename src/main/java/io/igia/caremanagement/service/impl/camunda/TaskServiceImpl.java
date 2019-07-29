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
package io.igia.caremanagement.service.impl.camunda;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.json.simple.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import io.igia.caremanagement.client.camunda.CamundaClient;
import io.igia.caremanagement.config.Constants;
import io.igia.caremanagement.domain.Task;
import io.igia.caremanagement.domain.enumeration.TaskCategory;
import io.igia.caremanagement.repository.TaskRepository;
import io.igia.caremanagement.security.SecurityUtils;
import io.igia.caremanagement.service.camunda.TaskService;
import io.igia.caremanagement.service.dto.camunda.CaseExecution;
import io.igia.caremanagement.service.dto.camunda.CaseInstance;
import io.igia.caremanagement.service.dto.camunda.TaskRequest;
import io.igia.caremanagement.service.dto.camunda.TaskResponse;
import io.igia.caremanagement.service.validation.TaskValidation;
import io.igia.caremanagement.web.rest.errors.CustomParameterizedException;

@Service("camundaTaskService")
@Transactional
public class TaskServiceImpl implements TaskService {
    private final Logger log = LoggerFactory.getLogger(TaskServiceImpl.class);

    private static final String CASE_INSTANCE_BUSINESS_KEY = "caseInstanceBusinessKey";
    private static final String CASE_DEFINITION_KEY = "caseDefinitionKey";
    private static final String CASE_INSTANCE_ID = "caseInstanceId";
    private static final String CASE = "Case";
    private static final String UNDERSCORE = "_";
    private static final String CASE_UNDERSCORE = CASE + UNDERSCORE;
    private static final String LC = "LC";
    private static final String LC_UNDERSCORE = LC + UNDERSCORE;
    private static final String ACTIVATE = "ACTIVATE";
    private static final String UNDERSCORE_ACTIVATE = UNDERSCORE + ACTIVATE;
    private static final String TASK_ASSIGNEE = "taskAssignee";
    private static final String FINISHED = "finished";
    private static final String TASK_DELETE_REASON = "taskDeleteReason";
    private static final String COMPLETED = "completed";
    private static final String OWNER = "owner";
    private static final String NAME = "name";
    private static final String DESCRIPTION = "description";
    private static final String ASSIGNEE = "assignee";
    private static final String DUE = "due";
    private static final String PARENT_TASK_ID = "parentTaskId";
    private static final String HUMAN_TASK = "humanTask";
    private static final String DUE_AFTER = "dueAfter";
    private static final String DUE_BEFORE = "dueBefore";
    private static final String BY_FILTERS_MRN_PROGRAM_ID_CASE_INSTANCE_ID = "by filters mrn {} programId {} caseInstanceId {}";

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private CamundaClient camundaClient;
    
    @Autowired
    private TaskValidation taskValidation;

	@Override
	public void completeTask(String id) {
		log.info("complete task : {}", id);
		camundaClient.completeTask(id);	
	}

	/*
	 * get tasks by provided filters
	 */
    @Override
    public List<TaskResponse> getTasks(TaskRequest taskRequest) {
        List<TaskResponse> taskResponse = null;
        if (taskRequest.getTaskCategory() == TaskCategory.AVAILABLE) {
            taskResponse = getAvailableTasks(taskRequest.getMrn(), taskRequest.getProgramId(), taskRequest.getCaseInstanceId());
        } else if (taskRequest.getTaskCategory() == TaskCategory.HISTORY) {
            taskResponse = getHistoryTasks(taskRequest);
        } else if (taskRequest.getTaskCategory() == TaskCategory.TODO || taskRequest.getTaskCategory() == TaskCategory.MISSED || taskRequest.getTaskCategory() == TaskCategory.UPCOMING) {
            taskResponse = getTodoMissedUpcoming(taskRequest);
        } else {
        	throw new CustomParameterizedException("Invalid TaskCategory {}", "taskCategory");  	
        }
        return taskResponse;
    }

    /*
     * get available tasks that can be moved to active state to be able to work on it
     */
    @Override
    public List<TaskResponse> getAvailableTasks(String mrn, String programId, String caseInstanceId) {
        log.info("get available tasks {}, {}, {}, {}", BY_FILTERS_MRN_PROGRAM_ID_CASE_INSTANCE_ID,  mrn, programId, caseInstanceId);
        List<CaseExecution> caseExecutionList = getCaseExecutions(mrn, programId, caseInstanceId);
        List<TaskResponse> taskList = new ArrayList<>();
        if (caseExecutionList != null && !caseExecutionList.isEmpty()) {
            for (CaseExecution caseExecution : caseExecutionList) {
                TaskResponse taskResponse = new TaskResponse();
                String activityId = caseExecution.getActivityId();
                String taskId = activityId.substring(activityId.indexOf(UNDERSCORE) + 1, activityId.length());
                Task task = taskRepository.findOneByLookup(taskId);
                if ((null != task)
                        && ((null == task.getEntryCriteria()) || task.getEntryCriteria().isEmpty())
                        && (caseExecution.getActivityType().equalsIgnoreCase(HUMAN_TASK)) && !caseExecution.getActive()
                        && !caseExecution.getDisabled() && !caseExecution.getEnabled()
                        && !caseExecution.getRequired()) {
                    taskResponse.setId(taskId);
                    taskResponse.setCaseInstanceId(caseExecution.getCaseInstanceId());
                    taskResponse.setDescription(caseExecution.getActivityDescription());
                    taskResponse.setName(caseExecution.getActivityName());
                    taskResponse.setActivityId(caseExecution.getActivityId());
                    taskResponse.setGoalId(caseExecution.getParentId());
                    taskList.add(taskResponse);
                }
            }
            
            setAvailableTasksGoalAndEpisode(taskList, caseExecutionList);
        }
        return taskList;
    }

    private void setAvailableTasksGoalAndEpisode(List<TaskResponse> taskList, List<CaseExecution> caseExecutionList) {
        for (TaskResponse taskReponse : taskList) {
            for (CaseExecution caseExecution : caseExecutionList) {
                if (caseExecution.getId().equals(taskReponse.getGoalId())) {
                    taskReponse.setGoalName(caseExecution.getActivityName());
                    taskReponse.setEpisodeId(caseExecution.getParentId());
                    break;
                }
            }
            for (CaseExecution caseExecution : caseExecutionList) {
                if (caseExecution.getId().equals(taskReponse.getEpisodeId())) {
                    taskReponse.setEpisodeName(caseExecution.getActivityName());
                    break;
                }
            }
        }
    }

    private List<CaseExecution> getCaseExecutions(String mrn, String programId, String caseInstanceId) {
        String caseDefinitionKey = null;
        programId = getTrimmedValue(programId);
        if (programId != null)
            caseDefinitionKey = CASE_UNDERSCORE + programId;
        caseInstanceId = getTrimmedValue(caseInstanceId);
        List<CaseExecution> caseExecutionList = null;
        if (caseDefinitionKey != null && caseInstanceId != null)
            caseExecutionList = camundaClient.getCaseExecutions(mrn, caseDefinitionKey, caseInstanceId);
        else if (programId != null)
            caseExecutionList = camundaClient.getCaseExecutions(mrn, caseDefinitionKey);
        else if (caseInstanceId != null)
            caseExecutionList = camundaClient.getCaseExecutionsByCaseInstance(mrn, caseInstanceId);
        else
            caseExecutionList = camundaClient.getCaseExecutions(mrn);
        return caseExecutionList;
    }

    /*
     * get active tasks filtered by date of the requested category
     */
    @Override
    public List<TaskResponse> getTodoMissedUpcoming(TaskRequest taskRequest) {
        log.info("get active tasks filtered by date of the task category {}", taskRequest.getTaskCategory());
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS[Z]");
        Map<String, Object> map = new HashMap<>();
        String assignee = SecurityUtils.getCurrentUserLogin().orElse(Constants.SYSTEM_ACCOUNT)
                .toLowerCase(Locale.ENGLISH);
        map.put(ASSIGNEE, assignee);
        if (taskRequest.getTaskCategory() == TaskCategory.TODO) {
            log.info("get todo task for {}", assignee);
            OffsetDateTime dueAfter = OffsetDateTime.of(LocalDate.now().minusDays(1),
                    LocalTime.of(23, 59, 59, 999000000), OffsetDateTime.now().getOffset());
            OffsetDateTime dueBefore = OffsetDateTime.of(LocalDate.now().plusDays(1),
                    LocalTime.of(00, 00, 00, 000000000), OffsetDateTime.now().getOffset());
            map.put(DUE_AFTER, dateTimeFormatter.format(dueAfter));
            map.put(DUE_BEFORE, dateTimeFormatter.format(dueBefore));
        } else if (taskRequest.getTaskCategory() == TaskCategory.MISSED) {
            log.info("get missed task for {}", assignee);
            OffsetDateTime dueBefore = OffsetDateTime.of(LocalDate.now(), LocalTime.of(00, 00, 00, 000000000),
                    OffsetDateTime.now().getOffset());
            map.put(DUE_BEFORE, dateTimeFormatter.format(dueBefore));
        } else if (taskRequest.getTaskCategory() == TaskCategory.UPCOMING) {
            log.info("get upcoming task for {}", assignee);
            OffsetDateTime dueAfter = OffsetDateTime.of(LocalDate.now(), LocalTime.of(23, 59, 59, 999000000),
                    OffsetDateTime.now().getOffset());
            map.put(DUE_AFTER, dateTimeFormatter.format(dueAfter));
        }

        // add mrn filter to get tasks by caseInstanceBusinessKey
        String mrn = getTrimmedValue(taskRequest.getMrn());
        if (mrn != null)
            map.put(CASE_INSTANCE_BUSINESS_KEY, mrn);
        
        // add programId filter to get tasks by caseDefinitionKey
        String programId = getTrimmedValue(taskRequest.getProgramId());
        if (programId != null)
        {
            String caseDefinitionKey = CASE_UNDERSCORE + programId;
            map.put(CASE_DEFINITION_KEY, caseDefinitionKey);
        }

        // add caseInstanceId filter to get tasks by caseInstanceId
        String caseInstanceId = getTrimmedValue(taskRequest.getCaseInstanceId());
        if (caseInstanceId != null)
        {
            map.put(CASE_INSTANCE_ID, caseInstanceId);
        }

        log.info("get active tasks {}, {}, {}, {}", BY_FILTERS_MRN_PROGRAM_ID_CASE_INSTANCE_ID,  mrn, programId, caseInstanceId);

        List<TaskResponse> taskList = null;

        if (null == taskRequest.getFirstResult()) {
            taskRequest.setFirstResult(0);
        }
        if (null != taskRequest.getMaxResults()) {
            taskList = camundaClient.getTasks(new JSONObject(map), taskRequest.getFirstResult(),
                    taskRequest.getMaxResults());
        } else {
            taskList = camundaClient.getTasks(new JSONObject(map), taskRequest.getFirstResult());
        }

        return taskList;
    }

    /*
     * get completed tasks from the history
     */
    public List<TaskResponse> getHistoryTasks(TaskRequest taskRequest) {
        log.info("get completed tasks {}, {}, {}, {}",  BY_FILTERS_MRN_PROGRAM_ID_CASE_INSTANCE_ID, taskRequest.getMrn(), taskRequest.getProgramId(), taskRequest.getCaseInstanceId());
        String assignee = SecurityUtils.getCurrentUserLogin().orElse(Constants.SYSTEM_ACCOUNT)
                .toLowerCase(Locale.ENGLISH);

        // create filter parameters map
        Map<String, Object> map = new HashMap<>();
        
        // add filters
        map.put(TASK_ASSIGNEE, assignee);
        map.put(FINISHED, true);
        map.put(TASK_DELETE_REASON, COMPLETED);

        List<String> caseInstanceIdList = getCaseInstanceIdList(taskRequest, map);
        
        List<TaskResponse> taskListResult = new ArrayList<>();
        for (String caseInstanceId1 : caseInstanceIdList) {
            log.info("get tasks by caseInstanceId {}", caseInstanceId1);
            map.put(CASE_INSTANCE_ID, caseInstanceId1);
            List<TaskResponse> taskList;
            if (null == taskRequest.getFirstResult()) {
                taskRequest.setFirstResult(0);
            }
            if (null != taskRequest.getMaxResults()) {
                taskList = camundaClient.getHistoryTasks(new JSONObject(map), taskRequest.getFirstResult(),
                        taskRequest.getMaxResults());
            } else {
                taskList = camundaClient.getHistoryTasks(new JSONObject(map), taskRequest.getFirstResult());
            }

            taskListResult.addAll(taskList);
        }
        return taskListResult;
    }

    /*
     * return non empty trimmed string value or null
     */
    private String getTrimmedValue(String value) {
        if (value != null) {
            value = value.trim();
            if (value.isEmpty())
                value = null;
        }
        return value;
    }

    /*
     * moves available task to the active state to be able to work on it
     */
    @Override
    public void startTask(TaskRequest taskRequest) {
        taskValidation.startTask(taskRequest);
        for (String taskId : taskRequest.getTaskIdList()) {
        	log.info("start task for taskId :{}", taskId);
            Map<String, Object> map = new HashMap<>();
            map.put("value", true);
            map.put("type", "Boolean");
            String variable = LC_UNDERSCORE + taskId + UNDERSCORE_ACTIVATE;
            camundaClient.updateCaseInstanceVariable(taskRequest.getCaseInstanceId(), variable, new JSONObject(map));
        }
    }

    /*
     * re-assign task to another user
     */
    @Override
    public void updateTaskAssignee(TaskRequest taskRequest) {
        log.info("update task assignee:{}", taskRequest.getTaskId());
        taskValidation.reassignTask(taskRequest);
        Map<String, String> reqJson = new HashMap<>();
        reqJson.put("userId", taskRequest.getUserId());
        camundaClient.updateAssignee(taskRequest.getTaskId(), new JSONObject(reqJson));
    }

    /*
     * create a new task on demand to be able to work on it
     */
    @Override
    public void createAdHocTask(TaskRequest taskRequest){
        log.info("create ad hoc task {},{},{},{}", BY_FILTERS_MRN_PROGRAM_ID_CASE_INSTANCE_ID,  taskRequest.getMrn(), taskRequest.getProgramId(), taskRequest.getCaseInstanceId());
        taskValidation.createTask(taskRequest);

        // create filter parameters map
        Map<String, Object> map = new HashMap<>();

        String owner = SecurityUtils.getCurrentUserLogin().orElse(Constants.SYSTEM_ACCOUNT).toLowerCase(Locale.ENGLISH);

        // add filters
        map.put(OWNER, owner);
        map.put(NAME, taskRequest.getName());
        map.put(DESCRIPTION, taskRequest.getDescription());
        map.put(ASSIGNEE, taskRequest.getAssignee());
        map.put(DUE, taskRequest.getDue());

        String parentTaskId = getTrimmedValue(taskRequest.getParentTaskId());
        if (null != parentTaskId) {
            TaskResponse parentTask = camundaClient.getTask(taskRequest.getParentTaskId());
            if (null != parentTask) {
                map.put(PARENT_TASK_ID, taskRequest.getParentTaskId());
            }
        }

        List<String> caseInstanceIdList = getCaseInstanceIdList(taskRequest, map);

        for (String caseInstanceId1 : caseInstanceIdList) {
            map.put(CASE_INSTANCE_ID, caseInstanceId1);
            camundaClient.createAdHocTask(new JSONObject(map));
        }
    }

    /*
     * get the caseInstanceId list filtered by mrn and programId
     */
    private List<String> getCaseInstanceIdList(TaskRequest taskRequest, Map<String, Object> map) {
        List<String> caseInstanceIdList = new ArrayList<>();
        
        String programId = getTrimmedValue(taskRequest.getProgramId());
        // create caseInstanceId list filter to get tasks by caseInstanceId
        String caseInstanceId = getTrimmedValue(taskRequest.getCaseInstanceId());
        if (caseInstanceId != null) {
            caseInstanceIdList.add(caseInstanceId);
            // add programId filter to create tasks by caseDefinitionKey
            map.put(CASE_DEFINITION_KEY, CASE_UNDERSCORE + programId);
        } else {
            List<CaseInstance> caseInstanceList = null;
            if (programId != null) {
                String caseDefinitionKey = CASE_UNDERSCORE + programId;
                caseInstanceList = camundaClient.getCaseInstance(taskRequest.getMrn(), caseDefinitionKey);
            }
            else
                caseInstanceList = camundaClient.getCaseInstance(taskRequest.getMrn());

            for (CaseInstance caseInstance : caseInstanceList)
                caseInstanceIdList.add(caseInstance.getId());
        }
        return caseInstanceIdList;
    }
}
