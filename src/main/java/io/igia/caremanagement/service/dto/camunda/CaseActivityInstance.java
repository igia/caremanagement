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

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@JsonInclude(Include.NON_NULL)
public class CaseActivityInstance {
    private String id;
    private String parentCaseActivityInstanceId;
    private String caseActivityId;
    private String caseActivityName;
    private String caseActivityType;
    private String caseDefinitionId;
    private String caseInstanceId;
    private String caseExecutionId;
    private boolean completed;
    private boolean terminated;
    private String createTime;
    private String endTime;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getParentCaseActivityInstanceId() {
        return parentCaseActivityInstanceId;
    }

    public void setParentCaseActivityInstanceId(String parentCaseActivityInstanceId) {
        this.parentCaseActivityInstanceId = parentCaseActivityInstanceId;
    }

    public String getCaseActivityId() {
        return caseActivityId;
    }

    public void setCaseActivityId(String caseActivityId) {
        this.caseActivityId = caseActivityId;
    }

    public String getCaseActivityName() {
        return caseActivityName;
    }

    public void setCaseActivityName(String caseActivityName) {
        this.caseActivityName = caseActivityName;
    }

    public String getCaseActivityType() {
        return caseActivityType;
    }

    public void setCaseActivityType(String caseActivityType) {
        this.caseActivityType = caseActivityType;
    }

    public String getCaseDefinitionId() {
        return caseDefinitionId;
    }

    public void setCaseDefinitionId(String caseDefinitionId) {
        this.caseDefinitionId = caseDefinitionId;
    }

    public String getCaseInstanceId() {
        return caseInstanceId;
    }

    public void setCaseInstanceId(String caseInstanceId) {
        this.caseInstanceId = caseInstanceId;
    }

    public String getCaseExecutionId() {
        return caseExecutionId;
    }

    public void setCaseExecutionId(String caseExecutionId) {
        this.caseExecutionId = caseExecutionId;
    }

    public boolean isCompleted() {
        return completed;
    }

    public void setCompleted(boolean completed) {
        this.completed = completed;
    }

    public boolean isTerminated() {
        return terminated;
    }

    public void setTerminated(boolean terminated) {
        this.terminated = terminated;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }
}
