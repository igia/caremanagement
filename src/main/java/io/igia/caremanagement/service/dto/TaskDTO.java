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
package io.igia.caremanagement.service.dto;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.Objects;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import io.igia.caremanagement.domain.enumeration.CaseExecutionEvent;
import io.igia.caremanagement.domain.enumeration.TimeUnit;
import io.igia.caremanagement.domain.enumeration.Type;

/**
 * A DTO for the Task entity.
 */
public class TaskDTO implements Serializable {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    private Long id;

    @NotNull
    @Size(min = 2, max = 25)
    private String taskIdU;

    @NotNull
    @Size(min = 2, max = 255)
    private String name;

    @Size(max = 1000)
    private String description;

    @NotNull
    private Type type;

    private String typeRef;

    private LocalDate dueDate;

    @NotNull
    private String assignee;

    @Size(max = 500)
    private String entryCriteria;

    private Integer repeatFrequencyValue;

    private TimeUnit repeatFrequencyUnit;

    private CaseExecutionEvent repeatEvent;

    @NotNull
    @Size(min = 2, max = 35)
    private String lookup;

    private Integer sla;

    @NotNull
    private Boolean isRepeat;

    @NotNull
    private Long goalId;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTaskIdU() {
        return taskIdU;
    }

    public void setTaskIdU(String taskIdU) {
        this.taskIdU = taskIdU;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public String getTypeRef() {
        return typeRef;
    }

    public void setTypeRef(String typeRef) {
        this.typeRef = typeRef;
    }

    public LocalDate getDueDate() {
        return dueDate;
    }

    public void setDueDate(LocalDate dueDate) {
        this.dueDate = dueDate;
    }

    public String getAssignee() {
        return assignee;
    }

    public void setAssignee(String assignee) {
        this.assignee = assignee;
    }

    public String getEntryCriteria() {
        return entryCriteria;
    }

    public void setEntryCriteria(String entryCriteria) {
        this.entryCriteria = entryCriteria;
    }

    public Integer getRepeatFrequencyValue() {
        return repeatFrequencyValue;
    }

    public void setRepeatFrequencyValue(Integer repeatFrequencyValue) {
        this.repeatFrequencyValue = repeatFrequencyValue;
    }

    public TimeUnit getRepeatFrequencyUnit() {
        return repeatFrequencyUnit;
    }

    public void setRepeatFrequencyUnit(TimeUnit repeatFrequencyUnit) {
        this.repeatFrequencyUnit = repeatFrequencyUnit;
    }

    public CaseExecutionEvent getRepeatEvent() {
        return repeatEvent;
    }

    public void setRepeatEvent(CaseExecutionEvent repeatEvent) {
        this.repeatEvent = repeatEvent;
    }

    public String getLookup() {
        return lookup;
    }

    public void setLookup(String lookup) {
        this.lookup = lookup;
    }

    public Integer getSla() {
        return sla;
    }

    public void setSla(Integer sla) {
        this.sla = sla;
    }

    public Boolean isIsRepeat() {
        return isRepeat;
    }

    public void setIsRepeat(Boolean isRepeat) {
        this.isRepeat = isRepeat;
    }

    public Long getGoalId() {
        return goalId;
    }

    public void setGoalId(Long goalId) {
        this.goalId = goalId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        TaskDTO taskDTO = (TaskDTO) o;
        if (taskDTO.getId() == null || getId() == null) {
            return false;
        }
        return Objects.equals(getId(), taskDTO.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getId());
    }

    @Override
    public String toString() {
        return "TaskDTO{" +
            "id=" + getId() +
            ", taskIdU='" + getTaskIdU() + "'" +
            ", name='" + getName() + "'" +
            ", description='" + getDescription() + "'" +
            ", type='" + getType() + "'" +
            ", typeRef='" + getTypeRef() + "'" +
            ", dueDate='" + getDueDate() + "'" +
            ", assignee='" + getAssignee() + "'" +
            ", entryCriteria='" + getEntryCriteria() + "'" +
            ", repeatFrequencyValue=" + getRepeatFrequencyValue() +
            ", repeatFrequencyUnit='" + getRepeatFrequencyUnit() + "'" +
            ", repeatEvent='" + getRepeatEvent() + "'" +
            ", lookup='" + getLookup() + "'" +
            ", sla=" + getSla() +
            ", isRepeat='" + isIsRepeat() + "'" +
            ", goal=" + getGoalId() +
            "}";
    }
}
