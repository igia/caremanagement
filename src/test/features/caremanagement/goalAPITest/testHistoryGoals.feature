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

Feature: To test complete Goal API's
  I want to test all API's related to complete Goals from the program.

  Background: 
    * def signIn = call read('classpath:login.feature')
    * def accessToken = signIn.accessToken

  Scenario: To complete the task of 2 goals of diffrent program to test completed goal API
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
    Then def T001P1G1T1 = response[0].id
    Then def T001P1G1T2 = response[1].id
    Then def T001P1G2T1 = response[2].id
    Then def T001P1G2T2 = response[3].id
    Then def T002P1G1T1 = response[4].id
    Then def T002P1G1T2 = response[5].id
    Then def T002P1G2T1 = response[6].id
    Then def T002P1G2T2 = response[7].id
    Then def T001P2G2T1 = response[11].id
    Then def T001P2G2T2 = response[12].id
    # To complete the to-do task of first goal of first program of test001 instance
    Given url baseUrl
    And path '/tasks/' + T001P1G1T1 + '/complete'
    And header Authorization = 'Bearer ' + accessToken
    And header 'Content-Type' = 'application/json'
    And request {}
    When method post
    Then status 200
    Given url baseUrl
    And path '/tasks/' + T001P1G1T2 + '/complete'
    And header Authorization = 'Bearer ' + accessToken
    And header 'Content-Type' = 'application/json'
    And request {}
    When method post
    Then status 200
    # To complete the to-do task of second  goal of first program of test001 instance
    Given url baseUrl
    And path '/tasks/' + T001P1G2T1 + '/complete'
    And header Authorization = 'Bearer ' + accessToken
    And header 'Content-Type' = 'application/json'
    And request {}
    When method post
    Then status 200
    Given url baseUrl
    And path '/tasks/' + T001P1G2T2 + '/complete'
    And header Authorization = 'Bearer ' + accessToken
    And header 'Content-Type' = 'application/json'
    And request {}
    When method post
    Then status 200
    # To complete the to-do task of first goal of first program of test002 instance
    Given url baseUrl
    And path '/tasks/' + T002P1G1T1 + '/complete'
    And header Authorization = 'Bearer ' + accessToken
    And header 'Content-Type' = 'application/json'
    And request {}
    When method post
    Then status 200
    Given url baseUrl
    And path '/tasks/' + T002P1G1T2 + '/complete'
    And header Authorization = 'Bearer ' + accessToken
    And header 'Content-Type' = 'application/json'
    And request {}
    When method post
    Then status 200
    # To complete the to-do task of second goal of first program of test002 instance
    Given url baseUrl
    And path '/tasks/' + T002P1G2T1 + '/complete'
    And header Authorization = 'Bearer ' + accessToken
    And header 'Content-Type' = 'application/json'
    And request {}
    When method post
    Then status 200
    Given url baseUrl
    And path '/tasks/' + T002P1G2T2 + '/complete'
    And header Authorization = 'Bearer ' + accessToken
    And header 'Content-Type' = 'application/json'
    And request {}
    When method post
    Then status 200
    # To complete the to-do task of second goal of second  program of test001 instance
    Given url baseUrl
    And path '/tasks/' + T001P2G2T1 + '/complete'
    And header Authorization = 'Bearer ' + accessToken
    And header 'Content-Type' = 'application/json'
    And request {}
    When method post
    Then status 200
    Given url baseUrl
    And path '/tasks/' + T001P2G2T2 + '/complete'
    And header Authorization = 'Bearer ' + accessToken
    And header 'Content-Type' = 'application/json'
    And request {}
    When method post
    Then status 200

  Scenario: To test History/completed Goal API is fetching all completed goals when programId and mrn is passed in the request body
    Given url baseUrl
    And path '/goals'
    And header Authorization = 'Bearer ' + accessToken
    And header 'Content-Type' = 'application/json'
    And request
      """
       {
                "category" : "HISTORY",
                 "mrn" : "test001",
                 "programId" : "P1"
      }		
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
    Then def goalId1 = response[0].caseInstances[0].goals[0].id
    Then match goalId1 == "P1_E1_G2"
    #For first Program
    Given url baseUrl
    And path '/goals'
    And header Authorization = 'Bearer ' + accessToken
    And header 'Content-Type' = 'application/json'
    And request
      """
       {
                "category" : "HISTORY",
                 "mrn" : "test001",
                 "programId" : "P1"
      }		
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
    Then def goalId1 = response[0].caseInstances[0].goals[0].id
    Then match goalId1 == "P1_E1_G2"
    #For second Program
    Given url baseUrl
    And path '/goals'
    And header Authorization = 'Bearer ' + accessToken
    And header 'Content-Type' = 'application/json'
    And request
      """
       {
                "category" : "HISTORY",
                 "mrn" : "test001",
                 "programId" : "P2"
      }		
      """
    When method post
    Then status 200
    Then match response[0].programId == "CasePlanModel_P2"
    Then match response[0] contains {"programId":"#present","programName": "#present"}
    Then match response[0].caseInstances[0] contains {"caseInstanceId":"#present"}
    Then match each response[0].caseInstances[0].goals[*] contains {"id":"#present"}
    Then match each response[0].caseInstances[0].goals[*] contains {"name":"#present"}
    Then match each response[0].caseInstances[0].goals[*] contains {"episodeId":"#present"}
    Then match each response[0].caseInstances[0].goals[*] contains {"episodeName":"#present"}
    Then match each response[0].caseInstances[0].goals[*] contains {"activityId":"#present"}
    Then def goalId2 = response[0].caseInstances[0].goals[0].id
    Then match goalId2 == "P2_E1_G2"

  Scenario: To test History/completed Goal API is fetching all completed goals when only mrn is passed in the request body
    #For first Program
    Given url baseUrl
    And path '/goals'
    And header Authorization = 'Bearer ' + accessToken
    And header 'Content-Type' = 'application/json'
    And request
      """
       {
                "category" : "HISTORY",
                 "mrn" : "test001"
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
    Then def goalId1 = response[0].caseInstances[0].goals[0].id
    Then def goalId2 = response[1].caseInstances[0].goals[0].id
    Then match goalId1 == "P1_E1_G2"
    Then match goalId2 == "P2_E1_G2"

  Scenario: To test History/completed Goal API is fetching all completed goals when mrn and program id is not passed in the request body
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
    Then def goalId1 = response[0].caseInstances[0].goals[0].id
    Then def goalId2 = response[0].caseInstances[1].goals[0].id
    Then def goalId3 = response[1].caseInstances[0].goals[0].id
    Then match goalId1 == "P1_E1_G2"
    Then match goalId2 == "P1_E1_G2"
    Then match goalId3 == "P2_E1_G2"

  Scenario: To test History Goal API when filtered by date.
    #Generate the current, previous and next date.
    * def getDate =
      """
       function(s) {
      var SimpleDateFormat = Java.type('java.text.SimpleDateFormat');
      var sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssXXX");
      var currDate = new java.util.Date();
      var prevDate =  new java.util.Date();
      var nextDate =  new java.util.Date();
      prevDate.setDate(prevDate.getDate()-1);
      nextDate.setDate(nextDate.getDate()+1);
      var dataJSON = {};
      dataJSON.currDate = sdf.format(currDate);
      dataJSON.prevDate = sdf.format(prevDate);
      dataJSON.nextDate = sdf.format(nextDate);
      return dataJSON;
      } 
      """
    * def dataJSON = getDate()
    #To test History Goal API when endedAfter parameter is given in the request. API should fetch all completed goals which are completed after given date
    #For Previous Date
    Given url baseUrl
    And path '/goals'
    And header Authorization = 'Bearer ' + accessToken
    And header 'Content-Type' = 'application/json'
    And request
      """
       {
                "category" : "HISTORY",
                "mrn" : "test001",
                "endedAfter" : '#(dataJSON.prevDate)'
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
    Then def goalId1 = response[0].caseInstances[0].goals[0].id
    Then def goalId2 = response[1].caseInstances[0].goals[0].id
    Then match goalId1 == "P1_E1_G2"
    Then match goalId2 == "P2_E1_G2"
    ##For current Date
    Given url baseUrl
    And path '/goals'
    And header Authorization = 'Bearer ' + accessToken
    And header 'Content-Type' = 'application/json'
    And request
      """
       {
                "category" : "HISTORY",
                "mrn" : "test001",
                "endedAfter" : '#(dataJSON.currDate)'
      }		
      """
    When method post
    Then status 200
    #Then match response == []
    #For Next Date
    Given url baseUrl
    And path '/goals'
    And header Authorization = 'Bearer ' + accessToken
    And header 'Content-Type' = 'application/json'
    And request
      """
       {
                "category" : "HISTORY",
                "mrn" : "test001",
                "endedAfter" : '#(dataJSON.nextDate)'
      }		
      """
    When method post
    Then status 200
    Then match response == []
    #History Goal API when endedBefore parameter is given in the request. API should fetch all completed goals which are completed before given date
    #For Previous Date
    Given url baseUrl
    And path '/goals'
    And header Authorization = 'Bearer ' + accessToken
    And header 'Content-Type' = 'application/json'
    And request
      """
       {
                "category" : "HISTORY",
                "mrn" : "test001",
                "endedBefore" : '#(dataJSON.prevDate)'
      }		
      """
    When method post
    Then status 200
    Then match response == []
    #For Current Date
    Given url baseUrl
    And path '/goals'
    And header Authorization = 'Bearer ' + accessToken
    And header 'Content-Type' = 'application/json'
    And request
      """
       {
                "category" : "HISTORY",
                "mrn" : "test001",
                "endedBefore" : '#(dataJSON.currDate)'
      }		
      """
    When method post
    Then status 200
    Then match response[0].programId == "CasePlanModel_P1"
    #Then match response[1].programId == "CasePlanModel_P2"
    Then match each response[*] contains {"programId":"#present","programName": "#present"}
    Then match each response[*].caseInstances[*] contains {"caseInstanceId":"#present"}
    Then match each response[*].caseInstances[*].goals[*] contains {"id":"#present"}
    Then match each response[*].caseInstances[*].goals[*] contains {"name":"#present"}
    Then match each response[*].caseInstances[*].goals[*] contains {"episodeId":"#present"}
    Then match each response[*].caseInstances[*].goals[*] contains {"episodeName":"#present"}
    Then match each response[*].caseInstances[*].goals[*] contains {"activityId":"#present"}
    Then def goalId1 = response[0].caseInstances[0].goals[0].id
    #Then def goalId2 = response[1].caseInstances[0].goals[0].id
    Then match goalId1 == "P1_E1_G2"
    #Then match goalId2 == "P2_E1_G2"
    #For Next Date
    Given url baseUrl
    And path '/goals'
    And header Authorization = 'Bearer ' + accessToken
    And header 'Content-Type' = 'application/json'
    And request
      """
       {
                "category" : "HISTORY",
                "mrn" : "test001",
                "endedBefore" : '#(dataJSON.nextDate)'
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
    Then def goalId1 = response[0].caseInstances[0].goals[0].id
    Then def goalId2 = response[1].caseInstances[0].goals[0].id
    Then match goalId1 == "P1_E1_G2"
    Then match goalId2 == "P2_E1_G2"
    #History Goal API when createdBefore parameter is given in the request. API should fetch all completed goals which are created before given date
    #For Previous Date
    Given url baseUrl
    And path '/goals'
    And header Authorization = 'Bearer ' + accessToken
    And header 'Content-Type' = 'application/json'
    And request
      """
       {
                "category" : "HISTORY",
                "mrn" : "test001",
                "createdBefore" : '#(dataJSON.prevDate)'
      }		
      """
    When method post
    Then status 200
    Then match response == []
    #For Current Date
    Given url baseUrl
    And path '/goals'
    And header Authorization = 'Bearer ' + accessToken
    And header 'Content-Type' = 'application/json'
    And request
      """
       {
                "category" : "HISTORY",
                "mrn" : "test001",
                "createdBefore" : '#(dataJSON.currDate)'
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
    Then def goalId1 = response[0].caseInstances[0].goals[0].id
    Then def goalId2 = response[1].caseInstances[0].goals[0].id
    Then match goalId1 == "P1_E1_G2"
    Then match goalId2 == "P2_E1_G2"
    #For Next Date
    Given url baseUrl
    And path '/goals'
    And header Authorization = 'Bearer ' + accessToken
    And header 'Content-Type' = 'application/json'
    And request
      """
       {
                "category" : "HISTORY",
                "mrn" : "test001",
                "createdBefore" : '#(dataJSON.nextDate)'
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
    Then def goalId1 = response[0].caseInstances[0].goals[0].id
    Then def goalId2 = response[1].caseInstances[0].goals[0].id
    Then match goalId1 == "P1_E1_G2"
    Then match goalId2 == "P2_E1_G2"
    #History Goal API when createdAfter parameter is given in the request. API should fetch all completed goals which are created after given date
    Given url baseUrl
    And path '/goals'
    And header Authorization = 'Bearer ' + accessToken
    And header 'Content-Type' = 'application/json'
    And request
      """
       {
                "category" : "HISTORY",
                "mrn" : "test001",
                "createdAfter" : '#(dataJSON.prevDate)'
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
    Then def goalId1 = response[0].caseInstances[0].goals[0].id
    Then def goalId2 = response[1].caseInstances[0].goals[0].id
    Then match goalId1 == "P1_E1_G2"
    Then match goalId2 == "P2_E1_G2"

  Scenario: To test History Goal API is fetching No goals and fail with error bad request 400 when invalid goalCategory is passed in the valid request body
    Given url baseUrl
    And path '/goals'
    And header Authorization = 'Bearer ' + accessToken
    And header 'Content-Type' = 'application/json'
    And request
      """
       {
                "category" : "HISTORIC",
                "mrn" : "test001",
                "programId" : "P1"
      }		
      """
    When method post
    Then status 400
    Then match response.error == "Bad Request"

  Scenario: To test History Goal API is fetching No goals a when invalid mrn is passed in the valid request body
    Given url baseUrl
    And path '/goals'
    And header Authorization = 'Bearer ' + accessToken
    And header 'Content-Type' = 'application/json'
    And request
      """
       {
                "category" : "HISTORY",
                "mrn" : "test",
                "programId" : "P1"
      }		
      """
    When method post
    Then status 200
    Then match response == []

  Scenario: To test History Goal API is fetching No goals a when invalid programId is passed in the valid request body
    Given url baseUrl
    And path '/goals'
    And header Authorization = 'Bearer ' + accessToken
    And header 'Content-Type' = 'application/json'
    And request
      """
       {
                "category" : "HISTORY",
                "mrn" : "test001",
                "programId" : "P276"
      }		
      """
    When method post
    Then status 200
    Then match response == []

  Scenario: To test History Goal API is fetching all completed goals when extra parameter is sent in valid request body
    Given url baseUrl
    And path '/goals'
    And header Authorization = 'Bearer ' + accessToken
    And header 'Content-Type' = 'application/json'
    And request
      """
       {
                "category" : "HISTORY",
                "mrn" : "test001",
                "programId" : "P1",
                "extra parameter" : "Hello"
      }		
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
    Then def goalId1 = response[0].caseInstances[0].goals[0].id
    Then match goalId1 == "P1_E1_G2"

  Scenario: To test History Goal API is getting failed with response code 405 when invalid method name is sent in the request
    Given url baseUrl
    And path '/goals'
    And header Authorization = 'Bearer ' + accessToken
    And header 'Content-Type' = 'application/json'
    And request
      """
       {
                "category" : "HISTORY",
                "mrn" : "test001",
                "programId" : "P1"
      }		
      """
    When method put
    Then status 405
    Then match response.title == "Method Not Allowed"

  Scenario: To test History Goal API is getting failed with response code 404 when invalid URL is sent in the request
    Given url baseUrl
    And path '/goal'
    And header Authorization = 'Bearer ' + accessToken
    And header 'Content-Type' = 'application/json'
    And request
      """
       {
                "category" : "HISTORY",
                "mrn" : "test001",
                "programId" : "P1"
      }		
      """
    When method put
    Then status 404
    Then match response.error == "Not Found"
