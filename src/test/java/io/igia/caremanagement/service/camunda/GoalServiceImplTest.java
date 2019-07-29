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
package io.igia.caremanagement.service.camunda;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import io.igia.caremanagement.client.camunda.CamundaClient;
import io.igia.caremanagement.domain.enumeration.Category;
import io.igia.caremanagement.service.dto.camunda.CaseActivityInstance;
import io.igia.caremanagement.service.dto.camunda.CaseExecution;
import io.igia.caremanagement.service.dto.camunda.CaseInstance;
import io.igia.caremanagement.service.dto.camunda.GoalRequest;
import io.igia.caremanagement.service.dto.camunda.GoalResponse;
import io.igia.caremanagement.service.impl.camunda.GoalServiceImpl;

import org.mockito.ArgumentMatchers;

public class GoalServiceImplTest {

    @InjectMocks
    private GoalServiceImpl goalServiceImpl;

    @Mock
    private CamundaClient camundaClient;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void getAvailableActiveGoals() throws Exception {
        CaseExecution caseExecution = new CaseExecution();
        caseExecution.setId("G3");
        caseExecution.setCaseInstanceId("3a07a10f-cc6d-11e8-85cc-caff28a2dbdf");
        caseExecution.setActivityId("PlanItem_G3");
        caseExecution.setActivityName("Alert PCP if Blood Pressure >150/90");
        caseExecution.setActivityType("stage");
        caseExecution.setActive(false);
        caseExecution.setEnabled(false);
        caseExecution.setDisabled(false);
        caseExecution.setRequired(false);
        caseExecution.setParentId("eb6d7113-f9f2-11e8-b61e-caff28a2dbdf");
        CaseExecution caseExecution1 = new CaseExecution();
        caseExecution1.setId("eb6d7113-f9f2-11e8-b61e-caff28a2dbdf");
        caseExecution1.setCaseInstanceId("3a07a10f-cc6d-11e8-85cc-caff28a2dbdf");
        caseExecution1.setActivityId("PlanItem_E2");
        caseExecution1.setActivityName("Assessment");
        caseExecution1.setActivityType("stage");
        caseExecution1.setActive(true);
        caseExecution1.setEnabled(false);
        caseExecution1.setDisabled(false);
        caseExecution1.setRequired(false);
        caseExecution1.setParentId("8fe32de3-fea1-11e8-aaf6-caff28a2dbdf");

        CaseExecution caseExecution2 = new CaseExecution();
        caseExecution2.setId("8fe32de3-fea1-11e8-aaf6-caff28a2dbdf");
        caseExecution2.setCaseInstanceId("3a07a10f-cc6d-11e8-85cc-caff28a2dbdf");
        caseExecution2.setActivityId("CasePlanModel_P1");
        caseExecution2.setActivityName("Stage 1/2, Risk Moderate");
        caseExecution2.setActivityType("casePlanModel");
        caseExecution2.setActive(true);
        caseExecution2.setEnabled(false);
        caseExecution2.setDisabled(false);
        caseExecution2.setRequired(false);
        caseExecution2.setParentId(null);

        List<CaseExecution> goalList = new ArrayList<>();
        goalList.add(caseExecution);
        goalList.add(caseExecution1);
        goalList.add(caseExecution2);
        Mockito.when(camundaClient.getCaseExecutions(any())).thenReturn(goalList);

        GoalRequest goalRequest = new GoalRequest();
        goalRequest.setMrn("mrn004");
        goalRequest.setCategory(Category.AVAILABLE);
        List<GoalResponse> goalResult = goalServiceImpl.getAvailableActiveGoals(goalRequest);
        if (!goalResult.isEmpty()) {
            assertEquals("G3", goalResult.get(0).getCaseInstances().get(0).getGoals().get(0).getId());
            assertEquals("3a07a10f-cc6d-11e8-85cc-caff28a2dbdf",
                    goalResult.get(0).getCaseInstances().get(0).getCaseInstanceId());
            assertEquals("PlanItem_G3", goalResult.get(0).getCaseInstances().get(0).getGoals().get(0).getActivityId());
            assertEquals("Alert PCP if Blood Pressure >150/90",
                    goalResult.get(0).getCaseInstances().get(0).getGoals().get(0).getName());
            assertEquals("Stage 1/2, Risk Moderate", goalResult.get(0).getProgramName());
        }

    }

    @Test
    public void testActiveGoals() throws Exception {
        CaseExecution caseExecution = new CaseExecution();
        caseExecution.setId("G3");
        caseExecution.setCaseInstanceId("3a07a10f-cc6d-11e8-85cc-caff28a2dbdf");
        caseExecution.setActivityId("PlanItem_G3");
        caseExecution.setActivityName("Alert PCP if Blood Pressure >150/90");
        caseExecution.setActivityType("stage");
        caseExecution.setActive(true);
        caseExecution.setEnabled(false);
        caseExecution.setDisabled(false);
        caseExecution.setRequired(false);
        caseExecution.setParentId("eb6d7113-f9f2-11e8-b61e-caff28a2dbdf");
        CaseExecution caseExecution1 = new CaseExecution();
        caseExecution1.setId("eb6d7113-f9f2-11e8-b61e-caff28a2dbdf");
        caseExecution1.setCaseInstanceId("3a07a10f-cc6d-11e8-85cc-caff28a2dbdf");
        caseExecution1.setActivityId("PlanItem_E2");
        caseExecution1.setActivityName("Assessment");
        caseExecution1.setActivityType("stage");
        caseExecution1.setActive(true);
        caseExecution1.setEnabled(false);
        caseExecution1.setDisabled(false);
        caseExecution1.setRequired(false);
        caseExecution1.setParentId("8fe32de3-fea1-11e8-aaf6-caff28a2dbdf");

        CaseExecution caseExecution2 = new CaseExecution();
        caseExecution2.setId("8fe32de3-fea1-11e8-aaf6-caff28a2dbdf");
        caseExecution2.setCaseInstanceId("3a07a10f-cc6d-11e8-85cc-caff28a2dbdf");
        caseExecution2.setActivityId("CasePlanModel_P1");
        caseExecution2.setActivityName("Stage 1/2, Risk Moderate");
        caseExecution2.setActivityType("casePlanModel");
        caseExecution2.setActive(true);
        caseExecution2.setEnabled(false);
        caseExecution2.setDisabled(false);
        caseExecution2.setRequired(false);
        caseExecution2.setParentId(null);

        List<CaseExecution> goalList = new ArrayList<>();
        goalList.add(caseExecution);
        goalList.add(caseExecution1);
        goalList.add(caseExecution2);
        Mockito.when(camundaClient.getCaseExecutions(any())).thenReturn(goalList);
        GoalRequest goalRequest = new GoalRequest();
        goalRequest.setMrn("mrn004");
        goalRequest.setCategory(Category.ACTIVE);

        List<GoalResponse> goalResult = goalServiceImpl.getAvailableActiveGoals(goalRequest);
        if (!goalResult.isEmpty()) {
            assertEquals("G3", goalResult.get(0).getCaseInstances().get(0).getGoals().get(0).getId());
            assertEquals("3a07a10f-cc6d-11e8-85cc-caff28a2dbdf",
                    goalResult.get(0).getCaseInstances().get(0).getCaseInstanceId());
            assertEquals("PlanItem_G3", goalResult.get(0).getCaseInstances().get(0).getGoals().get(0).getActivityId());
            assertEquals("Alert PCP if Blood Pressure >150/90",
                    goalResult.get(0).getCaseInstances().get(0).getGoals().get(0).getName());
            assertEquals("Stage 1/2, Risk Moderate", goalResult.get(0).getProgramName());
        }
    }

    @Test
    public void testStartGoal() throws Exception {
        GoalRequest goalRequest = new GoalRequest();
        List<String> goalIdList = new ArrayList<>();
        goalIdList.add("T5");
        goalIdList.add("T3");
        goalRequest.setGoalIdList(goalIdList);
        goalRequest.setCaseInstanceId("36f9dfef-d380-11e8-818f-caff28a2dbdf");
        doNothing().when(camundaClient).updateCaseInstanceVariable(any(), any(), any());
        goalServiceImpl.startGoal(goalRequest);
    }

    @Test
    public void testGoalsHistory() throws Exception {
        CaseExecution caseExecution = new CaseExecution();
        caseExecution.setId("G3");
        caseExecution.setCaseInstanceId("3a07a10f-cc6d-11e8-85cc-caff28a2dbdf");
        caseExecution.setActivityId("PlanItem_G3");
        caseExecution.setActivityName("Alert PCP if Blood Pressure >150/90");
        caseExecution.setActivityType("stage");
        caseExecution.setActive(false);
        caseExecution.setEnabled(false);
        caseExecution.setDisabled(false);
        caseExecution.setRequired(false);
        caseExecution.setParentId("eb6d7113-f9f2-11e8-b61e-caff28a2dbdf");
        CaseExecution caseExecution1 = new CaseExecution();
        caseExecution1.setId("eb6d7113-f9f2-11e8-b61e-caff28a2dbdf");
        caseExecution1.setCaseInstanceId("3a07a10f-cc6d-11e8-85cc-caff28a2dbdf");
        caseExecution1.setActivityId("PlanItem_E2");
        caseExecution1.setActivityName("Assessment");
        caseExecution1.setActivityType("stage");
        caseExecution1.setActive(true);
        caseExecution1.setEnabled(false);
        caseExecution1.setDisabled(false);
        caseExecution1.setRequired(false);
        caseExecution1.setParentId("8fe32de3-fea1-11e8-aaf6-caff28a2dbdf");

        CaseExecution caseExecution2 = new CaseExecution();
        caseExecution2.setId("b44bf0e3-0ea1-11e9-928f-caff28a2dbdf");
        caseExecution2.setCaseInstanceId("b44bf0e3-0ea1-11e9-928f-caff28a2dbdf");
        caseExecution2.setActivityId("CasePlanModel_P1");
        caseExecution2.setActivityName("Stage 1/2, Risk Moderate");
        caseExecution2.setActivityType("casePlanModel");
        caseExecution2.setActive(true);
        caseExecution2.setEnabled(false);
        caseExecution2.setDisabled(false);
        caseExecution2.setRequired(false);
        caseExecution2.setParentId(null);

        List<CaseExecution> goalList = new ArrayList<>();
        goalList.add(caseExecution);
        goalList.add(caseExecution1);
        goalList.add(caseExecution2);
        Mockito.when(camundaClient.getCaseExecutionsByCaseInstance(any(), any())).thenReturn(goalList);

        CaseInstance caseInstance = new CaseInstance();
        caseInstance.setId("86c17957-cac3-11e8-8bd1-caff28a2dbdf");
        List<CaseInstance> caseInstanceList = new ArrayList<>();
        caseInstanceList.add(caseInstance);
        Mockito.when(camundaClient.getCaseInstance((any()))).thenReturn(caseInstanceList);

        CaseActivityInstance caseActivityInstance = new CaseActivityInstance();
        caseActivityInstance.setId("b4f3a2ab-0ea1-11e9-928f-caff28a2dbdf");
        caseActivityInstance.setParentCaseActivityInstanceId("b44c3f07-0ea1-11e9-928f-caff28a2dbdf");
        caseActivityInstance.setCaseActivityId("PlanItem_P1_E4_G1");
        caseActivityInstance.setCaseActivityName("Stage_P1_E4_G1");
        caseActivityInstance.setCaseActivityType("stage");
        caseActivityInstance.setCaseDefinitionId("Case_P3:1:aad96c42-0ea1-11e9-928f-caff28a2dbdf");
        caseActivityInstance.setCaseInstanceId("b44bf0e3-0ea1-11e9-928f-caff28a2dbdf");
        caseActivityInstance.setCaseExecutionId("b4f3a2ab-0ea1-11e9-928f-caff28a2dbdf");
        caseActivityInstance.setTerminated(false);
        caseActivityInstance.setCompleted(true);

        CaseActivityInstance caseActivityInstance1 = new CaseActivityInstance();
        caseActivityInstance1.setId("b44c3f07-0ea1-11e9-928f-caff28a2dbdf");
        caseActivityInstance1.setParentCaseActivityInstanceId("b44bf0e3-0ea1-11e9-928f-caff28a2dbdf");
        caseActivityInstance1.setCaseActivityId("PlanItem_P1_E4");
        caseActivityInstance1.setCaseActivityName("Stage_P1_E4");
        caseActivityInstance1.setCaseActivityType("stage");
        caseActivityInstance1.setCaseDefinitionId("Case_P3:1:aad96c42-0ea1-11e9-928f-caff28a2dbdf");
        caseActivityInstance1.setCaseInstanceId("b44bf0e3-0ea1-11e9-928f-caff28a2dbdf");
        caseActivityInstance1.setCaseExecutionId("b44c3f07-0ea1-11e9-928f-caff28a2dbdf");
        caseActivityInstance1.setTerminated(false);
        caseActivityInstance1.setCompleted(true);
        List<CaseActivityInstance> caseActivityInstanceList = new ArrayList<>();
        caseActivityInstanceList.add(caseActivityInstance);
        caseActivityInstanceList.add(caseActivityInstance1);

        Mockito.when(
                camundaClient.getCaseActivityInstance(ArgumentMatchers.anyBoolean(), any(), any(), any(), any(), any()))
                .thenReturn(caseActivityInstanceList);
        GoalRequest goalRequest = new GoalRequest();
        goalRequest.setMrn("mrn004");
        List<GoalResponse> goalResult = goalServiceImpl.getHistoryGoals(goalRequest);
        if (!goalResult.isEmpty()) {
            assertEquals("P1_E4_G1", goalResult.get(0).getCaseInstances().get(0).getGoals().get(0).getId());
            assertEquals("b44bf0e3-0ea1-11e9-928f-caff28a2dbdf",
                    goalResult.get(0).getCaseInstances().get(0).getCaseInstanceId());
            assertEquals("PlanItem_P1_E4_G1",
                    goalResult.get(0).getCaseInstances().get(0).getGoals().get(0).getActivityId());
            assertEquals("Stage_P1_E4_G1", goalResult.get(0).getCaseInstances().get(0).getGoals().get(0).getName());
            assertEquals("Stage 1/2, Risk Moderate", goalResult.get(0).getProgramName());
        }

    }

    @Test
    public void testGoalsHistoryDateFilter() throws Exception {
        CaseExecution caseExecution = new CaseExecution();
        caseExecution.setId("G3");
        caseExecution.setCaseInstanceId("3a07a10f-cc6d-11e8-85cc-caff28a2dbdf");
        caseExecution.setActivityId("PlanItem_G3");
        caseExecution.setActivityName("Alert PCP if Blood Pressure >150/90");
        caseExecution.setActivityType("stage");
        caseExecution.setActive(false);
        caseExecution.setEnabled(false);
        caseExecution.setDisabled(false);
        caseExecution.setRequired(false);
        caseExecution.setParentId("eb6d7113-f9f2-11e8-b61e-caff28a2dbdf");
        CaseExecution caseExecution1 = new CaseExecution();
        caseExecution1.setId("eb6d7113-f9f2-11e8-b61e-caff28a2dbdf");
        caseExecution1.setCaseInstanceId("3a07a10f-cc6d-11e8-85cc-caff28a2dbdf");
        caseExecution1.setActivityId("PlanItem_E2");
        caseExecution1.setActivityName("Assessment");
        caseExecution1.setActivityType("stage");
        caseExecution1.setActive(true);
        caseExecution1.setEnabled(false);
        caseExecution1.setDisabled(false);
        caseExecution1.setRequired(false);
        caseExecution1.setParentId("8fe32de3-fea1-11e8-aaf6-caff28a2dbdf");

        CaseExecution caseExecution2 = new CaseExecution();
        caseExecution2.setId("b44bf0e3-0ea1-11e9-928f-caff28a2dbdf");
        caseExecution2.setCaseInstanceId("b44bf0e3-0ea1-11e9-928f-caff28a2dbdf");
        caseExecution2.setActivityId("CasePlanModel_P1");
        caseExecution2.setActivityName("Stage 1/2, Risk Moderate");
        caseExecution2.setActivityType("casePlanModel");
        caseExecution2.setActive(true);
        caseExecution2.setEnabled(false);
        caseExecution2.setDisabled(false);
        caseExecution2.setRequired(false);
        caseExecution2.setParentId(null);

        List<CaseExecution> goalList = new ArrayList<>();
        goalList.add(caseExecution);
        goalList.add(caseExecution1);
        goalList.add(caseExecution2);
        Mockito.when(camundaClient.getCaseExecutionsByCaseInstance(any(), any())).thenReturn(goalList);

        CaseInstance caseInstance = new CaseInstance();
        caseInstance.setId("86c17957-cac3-11e8-8bd1-caff28a2dbdf");
        List<CaseInstance> caseInstanceList = new ArrayList<>();
        caseInstanceList.add(caseInstance);
        Mockito.when(camundaClient.getCaseInstance((any()))).thenReturn(caseInstanceList);

        CaseActivityInstance caseActivityInstance = new CaseActivityInstance();
        caseActivityInstance.setId("b4f3a2ab-0ea1-11e9-928f-caff28a2dbdf");
        caseActivityInstance.setParentCaseActivityInstanceId("b44c3f07-0ea1-11e9-928f-caff28a2dbdf");
        caseActivityInstance.setCaseActivityId("PlanItem_P1_E4_G1");
        caseActivityInstance.setCaseActivityName("Stage_P1_E4_G1");
        caseActivityInstance.setCaseActivityType("stage");
        caseActivityInstance.setCaseDefinitionId("Case_P3:1:aad96c42-0ea1-11e9-928f-caff28a2dbdf");
        caseActivityInstance.setCaseInstanceId("b44bf0e3-0ea1-11e9-928f-caff28a2dbdf");
        caseActivityInstance.setCaseExecutionId("b4f3a2ab-0ea1-11e9-928f-caff28a2dbdf");
        caseActivityInstance.setTerminated(false);
        caseActivityInstance.setCompleted(true);
        caseActivityInstance.setCreateTime("2019-01-31T15:37:41.554+0530");
        caseActivityInstance.setEndTime("2019-02-01T14:29:58.869+0530");

        CaseActivityInstance caseActivityInstance1 = new CaseActivityInstance();
        caseActivityInstance1.setId("b44c3f07-0ea1-11e9-928f-caff28a2dbdf");
        caseActivityInstance1.setParentCaseActivityInstanceId("b44bf0e3-0ea1-11e9-928f-caff28a2dbdf");
        caseActivityInstance1.setCaseActivityId("PlanItem_P1_E4");
        caseActivityInstance1.setCaseActivityName("Stage_P1_E4");
        caseActivityInstance1.setCaseActivityType("stage");
        caseActivityInstance1.setCaseDefinitionId("Case_P3:1:aad96c42-0ea1-11e9-928f-caff28a2dbdf");
        caseActivityInstance1.setCaseInstanceId("b44bf0e3-0ea1-11e9-928f-caff28a2dbdf");
        caseActivityInstance1.setCaseExecutionId("b44c3f07-0ea1-11e9-928f-caff28a2dbdf");
        caseActivityInstance1.setTerminated(false);
        caseActivityInstance1.setCompleted(true);
        caseActivityInstance.setCreateTime("2019-01-31T15:38:41.554+0530");
        caseActivityInstance.setEndTime("2019-02-01T14:29:58.869+0530");
        List<CaseActivityInstance> caseActivityInstanceList = new ArrayList<>();
        caseActivityInstanceList.add(caseActivityInstance);
        caseActivityInstanceList.add(caseActivityInstance1);

        Mockito.when(
                camundaClient.getCaseActivityInstance(ArgumentMatchers.anyBoolean(), any(), any(), any(), any(), any()))
                .thenReturn(caseActivityInstanceList);
        GoalRequest goalRequest = new GoalRequest();
        goalRequest.setMrn("mrn004");
        goalRequest.setCreatedBefore("2019-01-31T15:38:10+05:30");
        goalRequest.setCreatedAfter("2019-01-31T15:37:00+05:30");
        goalRequest.setEndedBefore("2019-02-01T14:30:00+05:30");
        goalRequest.setEndedAfter("2019-02-01T14:29:00+05:30");
        List<GoalResponse> goalResult = goalServiceImpl.getHistoryGoals(goalRequest);
        if (!goalResult.isEmpty()) {
            assertEquals("P1_E4_G1", goalResult.get(0).getCaseInstances().get(0).getGoals().get(0).getId());
            assertEquals("b44bf0e3-0ea1-11e9-928f-caff28a2dbdf",
                    goalResult.get(0).getCaseInstances().get(0).getCaseInstanceId());
            assertEquals("PlanItem_P1_E4_G1",
                    goalResult.get(0).getCaseInstances().get(0).getGoals().get(0).getActivityId());
            assertEquals("Stage_P1_E4_G1", goalResult.get(0).getCaseInstances().get(0).getGoals().get(0).getName());
            assertEquals("Stage 1/2, Risk Moderate", goalResult.get(0).getProgramName());
        }
    }

    @Test
    public void testGetGoals() throws Exception {
        GoalRequest goalRequest = new GoalRequest();
        goalRequest.setCategory(Category.ACTIVE);
        Mockito.when(goalServiceImpl.getAvailableActiveGoals(goalRequest)).thenReturn(new ArrayList<GoalResponse>());
        goalServiceImpl.getGoals(goalRequest);
    }

    @Test
    public void testGetHistoryGoals() throws Exception {
        GoalRequest goalRequest = new GoalRequest();
        goalRequest.setCategory(Category.HISTORY);
        Mockito.when(goalServiceImpl.getAvailableActiveGoals(goalRequest)).thenReturn(new ArrayList<GoalResponse>());
        goalServiceImpl.getGoals(goalRequest);
    }

    @Test(expected = Exception.class)
    public void testGetGoalsNoCategory() throws Exception {
        GoalRequest goalRequest = new GoalRequest();
        Mockito.when(goalServiceImpl.getAvailableActiveGoals(goalRequest)).thenReturn(new ArrayList<GoalResponse>());
        goalServiceImpl.getGoals(goalRequest);
    }
}
