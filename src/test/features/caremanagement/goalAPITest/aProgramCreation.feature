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

Feature: To create test program in Camunda and create its test instances

  Background: 
    * def signIn = call read('classpath:login.feature')
    * def accessToken = signIn.accessToken


  Scenario: Create the CMMN from the excel sheet
    Given url baseUrl
    And path '/file/program'
    And header Authorization = 'Bearer ' + accessToken
    And header 'Content-Type' = 'multipart/form-datamultipart/form-data; boundary=----WebKitFormBoundary7MA4YWxkTrZu0gW'
    And multipart file file = { read: 'classpath:caremanagement/goalAPITest/AdvanceProgramGoalTesting.xlsx', filename: 'AdvanceProgramGoalTesting.xlsx', contentType:'form-data'}
    And request {}
    When method post
    Then status 200
    Then def program1 = response[0]["Advanced Program"].id
    Then def program2 = response[1]["Advanced Program 2"].id
    Given url baseUrl
    And path '/file/program/' + program1
    And header Authorization = 'Bearer ' + accessToken
    Then method get
    Then status 200
    Then match header Content-Type == 'text/xml'
    Then xmlstring programXml1 = response
    Then def CreateFile = Java.type('io.igia.caremanagement.CreateFile')
    Then string filePath1 = "target/AdvanceProgramGoalTesting1.cmmn"
    Then def result = CreateFile.createFile(programXml1,filePath1)
    Given url baseUrl
    And path '/file/program/' + program2
    And header Authorization = 'Bearer ' + accessToken
    Then method get
    Then status 200
    Then match header Content-Type == 'text/xml'
    Then xmlstring programXml2 = response
    Then def CreateFile = Java.type('io.igia.caremanagement.CreateFile')
    Then string filePath2 = "target/AdvanceProgramGoalTesting2.cmmn"
    Then def result = CreateFile.createFile(programXml2,filePath2)
    Given url camundaUrl
    And path '/deployment/create'
    And header Authorization = 'Bearer ' + accessToken
    And header 'Content-Type' = 'multipart/form-datamultipart/form-data; boundary=----WebKitFormBoundary7MA4YWxkTrZu0gW'
    And multipart file DepCmmn1 = { read: 'file: target/AdvanceProgramGoalTesting1.cmmn', filename: 'AdvanceProgramGoalTesting1.cmmn', contentType:'form-data'}
    And multipart file DepCmmn2 = { read: 'file: target/AdvanceProgramGoalTesting2.cmmn', filename: 'AdvanceProgramGoalTesting2.cmmn', contentType:'form-data'}
    And request {}
    When method post
    Then status 200

  Scenario: Create first instance of the case definition
    Given url camundaUrl
    And path 'case-definition/key/Case_P1/create'
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
      "businessKey" : "test001"
      }
      """
    When method post
    Then status 200

  Scenario: Create second  instance of the case definition
    Given url camundaUrl
    And path 'case-definition/key/Case_P1/create'
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
      "businessKey" : "test002"
      }
      """
    When method post
    Then status 200

  Scenario: Create third  instance of the case definition
    Given url camundaUrl
    And path 'case-definition/key/Case_P2/create'
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
      "businessKey" : "test001"
      }
      """
    When method post
    Then status 200
