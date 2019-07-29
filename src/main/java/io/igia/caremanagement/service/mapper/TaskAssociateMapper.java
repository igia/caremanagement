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
import io.igia.caremanagement.service.dto.TaskAssociateDTO;

/**
 * Mapper for the entity TaskAssociate and its DTO TaskAssociateDTO.
 */
@Mapper(componentModel = "spring", uses = {TaskMapper.class})
public interface TaskAssociateMapper extends EntityMapper<TaskAssociateDTO, TaskAssociate> {

    @Mapping(source = "task.id", target = "taskId")
    TaskAssociateDTO toDto(TaskAssociate taskAssociate);

    @Mapping(source = "taskId", target = "task")
    TaskAssociate toEntity(TaskAssociateDTO taskAssociateDTO);

    default TaskAssociate fromId(Long id) {
        if (id == null) {
            return null;
        }
        TaskAssociate taskAssociate = new TaskAssociate();
        taskAssociate.setId(id);
        return taskAssociate;
    }
}
