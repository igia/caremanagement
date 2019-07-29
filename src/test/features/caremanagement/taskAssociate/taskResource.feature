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

Feature: Test CURD operations on task 

    Background:
    * def signIn = call read('classpath:login.feature')
    * def accessToken = signIn.accessToken

    Scenario: Create the tasks entity without assignee should fail with appropriate error
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

        Given url baseUrl
        And path '/definitions/tasks'
        And header Authorization = 'Bearer ' + accessToken
        And header 'Content-Type' = 'application/json'
        And request
            """
            {
            "description": "Task Description",
            "dueDate": "2018-11-12",
            "name": "TaskName1",
            "repeatFrequencyValue": 1,
            "repeatFrequencyUnit": "MONTHS",
            "sla" : 48,
            "taskIdU": "T11",
            "type": "HUMAN",
            "goalId":'#(goalId)',
            "repeatEvent": "CREATE",
            "lookup": "TaskName1",
            "isRepeat": true,
            "typeRef": "string"
            } 
            """
        When method post
        Then status 400
        Then match response.fieldErrors[0].field == "assignee"
        Then match response.fieldErrors[0].message == "NotNull"

    Scenario: Create the tasks entity without taskIdU should fail with appropriate error
        Given url baseUrl
        And path '/definitions/goals'
        And header Authorization = 'Bearer ' + accessToken
        When method get
        Then status 200
        Then def goalId = response[0].id
        Given url baseUrl
        And path '/definitions/tasks'
        And header Authorization = 'Bearer ' + accessToken
        And header 'Content-Type' = 'application/json'
        And request
            """
            {
            "assignee" : "service-account-internal",
            "dueDate": "2018-11-12",
            "name": "TaskName2",
            "repeatFrequencyValue": 1,
            "repeatFrequencyUnit": "MONTHS",
            "sla" : 48,
            "type": "HUMAN",
            "goalId":'#(goalId)',
            "repeatEvent": "CREATE",
            "lookup": "TaskName2",
            "isRepeat": true,
            "typeRef": "string"
            } 
            """
        When method post
        Then status 400
        Then match response.fieldErrors[0].field == "taskIdU"
        Then match response.fieldErrors[0].message == "NotNull"

    Scenario: Create the tasks entity without name should fail with appropriate error
        Given url baseUrl
        And path '/definitions/goals'
        And header Authorization = 'Bearer ' + accessToken
        When method get
        Then status 200
        Then def goalId = response[0].id
        Given url baseUrl
        And path '/definitions/tasks'
        And header Authorization = 'Bearer ' + accessToken
        And header 'Content-Type' = 'application/json'
        And request
            """
            {
            "assignee" : "service-account-internal",
            "dueDate": "2018-11-12",
            "taskIdU": "T18",
            "repeatFrequencyValue": 1,
            "repeatFrequencyUnit": "MONTHS",
            "sla" : 48,
            "type": "HUMAN",
            "goalId":'#(goalId)',
            "repeatEvent": "CREATE",
            "lookup": "TaskName18",
            "isRepeat": true,
            "typeRef": "string"
            } 
            """
        When method post
        Then status 400
        Then match response.fieldErrors[0].field == "name"
        Then match response.fieldErrors[0].message == "NotNull"

    Scenario: Create the tasks entity without goalId should fail with appropriate error
        Given url baseUrl
        And path '/definitions/goals'
        And header Authorization = 'Bearer ' + accessToken
        When method get
        Then status 200
        Then def goalId = response[0].id
        Given url baseUrl
        And path '/definitions/tasks'
        And header Authorization = 'Bearer ' + accessToken
        And header 'Content-Type' = 'application/json'
        And request
            """
            {
            "assignee" : "service-account-internal",
            "dueDate": "2018-11-12",
            "taskIdU": "T21",
            "repeatFrequencyValue": 1,
            "repeatFrequencyUnit": "MONTHS",
            "sla" : 48,
            "type": "HUMAN",
            "name":'TaskNew1',
            "repeatEvent": "CREATE",
            "lookup": "TaskName21",
            "isRepeat": true,
            "typeRef": "string"
            } 
            """
        When method post
        Then status 400
        Then match response.fieldErrors[0].field == "goalId"
        Then match response.fieldErrors[0].message == "NotNull"

    Scenario: Create the tasks entity without task type should fail with appropriate error
        Given url baseUrl
        And path '/definitions/goals'
        And header Authorization = 'Bearer ' + accessToken
        When method get
        Then status 200
        Then def goalId = response[0].id
        Given url baseUrl
        And path '/definitions/tasks'
        And header Authorization = 'Bearer ' + accessToken
        And header 'Content-Type' = 'application/json'
        And request
            """
            {
            "assignee" : "service-account-internal",
            "dueDate": "2018-11-12",
            "name": "TaskName3",
            "repeatFrequencyValue": 1,
            "repeatFrequencyUnit": "MONTHS",
            "sla" : 48,
            "taskIdU": "T12",
            "goalId":'#(goalId)',
            "repeatEvent": "CREATE",
            "lookup": "TaskName3",
            "isRepeat": true,
            "typeRef": "string"
            } 
            """
        When method post
        Then status 400
        Then match response.fieldErrors[0].field == "type"
        Then match response.fieldErrors[0].message == "NotNull"

    Scenario: Create the tasks entity without task ref should not fail
        Given url baseUrl
        And path '/definitions/goals'
        And header Authorization = 'Bearer ' + accessToken
        When method get
        Then status 200
        Then def goalId = response[0].id
        Given url baseUrl
        And path '/definitions/tasks'
        And header Authorization = 'Bearer ' + accessToken
        And header 'Content-Type' = 'application/json'
        And request
            """
            {
            "assignee" : "service-account-internal",
            "dueDate": "2018-11-12",
            "name": "TaskName19",
            "type" : "HUMAN",
            "repeatFrequencyValue": 1,
            "repeatFrequencyUnit": "MONTHS",
            "sla" : 48,
            "taskIdU": "T19",
            "goalId":'#(goalId)',
            "repeatEvent": "CREATE",
            "lookup": "TaskName19",
            "isRepeat": true
            } 
            """
        When method post
        Then status 201

    Scenario: Create the tasks entity
        Given url baseUrl
        And path '/definitions/goals'
        And header Authorization = 'Bearer ' + accessToken
        When method get
        Then status 200
        Then def goalId = response[0].id 
        Given url baseUrl
        And path '/definitions/tasks'
        And header Authorization = 'Bearer ' + accessToken
        And header 'Content-Type' = 'application/json'
        And request
            """
            {
            "assignee" : "service-account-internal",
            "dueDate": "2018-11-12",
            "name": "TaskName4",
            "repeatFrequencyValue": 1,
            "repeatFrequencyUnit": "MONTHS",
            "sla" : 48,
            "taskIdU": "T13",
            "type": "HUMAN",
            "goalId":'#(goalId)',
            "repeatEvent": "CREATE",
            "lookup": "TaskName4",
            "isRepeat": true,
            "typeRef": "string"
            } 
            """
        When method post
        Then status 201


    Scenario: Create the duplicate task entity must fail with 500
        Given url baseUrl
        And path '/definitions/goals'
        And header Authorization = 'Bearer ' + accessToken
        When method get
        Then status 200
        Then def goalId = response[0].id 
        Given url baseUrl
        And path '/definitions/tasks'
        And header Authorization = 'Bearer ' + accessToken
        And header 'Content-Type' = 'application/json'
        And request
            """
            {
            "assignee" : "service-account-internal",
            "dueDate": "2018-11-12",
            "name": "TaskName4",
            "repeatFrequencyValue": 1,
            "repeatFrequencyUnit": "MONTHS",
            "repeatType": "repeatType",
            "sla" : 48,
            "taskIdU": "T13",
            "type": "HUMAN",
            "goalId":'#(goalId)',
            "repeatEvent": "CREATE",
            "lookup": "TaskName4",
            "isRepeat": true,
            "typeRef": "string"
            } 
            """
        When method post
        Then status 500
        Then match response.type == "https://www.jhipster.tech/problem/problem-with-message"
        Then match response.title == "Internal Server Error"
        Then match response.detail contains "could not execute statement; SQL [n/a]; constraint [unique_task]; nested exception is org.hibernate.exception.ConstraintViolationException: could not execute statement"


    Scenario: Create the task with invalid type
        Given url baseUrl
        And path '/definitions/tasks'
        And header Authorization = 'Bearer ' + accessToken
        And header 'Content-Type' = 'application/json'
        And request
            """
            {
            "assignee" : "service-account-internal",
            "dueDate": "2018-11-12",
            "name": "TaskName5",
            "repeatFrequencyValue": 1,
            "repeatFrequencyUnit": "MONTHS",
            "sla" : 48,
            "taskIdU": "T14",
            "type": "NONEHUMAN",
            "goalId":'#(goalId)',
            "repeatEvent": "CREATE",
            "lookup": "TaskName5",
            "isRepeat": true,
            "typeRef": "string"
            } 
            """
        When method post
        Then status 400
        Then match response.detail contains "JSON parse error: Cannot deserialize value of type `io.igia.caremanagement.domain.enumeration.Type` from String \"NONEHUMAN\": value not one of declared Enum instance names: [DECISION, HUMAN]"

    Scenario: Create the task with invalid due date format
        Given url baseUrl
        And path '/definitions/tasks'
        And header Authorization = 'Bearer ' + accessToken
        And header 'Content-Type' = 'application/json'
        And request
            """
            {
            "assignee" : "service-account-internal",
            "dueDate": "2018-11-12 00:00:00",
            "name": "TaskName4",
            "repeatFrequencyValue": 1,
            "repeatFrequencyUnit": "MONTHS",
            "sla" : 48,
            "taskIdU": "T13",
            "type": "HUMAN",
            "goalId":'#(goalId)',
            "repeatEvent": "CREATE",
            "lookup": "TaskName4",
            "isRepeat": true,
            "typeRef": "string"
            } 
            """
        When method post
        Then status 400
        Then match response.detail contains "SON parse error: Cannot deserialize value of type `java.time.LocalDate` from String \"2018-11-12 00:00:00\": Failed to deserialize java.time.LocalDate: (java.time.format.DateTimeParseException) Text '2018-11-12 00:00:00' could not be parsed, unparsed text found at index 10"

    Scenario: Test the get for invalid task id
        Given url baseUrl
        And path '/definitions/tasks/1'
        And header Authorization = 'Bearer ' + accessToken
        And header 'Content-Type' = 'application/json'
        Then method get
        Then status 404

    Scenario: Verify if the task entities are correctly deleted
        Given url baseUrl
        And path '/definitions/tasks'
        And header Authorization = 'Bearer ' + accessToken
        And header 'Content-Type' = 'application/json'
        Then method get
        Then status 200
        Then def created = response
        Then def funDelete = call read('classpath:deleteTask.feature') created

        Given url baseUrl
        And path '/definitions/goals'
        And header Authorization = 'Bearer ' + accessToken
        And header 'Content-Type' = 'application/json'
        Then method get
        Then status 200
        Then def created1 = response
        Then def funDelete1 = call read('classpath:deleteGoal.feature') created1

        Given url baseUrl
        And path '/definitions/episodes'
        And header Authorization = 'Bearer ' + accessToken
        And header 'Content-Type' = 'application/json'
        Then method get
        Then status 200
        Then def created2 = response
        Then def funDelete2 = call read('classpath:deleteEpisode.feature') created2

        Given url baseUrl
        And path '/definitions/programs'
        And header Authorization = 'Bearer ' + accessToken
        And header 'Content-Type' = 'application/json'
        Then method get
        Then status 200
        Then def created3 = response
        Then def funDelete3 = call read('classpath:deleteProgram.feature') created3
