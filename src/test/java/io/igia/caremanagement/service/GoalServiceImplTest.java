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

import io.igia.caremanagement.domain.Episode;
import io.igia.caremanagement.domain.Goal;
import io.igia.caremanagement.repository.GoalRepository;
import io.igia.caremanagement.service.dto.GoalDTO;
import io.igia.caremanagement.service.impl.GoalServiceImpl;
import io.igia.caremanagement.service.mapper.GoalMapper;


public class GoalServiceImplTest {

    @InjectMocks
    private GoalServiceImpl goalServiceImpl;

    @Mock
    private GoalRepository goalRepository;

    @Mock
    private GoalMapper goalMapper;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
    }

    /*@Test
    public void testFindOneByGoalIdU() throws Exception {
        Goal goal = new Goal();
        goal.setId(1051L);
        goal.setGoalIdU("G1");
        goal.setName("name");
        goal.setDescription("description");
        goal.setEntryCriteria("entryCriteria");
        //goal.setEta("eta");
        Episode episode = new Episode();
        episode.setId(1001L);
        goal.setEpisode(episode); 
        GoalDTO goalDTO = new GoalDTO();
        goalDTO.setId(1051L);
        goalDTO.setGoalIdU("G1");
        goalDTO.setName("name");
        goalDTO.setDescription("description");
        goalDTO.setEntryCriteria("entryCriteria");
        //goalDTO.setEta("eta");
        goalDTO.setEpisodeId(1001L);
        Mockito.when(goalRepository.findOneByGoalIdU("G1")).thenReturn(Optional.of(goal));
        Mockito.when(goalMapper.toDto(goal)).thenReturn(goalDTO);
        Optional<GoalDTO> goalDTOResult = goalServiceImpl.findOneByGoalIdU("G1");
        assertThat(goalDTOResult.get().getId()).isEqualTo(1051L);
        assertEquals("G1", goalDTOResult.get().getGoalIdU());
        assertEquals("description", goalDTOResult.get().getDescription());
        assertEquals("name", goalDTOResult.get().getName());
        assertEquals("description", goalDTOResult.get().getDescription());
        assertEquals("entryCriteria", goalDTOResult.get().getEntryCriteria());
        //assertEquals("eta", goalDTOResult.get().getEta());
        assertThat(goalDTOResult.get().getEpisodeId()).isEqualTo(1001L);
    }*/
    
    @Test
    public void testFindOneByEpisodeId() throws Exception {
        Goal goal = new Goal();
        goal.setId(1051L);
        goal.setGoalIdU("G1");
        goal.setName("name");
        goal.setDescription("description");
        goal.setEntryCriteria("entryCriteria");
        //goal.setEta("eta");
        Episode episode = new Episode();
        episode.setId(1001L);
        goal.setEpisode(episode);
        List<Goal> goalList = new ArrayList<>();
        goalList.add(goal);
        GoalDTO goalDTO = new GoalDTO();
        goalDTO.setId(1051L);
        goalDTO.setGoalIdU("G1");
        goalDTO.setName("name");
        goalDTO.setDescription("description");
        goalDTO.setEntryCriteria("entryCriteria");
        //goalDTO.setEta("eta");
        goalDTO.setEpisodeId(1001L);
        List<GoalDTO> goalDTOList = new ArrayList<>();
        goalDTOList.add(goalDTO);
        Mockito.when(goalRepository.findAllByEpisodeId(1001L)).thenReturn(goalList);
        Mockito.when(goalMapper.toDto(goalList)).thenReturn(goalDTOList);
        List<GoalDTO> goalDTOResult = goalServiceImpl.findAllByEpisodeId(1001L);
        assertThat(goalDTOResult.get(0).getId()).isEqualTo(1051L);
        assertEquals("G1", goalDTOResult.get(0).getGoalIdU());
        assertEquals("description", goalDTOResult.get(0).getDescription());
        assertEquals("name", goalDTOResult.get(0).getName());
        assertEquals("description", goalDTOResult.get(0).getDescription());
        assertEquals("entryCriteria", goalDTOResult.get(0).getEntryCriteria());
        //assertEquals("eta", goalDTOResult.get(0).getEta());
        assertThat(goalDTOResult.get(0).getEpisodeId()).isEqualTo(1001L);
    }

}
