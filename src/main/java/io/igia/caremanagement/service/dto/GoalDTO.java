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
import java.util.Objects;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import io.igia.caremanagement.domain.enumeration.TimeUnit;

/**
 * A DTO for the Goal entity.
 */
public class GoalDTO implements Serializable {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    private Long id;

    @NotNull
    @Size(min = 2, max = 25)
    private String goalIdU;

    @NotNull
    @Size(min = 2, max = 255)
    private String name;

    @Size(max = 1000)
    private String description;

    @Size(max = 500)
    private String entryCriteria;

    @NotNull
    private Integer etaValue;

    @NotNull
    private TimeUnit etaUnit;

    @NotNull
    @Size(min = 2, max = 35)
    private String lookup;

    @NotNull
    private Long episodeId;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getGoalIdU() {
        return goalIdU;
    }

    public void setGoalIdU(String goalIdU) {
        this.goalIdU = goalIdU;
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

    public String getEntryCriteria() {
        return entryCriteria;
    }

    public void setEntryCriteria(String entryCriteria) {
        this.entryCriteria = entryCriteria;
    }

    public Integer getEtaValue() {
        return etaValue;
    }

    public void setEtaValue(Integer etaValue) {
        this.etaValue = etaValue;
    }

    public TimeUnit getEtaUnit() {
        return etaUnit;
    }

    public void setEtaUnit(TimeUnit etaUnit) {
        this.etaUnit = etaUnit;
    }

    public String getLookup() {
        return lookup;
    }

    public void setLookup(String lookup) {
        this.lookup = lookup;
    }

    public Long getEpisodeId() {
        return episodeId;
    }

    public void setEpisodeId(Long episodeId) {
        this.episodeId = episodeId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        GoalDTO goalDTO = (GoalDTO) o;
        if (goalDTO.getId() == null || getId() == null) {
            return false;
        }
        return Objects.equals(getId(), goalDTO.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getId());
    }

    @Override
    public String toString() {
        return "GoalDTO{" +
            "id=" + getId() +
            ", goalIdU='" + getGoalIdU() + "'" +
            ", name='" + getName() + "'" +
            ", description='" + getDescription() + "'" +
            ", entryCriteria='" + getEntryCriteria() + "'" +
            ", etaValue=" + getEtaValue() +
            ", etaUnit='" + getEtaUnit() + "'" +
            ", lookup='" + getLookup() + "'" +
            ", episode=" + getEpisodeId() +
            "}";
    }
}
