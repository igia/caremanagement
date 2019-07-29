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

import java.util.Optional;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import io.igia.caremanagement.domain.Program;
import io.igia.caremanagement.repository.ProgramRepository;
import io.igia.caremanagement.service.dto.ProgramDTO;
import io.igia.caremanagement.service.impl.ProgramServiceImpl;
import io.igia.caremanagement.service.mapper.ProgramMapper;

public class ProgramServiceImplTest {

    @InjectMocks
    private ProgramServiceImpl programServiceImpl;

    @Mock
    private ProgramRepository programRepository;

    @Mock
    private ProgramMapper programMapper;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testFindOneByProgramIdU() throws Exception {
        Program program = new Program();
        program.setId(951L);
        program.setProgramIdU("P1");
        program.setDescription("description");
        program.setName("name");
        ProgramDTO programDTO = new ProgramDTO();
        programDTO.setId(951L);
        programDTO.setProgramIdU("P1");
        programDTO.setDescription("description");
        programDTO.setName("name");
        Mockito.when(programRepository.findOneByProgramIdU("P1")).thenReturn(Optional.of(program));
        Mockito.when(programMapper.toDto(program)).thenReturn(programDTO);
        Optional<ProgramDTO> programDTOResult = programServiceImpl.findOneByProgramIdU("P1");
        assertThat(programDTOResult.get().getId()).isEqualTo(951L);
        assertEquals("P1", programDTOResult.get().getProgramIdU());
        assertEquals("description", programDTOResult.get().getDescription());
        assertEquals("name", programDTOResult.get().getName());
    }
}
