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

import java.util.AbstractMap.SimpleImmutableEntry;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import io.igia.caremanagement.domain.enumeration.CaseExecutionEvent;
import io.igia.caremanagement.domain.enumeration.TimeUnit;
import io.igia.caremanagement.domain.enumeration.Type;
import io.igia.caremanagement.service.ProgramImportService;
import io.igia.caremanagement.service.dto.EpisodeDTO;
import io.igia.caremanagement.service.dto.GoalAssociateDTO;
import io.igia.caremanagement.service.dto.GoalDTO;
import io.igia.caremanagement.service.dto.ProgramDTO;
import io.igia.caremanagement.service.dto.TaskAssociateDTO;
import io.igia.caremanagement.service.dto.TaskDTO;
import io.igia.caremanagement.web.rest.errors.InternalServerErrorException;

/**
 * Service for importing program worksheet.
 */
@Service
@Transactional
public class ProgramImportServiceImpl extends EntityServices implements ProgramImportService {

    private static final Logger log = LoggerFactory.getLogger(ProgramImportServiceImpl.class);

    // time intervals
    private static final String DAY = "DAY";
    private static final String DAYS = "DAYS";
    private static final String DAILY = "DAILY";
    private static final String WEEK = "WEEK";
    private static final String WEEKS = "WEEKS";
    private static final String WEEKLY = "WEEKLY";
    private static final String MONTH = "MONTH";
    private static final String MONTHS = "MONTHS";
    private static final String MONTHLY = "MONTHLY";
    private static final String YEAR = "YEAR";
    private static final String YEARS = "YEARS";
    private static final String YEARLY = "YEARLY";
    private static final String SOS = "SOS";
    private static final String HOURS = "HOURS";
    private static final String ONCE = "ONCE";

    // program headers
    private static final String PROGRAM_NAME = "Program Name";
    private static final String GOAL_NAME = "Goal Name";
    private static final String TASK_NAME = "Task Name";
    
    private static final String SPACE = " ";
    private static final String HASH = "#";
    private static final String COLON = ":";
    private static final String UNDERSCORE = "_";
    private static final String COMMA = ",";
    private static final String OPEN_CURLY = "{";
    private static final String CLOSE_CURLY = "}";
    private static final String OPEN_CLOSE_CURLY = OPEN_CURLY + CLOSE_CURLY;
    private static final String UNDEFINED = "undefined";
    private static final String SPACE_UNDEFINED_SPACE = SPACE + UNDEFINED + SPACE;
    private static final String ROW = "Row";
    private static final String ROW_NUM = ROW + HASH;
    private static final String COL = "col";
    private static final String COL_NUM = COL + HASH;
    private static final String ROW_NUM_SPACE_OPEN_CLOSE_CURLY_COL_NUM_SPACE_OPEN_CLOSE_CURLY_SPACE_UNDEFINED_SPACE_OPEN_CLOSE_CURLY = ROW_NUM + SPACE + OPEN_CLOSE_CURLY + COL_NUM + SPACE + OPEN_CLOSE_CURLY + SPACE + UNDEFINED + SPACE + OPEN_CLOSE_CURLY;
    private static final String WORKSHEET = "Worksheet";
    private static final String WORKSHEET_COLON_SPACE = WORKSHEET + COLON + SPACE;
    private static final String SPACE_COL_NUM_SPACE = SPACE + COL_NUM + SPACE;
    private static final String ROW_NUM_SPACE = ROW_NUM + SPACE;
    private static final String COMMA_SPACE = COMMA + SPACE;
    
    String getValidCellValue(Row row, Cell cell, short firstColumnIndex) {
    	
    	// skip row if there is cell defined before program first column and starts with hash character #
        if (isCommentRow(row, firstColumnIndex)) {
            return null;
        }
        
    	if (cell == null) {
        	return null;
        }
        
        // skip the row if first defined cell type is other than string
        if (cell.getCellType().compareTo(CellType.STRING) != 0) {
        	return null;
        }
        
        String value = cell.getStringCellValue();
        
    	if (value == null) {
        	log.warn("skipping row#  {}, unexpected first cell type: {}", row.getRowNum() + 1, cell.getCellType());
        	return null;
        }
            
        value = value.trim();
        
        log.debug("row# {} col# {} val: {}", row.getRowNum() + 1, cell.getColumnIndex() + 1, value);

        // skip the row if cell value is empty or starts with #
        if (!value.isEmpty() && value.charAt(0) != '#') {
        	return value;
        }
        
        return null;
    }
    
    private void validateImport(XSSFSheet worksheet, ProgramDTO programDTO, GoalDTO goalDTO, TaskDTO taskDTO) {
		 if (programDTO == null) {
             log.error("worksheet: {} undefined program", worksheet.getSheetName());
             throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY, WORKSHEET_COLON_SPACE + worksheet.getSheetName() + ", undefined program");
         } else if (goalDTO == null) {
             log.error("worksheet: {} undefined goals", worksheet.getSheetName());
             throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY, WORKSHEET_COLON_SPACE + worksheet.getSheetName() + ", undefined goals");
         } else if (taskDTO == null) {
             log.error("worksheet: {} undefined tasks", worksheet.getSheetName());
             throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY, WORKSHEET_COLON_SPACE + worksheet.getSheetName() + ", undefined tasks");
         }
	}
    
    private void validateProgramHeader(Row row, Cell cell, ProgramDTO programDTO, short firstColumnIndex) {
    	// throw exception if program name is redefined
        if (firstColumnIndex != -1 && programDTO != null)
            throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY, ROW_NUM_SPACE + (row.getRowNum() + 1) + SPACE_COL_NUM_SPACE + (cell.getColumnIndex() + 1) + ", program name is redefined");
        
    }
    
    private void validateGoalHeader(Row row, Cell cell, ProgramDTO programDTO) {
    	if (programDTO == null)
            throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY, ROW_NUM_SPACE + (row.getRowNum() + 1) + SPACE_COL_NUM_SPACE + (cell.getColumnIndex() + 1) + ", goal name is defined before program name");
        
    }
    
    private void validateTaskHeader(Row row, Cell cell, GoalDTO goalDTO) {
    	if (goalDTO == null)
            throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY, ROW_NUM_SPACE + (row.getRowNum() + 1) + SPACE_COL_NUM_SPACE + (cell.getColumnIndex() + 1) + ", task name is defined before goal name");
        
    }
    
    private Iterator<Row> getWorksheetRowIterator(XSSFSheet worksheet) {
    	if (worksheet == null)
            throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY, "Received null worksheet, nothing to import!");
    	
    	// we get row iterator
        Iterator<Row> rowIt = worksheet.iterator();
        
        if (rowIt == null)
        	throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY, "Row iterator null, nothing to import!");
        
        return rowIt;
    }
    
    @Transactional(propagation = Propagation.REQUIRES_NEW, rollbackFor = ResponseStatusException.class)
    public ObjectNode importProgramWorksheet(XSSFSheet worksheet) {
        /*
         * we skip
         * 1. blank rows
         * 2. rows having cell value as "Program"
         * 3. rows having cell value as "Goals and Tasks"
         * 4. all rows preceding cell "Program Name"
         * 5. rows having cell value as "Program Name"
         * 6. rows having cell value as "Goal Name"
         * 7. rows having cell value string with "#"
         * 8. rows having first cell type other than string
         */
        
    	log.info("importing worksheet: {}", worksheet.getSheetName());

        ObjectMapper mapper = new ObjectMapper();
        ObjectNode response = mapper.createObjectNode();
        
        try {
            ProgramDTO programDTO = null;
            EpisodeDTO episodeDTO = null;
            GoalDTO goalDTO = null;
            TaskDTO taskDTO = null;
            
            short episodeCount = 0;
            short goalCount = 0;
            short taskCount = 0;
            
            /*
             * next actions
             * 0 - read program details
             * 1 - read goal details
             * 2 - read task details
             */
            short nextAction = -1;
            
            
            Iterator<Row> rowIt = getWorksheetRowIterator(worksheet);
                
            // skip rows until program header
            short firstColumnIndex = getProgramFirstColumnIndex(rowIt);
            if (firstColumnIndex != -1) {
                nextAction = 0;
            }

            // we will hold the associated goals and tasks in the map until we have processed the entire worksheet
            Map<Long, SimpleImmutableEntry<String, Map<Long, String>>> id2AssociatedMap = new HashMap<>();

            while (rowIt.hasNext()) {
                Row row = rowIt.next();
                
                log.debug("reading row# {}", row.getRowNum() + 1);

                Cell cell = row.getCell(firstColumnIndex); // get first defined cell
                String value = getValidCellValue(row, cell, firstColumnIndex);
                if (value == null) {
                	continue;
                }
                
                boolean headerFound = false;
                
                // if program header
                if (value.equalsIgnoreCase(PROGRAM_NAME)) {
                    validateProgramHeader(row, cell, programDTO, firstColumnIndex);
                	firstColumnIndex = (short) cell.getColumnIndex();
                    nextAction = 0;
                    headerFound = true;
                    // if goal header
                } else if (value.equalsIgnoreCase(GOAL_NAME)) {
                    validateGoalHeader(row, cell, programDTO);
                	nextAction = 1;
                	headerFound = true;
                } else if (value.equalsIgnoreCase(TASK_NAME)) {
                	validateTaskHeader(row, cell, goalDTO);
                    nextAction = 2;
                    headerFound = true;
                }
                
                if (!headerFound) {
                    switch (nextAction) {
	                case 0:
	                    programDTO = importProgramRow(row, firstColumnIndex);
	                    episodeDTO = importEpisode(++episodeCount, programDTO);
	                    nextAction = -1;
	                    break;
	                case 1:
	                    goalDTO = importGoalRow(row, firstColumnIndex, ++goalCount, programDTO, episodeDTO, id2AssociatedMap);
	                    nextAction = -1;
	                    taskCount = 0;
	                    break;
	                case 2:
	                    taskDTO = importTaskRow(row, firstColumnIndex, ++taskCount, programDTO, episodeDTO, goalDTO, id2AssociatedMap);
	                    break;
	                default:
	                    break;
	                }
                }
            } 
            
            // reached at the end of the worksheet, let's import goal and task associations
            
            // import goal associations
            importGoalAssociations(id2AssociatedMap, episodeDTO);
            
            // import task associations
            importTaskAssociations(id2AssociatedMap);
            
            validateImport(worksheet, programDTO, goalDTO, taskDTO);
            
            ObjectNode details = response.putObject(worksheet.getSheetName());
            details.put("status", HttpStatus.OK.value());
            details.put("id", programDTO.getProgramIdU());
           
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY, WORKSHEET_COLON_SPACE + worksheet.getSheetName() + COMMA_SPACE + e.getMessage());
        }
        
        return response;
    }

    
    private TaskDTO getAssociatedTask(String associatedName, Long goalEntryKey, Long taskEntryKey) {
    	// query task by name and goal id
        Optional<TaskDTO> optionalAssociatedTaskDTO = taskService.findOneByNameIgnoreCaseAndGoalId(associatedName, goalEntryKey);

        // validate if defined task
        if (!optionalAssociatedTaskDTO.isPresent()) {
            log.error("undefined task name '{}' in the associated task names", associatedName);
            throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY, "Undefined task name '" + associatedName + "' in the associated task names");
        }

        // validate for association on the self
        TaskDTO associatedTaskDTO = optionalAssociatedTaskDTO.get();
        if (associatedTaskDTO.getId().equals(taskEntryKey)) {
            log.error("invalid associated task id {} task can not have association on the self!", associatedTaskDTO.getId());
            throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY, "Invalid associated task id " + associatedTaskDTO.getId() + ", task can not have association on the self!");
        }
        
        return associatedTaskDTO;

    }
    
    
    private String [] getTaskAssociatedNames(Entry<Long, String> taskEntry) {
    	String associatedNamesStr = taskEntry.getValue();
        if (associatedNamesStr == null || associatedNamesStr.trim().isEmpty()) {
            log.debug("associated tasks are not defined for task id: {}", taskEntry.getKey());
            return new String [] {};
        }
        
        String[] associatedNames = associatedNamesStr.split("\\|");
        if (associatedNames == null) {
            log.debug("associated tasks are not defined for task id: {}", taskEntry.getKey());
            return new String [] {};
        }
        
        return associatedNames;
	}
    
	private void importTaskAssociations(Map<Long, SimpleImmutableEntry<String, Map<Long, String>>> id2AssociatedMap) {
        for (Entry<Long, SimpleImmutableEntry<String, Map<Long, String>>> goalEntry : id2AssociatedMap.entrySet()) {
            for (Entry<Long, String> taskEntry : goalEntry.getValue().getValue().entrySet()) {
                String[] associatedNames = getTaskAssociatedNames(taskEntry);
                
                
                for (String associatedName : associatedNames) {
                    
                	TaskDTO associatedTaskDTO = getAssociatedTask(associatedName.trim(), goalEntry.getKey(), taskEntry.getKey());
                	
                	                    
                    TaskAssociateDTO taskAssociateDTO = new TaskAssociateDTO();
                    taskAssociateDTO.setTaskId(taskEntry.getKey());
                    taskAssociateDTO.setAssociateOn(associatedTaskDTO.getId());
                    taskAssociateDTO.setAssociateEvent(CaseExecutionEvent.COMPLETE);

                    taskAssocService.save(taskAssociateDTO);
                }
            }
        }
    }

	private String [] getGoalAssociatedNames(Entry<Long, SimpleImmutableEntry<String, Map<Long, String>>> goalEntry) {
		String associatedNamesStr = goalEntry.getValue().getKey();
        if (associatedNamesStr == null || associatedNamesStr.trim().isEmpty()) {
            log.debug("associated goals are not defined for goal id: {}", goalEntry.getKey());
            return new String [] {};
        }
        
        String[] associatedNames = associatedNamesStr.split("\\|");
        if (associatedNames == null) {
            log.debug("associated goals are not defined for goal id: {}", goalEntry.getKey());
            return new String [] {};
        }
        
        return associatedNames;
	}
	
    private void importGoalAssociations(Map<Long, SimpleImmutableEntry<String, Map<Long, String>>> id2AssociatedMap, EpisodeDTO episodeDTO) {
        for (Entry<Long, SimpleImmutableEntry<String, Map<Long, String>>> goalEntry : id2AssociatedMap.entrySet()) {
            
        	String[] associatedNames = getGoalAssociatedNames(goalEntry);
        	
            for (String associatedName : associatedNames) {
                associatedName = associatedName.trim();
                
                // query goal by name and episode id
                Optional<GoalDTO> optionalAssociatedGoalDTO = goalService.findOneByNameIgnoreCaseAndEpisodeId(associatedName, episodeDTO.getId());

                // validate if defined goal
                if (!optionalAssociatedGoalDTO.isPresent()) {
                    log.error("undefined goal name '{}' in the associated goal names", associatedName);
                    throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY, "Undefined goal name '" + associatedName + "' in the associated goal names");
                }

                // validate for association on the self
                GoalDTO associatedGoalDTO = optionalAssociatedGoalDTO.get();
                if (associatedGoalDTO.getId().equals(goalEntry.getKey())) {
                    log.error("invalid associated goal id {} goal can not have association on the self!", associatedGoalDTO.getId());
                    throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY, "Invalid associated goal id " + associatedGoalDTO.getId() + ", goal can not have association on the self!");
                }
                
                GoalAssociateDTO goalAssociateDTO = new GoalAssociateDTO();
                goalAssociateDTO.setGoalId(goalEntry.getKey());
                goalAssociateDTO.setAssociateOn(associatedGoalDTO.getId());
                goalAssociateDTO.setAssociateEvent(CaseExecutionEvent.COMPLETE);

                goalAssocService.save(goalAssociateDTO);
            }
        }
    }

    /*
     * checks columns preceding program template first column
     * returns as comment row if starts with hash character #
     */
    private boolean isCommentRow(Row row, short firstColumnIndex) {
        
    	short minColIx = row.getFirstCellNum();
        if (minColIx == -1) {
        	return false;
        }
        
        for (short colIx = minColIx; colIx < firstColumnIndex; colIx++) {
            Cell cell = row.getCell(colIx);
            // skip the cell if null
            if(cell != null) {
                // return if cell type other than string as it will not start with hash character #
                if (!cell.getCellType().equals(CellType.STRING)) {
                    break;
                }
                
                String value = cell.getStringCellValue();
                
                // skip the cell if value is null
                if (value != null && !value.trim().isEmpty() && value.trim().charAt(0) == '#')  {
                	return true;
	            }
            }
        }
        
        
        return false;
    }

    private boolean cellHasProgramName(Cell cell) {
    	if (cell == null) {
    		return false;
    	}
    	
    	if (!cell.getCellType().equals(CellType.STRING)) {
            return false;
    	}
    	
    	String value = cell.getStringCellValue();
            
        // skip the cell if value is null
        return value != null && 
        	!value.trim().isEmpty() && 
        	value.trim().charAt(0) != '#' && 
        	value.equalsIgnoreCase(PROGRAM_NAME);
    }
    
    private short getProgramFirstColumnIndex(Iterator<Row> rowIt) {
        // skip all the blank rows and rows preceding "Program Name" and find its index
        short firstColumnIndex = -1;

        while (rowIt.hasNext() && firstColumnIndex == -1) {
            Row row = rowIt.next();
            
            log.debug("reading row# {}", row.getRowNum() + 1);

            /*
             * get first defined non empty cell
             * skip empty cells
             * Note: cells which had content before and were set to empty later might still be counted as cells
             */
            short minColIx = row.getFirstCellNum();
            if (minColIx == -1)
                continue;
            
            short maxColIx = row.getLastCellNum();
            for (short colIx = minColIx; colIx < maxColIx; colIx++) {
                Cell cell = row.getCell(colIx);
                if (cellHasProgramName(cell)) {
                	firstColumnIndex = (short) cell.getColumnIndex();
                    break;
                }
            }
        }
        return firstColumnIndex;
    }

    private ProgramDTO importProgramRow(Row row, short firstColumnIndex) {
        log.debug("importing row# {} as a program", row.getRowNum() + 1);
        
        ProgramDTO programDTO = new ProgramDTO();

        // set program idu
        Long programCount = programService.count();
        log.debug("program count: {}", programCount);
        String programIdU = "P" + (programCount + 1);
        log.debug("generated programIdU: {}", programIdU);
        programDTO.setProgramIdU(programIdU);
        
        Cell cell = null;
        short columnIndex = firstColumnIndex;
        
        // set program name
        cell = row.getCell(columnIndex);
        String value = validateNameCell(row, cell, columnIndex, "program name");
        // check for duplicate program name
        Optional<ProgramDTO> optionalProgramDTO = programService.findOneByName(value);
        if (!optionalProgramDTO.isPresent())
            programDTO.setName(value);
        else {
            log.error("row# {} col# {} duplicate program name", row.getRowNum() + 1, columnIndex + 1);
            throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY, ROW_NUM_SPACE + (row.getRowNum() + 1) + SPACE_COL_NUM_SPACE + (columnIndex + 1) + ", duplicate program name");
        }
        
        columnIndex++;
        // set program description
        cell = row.getCell(columnIndex);
        value = validateOptionalStringCell(row, cell, columnIndex, "program description");
        if (value != null) {
            programDTO.setDescription(value);
            log.debug("row# {} col# {} program description: {}", row.getRowNum() + 1, columnIndex + 1, value);
        }
        
        return programService.save(programDTO );
    }

    private EpisodeDTO importEpisode(short episodeCount, ProgramDTO programDTO) {
        log.debug("importing default episode for program name: {}", programDTO.getName());
        
        EpisodeDTO episodeDTO = new EpisodeDTO();
        
        // set episode idu
        log.debug("episode count: {}", episodeCount - 1);
        String episodeIdU = "E" + episodeCount;
        log.debug("generated episodeIdU: {}", episodeIdU);
        episodeDTO.setEpisodeIdU(episodeIdU);
        
        // set episode name
        episodeDTO.setName("Default Episode");

        // set lookup
        episodeDTO.setLookup(programDTO.getProgramIdU() + UNDERSCORE + episodeDTO.getEpisodeIdU());

        // set program id
        episodeDTO.setProgramId(programDTO.getId());

        
        return episodeService.save(episodeDTO );
    }

    private GoalDTO importGoalRow(Row row, short firstColumnIndex, short goalCount, ProgramDTO programDTO, EpisodeDTO episodeDTO, Map<Long, SimpleImmutableEntry<String, Map<Long, String>>> id2AssociatedMap) {
        log.debug("importing row# {} as a goal", row.getRowNum() + 1);
        
        GoalDTO goalDTO = new GoalDTO();
        
        // set goal idu
        log.debug("goal count: {}", goalCount - 1);
        String goalIdU = "G" + goalCount;
        log.debug("generated goalIdU: {}", goalIdU);
        goalDTO.setGoalIdU(goalIdU);
        
        Cell cell = null;
        short columnIndex = firstColumnIndex;
        
        // set goal name
        cell = row.getCell(columnIndex);
        String value = validateNameCell(row, cell, columnIndex, "goal name");
        // check for valid goal name, it should not contain pipe character
        if (value.contains("|")) {
            log.error("row# {} col# {} invalid goal name, contains pipe character/s", row.getRowNum() + 1, columnIndex + 1);
            throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY, ROW_NUM_SPACE + (row.getRowNum() + 1) + SPACE_COL_NUM_SPACE + (columnIndex + 1) + ", invalid goal name, contains pipe character/s");
        }
        // check for duplicate goal name, query goal by name and episode id
        Optional<GoalDTO> optionalGoalDTO = goalService.findOneByNameIgnoreCaseAndEpisodeId(value, episodeDTO.getId());
        if (optionalGoalDTO.isPresent()) {
            log.error("row# {} col# {} duplicate goal name {}", row.getRowNum() + 1, columnIndex + 1, value);
            throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY, ROW_NUM_SPACE + (row.getRowNum() + 1) + SPACE_COL_NUM_SPACE + (columnIndex + 1) + ", duplicate goal name " + value);
        }
        goalDTO.setName(value);

        columnIndex++;
        // set goal ETA
        cell = row.getCell(columnIndex);
        value = validateStringCell(row, cell, columnIndex, "goal ETA");
        short charIndex = 0;
        for (; charIndex < value.length(); charIndex++) {
            if (value.charAt(charIndex) > '9')
                break;
        }
        
        // throw exception if all numbers or alphabets
        if (charIndex == 0 || charIndex == value.length()) {
            log.error("row# {} col# {} invalid goal ETA: {}", row.getRowNum() + 1, columnIndex + 1, value);
            throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY, ROW_NUM_SPACE + (row.getRowNum() + 1) + SPACE_COL_NUM_SPACE + (columnIndex + 1) + " invalid goal ETA: " + value);
        }

        String etaValue = value.substring(0, charIndex).trim();
        String etaUnit = value.substring(charIndex).trim().toUpperCase();

        // set goal ETA value
        try {
            goalDTO.setEtaValue(Integer.valueOf(etaValue));
        } catch (NumberFormatException e) {
            log.error("row# {} col# {} invalid goal ETA value: {}", row.getRowNum() + 1, columnIndex + 1, etaValue);
            throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY, ROW_NUM_SPACE + (row.getRowNum() + 1) + SPACE_COL_NUM_SPACE + (columnIndex + 1) + " invalid goal ETA value: " + etaValue);
        }
        
        TimeUnit timeUnit = validateGoalETA(row, etaUnit, columnIndex);
        goalDTO.setEtaUnit(timeUnit);
        
        columnIndex++;
        // read associated goal names
        cell = row.getCell(columnIndex);
        String associatedNames = validateOptionalStringCell(row, cell, columnIndex, "associated goals");

        // set lookup
        goalDTO.setLookup(programDTO.getProgramIdU() + UNDERSCORE + episodeDTO.getEpisodeIdU() + UNDERSCORE + goalDTO.getGoalIdU());

        // set episodeId
        goalDTO.setEpisodeId(episodeDTO.getId());
        
        // persist goal
        goalDTO = goalService.save(goalDTO );
        
        // keep the associated goal names in the map
        id2AssociatedMap.put(goalDTO.getId(), new SimpleImmutableEntry<String, Map<Long, String>>(associatedNames, new HashMap<Long, String>()));
        
        return goalDTO; 
    }

    private TaskDTO importTaskRow(Row row, short firstColumnIndex, short taskCount, ProgramDTO programDTO, EpisodeDTO episodeDTO, GoalDTO goalDTO, Map<Long, SimpleImmutableEntry<String, Map<Long, String>>> id2AssociatedMap) {
        log.debug("importing row# {} as a task", row.getRowNum() + 1);

        TaskDTO taskDTO = new TaskDTO();
        
        // set task idu
        log.debug("task count: {}", taskCount - 1);
        String taskIdU = "T" + taskCount;
        log.debug("generated taskIdU: {}", taskIdU);
        taskDTO.setTaskIdU(taskIdU);
        
        Cell cell = null;
        short columnIndex = firstColumnIndex;
        
        // set task name
        cell = row.getCell(columnIndex);
        String value = validateNameCell(row, cell, columnIndex, "task name");
        // check for duplicate task name, query task by name and goal id
        Optional<TaskDTO> optionalTaskDTO = taskService.findOneByNameIgnoreCaseAndGoalId(value, goalDTO.getId());
        if (optionalTaskDTO.isPresent()) {
            log.error("row# {} col# {} duplicate task name {}", row.getRowNum() + 1, columnIndex + 1, value);
            throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY, ROW_NUM_SPACE + (row.getRowNum() + 1) + SPACE_COL_NUM_SPACE + (columnIndex + 1) + ", duplicate task name " + value);
        }
        taskDTO.setName(value);

        columnIndex++;
        // set task frequency
        cell = row.getCell(columnIndex);
        value = validateStringCell(row, cell, columnIndex, "task frequency");
        setTaskFrequency(row, value, columnIndex, taskDTO);

        columnIndex++;
        // set task SLA
        cell = row.getCell(columnIndex);
        value = validateStringCell(row, cell, columnIndex, "task SLA");
        setTaskSLA(row, value, columnIndex, taskDTO);

        columnIndex++;
        // set task assignee
        cell = row.getCell(columnIndex);
        value = validateStringCell(row, cell, columnIndex, "task assignee");
        taskDTO.setAssignee("${" + value + "}");

        columnIndex++;
        // read associated task names
        cell = row.getCell(columnIndex);
        String associatedNames = validateOptionalStringCell(row, cell, columnIndex, "associated tasks");
        
        // set task type
        taskDTO.setType(Type.HUMAN);
        
        // set lookup
        taskDTO.setLookup(programDTO.getProgramIdU() + UNDERSCORE + episodeDTO.getEpisodeIdU() + UNDERSCORE + goalDTO.getGoalIdU() + UNDERSCORE + taskDTO.getTaskIdU());

        // set goalId
        taskDTO.setGoalId(goalDTO.getId());
        
        // persist task
        taskDTO = taskService.save(taskDTO ); 

        // keep associated task names in the map

        // get task id to associated task names map
        SimpleImmutableEntry<String, Map<Long, String>> goalId2TaskAssociatedMap = id2AssociatedMap.get(goalDTO.getId());
        if (goalId2TaskAssociatedMap == null) {
            log.error("row# {} unexpected error, goalId2TaskAssociatedMap does not contain goal id for the task", row.getRowNum() + 1);
            throw new InternalServerErrorException(ROW_NUM_SPACE + (row.getRowNum() + 1) + " unexpected error, goalId2TaskAssociatedMap does not contain goal id for the task");
        }
        Map<Long, String> taskId2AssociatedMap = goalId2TaskAssociatedMap.getValue();
        if (taskId2AssociatedMap == null) {
            log.error("row# {} unexpected error, task id to associated Map is null for the goal name: {}", row.getRowNum() + 1, goalDTO.getName());
            throw new InternalServerErrorException(ROW_NUM_SPACE + (row.getRowNum() + 1) + " unexpected error, task id to associated Map is null for the goal name: " + goalDTO.getName());
        }
        taskId2AssociatedMap.put(taskDTO.getId(), associatedNames);

        return taskDTO;
    }

    private void setTaskSLA(Row row, String value, short columnIndex, TaskDTO taskDTO) {
        short charIndex = 0;
        for (; charIndex < value.length(); charIndex++) {
            if (value.charAt(charIndex) > '9')
                break;
        }
        
        // throw exception if all alphabets
        if (charIndex == 0) {
            log.error("row# {} col# {} invalid task SLA: {}", row.getRowNum() + 1, columnIndex + 1, value);
            throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY, ROW_NUM_SPACE + (row.getRowNum() + 1) + SPACE_COL_NUM_SPACE + (columnIndex + 1) + " invalid task SLA: " + value);
        }

        String slaValue = value.substring(0, charIndex).trim();
        String slaUnit = value.substring(charIndex).trim();

        // throw exception if SLA unit other than hours
        if (!slaUnit.isEmpty() && !slaUnit.equalsIgnoreCase(HOURS)) {
            log.error("row# {} col# {} invalid task SLA: {}", row.getRowNum() + 1, columnIndex + 1, value);
            throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY, ROW_NUM_SPACE + (row.getRowNum() + 1) + SPACE_COL_NUM_SPACE + (columnIndex + 1) + " invalid task SLA: " + value);
        }
        
        // set task SLA value
        try {
            taskDTO.setSla(Integer.valueOf(slaValue));
        } catch (NumberFormatException e) {
            log.error("row# {} col# {} invalid task SLA value: {}", row.getRowNum() + 1, columnIndex + 1, slaValue);
            throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY, ROW_NUM_SPACE + (row.getRowNum() + 1) + SPACE_COL_NUM_SPACE + (columnIndex + 1) + " invalid task SLA value: " + slaValue);
        }
    }

    private void setTaskFrequency(Row row, String value, short columnIndex, TaskDTO taskDTO) {
        short charIndex = 0;
        for (; charIndex < value.length(); charIndex++) {
            if (value.charAt(charIndex) > '9')
                break;
        }
        
        // throw exception if all numbers
        if (charIndex == value.length()) {
            log.error("row# {} col# {} invalid task frequency: {}", row.getRowNum() + 1, columnIndex + 1, value);
            throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY, ROW_NUM_SPACE + (row.getRowNum() + 1) + SPACE_COL_NUM_SPACE + (columnIndex + 1) + " invalid task frequency: " + value);
        }

        String frequencyValue = value.substring(0, charIndex).trim();
        String frequencyUnit = value.substring(charIndex).trim().toUpperCase();

        // set task frequency value
        try {
            taskDTO.setRepeatFrequencyValue(frequencyValue.isEmpty() ? 0 : Integer.valueOf(frequencyValue));
        } catch (NumberFormatException e) {
            log.error("row# {} col# {} invalid task frequency value: {}", row.getRowNum() + 1, columnIndex + 1, frequencyValue);
            throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY, ROW_NUM_SPACE + (row.getRowNum() + 1) + SPACE_COL_NUM_SPACE + (columnIndex + 1) + " invalid task frequency value: " + frequencyValue);
        }
        
        // do not repeat if ONCE
        if (frequencyUnit.equals(ONCE))
            taskDTO.setIsRepeat(Boolean.FALSE);
        else {
            taskDTO.setIsRepeat(Boolean.TRUE);
            taskDTO.setRepeatEvent(CaseExecutionEvent.COMPLETE);
        }
        
        //set task frequency unit
        TimeUnit timeUnit = validateTimeUnit(row, frequencyUnit, columnIndex, "task frequency");
        taskDTO.setRepeatFrequencyUnit(timeUnit);
    }

    private TimeUnit validateGoalETA(Row row, String timeUnitStr, short columnIndex) {
        TimeUnit timeUnit = validateTimeUnit(row, timeUnitStr, columnIndex, "goal ETA");
   
        // goal ETA can not be SOS or ONCE
        if (timeUnit.equals(TimeUnit.SOS) || timeUnit.equals(TimeUnit.ONCE)) {
            log.error("row# {} col# {} invalid goal ETA unit: {}", row.getRowNum() + 1, columnIndex + 1, timeUnit);
            throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY, ROW_NUM_SPACE + (row.getRowNum() + 1) + SPACE_COL_NUM_SPACE + (columnIndex + 1) + " invalid goal ETA unit: " + timeUnit);
        }

        return timeUnit;
    }

    private TimeUnit validateTimeUnit(Row row, String timeUnitStr, short columnIndex, String msgPart) {
        TimeUnit timeUnit = null;
        switch (timeUnitStr) {
        case DAY:
            timeUnit = TimeUnit.DAY;
            break;
        case DAYS:
            timeUnit = TimeUnit.DAYS;
            break;
        case DAILY:
            timeUnit = TimeUnit.DAILY;
            break;
        case WEEK:
            timeUnit = TimeUnit.WEEK;
            break;
        case WEEKS:
            timeUnit = TimeUnit.WEEKS;
            break;
        case WEEKLY:
            timeUnit = TimeUnit.WEEKLY;
            break;
        case MONTH:
            timeUnit = TimeUnit.MONTH;
            break;
        case MONTHS:
            timeUnit = TimeUnit.MONTHS;
            break;
        case MONTHLY:
            timeUnit = TimeUnit.MONTHLY;
            break;
        case YEAR:
            timeUnit = TimeUnit.YEAR;
            break;
        case YEARS:
            timeUnit = TimeUnit.YEARS;
            break;
        case YEARLY:
            timeUnit = TimeUnit.YEARLY;
            break;
        case SOS:
            timeUnit = TimeUnit.SOS;
            break;
        case ONCE:
            timeUnit = TimeUnit.ONCE;
            break;
        default:
            log.error("row# {} col# {} invalid {} unit: {}", row.getRowNum() + 1, columnIndex + 1, msgPart, timeUnitStr);
            throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY, ROW_NUM_SPACE + (row.getRowNum() + 1) + SPACE_COL_NUM_SPACE + (columnIndex + 1) + " invalid " + msgPart +  " unit: " + timeUnitStr);
        }
        return timeUnit;
    }

    // validate that cell is not null, non empty and does not contain pipe character/s
    private String validateNameCell(Row row, Cell cell, short columnIndex, String msgPart) {
        String value = validateStringCell(row, cell, columnIndex, msgPart);
        //check for valid task name
        if (value.contains("|")) {
            log.error("row# {} col# {} invalid {}, contains pipe character/s", row.getRowNum() + 1, columnIndex + 1, msgPart);
            throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY, ROW_NUM_SPACE + (row.getRowNum() + 1) + SPACE_COL_NUM_SPACE + (columnIndex + 1) + ", invalid " + msgPart + ", contains pipe character/s");
        }
        return value;
    }

    // validate that cell is not null and non empty
    private String validateStringCell(Row row, Cell cell, short columnIndex, String msgPart) {
        if (cell == null) {
            log.error(ROW_NUM_SPACE_OPEN_CLOSE_CURLY_COL_NUM_SPACE_OPEN_CLOSE_CURLY_SPACE_UNDEFINED_SPACE_OPEN_CLOSE_CURLY, row.getRowNum() + 1, columnIndex + 1, msgPart);
            throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY, ROW_NUM_SPACE + (row.getRowNum() + 1) + SPACE_COL_NUM_SPACE + (columnIndex + 1) + SPACE_UNDEFINED_SPACE + msgPart);
        }
        if (!(cell.getCellType().equals(CellType.STRING) || cell.getCellType().equals(CellType.BLANK))) {
            log.error("row# {} col# {} {} must be string, found {}", row.getRowNum() + 1, columnIndex + 1, msgPart, cell.getCellType());
            throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY, ROW_NUM_SPACE + (row.getRowNum() + 1) + SPACE_COL_NUM_SPACE + (columnIndex + 1) + COMMA_SPACE + msgPart + " must be string, found " + cell.getCellType());
        }
        String value = cell.getStringCellValue();
        if (value == null) {
            log.error(ROW_NUM_SPACE_OPEN_CLOSE_CURLY_COL_NUM_SPACE_OPEN_CLOSE_CURLY_SPACE_UNDEFINED_SPACE_OPEN_CLOSE_CURLY, row.getRowNum() + 1, columnIndex + 1, msgPart);
            throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY, ROW_NUM_SPACE + (row.getRowNum() + 1) + SPACE_COL_NUM_SPACE + (columnIndex + 1) + SPACE_UNDEFINED_SPACE + msgPart);
        }
        value = value.trim();
        if (value.isEmpty()) {
            log.error(ROW_NUM_SPACE_OPEN_CLOSE_CURLY_COL_NUM_SPACE_OPEN_CLOSE_CURLY_SPACE_UNDEFINED_SPACE_OPEN_CLOSE_CURLY, row.getRowNum() + 1, columnIndex + 1, msgPart);
            throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY, ROW_NUM_SPACE + (row.getRowNum() + 1) + SPACE_COL_NUM_SPACE + (columnIndex + 1) + SPACE_UNDEFINED_SPACE + msgPart);
        }
        return value;
    }

    // validate that cell is null or type is string
    private String validateOptionalStringCell(Row row, Cell cell, short columnIndex, String msgPart) {
        if (cell == null) {
            return null;
        }
        if (!(cell.getCellType().equals(CellType.STRING) || cell.getCellType().equals(CellType.BLANK))) {
            log.error("row# {} col# {} {} must be string, found {}", row.getRowNum() + 1, columnIndex + 1, msgPart, cell.getCellType());
            throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY, ROW_NUM_SPACE + (row.getRowNum() + 1) + SPACE_COL_NUM_SPACE + (columnIndex + 1) + COMMA_SPACE + msgPart + " must be string, found " + cell.getCellType());
        }
        String value = cell.getStringCellValue();
        if (value != null) {
            value = value.trim(); 
        }
        
        return value;
    }
}