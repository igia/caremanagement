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

Feature: To test get active Goal API scenarios
  I want to test get active Goal API scenarios.

  Background: 
    * def signIn = call read('classpath:login.feature')
    * def accessToken = signIn.accessToken

  Scenario: To test Get Active Goal API is retrieving all active goals for given mrn when program Id and mrn is provided in the request body
    Given url baseUrl
    And path '/goals'
    And header Authorization = 'Bearer ' + accessToken
    And header 'Content-Type' = 'application/json'
    And request
      """
       {"category" : "ACTIVE",
        "mrn" : "test001",
        "programId" : "P1" }	
      """
    When method post
    Then status 200
    Then match response[0].programId == "CasePlanModel_P1"
    Then match response[0] contains {"programId":"#present","programName": "#present"}
    Then match response[0].caseInstances[0] contains {"caseInstanceId":"#present"}
    Then match each response[0].caseInstances[0].goals[*] contains {"id":"#present"}
    Then match each response[0].caseInstances[0].goals[*] contains {"name":"#present"}
    Then match each response[0].caseInstances[0].goals[*] contains {"episodeId":"#present"}
    Then match each response[0].caseInstances[0].goals[*] contains {"episodeName":"#present"}
    Then match each response[0].caseInstances[0].goals[*] contains {"activityId":"#present"}
    Then match response[0].caseInstances[0].goals[0].id == "P1_E1_G1"
    Then match response[0].caseInstances[0].goals[1].id == "P1_E1_G2"

  Scenario: To test Get Active Goal API is retrieving all active goals for given mrn when program Id is not provided in the request body
    Given url baseUrl
    And path '/goals'
    And header Authorization = 'Bearer ' + accessToken
    And header 'Content-Type' = 'application/json'
    And request
      """
       {"category" : "ACTIVE",
        "mrn" : "test001"}	
      """
    When method post
    Then status 200
    Then assert response.length == 2
    Then match response[0].programId == "CasePlanModel_P1"
    Then match response[1].programId == "CasePlanModel_P2"
    Then match each response[*] contains {"programId":"#present","programName": "#present"}
    Then match each response[*].caseInstances[*] contains {"caseInstanceId":"#present"}
    Then match each response[*].caseInstances[*].goals[*] contains {"id":"#present"}
    Then match each response[*].caseInstances[*].goals[*] contains {"name":"#present"}
    Then match each response[*].caseInstances[*].goals[*] contains {"episodeId":"#present"}
    Then match each response[*].caseInstances[*].goals[*] contains {"episodeName":"#present"}
    Then match each response[*].caseInstances[*].goals[*] contains {"activityId":"#present"}
    Then match response[0].caseInstances[0].goals[0].id == "P1_E1_G1"
    Then match response[0].caseInstances[0].goals[1].id == "P1_E1_G2"
    Then match response[1].caseInstances[0].goals[0].id == "P2_E1_G1"
    Then match response[1].caseInstances[0].goals[1].id == "P2_E1_G2"

  Scenario: To test Get Active Goal API is retrieving all active goals when mrn is not provided in the request body
    Given url baseUrl
    And path '/goals'
    And header Authorization = 'Bearer ' + accessToken
    And header 'Content-Type' = 'application/json'
    And request
      """
       {"category" : "ACTIVE",
        "programId" : "P1"}	
      """
    When method post
    Then status 200
    Then assert response.length == 1
    Then match response[0].programId == "CasePlanModel_P1"
    Then match each response[*] contains {"programId":"#present","programName": "#present"}
    Then match each response[*].caseInstances[*] contains {"caseInstanceId":"#present"}
    Then match each response[*].caseInstances[*].goals[*] contains {"id":"#present"}
    Then match each response[*].caseInstances[*].goals[*] contains {"name":"#present"}
    Then match each response[*].caseInstances[*].goals[*] contains {"episodeId":"#present"}
    Then match each response[*].caseInstances[*].goals[*] contains {"episodeName":"#present"}
    Then match each response[*].caseInstances[*].goals[*] contains {"activityId":"#present"}
    Then match response[0].caseInstances[0].goals[0].id == "P1_E1_G1"
    Then match response[0].caseInstances[0].goals[1].id == "P1_E1_G2"
    Then match response[0].caseInstances[1].goals[0].id == "P1_E1_G1"
    Then match response[0].caseInstances[1].goals[1].id == "P1_E1_G2"

  Scenario: To test Get Active Goal API is getting no response when invalid goalCategory is sent in the request
    Given url baseUrl
    And path '/goals'
    And header Authorization = 'Bearer ' + accessToken
    And header 'Content-Type' = 'application/json'
    And request
      """
       {"category" : "WRONG",
        "mrn" : "test001" }	
      """
    When method post
    Then status 400
    Then match response.error == "Bad Request"

  Scenario: To test Get Active Goal API when invalid parameter is sent in the URL
    Given url baseUrl
    And path '/goal'
    And header Authorization = 'Bearer ' + accessToken
    And header 'Content-Type' = 'application/json'
    And request
      """
       {"category" : "ACTIVE",
        "mrn" : "test001" }	
      """
    When method post
    Then status 404
    Then match response.error == "Not Found"

  Scenario: To test Get Active Goal API when extra parameter is sent in request body
    Given url baseUrl
    And path '/goals'
    And header Authorization = 'Bearer ' + accessToken
    And header 'Content-Type' = 'application/json'
    And request
      """
       {"category" : "ACTIVE",
        "mrn" : "test001",
        "otherPara" : "testvalue" }	
      """
    When method post
    Then status 200
    Then match $ != []
    Then assert response.length == 2
    Then match response[0].programId == "CasePlanModel_P1"
    Then match response[1].programId == "CasePlanModel_P2"
    Then match each response[*] contains {"programId":"#present","programName": "#present"}
    Then match each response[*].caseInstances[*] contains {"caseInstanceId":"#present"}
    Then match each response[*].caseInstances[*].goals[*] contains {"id":"#present"}
    Then match each response[*].caseInstances[*].goals[*] contains {"name":"#present"}
    Then match each response[*].caseInstances[*].goals[*] contains {"episodeId":"#present"}
    Then match each response[*].caseInstances[*].goals[*] contains {"episodeName":"#present"}
    Then match each response[*].caseInstances[*].goals[*] contains {"activityId":"#present"}
    Then match response[0].caseInstances[0].goals[0].id == "P1_E1_G1"
    Then match response[0].caseInstances[0].goals[1].id == "P1_E1_G2"
    Then match response[1].caseInstances[0].goals[0].id == "P2_E1_G1"
    Then match response[1].caseInstances[0].goals[1].id == "P2_E1_G2"

  Scenario: To test Get Active Goal API when invalid mrn is passed in request body
    Given url baseUrl
    And path '/goals'
    And header Authorization = 'Bearer ' + accessToken
    And header 'Content-Type' = 'application/json'
    And request
      """
       {"category" : "ACTIVE",
        "mrn" : "invalid" }	
      """
    When method post
    Then status 200
    Then match $ == []
