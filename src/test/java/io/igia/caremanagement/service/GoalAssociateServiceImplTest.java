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
import io.igia.caremanagement.domain.GoalAssociate;
import io.igia.caremanagement.domain.enumeration.CaseExecutionEvent;
import io.igia.caremanagement.repository.GoalAssociateRepository;
import io.igia.caremanagement.service.dto.GoalAssociateDTO;
import io.igia.caremanagement.service.impl.GoalAssociateServiceImpl;
import io.igia.caremanagement.service.mapper.GoalAssociateMapper;

public class GoalAssociateServiceImplTest {

    @InjectMocks
    private GoalAssociateServiceImpl goalAssociateServiceImpl;

    @Mock
    private GoalAssociateRepository goalAssociateRepository;

    @Mock
    private GoalAssociateMapper goalAssociateMapper;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testFindOneByAssociateOn() throws Exception {
        GoalAssociate goalAssociate = new GoalAssociate();
        goalAssociate.setAssociateEvent(CaseExecutionEvent.COMPLETE);
        goalAssociate.setAssociateOn(11L);
        goalAssociate.setId(2051L);
        Goal goal = new Goal();
        goal.setId(1051L);
        goalAssociate.setGoal(goal);
        List<GoalAssociate> goalAssociateList = new ArrayList<>();
        goalAssociateList.add(goalAssociate);
        GoalAssociateDTO goalAssociateDTO = new GoalAssociateDTO();
        goalAssociateDTO.setAssociateEvent(CaseExecutionEvent.COMPLETE);
        goalAssociateDTO.setAssociateOn(11L);
        goalAssociateDTO.setId(2051L);
        goalAssociateDTO.setGoalId(1051L);
        List<GoalAssociateDTO> goalAssociateDTOList = new ArrayList<>();
        goalAssociateDTOList.add(goalAssociateDTO);
        Mockito.when(goalAssociateRepository.findAllByAssociateOn(11L)).thenReturn(goalAssociateList);
        Mockito.when(goalAssociateMapper.toDto(goalAssociateList)).thenReturn(goalAssociateDTOList);
        List<GoalAssociateDTO> goalAssociateDTOResult = goalAssociateServiceImpl.findAllByAssociateOn(11L);
        assertThat(goalAssociateDTOResult.get(0).getId()).isEqualTo(2051L);
        assertEquals(CaseExecutionEvent.COMPLETE, goalAssociateDTOResult.get(0).getAssociateEvent());
        assertThat(goalAssociateDTOResult.get(0).getAssociateOn()).isEqualTo(11L);
        assertThat(goalAssociateDTOResult.get(0).getGoalId()).isEqualTo(1051L);
    }
    
    @Test
    public void testFindOneByGoalId() throws Exception {
        GoalAssociate goalAssociate = new GoalAssociate();
        goalAssociate.setAssociateEvent(CaseExecutionEvent.COMPLETE);
        goalAssociate.setAssociateOn(11L);
        goalAssociate.setId(2051L);
        Goal goal = new Goal();
        goal.setId(1051L);
        goalAssociate.setGoal(goal);
        List<GoalAssociate> goalAssociateList = new ArrayList<>();
        goalAssociateList.add(goalAssociate);
        GoalAssociateDTO goalAssociateDTO = new GoalAssociateDTO();
        goalAssociateDTO.setAssociateEvent(CaseExecutionEvent.COMPLETE);
        goalAssociateDTO.setAssociateOn(11L);
        goalAssociateDTO.setId(2051L);
        goalAssociateDTO.setGoalId(1051L);
        List<GoalAssociateDTO> goalAssociateDTOList = new ArrayList<>();
        goalAssociateDTOList.add(goalAssociateDTO);
        Mockito.when(goalAssociateRepository.findAllByGoalId(1051L)).thenReturn(goalAssociateList);
        Mockito.when(goalAssociateMapper.toDto(goalAssociateList)).thenReturn(goalAssociateDTOList);
        List<GoalAssociateDTO> goalAssociateDTOResult = goalAssociateServiceImpl.findAllByGoalId(1051L);
        assertThat(goalAssociateDTOResult.get(0).getId()).isEqualTo(2051L);
        assertEquals(CaseExecutionEvent.COMPLETE, goalAssociateDTOResult.get(0).getAssociateEvent());
        assertThat(goalAssociateDTOResult.get(0).getAssociateOn()).isEqualTo(11L);
        assertThat(goalAssociateDTOResult.get(0).getGoalId()).isEqualTo(1051L);
    }
}
