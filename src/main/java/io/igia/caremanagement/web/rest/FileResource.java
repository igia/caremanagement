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
package io.igia.caremanagement.web.rest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import com.fasterxml.jackson.databind.node.ArrayNode;

import io.igia.caremanagement.service.FileService;
import io.swagger.annotations.ApiOperation;

/**
 * REST controller for importing exporting the files.
 */
@RestController
@RequestMapping("/api")
public class FileResource {

    private static final Logger log = LoggerFactory.getLogger(FileResource.class);

    public static final String PATH_VAR_CARE_PROGRAM = "program";

    @Autowired
    private FileService fileService;

    @PostMapping("/file/{definition}")
    public ResponseEntity<Object> uploadFile(@PathVariable String definition, @RequestParam("file") MultipartFile file) {
        if (definition == null)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Path variable definition is NULL");
        
        if (!definition.equalsIgnoreCase(PATH_VAR_CARE_PROGRAM))
            throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY, "Unsupported definition value: " + definition);

        log.debug("importing programs from file {}", file.getOriginalFilename());
        ArrayNode response = fileService.importProgramFile(file);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/file/{definition}/{id}")
    @ApiOperation(notes = "This downloads .cmmn file", value = "Returns .cmmn file")
    public ResponseEntity<Resource> downloadFile(@PathVariable String definition, @PathVariable String id) {
        if (definition == null)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Path variable definition is NULL");

        if (id == null)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Path variable id is NULL");

        if (!definition.equalsIgnoreCase(PATH_VAR_CARE_PROGRAM))
            throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY, "Unsupported definition value: " + definition);
        
        log.debug("exporting program having id {} as cmmn definition", id);
        Resource resource = fileService.exportFileAsResource(definition, id);
        return ResponseEntity.ok()
                .contentType(MediaType.TEXT_XML)
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + id +  ".cmmn" + "\"")
                .body(resource);
    }
}
