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

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import io.igia.caremanagement.domain.enumeration.CaseExecutionEvent;

import javax.persistence.*;
import javax.validation.constraints.*;

import org.hibernate.envers.Audited;

import java.io.Serializable;
import java.util.Objects;

/**
 * A TaskAssociate.
 */
@Entity
@Table(name = "task_associate")
@Audited
public class TaskAssociate implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator", sequenceName = "task_associate_sequence")
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(name = "associate_event")
    private CaseExecutionEvent associateEvent;

    @NotNull
    @Column(name = "associate_on", nullable = false)
    private Long associateOn;

    @ManyToOne
    @JsonIgnoreProperties("taskAssociates")
    private Task task;

    // jhipster-needle-entity-add-field - JHipster will add fields here, do not remove
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public CaseExecutionEvent getAssociateEvent() {
        return associateEvent;
    }

    public TaskAssociate associateEvent(CaseExecutionEvent associateEvent) {
        this.associateEvent = associateEvent;
        return this;
    }

    public void setAssociateEvent(CaseExecutionEvent associateEvent) {
        this.associateEvent = associateEvent;
    }

    public Long getAssociateOn() {
        return associateOn;
    }

    public TaskAssociate associateOn(Long associateOn) {
        this.associateOn = associateOn;
        return this;
    }

    public void setAssociateOn(Long associateOn) {
        this.associateOn = associateOn;
    }

    public Task getTask() {
        return task;
    }

    public TaskAssociate task(Task task) {
        this.task = task;
        return this;
    }

    public void setTask(Task task) {
        this.task = task;
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
        TaskAssociate taskAssociate = (TaskAssociate) o;
        if (taskAssociate.getId() == null || getId() == null) {
            return false;
        }
        return Objects.equals(getId(), taskAssociate.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getId());
    }

    @Override
    public String toString() {
        return "TaskAssociate{" +
            "id=" + getId() +
            ", associateEvent='" + getAssociateEvent() + "'" +
            ", associateOn=" + getAssociateOn() +
            "}";
    }
}
