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

Feature: Test CURD operations on goal resource

    Background:
    * def signIn = call read('classpath:login.feature')
    * def accessToken = signIn.accessToken

    Scenario: Test creation of program resource,episode and then creating a goal
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
        And request {"description": "Goal To fetch BP results", "entryCriteria": "PCP == true", "episodeId": '#(episodeId)', "etaValue": 6,"etaUnit": "MONTHS", "goalIdU": "Goal_1","name": "Goal_1","lookup" : "Goal1"}
        When method post
        Then status 201
        Then match response.description == "Goal To fetch BP results"
        Then match response.episodeId == episodeId
        Then match response.name == "Goal_1"
        Then match response.goalIdU == "Goal_1"
        Then match response.entryCriteria == "PCP == true"
        Then match response.etaValue == 6
        Then match response.etaUnit == "MONTHS"
        Then match response.lookup == "##notnull"
        Then def goalId = response.id

        # Get created goal
        Given url baseUrl
        And path '/definitions/goals/' + goalId
        And header Authorization = 'Bearer ' + accessToken
        When method get
        Then status 200
        Then match response.description == "Goal To fetch BP results"
        Then match response.episodeId == episodeId
        Then match response.name == "Goal_1"
        Then match response.goalIdU == "Goal_1"
        Then match response.entryCriteria == "PCP == true"
        Then match response.etaValue == 6
        Then match response.etaUnit == "MONTHS"
        Then match response.id == goalId

        # Get all goals
        Given url baseUrl
        And path '/definitions/goals'
        And header Authorization = 'Bearer ' + accessToken
        When method get
        Then status 200
        Then match response[0].description == "Goal To fetch BP results"
        Then match response[0].episodeId == episodeId
        Then match response[0].name == "Goal_1"
        Then match response[0].goalIdU == "Goal_1"
        Then match response[0].entryCriteria == "PCP == true"
        Then match response[0].etaValue == 6
        Then match response[0].etaUnit == "MONTHS"
        Then match response[0].id == goalId
        Then match header X-Total-Count == '1'

    Scenario: Test creation of another goal resource without description
        Given url baseUrl
        And path '/definitions/episodes'
        And header Authorization = 'Bearer ' + accessToken
        When method get
        Then status 200
        Then def episodeId = response[0].id

        Given url baseUrl
        And path '/definitions/goals'
        And header Authorization = 'Bearer ' + accessToken
        And header 'Content-Type' = 'application/json'
        And request {"episodeId": '#(episodeId)', "etaValue": 6,"etaUnit": "MONTHS", "goalIdU": "Goal_2","name": "Goal_2","lookup" : "Goal2"}
        When method post
        Then status 201
        Then match response.episodeId == episodeId
        Then match response.name == "Goal_2"
        Then match response.goalIdU == "Goal_2"
        Then match response.etaValue == 6
        Then match response.etaUnit == "MONTHS"
        Then def respId = response.id

        # Get created goal
        Given url baseUrl
        And path '/definitions/goals/' + respId
        And header Authorization = 'Bearer ' + accessToken
        When method get
        Then status 200
        Then match response.episodeId == episodeId
        Then match response.name == "Goal_2"
        Then match response.goalIdU == "Goal_2"
        Then match response.etaValue == 6
        Then match response.etaUnit == "MONTHS"
        Then match response.id == respId

        # Get all goals
        Given url baseUrl
        And path '/definitions/goals'
        And header Authorization = 'Bearer ' + accessToken
        When method get
        Then status 200
        Then match header X-Total-Count == '2'

    Scenario: Test recreating goal resource same payload as above
        Given url baseUrl
        And path '/definitions/episodes'
        And header Authorization = 'Bearer ' + accessToken
        When method get
        Then status 200
        Then def episodeId = response[0].id

        Given url baseUrl
        And path '/definitions/goals'
        And header Authorization = 'Bearer ' + accessToken
        And header 'Content-Type' = 'application/json'
        And request {"episodeId": '#(episodeId)', "etaValue": 6,"etaUnit": "MONTHS", "goalIdU": "Goal_2","name": "Goal_2","lookup" : "Goal2"}
        When method post
        Then status 500
        Then match response.type == "https://www.jhipster.tech/problem/problem-with-message"
        Then match response.title == "Internal Server Error"
        Then match response.detail contains "could not execute statement; SQL [n/a]; constraint [unique_goal]; nested exception is org.hibernate.exception.ConstraintViolationException: could not execute statement"



    Scenario: Test recreating goal resource same payload just different lookup
        Given url baseUrl
        And path '/definitions/episodes'
        And header Authorization = 'Bearer ' + accessToken
        When method get
        Then status 200
        Then def episodeId = response[0].id

        Given url baseUrl
        And path '/definitions/goals'
        And header Authorization = 'Bearer ' + accessToken
        And header 'Content-Type' = 'application/json'
        And request {"episodeId": '#(episodeId)', "etaValue": 6,"etaUnit": "MONTHS", "goalIdU": "Goal_2","name": "Goal_2","lookup" : "Goal8"}
        When method post
        Then status 500
        Then match response.type == "https://www.jhipster.tech/problem/problem-with-message"
        Then match response.title == "Internal Server Error"
        Then match response.detail contains "could not execute statement; SQL [n/a]; constraint [unique_goal]; nested exception is org.hibernate.exception.ConstraintViolationException: could not execute statement"


    Scenario:Test creating goal resource without episodeId should fail with appropriate error
        Given url baseUrl
        And path '/definitions/goals'
        And header Authorization = 'Bearer ' + accessToken
        And header 'Content-Type' = 'application/json'
        And request {"etaValue": 7,"etaUnit": "MONTHS", "goalIdU": "Goal_3","name": "Goal_3","lookup" : "Goal3"}
        When method post
        Then status 400
        Then match response.fieldErrors[0].field == "episodeId"
        Then match response.fieldErrors[0].message == "NotNull"

    Scenario:Test creating goal resource with invalid episodeId should fail with appropriate error
        Given url baseUrl
        And path '/definitions/goals'
        And header Authorization = 'Bearer ' + accessToken
        And header 'Content-Type' = 'application/json'
        And request {"episodeId": 1, "etaValue": 6,"etaUnit": "MONTHS", "goalIdU": "Goal_4","name": "Goal_4","lookup" : "Goal4"}
        When method post
        Then status 500
        Then match response.detail == "could not execute statement; SQL [n/a]; constraint [fk_goal_episode_id]; nested exception is org.hibernate.exception.ConstraintViolationException: could not execute statement"

    Scenario:Test creating goal resource without goalIdU should fail with appropriate error
        Given url baseUrl
        And path '/definitions/episodes'
        And header Authorization = 'Bearer ' + accessToken
        When method get
        Then status 200
        Then def episodeId = response[0].id

        Given url baseUrl
        And path '/definitions/goals'
        And header Authorization = 'Bearer ' + accessToken
        And header 'Content-Type' = 'application/json'
        And request {"episodeId": '#(episodeId)', "etaValue": 6,"etaUnit": "MONTHS","name": "Goal_5","lookup" : "Goal5"}
        When method post
        Then status 400
        Then match response.fieldErrors[0].field == "goalIdU"
        Then match response.fieldErrors[0].message == "NotNull"

    Scenario:Test creating goal resource without name should fail with appropriate error
        Given url baseUrl
        And path '/definitions/episodes'
        And header Authorization = 'Bearer ' + accessToken
        When method get
        Then status 200
        Then def episodeId = response[0].id

        Given url baseUrl
        And path '/definitions/goals'
        And header Authorization = 'Bearer ' + accessToken
        And header 'Content-Type' = 'application/json'
        And request {"episodeId": '#(episodeId)', "etaValue": 6,"etaUnit": "MONTHS","goalIdU": "Goal_4","lookup" : "Goal6"}
        When method post
        Then status 400
        Then match response.fieldErrors[0].field == "name"
        Then match response.fieldErrors[0].message == "NotNull"

    Scenario:Test creating goal resource without etaValue should fail with appropriate error
        Given url baseUrl
        And path '/definitions/episodes'
        And header Authorization = 'Bearer ' + accessToken
        When method get
        Then status 200
        Then def episodeId = response[0].id

        Given url baseUrl
        And path '/definitions/goals'
        And header Authorization = 'Bearer ' + accessToken
        And header 'Content-Type' = 'application/json'
        And request {"episodeId": '#(episodeId)',"name": "Goal_7" ,"etaUnit": "MONTHS","goalIdU": "Goal_7","lookup" : "Goal7"}
        When method post
        Then status 400
        Then match response.fieldErrors[0].field == "etaValue"
        Then match response.fieldErrors[0].message == "NotNull"

    Scenario:Test creating goal resource without etaUnit should fail with appropriate error
        Given url baseUrl
        And path '/definitions/episodes'
        And header Authorization = 'Bearer ' + accessToken
        When method get
        Then status 200
        Then def episodeId = response[0].id

        Given url baseUrl
        And path '/definitions/goals'
        And header Authorization = 'Bearer ' + accessToken
        And header 'Content-Type' = 'application/json'
        And request {"episodeId": '#(episodeId)',"name": "Goal_7" ,"etaValue": 8,"goalIdU": "Goal_7","lookup" : "Goal7"}
        When method post
        Then status 400
        Then match response.fieldErrors[0].field == "etaUnit"
        Then match response.fieldErrors[0].message == "NotNull"

    Scenario:Test fetching of goals by invalid goal id
        Given url baseUrl
        And path '/definitions/goals/1'
        And header Authorization = 'Bearer ' + accessToken
        When method get
        Then status 404

    Scenario:Test Update of goals for valid episode id and valid goal id
        #Get the episode created
        Given url baseUrl
        And path '/definitions/episodes'
        And header Authorization = 'Bearer ' + accessToken
        When method get
        Then status 200
        Then def episodeId = get response[0].id

        #Get the goal created
        Given url baseUrl
        And path '/definitions/goals'
        And header Authorization = 'Bearer ' + accessToken
        When method get
        Then status 200
        Then def goalId = get response[1].id

        Given url baseUrl
        And path '/definitions/goals'
        And header Authorization = 'Bearer ' + accessToken
        And header 'Content-Type' = 'application/json'
        And request
            """
            {
            "description": "Goal To fetch BP values", 
            "entryCriteria": "PCP == false", 
            "episodeId": '#(episodeId)',
            "etaValue": 9, 
            "etaUnit": "MONTHS", 
            "goalIdU": "Goal_11",
            "name": "Goal_11", 
            "lookup" : "Goal_11",
            "id" : '#(goalId)'
            }
            """
        When method put
        Then status 200

        Given url baseUrl
        And path '/definitions/goals/' + goalId
        And header Authorization = 'Bearer ' + accessToken
        When method get
        Then status 200
        Then match response.description == "Goal To fetch BP values"
        Then match response.episodeId == episodeId
        Then match response.name == "Goal_11"
        Then match response.entryCriteria == "PCP == false"
        Then match response.etaValue == 9
        Then match response.etaUnit == "MONTHS"
        Then match response.lookup == "##notnull"
        Then match response.id == goalId
        Then match response.goalIdU == "Goal_11"
    
    Scenario:Test Update of goals for valid episode id and invalid goal id
        #Get the episode created
        Given url baseUrl
        And path '/definitions/episodes'
        And header Authorization = 'Bearer ' + accessToken
        When method get
        Then status 200
        Then def episodeId = get response[0].id

        Given url baseUrl
        And path '/definitions/goals'
        And header Authorization = 'Bearer ' + accessToken
        And header 'Content-Type' = 'application/json'
        And request
            """
            {
            "description": "Goal To fetch BP values", 
            "entryCriteria": "PCP == false", 
            "episodeId": '#(episodeId)',
            "etaValue": 9, 
            "etaUnit": "MONTHS", 
            "goalIdU": "Goal_11",
            "name": "Goal_11", 
            "lookup" : "Goal_11",
            "id" : 1
            }
            """
        When method put
        Then status 400
        Then match response.message == "Goal does not exist"
    
    Scenario:Test delete goals 
        Given url baseUrl
        And path '/definitions/goals'
        And header Authorization = 'Bearer ' + accessToken
        When method get
        Then status 200
        Then def goalId = get response[0].id

        Given url baseUrl
        And path '/definitions/goals/' + goalId
        And header Authorization = 'Bearer ' + accessToken
        When method delete
        Then status 200

        Given url baseUrl
        And path '/definitions/goals'
        And header Authorization = 'Bearer ' + accessToken
        When method get
        Then match response[*].id != goalId

    Scenario: Delete the rest for the goals created 
        Given url baseUrl
        And path '/definitions/goals'
        And header Authorization = 'Bearer ' + accessToken
        When method get
        Then status 200
        Then def goalIdArray = response
        Then def runDelete = call read('classpath:deleteGoal.feature') goalIdArray 

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

    Scenario:Test delete goals for invalid goals id
        Given url baseUrl
        And path '/definitions/goals/1'
        And header Authorization = 'Bearer ' + accessToken
        When method delete
        Then status 500
        Then match response.detail == "No class io.igia.caremanagement.domain.Goal entity with id 1 exists!"