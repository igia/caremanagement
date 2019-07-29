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

Feature: To test start Goal API's
  I want to test all API's related to start Goals from the program.

  Background: 
    * def signIn = call read('classpath:login.feature')
    * def accessToken = signIn.accessToken

  Scenario: To test start Goal API is starting the available goals when correct data is passed in the request body
    #Below steps are to fetch all available goals
    Given url baseUrl
    And path '/goals'
    And header Authorization = 'Bearer ' + accessToken
    And header 'Content-Type' = 'application/json'
    And request
      """
       {"category" : "AVAILABLE",
        "mrn" : "test001",
        "programId" : "P1"
         }	
      """
    When method post
    Then status 200
    Then match response[0].programId == "CasePlanModel_P1"
    Then def goalId = response[0].caseInstances[0].goals[0].id
    Then def caseInstanceId = response[0].caseInstances[0].caseInstanceId
    #Start the available Goal
    Given url baseUrl
    And path '/goals/start'
    And header Authorization = 'Bearer ' + accessToken
    And header 'Content-Type' = 'application/json'
    And request
      """
       {"caseInstanceId" : '#(caseInstanceId)', 
        "goalIdList" : ["#(goalId)"]}	
      """
    When method put
    Then status 200
    Then match response ==
      """
      {
        "P1_E1_G3": "Success"
      }
      """

  Scenario: To check if Goal has been started successfully
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
    Then match response[0].caseInstances[0].goals[2].id == "P1_E1_G3"

  Scenario: To check if started goal is not in the available goal list
    Given url baseUrl
    And path '/goals'
    And header Authorization = 'Bearer ' + accessToken
    And header 'Content-Type' = 'application/json'
    And request
      """
       {"category" : "AVAILABLE",
        "mrn" : "test001",
        "programId" : "P1"
         }	
      """
    When method post
    Then status 200
    Then match response == []

  Scenario: To test Start Goal API is getting no response when invalid caseInstanceId is sent in the request
    Given url baseUrl
    And path '/goals/start'
    And header Authorization = 'Bearer ' + accessToken
    And header 'Content-Type' = 'application/json'
    And request
      """
             {"caseInstanceId" : "0ff7dc3e-0e5d-11e9", 
              "goalIdList" : ["P1_E1_G3"] }	
      """
    When method put
    Then status 200
    Then match response ==
      """
      {
      "P1_E1_G3": "Fail"
      }
      """

  Scenario: To test Start Goal API when invalid parameter is sent in the URL
    Given url baseUrl
    And path '/goals/starts'
    And header Authorization = 'Bearer ' + accessToken
    And header 'Content-Type' = 'application/json'
    And request
      """
            {"caseInstanceId" : "0ff7dc3e-0e5d-11e9-a90a-00155d6bfe00", 
             "goalIdList" : ["P2_E2_G1"] }	
      """
    When method put
    Then status 404
    Then match response.error == "Not Found"

  Scenario: To test Start Goal API  when extra parameter is sent in request body
    #To fetch available goal list
    Given url baseUrl
    And path '/goals'
    And header Authorization = 'Bearer ' + accessToken
    And header 'Content-Type' = 'application/json'
    And request
      """
       {"category" : "AVAILABLE",
        "mrn" : "test002",
        "programId" : "P1"
         }	
      """
    When method post
    Then status 200
    Then match response[0].programId == "CasePlanModel_P1"
    Then def goalId = response[0].caseInstances[0].goals[0].id
    Then def caseInstanceId = response[0].caseInstances[0].caseInstanceId
    #Start the available Goal when extra parameter is passed in the request
    Given url baseUrl
    And path '/goals/start'
    And header Authorization = 'Bearer ' + accessToken
    And header 'Content-Type' = 'application/json'
    And request
      """
       {"caseInstanceId" : '#(caseInstanceId)', 
        "goalIdList" : ["#(goalId)"],
        "extra parameter" : "test" }
      """
    When method put
    Then status 200
    Then match response ==
      """
      {
        "P1_E1_G3": "Success"
      }
      """

  Scenario: To test Start Goal API when invalid goal id in goalList is passed in request body
    #To fetch valid case instance if from available goals
    Given url baseUrl
    And path '/goals'
    And header Authorization = 'Bearer ' + accessToken
    And header 'Content-Type' = 'application/json'
    And request
      """
       {"category" : "AVAILABLE",
        "mrn" : "test001",
        "programId" : "P2"
         }	
      """
    When method post
    Then status 200
    Then match response[0].programId == "CasePlanModel_P2"
    Then def goalId = response[0].caseInstances[0].goals[0].id
    Then def caseInstanceId = response[0].caseInstances[0].caseInstanceId
    #Start the available Goal API when invaid goalId is passed in the request
    Given url baseUrl
    And path '/goals/start'
    And header Authorization = 'Bearer ' + accessToken
    And header 'Content-Type' = 'application/json'
    And request
      """
       {"caseInstanceId" : '#(caseInstanceId)', 
        "goalIdList" : ["P2_E1_G5"] }
      """
    When method put
    Then status 200
    #we get success response for any goal id if valid case instance id is passed
    Then match response ==
      """
      {
        "P2_E1_G5": "Success"
      }
      """
