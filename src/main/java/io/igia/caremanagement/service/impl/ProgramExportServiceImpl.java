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
package io.igia.caremanagement.service.impl;

import java.util.List;
import java.util.Optional;
import org.camunda.bpm.model.cmmn.Cmmn;
import org.camunda.bpm.model.cmmn.CmmnModelInstance;
import org.camunda.bpm.model.cmmn.VariableTransition;
import org.camunda.bpm.model.cmmn.impl.CmmnModelConstants;
import org.camunda.bpm.model.cmmn.instance.Case;
import org.camunda.bpm.model.cmmn.instance.CasePlanModel;
import org.camunda.bpm.model.cmmn.instance.CmmnElement;
import org.camunda.bpm.model.cmmn.instance.CmmnModelElementInstance;
import org.camunda.bpm.model.cmmn.instance.ConditionExpression;
import org.camunda.bpm.model.cmmn.instance.DecisionTask;
import org.camunda.bpm.model.cmmn.instance.Definitions;
import org.camunda.bpm.model.cmmn.instance.Documentation;
import org.camunda.bpm.model.cmmn.instance.EntryCriterion;
import org.camunda.bpm.model.cmmn.instance.ExtensionElements;
import org.camunda.bpm.model.cmmn.instance.HumanTask;
import org.camunda.bpm.model.cmmn.instance.IfPart;
import org.camunda.bpm.model.cmmn.instance.ItemControl;
import org.camunda.bpm.model.cmmn.instance.PlanItem;
import org.camunda.bpm.model.cmmn.instance.PlanItemDefinition;
import org.camunda.bpm.model.cmmn.instance.RepetitionRule;
import org.camunda.bpm.model.cmmn.instance.Sentry;
import org.camunda.bpm.model.cmmn.instance.Stage;
import org.camunda.bpm.model.cmmn.instance.Task;
import org.camunda.bpm.model.cmmn.instance.camunda.CamundaCaseExecutionListener;
import org.camunda.bpm.model.cmmn.instance.camunda.CamundaScript;
import org.camunda.bpm.model.cmmn.instance.camunda.CamundaVariableOnPart;
import org.camunda.bpm.model.cmmn.instance.camunda.CamundaVariableTransitionEvent;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import io.igia.caremanagement.config.ApplicationProperties;
import io.igia.caremanagement.domain.enumeration.CaseExecutionEvent;
import io.igia.caremanagement.domain.enumeration.TimeUnit;
import io.igia.caremanagement.domain.enumeration.Type;
import io.igia.caremanagement.service.ProgramExportService;
import io.igia.caremanagement.service.dto.EpisodeAssociateDTO;
import io.igia.caremanagement.service.dto.EpisodeDTO;
import io.igia.caremanagement.service.dto.GoalAssociateDTO;
import io.igia.caremanagement.service.dto.GoalDTO;
import io.igia.caremanagement.service.dto.ProgramDTO;
import io.igia.caremanagement.service.dto.TaskAssociateDTO;
import io.igia.caremanagement.service.dto.TaskDTO;

/**
 * Service for importing program worksheet.
 */
@Service
@Transactional
public class ProgramExportServiceImpl extends EntityServices implements ProgramExportService {

    private static final Logger log = LoggerFactory.getLogger(ProgramExportServiceImpl.class);

    private static final String CMMN_EXPORTER_NAME = "CARE-MANAGEMENT-WORKFLOW";
    private static final String CMMN_EXPORTER_VERSION = "1.0.0";

    // Array indices for events
    private static final int COMPLETE_EVENT_INDEX = 0;
    private static final int CREATE_EVENT_INDEX = 1;
    private static final int START_EVENT_INDEX = 2;

    // 4 spaces for indentation
    private static final String SPACES = "    ";

    // Event names
    private static final String COMPLETE_EVENT_NAME = "complete";
    private static final String CREATE_EVENT_NAME = "create";
    private static final String START_EVENT_NAME = "start";

    // joda DateTime methods
    private static final String PLUS_DAYS = "plusDays";
    private static final String PLUS_WEEKS = "plusWeeks";
    private static final String PLUS_MONTHS = "plusMonths";
    private static final String PLUS_YEARS = "plusYears"; 

    private static final String UNDERSCORE = "_";
    private static final String OPEN_PARENTHESIS = "(";
    private static final String CLOSE_PARENTHESIS = ")";
    private static final String OPEN_CURLY = "{";
    private static final String CLOSE_CURLY = "}";
    private static final String NL = "\n";
    private static final String SQ = "'";
    private static final String COMMA = ",";
    private static final String SEMICOLON = ";";
    private static final String COLON = ":";
    private static final String SPACE = " ";
    private static final String EQUAL_TO = "==";
    private static final String NOT_EQUAL_TO = "!==";
    private static final String AND = "&&";
    private static final String TRUE = "true";
    private static final String CASE = "Case";
    private static final String VAR = "var";
    private static final String IF = "if";
    private static final String TYPEOF = "typeof";
    private static final String UNDEFINED = "undefined";
    private static final String CASE_UNDERSCORE = CASE + UNDERSCORE;
    private static final String CASE_PLAN_MODEL = "CasePlanModel";
    private static final String CASE_PLAN_MODEL_UNDERSCORE = CASE_PLAN_MODEL + UNDERSCORE;
    private static final String DOCUMENTATION = "Documentation";
    private static final String DOCUMENTATION_UNDERSCORE = DOCUMENTATION + UNDERSCORE;
    private static final String PLAN_ITEM = "PlanItem";
    private static final String PLAN_ITEM_UNDERSCORE = PLAN_ITEM + UNDERSCORE;
    private static final String ENTRY_CRITERION = "EntryCriterion";
    private static final String ENTRY_CRITERION_UNDERSCORE = ENTRY_CRITERION + UNDERSCORE;
    private static final String SENTRY = "Sentry";
    private static final String SENTRY_UNDERSCORE = SENTRY + UNDERSCORE;
    private static final String LC = "LC";
    private static final String LC_UNDERSCORE = LC + UNDERSCORE;
    private static final String ACTIVATE = "ACTIVATE";
    private static final String UNDERSCORE_ACTIVATE = UNDERSCORE + ACTIVATE;
    private static final String IF_PART = "IfPart";
    private static final String IF_PART_UNDERSCORE = IF_PART + UNDERSCORE;
    private static final String CONDITION_EXPRESSION = "ConditionExpression";
    private static final String CONDITION_EXPRESSION_UNDERSCORE = CONDITION_EXPRESSION + UNDERSCORE;
    private static final String IF_PART_UNDERSCORE_CONDITION_EXPRESSION_UNDERSCORE = IF_PART_UNDERSCORE + CONDITION_EXPRESSION_UNDERSCORE;
    private static final String STAGE = "Stage";
    private static final String STAGE_UNDERSCORE = STAGE + UNDERSCORE;
    private static final String START_DATE = "START_DATE";
    private static final String UNDERSCORE_START_DATE = UNDERSCORE + START_DATE;
    private static final String END_DATE = "END_DATE";
    private static final String UNDERSCORE_END_DATE = UNDERSCORE + END_DATE;
    private static final String DUE_DATE = "DUE_DATE";
    private static final String UNDERSCORE_DUE_DATE = UNDERSCORE + DUE_DATE;
    private static final String DOT = ".";
    private static final String UNDERSCORE_DUE_DATE_DOT = UNDERSCORE_DUE_DATE + DOT;
    private static final String CONTROL = "Control";
    private static final String PLAN_ITEM_CONTROL_UNDERSCORE = PLAN_ITEM + CONTROL + UNDERSCORE;
    private static final String REPETITION_RULE = "RepetitionRule";
    private static final String REPETITION_RULE_UNDERSCORE = REPETITION_RULE + UNDERSCORE;
    private static final String REPETITION_RULE_UNDERSCORE_CONDITION_EXPRESSION_UNDERSCORE = REPETITION_RULE_UNDERSCORE + CONDITION_EXPRESSION_UNDERSCORE;
    private static final String REPEAT = "REPEAT";
    private static final String UNDERSCORE_REPEAT = UNDERSCORE + REPEAT;
    private static final String HUMAN_TASK = "HumanTask";
    private static final String HUMAN_TASK_UNDERSCORE = HUMAN_TASK + UNDERSCORE;
    private static final String DECISION_TASK = "DecisionTask";
    private static final String DECISION_TASK_UNDERSCORE = DECISION_TASK + UNDERSCORE;
    private static final String DR = "DR";
    private static final String UNDERSCORE_DR = UNDERSCORE + DR;
    private static final String SINGLE_RESULT = "singleResult";
    private static final String SPACE_EQUAL_TO_SPACE_TRUE = SPACE + EQUAL_TO + SPACE + TRUE;
    private static final String CLOSE_PARENTHESIS_SPACE_AND_SPACE_OPEN_PARENTHESIS = CLOSE_PARENTHESIS + SPACE + AND + SPACE + OPEN_PARENTHESIS;
    private static final String CASE_EXEC_DOT_SET_VAR_OPEN_PARENTHESIS_SQ = "caseExecution" + DOT + "setVariable" + OPEN_PARENTHESIS + SQ;
    private static final String NL_CASE_EXEC_DOT_SET_VAR_OPEN_PARENTHESIS_SQ = NL + CASE_EXEC_DOT_SET_VAR_OPEN_PARENTHESIS_SQ;
    private static final String SQ_COMMA_SPACE_TRUE_CLOSE_PARENTHESIS_SEMICOLON = SQ + COMMA + SPACE + TRUE + CLOSE_PARENTHESIS + SEMICOLON;
    private static final String SQ_COMMA_SPACE_TRUE_CLOSE_PARENTHESIS_SEMICOLON_NL_CLOSE_CURLY = SQ_COMMA_SPACE_TRUE_CLOSE_PARENTHESIS_SEMICOLON + NL + CLOSE_CURLY;
    private static final String CLOSE_PARENTHESIS_SPACE_OPEN_CURLY_NL = CLOSE_PARENTHESIS + SPACE + OPEN_CURLY + NL;
    private static final String NL_IF_SPACE_OPEN_PARENTHESIS = NL + IF + SPACE + OPEN_PARENTHESIS;
    private static final String TYPEOF_SPACE = TYPEOF + SPACE;
    private static final String NAME_COLON_SPACE_PARAM = "name" + COLON + SPACE + OPEN_CURLY + CLOSE_CURLY;
    private static final String ID_COLON_SPACE_PARAM = "id" + COLON + SPACE + OPEN_CURLY + CLOSE_CURLY;
    private static final String ID_COLON_SPACE_PARAM_SPACE_NAME_COLON_SPACE_PARAM = ID_COLON_SPACE_PARAM + SPACE + NAME_COLON_SPACE_PARAM;
    private static final String SPACE_NOT_EQUAL_TO_SPACE_SQ_UNDEFINED_SQ_SPACE_AND_SPACE = SPACE + NOT_EQUAL_TO + SPACE + SQ + UNDEFINED + SQ + SPACE + AND + SPACE;
    private static final String SPACE_NOT_EQUAL_TO_SPACE_SQ_UNDEFINED_SQ_CLOSE_PARENTHESIS_SPACE_OPEN_CURLY_NL = SPACE + NOT_EQUAL_TO + SPACE + SQ + UNDEFINED + SQ + CLOSE_PARENTHESIS + SPACE + OPEN_CURLY + NL;
    private static final String NL_IF_SPACE_OPEN_PARENTHESIS_TYPEOF_SPACE = NL + IF + SPACE + OPEN_PARENTHESIS + TYPEOF + SPACE;
    private static final String CASE_EXEC_DOT_REMOVE_VAR_OPEN_PARENTHESIS_SQ = "caseExecution" + DOT + "removeVariable" + OPEN_PARENTHESIS + SQ;
    private static final String SQ_CLOSE_PARENTHESIS_SEMICOLON_NL_CLOSE_CURLY = SQ + CLOSE_PARENTHESIS + SEMICOLON + NL + CLOSE_CURLY;
    private static final String NL_VAR_SPACE = NL + VAR + SPACE;
    private static final String SPACE_NAME_COLON_SPACE = SPACE + "name" + COLON + SPACE; 
    private static final String COMMA_SPACE_NAME_COLON_SPACE = COMMA + SPACE_NAME_COLON_SPACE; 

    @Autowired
    ApplicationProperties applicationProperties;

    @Override
    public CmmnModelInstance createProgram(String id) {
        CmmnModelInstance modelInstance = createEmptyModel();

        Optional<ProgramDTO> optionalProgramDTO = programService.findOneByProgramIdU(id);
        if (!optionalProgramDTO.isPresent()) {
            log.error("error creating a program, did not find program id: {}", id);
            throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY, "Error creating a program, did not find program id: " +  id);
        }
        ProgramDTO programDTO = optionalProgramDTO.get();

        // create program (case and case plan model)
        
        // create case
        Case caseElement = createElement(modelInstance, modelInstance.getDefinitions(), CASE_UNDERSCORE + programDTO.getProgramIdU(), Case.class);
        caseElement.setName(programDTO.getName());
        
        // create  case plan model
        CasePlanModel casePlanModel = createElement(modelInstance, caseElement, CASE_PLAN_MODEL_UNDERSCORE + programDTO.getProgramIdU(), CasePlanModel.class);
        casePlanModel.setName(programDTO.getName());
        
        // create documentation
        createDocumentation(modelInstance, casePlanModel, programDTO.getDescription(), programDTO.getProgramIdU());
        
        // create episodes
        createEpisodes(modelInstance, casePlanModel, programDTO);

        return modelInstance;
    }

    private void createEpisodes(CmmnModelInstance modelInstance, CasePlanModel casePlanModel, ProgramDTO programDTO) {
        // create episodes
        
        // get all episodes by program id and create a plan item for each episode
        List<EpisodeDTO> episodeDTOs = episodeService.findAllByProgramId(programDTO.getId());

        if (episodeDTOs == null || episodeDTOs.isEmpty())
            return;

        for (EpisodeDTO episodeDTO : episodeDTOs) {
            // create plan item
            PlanItem planItem = createElement(modelInstance, casePlanModel, PLAN_ITEM_UNDERSCORE + programDTO.getProgramIdU() + UNDERSCORE + episodeDTO.getEpisodeIdU(), PlanItem.class);
            
            // get episode assoc by id
            List<EpisodeAssociateDTO> episodeAssociateDTOsById = episodeAssocService.findAllByEpisodeId(episodeDTO.getId());

            // create documentation
            String id = programDTO.getProgramIdU() + UNDERSCORE + episodeDTO.getEpisodeIdU();
            createDocumentation(modelInstance, planItem, episodeDTO.getDescription(), id);

            String entryCriteria = episodeDTO.getEntryCriteria();
            boolean isAssociations = episodeAssociateDTOsById != null && !episodeAssociateDTOsById.isEmpty();

            // create entry criteria if entry criteria is defined and/or associations are defined
            createEntryCriteria(modelInstance, planItem, casePlanModel, entryCriteria, isAssociations, false, id);

            // create stage (episode)
            String stageId = STAGE_UNDERSCORE + programDTO.getProgramIdU() + UNDERSCORE + episodeDTO.getEpisodeIdU();
            Stage stage = createElement(modelInstance, casePlanModel, stageId, Stage.class);
            stage.setName(episodeDTO.getName());
            stage.setAutoComplete(true);

            planItem.setDefinition(stage);
            
            // get episode assoc by assoc on
            List<EpisodeAssociateDTO> episodeAssociateDTOsByAssocOn = episodeAssocService.findAllByAssociateOn(episodeDTO.getId());

            // we create array size by last event index + 1
            StringBuilder[] scriptContents = new StringBuilder[START_EVENT_INDEX + 1];
            
            // initialize array
            for (int i = 0; i < START_EVENT_INDEX + 1; i++)
                scriptContents[i] = new StringBuilder();

            // creates script variables to take care of episode associations
            createEpisodeScriptToCreateVariables(episodeAssociateDTOsByAssocOn, scriptContents, programDTO, episodeDTO);

            // create script content to remove variables
            createEpisodeScriptToRemoveVariables(episodeAssociateDTOsById, scriptContents, programDTO, episodeDTO);

            // create execution listeners for supported events
            createExecutionListeners(modelInstance, stage, scriptContents);

            createGoals(modelInstance, stage, programDTO, episodeDTO);
        }
    }

    /*
     * check if we need to create case execution listener script to create variables
     * if associations in the episode assoc when queried by assoc on
     * if associations in the episode assoc when queried by id
     */
    private void createEpisodeScriptToCreateVariables(List<EpisodeAssociateDTO> episodeAssociateDTOsByAssocOn,
            StringBuilder[] scriptContents, ProgramDTO programDTO, EpisodeDTO episodeDTO) {
        if (episodeAssociateDTOsByAssocOn != null && !episodeAssociateDTOsByAssocOn.isEmpty()) {
            // create script content to create variables
            for (EpisodeAssociateDTO episodeAssociateDTO : episodeAssociateDTOsByAssocOn) {
                Optional<EpisodeDTO> optionalEpisodeDTOAssocTo = episodeService.findOne(episodeAssociateDTO.getEpisodeId());
                if (!optionalEpisodeDTOAssocTo.isPresent()) {
                    log.warn("Skipping script statement to create case execution variable as did not find episode id {} association to, from episode id {} episode idU {}",
                            episodeAssociateDTO.getEpisodeId(), episodeDTO.getId(), episodeDTO.getEpisodeIdU());
                    continue;
                }
                EpisodeDTO episodeDTOAssocTo = optionalEpisodeDTOAssocTo.get(); 
                StringBuilder scriptContent = getScriptObj4AssocEvent(scriptContents, episodeAssociateDTO.getAssociateEvent(), "episode", episodeDTOAssocTo.getEpisodeIdU(), episodeDTO.getEpisodeIdU());
                if (scriptContent != null) {
                    String variable = LC_UNDERSCORE + programDTO.getProgramIdU() + UNDERSCORE + episodeDTO.getEpisodeIdU() + UNDERSCORE + episodeDTOAssocTo.getEpisodeIdU()  + UNDERSCORE_ACTIVATE;
                    scriptContent.append(NL_CASE_EXEC_DOT_SET_VAR_OPEN_PARENTHESIS_SQ + variable + SQ_COMMA_SPACE_TRUE_CLOSE_PARENTHESIS_SEMICOLON);
                    
                    // checks if multi association condition is satisfied and creates variable to trigger event for associated episode
                    createEpisodeScriptToChkMultiAssocCondAndCreateVars(episodeAssociateDTO.getEpisodeId(), scriptContent, programDTO, episodeDTOAssocTo);
                }
            }
        }
    }

    /*
     * create a script to check all pre conditions
     * and define a variable if all pre conditions are satisfied
     * variables used in juel expressions must be defined before evaluation
     */
    private void createEpisodeScriptToChkMultiAssocCondAndCreateVars(Long episodeIdAssocTo, StringBuilder scriptContent, ProgramDTO programDTO, EpisodeDTO episodeDTOAssocTo) {
        
        // get episode assoc by to episode id
        List<EpisodeAssociateDTO> episodeAssociateDTOsByToEpisodeId = episodeAssocService.findAllByEpisodeId(episodeIdAssocTo);
        if (episodeAssociateDTOsByToEpisodeId != null && !episodeAssociateDTOsByToEpisodeId.isEmpty()) {
            scriptContent.append(NL_IF_SPACE_OPEN_PARENTHESIS);
            boolean op = false;
            for (EpisodeAssociateDTO toEpisodeAssociateDTO : episodeAssociateDTOsByToEpisodeId) {
                Optional<EpisodeDTO> optionalFromEpisodeDTO = episodeService.findOne(toEpisodeAssociateDTO.getAssociateOn());
                if (optionalFromEpisodeDTO.isPresent()) {
                    EpisodeDTO fromEpisodeDTO = optionalFromEpisodeDTO.get();
                    if (op)
                        scriptContent.append(" && ");
                    else
                        op = true;
                    String variable = LC_UNDERSCORE + programDTO.getProgramIdU() + UNDERSCORE + fromEpisodeDTO.getEpisodeIdU() + UNDERSCORE + episodeDTOAssocTo.getEpisodeIdU() + UNDERSCORE_ACTIVATE;
                    scriptContent.append(TYPEOF_SPACE + variable + SPACE_NOT_EQUAL_TO_SPACE_SQ_UNDEFINED_SQ_SPACE_AND_SPACE + variable + SPACE_EQUAL_TO_SPACE_TRUE);
                } else {
                    log.error("Association from episode id {} is missing in episode", toEpisodeAssociateDTO.getAssociateOn());
                    throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY, "Association from episode id: " + toEpisodeAssociateDTO.getAssociateOn() + ", is missing in episode");
                }
            }
            scriptContent.append(CLOSE_PARENTHESIS_SPACE_OPEN_CURLY_NL);
            String variable = LC_UNDERSCORE + programDTO.getProgramIdU() + UNDERSCORE + episodeDTOAssocTo.getEpisodeIdU() + UNDERSCORE_ACTIVATE;
            scriptContent.append(SPACES + CASE_EXEC_DOT_SET_VAR_OPEN_PARENTHESIS_SQ + variable + SQ_COMMA_SPACE_TRUE_CLOSE_PARENTHESIS_SEMICOLON_NL_CLOSE_CURLY);
        }
    }

    // create script content to remove variables
    private void createEpisodeScriptToRemoveVariables(List<EpisodeAssociateDTO> episodeAssociateDTOsById, StringBuilder[] scriptContents, ProgramDTO programDTO, EpisodeDTO episodeDTO) {
        if (episodeAssociateDTOsById != null && !episodeAssociateDTOsById.isEmpty()) {
            // remove variable episode id
            StringBuilder scriptContent = scriptContents[COMPLETE_EVENT_INDEX];
            if (scriptContent != null) {
                String variable = LC_UNDERSCORE + programDTO.getProgramIdU() + UNDERSCORE +  episodeDTO.getEpisodeIdU()  + UNDERSCORE_ACTIVATE;
                scriptContent.append(NL_IF_SPACE_OPEN_PARENTHESIS_TYPEOF_SPACE + variable + SPACE_NOT_EQUAL_TO_SPACE_SQ_UNDEFINED_SQ_CLOSE_PARENTHESIS_SPACE_OPEN_CURLY_NL);
                scriptContent.append(SPACES + CASE_EXEC_DOT_REMOVE_VAR_OPEN_PARENTHESIS_SQ + variable + SQ_CLOSE_PARENTHESIS_SEMICOLON_NL_CLOSE_CURLY);
            }

            for (EpisodeAssociateDTO episodeAssociateDTO : episodeAssociateDTOsById) {
                Optional<EpisodeDTO> optionalEpisodeDTOAssocFrom = episodeService.findOne(episodeAssociateDTO.getAssociateOn());
                if (!optionalEpisodeDTOAssocFrom.isPresent()) {
                    log.warn("Skipping script statement to remove case execution variable as did not find episode id {} association from, to episode id {} episode idU {}",
                            episodeAssociateDTO.getAssociateOn(), episodeDTO.getId(), episodeDTO.getEpisodeIdU());
                    continue;
                }
                EpisodeDTO episodeDTOAssocFrom = optionalEpisodeDTOAssocFrom.get();
                scriptContent = getScriptObj4AssocEvent(scriptContents, episodeAssociateDTO.getAssociateEvent(), "episode", episodeDTO.getEpisodeIdU(), episodeDTOAssocFrom.getEpisodeIdU());
                if (scriptContent != null) {
                    String variable = LC_UNDERSCORE + programDTO.getProgramIdU() + UNDERSCORE + episodeDTOAssocFrom.getEpisodeIdU() + UNDERSCORE + episodeDTO.getEpisodeIdU()  + UNDERSCORE_ACTIVATE;
                    scriptContent.append(NL_IF_SPACE_OPEN_PARENTHESIS_TYPEOF_SPACE + variable + SPACE_NOT_EQUAL_TO_SPACE_SQ_UNDEFINED_SQ_CLOSE_PARENTHESIS_SPACE_OPEN_CURLY_NL);
                    scriptContent.append(SPACES + CASE_EXEC_DOT_REMOVE_VAR_OPEN_PARENTHESIS_SQ + variable + SQ_CLOSE_PARENTHESIS_SEMICOLON_NL_CLOSE_CURLY);
                }
            }
        }
    }

    private void createGoals(CmmnModelInstance modelInstance, Stage stageEpisode, ProgramDTO programDTO,
            EpisodeDTO episodeDTO) {
        // create goals
        
        // get all goals by episode id and create a plan item for each goal
        List<GoalDTO> goalDTOs = goalService.findAllByEpisodeId(episodeDTO.getId());

        if (goalDTOs == null || goalDTOs.isEmpty())
            return;

        for (GoalDTO goalDTO : goalDTOs) {
            String planItemId = PLAN_ITEM_UNDERSCORE + programDTO.getProgramIdU() + UNDERSCORE + episodeDTO.getEpisodeIdU() + UNDERSCORE + goalDTO.getGoalIdU();
                    
            // create plan item
            PlanItem planItem = createElement(modelInstance, stageEpisode, planItemId, PlanItem.class);
            
            // get goal assoc by id
            List<GoalAssociateDTO> goalAssociateDTOsById = goalAssocService.findAllByGoalId(goalDTO.getId());

            // create documentation
            String id = programDTO.getProgramIdU() + UNDERSCORE + episodeDTO.getEpisodeIdU() + UNDERSCORE + goalDTO.getGoalIdU();
            createDocumentation(modelInstance, planItem, goalDTO.getDescription(), id);

            String entryCriteria = goalDTO.getEntryCriteria();
            boolean isAssociations = goalAssociateDTOsById != null && !goalAssociateDTOsById.isEmpty();

            // create entry criteria if entry criteria is defined and/or associations are defined
            createEntryCriteria(modelInstance, planItem, stageEpisode, entryCriteria, isAssociations, false, id);

            // create stage (goal)
            String stageId = STAGE_UNDERSCORE + programDTO.getProgramIdU() + UNDERSCORE + episodeDTO.getEpisodeIdU() + UNDERSCORE + goalDTO.getGoalIdU();
            Stage stage = createElement(modelInstance, stageEpisode, stageId, Stage.class);
            stage.setName(goalDTO.getName());
            stage.setAutoComplete(true);

            planItem.setDefinition(stage);
            
            // get goal assoc by assoc on
            List<GoalAssociateDTO> goalAssociateDTOsByAssocOn = goalAssocService.findAllByAssociateOn(goalDTO.getId());

            // we create array size by last event index + 1
            StringBuilder[] scriptContents = new StringBuilder[START_EVENT_INDEX + 1];
            
            // initialize array
            for (int i = 0; i < START_EVENT_INDEX + 1; i++)
                scriptContents[i] = new StringBuilder();

            // create script variables to take care of goal associations
            createGoalScriptToCreateVariables(goalAssociateDTOsByAssocOn, scriptContents, programDTO, episodeDTO, goalDTO);
            
            // create script content to remove variables
            createGoalScriptToRemoveVariables(goalAssociateDTOsById, scriptContents, programDTO, episodeDTO, goalDTO);

            // create script content to define goal ETA
            createGoalScriptToDefineGoalETA(scriptContents, programDTO, episodeDTO, goalDTO);
            
            // create script content to define task due date
            createGoalScriptToDefineTaskDueDate(scriptContents, programDTO, episodeDTO, goalDTO);
            
            // create execution listeners for supported events
            createExecutionListeners(modelInstance, stage, scriptContents);

            createTasks(modelInstance, stage, programDTO, episodeDTO, goalDTO);
        }
    }


    /*
     * check if we need to create case execution listener script to create variables
     * if associations in the goal assoc when queried by assoc on
     * if associations in the goal assoc when queried by id
     */
    private void createGoalScriptToCreateVariables(List<GoalAssociateDTO> goalAssociateDTOsByAssocOn,
            StringBuilder[] scriptContents, ProgramDTO programDTO, EpisodeDTO episodeDTO, GoalDTO goalDTO) {
        if (goalAssociateDTOsByAssocOn != null && !goalAssociateDTOsByAssocOn.isEmpty()) {
            // create script content to create variables
            for (GoalAssociateDTO goalAssociateDTO : goalAssociateDTOsByAssocOn) {
                Optional<GoalDTO> optinalGoalDTOAssocTo = goalService.findOne(goalAssociateDTO.getGoalId());
                if(!optinalGoalDTOAssocTo.isPresent()) {
                    log.warn("Skipping script statement to create case execution variable as did not find goal id {} association to, from goal id {} goal idU {}",
                            goalAssociateDTO.getGoalId(), goalDTO.getId(), goalDTO.getGoalIdU());
                    continue;
                }
                GoalDTO goalDTOAssocTo = optinalGoalDTOAssocTo.get();
                StringBuilder scriptContent = getScriptObj4AssocEvent(scriptContents, goalAssociateDTO.getAssociateEvent(), "goal", goalDTOAssocTo.getGoalIdU(), goalDTO.getGoalIdU());
                if (scriptContent != null) {
                    String variable = LC_UNDERSCORE + programDTO.getProgramIdU() + UNDERSCORE + episodeDTO.getEpisodeIdU() + UNDERSCORE + goalDTO.getGoalIdU() + UNDERSCORE + goalDTOAssocTo.getGoalIdU()  + UNDERSCORE_ACTIVATE;
                    scriptContent.append(NL_CASE_EXEC_DOT_SET_VAR_OPEN_PARENTHESIS_SQ + variable + SQ_COMMA_SPACE_TRUE_CLOSE_PARENTHESIS_SEMICOLON);

                    // checks if multi association condition is satisfied and creates variable to trigger event for associated goal
                    createGoalScriptToChkMultiAssocCondAndCreateVars(goalAssociateDTO.getGoalId(), scriptContent, programDTO, episodeDTO, goalDTOAssocTo);
                }
            }
        }
    }

    /*
     * create a script to check all pre conditions
     * and define a variable if all pre conditions are satisfied
     * variables used in juel expression must be defined before evaluation 
     */
    private void createGoalScriptToChkMultiAssocCondAndCreateVars(Long goalIdAssocTo, StringBuilder scriptContent,
            ProgramDTO programDTO, EpisodeDTO episodeDTO, GoalDTO goalDTOAssocTo) {
        // get goal assoc by to goal id
        List<GoalAssociateDTO> goalAssociateDTOsByToGoalId = goalAssocService.findAllByGoalId(goalIdAssocTo);
        if (goalAssociateDTOsByToGoalId != null && !goalAssociateDTOsByToGoalId.isEmpty()) {
            scriptContent.append(NL_IF_SPACE_OPEN_PARENTHESIS);
            boolean op = false;
            for (GoalAssociateDTO toGoalAssociateDTO : goalAssociateDTOsByToGoalId) {
                Optional<GoalDTO> optionalFromGoalDTO = goalService.findOne(toGoalAssociateDTO.getAssociateOn());
                if (optionalFromGoalDTO.isPresent()) {
                    GoalDTO fromGoalDTO = optionalFromGoalDTO.get();
                    if (op)
                        scriptContent.append(" && ");
                    else
                        op = true;
                    String variable = LC_UNDERSCORE + programDTO.getProgramIdU() + UNDERSCORE + episodeDTO.getEpisodeIdU() + UNDERSCORE + fromGoalDTO.getGoalIdU() + UNDERSCORE + goalDTOAssocTo.getGoalIdU() + UNDERSCORE_ACTIVATE;
                    scriptContent.append(TYPEOF_SPACE + variable + SPACE_NOT_EQUAL_TO_SPACE_SQ_UNDEFINED_SQ_SPACE_AND_SPACE + variable + SPACE_EQUAL_TO_SPACE_TRUE);
                } else {
                    log.error("Association from goal id {} is missing in goal", toGoalAssociateDTO.getAssociateOn());
                    throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY, "Association from goal id: " + toGoalAssociateDTO.getAssociateOn() + " is missing in goal");
                }
            }
            scriptContent.append(CLOSE_PARENTHESIS_SPACE_OPEN_CURLY_NL);
            String variable = LC_UNDERSCORE + programDTO.getProgramIdU() + UNDERSCORE + episodeDTO.getEpisodeIdU() + UNDERSCORE + goalDTOAssocTo.getGoalIdU() + UNDERSCORE_ACTIVATE;
            scriptContent.append(SPACES + CASE_EXEC_DOT_SET_VAR_OPEN_PARENTHESIS_SQ + variable + SQ_COMMA_SPACE_TRUE_CLOSE_PARENTHESIS_SEMICOLON_NL_CLOSE_CURLY);
        }
    }

    /*
     * create script to define due date for human tasks
     * we define due date for human tasks
     * we define goal start date as the task due date
     * we can not define due date for human tasks
     * if it is already defined
     */
    private void createGoalScriptToDefineTaskDueDate(StringBuilder[] scriptContents, ProgramDTO programDTO,
            EpisodeDTO episodeDTO, GoalDTO goalDTO) {
        
        // get all the tasks 
        // it is better to query tasks by goal id and type human
        List<TaskDTO> tasks = taskService.findAllByGoalId(goalDTO.getId());
        
        if (tasks != null && !tasks.isEmpty()) {
            String variablePartGoal = LC_UNDERSCORE + programDTO.getProgramIdU() + UNDERSCORE +  episodeDTO.getEpisodeIdU() + UNDERSCORE + goalDTO.getGoalIdU();
            for (TaskDTO task : tasks) {
                // get assoc on by task id
                if (task.getType() == Type.HUMAN && task.getDueDate() == null) {
                    StringBuilder scriptContent = scriptContents[START_EVENT_INDEX];
                    if (scriptContent != null) {
                        String variablePartTask = LC_UNDERSCORE + programDTO.getProgramIdU() + UNDERSCORE +  episodeDTO.getEpisodeIdU() + UNDERSCORE + goalDTO.getGoalIdU() +
                                UNDERSCORE + task.getTaskIdU();
                        scriptContent.append(NL_CASE_EXEC_DOT_SET_VAR_OPEN_PARENTHESIS_SQ + variablePartTask + UNDERSCORE_DUE_DATE + "', " + variablePartGoal + UNDERSCORE_START_DATE + ");");
                    }
                }
            }
        }
    }

    /*
     * create a script to define goal ETA
     * create variables that hold goal start and end date
     * joda DateTime and ISO
     */
    private void createGoalScriptToDefineGoalETA(StringBuilder[] scriptContents, ProgramDTO programDTO, EpisodeDTO episodeDTO, GoalDTO goalDTO) {
        if (goalDTO.getEtaUnit() != null) {
            try {
                String addDateTime = getJodaMethodToAddDateTime(goalDTO.getEtaValue(), goalDTO.getEtaUnit());
                StringBuilder scriptContent = scriptContents[START_EVENT_INDEX];
                if (scriptContent != null) {
                    scriptContent.append("\n// Load compatibility script");
                    scriptContent.append("\nload(\"nashorn:mozilla_compat.js\");");
                    scriptContent.append("\n// Import the org.joda.time package");
                    scriptContent.append("\nimportPackage(org.joda.time);");
                    scriptContent.append("\n// Import the org.joda.time.DateTime class");
                    scriptContent.append("\nimportClass(org.joda.time.DateTime);");
                    scriptContent.append("\n// Create a new DateTime object");
                    String variablePart = LC_UNDERSCORE + programDTO.getProgramIdU() + UNDERSCORE +  episodeDTO.getEpisodeIdU() + UNDERSCORE + goalDTO.getGoalIdU();
                    scriptContent.append(NL_VAR_SPACE + variablePart + UNDERSCORE_START_DATE + " = new org.joda.time.DateTime();");
                    scriptContent.append(NL_VAR_SPACE + variablePart + UNDERSCORE_END_DATE + " = " + variablePart + UNDERSCORE_START_DATE + DOT + addDateTime + ";");
                    scriptContent.append(NL_CASE_EXEC_DOT_SET_VAR_OPEN_PARENTHESIS_SQ + variablePart + UNDERSCORE_START_DATE + "', " + variablePart + UNDERSCORE_START_DATE + ");");
                    scriptContent.append(NL_CASE_EXEC_DOT_SET_VAR_OPEN_PARENTHESIS_SQ + variablePart + UNDERSCORE_END_DATE + "', " + variablePart + UNDERSCORE_END_DATE + ");");
                }
            } catch (ResponseStatusException e) {
                log.error("Invalid ETA Value: {} Unit: {} for Goal Id: {}", goalDTO.getEtaValue(), goalDTO.getEtaUnit(), goalDTO.getGoalIdU());
                throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY, e.getMessage() + ", invalid ETA for Goal Id: " + goalDTO.getGoalIdU());
            }
        }
    }

    private void createGoalScriptToRemoveVariables(List<GoalAssociateDTO> goalAssociateDTOsById,
            StringBuilder[] scriptContents, ProgramDTO programDTO, EpisodeDTO episodeDTO, GoalDTO goalDTO) {
        // create script content to remove variables
        if (goalAssociateDTOsById != null && !goalAssociateDTOsById.isEmpty()) {
            // remove variable goal id
            StringBuilder scriptContent = scriptContents[COMPLETE_EVENT_INDEX];
            if (scriptContent != null) {
                String variable = LC_UNDERSCORE + programDTO.getProgramIdU() + UNDERSCORE +  episodeDTO.getEpisodeIdU() + UNDERSCORE + goalDTO.getGoalIdU()  + UNDERSCORE_ACTIVATE;
                scriptContent.append(NL_IF_SPACE_OPEN_PARENTHESIS_TYPEOF_SPACE + variable + SPACE_NOT_EQUAL_TO_SPACE_SQ_UNDEFINED_SQ_CLOSE_PARENTHESIS_SPACE_OPEN_CURLY_NL);
                scriptContent.append(SPACES + CASE_EXEC_DOT_REMOVE_VAR_OPEN_PARENTHESIS_SQ + variable + SQ_CLOSE_PARENTHESIS_SEMICOLON_NL_CLOSE_CURLY);
            }

            for (GoalAssociateDTO goalAssociateDTO : goalAssociateDTOsById) {
                Optional<GoalDTO> optionalGoalDTOAssocFrom = goalService.findOne(goalAssociateDTO.getAssociateOn());
                if(!optionalGoalDTOAssocFrom.isPresent()) {
                    log.warn("Skipping script statement to remove case execution variable as did not find goal id {} association from, to goal id {} goal idU {}",
                            goalAssociateDTO.getAssociateOn(), goalDTO.getId(), goalDTO.getGoalIdU());
                    continue;
                }
                GoalDTO goalDTOAssocFrom = optionalGoalDTOAssocFrom.get();
                scriptContent = getScriptObj4AssocEvent(scriptContents, goalAssociateDTO.getAssociateEvent(), "goal", goalDTO.getGoalIdU(), goalDTOAssocFrom.getGoalIdU());
                if (scriptContent != null) {
                    String variable = LC_UNDERSCORE + programDTO.getProgramIdU() + UNDERSCORE + episodeDTO.getEpisodeIdU() + UNDERSCORE + goalDTOAssocFrom.getGoalIdU() + UNDERSCORE + goalDTO.getGoalIdU()  + UNDERSCORE_ACTIVATE;
                    scriptContent.append(NL_IF_SPACE_OPEN_PARENTHESIS_TYPEOF_SPACE + variable + SPACE_NOT_EQUAL_TO_SPACE_SQ_UNDEFINED_SQ_CLOSE_PARENTHESIS_SPACE_OPEN_CURLY_NL);
                    scriptContent.append(SPACES + CASE_EXEC_DOT_REMOVE_VAR_OPEN_PARENTHESIS_SQ + variable + SQ_CLOSE_PARENTHESIS_SEMICOLON_NL_CLOSE_CURLY);
                }
            }
        }
    }

    private void createTasks(CmmnModelInstance modelInstance, Stage stageGoal, ProgramDTO programDTO, EpisodeDTO episodeDTO,
            GoalDTO goalDTO) {
        // create tasks
        
        // get all tasks by goal id and create a plan item for each task
        List<TaskDTO> taskDTOs = taskService.findAllByGoalId(goalDTO.getId());

        if (taskDTOs == null || taskDTOs.isEmpty())
            return;

        for (TaskDTO taskDTO : taskDTOs) {
            String planItemId = PLAN_ITEM_UNDERSCORE + programDTO.getProgramIdU() + UNDERSCORE + episodeDTO.getEpisodeIdU() + UNDERSCORE + goalDTO.getGoalIdU() +
                    UNDERSCORE + taskDTO.getTaskIdU();
                    
            // create plan item
            PlanItem planItem = createElement(modelInstance, stageGoal, planItemId, PlanItem.class);
            
            // get task assoc by id
            List<TaskAssociateDTO> taskAssociateDTOsById = taskAssocService.findAllByTaskId(taskDTO.getId());

            // create documentation
            String id = programDTO.getProgramIdU() + UNDERSCORE + episodeDTO.getEpisodeIdU() + UNDERSCORE + goalDTO.getGoalIdU() + UNDERSCORE + taskDTO.getTaskIdU();
            createDocumentation(modelInstance, planItem, taskDTO.getDescription(), id);

            // create task repetition rule
            createTaskRepetitionRule(modelInstance, planItem, programDTO, episodeDTO, goalDTO, taskDTO);

            String entryCriteria = taskDTO.getEntryCriteria();
            boolean isAssociations = taskAssociateDTOsById != null && !taskAssociateDTOsById.isEmpty();
            boolean isSOSTask = taskDTO.getRepeatFrequencyUnit() != null && taskDTO.getRepeatFrequencyUnit() == TimeUnit.SOS;
            
            // create entry criteria if entry criteria is defined and/or associations are defined
            createEntryCriteria(modelInstance, planItem, stageGoal, entryCriteria, isAssociations, isSOSTask, id);

            // create task
            Task task = createTask(modelInstance, planItem, stageGoal, programDTO, episodeDTO, goalDTO, taskDTO);
            
            // get task assoc by assoc on
            List<TaskAssociateDTO> taskAssociateDTOsByAssocOn = taskAssocService.findAllByAssociateOn(taskDTO.getId());

            // we create array size by last event index + 1
            StringBuilder[] scriptContents = new StringBuilder[START_EVENT_INDEX + 1];
            
            // initialize array
            for (int i = 0; i < START_EVENT_INDEX + 1; i++) {
                scriptContents[i] = new StringBuilder();
            }
            
            // create script variables to take care of task associations
            createTaskScriptToCreateVariables(taskAssociateDTOsByAssocOn, scriptContents, programDTO, episodeDTO, goalDTO, taskDTO);

            // create script content to remove variables
            createTaskScriptToRemoveVariables(taskAssociateDTOsById, scriptContents, programDTO, episodeDTO, goalDTO, taskDTO);

            // create script content to define task due date
            createTaskScriptToDefineTaskDueDate(scriptContents, programDTO, episodeDTO, goalDTO, taskDTO);
            
            // create script content to define task repeat variable
            createTaskScriptToDefineTaskRepeatVariable(scriptContents, programDTO, episodeDTO, goalDTO, taskDTO);

            // create script content to create decision output variables
            createTaskScriptToCreateDecisionOutputVariables(scriptContents, programDTO, episodeDTO, goalDTO, taskDTO);
            
            // create execution listeners for supported events
            createExecutionListeners(modelInstance, task, scriptContents);
        }
    }

    /*
     * check if we need to create item control
     * repetition rule
     * we repeat the task if computed due date by adding repeat frequency, is before goal eta
     * if is repeat is true
     */
    private void createTaskRepetitionRule(CmmnModelInstance modelInstance, PlanItem planItem, ProgramDTO programDTO,
            EpisodeDTO episodeDTO, GoalDTO goalDTO, TaskDTO taskDTO) {
        if (taskDTO.isIsRepeat()) {
            
            // create item control
            String itemControlId = PLAN_ITEM_CONTROL_UNDERSCORE + programDTO.getProgramIdU() + UNDERSCORE + episodeDTO.getEpisodeIdU() + UNDERSCORE + goalDTO.getGoalIdU() + 
                    UNDERSCORE + taskDTO.getTaskIdU();
            ItemControl itemControl = createElement(modelInstance, planItem, itemControlId, ItemControl.class);

            // create repetition rule
            String repetitionRuleId = REPETITION_RULE_UNDERSCORE + programDTO.getProgramIdU() + UNDERSCORE + episodeDTO.getEpisodeIdU() + UNDERSCORE + goalDTO.getGoalIdU() + 
                    UNDERSCORE + taskDTO.getTaskIdU();
            RepetitionRule repetitionRule = modelInstance.newInstance(RepetitionRule.class);
            repetitionRule.setId(repetitionRuleId);
            
            // set repeat on event
            switch (taskDTO.getRepeatEvent()) {
            case COMPLETE:
                repetitionRule.setCamundaRepeatOnStandardEvent(COMPLETE_EVENT_NAME);
                break;
            case CREATE:
                repetitionRule.setCamundaRepeatOnStandardEvent(CREATE_EVENT_NAME);
                break;
            case START:
                repetitionRule.setCamundaRepeatOnStandardEvent(START_EVENT_NAME);
                break;
            default:
                log.error("Unsupported task repeat event: {} for task " + ID_COLON_SPACE_PARAM_SPACE_NAME_COLON_SPACE_PARAM, taskDTO.getRepeatEvent(), taskDTO.getTaskIdU(), taskDTO.getName());
                throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY, "Unsupported task repeat event: " + taskDTO.getRepeatEvent() + " for task id: " + taskDTO.getTaskIdU() + COMMA_SPACE_NAME_COLON_SPACE + taskDTO.getName());
            }

            if (taskDTO.getType() == Type.HUMAN && (taskDTO.getRepeatFrequencyUnit() == null || taskDTO.getRepeatFrequencyUnit() != TimeUnit.SOS)) {
                // create condition expression
                String conditionExpressionId = REPETITION_RULE_UNDERSCORE_CONDITION_EXPRESSION_UNDERSCORE + programDTO.getProgramIdU() + UNDERSCORE + episodeDTO.getEpisodeIdU() + UNDERSCORE + goalDTO.getGoalIdU() + 
                        UNDERSCORE + taskDTO.getTaskIdU();
                ConditionExpression conditionExpression = modelInstance.newInstance(ConditionExpression.class);
                conditionExpression.setId(conditionExpressionId);

                String variable = LC_UNDERSCORE + programDTO.getProgramIdU() + UNDERSCORE + episodeDTO.getEpisodeIdU() + UNDERSCORE + goalDTO.getGoalIdU() + 
                        UNDERSCORE + taskDTO.getTaskIdU() + UNDERSCORE_REPEAT;
                String conditionExpressionText = "${" + variable + " == true}";
                
                conditionExpression.setTextContent(conditionExpressionText);
                
                repetitionRule.setCondition(conditionExpression);
            }
            
            itemControl.setRepetitionRule(repetitionRule);
        }
    }

    private Task createTask(CmmnModelInstance modelInstance, PlanItem planItem, Stage stageGoal, ProgramDTO programDTO, EpisodeDTO episodeDTO, GoalDTO goalDTO,
            TaskDTO taskDTO) {
        String taskIdPart = programDTO.getProgramIdU() + UNDERSCORE + episodeDTO.getEpisodeIdU() + UNDERSCORE + goalDTO.getGoalIdU() + 
                UNDERSCORE + taskDTO.getTaskIdU();
        if (taskDTO.getType() == Type.HUMAN) {
            // create human task
            HumanTask humanTask = createElement(modelInstance, stageGoal, HUMAN_TASK_UNDERSCORE + taskIdPart, HumanTask.class);
            humanTask.setName(taskDTO.getName());
            humanTask.setCamundaAssignee(taskDTO.getAssignee());
            
            Integer sla = taskDTO.getSla() != null && taskDTO.getSla().intValue() != 0 ? taskDTO.getSla() : applicationProperties.getDefaultTaskSla();
            
            if (taskDTO.getDueDate() == null) {
                String variablePartTask = LC_UNDERSCORE + programDTO.getProgramIdU() + UNDERSCORE +  episodeDTO.getEpisodeIdU() + UNDERSCORE + goalDTO.getGoalIdU() +
                        UNDERSCORE + taskDTO.getTaskIdU();
                humanTask.setCamundaDueDate("${" + variablePartTask + UNDERSCORE_DUE_DATE_DOT + "toString()}");
                humanTask.setCamundaFollowUpDate("${" + variablePartTask + UNDERSCORE_DUE_DATE_DOT + "plusHours(" + sla + ").toString()}");
            } else {
                //set ISO date
                DateTime dueDate = new DateTime(taskDTO.getDueDate().getYear(), taskDTO.getDueDate().getMonthValue(), taskDTO.getDueDate().getDayOfMonth(), 0, 0);
                humanTask.setCamundaDueDate(dueDate.toString());
                humanTask.setCamundaFollowUpDate(dueDate.plusHours(sla).toString());
            }

            planItem.setDefinition(humanTask);
            
            return humanTask;
        } else if (taskDTO.getType() == Type.DECISION) {
            // create decision task
            DecisionTask decisionTask = createElement(modelInstance, stageGoal, DECISION_TASK_UNDERSCORE + taskIdPart, DecisionTask.class);
            decisionTask.setName(taskDTO.getName());
            if (taskDTO.getTypeRef() == null || taskDTO.getTypeRef().trim().isEmpty()) {
                log.error("Decision Key not defined for decision task " + ID_COLON_SPACE_PARAM_SPACE_NAME_COLON_SPACE_PARAM, taskDTO.getTaskIdU(), taskDTO.getName());
                throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY, "Decision Key not defined for decision task id: " +  taskDTO.getTaskIdU() + COMMA_SPACE_NAME_COLON_SPACE + taskDTO.getName());                        
            }
            decisionTask.setDecision(taskDTO.getTypeRef());
            decisionTask.setCamundaMapDecisionResult(SINGLE_RESULT);
            decisionTask.setCamundaResultVariable(LC_UNDERSCORE + taskIdPart + UNDERSCORE_DR);

            planItem.setDefinition(decisionTask);
            
            return decisionTask;
        } else {
            log.error("Unsupported task type {}", taskDTO.getType());
            throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY, "Unsupported task type: " + taskDTO.getType());
        }
    }

    private void createTaskScriptToCreateDecisionOutputVariables(StringBuilder[] scriptContents, ProgramDTO programDTO,
            EpisodeDTO episodeDTO, GoalDTO goalDTO, TaskDTO taskDTO) {
        /*
         * create a script to create decision output variables
         * and remove decision result variable 
         */
        if (taskDTO.getType() == Type.DECISION) {
            StringBuilder scriptContent = scriptContents[COMPLETE_EVENT_INDEX];
            if (scriptContent != null) {
                String variable = LC_UNDERSCORE + programDTO.getProgramIdU() + UNDERSCORE +  episodeDTO.getEpisodeIdU() + UNDERSCORE + goalDTO.getGoalIdU() + 
                        UNDERSCORE + taskDTO.getTaskIdU() + UNDERSCORE_DR;
                scriptContent.append("\nvar singleResult = caseExecution.getVariable('" + variable + "');");
                scriptContent.append("\nkeys = singleResult.keySet().toArray();");
                scriptContent.append("\nfor (key = 0; key < keys.length; key++) {\n");
                scriptContent.append(SPACES + "caseExecution.setVariable(keys[key], singleResult.get(keys[key]));\n}");
                scriptContent.append("\ncaseExecution.removeVariable('" + variable + "');");
            }
        }
    }

    /*
     * create a script to define task repeat variable
     * camunda is evaluating repeat rule when task is triggered by completion of another task
     * that is when sentry is satisfied
     * creating this variable on task create to avoid undefined variable script issue
     */
    private void createTaskScriptToDefineTaskRepeatVariable(StringBuilder[] scriptContents, ProgramDTO programDTO,
            EpisodeDTO episodeDTO, GoalDTO goalDTO, TaskDTO taskDTO) {
        if (taskDTO.isIsRepeat()) {
            StringBuilder scriptContent = scriptContents[CREATE_EVENT_INDEX];
            if (scriptContent != null) {
                String variable = LC_UNDERSCORE + programDTO.getProgramIdU() + UNDERSCORE + episodeDTO.getEpisodeIdU() + UNDERSCORE + goalDTO.getGoalIdU() + 
                        UNDERSCORE + taskDTO.getTaskIdU() + UNDERSCORE_REPEAT;
                scriptContent.append(NL_CASE_EXEC_DOT_SET_VAR_OPEN_PARENTHESIS_SQ + variable + SQ_COMMA_SPACE_TRUE_CLOSE_PARENTHESIS_SEMICOLON);
            }
        }
    }

    /*
     * create a script to define human task due date and
     * repeat rule evaluation result
     * create variables that hold due date
     * joda DateTime and ISO
     * repeat rule evaluation result
     * boolean
     */
    private void createTaskScriptToDefineTaskDueDate(StringBuilder[] scriptContents, ProgramDTO programDTO, EpisodeDTO episodeDTO, GoalDTO goalDTO, TaskDTO taskDTO) {
        if (taskDTO.getType() == Type.HUMAN && taskDTO.isIsRepeat()) {
            try {
                StringBuilder scriptContent = null;
                switch(taskDTO.getRepeatEvent()) {
                case COMPLETE:
                    scriptContent = scriptContents[COMPLETE_EVENT_INDEX];
                    break;
                case CREATE:
                    scriptContent = scriptContents[CREATE_EVENT_INDEX];
                    break;
                case START:
                    scriptContent = scriptContents[START_EVENT_INDEX];
                    break;
                default:
                    log.error("Unsupported task repeat event: {} for task " + ID_COLON_SPACE_PARAM_SPACE_NAME_COLON_SPACE_PARAM, taskDTO.getRepeatEvent(), taskDTO.getTaskIdU(), taskDTO.getName());
                    throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY, "Unsupported task repeat event: " + taskDTO.getRepeatEvent() + " for task id: " + taskDTO.getTaskIdU() + COMMA_SPACE_NAME_COLON_SPACE + taskDTO.getName());
                }
                if (scriptContent != null) {
                    scriptContent.append("\n// Load compatibility script");
                    scriptContent.append("\nload(\"nashorn:mozilla_compat.js\");");
                    scriptContent.append("\n// Import the org.joda.time package");
                    scriptContent.append("\nimportPackage(org.joda.time);");
                    scriptContent.append("\n// Import the org.joda.time.DateTime class");
                    scriptContent.append("\nimportClass(org.joda.time.DateTime);");
                    String variablePartGoal = LC_UNDERSCORE + programDTO.getProgramIdU() + UNDERSCORE +  episodeDTO.getEpisodeIdU() + UNDERSCORE + goalDTO.getGoalIdU();
                    String variablePartTask = LC_UNDERSCORE + programDTO.getProgramIdU() + UNDERSCORE +  episodeDTO.getEpisodeIdU() + UNDERSCORE + goalDTO.getGoalIdU() + 
                            UNDERSCORE + taskDTO.getTaskIdU();
                    scriptContent.append(NL_VAR_SPACE + variablePartGoal + UNDERSCORE_END_DATE + " = caseExecution.getVariable('" + variablePartGoal + UNDERSCORE_END_DATE + "');");
                    if (taskDTO.getRepeatFrequencyUnit() != null && taskDTO.getRepeatFrequencyUnit() != TimeUnit.SOS) {
                        String addDateTime = getJodaMethodToAddDateTime(taskDTO.getRepeatFrequencyValue(), taskDTO.getRepeatFrequencyUnit());
                        scriptContent.append(NL_VAR_SPACE + variablePartTask + UNDERSCORE_DUE_DATE + " = caseExecution.getVariable('" + variablePartTask + UNDERSCORE_DUE_DATE + "');");
                        scriptContent.append("\n" + variablePartTask + UNDERSCORE_DUE_DATE + " = " + variablePartTask + UNDERSCORE_DUE_DATE_DOT + addDateTime + ";");
                        scriptContent.append(NL_CASE_EXEC_DOT_SET_VAR_OPEN_PARENTHESIS_SQ + variablePartTask + UNDERSCORE_DUE_DATE + "', " + variablePartTask + UNDERSCORE_DUE_DATE + ");");
                    } else {
                        scriptContent.append("\n// Create a new DateTime object");
                        scriptContent.append(NL_VAR_SPACE + variablePartTask + UNDERSCORE_DUE_DATE + " = new org.joda.time.DateTime();");
                        scriptContent.append(NL_CASE_EXEC_DOT_SET_VAR_OPEN_PARENTHESIS_SQ + variablePartTask + UNDERSCORE_DUE_DATE + "', " + variablePartTask + UNDERSCORE_DUE_DATE + ");");
                    }
                    scriptContent.append(NL_CASE_EXEC_DOT_SET_VAR_OPEN_PARENTHESIS_SQ + variablePartTask + UNDERSCORE_REPEAT + "', " + variablePartTask + UNDERSCORE_DUE_DATE_DOT + "isBefore(" + variablePartGoal + UNDERSCORE_END_DATE + "));");
                }

            } catch (ResponseStatusException e) {
                log.error("Invalid task repeat frequency value: {} unit: {} for Task Id: {}", taskDTO.getRepeatFrequencyValue(), taskDTO.getRepeatFrequencyUnit(), taskDTO.getTaskIdU());
                throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY, e.getMessage() + ", task repeat frequency value: " + taskDTO.getRepeatFrequencyValue() + ", unit: " + taskDTO.getRepeatFrequencyUnit() + " for Task Id: " + taskDTO.getTaskIdU());
            }
        }
    }

    /*
     * check if we need to create case execution listener script to create variables
     * if associations in the goal assoc when queried by assoc on
     * if associations in the goal assoc when queried by id
     */
    private void createTaskScriptToCreateVariables(List<TaskAssociateDTO> taskAssociateDTOsByAssocOn,
            StringBuilder[] scriptContents, ProgramDTO programDTO, EpisodeDTO episodeDTO, GoalDTO goalDTO,
            TaskDTO taskDTO) {
        if (taskAssociateDTOsByAssocOn != null && !taskAssociateDTOsByAssocOn.isEmpty()) {
            // create script content to create variables
            for (TaskAssociateDTO taskAssociateDTO : taskAssociateDTOsByAssocOn) {
                Optional<TaskDTO> optinalTaskDTOAssocTo = taskService.findOne(taskAssociateDTO.getTaskId());
                if(!optinalTaskDTOAssocTo.isPresent()) {
                    log.warn("Skipping script statement to create case execution variable as did not find task id {} association to, from task id {} task idU {}",
                            taskAssociateDTO.getTaskId(), taskDTO.getId(), taskDTO.getTaskIdU());
                    continue;
                }
                TaskDTO taskDTOAssocTo = optinalTaskDTOAssocTo.get();
                StringBuilder scriptContent = getScriptObj4AssocEvent(scriptContents, taskAssociateDTO.getAssociateEvent(), "task", taskDTOAssocTo.getTaskIdU(), taskDTO.getTaskIdU());
                if (scriptContent != null) {
                    String variable = LC_UNDERSCORE + programDTO.getProgramIdU() + UNDERSCORE + episodeDTO.getEpisodeIdU() + UNDERSCORE + goalDTO.getGoalIdU() + 
                            UNDERSCORE + taskDTO.getTaskIdU() + UNDERSCORE + taskDTOAssocTo.getTaskIdU()  + UNDERSCORE_ACTIVATE;
                    scriptContent.append(NL_CASE_EXEC_DOT_SET_VAR_OPEN_PARENTHESIS_SQ + variable + SQ_COMMA_SPACE_TRUE_CLOSE_PARENTHESIS_SEMICOLON);

                    // checks if multi association condition is satisfied and creates variable to trigger event for associated task
                    createTaskScriptToChkMultiAssocCondAndCreateVars(taskAssociateDTO.getTaskId(), scriptContent, programDTO, episodeDTO, goalDTO, taskDTOAssocTo);
                }
            }
        }
    }

    /*
     * create a script to check all pre conditions
     * and define a variable if all pre conditions are satisfied
     * variables used in juel expression must be defined before evaluation 
     */
    private void createTaskScriptToChkMultiAssocCondAndCreateVars(Long taskIdAssocTo, StringBuilder scriptContent,
            ProgramDTO programDTO, EpisodeDTO episodeDTO, GoalDTO goalDTO, TaskDTO taskDTOAssocTo) {
        // get task assoc by to task id
        List<TaskAssociateDTO> taskAssociateDTOsByToTaskId = taskAssocService.findAllByTaskId(taskIdAssocTo);
        if (taskAssociateDTOsByToTaskId != null && !taskAssociateDTOsByToTaskId.isEmpty()) {
            scriptContent.append(NL_IF_SPACE_OPEN_PARENTHESIS);
            boolean op = false;
            for (TaskAssociateDTO toTaskAssociateDTO : taskAssociateDTOsByToTaskId) {
                Optional<TaskDTO> optionalFromTaskDTO = taskService.findOne(toTaskAssociateDTO.getAssociateOn());
                if (optionalFromTaskDTO.isPresent()) {
                    TaskDTO fromTaskDTO = optionalFromTaskDTO.get();
                    if (op)
                        scriptContent.append(" && ");
                    else
                        op = true;
                    String variable = LC_UNDERSCORE + programDTO.getProgramIdU() + UNDERSCORE + episodeDTO.getEpisodeIdU() + UNDERSCORE + goalDTO.getGoalIdU() + UNDERSCORE + fromTaskDTO.getTaskIdU() + UNDERSCORE + taskDTOAssocTo.getTaskIdU() + UNDERSCORE_ACTIVATE;
                    scriptContent.append(TYPEOF_SPACE + variable + SPACE_NOT_EQUAL_TO_SPACE_SQ_UNDEFINED_SQ_SPACE_AND_SPACE + variable + SPACE_EQUAL_TO_SPACE_TRUE);
                } else {
                    log.error("Association from task id {} is missing in task", toTaskAssociateDTO.getAssociateOn());
                    throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY, "Association from task id: " + toTaskAssociateDTO.getAssociateOn() + " is missing in task");
                }
            }
            scriptContent.append(CLOSE_PARENTHESIS_SPACE_OPEN_CURLY_NL);
            String variable = LC_UNDERSCORE + programDTO.getProgramIdU() + UNDERSCORE + episodeDTO.getEpisodeIdU() + UNDERSCORE + goalDTO.getGoalIdU() + UNDERSCORE + taskDTOAssocTo.getTaskIdU() + UNDERSCORE_ACTIVATE;
            scriptContent.append(SPACES + CASE_EXEC_DOT_SET_VAR_OPEN_PARENTHESIS_SQ + variable + SQ_COMMA_SPACE_TRUE_CLOSE_PARENTHESIS_SEMICOLON_NL_CLOSE_CURLY);
        }
    }

    private void createTaskScriptToRemoveVariables(List<TaskAssociateDTO> taskAssociateDTOsById,
            StringBuilder[] scriptContents, ProgramDTO programDTO, EpisodeDTO episodeDTO, GoalDTO goalDTO,
            TaskDTO taskDTO) {
        boolean isAssociations = taskAssociateDTOsById != null && !taskAssociateDTOsById.isEmpty(); 
        // create script content to remove variables
        if (isAssociations || (taskDTO.getRepeatFrequencyUnit() != null && taskDTO.getRepeatFrequencyUnit() == TimeUnit.SOS)) {
            // remove variable task id
            StringBuilder scriptContent = scriptContents[COMPLETE_EVENT_INDEX];
            if (scriptContent != null) {
                String variable = LC_UNDERSCORE + programDTO.getProgramIdU() + UNDERSCORE +  episodeDTO.getEpisodeIdU() + UNDERSCORE + goalDTO.getGoalIdU()  + 
                        UNDERSCORE + taskDTO.getTaskIdU() + UNDERSCORE_ACTIVATE;
                scriptContent.append(NL_IF_SPACE_OPEN_PARENTHESIS_TYPEOF_SPACE + variable + SPACE_NOT_EQUAL_TO_SPACE_SQ_UNDEFINED_SQ_CLOSE_PARENTHESIS_SPACE_OPEN_CURLY_NL);
                scriptContent.append(SPACES + CASE_EXEC_DOT_REMOVE_VAR_OPEN_PARENTHESIS_SQ + variable + SQ_CLOSE_PARENTHESIS_SEMICOLON_NL_CLOSE_CURLY);
            }
        }

        if (isAssociations) { 
            for (TaskAssociateDTO taskAssociateDTO : taskAssociateDTOsById) {
                Optional<TaskDTO> optionalTaskDTOAssocFrom = taskService.findOne(taskAssociateDTO.getAssociateOn());
                if(!optionalTaskDTOAssocFrom.isPresent()) {
                    log.warn("Skipping script statement to remove case execution variable as did not find task id {} association from, to task id {} task idU {}",
                            taskAssociateDTO.getAssociateOn(), taskDTO.getId(), taskDTO.getTaskIdU());
                    continue;
                }
                TaskDTO taskDTOAssocFrom = optionalTaskDTOAssocFrom.get();
                StringBuilder scriptContent = getScriptObj4AssocEvent(scriptContents, taskAssociateDTO.getAssociateEvent(), "task", taskDTO.getTaskIdU(), taskDTOAssocFrom.getTaskIdU());
                if (scriptContent != null) {
                    String variable = LC_UNDERSCORE + programDTO.getProgramIdU() + UNDERSCORE + episodeDTO.getEpisodeIdU() + UNDERSCORE + goalDTO.getGoalIdU() + 
                            UNDERSCORE + taskDTOAssocFrom.getTaskIdU() + UNDERSCORE + taskDTO.getTaskIdU()  + UNDERSCORE_ACTIVATE;
                    scriptContent.append(NL_IF_SPACE_OPEN_PARENTHESIS_TYPEOF_SPACE + variable + SPACE_NOT_EQUAL_TO_SPACE_SQ_UNDEFINED_SQ_CLOSE_PARENTHESIS_SPACE_OPEN_CURLY_NL);
                    scriptContent.append(SPACES + CASE_EXEC_DOT_REMOVE_VAR_OPEN_PARENTHESIS_SQ + variable + SQ_CLOSE_PARENTHESIS_SEMICOLON_NL_CLOSE_CURLY);
                }
            }
        }
    }

    /*
     * check if we need to create entry criteria
     * if entry criteria is defined
     * if associations are defined
     */
    private void createEntryCriteria(CmmnModelInstance modelInstance, PlanItem planItem, Stage stage,
            String entryCriteria, boolean isAssociations, boolean isSOSTask, String id) {
        if (entryCriteria != null || isAssociations || isSOSTask) {

            // create entry criterion
            String entryCriterionId = ENTRY_CRITERION_UNDERSCORE + id;
            EntryCriterion entryCriterion =  createElement(modelInstance, planItem, entryCriterionId, EntryCriterion.class);
            
            // create sentry
            String sentryId = SENTRY_UNDERSCORE + id;
            Sentry sentry = createElement(modelInstance, stage, sentryId, Sentry.class);
            
            entryCriterion.setSentry(sentry);
            
            // create extension elements
            ExtensionElements extensionElements = createElement(modelInstance, null, null, ExtensionElements.class);
            sentry.setExtensionElements(extensionElements);

            StringBuilder entryCriteriaAssoc = null;
            // create variable on part for associations
            if (isAssociations || isSOSTask) {

                // create variable on part
                String variableName = LC_UNDERSCORE + id + UNDERSCORE_ACTIVATE;
                createVariableOnPart(modelInstance, extensionElements, variableName);

                // create entry criteria for associations
                entryCriteriaAssoc = new StringBuilder(variableName + SPACE_EQUAL_TO_SPACE_TRUE);
            }
            
            // create if part
            String ifPartId = IF_PART_UNDERSCORE + id;
            IfPart ifPart = modelInstance.newInstance(IfPart.class);
            ifPart.setId(ifPartId);
            
            // create condition expression
            String conditionExpressionId = IF_PART_UNDERSCORE_CONDITION_EXPRESSION_UNDERSCORE + id;
            ConditionExpression conditionExpression = modelInstance.newInstance(ConditionExpression.class);
            conditionExpression.setId(conditionExpressionId);
            
            // create entry criteria for associations
            String conditionExpressionText = createEntryCriteriaExpression(entryCriteria, entryCriteriaAssoc);
            conditionExpression.setTextContent(conditionExpressionText);
            ifPart.setCondition(conditionExpression);
            sentry.setIfPart(ifPart);
        }
    }

    private void createDocumentation(CmmnModelInstance modelInstance, CmmnElement cmmnElement, String description, String id) {
        /*
         * check if we need to create documentation
         */
        if (description != null && !description.trim().isEmpty()) {
            // create documentation
            String documentationId = DOCUMENTATION_UNDERSCORE + id;
            Documentation documentation = createElement(modelInstance, cmmnElement, documentationId, Documentation.class);
            documentation.setTextContent(description);
        }
    }

    private String createEntryCriteriaExpression(String entryCriteria, StringBuilder entryCriteriaAssoc) {
        String conditionExpressionText = null;
        if (entryCriteria != null && entryCriteriaAssoc != null)
            conditionExpressionText = "${(" + entryCriteria + CLOSE_PARENTHESIS_SPACE_AND_SPACE_OPEN_PARENTHESIS + entryCriteriaAssoc + ")}";
        else if (entryCriteria != null)
            conditionExpressionText = "${" + entryCriteria + "}";
        else
            conditionExpressionText = "${" + entryCriteriaAssoc + "}";
        return conditionExpressionText;
    }

    private String getJodaMethodToAddDateTime(Integer value, TimeUnit unit) {
        /*
         * if time interval value is undefined, we define it to one unit
         * for example: we define 1 day for time interval value daily
         */
        if (value == null || value.intValue() == 0)
            value = Integer.valueOf("1");

        switch (unit) {
        case DAYS:
        case DAILY:
        case DAY:
            return PLUS_DAYS + "(" + value + ")";
        case WEEKS:
        case WEEKLY:
        case WEEK:
            return PLUS_WEEKS + "(" + value + ")";
        case MONTHS:
        case MONTHLY:
        case MONTH:
            return PLUS_MONTHS + "(" + value + ")";
        case YEARS:
        case YEARLY:
        case YEAR:
            return PLUS_YEARS + "(" + value + ")";
        default:
            log.error("Invalid Time Interval Value: {} Unit: {}", value, unit);
            throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY, "Invalid Time Interval Value: " + value + ", Unit: " + unit);
        }
    }

    private StringBuilder getScriptObj4AssocEvent(StringBuilder[] scriptContents, CaseExecutionEvent associateEvent,
            String msgPart, String id, String assocOnId) {
        
        StringBuilder scriptContent = null;
                
        // we define association on event to be complete if not given
        if (associateEvent == null) {
            scriptContent = scriptContents[COMPLETE_EVENT_INDEX];
        } else {
            switch (associateEvent) {
            case COMPLETE:
                scriptContent = scriptContents[COMPLETE_EVENT_INDEX];
                break;
            case CREATE:
                scriptContent = scriptContents[CREATE_EVENT_INDEX];
                break;
            case START:
                scriptContent = scriptContents[START_EVENT_INDEX];
                break;
            default:
                log.warn("Skipping unsupported {} association on event: {}, {} Id: {}, association on: {}", msgPart, associateEvent, msgPart, id, assocOnId);
            }
        }
        
        return scriptContent;
    }

    /*
     * creates execution listeners for supported events and sets java script content
     */
    private void createExecutionListeners(CmmnModelInstance modelInstance, PlanItemDefinition planItemDefinition, StringBuilder[] scriptContents) {
        ExtensionElements extensionElements = null;

        for (int i = 0; i < START_EVENT_INDEX + 1; i++) {
            String event = null;
            switch (i) {
            case COMPLETE_EVENT_INDEX:
                event = COMPLETE_EVENT_NAME;
                break;
            case CREATE_EVENT_INDEX:
                event = CREATE_EVENT_NAME;
                break;
            case START_EVENT_INDEX:
                event = START_EVENT_NAME;
                break;
            default:
                log.warn("Skipping to create event listener for unsupported event index: {} for PlanItemDefinition " + ID_COLON_SPACE_PARAM_SPACE_NAME_COLON_SPACE_PARAM, i, planItemDefinition.getId(), planItemDefinition.getName());
            }
            
            StringBuilder scriptContent = scriptContents[i];
            if ((scriptContent == null || scriptContent.length() <= 0) || (event == null))
                continue;

            scriptContent.append("\n");
            
            // create extension elements if not created yet as we loop through events
            if (extensionElements == null)
                extensionElements = createElement(modelInstance, null, null, ExtensionElements.class);

            // create case execution listener
            CamundaCaseExecutionListener caseExecutionListener = extensionElements.addExtensionElement(CamundaCaseExecutionListener.class);

            // create java script
            CamundaScript script = modelInstance.newInstance(CamundaScript.class);
            script.setCamundaScriptFormat("javascript");

            // set event
            caseExecutionListener.setCamundaEvent(event);

            // set script and content
            caseExecutionListener.setCamundaScript(script);
            script.setTextContent(scriptContent.toString());
        }

        // set extension elements if at least one script was created for supported events
        if (extensionElements != null)
            planItemDefinition.setExtensionElements(extensionElements);
    }

    private void createVariableOnPart(CmmnModelInstance modelInstance,
            ExtensionElements extensionElements, String variableName) {
        // create variable on part
        CamundaVariableOnPart variableOnPart = extensionElements.addExtensionElement(CamundaVariableOnPart.class);
        
        // create variable transition event
        createElement(modelInstance, variableOnPart, null, CamundaVariableTransitionEvent.class);
        
        variableOnPart.setVariableName(variableName);
        variableOnPart.setVariableEvent(VariableTransition.create);
    }

    private <T extends CmmnModelElementInstance> T createElement(CmmnModelInstance modelInstance, CmmnModelElementInstance parentElement, String id, Class<T> elementClass) {
        T element = modelInstance.newInstance(elementClass);
        if (id != null)
            element.setAttributeValue("id", id, true);
        if (parentElement != null)
            parentElement.addChildElement(element);
        return element;
    }

    private CmmnModelInstance createEmptyModel() {
        CmmnModelInstance modelInstance;
        Definitions definitions;

        modelInstance = Cmmn.createEmptyModel();
        definitions = modelInstance.newInstance(Definitions.class);
        definitions.setTargetNamespace(CmmnModelConstants.CMMN11_NS);
        definitions.setExporter(CMMN_EXPORTER_NAME);
        definitions.setExporterVersion(CMMN_EXPORTER_VERSION);
        modelInstance.setDefinitions(definitions);
        
        return modelInstance;
    }

}