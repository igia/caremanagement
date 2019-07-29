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
package io.igia.caremanagement.service.camunda;

import java.util.List;

import io.igia.caremanagement.service.dto.camunda.CaseInstance;
import io.igia.caremanagement.service.dto.camunda.CaseInstanceRequest;
import io.igia.caremanagement.service.dto.camunda.CaseInstanceVariableRequest;

public interface CaseInstanceService {

	List<CaseInstance> getCaseInstances(String caseInstanceId, String mrn, Integer firstResult, Integer maxResults, String programId, Boolean active, Boolean completed);
	
	CaseInstance createCaseInstance(CaseInstanceRequest payload);

	void updateCaseInstance(String id, CaseInstanceVariableRequest payload);

	void closeCaseInstance(String mrn, String programId);
	
	void terminateCaseInstance(String mrn, String programId);
}
