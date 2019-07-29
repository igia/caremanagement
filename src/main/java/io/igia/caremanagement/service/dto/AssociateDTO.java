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

/**
 * A DTO for the EntityAssociate entity.
 */
public class AssociateDTO implements Serializable {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    private String associateEvent;

    private String associateOn;

    public String getAssociateEvent() {
        return associateEvent;
    }

    public void setAssociateEvent(String associateEvent) {
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
        AssociateDTO other = (AssociateDTO) obj;
        if (associateEvent != other.associateEvent)
            return false;
        if (associateOn == null) {
            if (other.associateOn != null)
                return false;
        } else if (!associateOn.equals(other.associateOn))
            return false;
        return true;
    }

    @Override
    public String toString() {
        return "AssociateDTO [associateEvent=" + associateEvent + ", associateOn=" + associateOn + "]";
    }

}
