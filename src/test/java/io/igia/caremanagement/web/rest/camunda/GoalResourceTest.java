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

import java.util.ArrayList;
import java.util.List;

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
import io.igia.caremanagement.domain.enumeration.Category;
import io.igia.caremanagement.service.dto.camunda.GoalRequest;
import io.igia.caremanagement.service.impl.camunda.GoalServiceImpl;
import io.igia.caremanagement.web.rest.TestUtil;
import io.igia.caremanagement.web.rest.camunda.GoalResource;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = CaremanagementApp.class)
public class GoalResourceTest {
	@MockBean
	private GoalServiceImpl goalServiceImpl;

	private MockMvc mvc;

	@Before
	public void setup() {
		MockitoAnnotations.initMocks(this);
		GoalResource resource = new GoalResource(goalServiceImpl);
		this.mvc = MockMvcBuilders.standaloneSetup(resource).build();
	}

	@Test
	public void testGetAvailableGoals() throws Exception {
		GoalRequest goal = new GoalRequest();
		goal.setMrn("asd22");
		goal.setCategory(Category.AVAILABLE);
		mvc.perform(post("/api/goals").contentType(TestUtil.APPLICATION_JSON_UTF8)
				.content(TestUtil.convertObjectToJsonBytes(goal))).andExpect(status().isOk())
				.andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE));
	}

	@Test
	public void testGetAvailableGoalsWithBadRequest() throws Exception {

		mvc.perform(post("/api/goals").contentType(TestUtil.APPLICATION_JSON_UTF8).content("{Invalid Json}"))
				.andExpect(status().isBadRequest());
	}

	@Test
	public void testGetActiveGoals() throws Exception {
		GoalRequest goal = new GoalRequest();
		goal.setMrn("asd22");
		goal.setCategory(Category.ACTIVE);
		mvc.perform(post("/api/goals").contentType(TestUtil.APPLICATION_JSON_UTF8)
				.content(TestUtil.convertObjectToJsonBytes(goal))).andExpect(status().isOk())
				.andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE));
	}

	@Test
	public void testStartGoal() throws Exception {
		GoalRequest goalRequest = new GoalRequest();
		List<String> goalIdList = new ArrayList<>();
		goalIdList.add("T5");
		goalIdList.add("T3");
		goalRequest.setGoalIdList(goalIdList);
		goalRequest.setCaseInstanceId("36f9dfef-d380-11e8-818f-caff28a2dbdf");
		mvc.perform(put("/api/goals/start").contentType(TestUtil.APPLICATION_JSON_UTF8)
				.content(TestUtil.convertObjectToJsonBytes(goalRequest))).andExpect(status().isOk());
	}

}
