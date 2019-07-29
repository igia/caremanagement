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
package io.igia.caremanagement.service.impl;

import org.springframework.beans.factory.annotation.Autowired;

import io.igia.caremanagement.service.EpisodeAssociateService;
import io.igia.caremanagement.service.EpisodeService;
import io.igia.caremanagement.service.GoalAssociateService;
import io.igia.caremanagement.service.GoalService;
import io.igia.caremanagement.service.ProgramService;
import io.igia.caremanagement.service.TaskAssociateService;
import io.igia.caremanagement.service.TaskService;

public abstract class EntityServices {
    @Autowired
    ProgramService programService;

    @Autowired
    EpisodeService episodeService;

    @Autowired
    EpisodeAssociateService episodeAssocService;

    @Autowired
    GoalService goalService;

    @Autowired
    GoalAssociateService goalAssocService;

    @Autowired
    TaskService taskService;

    @Autowired
    TaskAssociateService taskAssocService;
}
