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
package io.igia.caremanagement.service.dto.camunda;

import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@JsonInclude(Include.NON_NULL)
public class CaseInstanceVariableRequest {
	private Map<String, Variable> modifications;
	
	private List<String> deletions;

	public Map<String, Variable> getModifications() {
		return modifications;
	}

	public void setModifications(Map<String, Variable> modifications) {
		this.modifications = modifications;
	}

	public List<String> getDeletions() {
		return deletions;
	}

	public void setDeletions(List<String> deletions) {
		this.deletions = deletions;
	}
	
	
}
