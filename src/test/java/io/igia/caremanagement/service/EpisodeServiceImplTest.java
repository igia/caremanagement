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
import java.util.Optional;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import io.igia.caremanagement.domain.Episode;
import io.igia.caremanagement.domain.Program;
import io.igia.caremanagement.repository.EpisodeRepository;
import io.igia.caremanagement.service.dto.EpisodeDTO;
import io.igia.caremanagement.service.impl.EpisodeServiceImpl;
import io.igia.caremanagement.service.mapper.EpisodeMapper;

public class EpisodeServiceImplTest {

    @InjectMocks
    private EpisodeServiceImpl episodeServiceImpl;

    @Mock
    private EpisodeRepository episodeRepository;

    @Mock
    private EpisodeMapper episodeMapper;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
    }

/*    @Test
    public void testFindOneByEpisodeIdU() throws Exception {
        Episode episode = new Episode();
        episode.setId(1001L);
        episode.episodeIdU("E1");
        episode.setDescription("description");
        episode.setEntryCriteria("entryCriteria");
        episode.setName("name");
        Program program = new Program();
        program.setId(951L);
        episode.setProgram(program);
        EpisodeDTO episodeDTO = new EpisodeDTO();
        episodeDTO.setId(1001L);
        episodeDTO.setEpisodeIdU("E1");
        episodeDTO.setDescription("description");
        episodeDTO.setEntryCriteria("entryCriteria");
        episodeDTO.setName("name");
        episodeDTO.setProgramId(951L);
        Mockito.when(episodeRepository.findOneByEpisodeIdU("E1")).thenReturn(Optional.of(episode));
        Mockito.when(episodeMapper.toDto(episode)).thenReturn(episodeDTO);
        Optional<EpisodeDTO> episodeDTOResult = episodeServiceImpl.findOneByEpisodeIdU("E1");
        assertThat(episodeDTOResult.get().getId()).isEqualTo(1001L);
        assertEquals("E1", episodeDTOResult.get().getEpisodeIdU());
        assertEquals("description", episodeDTOResult.get().getDescription());
        assertEquals("entryCriteria", episodeDTOResult.get().getEntryCriteria());
        assertEquals("name", episodeDTOResult.get().getName());
        assertThat(episodeDTOResult.get().getProgramId()).isEqualTo(951L);
    }
*/    
    @Test
    public void findOneByProgramId() throws Exception {
        Episode episode = new Episode();
        episode.setId(1001L);
        episode.episodeIdU("E1");
        episode.setDescription("description");
        episode.setEntryCriteria("entryCriteria");
        episode.setName("name");
        Program program = new Program();
        program.setId(951L);
        episode.setProgram(program);
        List<Episode> episodeList = new ArrayList<>();
        episodeList.add(episode);
        EpisodeDTO episodeDTO = new EpisodeDTO();
        episodeDTO.setId(1001L);
        episodeDTO.setEpisodeIdU("E1");
        episodeDTO.setDescription("description");
        episodeDTO.setEntryCriteria("entryCriteria");
        episodeDTO.setName("name");
        episodeDTO.setProgramId(951L);
        List<EpisodeDTO> episodeDTOList = new ArrayList<>();
        episodeDTOList.add(episodeDTO);
        Mockito.when(episodeRepository.findAllByProgramId(951L)).thenReturn(episodeList);
        Mockito.when(episodeMapper.toDto(episodeList)).thenReturn(episodeDTOList);
        List<EpisodeDTO> episodeDTOResult = episodeServiceImpl.findAllByProgramId(951L);
        assertThat(episodeDTOResult.get(0).getId()).isEqualTo(1001L);
        assertEquals("E1", episodeDTOResult.get(0).getEpisodeIdU());
        assertEquals("description", episodeDTOResult.get(0).getDescription());
        assertEquals("entryCriteria", episodeDTOResult.get(0).getEntryCriteria());
        assertEquals("name", episodeDTOResult.get(0).getName());
        assertThat(episodeDTOResult.get(0).getProgramId()).isEqualTo(951L);
    }


}
