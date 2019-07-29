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

/**
 * A Episode.
 */
@Entity
@Table(name = "episode")
@Audited
public class Episode implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator", sequenceName = "episode_sequence")
    private Long id;

    @NotNull
    @Size(min = 2, max = 25)
    @Column(name = "episode_id_u", length = 25, nullable = false)
    private String episodeIdU;

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
    @Size(min = 2, max = 35)
    @Column(name = "lookup", length = 35, nullable = false)
    private String lookup;

    @ManyToOne
    @JsonIgnoreProperties("episodes")
    private Program program;

    @OneToMany(mappedBy = "episode")
    private Set<Goal> goals = new HashSet<>();
    @OneToMany(mappedBy = "episode")
    private Set<EpisodeAssociate> episodeAssociates = new HashSet<>();
    // jhipster-needle-entity-add-field - JHipster will add fields here, do not remove
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getEpisodeIdU() {
        return episodeIdU;
    }

    public Episode episodeIdU(String episodeIdU) {
        this.episodeIdU = episodeIdU;
        return this;
    }

    public void setEpisodeIdU(String episodeIdU) {
        this.episodeIdU = episodeIdU;
    }

    public String getName() {
        return name;
    }

    public Episode name(String name) {
        this.name = name;
        return this;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public Episode description(String description) {
        this.description = description;
        return this;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getEntryCriteria() {
        return entryCriteria;
    }

    public Episode entryCriteria(String entryCriteria) {
        this.entryCriteria = entryCriteria;
        return this;
    }

    public void setEntryCriteria(String entryCriteria) {
        this.entryCriteria = entryCriteria;
    }

    public String getLookup() {
        return lookup;
    }

    public Episode lookup(String lookup) {
        this.lookup = lookup;
        return this;
    }

    public void setLookup(String lookup) {
        this.lookup = lookup;
    }

    public Program getProgram() {
        return program;
    }

    public Episode program(Program program) {
        this.program = program;
        return this;
    }

    public void setProgram(Program program) {
        this.program = program;
    }

    public Set<Goal> getGoals() {
        return goals;
    }

    public Episode goals(Set<Goal> goals) {
        this.goals = goals;
        return this;
    }

    public Episode addGoal(Goal goal) {
        this.goals.add(goal);
        goal.setEpisode(this);
        return this;
    }

    public Episode removeGoal(Goal goal) {
        this.goals.remove(goal);
        goal.setEpisode(null);
        return this;
    }

    public void setGoals(Set<Goal> goals) {
        this.goals = goals;
    }

    public Set<EpisodeAssociate> getEpisodeAssociates() {
        return episodeAssociates;
    }

    public Episode episodeAssociates(Set<EpisodeAssociate> episodeAssociates) {
        this.episodeAssociates = episodeAssociates;
        return this;
    }

    public Episode addEpisodeAssociate(EpisodeAssociate episodeAssociate) {
        this.episodeAssociates.add(episodeAssociate);
        episodeAssociate.setEpisode(this);
        return this;
    }

    public Episode removeEpisodeAssociate(EpisodeAssociate episodeAssociate) {
        this.episodeAssociates.remove(episodeAssociate);
        episodeAssociate.setEpisode(null);
        return this;
    }

    public void setEpisodeAssociates(Set<EpisodeAssociate> episodeAssociates) {
        this.episodeAssociates = episodeAssociates;
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
        Episode episode = (Episode) o;
        if (episode.getId() == null || getId() == null) {
            return false;
        }
        return Objects.equals(getId(), episode.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getId());
    }

    @Override
    public String toString() {
        return "Episode{" +
            "id=" + getId() +
            ", episodeIdU='" + getEpisodeIdU() + "'" +
            ", name='" + getName() + "'" +
            ", description='" + getDescription() + "'" +
            ", entryCriteria='" + getEntryCriteria() + "'" +
            ", lookup='" + getLookup() + "'" +
            "}";
    }
}
