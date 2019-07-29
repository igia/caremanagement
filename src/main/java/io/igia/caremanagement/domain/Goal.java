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

import io.igia.caremanagement.domain.enumeration.TimeUnit;

/**
 * A Goal.
 */
@Entity
@Table(name = "goal")
@Audited
public class Goal implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator", sequenceName = "goal_sequence")
    private Long id;

    @NotNull
    @Size(min = 2, max = 25)
    @Column(name = "goal_id_u", length = 25, nullable = false)
    private String goalIdU;

    @NotNull
    @Size(min = 2, max = 255)
    @Column(name = "name", length = 255, nullable = false)
    private String name;

    @Size(max = 1000)
    @Column(name = "description", length = 1000)
    private String description;

    @Size(max = 500)
    @Column(name = "entry_criteria", length = 500)
    private String entryCriteria;

    @NotNull
    @Column(name = "eta_value", nullable = false)
    private Integer etaValue;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "eta_unit", nullable = false)
    private TimeUnit etaUnit;

    @NotNull
    @Size(min = 2, max = 35)
    @Column(name = "lookup", length = 35, nullable = false)
    private String lookup;

    @ManyToOne
    @JsonIgnoreProperties("goals")
    private Episode episode;

    @OneToMany(mappedBy = "goal")
    private Set<Task> tasks = new HashSet<>();
    @OneToMany(mappedBy = "goal")
    private Set<GoalAssociate> goalAssociates = new HashSet<>();
    // jhipster-needle-entity-add-field - JHipster will add fields here, do not remove
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getGoalIdU() {
        return goalIdU;
    }

    public Goal goalIdU(String goalIdU) {
        this.goalIdU = goalIdU;
        return this;
    }

    public void setGoalIdU(String goalIdU) {
        this.goalIdU = goalIdU;
    }

    public String getName() {
        return name;
    }

    public Goal name(String name) {
        this.name = name;
        return this;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public Goal description(String description) {
        this.description = description;
        return this;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getEntryCriteria() {
        return entryCriteria;
    }

    public Goal entryCriteria(String entryCriteria) {
        this.entryCriteria = entryCriteria;
        return this;
    }

    public void setEntryCriteria(String entryCriteria) {
        this.entryCriteria = entryCriteria;
    }

    public Integer getEtaValue() {
        return etaValue;
    }

    public Goal etaValue(Integer etaValue) {
        this.etaValue = etaValue;
        return this;
    }

    public void setEtaValue(Integer etaValue) {
        this.etaValue = etaValue;
    }

    public TimeUnit getEtaUnit() {
        return etaUnit;
    }

    public Goal etaUnit(TimeUnit etaUnit) {
        this.etaUnit = etaUnit;
        return this;
    }

    public void setEtaUnit(TimeUnit etaUnit) {
        this.etaUnit = etaUnit;
    }

    public String getLookup() {
        return lookup;
    }

    public Goal lookup(String lookup) {
        this.lookup = lookup;
        return this;
    }

    public void setLookup(String lookup) {
        this.lookup = lookup;
    }

    public Episode getEpisode() {
        return episode;
    }

    public Goal episode(Episode episode) {
        this.episode = episode;
        return this;
    }

    public void setEpisode(Episode episode) {
        this.episode = episode;
    }

    public Set<Task> getTasks() {
        return tasks;
    }

    public Goal tasks(Set<Task> tasks) {
        this.tasks = tasks;
        return this;
    }

    public Goal addTask(Task task) {
        this.tasks.add(task);
        task.setGoal(this);
        return this;
    }

    public Goal removeTask(Task task) {
        this.tasks.remove(task);
        task.setGoal(null);
        return this;
    }

    public void setTasks(Set<Task> tasks) {
        this.tasks = tasks;
    }

    public Set<GoalAssociate> getGoalAssociates() {
        return goalAssociates;
    }

    public Goal goalAssociates(Set<GoalAssociate> goalAssociates) {
        this.goalAssociates = goalAssociates;
        return this;
    }

    public Goal addGoalAssociate(GoalAssociate goalAssociate) {
        this.goalAssociates.add(goalAssociate);
        goalAssociate.setGoal(this);
        return this;
    }

    public Goal removeGoalAssociate(GoalAssociate goalAssociate) {
        this.goalAssociates.remove(goalAssociate);
        goalAssociate.setGoal(null);
        return this;
    }

    public void setGoalAssociates(Set<GoalAssociate> goalAssociates) {
        this.goalAssociates = goalAssociates;
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
        Goal goal = (Goal) o;
        if (goal.getId() == null || getId() == null) {
            return false;
        }
        return Objects.equals(getId(), goal.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getId());
    }

    @Override
    public String toString() {
        return "Goal{" +
            "id=" + getId() +
            ", goalIdU='" + getGoalIdU() + "'" +
            ", name='" + getName() + "'" +
            ", description='" + getDescription() + "'" +
            ", entryCriteria='" + getEntryCriteria() + "'" +
            ", etaValue=" + getEtaValue() +
            ", etaUnit='" + getEtaUnit() + "'" +
            ", lookup='" + getLookup() + "'" +
            "}";
    }
}
