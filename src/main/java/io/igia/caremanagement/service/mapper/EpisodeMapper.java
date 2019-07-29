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
import io.igia.caremanagement.service.dto.EpisodeDTO;

/**
 * Mapper for the entity Episode and its DTO EpisodeDTO.
 */
@Mapper(componentModel = "spring", uses = {ProgramMapper.class})
public interface EpisodeMapper extends EntityMapper<EpisodeDTO, Episode> {

    @Mapping(source = "program.id", target = "programId")
    EpisodeDTO toDto(Episode episode);

    @Mapping(source = "programId", target = "program")
    @Mapping(target = "goals", ignore = true)
    @Mapping(target = "episodeAssociates", ignore = true)
    Episode toEntity(EpisodeDTO episodeDTO);

    default Episode fromId(Long id) {
        if (id == null) {
            return null;
        }
        Episode episode = new Episode();
        episode.setId(id);
        return episode;
    }
}
