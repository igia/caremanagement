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
package io.igia.caremanagement.service.dto.camunda;

import java.util.List;

import io.igia.caremanagement.domain.enumeration.Category;

public class GoalRequest {
	private Category category;
	private String mrn;
	private String caseInstanceId;
	private List<String> goalIdList;
	private String programId;
	private String createdBefore;
	private String createdAfter;
	private String endedBefore;
	private String endedAfter;
	
	public Category getCategory() {
        return category;
    }
    public void setCategory(Category category) {
        this.category = category;
    }
    public String getMrn() {
		return mrn;
	}
	public void setMrn(String mrn) {
		this.mrn = mrn;
	}

	public String getCaseInstanceId() {
		return caseInstanceId;
	}
	public void setCaseInstanceId(String caseInstanceId) {
		this.caseInstanceId = caseInstanceId;
	}
	public List<String> getGoalIdList() {
		return goalIdList;
	}
	public void setGoalIdList(List<String> goalIdList) {
		this.goalIdList = goalIdList;
	}
	public String getProgramId() {
		return programId;
	}
	public void setProgramId(String programId) {
		this.programId = programId;
	}
	public String getCreatedBefore() {
		return createdBefore;
	}
	public void setCreatedBefore(String createdBefore) {
		this.createdBefore = createdBefore;
	}
	public String getCreatedAfter() {
		return createdAfter;
	}
	public void setCreatedAfter(String createdAfter) {
		this.createdAfter = createdAfter;
	}
	public String getEndedBefore() {
		return endedBefore;
	}
	public void setEndedBefore(String endedBefore) {
		this.endedBefore = endedBefore;
	}
	public String getEndedAfter() {
		return endedAfter;
	}
	public void setEndedAfter(String endedAfter) {
		this.endedAfter = endedAfter;
	}
	
}
