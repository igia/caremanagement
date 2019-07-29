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
package io.igia.caremanagement.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import io.igia.caremanagement.domain.GoalAssociate;


/**
 * Spring Data  repository for the GoalAssociate entity.
 */

@Repository
public interface GoalAssociateRepository extends JpaRepository<GoalAssociate, Long> {

    List<GoalAssociate> findAllByGoalId(Long goalId);
    
    List<GoalAssociate> findAllByAssociateOn(Long associateOn);
}
