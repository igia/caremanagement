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
package io.igia.caremanagement.service.validation;

import java.util.Locale;

import org.springframework.stereotype.Component;

import io.igia.caremanagement.config.Constants;
import io.igia.caremanagement.security.SecurityUtils;
import io.igia.caremanagement.service.dto.camunda.TaskRequest;
import io.igia.caremanagement.web.rest.errors.CustomParameterizedException;

@Component
public class TaskValidation {

	public void createTask(TaskRequest taskRequest){
		if(null == taskRequest.getMrn() || taskRequest.getMrn().isEmpty()){
			throw new CustomParameterizedException("Invalid mrn","mrn");
		}
		if(null == taskRequest.getName() || taskRequest.getName().isEmpty()){
			throw new CustomParameterizedException("Invalid name","name");
		}
		if(taskRequest.getAssignee() == null || taskRequest.getAssignee().isEmpty()){
		    taskRequest.setAssignee(SecurityUtils.getCurrentUserLogin().orElse(Constants.SYSTEM_ACCOUNT).toLowerCase(Locale.ENGLISH));
		}
		
	}
	
	public void startTask(TaskRequest taskRequest){
	    if (taskRequest.getCaseInstanceId() == null || taskRequest.getCaseInstanceId().isEmpty()) {
	        throw new CustomParameterizedException("Invalid caseInstanceId","caseInstanceId");
        }
        if((taskRequest.getTaskIdList() == null) || (taskRequest.getTaskIdList().isEmpty())){
            throw new CustomParameterizedException("Invalid taskList","taskList");
        }
	}
	
	public void reassignTask(TaskRequest taskRequest){
        if (taskRequest.getTaskId() == null || taskRequest.getTaskId().isEmpty()) {
            throw new CustomParameterizedException("Invalid taskId","taskId");
        }
        if((taskRequest.getUserId() == null) || (taskRequest.getUserId().isEmpty())){
            throw new CustomParameterizedException("Invalid userId","userId");
        }
    }
}
