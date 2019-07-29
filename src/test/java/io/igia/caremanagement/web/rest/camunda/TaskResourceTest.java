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
package io.igia.caremanagement.web.rest.camunda;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.HashMap;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import io.igia.caremanagement.CaremanagementApp;
import io.igia.caremanagement.domain.enumeration.TaskCategory;
import io.igia.caremanagement.service.dto.camunda.TaskRequest;
import io.igia.caremanagement.service.impl.camunda.TaskServiceImpl;
import io.igia.caremanagement.web.rest.TestUtil;
import io.igia.caremanagement.web.rest.camunda.TaskResource;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = CaremanagementApp.class)
public class TaskResourceTest {

	@MockBean
	private TaskServiceImpl taskServiceImpl;

	private MockMvc mvc;

	@Before
	public void setup() {
		MockitoAnnotations.initMocks(this);
		TaskResource resource = new TaskResource(taskServiceImpl);
		this.mvc = MockMvcBuilders.standaloneSetup(resource).build();
	}

	@Test
	public void testCompleteTask() throws Exception {
		Map<String, Object> fields = new HashMap<>();

		fields.put("field", "value");
		String id = "sdfe";
		mvc.perform(post("/api/tasks/{id}/complete", id)).andExpect(status().isOk());
	}

	@Test
	public void testGetTask() throws Exception {
		TaskRequest task = new TaskRequest();
		task.setTaskCategory(TaskCategory.MISSED);

		mvc.perform(post("/api/tasks").contentType(TestUtil.APPLICATION_JSON_UTF8)
				.content(TestUtil.convertObjectToJsonBytes(task))).andExpect(status().isOk())
				.andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE));
	}

	@Test
	public void testUpdateTaskAssignee() throws Exception {
		TaskRequest task = new TaskRequest();
		task.setTaskId("11609547-d38a-11e8-818f-caff28a2dbdf");
		task.setUserId("john");

		mvc.perform(put("/api/tasks/reassign").contentType(TestUtil.APPLICATION_JSON_UTF8)
				.content(TestUtil.convertObjectToJsonBytes(task))).andExpect(status().isOk());
	}

	
	@Test
	public void testGetActiveTask() throws Exception {
		TaskRequest task = new TaskRequest();
		task.setMrn("5tr6");
		task.setTaskCategory(TaskCategory.AVAILABLE);
		mvc.perform(post("/api/tasks").contentType(TestUtil.APPLICATION_JSON_UTF8).content(TestUtil.convertObjectToJsonBytes(task))).andExpect(status().isOk()).andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE));
	}
	
	
	@Test
	public void testGetHistoryTask() throws Exception {
		TaskRequest task = new TaskRequest();
		task.setMrn("asd22");
		task.setTaskCategory(TaskCategory.HISTORY);
		mvc.perform(post("/api/tasks").contentType(TestUtil.APPLICATION_JSON_UTF8)
				.content(TestUtil.convertObjectToJsonBytes(task))).andExpect(status().isOk())
				.andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE));
	}


	@Test
	public void testStartTask() throws Exception {
		TaskRequest task = new TaskRequest();
		task.setCaseInstanceId("36f9dfef-d380-11e8-818f-caff28a2dbdf");
		task.setTaskId("T3");
				
		mvc.perform(put("/api/tasks/start").contentType(TestUtil.APPLICATION_JSON_UTF8)
				.content(TestUtil.convertObjectToJsonBytes(task))).andExpect(status().isOk());
	}
	
	@Test
	public void testCreatAdHocTask() throws Exception {
		TaskRequest task = new TaskRequest();
		task.setName("Capture blood pressure");
		task.setDescription("Capture blood pressure");
		task.setAssignee("demo");
		task.setDue("2018-11-22T11:11:15.372+0530");
		task.setCaseInstanceId("d4016a82-d817-11e8-9648-caff28a2dbdf");
	
		mvc.perform(post("/api/tasks/create").contentType(TestUtil.APPLICATION_JSON_UTF8)
				.content(TestUtil.convertObjectToJsonBytes(task))).andExpect(status().isOk());
	}


}
