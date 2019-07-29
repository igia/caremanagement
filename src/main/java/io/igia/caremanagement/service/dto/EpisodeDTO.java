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

import javax.validation.constraints.*;
import java.io.Serializable;
import java.util.Objects;

/**
 * A DTO for the Episode entity.
 */
public class EpisodeDTO implements Serializable {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    private Long id;

    @NotNull
    @Size(min = 2, max = 25)
    private String episodeIdU;

    @NotNull
    @Size(min = 2, max = 255)
    private String name;

    @Size(max = 1000)
    private String description;

    @Size(max = 500)
    private String entryCriteria;

    @NotNull
    @Size(min = 2, max = 35)
    private String lookup;

    private Long programId;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getEpisodeIdU() {
        return episodeIdU;
    }

    public void setEpisodeIdU(String episodeIdU) {
        this.episodeIdU = episodeIdU;
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

    public String getLookup() {
        return lookup;
    }

    public void setLookup(String lookup) {
        this.lookup = lookup;
    }

    public Long getProgramId() {
        return programId;
    }

    public void setProgramId(Long programId) {
        this.programId = programId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        EpisodeDTO episodeDTO = (EpisodeDTO) o;
        if (episodeDTO.getId() == null || getId() == null) {
            return false;
        }
        return Objects.equals(getId(), episodeDTO.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getId());
    }

    @Override
    public String toString() {
        return "EpisodeDTO{" +
            "id=" + getId() +
            ", episodeIdU='" + getEpisodeIdU() + "'" +
            ", name='" + getName() + "'" +
            ", description='" + getDescription() + "'" +
            ", entryCriteria='" + getEntryCriteria() + "'" +
            ", lookup='" + getLookup() + "'" +
            ", program=" + getProgramId() +
            "}";
    }
}
