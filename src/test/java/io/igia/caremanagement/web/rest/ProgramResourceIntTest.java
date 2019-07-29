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
package io.igia.caremanagement.web.rest;

import org.hibernate.envers.AuditReader;
import org.hibernate.envers.AuditReaderFactory;
import org.hibernate.envers.query.AuditEntity;
import org.hibernate.envers.query.AuditQuery;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.DefaultTransactionDefinition;

import io.igia.caremanagement.CaremanagementApp;
import io.igia.caremanagement.domain.Program;
import io.igia.caremanagement.repository.ProgramRepository;
import io.igia.caremanagement.service.ProgramService;
import io.igia.caremanagement.service.dto.ProgramDTO;
import io.igia.caremanagement.service.mapper.ProgramMapper;
import io.igia.caremanagement.web.rest.ProgramResource;
import io.igia.caremanagement.web.rest.errors.ExceptionTranslator;

import javax.persistence.EntityManager;
import java.util.List;

import static io.igia.caremanagement.web.rest.TestUtil.createFormattingConversionService;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Test class for the ProgramResource REST controller.
 *
 * @see ProgramResource
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = CaremanagementApp.class)
public class ProgramResourceIntTest {

    private static final String DEFAULT_PROGRAM_ID_U = "AAAAAAAAAA";
    private static final String UPDATED_PROGRAM_ID_U = "BBBBBBBBBB";

    private static final String DEFAULT_NAME = "AAAAAAAAAA";
    private static final String UPDATED_NAME = "BBBBBBBBBB";

    private static final String DEFAULT_DESCRIPTION = "AAAAAAAAAA";
    private static final String UPDATED_DESCRIPTION = "BBBBBBBBBB";

    @Autowired
    private ProgramRepository programRepository;

    @Autowired
    private ProgramMapper programMapper;

    @Autowired
    private ProgramService programService;

    @Autowired
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Autowired
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    @Autowired
    private ExceptionTranslator exceptionTranslator;

    @Autowired
    private EntityManager em;

    @Autowired
    private PlatformTransactionManager platformTransactionManager;

    private MockMvc restProgramMockMvc;

    private Program program;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        final ProgramResource programResource = new ProgramResource(programService);
        this.restProgramMockMvc = MockMvcBuilders.standaloneSetup(programResource)
                .setCustomArgumentResolvers(pageableArgumentResolver).setControllerAdvice(exceptionTranslator)
                .setConversionService(createFormattingConversionService()).setMessageConverters(jacksonMessageConverter)
                .build();
    }

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Program createEntity(EntityManager em) {
        Program program = new Program().programIdU(DEFAULT_PROGRAM_ID_U).name(DEFAULT_NAME)
                .description(DEFAULT_DESCRIPTION);
        return program;
    }

    @Before
    public void initTest() {
        program = createEntity(em);
    }

    @Test
    @Transactional
    public void createProgram() throws Exception {
        int databaseSizeBeforeCreate = programRepository.findAll().size();

        // Create the Program
        ProgramDTO programDTO = programMapper.toDto(program);
        restProgramMockMvc.perform(post("/api/definitions/programs").contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(programDTO))).andExpect(status().isCreated());

        // Validate the Program in the database
        List<Program> programList = programRepository.findAll();
        assertThat(programList).hasSize(databaseSizeBeforeCreate + 1);
        Program testProgram = programList.get(programList.size() - 1);
        assertThat(testProgram.getProgramIdU()).isEqualTo(DEFAULT_PROGRAM_ID_U);
        assertThat(testProgram.getName()).isEqualTo(DEFAULT_NAME);
        assertThat(testProgram.getDescription()).isEqualTo(DEFAULT_DESCRIPTION);
    }

    @Test
    @Transactional
    public void createProgramWithExistingId() throws Exception {
        int databaseSizeBeforeCreate = programRepository.findAll().size();

        // Create the Program with an existing ID
        program.setId(1L);
        ProgramDTO programDTO = programMapper.toDto(program);

        // An entity with an existing ID cannot be created, so this API call
        // must fail
        restProgramMockMvc.perform(post("/api/definitions/programs").contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(programDTO))).andExpect(status().isBadRequest());

        // Validate the Program in the database
        List<Program> programList = programRepository.findAll();
        assertThat(programList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    public void checkProgramIdUIsRequired() throws Exception {
        int databaseSizeBeforeTest = programRepository.findAll().size();
        // set the field null
        program.setProgramIdU(null);

        // Create the Program, which fails.
        ProgramDTO programDTO = programMapper.toDto(program);

        restProgramMockMvc.perform(post("/api/definitions/programs").contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(programDTO))).andExpect(status().isBadRequest());

        List<Program> programList = programRepository.findAll();
        assertThat(programList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void checkNameIsRequired() throws Exception {
        int databaseSizeBeforeTest = programRepository.findAll().size();
        // set the field null
        program.setName(null);

        // Create the Program, which fails.
        ProgramDTO programDTO = programMapper.toDto(program);

        restProgramMockMvc.perform(post("/api/definitions/programs").contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(programDTO))).andExpect(status().isBadRequest());

        List<Program> programList = programRepository.findAll();
        assertThat(programList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void getAllPrograms() throws Exception {
        // Initialize the database
        programRepository.saveAndFlush(program);

        // Get all the programList
        restProgramMockMvc.perform(get("/api/definitions/programs?sort=id,desc")).andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andExpect(jsonPath("$.[*].id").value(hasItem(program.getId().intValue())))
                .andExpect(jsonPath("$.[*].programIdU").value(hasItem(DEFAULT_PROGRAM_ID_U.toString())))
                .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME.toString())))
                .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION.toString())));
    }

    @Test
    @Transactional
    public void getProgram() throws Exception {
        // Initialize the database
        programRepository.saveAndFlush(program);

        // Get the program
        restProgramMockMvc.perform(get("/api/definitions/programs/{id}", program.getId())).andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andExpect(jsonPath("$.id").value(program.getId().intValue()))
                .andExpect(jsonPath("$.programIdU").value(DEFAULT_PROGRAM_ID_U.toString()))
                .andExpect(jsonPath("$.name").value(DEFAULT_NAME.toString()))
                .andExpect(jsonPath("$.description").value(DEFAULT_DESCRIPTION.toString()));
    }

    @Test
    @Transactional
    public void getNonExistingProgram() throws Exception {
        // Get the program
        restProgramMockMvc.perform(get("/api/definitions/programs/{id}", Long.MAX_VALUE))
                .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateProgram() throws Exception {
        // Initialize the database
        programRepository.saveAndFlush(program);

        int databaseSizeBeforeUpdate = programRepository.findAll().size();

        // Update the program
        Program updatedProgram = programRepository.findById(program.getId()).get();
        // Disconnect from session so that the updates on updatedProgram are not
        // directly saved in db
        em.detach(updatedProgram);
        updatedProgram.programIdU(UPDATED_PROGRAM_ID_U).name(UPDATED_NAME).description(UPDATED_DESCRIPTION);
        ProgramDTO programDTO = programMapper.toDto(updatedProgram);

        restProgramMockMvc.perform(put("/api/definitions/programs").contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(programDTO))).andExpect(status().isOk());

        // Validate the Program in the database
        List<Program> programList = programRepository.findAll();
        assertThat(programList).hasSize(databaseSizeBeforeUpdate);
        Program testProgram = programList.get(programList.size() - 1);
        assertThat(testProgram.getProgramIdU()).isEqualTo(UPDATED_PROGRAM_ID_U);
        assertThat(testProgram.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testProgram.getDescription()).isEqualTo(UPDATED_DESCRIPTION);
    }

    @Test
    @Transactional
    public void updateNonExistingProgram() throws Exception {
        int databaseSizeBeforeUpdate = programRepository.findAll().size();

        // Create the Program
        ProgramDTO programDTO = programMapper.toDto(program);

        // If the entity doesn't have an ID, it will throw
        // BadRequestAlertException
        restProgramMockMvc.perform(put("/api/definitions/programs").contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(programDTO))).andExpect(status().isBadRequest());

        // Validate the Program in the database
        List<Program> programList = programRepository.findAll();
        assertThat(programList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    public void deleteProgram() throws Exception {
        // Initialize the database
        programRepository.saveAndFlush(program);

        int databaseSizeBeforeDelete = programRepository.findAll().size();

        // Get the program
        restProgramMockMvc.perform(
                delete("/api/definitions/programs/{id}", program.getId()).accept(TestUtil.APPLICATION_JSON_UTF8))
                .andExpect(status().isOk());

        // Validate the database is empty
        List<Program> programList = programRepository.findAll();
        assertThat(programList).hasSize(databaseSizeBeforeDelete - 1);
    }

    @Test
    @Transactional
    public void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Program.class);
        Program program1 = new Program();
        program1.setId(1L);
        Program program2 = new Program();
        program2.setId(program1.getId());
        assertThat(program1).isEqualTo(program2);
        program2.setId(2L);
        assertThat(program1).isNotEqualTo(program2);
        program1.setId(null);
        assertThat(program1).isNotEqualTo(program2);
    }

    @Test
    @Transactional
    public void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(ProgramDTO.class);
        ProgramDTO programDTO1 = new ProgramDTO();
        programDTO1.setId(1L);
        ProgramDTO programDTO2 = new ProgramDTO();
        assertThat(programDTO1).isNotEqualTo(programDTO2);
        programDTO2.setId(programDTO1.getId());
        assertThat(programDTO1).isEqualTo(programDTO2);
        programDTO2.setId(2L);
        assertThat(programDTO1).isNotEqualTo(programDTO2);
        programDTO1.setId(null);
        assertThat(programDTO1).isNotEqualTo(programDTO2);
    }

    @Test
    @Transactional
    public void testEntityFromId() {
        assertThat(programMapper.fromId(42L).getId()).isEqualTo(42);
        assertThat(programMapper.fromId(null)).isNull();
    }

    @Test
    @Transactional
    public void updateProgramTestEnverse() throws Exception {
        TransactionStatus status = platformTransactionManager
                .getTransaction(new DefaultTransactionDefinition(TransactionDefinition.PROPAGATION_REQUIRES_NEW));
        // Initialize the database
        programRepository.saveAndFlush(program);

        int databaseSizeBeforeUpdate = programRepository.findAll().size();

        // Update the program
        Program updatedProgram = programRepository.findById(program.getId()).get();
        // Disconnect from session so that the updates on updatedProgram are not
        // directly saved in db
        em.detach(updatedProgram);
        updatedProgram.programIdU("P1_enverse").name("enverse").description("test enverse");
        ProgramDTO programDTO = programMapper.toDto(updatedProgram);

        restProgramMockMvc.perform(put("/api/definitions/programs").contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(programDTO))).andExpect(status().isOk());

        // Validate the Program in the database
        List<Program> programList = programRepository.findAll();
        assertThat(programList).hasSize(databaseSizeBeforeUpdate);
        Program testProgram = programList.get(programList.size() - 1);
        assertThat(testProgram.getProgramIdU()).isEqualTo("P1_enverse");
        assertThat(testProgram.getName()).isEqualTo("enverse");
        assertThat(testProgram.getDescription()).isEqualTo("test enverse");

        platformTransactionManager.commit(status);

        AuditReader reader = AuditReaderFactory.get(em);
        AuditQuery query = reader.createQuery().forRevisionsOfEntity(Program.class, true, true);
        query.addProjection(AuditEntity.revisionNumber().max()).add(AuditEntity.id().eq(updatedProgram.getId()));
        Number revision = (Number) query.getSingleResult();
        Program program_rev1 = reader.find(Program.class, updatedProgram.getId(), revision);

        assertThat(program_rev1.getProgramIdU()).isEqualTo("P1_enverse");
        assertThat(program_rev1.getName()).isEqualTo("enverse");
        assertThat(program_rev1.getDescription()).isEqualTo("test enverse");
    }

}
