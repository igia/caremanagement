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
package io.igia.caremanagement.service.impl.camunda;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import io.igia.caremanagement.client.camunda.CamundaClient;
import io.igia.caremanagement.service.camunda.DeploymentService;
import io.igia.caremanagement.service.dto.camunda.DeploymentResponse;

@Service("camundaDeploymentService")
@Transactional
public class DeploymentServiceImpl implements DeploymentService {
	private final Logger log = LoggerFactory.getLogger(DeploymentServiceImpl.class);

	@Autowired
	private CamundaClient camundaClient;

	@Override
	public List<DeploymentResponse> getDeployments(String id, String nameLike, String name, String source) {
		log.info("get all deployments for id:{}", id);
		return camundaClient.getDeployments(id, nameLike, name, source);
	}
	
	@Override
	public void deleteDeployment(String id, Boolean cascade) {
		log.info("delete deployment id: {}", id);
		camundaClient.deleteDeployment(id, cascade);
	}
}