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
import io.igia.caremanagement.service.dto.GoalDTO;

/**
 * Mapper for the entity Goal and its DTO GoalDTO.
 */
@Mapper(componentModel = "spring", uses = {EpisodeMapper.class})
public interface GoalMapper extends EntityMapper<GoalDTO, Goal> {

    @Mapping(source = "episode.id", target = "episodeId")
    GoalDTO toDto(Goal goal);

    @Mapping(source = "episodeId", target = "episode")
    @Mapping(target = "tasks", ignore = true)
    @Mapping(target = "goalAssociates", ignore = true)
    Goal toEntity(GoalDTO goalDTO);

    default Goal fromId(Long id) {
        if (id == null) {
            return null;
        }
        Goal goal = new Goal();
        goal.setId(id);
        return goal;
    }
}
