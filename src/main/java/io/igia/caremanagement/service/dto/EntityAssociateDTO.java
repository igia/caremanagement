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

import io.igia.caremanagement.domain.enumeration.CaseExecutionEvent;

/**
 * A DTO for the EntityAssociate entity.
 */
public class EntityAssociateDTO implements Serializable {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    private Long id;
    
    private String idU;

    private CaseExecutionEvent associateEvent;

    private String associateOn;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getIdU() {
        return idU;
    }

    public void setIdU(String idU) {
        this.idU = idU;
    }

    
    public CaseExecutionEvent getAssociateEvent() {
		return associateEvent;
	}

	public void setAssociateEvent(CaseExecutionEvent associateEvent) {
		this.associateEvent = associateEvent;
	}

	public String getAssociateOn() {
        return associateOn;
    }

    public void setAssociateOn(String associateOn) {
        this.associateOn = associateOn;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((associateEvent == null) ? 0 : associateEvent.hashCode());
        result = prime * result + ((associateOn == null) ? 0 : associateOn.hashCode());
        result = prime * result + ((id == null) ? 0 : id.hashCode());
        result = prime * result + ((idU == null) ? 0 : idU.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        EntityAssociateDTO other = (EntityAssociateDTO) obj;
        if (associateEvent == null) {
            if (other.associateEvent != null)
                return false;
        } else if (!associateEvent.equals(other.associateEvent))
            return false;
        if (associateOn == null) {
            if (other.associateOn != null)
                return false;
        } else if (!associateOn.equals(other.associateOn))
            return false;
        if (id == null) {
            if (other.id != null)
                return false;
        } else if (!id.equals(other.id))
            return false;
        if (idU == null) {
            if (other.idU != null)
                return false;
        } else if (!idU.equals(other.idU))
            return false;
        return true;
    }

    @Override
    public String toString() {
        return "EntityAssociateDTO [id=" + id + ", idU=" + idU + ", associateEvent=" + associateEvent + ", associateOn="
                + associateOn + "]";
    }

}
