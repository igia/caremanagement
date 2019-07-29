#
# This Source Code Form is subject to the terms of the Mozilla Public License, v.
# 2.0 with a Healthcare Disclaimer.
# A copy of the Mozilla Public License, v. 2.0 with the Healthcare Disclaimer can
# be found under the top level directory, named LICENSE.
# If a copy of the MPL was not distributed with this file, You can obtain one at
# http://mozilla.org/MPL/2.0/.
# If a copy of the Healthcare Disclaimer was not distributed with this file, You
# can obtain one at the project website https://github.com/igia.
#
# Copyright (C) 2018-2019 Persistent Systems, Inc.
#

Feature: Deploy and create case definition instance in camumda

    Background:
    * def signIn = call read('classpath:login.feature')
    * def accessToken = signIn.accessToken


    Scenario: Deploy the cmmn and dmn

        Given url baseUrl
        And path '/file/program'
        And header Authorization = 'Bearer ' + accessToken
        And header 'Content-Type' = 'multipart/form-data; boundary=----WebKitFormBoundary7MA4YWxkTrZu0gW'
        And multipart file file = { read: 'classpath:testData/Test7.xlsx', filename: 'Test7.xlsx', contentType:'form-data'}
        And request {}
        When method post
        Then status 200
        Then def program3 = response[0].Program.id
        
        Given url baseUrl
        And path '/file/program/'+ program3
        And header Authorization = 'Bearer ' + accessToken
        Then method get
        Then status 200
        Then match header Content-Type == 'text/xml'
        Then xmlstring programXml1 = response
        Then def CreateFile = Java.type('io.igia.caremanagement.CreateFile')
        Then string filePath1 = "target/test17.cmmn"
        Then def result = CreateFile.createFile(programXml1,filePath1)
        
        Given url camundaUrl
        And path '/deployment/create'
        And header Authorization = 'Bearer ' + accessToken
        And header 'Content-Type' = 'multipart/form-data; boundary=----WebKitFormBoundary7MA4YWxkTrZu0gW'
        And multipart file Cmmn1 = { read: 'file:target/test17.cmmn', filename: 'test17.cmmn', contentType:'form-data'}
        And request {}
        When method post
        Then status 200
        
        #Test that if there are no todo tasks returns empty array
        Given url baseUrl
        And path '/tasks'
        And header Authorization = 'Bearer ' + accessToken
        And header 'Content-Type' = 'application/json'
        And request {"taskCategory": "TODO"}
        When method post
        Then status 200
        And match $ == []
        
        #Test that if there are no available tasks returns empty array
        Given url baseUrl
        And path '/tasks'
        And header Authorization = 'Bearer ' + accessToken
        And header 'Content-Type' = 'application/json'
        And request {"taskCategory": "AVAILABLE"}
        When method post
        Then status 200
        And match $ == []

        Given url baseUrl
        And path '/case-definition/create'
        And header Authorization = 'Bearer ' + accessToken
        And header 'Content-Type' = 'application/json'
        And request
            """
            {
            "variables":
            {
            "Navigator" : {"value" : "service-account-internal", "type": "String"},
            "PCP" : {"value" : "service-account-internal", "type": "String"}
            },
            "mrn" : "mrn004",
            "programId": "#(program3)"
            }
            """
        When method post
        Then status 200
        
       