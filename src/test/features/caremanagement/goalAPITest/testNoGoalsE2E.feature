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

@test
Feature: To test Get Active/Available/Completed Goal API when all the goals and tasks of program are completed

  Background: 
    * def signIn = call read('classpath:login.feature')
    * def accessToken = signIn.accessToken

  Scenario: To complete all the task of all the goals of diffrent program to test completed/active/Available goal API
    # First to complete the human task of a goal by fetching task id from get to-do task API
    Given url baseUrl
    And path '/tasks'
    And header Authorization = 'Bearer ' + accessToken
    And header 'Content-Type' = 'application/json'
    And request
      """
      {
      "taskCategory": "TODO",
      "formField": true,
      "firstResult": "0"
      }
      """
    When method post
    Then status 200
    Then def T001P1G1T3 = response[0].id
    Then def T002P1G1T3 = response[1].id
    Then def T001P2G3T1 = response[2].id
    Then def T001P2G3T2 = response[3].id
    Then def T001P2G1T1 = response[4].id
    Then def T001P2G1T2 = response[5].id
    Then def T001P2G1T3 = response[6].id
    Then def T001P1G3T1 = response[7].id
    Then def T001P1G3T2 = response[8].id
    Then def T001P1G3T3 = response[9].id
    Then def T002P1G3T1 = response[10].id
    Then def T002P1G3T2 = response[11].id
    Then def T002P1G3T3 = response[12].id
    # To complete all the todo task
    Given url baseUrl
    And path '/tasks/' + T001P1G1T3 + '/complete'
    And header Authorization = 'Bearer ' + accessToken
    And header 'Content-Type' = 'application/json'
    And request {}
    When method post
    Then status 200
    Given url baseUrl
    And path '/tasks/' + T002P1G1T3 + '/complete'
    And header Authorization = 'Bearer ' + accessToken
    And header 'Content-Type' = 'application/json'
    And request {}
    When method post
    Then status 200
    Given url baseUrl
    And path '/tasks/' + T001P2G3T1 + '/complete'
    And header Authorization = 'Bearer ' + accessToken
    And header 'Content-Type' = 'application/json'
    And request {}
    When method post
    Then status 200
    Given url baseUrl
    And path '/tasks/' + T001P2G3T2 + '/complete'
    And header Authorization = 'Bearer ' + accessToken
    And header 'Content-Type' = 'application/json'
    And request {}
    When method post
    Then status 200
    Given url baseUrl
    And path '/tasks/' + T001P2G1T1 + '/complete'
    And header Authorization = 'Bearer ' + accessToken
    And header 'Content-Type' = 'application/json'
    And request {}
    When method post
    Then status 200
    Given url baseUrl
    And path '/tasks/' + T001P2G1T2 + '/complete'
    And header Authorization = 'Bearer ' + accessToken
    And header 'Content-Type' = 'application/json'
    And request {}
    When method post
    Then status 200
    Given url baseUrl
    And path '/tasks/' + T001P2G1T3 + '/complete'
    And header Authorization = 'Bearer ' + accessToken
    And header 'Content-Type' = 'application/json'
    And request {}
    When method post
    Then status 200
    Given url baseUrl
    And path '/tasks/' + T001P1G3T1 + '/complete'
    And header Authorization = 'Bearer ' + accessToken
    And header 'Content-Type' = 'application/json'
    And request {}
    When method post
    Then status 200
    Given url baseUrl
    And path '/tasks/' + T001P1G3T2 + '/complete'
    And header Authorization = 'Bearer ' + accessToken
    And header 'Content-Type' = 'application/json'
    And request {}
    When method post
    Then status 200
    Given url baseUrl
    And path '/tasks/' + T001P1G3T3 + '/complete'
    And header Authorization = 'Bearer ' + accessToken
    And header 'Content-Type' = 'application/json'
    And request {}
    When method post
    Then status 200
    Given url baseUrl
    And path '/tasks/' + T002P1G3T1 + '/complete'
    And header Authorization = 'Bearer ' + accessToken
    And header 'Content-Type' = 'application/json'
    And request {}
    When method post
    Then status 200
    Given url baseUrl
    And path '/tasks/' + T002P1G3T2 + '/complete'
    And header Authorization = 'Bearer ' + accessToken
    And header 'Content-Type' = 'application/json'
    And request {}
    When method post
    Then status 200
    Given url baseUrl
    And path '/tasks/' + T002P1G3T3 + '/complete'
    And header Authorization = 'Bearer ' + accessToken
    And header 'Content-Type' = 'application/json'
    And request {}
    When method post
    Then status 200

  Scenario: To test get active goal API when all goals/tasks in the program are completed
    Given url baseUrl
    And path '/goals'
    And header Authorization = 'Bearer ' + accessToken
    And header 'Content-Type' = 'application/json'
    And request
      """
       {"category" : "ACTIVE"}	
      """
    When method post
    Then status 200
    Then match response == []

  Scenario: To test get Available goal API when all goals/tasks in the program are completed
    Given url baseUrl
    And path '/goals'
    And header Authorization = 'Bearer ' + accessToken
    And header 'Content-Type' = 'application/json'
    And request
      """
       {"category" : "AVAILABLE"}	
      """
    When method post
    Then status 200
    Then match response == []

  Scenario: To test get Completed goal API when all goals/tasks in the program are completed
    Given url baseUrl
    And path '/goals'
    And header Authorization = 'Bearer ' + accessToken
    And header 'Content-Type' = 'application/json'
    And request
      """
       {
                "category" : "HISTORY"
      }		
      """
    When method post
    Then status 200
    Then match response[0].programId == "CasePlanModel_P1"
    Then match response[1].programId == "CasePlanModel_P2"
    Then match each response[*] contains {"programId":"#present","programName": "#present"}
    Then match each response[*].caseInstances[*] contains {"caseInstanceId":"#present"}
    Then match each response[*].caseInstances[*].goals[*] contains {"id":"#present"}
    Then match each response[*].caseInstances[*].goals[*] contains {"name":"#present"}
    Then match each response[*].caseInstances[*].goals[*] contains {"episodeId":"#present"}
    Then match each response[*].caseInstances[*].goals[*] contains {"episodeName":"#present"}
    Then match each response[*].caseInstances[*].goals[*] contains {"activityId":"#present"}
