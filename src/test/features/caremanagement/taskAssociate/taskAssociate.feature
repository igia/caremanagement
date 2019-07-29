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

Feature: Test CURD operations on task associate

    Background:
    * def signIn = call read('classpath:login.feature')
    * def accessToken = signIn.accessToken

    Scenario: Test creation of task associate entity
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
            "assignee": "service-account-internal",
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
        Then status 201
        Then def taskId = response.id

        Given url baseUrl
        And path '/definitions/tasks'
        And header Authorization = 'Bearer ' + accessToken
        And header 'Content-Type' = 'application/json'
        And request
            """
            {
            "assignee": "service-account-internal",
            "description": "Task Description2",
            "dueDate": "2018-11-12",
            "name": "TaskName2",
            "repeatFrequencyValue": 1,
            "repeatFrequencyUnit": "MONTHS",
            "sla" : 48,
            "taskIdU": "T12",
            "type": "HUMAN",
            "goalId":'#(goalId)',
            "repeatEvent": "CREATE",
            "lookup": "TaskName2",
            "isRepeat": true,
            "typeRef": "string"
            } 
            """
        When method post
        Then status 201
        Then def taskId2 = response.id

        # Create task associate with taskId created above
        Given url baseUrl
        And path '/definitions/task-associates'
        And header Authorization = 'Bearer ' + accessToken
        And request {"associateEvent": "COMPLETE", "associateOn": '#(taskId)',"taskId": '#(taskId2)'}
        When method post
        Then status 201
        Then match response.associateEvent == "COMPLETE"
        Then match response.associateOn == taskId
        Then match response.taskId == taskId2
        Then def taskAssociateId = response.id

        # Get all task associate
        Given url baseUrl
        And path '/definitions/task-associates'
        And header Authorization = 'Bearer ' + accessToken
        When method get
        Then status 200
        Then match response[0].associateEvent == "COMPLETE"
        Then match response[0].associateOn == taskId
        Then match response[0].taskId == taskId2
        Then match header X-Total-Count == '1'

        # Get the task associated created above
        Given url baseUrl
        And path '/definitions/task-associates/' + taskAssociateId
        And header Authorization = 'Bearer ' + accessToken
        When method get
        Then status 200
        Then match response.associateEvent == "COMPLETE"
        Then match response.associateOn == taskId
        Then match response.taskId == taskId2
        Then match response.id == taskAssociateId


    Scenario: Test creation of another task associate without associateOn should fail with appropriate error
        Given url baseUrl
        And path '/definitions/tasks'
        And header Authorization = 'Bearer ' + accessToken
        When method get
        Then status 200
        Then def taskId = get response[0].id

        # Get created task associate without associateEvent name
        Given url baseUrl
        And path '/definitions/task-associates'
        And header Authorization = 'Bearer ' + accessToken
        And request {"associateEvent": "COMPLETE","taskId": '#(taskId)'}
        When method post
        Then status 400

    Scenario: Test creation of another task associate with invalid associateOn should fail with appropriate error
        Given url baseUrl
        And path '/definitions/tasks'
        And header Authorization = 'Bearer ' + accessToken
        When method get
        Then status 200
        Then def taskId = get response[0].id

        # Get created task associate without associateEvent name
        Given url baseUrl
        And path '/definitions/task-associates'
        And header Authorization = 'Bearer ' + accessToken
        And request {"associateEvent": "COMPLETE","associateOn": 123 ,"taskId": '#(taskId)'}
        When method post
        Then status 500
        Then match response.detail == "could not execute statement; SQL [n/a]; constraint [fk_task_associate_on_task_id]; nested exception is org.hibernate.exception.ConstraintViolationException: could not execute statement"

    Scenario: Test creation of another task associate with invalid associateEvent should fail with appropriate error
        Given url baseUrl
        And path '/definitions/tasks'
        And header Authorization = 'Bearer ' + accessToken
        When method get
        Then status 200
        Then def taskId = get response[0].id
        Then def taskId2 = get response[1].id

        # Get created task associate without associateEvent name
        Given url baseUrl
        And path '/definitions/task-associates'
        And header Authorization = 'Bearer ' + accessToken
        And request {"associateEvent": "123","associateOn": '#(taskId)' ,"taskId": '#(taskId2)'}
        When method post
        Then status 400

    Scenario: Test recreating task associate resource same payload as above
        Given url baseUrl
        And path '/definitions/tasks'
        And header Authorization = 'Bearer ' + accessToken
        When method get
        Then status 200
        Then def taskId = get response[0].id
        Then def taskId2 = get response[1].id

        # Get created task associate without associateEvent name
        Given url baseUrl
        And path '/definitions/task-associates'
        And header Authorization = 'Bearer ' + accessToken
        And request {"associateEvent": "COMPLETE", "associateOn": '#(taskId)' ,"taskId": '#(taskId2)'}
        When method post
        Then status 500
        Then match response.detail == "could not execute statement; SQL [n/a]; constraint [unique_associate_on_task_associate]; nested exception is org.hibernate.exception.ConstraintViolationException: could not execute statement"

    Scenario:Test creating task associate without taskId should fail with appropriate error
        Given url baseUrl
        And path '/definitions/tasks'
        And header Authorization = 'Bearer ' + accessToken
        When method get
        Then status 200
        Then def taskId = get response[1].id

        Given url baseUrl
        And path '/definitions/task-associates'
        And header Authorization = 'Bearer ' + accessToken
        And request {"associateEvent": "COMPLETE", "associateOn": '#(taskId)'}
        When method post
        Then status 400

    Scenario:Test creating task associate with invalid taskId should fail with appropriate error
        Given url baseUrl
        And path '/definitions/tasks'
        And header Authorization = 'Bearer ' + accessToken
        When method get
        Then status 200
        Then def taskId = get response[1].id

        Given url baseUrl
        And path '/definitions/task-associates'
        And header Authorization = 'Bearer ' + accessToken
        And request {"associateEvent": "COMPLETE", "associateOn": '#(taskId)',"taskId": 1}
        When method post
        Then status 500

    Scenario:Test fetching of task associate by invalid task associate id
        Given url baseUrl
        And path '/definitions/task-associates/1'
        And header Authorization = 'Bearer ' + accessToken
        When method get
        Then status 404

    Scenario:Test Update of task association for valid task associate id
        Given url baseUrl
        And path '/definitions/tasks'
        And header Authorization = 'Bearer ' + accessToken
        When method get
        Then status 200
        Then def taskId = get response[0].id
        Then def taskId2 = get response[1].id

        Given url baseUrl
        And path '/definitions/task-associates'
        And header Authorization = 'Bearer ' + accessToken
        When method get
        Then status 200
        Then def taskAssociateId = get response[0].id

        # Get created task associate
        Given url baseUrl
        And path '/definitions/task-associates'
        And header Authorization = 'Bearer ' + accessToken
        And request {"associateEvent": "CREATE", "associateOn": '#(taskId)',"taskId": '#(taskId2)',"id": '#(taskAssociateId)'}
        When method put
        Then status 200
        Then match response.associateEvent == "CREATE"
        Then match response.associateOn == taskId
        Then match response.taskId == taskId2
        Then match response.id == taskAssociateId

        Given url baseUrl
        And path '/definitions/task-associates/'+ taskAssociateId
        And header Authorization = 'Bearer ' + accessToken
        When method get
        Then status 200
        Then match response.associateEvent == "CREATE"
        Then match response.associateOn == taskId
        Then match response.taskId == taskId2
        Then match response.id == taskAssociateId
        
    Scenario:Test Update of form for invalid task associate id
        #Get the form created
        Given url baseUrl
        And path '/definitions/tasks'
        And header Authorization = 'Bearer ' + accessToken
        When method get
        Then status 200
        Then def taskId = get response[0].id
        Then def taskId2 = get response[1].id

        # Get created task associate
        Given url baseUrl
        And path '/definitions/task-associates'
        And header Authorization = 'Bearer ' + accessToken
        And request {"associateEvent": "CREATE", "associateOn": '#(taskId)',"taskId": '#(taskId2)',"id": 1}
        When method put
        Then status 400
        Then match response.message == "Task Associate does not exist"

    Scenario:Test delete task associate  
        Given url baseUrl
        And path '/definitions/task-associates'
        And header Authorization = 'Bearer ' + accessToken
        When method get
        Then status 200
        Then def taskAssociateId = get response[0].id

        Given url baseUrl
        And path '/definitions/task-associates/' + taskAssociateId
        And header Authorization = 'Bearer ' + accessToken
        When method delete
        Then status 200

        Given url baseUrl
        And path '/definitions/task-associates'
        And header Authorization = 'Bearer ' + accessToken
        When method get
        Then match response[*].id != taskAssociateId

    Scenario:Test delete rest task associate
        Given url baseUrl
        And path '/definitions/task-associates'
        And header Authorization = 'Bearer ' + accessToken
        When method get
        Then status 200
        Then def taskAssociate = response
        Then def deleteTaskAssociate = call read('classpath:deleteTaskAssociate.feature') taskAssociate

    Scenario:Test delete rest task
        Given url baseUrl
        And path '/definitions/tasks'
        And header Authorization = 'Bearer ' + accessToken
        When method get
        Then status 200
        Then def tasks = response
        Then def deleteTaskAssociate = call read('classpath:deleteTask.feature') tasks 

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

    Scenario:Test delete form for invalid form id
        Given url baseUrl
        And path '/definitions/task-associates/1'
        And header Authorization = 'Bearer ' + accessToken
        When method delete
        Then status 500

