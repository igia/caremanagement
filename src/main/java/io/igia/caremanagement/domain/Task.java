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
package io.igia.caremanagement.domain;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.hibernate.envers.Audited;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import io.igia.caremanagement.domain.enumeration.CaseExecutionEvent;
import io.igia.caremanagement.domain.enumeration.TimeUnit;
import io.igia.caremanagement.domain.enumeration.Type;

/**
 * A Task.
 */
@Entity
@Table(name = "task")
@Audited
public class Task implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator",sequenceName = "task_sequence")
    private Long id;

    @NotNull
    @Size(min = 2, max = 25)
    @Column(name = "task_id_u", length = 25, nullable = false)
    private String taskIdU;

    @NotNull
    @Size(min = 2, max = 255)
    @Column(name = "name", length = 255, nullable = false)
    private String name;

    @Size(max = 1000)
    @Column(name = "description", length = 1000)
    private String description;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "jhi_type", nullable = false)
    private Type type;

    @Column(name = "type_ref")
    private String typeRef;

    @Column(name = "due_date")
    private LocalDate dueDate;

    @NotNull
    @Column(name = "assignee", nullable = false)
    private String assignee;

    @Size(max = 500)
    @Column(name = "entry_criteria", length = 500)
    private String entryCriteria;

    @Column(name = "repeat_frequency_value")
    private Integer repeatFrequencyValue;

    @Enumerated(EnumType.STRING)
    @Column(name = "repeat_frequency_unit")
    private TimeUnit repeatFrequencyUnit;

    @Enumerated(EnumType.STRING)
    @Column(name = "repeat_event")
    private CaseExecutionEvent repeatEvent;

    @NotNull
    @Size(min = 2, max = 35)
    @Column(name = "lookup", length = 35, nullable = false)
    private String lookup;

    @Column(name = "sla")
    private Integer sla;

    @NotNull
    @Column(name = "is_repeat", nullable = false)
    private Boolean isRepeat;

    @ManyToOne
    @JsonIgnoreProperties("tasks")
    private Goal goal;

    @OneToMany(mappedBy = "task")
    private Set<TaskAssociate> taskAssociates = new HashSet<>();
    // jhipster-needle-entity-add-field - JHipster will add fields here, do not remove
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTaskIdU() {
        return taskIdU;
    }

    public Task taskIdU(String taskIdU) {
        this.taskIdU = taskIdU;
        return this;
    }

    public void setTaskIdU(String taskIdU) {
        this.taskIdU = taskIdU;
    }

    public String getName() {
        return name;
    }

    public Task name(String name) {
        this.name = name;
        return this;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public Task description(String description) {
        this.description = description;
        return this;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Type getType() {
        return type;
    }

    public Task type(Type type) {
        this.type = type;
        return this;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public String getTypeRef() {
        return typeRef;
    }

    public Task typeRef(String typeRef) {
        this.typeRef = typeRef;
        return this;
    }

    public void setTypeRef(String typeRef) {
        this.typeRef = typeRef;
    }

    public LocalDate getDueDate() {
        return dueDate;
    }

    public Task dueDate(LocalDate dueDate) {
        this.dueDate = dueDate;
        return this;
    }

    public void setDueDate(LocalDate dueDate) {
        this.dueDate = dueDate;
    }

    public String getAssignee() {
        return assignee;
    }

    public Task assignee(String assignee) {
        this.assignee = assignee;
        return this;
    }

    public void setAssignee(String assignee) {
        this.assignee = assignee;
    }

    public String getEntryCriteria() {
        return entryCriteria;
    }

    public Task entryCriteria(String entryCriteria) {
        this.entryCriteria = entryCriteria;
        return this;
    }

    public void setEntryCriteria(String entryCriteria) {
        this.entryCriteria = entryCriteria;
    }

    public Integer getRepeatFrequencyValue() {
        return repeatFrequencyValue;
    }

    public Task repeatFrequencyValue(Integer repeatFrequencyValue) {
        this.repeatFrequencyValue = repeatFrequencyValue;
        return this;
    }

    public void setRepeatFrequencyValue(Integer repeatFrequencyValue) {
        this.repeatFrequencyValue = repeatFrequencyValue;
    }

    public TimeUnit getRepeatFrequencyUnit() {
        return repeatFrequencyUnit;
    }

    public Task repeatFrequencyUnit(TimeUnit repeatFrequencyUnit) {
        this.repeatFrequencyUnit = repeatFrequencyUnit;
        return this;
    }

    public void setRepeatFrequencyUnit(TimeUnit repeatFrequencyUnit) {
        this.repeatFrequencyUnit = repeatFrequencyUnit;
    }

    public CaseExecutionEvent getRepeatEvent() {
        return repeatEvent;
    }

    public Task repeatEvent(CaseExecutionEvent repeatEvent) {
        this.repeatEvent = repeatEvent;
        return this;
    }

    public void setRepeatEvent(CaseExecutionEvent repeatEvent) {
        this.repeatEvent = repeatEvent;
    }

    public String getLookup() {
        return lookup;
    }

    public Task lookup(String lookup) {
        this.lookup = lookup;
        return this;
    }

    public void setLookup(String lookup) {
        this.lookup = lookup;
    }

    public Integer getSla() {
        return sla;
    }

    public Task sla(Integer sla) {
        this.sla = sla;
        return this;
    }

    public void setSla(Integer sla) {
        this.sla = sla;
    }

    public Boolean isIsRepeat() {
        return isRepeat;
    }

    public Task isRepeat(Boolean isRepeat) {
        this.isRepeat = isRepeat;
        return this;
    }

    public void setIsRepeat(Boolean isRepeat) {
        this.isRepeat = isRepeat;
    }

    public Goal getGoal() {
        return goal;
    }

    public Task goal(Goal goal) {
        this.goal = goal;
        return this;
    }

    public void setGoal(Goal goal) {
        this.goal = goal;
    }

    public Set<TaskAssociate> getTaskAssociates() {
        return taskAssociates;
    }

    public Task taskAssociates(Set<TaskAssociate> taskAssociates) {
        this.taskAssociates = taskAssociates;
        return this;
    }

    public Task addTaskAssociate(TaskAssociate taskAssociate) {
        this.taskAssociates.add(taskAssociate);
        taskAssociate.setTask(this);
        return this;
    }

    public Task removeTaskAssociate(TaskAssociate taskAssociate) {
        this.taskAssociates.remove(taskAssociate);
        taskAssociate.setTask(null);
        return this;
    }

    public void setTaskAssociates(Set<TaskAssociate> taskAssociates) {
        this.taskAssociates = taskAssociates;
    }
    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here, do not remove

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Task task = (Task) o;
        if (task.getId() == null || getId() == null) {
            return false;
        }
        return Objects.equals(getId(), task.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getId());
    }

    @Override
    public String toString() {
        return "Task{" +
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
            "}";
    }
}
