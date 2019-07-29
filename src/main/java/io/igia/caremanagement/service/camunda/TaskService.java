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
package io.igia.caremanagement.service.camunda;

import java.util.List;

import io.igia.caremanagement.service.dto.camunda.TaskRequest;
import io.igia.caremanagement.service.dto.camunda.TaskResponse;

public interface TaskService {

	void completeTask(String id);

	List<TaskResponse> getTasks(TaskRequest task);

    List<TaskResponse> getAvailableTasks(String mrn, String programId, String caseInstanceId);

	List<TaskResponse> getTodoMissedUpcoming(TaskRequest taskRequest);

	List<TaskResponse> getHistoryTasks(TaskRequest task);

	void startTask(TaskRequest taskRequest);
	
	void updateTaskAssignee(TaskRequest task);
	
	void createAdHocTask(TaskRequest taskRequest);
}
