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

Feature: Test CURD operations on goal associate resource

    Background:
    * def signIn = call read('classpath:login.feature')
    * def accessToken = signIn.accessToken

    Scenario: Test creation of program resource,episode,goal and then creating a goal associate
        Given url baseUrl
        And path '/definitions/programs'
        And header Authorization = 'Bearer ' + accessToken
        And header 'Content-Type' = 'application/json'
        And request {"description": "Program to analyze the BP",  "programIdU": "BP_Assessment",  "name": "BP_Assessment"}
        When method post
        Then status 201
        Then match response.description == "Program to analyze the BP"
        Then match response.programIdU == "BP_Assessment"
        Then match response.name == "BP_Assessment"
        Then match response.id == "##notnull"
        Then def programId = get response.id

        Given url baseUrl
        And path '/definitions/episodes'
        And header Authorization = 'Bearer ' + accessToken
        And header 'Content-Type' = 'application/json'
        And request {"description": "Episode Testing","episodeIdU": "Episode1","name": "Test Episode","programId": '#(programId)',"lookup" : "Episode1"}
        When method post
        Then status 201
        Then match response.description == "Episode Testing"
        Then match response.episodeIdU == "Episode1"
        Then match response.name == "Test Episode"
        Then match response.programId == programId
        Then def episodeId = response.id

        Given url baseUrl
        And path '/definitions/goals'
        And header Authorization = 'Bearer ' + accessToken
        And header 'Content-Type' = 'application/json'
        And request {"description": "Goal To fetch BP results", "entryCriteria": "PCP == true", "episodeId": '#(episodeId)', "etaValue": 6 ,"etaUnit": "MONTHS" , "goalIdU": "Goal_1","name": "Goal_1","lookup" : "Goal1"}
        When method post
        Then status 201
        Then match response.description == "Goal To fetch BP results"
        Then match response.episodeId == episodeId
        Then match response.name == "Goal_1"
        Then match response.goalIdU == "Goal_1"
        Then match response.entryCriteria == "PCP == true"
        Then match response.etaValue == 6
        Then match response.etaUnit == "MONTHS"
        Then def goalId = response.id

        Given url baseUrl
        And path '/definitions/goals'
        And header Authorization = 'Bearer ' + accessToken
        And header 'Content-Type' = 'application/json'
        And request {"description": "Goal To fetch BP results", "entryCriteria": "PCP == true", "episodeId": '#(episodeId)', "etaValue": 6 ,"etaUnit": "MONTHS" , "goalIdU": "Goal_2","name": "Goal_2","lookup" : "Goal2"}
        When method post
        Then status 201
        Then match response.description == "Goal To fetch BP results"
        Then match response.episodeId == episodeId
        Then match response.name == "Goal_2"
        Then match response.goalIdU == "Goal_2"
        Then match response.entryCriteria == "PCP == true"
        Then match response.etaValue == 6
        Then match response.etaUnit == "MONTHS"
        Then def goalId2 = response.id

        Given url baseUrl
        And path '/definitions/goal-associates'
        And header Authorization = 'Bearer ' + accessToken
        And header 'Content-Type' = 'application/json'
        And request {"associateEvent": "COMPLETE", "associateOn": '#(goalId)', "goalId": '#(goalId2)'}
        When method post
        Then status 201
        Then match response.associateEvent == "COMPLETE"
        Then match response.associateOn == goalId
        Then match response.goalId == goalId2
        Then def goalAssociateId = get response.id

        # Get created goal associate
        Given url baseUrl
        And path '/definitions/goal-associates/' + goalAssociateId
        And header Authorization = 'Bearer ' + accessToken
        When method get
        Then status 200
        Then match response.associateEvent == "COMPLETE"
        Then match response.associateOn == goalId
        Then match response.goalId == goalId2
        Then match response.id == goalAssociateId


        # Get all goal associates
        Given url baseUrl
        And path '/definitions/goal-associates'
        And header Authorization = 'Bearer ' + accessToken
        When method get
        Then status 200
        Then match response[0].associateEvent == "COMPLETE"
        Then match response[0].associateOn == goalId
        Then match response[0].goalId == goalId2
        Then match response[0].id == goalAssociateId
        Then match header X-Total-Count == '1'

    Scenario: Test creation of another goal associate resource with invalid goal associate event should fail with appropriate error
        Given url baseUrl
        And path '/definitions/goals'
        And header Authorization = 'Bearer ' + accessToken
        When method get
        Then status 200
        Then def goalId = get response[0].id
        Then def goalId2 = get response[1].id

        Given url baseUrl
        And path '/definitions/goal-associates'
        And header Authorization = 'Bearer ' + accessToken
        And header 'Content-Type' = 'application/json'
        And request {"associateEvent": '#(goalId)', "goalId": '#(goalId2)'}
        When method post
        Then status 400
        Then match response.detail contains "io.igia.caremanagement.domain.enumeration.CaseExecutionEvent"

    Scenario: Test creation of another goal associate resource without goal associateOn should fail with appropriate error
        Given url baseUrl
        And path '/definitions/goals'
        And header Authorization = 'Bearer ' + accessToken
        When method get
        Then status 200
        Then def goalId = get response[0].id
        Then def goalId2 = get response[1].id

        Given url baseUrl
        And path '/definitions/goal-associates'
        And header Authorization = 'Bearer ' + accessToken
        And header 'Content-Type' = 'application/json'
        And request {"associateEvent": "COMPLETE", "goalId": '#(goalId)'}
        When method post
        Then status 400
        Then match response.fieldErrors[0].field == "associateOn"
        Then match response.fieldErrors[0].message == "NotNull"

    Scenario: Test creation of another goal associate resource with invalid goal associateOn should fail with appropriate error
        Given url baseUrl
        And path '/definitions/goals'
        And header Authorization = 'Bearer ' + accessToken
        When method get
        Then status 200
        Then def goalId = get response[0].id

        Given url baseUrl
        And path '/definitions/goal-associates'
        And header Authorization = 'Bearer ' + accessToken
        And header 'Content-Type' = 'application/json'
        And request {"associateEvent": "COMPLETE", "goalId": '#(goalId)', "associateOn": 123}
        When method post
        Then status 500
        Then match response.detail == "could not execute statement; SQL [n/a]; constraint [fk_goal_associate_on_goal_id]; nested exception is org.hibernate.exception.ConstraintViolationException: could not execute statement"

    Scenario: Test recreating goal associate resource same payload as above
        Given url baseUrl
        And path '/definitions/goals'
        And header Authorization = 'Bearer ' + accessToken
        When method get
        Then status 200
        Then def goalId = get response[0].id
        Then def goalId2 = get response[1].id

        Given url baseUrl
        And path '/definitions/goal-associates'
        And header Authorization = 'Bearer ' + accessToken
        And header 'Content-Type' = 'application/json'
        And request {"associateEvent": "COMPLETE", "associateOn": '#(goalId)', "goalId": '#(goalId2)'}
        When method post
        Then status 500
        Then match response.detail == "could not execute statement; SQL [n/a]; constraint [unique_associate_on_goal_associate]; nested exception is org.hibernate.exception.ConstraintViolationException: could not execute statement"

    Scenario:Test creating goal associate resource without goalId should fail with appropriate error
        Given url baseUrl
        And path '/definitions/goals'
        And header Authorization = 'Bearer ' + accessToken
        When method get
        Then status 200
        Then def goalId = get response[1].id

        Given url baseUrl
        And path '/definitions/goal-associates'
        And header Authorization = 'Bearer ' + accessToken
        And header 'Content-Type' = 'application/json'
        And request {"associateEvent": "COMPLETE", "associateOn": '#(goalId)'}
        When method post
        Then status 400
        Then match response.fieldErrors[0].field == "goalId"
        Then match response.fieldErrors[0].message == "NotNull"

    Scenario:Test creating goal associate resource with invalid goalId should fail with appropriate error
        Given url baseUrl
        And path '/definitions/goals'
        And header Authorization = 'Bearer ' + accessToken
        When method get
        Then status 200
        Then def goalId = get response[1].id

        Given url baseUrl
        And path '/definitions/goal-associates'
        And header Authorization = 'Bearer ' + accessToken
        And header 'Content-Type' = 'application/json'
        And request {"associateEvent": "COMPLETE", "associateOn": '#(goalId)', "goalId": 1}
        When method post
        Then status 500
        Then match response.detail == "could not execute statement; SQL [n/a]; constraint [fk_goal_associate_goal_id]; nested exception is org.hibernate.exception.ConstraintViolationException: could not execute statement"

    Scenario:Test fetching of goal associates by invalid goal associate id
        Given url baseUrl
        And path '/definitions/goals/1'
        And header Authorization = 'Bearer ' + accessToken
        When method get
        Then status 404

    Scenario:Test Update of goal associates for valid goal id
        #Get the goal created
        Given url baseUrl
        And path '/definitions/goals'
        And header Authorization = 'Bearer ' + accessToken
        When method get
        Then status 200
        Then def goalId = get response[0].id
        Then def goalId2 = get response[1].id

        #Get the goal created
        Given url baseUrl
        And path '/definitions/goal-associates'
        And header Authorization = 'Bearer ' + accessToken
        When method get
        Then status 200
        Then def goalAssociateId = get response[0].id

        Given url baseUrl
        And path '/definitions/goal-associates'
        And header Authorization = 'Bearer ' + accessToken
        And header 'Content-Type' = 'application/json'
        And request {"associateEvent": "CREATE", "associateOn": '#(goalId)', "goalId": '#(goalId2)',"id" : '#(goalAssociateId)'}
        When method put
        Then status 200

        Given url baseUrl
        And path '/definitions/goal-associates/' + goalAssociateId
        And header Authorization = 'Bearer ' + accessToken
        When method get
        Then status 200
        Then match response.associateEvent == "CREATE"
        Then match response.associateOn == '#(goalId)'
        Then match response.goalId == goalId2
        Then match response.id == goalAssociateId
        
    Scenario:Test Update of goal associates for invalid goal id
        #Get the goal created
        Given url baseUrl
        And path '/definitions/goals'
        And header Authorization = 'Bearer ' + accessToken
        When method get
        Then status 200
        Then def goalId = get response[0].id
        Then def goalId2 = get response[1].id

        Given url baseUrl
        And path '/definitions/goal-associates'
        And header Authorization = 'Bearer ' + accessToken
        And header 'Content-Type' = 'application/json'
        And request {"associateEvent": "CREATE", "associateOn": '#(goalId)', "goalId": '#(goalId2)',"id" : 1}
        When method put
        Then status 400
        Then match response.message == "Goal Associate does not exist"
        
    Scenario:Test delete goal associate 
        Given url baseUrl
        And path '/definitions/goal-associates'
        And header Authorization = 'Bearer ' + accessToken
        When method get
        Then status 200
        Then def goalAssociateId = get response[0].id

        Given url baseUrl
        And path '/definitions/goal-associates/' + goalAssociateId
        And header Authorization = 'Bearer ' + accessToken
        When method delete
        Then status 200

        Given url baseUrl
        And path '/definitions/goal-associates'
        And header Authorization = 'Bearer ' + accessToken
        When method get
        Then match response[*].id != goalAssociateId

    Scenario: Delete the rest for the goal associates created 
        Given url baseUrl
        And path '/definitions/goal-associates'
        And header Authorization = 'Bearer ' + accessToken
        When method get
        Then status 200
        Then def goalAssociateIdArray = response
        Then def runDelete = call read('classpath:deleteGoalAssociate.feature') goalAssociateIdArray 

        Given url baseUrl
        And path '/definitions/goals'
        And header Authorization = 'Bearer ' + accessToken
        When method get
        Then status 200
        Then def goalId = get response[0].id
        Then def goalId2 = get response[1].id
        And path '/definitions/goals/' + goalId
        And header Authorization = 'Bearer ' + accessToken
        When method delete
        Then status 200
        And path '/definitions/goals/' + goalId2
        And header Authorization = 'Bearer ' + accessToken
        When method delete
        Then status 200

        Given url baseUrl
        And path '/definitions/episodes'
        And header Authorization = 'Bearer ' + accessToken
        When method get
        Then status 200
        Then def episodeId = get response[0].id
        And path '/definitions/episodes/' + episodeId
        And header Authorization = 'Bearer ' + accessToken
        When method delete
        Then status 200

        Given url baseUrl
        And path '/definitions/programs'
        And header Authorization = 'Bearer ' + accessToken
        When method get
        Then status 200
        Then def programId = get response[0].id
        And path '/definitions/programs/' + programId
        And header Authorization = 'Bearer ' + accessToken
        When method delete
        Then status 200

    Scenario:Test delete goal associates for invalid goals associates id
        Given url baseUrl
        And path '/definitions/goal-associates/1'
        And header Authorization = 'Bearer ' + accessToken
        When method delete
        Then status 500
        Then match response.detail == "No class io.igia.caremanagement.domain.GoalAssociate entity with id 1 exists!"