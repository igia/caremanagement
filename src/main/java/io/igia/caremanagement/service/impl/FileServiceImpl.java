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

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.camunda.bpm.model.cmmn.Cmmn;
import org.camunda.bpm.model.cmmn.CmmnModelInstance;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import io.igia.caremanagement.service.FileService;

/**
 * Service for importing exporting the files.
 */
@Service
@Transactional
public class FileServiceImpl implements FileService {

    private static final Logger log = LoggerFactory.getLogger(FileServiceImpl.class);
   
    @Autowired
    ProgramImportServiceImpl importProgram;

    @Autowired
    ProgramExportServiceImpl exportProgram;

    // create cmmn model instance and export as resource
    @Transactional(propagation = Propagation.REQUIRES_NEW, readOnly = true)
    public Resource exportFileAsResource(String def, String id) {
        CmmnModelInstance modelInstance = exportProgram.createProgram(id);
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        Cmmn.writeModelToStream(outputStream, modelInstance);
        return new ByteArrayResource(outputStream.toByteArray());
    }
    

    // import care program definition in excel file format in to DB
    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public ArrayNode importProgramFile(MultipartFile file) {        
        if (file == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Program MultipartFile is NULL");
        }

     // we create an XSSF Workbook object for our XLSX Excel File
        try (InputStream fis = file.getInputStream(); XSSFWorkbook workbook = new XSSFWorkbook(fis)) {
            ObjectMapper mapper = new ObjectMapper();
            ArrayNode workbookResponse = mapper.createArrayNode();

            // we import all worksheets in the workbook
            int worksheetsCount = workbook.getNumberOfSheets();
            for (int index = 0; index < worksheetsCount; index++) {
                // we get worksheet
                XSSFSheet worksheet = workbook.getSheetAt(index);
                
                if (worksheet == null)
                    throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Error reading worksheet at index# " + index + ", worksheet object is null");

                // import program from worksheet
                ObjectNode worksheetResponse = importProgramWorksheet(worksheet, index);

                workbookResponse.add(worksheetResponse);
            }
            return workbookResponse;
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "File import error", e);
        }
    }


    /*
     * imports program from worksheet and creates error response if it fails
     */
    private ObjectNode importProgramWorksheet(XSSFSheet worksheet, int index) {
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode worksheetResponse = null;
        try {
            worksheetResponse = importProgram.importProgramWorksheet(worksheet);
        } catch (Exception e) {
            log.error("Error importing worksheet: {}, exception class: {}, message: {}", worksheet.getSheetName(), e.getClass().getSimpleName(), e.getMessage());
            worksheetResponse = mapper.createObjectNode();
            ObjectNode error = worksheetResponse.putObject(worksheet.getSheetName());
            error.put("status", HttpStatus.UNPROCESSABLE_ENTITY.value());
            error.put("error", HttpStatus.UNPROCESSABLE_ENTITY.getReasonPhrase());
            String message = null;
            if (e instanceof ResponseStatusException) {
                ResponseStatusException rse = (ResponseStatusException) e;
                message = rse.getReason();
            } else {
                message = e.getMessage();
            }
            error.put("message", "Error importing worksheet at index# " + (index + 1) + ", " + message);
        }
        return worksheetResponse;
    }
}