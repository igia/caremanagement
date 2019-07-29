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

import io.igia.caremanagement.domain.enumeration.CaseExecutionEvent;

/**
 * A DTO for the EpisodeAssociate entity.
 */
public class EpisodeAssociateDTO implements Serializable {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    private Long id;

    private CaseExecutionEvent associateEvent;

    private Long associateOn;
    
    private Long episodeId;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public CaseExecutionEvent getAssociateEvent() {
        return associateEvent;
    }

    public void setAssociateEvent(CaseExecutionEvent associateEvent) {
        this.associateEvent = associateEvent;
    }

    public Long getAssociateOn() {
        return associateOn;
    }

    public void setAssociateOn(Long associateOn) {
        this.associateOn = associateOn;
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

        EpisodeAssociateDTO episodeAssociateDTO = (EpisodeAssociateDTO) o;
        if (episodeAssociateDTO.getId() == null || getId() == null) {
            return false;
        }
        return Objects.equals(getId(), episodeAssociateDTO.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getId());
    }

    @Override
    public String toString() {
        return "EpisodeAssociateDTO{" +
            "id=" + getId() +
            ", associateEvent='" + getAssociateEvent() + "'" +
            ", associateOn=" + getAssociateOn() +
            ", episode=" + getEpisodeId() +
            "}";
    }
}
