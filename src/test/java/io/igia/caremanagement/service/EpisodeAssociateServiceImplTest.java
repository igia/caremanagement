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
import io.igia.caremanagement.domain.EpisodeAssociate;
import io.igia.caremanagement.domain.enumeration.CaseExecutionEvent;
import io.igia.caremanagement.repository.EpisodeAssociateRepository;
import io.igia.caremanagement.service.dto.EpisodeAssociateDTO;
import io.igia.caremanagement.service.impl.EpisodeAssociateServiceImpl;
import io.igia.caremanagement.service.mapper.EpisodeAssociateMapper;

public class EpisodeAssociateServiceImplTest {
    
    @InjectMocks
    private EpisodeAssociateServiceImpl episodeAssociateServiceImpl;

    @Mock
    private EpisodeAssociateRepository episodeAssociateRepository;

    @Mock
    private EpisodeAssociateMapper episodeAssociateMapper;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testFindOneByEpisodeId() throws Exception {
        EpisodeAssociate episodeAssociate= new EpisodeAssociate();
        episodeAssociate.setAssociateEvent(CaseExecutionEvent.COMPLETE);
        episodeAssociate.setAssociateOn(11L);
        episodeAssociate.setId(2001L);
        Episode episode = new Episode();
        episode.setId(1001L);
        episodeAssociate.setEpisode(episode);
        List<EpisodeAssociate> episodeAssociateList= new ArrayList<>();
        episodeAssociateList.add(episodeAssociate);
        EpisodeAssociateDTO episodeAssociateDTO = new EpisodeAssociateDTO();
        episodeAssociateDTO.setAssociateEvent(CaseExecutionEvent.COMPLETE);
        episodeAssociateDTO.setAssociateOn(11L);
        episodeAssociateDTO.setId(2001L);
        episodeAssociateDTO.setEpisodeId(1001L);
        List<EpisodeAssociateDTO> episodeAssociateDTOList= new ArrayList<>();
        episodeAssociateDTOList.add(episodeAssociateDTO);
        Mockito.when(episodeAssociateRepository.findAllByEpisodeId(1001L)).thenReturn(episodeAssociateList);
        Mockito.when(episodeAssociateMapper.toDto(episodeAssociateList)).thenReturn(episodeAssociateDTOList);
        List<EpisodeAssociateDTO> episodeAssociateDTOResult = episodeAssociateServiceImpl.findAllByEpisodeId(1001L);
        assertThat(episodeAssociateDTOResult.get(0).getId()).isEqualTo(2001L);
        assertEquals(CaseExecutionEvent.COMPLETE, episodeAssociateDTOResult.get(0).getAssociateEvent());
        assertThat(episodeAssociateDTOResult.get(0).getAssociateOn()).isEqualTo(11L);
        assertThat(episodeAssociateDTOResult.get(0).getEpisodeId()).isEqualTo(1001L);
    }
    
    @Test
    public void testFindOneByAssociateOn() throws Exception {
        EpisodeAssociate episodeAssociate= new EpisodeAssociate();
        episodeAssociate.setAssociateEvent(CaseExecutionEvent.COMPLETE);
        episodeAssociate.setAssociateOn(11L);
        episodeAssociate.setId(2001L);
        Episode episode = new Episode();
        episode.setId(1001L);
        episodeAssociate.setEpisode(episode);
        List<EpisodeAssociate> episodeAssociateList= new ArrayList<>();
        episodeAssociateList.add(episodeAssociate);
        EpisodeAssociateDTO episodeAssociateDTO = new EpisodeAssociateDTO();
        episodeAssociateDTO.setAssociateEvent(CaseExecutionEvent.COMPLETE);
        episodeAssociateDTO.setAssociateOn(11L);
        episodeAssociateDTO.setId(2001L);
        episodeAssociateDTO.setEpisodeId(1001L);
        List<EpisodeAssociateDTO> episodeAssociateDTOList= new ArrayList<>();
        episodeAssociateDTOList.add(episodeAssociateDTO);
        Mockito.when(episodeAssociateRepository.findAllByAssociateOn(11L)).thenReturn(episodeAssociateList);
        Mockito.when(episodeAssociateMapper.toDto(episodeAssociateList)).thenReturn(episodeAssociateDTOList);
        List<EpisodeAssociateDTO> episodeAssociateDTOResult = episodeAssociateServiceImpl.findAllByAssociateOn(11L);
        assertThat(episodeAssociateDTOResult.get(0).getId()).isEqualTo(2001L);
        assertEquals(CaseExecutionEvent.COMPLETE, episodeAssociateDTOResult.get(0).getAssociateEvent());
        assertThat(episodeAssociateDTOResult.get(0).getAssociateOn()).isEqualTo(11L);
        assertThat(episodeAssociateDTOResult.get(0).getEpisodeId()).isEqualTo(1001L);
    }

}
