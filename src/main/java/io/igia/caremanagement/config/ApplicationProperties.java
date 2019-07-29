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
package io.igia.caremanagement.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Properties specific to Caremanagement.
 * <p>
 * Properties are configured in the application.yml file.
 * See {@link io.github.jhipster.config.JHipsterProperties} for a good example.
 */
@ConfigurationProperties(prefix = "application", ignoreUnknownFields = false)
public class ApplicationProperties {
	private String dmnDirectory;
	private Integer defaultTaskSla;

	public String getDmnDirectory() {
		return dmnDirectory;
	}

	public void setDmnDirectory(String dmnDirectory) {
		this.dmnDirectory = dmnDirectory;
	}

	public Integer getDefaultTaskSla() {
	    return defaultTaskSla;
	}
	
	public void setDefaultTaskSla(Integer defaultTaskSla) {
	    this.defaultTaskSla = defaultTaskSla;
	}
}
