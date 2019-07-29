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

import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.json.simple.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import io.igia.caremanagement.client.camunda.CamundaClient;
import io.igia.caremanagement.config.Constants;
import io.igia.caremanagement.domain.enumeration.Category;
import io.igia.caremanagement.service.camunda.GoalService;
import io.igia.caremanagement.service.dto.camunda.CaseActivityInstance;
import io.igia.caremanagement.service.dto.camunda.CaseExecution;
import io.igia.caremanagement.service.dto.camunda.CaseInstance;
import io.igia.caremanagement.service.dto.camunda.Goal;
import io.igia.caremanagement.service.dto.camunda.GoalCaseInstance;
import io.igia.caremanagement.service.dto.camunda.GoalRequest;
import io.igia.caremanagement.service.dto.camunda.GoalResponse;
import io.igia.caremanagement.web.rest.errors.CustomParameterizedException;

@Service("camundaGoalService")
@Transactional
public class GoalServiceImpl implements GoalService {
    private final Logger log = LoggerFactory.getLogger(GoalServiceImpl.class);

    @Autowired
    private CamundaClient camundaClient;

    @Override
    public List<GoalResponse> getGoals(GoalRequest goalRequest) {
        List<GoalResponse> goalResponse = null;
        if ((goalRequest.getCategory() == Category.AVAILABLE) || (goalRequest.getCategory() == Category.ACTIVE)) {
            goalResponse = getAvailableActiveGoals(goalRequest);
        } else if (goalRequest.getCategory() == Category.HISTORY) {
            goalResponse = getHistoryGoals(goalRequest);
        } else {
            throw new CustomParameterizedException("Invalid Goal Category", "Category");
        }
        return goalResponse;
    }

    @Override
    public List<GoalResponse> getAvailableActiveGoals(GoalRequest goalRequest) {
        log.info("get goals for mrn: {} ", goalRequest.getMrn());
        List<CaseExecution> caseExecutionList;
        if (null == goalRequest.getProgramId()) {
            caseExecutionList = camundaClient.getCaseExecutions(goalRequest.getMrn());
        } else {
            String programId = Constants.CASE + "_" + goalRequest.getProgramId();
            caseExecutionList = camundaClient.getCaseExecutions(goalRequest.getMrn(), programId);
        }
        Map<String, List<GoalResponse>> caseInstanceMap = new HashMap<>();
        Map<String, CaseExecution> pogramMap1 = new HashMap<>();
        Map<String, CaseExecution> stageMap = new HashMap<>();
        /// filter all stage objects and add in map<caseInstance,stageObject>
        if (caseExecutionList != null && !caseExecutionList.isEmpty()) {
            filterStage(caseExecutionList, goalRequest, pogramMap1, caseInstanceMap, stageMap);
        }
        // filter goal objects
        Map<String, List<GoalResponse>> caseInstanceResult = new HashMap<>();
        filterActiveavailableGoals(stageMap, caseInstanceResult, pogramMap1, caseInstanceMap);
        // map programId to map of caseInstance and list of goal
        Map<String, Map<String, List<GoalResponse>>> pogramMap = new HashMap<>();
        goalList(pogramMap, caseInstanceResult);
        List<GoalResponse> goalresult = new ArrayList<>();
        availableActiveFinalResponse(pogramMap, goalresult);
        return goalresult;
    }

    private void filterStage(List<CaseExecution> caseExecutionList, GoalRequest goalRequest,
            Map<String, CaseExecution> pogramMap1, Map<String, List<GoalResponse>> caseInstanceMap,
            Map<String, CaseExecution> stageMap) {
        for (CaseExecution caseExecution : caseExecutionList) {
            stageMap.putIfAbsent(caseExecution.getId(), caseExecution);
            boolean available = false;
            if (checkAvailableActive(available, goalRequest, caseExecution)) {
                caseMap(caseInstanceMap, caseExecution);
            }
            // filter caseplan object and add in map<id,caseObject>
            else if (caseExecution.getActivityType().equalsIgnoreCase(Constants.CASEPLAN)) {
                pogramMap1.putIfAbsent(caseExecution.getId(), caseExecution);
            }
        }
    }

    private boolean checkAvailableActive(boolean available, GoalRequest goalRequest, CaseExecution caseExecution) {
        if (goalRequest.getCategory() == Category.AVAILABLE) {
            available = (caseExecution.getActivityType().equalsIgnoreCase(Constants.STAGE))
                    && !caseExecution.getActive() && !caseExecution.getDisabled() && !caseExecution.getEnabled()
                    && !caseExecution.getRequired();
        } else if (goalRequest.getCategory() == Category.ACTIVE) {
            available = (caseExecution.getActivityType().equalsIgnoreCase(Constants.STAGE))
                    && caseExecution.getActive();
        }
        return available;
    }

    private void caseMap(Map<String, List<GoalResponse>> caseInstanceMap, CaseExecution caseExecution) {
        List<GoalResponse> goalList;
        GoalResponse goalResponse = new GoalResponse();
        if (caseInstanceMap.containsKey(caseExecution.getCaseInstanceId())) {
            goalList = caseInstanceMap.get(caseExecution.getCaseInstanceId());
        } else {
            goalList = new ArrayList<>();
        }
        String activityId = caseExecution.getActivityId();
        if (activityId != null && (activityId.length() > 0)) {
            int index = activityId.indexOf('_');
            if (index != -1) {
                String episodeId = activityId.substring(index + 1, activityId.length());
                goalResponse.setId(episodeId);
            }
        }
        goalResponse.setCaseInstanceId(caseExecution.getCaseInstanceId());
        goalResponse.setDescription(caseExecution.getActivityDescription());
        goalResponse.setName(caseExecution.getActivityName());
        goalResponse.setActivityId(activityId);
        goalResponse.setEpisodeId(caseExecution.getParentId());
        goalList.add(goalResponse);
        caseInstanceMap.put(caseExecution.getCaseInstanceId(), goalList);
    }

    private void filterActiveavailableGoals(Map<String, CaseExecution> stageMap,
            Map<String, List<GoalResponse>> caseInstanceResult, Map<String, CaseExecution> pogramMap1,
            Map<String, List<GoalResponse>> caseInstanceMap) {
        for (Entry<String, List<GoalResponse>> entry : caseInstanceMap.entrySet()) {
            List<GoalResponse> goalResponse = entry.getValue();
            List<GoalResponse> goalresult = new ArrayList<>();
            for (GoalResponse goal : goalResponse) {
                if (stageMap.containsKey(goal.getEpisodeId())) {
                    CaseExecution caseExecution = stageMap.get(goal.getEpisodeId());
                    if (caseExecution.getActivityType().equalsIgnoreCase(Constants.STAGE)) {
                        goal.setEpisodeName(caseExecution.getActivityName());
                        goal.setProgramId(caseExecution.getParentId());
                        if (pogramMap1.containsKey(goal.getProgramId())) {
                            CaseExecution caseExecution1 = pogramMap1.get(goal.getProgramId());
                            goal.setProgramName(caseExecution1.getActivityName());
                            goal.setProgramId(caseExecution1.getActivityId());
                        }
                        goalresult.add(goal);
                    }
                }
            }
            caseInstanceResult.put(entry.getKey(), goalresult);
        }
    }

    private void goalList(Map<String, Map<String, List<GoalResponse>>> pogramMap,
            Map<String, List<GoalResponse>> caseInstanceResult) {
        for (Entry<String, List<GoalResponse>> entry : caseInstanceResult.entrySet()) {
            List<GoalResponse> goalResponse = entry.getValue();
            for (GoalResponse goal : goalResponse) {
                if (pogramMap.containsKey(goal.getProgramId())) {
                    Map<String, List<GoalResponse>> map = pogramMap.get(goal.getProgramId());
                    if (map.containsKey(entry.getKey())) {
                        List<GoalResponse> list = map.get(entry.getKey());
                        list.add(goal);
                        map.put(entry.getKey(), list);
                    } else {
                        List<GoalResponse> list = new ArrayList<>();
                        list.add(goal);
                        map.put(entry.getKey(), list);
                    }
                    pogramMap.put(goal.getProgramId(), map);

                } else {
                    Map<String, List<GoalResponse>> map = new HashMap<>();
                    List<GoalResponse> list = new ArrayList<>();
                    list.add(goal);
                    map.put(goal.getCaseInstanceId(), list);
                    pogramMap.put(goal.getProgramId(), map);
                }
            }

        }
    }
     
    private void availableActiveFinalResponse(Map<String, Map<String, List<GoalResponse>>> pogramMap,
            List<GoalResponse> goalresult) {
        for (Entry<String, Map<String, List<GoalResponse>>> map : pogramMap.entrySet()) {
            GoalResponse goalResponse = new GoalResponse();
            String programName = "";
            String program = "";
            List<GoalCaseInstance> goalCaseInstanceList = new ArrayList<>();
            for (Entry<String, List<GoalResponse>> entry : map.getValue().entrySet()) {
                String key = entry.getKey();
                List<GoalResponse> value = entry.getValue();
                List<Goal> goals = new ArrayList<>();
                GoalCaseInstance goalCaseInstance = new GoalCaseInstance();
                goalCaseInstance.setCaseInstanceId(key);
                for (GoalResponse currentEpisode : value) {
                    Goal goal = new Goal();
                    goal.setId(currentEpisode.getId());
                    goal.setName(currentEpisode.getName());
                    goal.setDescription(currentEpisode.getDescription());
                    goal.setActivityId(currentEpisode.getActivityId());
                    goal.setEpisodeId(currentEpisode.getEpisodeId());
                    goal.setEpisodeName(currentEpisode.getEpisodeName());
                    goals.add(goal);
                    goalCaseInstance.setGoals(goals);
                    programName = currentEpisode.getProgramName();
                    program = currentEpisode.getProgramId();
                }
                goalCaseInstanceList.add(goalCaseInstance);
                goalResponse.setCaseInstances(goalCaseInstanceList);
            }
            goalResponse.setProgramId(program);
            goalResponse.setProgramName(programName);
            goalresult.add(goalResponse);
        }
    }

    @Override
    public Map<String, String> startGoal(GoalRequest goalRequest) {
        Map<String, String> result = new HashMap<>();
        for (String goalId : goalRequest.getGoalIdList()) {
            log.info("start goal for goalId :{}", goalId);
            Map<String, Object> map = new HashMap<>();
            map.put("value", true);
            map.put("type", "Boolean");
            String variable = Constants.LC + "_" + goalId + "_" + Constants.ACTIVATE;
            try {
                camundaClient.updateCaseInstanceVariable(goalRequest.getCaseInstanceId(), variable,
                        new JSONObject(map));
                result.put(goalId, "Success");
            } catch (Exception e) {
                result.put(goalId, "Fail");
            }
        }
        return result;
    }

    @Override
    public List<GoalResponse> getHistoryGoals(GoalRequest goalRequest) {
        log.info("get goals history for mrn: {} ", goalRequest.getMrn());
        
        goalRequest.setCreatedBefore(dateValidate(goalRequest.getCreatedBefore(), "createdBefore"));
        goalRequest.setCreatedAfter(dateValidate(goalRequest.getCreatedAfter(),"createdAfter"));
        goalRequest.setEndedBefore(dateValidate(goalRequest.getEndedBefore(),"endedBefore"));
        goalRequest.setEndedAfter(dateValidate(goalRequest.getEndedAfter(),"endedAfter"));
        
        List<CaseInstance> caseInstanceList;
        if (null == goalRequest.getProgramId() || goalRequest.getProgramId().isEmpty()) {
            caseInstanceList = camundaClient.getCaseInstance(goalRequest.getMrn());
        } else {
            String programId = Constants.CASE + "_" + goalRequest.getProgramId();
            caseInstanceList = camundaClient.getCaseInstance(goalRequest.getMrn(), programId);
        }
        Map<String, List<GoalResponse>> caseInstanceMap = new HashMap<>();
        Map<String, CaseActivityInstance> caseActivityInstanceMap = new HashMap<>();
        filterHistoryGolas(goalRequest,caseActivityInstanceMap,caseInstanceList,caseInstanceMap);        
        // map program to map of list of case instance, list of goal
        Map<String, Map<String, List<GoalResponse>>> pogramMap = new HashMap<>();        
        goalList(pogramMap, caseInstanceMap);
        // form final response
        List<GoalResponse> goalResult = new ArrayList<>();
        for (Entry<String, Map<String, List<GoalResponse>>> map : pogramMap.entrySet()) {
            String programName = "";
            GoalResponse goalResponse = new GoalResponse();
            goalResponse.setProgramId(map.getKey());
            List<GoalCaseInstance> goalCaseInstanceList = new ArrayList<>();
            for (Entry<String, List<GoalResponse>> entry : map.getValue().entrySet()) {
                String key = entry.getKey();
                List<GoalResponse> value = entry.getValue();
                List<Goal> goals = new ArrayList<>();
                GoalCaseInstance goalCaseInstance = new GoalCaseInstance();
                goalCaseInstance.setCaseInstanceId(key);
                for (GoalResponse currentGoal : value) {
                    Goal goal = new Goal();
                    goal.setId(currentGoal.getId());
                    goal.setName(currentGoal.getName());
                    goal.setDescription(currentGoal.getDescription());
                    goal.setEpisodeId(currentGoal.getEpisodeId());
                    goal.setEpisodeName(currentGoal.getEpisodeName());
                    goal.setActivityId(currentGoal.getActivityId());
                    goals.add(goal);
                    goalCaseInstance.setGoals(goals);
                    programName = currentGoal.getProgramName();
                }
                goalCaseInstanceList.add(goalCaseInstance);
                goalResponse.setCaseInstances(goalCaseInstanceList);
            }
            goalResponse.setProgramName(programName);
            goalResult.add(goalResponse);
        }
        return goalResult;
    }
    
    
    private String dateValidate(String isoDate, String paramName) {
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS[Z]");
        if (null != isoDate && !isoDate.isEmpty()) {
            OffsetDateTime date;
            try {
                date = OffsetDateTime.parse(isoDate);
            } catch (Exception e) {
                throw new CustomParameterizedException("Invalid "+paramName+" date format", paramName);
            }
            return dateTimeFormatter.format(date);
        }
        return null;
    }
   
    private void filterHistoryGolas(GoalRequest goalRequest,Map<String, CaseActivityInstance> caseActivityInstanceMap,List<CaseInstance> caseInstanceList,Map<String, List<GoalResponse>> caseInstanceMap){
        List<CaseActivityInstance> caseActivityInstanceList = null;
        for (CaseInstance caseInstance : caseInstanceList) {
            List<GoalResponse> goalResponseList = new ArrayList<>();
            List<CaseExecution> caseExecutionList = camundaClient.getCaseExecutionsByCaseInstance(goalRequest.getMrn(),
                    caseInstance.getId());
            Map<String, CaseExecution> caseExecutionMap = new HashMap<>();
            for (CaseExecution caseExecution : caseExecutionList) {
                caseExecutionMap.put(caseExecution.getId(), caseExecution);
            }
            caseActivityInstanceList = camundaClient.getCaseActivityInstance(Constants.COMPLETED, caseInstance.getId(),goalRequest.getCreatedBefore(),goalRequest.getCreatedAfter(),goalRequest.getEndedBefore(),goalRequest.getEndedAfter());
            historyFilterStage(caseActivityInstanceList,caseActivityInstanceMap,goalResponseList);            
            List<GoalResponse> goalResultList = new ArrayList<>();
            for (GoalResponse goal : goalResponseList) {
                // Retrieve goal and program from history
                CaseActivityInstance caseActivityInstance = caseActivityInstanceMap.get(goal.getEpisodeId());
                if (null != caseActivityInstance) {
                    goalsProgramFromHistory(caseActivityInstance,caseActivityInstanceMap,caseExecutionMap,goal,goalResultList);
                } else { // retrieve goal and program from case execution
                    CaseExecution caseExecution = caseExecutionMap.get(goal.getEpisodeId());
                    goalsProgramsFromCaseExecution(caseExecution,goal, goalResultList,caseExecutionMap);
                }
            }
            caseInstanceMap.put(caseInstance.getId(), goalResultList);
        }

    }
    
    private void historyFilterStage(List<CaseActivityInstance> caseActivityInstanceList,Map<String, CaseActivityInstance> caseActivityInstanceMap, List<GoalResponse> goalResponseList){
        for (CaseActivityInstance caseActivityInstance : caseActivityInstanceList) {
            caseActivityInstanceMap.put(caseActivityInstance.getId(), caseActivityInstance);
            // filter stage objects
            if (caseActivityInstance.getCaseActivityType().equalsIgnoreCase(Constants.STAGE)) {
                GoalResponse goalResponse = new GoalResponse();
                String activityId = caseActivityInstance.getCaseActivityId();
                if (activityId != null) {
                    String goalId = activityId.substring(activityId.indexOf('_') + 1, activityId.length());
                    goalResponse.setId(goalId);
                }
                goalResponse.setCaseInstanceId(caseActivityInstance.getCaseInstanceId());
                goalResponse.setName(caseActivityInstance.getCaseActivityName());
                goalResponse.setActivityId(activityId);
                goalResponse.setEpisodeId(caseActivityInstance.getParentCaseActivityInstanceId());
                goalResponseList.add(goalResponse);
            }
        }
    }
    
    private void goalsProgramFromHistory(CaseActivityInstance caseActivityInstance,Map<String, CaseActivityInstance> caseActivityInstanceMap,Map<String, CaseExecution> caseExecutionMap,GoalResponse goal,List<GoalResponse> goalResultList){
        if (caseActivityInstance.getCaseActivityType().equalsIgnoreCase(Constants.STAGE)) {

            CaseActivityInstance caseActivityInstanceParent = caseActivityInstanceMap
                    .get(caseActivityInstance.getParentCaseActivityInstanceId());
            goal.setEpisodeName(caseActivityInstance.getCaseActivityName());
            goal.setProgramId(caseActivityInstance.getParentCaseActivityInstanceId());
            if (null != caseActivityInstanceParent && caseActivityInstanceParent.getCaseActivityType()
                    .equalsIgnoreCase(Constants.CASEPLAN)) {
                goal.setProgramName(caseActivityInstanceParent.getCaseActivityName());
                goal.setProgramId(caseActivityInstanceParent.getCaseActivityId());
            } else {
                CaseExecution caseExecutionProgram = caseExecutionMap.get(goal.getProgramId());
                if (null != caseExecutionProgram
                        && caseExecutionProgram.getActivityType().equalsIgnoreCase(Constants.CASEPLAN)) {
                    goal.setProgramName(caseExecutionProgram.getActivityName());
                    goal.setProgramId(caseExecutionProgram.getActivityId());
                }
            }
            goalResultList.add(goal);
        }
    }
    
    private void goalsProgramsFromCaseExecution(CaseExecution caseExecution,GoalResponse goal, List<GoalResponse> goalResultList,Map<String, CaseExecution> caseExecutionMap){
        if ((null != caseExecution)
                && (caseExecution.getActivityType().equalsIgnoreCase(Constants.STAGE))) {
            goal.setEpisodeName(caseExecution.getActivityName());
            goal.setProgramId(caseExecution.getParentId());

            CaseExecution caseExecutionProgram = caseExecutionMap.get(goal.getProgramId());
            if ((null == goal.getProgramName()) && (goal.getProgramId() != null)
                    && (goal.getProgramId().equalsIgnoreCase(caseExecutionProgram.getId()))
                    && (caseExecutionProgram.getActivityType().equalsIgnoreCase(Constants.CASEPLAN))) {
                goal.setProgramName(caseExecutionProgram.getActivityName());
                goal.setProgramId(caseExecutionProgram.getActivityId());
            }
            goalResultList.add(goal);
        }
    }

}
