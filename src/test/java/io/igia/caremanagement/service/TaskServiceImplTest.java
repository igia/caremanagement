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
package io.igia.caremanagement.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import io.igia.caremanagement.domain.Goal;
import io.igia.caremanagement.domain.Task;
import io.igia.caremanagement.domain.enumeration.CaseExecutionEvent;
import io.igia.caremanagement.domain.enumeration.Type;
import io.igia.caremanagement.repository.TaskRepository;
import io.igia.caremanagement.service.dto.TaskDTO;
import io.igia.caremanagement.service.impl.TaskServiceImpl;
import io.igia.caremanagement.service.mapper.TaskMapper;

public class TaskServiceImplTest {

    @InjectMocks
    private TaskServiceImpl taskServiceImpl;

    @Mock
    private TaskRepository taskRepository;

    @Mock
    private TaskMapper taskMapper;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
    }
    
/*    @Test
    public void testFindOneByTaskIdU() throws Exception {
        Task task = new Task();
        task.setId(1101L);
        task.setTaskIdU("T1");
        task.setDescription("description");
        task.setName("name");
        task.setType(Type.HUMAN);
        task.setTypeRef("typeRef");
        task.setAssignee("assignee");
        task.setEntryCriteria("entryCriteria");
        task.setRepeatFrequencyValue(1);
        task.setRepeatEvent(CaseExecutionEvent.COMPLETE);
        Goal goal = new Goal();
        goal.setId(1051L);
        task.setGoal(goal);
        Form form = new Form();
        form.setId(1101L);
        
        TaskDTO taskDTO = new TaskDTO();
        taskDTO.setId(1101L);
        taskDTO.setTaskIdU("T1");
        taskDTO.setDescription("description");
        taskDTO.setName("name");
        taskDTO.setType(Type.HUMAN);
        taskDTO.setTypeRef("typeRef");
        taskDTO.setAssignee("assignee");
        taskDTO.setEntryCriteria("entryCriteria");
        taskDTO.setRepeatFrequencyValue(1);
        taskDTO.setRepeatEvent(CaseExecutionEvent.COMPLETE);
        taskDTO.setGoalId(1051L);
        
        Mockito.when(taskRepository.findOneByTaskIdU("F1")).thenReturn(task);
        Mockito.when(taskMapper.toDto(task)).thenReturn(taskDTO);
        TaskDTO taskDTOResult = taskServiceImpl.findOneByTaskIdU("F1");
        assertThat(taskDTOResult.getId()).isEqualTo(1101L);
        assertEquals("T1", taskDTOResult.getTaskIdU());
        assertEquals("name", taskDTOResult.getName());
        assertEquals("description", taskDTOResult.getDescription());        
    }*/
    
    @Test
    public void testFindOneByGoalId() throws Exception {
        Task task = new Task();
        task.setId(1101L);
        task.setTaskIdU("T1");
        task.setDescription("description");
        task.setName("name");
        task.setType(Type.HUMAN);
        task.setTypeRef("typeRef");
        task.setAssignee("assignee");
        task.setEntryCriteria("entryCriteria");
        task.setRepeatFrequencyValue(1);
        task.setRepeatEvent(CaseExecutionEvent.COMPLETE);
        Goal goal = new Goal();
        goal.setId(1051L);
        task.setGoal(goal);
        
        List<Task> taskList = new ArrayList<>();
        taskList.add(task);
        TaskDTO taskDTO = new TaskDTO();
        taskDTO.setId(1101L);
        taskDTO.setTaskIdU("T1");
        taskDTO.setDescription("description");
        taskDTO.setName("name");
        taskDTO.setType(Type.HUMAN);
        taskDTO.setTypeRef("typeRef");
        taskDTO.setAssignee("assignee");
        taskDTO.setEntryCriteria("entryCriteria");
        taskDTO.setRepeatFrequencyValue(1);
        taskDTO.setRepeatEvent(CaseExecutionEvent.COMPLETE);
        taskDTO.setGoalId(1051L);
        List<TaskDTO> taskListDTO = new ArrayList<>();
        taskListDTO.add(taskDTO);
        Mockito.when(taskRepository.findAllByGoalId(1051L)).thenReturn(taskList);
        Mockito.when(taskMapper.toDto(taskList)).thenReturn(taskListDTO);
        List<TaskDTO> taskDTOResult = taskServiceImpl.findAllByGoalId(1051L);
        assertThat(taskDTOResult.get(0).getId()).isEqualTo(1101L);
        assertEquals("T1", taskDTOResult.get(0).getTaskIdU());
        assertEquals("name", taskDTOResult.get(0).getName());
        assertEquals("description", taskDTOResult.get(0).getDescription());
        assertThat(taskDTOResult.get(0).getGoalId()).isEqualTo(1051L);
    }
}
