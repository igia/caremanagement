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
package io.igia.caremanagement.service.mapper;

import org.mapstruct.*;

import io.igia.caremanagement.domain.*;
import io.igia.caremanagement.service.dto.GoalAssociateDTO;

/**
 * Mapper for the entity GoalAssociate and its DTO GoalAssociateDTO.
 */
@Mapper(componentModel = "spring", uses = {GoalMapper.class})
public interface GoalAssociateMapper extends EntityMapper<GoalAssociateDTO, GoalAssociate> {

    @Mapping(source = "goal.id", target = "goalId")
    GoalAssociateDTO toDto(GoalAssociate goalAssociate);

    @Mapping(source = "goalId", target = "goal")
    GoalAssociate toEntity(GoalAssociateDTO goalAssociateDTO);

    default GoalAssociate fromId(Long id) {
        if (id == null) {
            return null;
        }
        GoalAssociate goalAssociate = new GoalAssociate();
        goalAssociate.setId(id);
        return goalAssociate;
    }
}
