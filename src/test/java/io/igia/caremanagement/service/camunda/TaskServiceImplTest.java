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
/**
 * 
 */
package io.igia.caremanagement.service.camunda;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;

import io.igia.caremanagement.client.camunda.CamundaClient;
import io.igia.caremanagement.domain.enumeration.TaskCategory;
import io.igia.caremanagement.repository.TaskRepository;
import io.igia.caremanagement.service.dto.camunda.CaseExecution;
import io.igia.caremanagement.service.dto.camunda.CaseInstance;
import io.igia.caremanagement.service.dto.camunda.TaskRequest;
import io.igia.caremanagement.service.dto.camunda.TaskResponse;
import io.igia.caremanagement.service.impl.camunda.TaskServiceImpl;
import io.igia.caremanagement.service.validation.TaskValidation;

@RunWith(MockitoJUnitRunner.Silent.class)
public class TaskServiceImplTest {

    @InjectMocks
    private TaskServiceImpl taskServiceImpl;

    @Mock
    private CamundaClient camundaClient;

    @Mock
    private TaskRepository taskRepository;
    
    @Mock
    private TaskValidation taskValidation;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testCompleteTask() throws Exception {
        //TaskRequest task = new TaskRequest();
        
        doNothing().when(camundaClient).completeTask(any());
        taskServiceImpl.completeTask("taskId");
    }

    @Test(expected = Exception.class)
    public void testCompleteTaskWithError() throws Exception {
        doThrow().when(camundaClient).completeTask(any());
        taskServiceImpl.completeTask("taskId");
    }

    @Test
    public void testTodoTask() throws Exception {
        getAndAssertTasks(TaskCategory.TODO, null, null);
    }

    @Test
    public void testTodoTaskWithProgramId() throws Exception {
        getAndAssertTasks(TaskCategory.TODO, "P1", null);
    }
    
    @Test
    public void testTodoTaskWithCaseInstanceId() throws Exception {
        getAndAssertTasks(TaskCategory.TODO, null, "86c17957-cac3-11e8-8bd1-caff28a2dbdf");
    }
    
    @Test
    public void testTodoTaskWithProgramIdAndCaseInstanceId() throws Exception {
        getAndAssertTasks(TaskCategory.TODO, "P1", "86c17957-cac3-11e8-8bd1-caff28a2dbdf");
    }
    
    @Test
    public void testUpComingTask() throws Exception {
        getAndAssertTasks(TaskCategory.UPCOMING, null, null);
    }

    @Test
    public void testUpComingTaskWithProgramId() throws Exception {
        getAndAssertTasks(TaskCategory.UPCOMING, "P1", null);
    }

    @Test
    public void testUpComingTaskWithCaseInstanceId() throws Exception {
        getAndAssertTasks(TaskCategory.UPCOMING, null, "86c17957-cac3-11e8-8bd1-caff28a2dbdf");
    }
    
    @Test
    public void testUpComingTaskWithProgramIdAndCaseInstanceId() throws Exception {
        getAndAssertTasks(TaskCategory.UPCOMING, "P1", "86c17957-cac3-11e8-8bd1-caff28a2dbdf");
    }

    @Test
    public void testMissedTask() throws Exception {
        getAndAssertTasks(TaskCategory.MISSED, null, null);
    }

    @Test
    public void testMissedTaskWithProgramId() throws Exception {
        getAndAssertTasks(TaskCategory.MISSED, "P1", null);
    }

    @Test
    public void testMissedTaskWithCaseInstanceId() throws Exception {
        getAndAssertTasks(TaskCategory.MISSED, null, "86c17957-cac3-11e8-8bd1-caff28a2dbdf");
    }

    @Test
    public void testMissedTaskWithProgramIdAndCaseInstanceId() throws Exception {
        getAndAssertTasks(TaskCategory.MISSED, "P1", "86c17957-cac3-11e8-8bd1-caff28a2dbdf");
    }

    private void getAndAssertTasks(TaskCategory taskCategory, String programId, String caseInstanceId) {
        if (caseInstanceId == null)
            caseInstanceId = "86c17957-cac3-11e8-8bd1-caff28a2dbdf";
        TaskRequest taskRequest = new TaskRequest();
        taskRequest.setTaskCategory(taskCategory);
        if (programId != null)
            taskRequest.setProgramId("P1");
        taskRequest.setCaseInstanceId(caseInstanceId);
        taskRequest.setFirstResult(0);
        taskRequest.setMaxResults(2);
        TaskResponse taskResponse = new TaskResponse();
        taskResponse.setId("86c1c781-cac3-11e8-8bd1-caff28a2dbdf");
        taskResponse.setName("stage stratification");
        taskResponse.setAssignee("demo");
        taskResponse.setCreated("2018-10-08T11:59:36.308+0530");
        taskResponse.setDue("2018-10-07T23:54:00.000+0530");
        taskResponse.setPriority(50);
        taskResponse.setTaskDefinitionKey("PlanItem_T2");
        taskResponse.setCaseExecutionId("86c1a06e-cac3-11e8-8bd1-caff28a2dbdf");
        taskResponse.setCaseInstanceId(caseInstanceId);
        taskResponse.setSuspended(false);
        List<TaskResponse> taskList = new ArrayList<>();
        taskList.add(taskResponse);
        Mockito.when(camundaClient.getTasks(any(), any(Integer.class), any(Integer.class))).thenReturn(taskList);

        List<TaskResponse> taskResult = taskServiceImpl.getTasks(taskRequest);
        assertEquals("86c1c781-cac3-11e8-8bd1-caff28a2dbdf", taskResult.get(0).getId());
        assertEquals("stage stratification", taskResult.get(0).getName());
        assertEquals("PlanItem_T2", taskResult.get(0).getTaskDefinitionKey());
    }

    @Test
    public void testUpdateTaskAssignee() throws Exception {
        TaskRequest taskRequest = new TaskRequest();
        taskRequest.setTaskId("11609547-d38a-11e8-818f-caff28a2dbdf");
        taskRequest.setUserId("john");
        doNothing().when(camundaClient).updateAssignee(any(), any());
        taskServiceImpl.updateTaskAssignee(taskRequest);
    }

    @Test
    public void testAvailableTask() throws Exception {
        getAndAssertAvailableTasks(null, null);
    }

    @Test
    public void testAvailableTaskWithProgramId() throws Exception {
        getAndAssertAvailableTasks("P1", null);
    }

    @Test
    public void testAvailableTaskWithCaseInstanceId() throws Exception {
        getAndAssertAvailableTasks(null, "3a07a10f-cc6d-11e8-85cc-caff28a2dbdf");
    }
    
    @Test
    public void testAvailableTaskWithProgramIdAndCaseInstanceId() throws Exception {
        getAndAssertAvailableTasks("P1", "3a07a10f-cc6d-11e8-85cc-caff28a2dbdf");
    }

    private void getAndAssertAvailableTasks(String programId, String caseInstanceId) {
        CaseExecution caseExecution = new CaseExecution();

        caseExecution.setId("T3");
        if (caseInstanceId == null)
            caseInstanceId = "3a07a10f-cc6d-11e8-85cc-caff28a2dbdf";
        caseExecution.setCaseInstanceId(caseInstanceId);
        caseExecution.setActivityId("PlanItem_T3");
        caseExecution.setActivityName("Alert PCP if Blood Pressure >150/90");
        caseExecution.setActivityType("humanTask");
        caseExecution.setActive(false);
        caseExecution.setEnabled(false);
        caseExecution.setDisabled(false);
        caseExecution.setRequired(false);
        List<CaseExecution> caseExecutionList = new ArrayList<>();
        caseExecutionList.add(caseExecution);
        Mockito.when(camundaClient.getCaseExecutions(any())).thenReturn(caseExecutionList);

        List<TaskResponse> taskResult = taskServiceImpl.getAvailableTasks("sdc11", programId, caseInstanceId);
        if (!taskResult.isEmpty()) {
            assertEquals("T3", taskResult.get(0).getId());
            assertEquals(caseInstanceId, taskResult.get(0).getCaseInstanceId());
            assertEquals("PlanItem_T3", taskResult.get(0).getActivityId());
            assertEquals("Alert PCP if Blood Pressure >150/90", taskResult.get(0).getName());
        }
    }

    @Test
    public void testCompletedTask() throws Exception {
        TaskRequest task = new TaskRequest();
        task.setBusinessKey("add22");
        task.setFirstResult(0);
        task.setMaxResults(2);
        TaskResponse taskResponse = new TaskResponse();
        taskResponse.setId("86c1c781-cac3-11e8-8bd1-caff28a2dbdf");
        taskResponse.setName("stage stratification");
        taskResponse.setAssignee("demo");
        taskResponse.setDue("2018-10-07T23:54:00.000+0530");
        taskResponse.setPriority(50);
        taskResponse.setTaskDefinitionKey("PlanItem_T2");
        taskResponse.setCaseExecutionId("86c1a06e-cac3-11e8-8bd1-caff28a2dbdf");
        taskResponse.setCaseInstanceId("86c17957-cac3-11e8-8bd1-caff28a2dbdf");

        List<TaskResponse> taskList = new ArrayList<>();
        taskList.add(taskResponse);
        CaseInstance caseInstance = new CaseInstance();
        caseInstance.setId("86c17957-cac3-11e8-8bd1-caff28a2dbdf");
        List<CaseInstance> caseInstanceList = new ArrayList<>();
        caseInstanceList.add(caseInstance);
        Mockito.when(camundaClient.getCaseInstance((any()))).thenReturn(caseInstanceList);
        Mockito.when(camundaClient.getHistoryTasks(any(), any(Integer.class), any(Integer.class))).thenReturn(taskList);
        List<TaskResponse> taskResult = taskServiceImpl.getHistoryTasks(task);
        assertEquals("86c1c781-cac3-11e8-8bd1-caff28a2dbdf", taskResult.get(0).getId());
        assertEquals("stage stratification", taskResult.get(0).getName());
        assertEquals("PlanItem_T2", taskResult.get(0).getTaskDefinitionKey());
    }

    @Test
    public void testStartTask() throws Exception {
        TaskRequest task = new TaskRequest();
        List<String> taskIdList = new ArrayList<>();
        taskIdList.add("T5");
        taskIdList.add("T3");
        task.setTaskIdList(taskIdList);
        task.setCaseInstanceId("36f9dfef-d380-11e8-818f-caff28a2dbdf");
        doNothing().when(camundaClient).updateCaseInstanceVariable(any(), any(), any());
        taskServiceImpl.startTask(task);
    }

    @Test
    public void testCreateAdHocTask() throws Exception {
        createAdHocTask(null, null);
    }

    @Test
    public void testCreateAdHocTaskWithProgramId() throws Exception {
        createAdHocTask("P1", null);
    }

    @Test
    public void testCreateAdHocTaskWithCaseInstanceId() throws Exception {
        createAdHocTask(null, "36f9dfef-d380-11e8-818f-caff28a2dbdf");
    }

    @Test
    public void testCreateAdHocTaskWithProgramIdAndCaseInstanceId() throws Exception {
        createAdHocTask("P1", "36f9dfef-d380-11e8-818f-caff28a2dbdf");
    }

    private TaskRequest createAdHocTaskRequest() {
        TaskRequest taskRequest = new TaskRequest();
        taskRequest.setName("Capture blood pressure");
        taskRequest.setDescription("Capture blood pressure");
        taskRequest.setAssignee("demo");
        taskRequest.setDue("2018-11-22T11:11:15.372+0530");
        taskRequest.setMrn("asd11");
        taskRequest.setParentTaskId("127aead5-d83d-11e8-9648-caff28a2dbdf");
        return taskRequest;
    }

    private void createAdHocTask(String programId, String caseInstanceId) {
        TaskRequest taskRequest = createAdHocTaskRequest();
        if (programId != null)
            taskRequest.setProgramId(programId);
        if (caseInstanceId != null)
            taskRequest.setCaseInstanceId(caseInstanceId);
        CaseInstance caseInstance = new CaseInstance();
        caseInstance.setId("86c17957-cac3-11e8-8bd1-caff28a2dbdf");
        List<CaseInstance> caseInstanceList = new ArrayList<>();
        caseInstanceList.add(caseInstance);
        TaskResponse parentTask = new TaskResponse();
        parentTask.setId("127aead5-d83d-11e8-9648-caff28a2dbdf");
        Mockito.when(camundaClient.getCaseInstance((any()))).thenReturn(caseInstanceList);
        Mockito.when(camundaClient.getTask((any()))).thenReturn(parentTask);
        doNothing().when(camundaClient).createAdHocTask(any());
        doNothing().when(taskValidation).createTask(taskRequest);
        taskServiceImpl.createAdHocTask(taskRequest);
    }

}
