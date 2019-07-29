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

import io.igia.caremanagement.domain.Task;
import io.igia.caremanagement.domain.TaskAssociate;
import io.igia.caremanagement.domain.enumeration.CaseExecutionEvent;
import io.igia.caremanagement.repository.TaskAssociateRepository;
import io.igia.caremanagement.service.dto.TaskAssociateDTO;
import io.igia.caremanagement.service.impl.TaskAssociateServiceImpl;
import io.igia.caremanagement.service.mapper.TaskAssociateMapper;



public class TaskAssociateServiceImplTest {

    @InjectMocks
    private TaskAssociateServiceImpl taskAssociateServiceImpl;

    @Mock
    private TaskAssociateRepository taskAssociateRepository;

    @Mock
    private TaskAssociateMapper taskAssociateMapper;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testFindOneByAssociateOn() throws Exception {
        TaskAssociate taskAssociate = new TaskAssociate();
        taskAssociate.setAssociateEvent(CaseExecutionEvent.COMPLETE);
        taskAssociate.setAssociateOn(11L);
        taskAssociate.setId(2051L);
        Task task = new Task();
        task.setId(1351L);
        taskAssociate.setTask(task);
        List<TaskAssociate> taskAssociateList = new ArrayList<>();
        taskAssociateList.add(taskAssociate);
        TaskAssociateDTO taskAssociateDTO = new TaskAssociateDTO();
        taskAssociateDTO.setAssociateEvent(CaseExecutionEvent.COMPLETE);
        taskAssociateDTO.setAssociateOn(11L);
        taskAssociateDTO.setId(2051L);
        taskAssociateDTO.setTaskId(1351L);
        List<TaskAssociateDTO> taskAssociateDTOList = new ArrayList<>();
        taskAssociateDTOList.add(taskAssociateDTO);
        Mockito.when(taskAssociateRepository.findAllByAssociateOn(11L)).thenReturn(taskAssociateList);
        Mockito.when(taskAssociateMapper.toDto(taskAssociateList)).thenReturn(taskAssociateDTOList);
        List<TaskAssociateDTO> taskAssociateDTOResult = taskAssociateServiceImpl.findAllByAssociateOn(11L);
        assertThat(taskAssociateDTOResult.get(0).getId()).isEqualTo(2051L);
        assertEquals(CaseExecutionEvent.COMPLETE, taskAssociateDTOResult.get(0).getAssociateEvent());
        assertThat(taskAssociateDTOResult.get(0).getAssociateOn()).isEqualTo(11L);
        assertThat(taskAssociateDTOResult.get(0).getTaskId()).isEqualTo(1351L);
    }

    @Test
    public void testFindOneByTaskId() throws Exception {
        TaskAssociate taskAssociate = new TaskAssociate();
        taskAssociate.setAssociateEvent(CaseExecutionEvent.COMPLETE);
        taskAssociate.setAssociateOn(11L);
        taskAssociate.setId(2051L);
        Task task = new Task();
        task.setId(1351L);
        taskAssociate.setTask(task);
        List<TaskAssociate> taskAssociateList = new ArrayList<>();
        taskAssociateList.add(taskAssociate);
        TaskAssociateDTO taskAssociateDTO = new TaskAssociateDTO();
        taskAssociateDTO.setAssociateEvent(CaseExecutionEvent.COMPLETE);
        taskAssociateDTO.setAssociateOn(11L);
        taskAssociateDTO.setId(2051L);
        taskAssociateDTO.setTaskId(1351L);
        List<TaskAssociateDTO> taskAssociateDTOList = new ArrayList<>();
        taskAssociateDTOList.add(taskAssociateDTO);
        Mockito.when(taskAssociateRepository.findAllByTaskId(1351L)).thenReturn(taskAssociateList);
        Mockito.when(taskAssociateMapper.toDto(taskAssociateList)).thenReturn(taskAssociateDTOList);
        List<TaskAssociateDTO> taskAssociateDTOResult = taskAssociateServiceImpl.findAllByTaskId(1351L);
        assertThat(taskAssociateDTOResult.get(0).getId()).isEqualTo(2051L);
        assertEquals(CaseExecutionEvent.COMPLETE, taskAssociateDTOResult.get(0).getAssociateEvent());
        assertThat(taskAssociateDTOResult.get(0).getAssociateOn()).isEqualTo(11L);
        assertThat(taskAssociateDTOResult.get(0).getTaskId()).isEqualTo(1351L);
    }

}
