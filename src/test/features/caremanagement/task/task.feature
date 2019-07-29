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

Feature: Test CURD operations for Task Entity

    Background:
    * def signIn = call read('classpath:login.feature')
    * def accessToken = signIn.accessToken



    # Test the rest of the operations for tasks entity
    Scenario: Create the tasks entity successfully
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
        Then table tasks
            | assignee                   | description        | dueDate      | name        | repeatFrequencyValue | taskIdU | taskType | typeRef  | repeatFrequencyUnit | sla | repeatEvent | lookup |
            | "service-account-internal" | "Task description" | "2018-11-12" | "TaskName1" | 1                    | "T13"   | "HUMAN"  | "string" | "MONTHS"            | 48  | "CREATE"    | "T3"   |
            | "service-account-internal" | "Task description" | "2018-11-12" | "TaskName2" | 1                    | "T12"   | "HUMAN"  | "string" | "MONTHS"            | 48  | "CREATE"    | "T2"   |
            | "service-account-internal" | "Task description" | "2018-11-12" | "TaskName3" | 1                    | "T14"   | "HUMAN"  | "string" | "MONTHS"            | 48  | "CREATE"    | "T4"   |
            | "service-account-internal" | "Task description" | "2018-11-12" | "TaskName4" | 1                    | "T15"   | "HUMAN"  | "string" | "MONTHS"            | 48  | "CREATE"    | "T5"   |
            | "service-account-internal" | "Task description" | "2018-11-12" | "TaskName5" | 6                    | "T16"   | "HUMAN"  | "string" | "MONTHS"            | 48  | "CREATE"    | "T6"   |
            * def result = call read('classpath:create-task.feature') tasks
            * def created = $result[*].response
            * match created[*].taskIdU contains only ['T13','T12','T14','T15','T16']

    Scenario: Verify all the created task entities in the above step are fetched correctly in get
        Given url baseUrl
        And path '/definitions/tasks'
        And header Authorization = 'Bearer ' + accessToken
        And header 'Content-Type' = 'application/json'
        Then method get
        Then status 200
        Then def created = response
        Then match created[*].taskIdU contains only ['T13','T12','T14','T15','T16']
        Then match each created[*].assignee == "service-account-internal"
        Then match each created[*].description == "Task description"
        Then match each created[*].dueDate ==  "2018-11-12"
        Then match created[*].name == ['TaskName1','TaskName2','TaskName3','TaskName4','TaskName5']
        Then match each created[*].repeatFrequencyUnit == "#present"
        Then match each created[*].repeatFrequencyValue == "#present"
        Then match each created[*].repeatEvent == "CREATE"
        Then match each created[*].taskType == "HUMAN"
        Then match each created[*].typeRef == "#present"
        Then match each created[*].sla == 48
        Then match each created[*].isRepeat == "#present"
        Then def taskIdArray = get[0] created[?(@.taskIdU == "T12")]
        Then def taskId = taskIdArray.id

        Given url baseUrl
        And path '/definitions/tasks/' + taskId
        And header Authorization = 'Bearer ' + accessToken
        And header 'Content-Type' = 'application/json'
        Then method get
        Then status 200
        Then match response.taskIdU == "T12"

    Scenario: Update task entity with entry criteria
        Given url baseUrl
        And path '/definitions/tasks'
        And header Authorization = 'Bearer ' + accessToken
        And header 'Content-Type' = 'application/json'
        Then method get
        Then status 200
        Then def taskIdArray = get[0] response[?(@.taskIdU == "T12")]
        Then def taskId = get taskIdArray.id

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
            "assignee": "service-account-internal",
            "description": "Task Description",
            "dueDate": "2018-11-12",
            "name": "TaskName8",
            "repeatFrequencyValue": 1,
            "repeatFrequencyUnit": "MONTHS",
            "sla" : 48,
            "taskIdU": "T12",
            "type": "HUMAN",
            "goalId":'#(goalId)',
            "repeatEvent": "CREATE",
            "lookup": "TaskName1",
            "isRepeat": true,
            "typeRef": "string",
            "id": "#(taskId)",
            "entryCriteria" : "PCP == true"
            } 
            """
        When method put
        Then status 200
        Then match response.entryCriteria == "PCP == true"
        
    Scenario: Update task entity against invalid Id

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
            "assignee": "service-account-internal",
            "description": "Task Description",
            "dueDate": "2018-11-12",
            "name": "TaskName118",
            "repeatFrequencyValue": 1,
            "repeatFrequencyUnit": "MONTHS",
            "sla" : 48,
            "taskIdU": "T12",
            "type": "HUMAN",
            "goalId":'#(goalId)',
            "repeatEvent": "CREATE",
            "lookup": "TaskName1111",
            "isRepeat": true,
            "typeRef": "string",
            "id": 1,
            "entryCriteria" : "PCP == true"
            } 
            """
        When method put
        Then status 400
        Then match response.message == "Task does not exist"

    # Testing of story HIPLATFORM-12
    Scenario: Create case instance
        * def createCaseInstance = call read('classpath:camundaCreateCaseInstance.feature')

    Scenario: Test that if there are no missed tasks returns empty array
        Given url baseUrl
        And path '/tasks'
        And header Authorization = 'Bearer ' + accessToken
        And header 'Content-Type' = 'application/json'
        And request {"taskCategory": "MISSED","firstResult": "0"}
        When method post
        Then status 200
        And match $ == []

    Scenario: Test that if there are no upcoming tasks returns empty array
        Given url baseUrl
        And path '/tasks'
        And header Authorization = 'Bearer ' + accessToken
        And header 'Content-Type' = 'application/json'
        And request {"taskCategory": "UPCOMING","firstResult": "0"}
        When method post
        Then status 200
        And match $ == []

    Scenario: Verify if task with no request parameters is completed successfully

        # Fetch task id
        Given url camundaUrl
        And path '/task'
        And header Authorization = 'Bearer ' + accessToken
        When method get
        Then status 200
        * def task_id = get response[0].id


        # Complete a task for with no request parameters with fetched task id
        Given url baseUrl
        And def taskId = task_id
        And path '/tasks/'+ task_id +'/complete'
        And header Authorization = 'Bearer ' + accessToken
        And header 'Content-Type' = 'application/json'
        And request {}
        When method post
        Then status 200

        # Check if the task is not present in available list
        Given url camundaUrl
        And def pastTaskId = task_id
        And path '/task'
        And header Authorization = 'Bearer ' + accessToken
        When method get
        Then status 200
        Then def req = get[0] response[?(@.id == '"+ pastTaskId +"')]
        Then match req == []


    Scenario: Verify if we complete a task for invalid task id, api fails with 500 error code and appropriate error message
        Given url baseUrl
        And path '/tasks/1/complete'
        And header Authorization = 'Bearer ' + accessToken
        And header 'Content-Type' = 'application/json'
        And request {}
        When method post
        Then status 500
        Then match response == 
            """
            {
            "timestamp": "#string",
            "status": 500,
            "error": "Internal Server Error",
            "message": "#present",
            "path": "/api/tasks/1/complete"
            }		
            """
        Then match response.message contains 'Cannot find task with id 1'


    Scenario: Check if there are Todo task available, Also check due date is today's date
        Given url baseUrl
        And path '/tasks'
        And header Authorization = 'Bearer ' + accessToken
        And header 'Content-Type' = 'application/json'
        And request {"taskCategory": "TODO","firstResult": "0","maxResults": "5"}
        When method post
        Then status 200
        Then match $ != []
        Then def taskResponse = response
        Then match response[*].id == "#present"
        Then match response[*].name == "#present"
        Then match response[*].assignee == "##notnull"
        Then match response[*].assignee == "##notnull"
        Then match response[*].taskDefinitionKey == "#present"
        Then match response[*].caseInstanceId == "#present"
        Then match response[*].due == "#present"
        Then match response[*].priority == "#present"
        #Then match response[*].priority == "#integer"
        #Then match response[*].suspended == false

        Then def verifyTask = call read('classpath:verifyTodoTasks.feature') taskResponse

        # Fetch the today's date in yyyy-MM-dd format
        Then def getDate =
            """
            function() {
            var SimpleDateFormat = Java.type('java.text.SimpleDateFormat');
            var sdf = new SimpleDateFormat('yyyy-MM-dd');
            var date = new java.util.Date();
            return sdf.format(date);
            }
            """
        Then def today = getDate()
        Then def dueDate = response[0].due

        # Format the due date returned
        Then def formatDate = 
            """
            function(due) {
            var SimpleDateFormat = Java.type('java.text.SimpleDateFormat');
            var sdf = new SimpleDateFormat('yyyy-MM-dd');
            var dueDate = sdf.parse(due);
            return sdf.format(dueDate);
            }
            """
        Then def formattedDueDate = formatDate(dueDate)
        #Check the due date with today's date
        Then match formattedDueDate == today


    Scenario: Test missed tasks
        # create a task with past due date
        Given url camundaUrl
        And path '/task/create'
        And header Authorization = 'Bearer ' + accessToken
        And header 'Content-Type' = 'application/json'
        And request {"name": "New Task","description": "Testing missed tasks","assignee": "service-account-internal","due": "2017-10-22T11:11:15.372+0530","mrn": "mrn004"}
        When method post
        Then status 204

        # Check if there are missed task available
        Given url baseUrl
        And path '/tasks'
        And header Authorization = 'Bearer ' + accessToken
        And header 'Content-Type' = 'application/json'
        And request {"taskCategory": "MISSED","firstResult": "0","maxResults": "5"}
        When method post
        Then status 200
        Then match response[*] contains 
            """
            { 
            "id" : "#present",
            "name" : "New Task",
            "assignee" : "##notnull",
            "created" : "#present",
            "due" : "#present",
            "description": "Testing missed tasks",
            "priority" : "#present",
            "suspended" : "#present"
            }
            """
        Then def dueDate = response[0].due
        And match dueDate contains '2017-10-22'
        And def missedTaskId = $[0].id

        # Complete the task created
        Given url baseUrl
        And path '/tasks/'+ missedTaskId +'/complete'
        And header Authorization = 'Bearer ' + accessToken
        And header 'Content-Type' = 'application/json'
        And request {}
        When method post
        Then status 200

    Scenario: Verify if no parameters are passed to POST /api/tasks api fails with 400
        Given url baseUrl
        And path '/tasks'
        And header Authorization = 'Bearer ' + accessToken
        And header 'Content-Type' = 'application/json'
        And request {}
        When method post
        Then status 400
        Then match response.message == "Invalid TaskCategory {}"

    Scenario: Verify if invalid task category is passed to POST /api/tasks as others task fails with appropriate error and response status 400
        Given url baseUrl
        And path '/tasks'
        And header Authorization = 'Bearer ' + accessToken
        And header 'Content-Type' = 'application/json'
        And request {"taskCategory": "OTHERS", "firstResult": "0", "maxResults": "5"}
        When method post
        Then status 400
        Then match response contains 
            """
            {
            "timestamp": "#present",
            "status": 400,
            "error": "Bad Request",
            "message": "#string",
            "path": "/api/tasks"
            }
            """
        And match response.message contains "Cannot deserialize value of type `io.igia.caremanagement.domain.enumeration.TaskCategory` from String \"OTHERS\": value not one of declared Enum instance names: [AVAILABLE, MISSED, UPCOMING, TODO, HISTORY]"

    Scenario: Verify the available tasks
        Given url baseUrl
        And path '/tasks'
        And header Authorization = 'Bearer ' + accessToken
        And header 'Content-Type' = 'application/json'
        And request {"mrn": "mrn004","taskCategory": "AVAILABLE"}
        When method post
        Then status 200
        Then match each response[*] contains
            """
            {
            "id": "#present",
            "name": "#present",
            "caseInstanceId": "#string",
            "goalName": "#string",
            "goalId": "#string",
            "episodeName": "#string",
            "episodeId": "#string",
            "activityId": "#present",
            "priority":"#present",
            "suspended":"#present",
            "suspended": false
            }
            """
        Then def taskId = get response[0].id
        Given url camundaUrl
        And path '/task'
        And header Authorization = 'Bearer ' + accessToken
        And header 'Content-Type' = 'application/json'
        When method get
        Then status 200
        Then match response[*].id != taskId






    # Testing of story HIPLATFORM-162

    Scenario: Verify the creation of the task for the past due date with parent as task from current instance
        # Create a new task with parent as one of the existing task
        Given url baseUrl
        And path '/tasks'
        And header Authorization = 'Bearer ' + accessToken
        And header 'Content-Type' = 'application/json'
        And request {"taskCategory": "TODO","firstResult": "0","maxResults": "5"}
        When method post
        Then status 200
        Then def taskId = get response[0].id

        Given url baseUrl
        And path '/tasks/create'
        And def tasksId = get taskId
        And header Authorization = 'Bearer ' + accessToken
        And header 'Content-Type' = 'application/json'
        And request
            """
            {
            "name": "New Task",
            "description": "Testing missed tasks",
            "assignee": "service-account-internal",
            "due": "2017-10-22T11:11:15.372+0530",
            "mrn": "mrn004",
            "parentTaskId": "#(tasksId)"
            }
            """
        When method post
        Then status 200

    Scenario: Check above created task in missed tasks list and complete the task
        Given url baseUrl
        And path '/tasks'
        And header Authorization = 'Bearer ' + accessToken
        And header 'Content-Type' = 'application/json'
        And request {"taskCategory": "MISSED","firstResult": "0", "maxResults": "5"}
        When method post
        Then status 200
        Then def req = get[0] response[?(@.name=="New Task")]
        Then match req.name == "New Task"
        Then match req.due contains "2017-10-22"
        Then match req.description == "Testing missed tasks"
        Then def newTaskId = req.id

        # Complete the task created
        Given url baseUrl
        And path '/tasks/'+ newTaskId +'/complete'
        And header Authorization = 'Bearer ' + accessToken
        And header 'Content-Type' = 'application/json'
        And request {}
        When method post
        Then status 200

    Scenario: Verify the creation of the task for the future due date with parent task id null
        # Create a new task with parent as one of the existing task
        Given url baseUrl
        And path '/tasks/create'
        And header Authorization = 'Bearer ' + accessToken
        And header 'Content-Type' = 'application/json'
        And request
            """
            {
            "name": "Task with parent null",
            "description": "Testing",
            "assignee": "service-account-internal",
            "due": "2025-10-22T11:11:15.372+0530",
            "mrn": "mrn004",
            }
            """
        When method post
        Then status 200

        # Check in upcoming tasks list
        Given url baseUrl
        And path '/tasks'
        And header Authorization = 'Bearer ' + accessToken
        And header 'Content-Type' = 'application/json'
        And request {"taskCategory": "UPCOMING","firstResult": "0"}
        When method post
        Then status 200
        Then def req = get[0] response[?(@.name == "Task with parent null")]
        Then match req.name == "Task with parent null"
        Then match req.due contains "2025-10-22"
        Then match req.description == "Testing"
        Then def newTaskId = get req.id
        Then match newTaskId != "#null"
        Then match req.caseInstanceId == "#present"
        Then match req.due == "#present"

        # Complete the task created
        Given url baseUrl
        And path '/tasks/'+ newTaskId +'/complete'
        And header Authorization = 'Bearer ' + accessToken
        And header 'Content-Type' = 'application/json'
        And request {}
        When method post
        Then status 200

    Scenario: Verify the creation of the task without mrn value should fail with appropriate error 
        Given url baseUrl
        And path '/tasks/create'
        And header Authorization = 'Bearer ' + accessToken
        And header 'Content-Type' = 'application/json'
        And request
            """
            {
            "name": "New Task without mrn",
            "description": "Testing missed tasks",
            "assignee": "service-account-internal",
            "due": "2017-10-22T11:11:15.372+0530"
            }
            """
        When method post
        Then status 400

    Scenario: Verify the creation of the task without assignee value should assign default logged in user as assignee else fails
        Given url baseUrl
        And path '/tasks/create'
        And header Authorization = 'Bearer ' + accessToken
        And header 'Content-Type' = 'application/json'
        And request
            """
            {
            "name": "Task without assignee",
            "description": "test",
            "due": "2025-11-22T11:11:15.372+0530",
            "mrn" : "mrn004"
            }
            """
        When method post
        Then status 200
        # Fetch the assignee     
        Given url camundaUrl
        And path '/task'
        And header Authorization = 'Bearer ' + accessToken
        When method get
        Then status 200
        Then def req = get[0] response[?(@.name == "Task without assignee")]
        Then match req.assignee == "##notnull"

    Scenario: Verify the creation of the task without task name should fail with appropriate error message
        Given url baseUrl
        And path '/tasks/create'
        And header Authorization = 'Bearer ' + accessToken
        And header 'Content-Type' = 'application/json'
        And request
            """
            {
            "description": "test",
            "assignee": "service-account-internal",
            "due": "2025-11-22T11:11:15.372+0530",
            "mrn" : "mrn004"
            }
            """
        When method post
        Then status 400

    Scenario: Verify the creation of the task should fail with 500 error code for incorrect parent task id
        Given url baseUrl
        And path '/tasks/create'
        And header Authorization = 'Bearer ' + accessToken
        And header 'Content-Type' = 'application/json'
        And request
            """
            {
            "name": "Task without assignee",
            "description": "test",
            "assignee": "service-account-internal",
            "due": "2025-11-22T11:11:15.372+0530",
            "mrn" : "mrn004",
            "parentTaskId" : "123"
            }
            """
        When method post
        Then status 500
        Then match response.error == "Internal Server Error"
        Then match response.message contains "No matching task with id 123"
        Then match response.path == "/api/tasks/create"
        Then match response.timestamp == "#present"
        Then match response.status == 500

    Scenario: Verify the reassign api assigns task to new assignee successfully
        # Check in missed tasks list
        Given url camundaUrl
        And path '/task'
        And header Authorization = 'Bearer ' + accessToken
        And header 'Content-Type' = 'application/json'
        When method get
        Then status 200
        Then def mrnArray = get[0] response[?(@.assignee == "service-account-internal")]
        Then def taskId = mrnArray.id
        Then match taskId == "##notnull"
        Given url baseUrl
        And path '/tasks/reassign'
        And header Authorization = 'Bearer ' + accessToken
        And header 'Content-Type' = 'application/json'
        And request
            """
            {
            "taskId" : "#(taskId)",
            "userId" : "demo"
            }
            """
        When method put
        Then status 200

        Given url camundaUrl
        And path '/task'
        And header Authorization = 'Bearer ' + accessToken
        And header 'Content-Type' = 'application/json'
        When method get
        Then status 200
        Then def resp = get[0] response[?(@.assignee == "demo")]
        Then match resp == "##notnull"

    Scenario: Verify the reassign api fails to assign task to new assignee if task id is invalid
        Given url baseUrl
        And path '/tasks/reassign'
        And header Authorization = 'Bearer ' + accessToken
        And header 'Content-Type' = 'application/json'
        And request
            """
            {
            "taskId" : "123",
            "userId" : "demo"
            }
            """
        When method put
        Then status 500
        Then match response.error == "Internal Server Error"
        Then match response.message contains "Cannot find task with id 123: task is null" 
        Then match response.path == "/api/tasks/reassign"
        Then match response.timestamp == "#present"
        Then match response.status == 500

    Scenario: Verify the reassign api fails to assign task to new assignee if user is invalid
        Given url camundaUrl
        And path '/task'
        And header Authorization = 'Bearer ' + accessToken
        And header 'Content-Type' = 'application/json'
        When method get
        Then status 200
        Then def taskId = get response[0].id
        Then match taskId == "##notnull"
        Given url baseUrl
        And path '/tasks/reassign'
        And header Authorization = 'Bearer ' + accessToken
        And header 'Content-Type' = 'application/json'
        And request
            """
            {
            "taskId" : "#(taskId)",
            "userId" : "demo"
            }
            """
        When method put
        Then status 200

    Scenario: Verify the reassign api fails to assign task to new assignee if user is null
        Given url camundaUrl
        And path '/task'
        And header Authorization = 'Bearer ' + accessToken
        And header 'Content-Type' = 'application/json'
        When method get
        Then status 200
        Then def taskId = get response[0].id
        Then match taskId == "##notnull"
        Given url baseUrl
        And path '/tasks/reassign'
        And header Authorization = 'Bearer ' + accessToken
        And header 'Content-Type' = 'application/json'
        And request
            """
            {
            "taskId" : "#(taskId)",
            }
            """
        When method put
        Then status 400
        Then match response.message == "Invalid userId"

    Scenario: Verify the reassign api fails to assign task to new assignee if taskId is null
        Given url camundaUrl
        And path '/task'
        And header Authorization = 'Bearer ' + accessToken
        And header 'Content-Type' = 'application/json'
        When method get
        Then status 200
        Then def taskId = get response[0].id
        Then match taskId == "##notnull"
        Given url baseUrl
        And path '/tasks/reassign'
        And header Authorization = 'Bearer ' + accessToken
        And header 'Content-Type' = 'application/json'
        And request
            """
            {
            "userId" : "demo",
            }
            """
        When method put
        Then status 400
        Then match response.message == "Invalid taskId"
        
    Scenario: Verify the reassign api fails if request is empty
        Given url baseUrl
        And path '/tasks/reassign'
        And header Authorization = 'Bearer ' + accessToken
        And header 'Content-Type' = 'application/json'
        And request
            """
            {
            }
            """
        When method put
        Then status 400

    Scenario: Verify if the /api/tasks/start api activates the available task successfully
        Given url baseUrl
        And path '/tasks'
        And header Authorization = 'Bearer ' + accessToken
        And header 'Content-Type' = 'application/json'
        And request {"mrn": "mrn004","taskCategory": "AVAILABLE"}
        When method post
        Then status 200
        Then def respId = get response[0].id
        Then match respId == "##notnull"
        Then def instId = get response[0].caseInstanceId
        Then match instId == "##notnull"
        Then def name = get response[0].name
        Given url baseUrl
        And path '/tasks/start'
        And header Authorization = 'Bearer ' + accessToken
        And header 'Content-Type' = 'application/json'
        And request
            """
            {
            "taskIdList" : ["#(respId)"],
            "caseInstanceId" : "#(instId)"
            }
            """
        When method put
        Then status 200
        Given url camundaUrl
        And path '/task'
        And header Authorization = 'Bearer ' + accessToken
        And header 'Content-Type' = 'application/json'
        When method get
        Then status 200
        Then match response[*].name contains name

    Scenario: Verify if the /api/tasks/start api fails activation with appropriate error if tasklist is null
        Given url baseUrl
        And path '/tasks'
        And header Authorization = 'Bearer ' + accessToken
        And header 'Content-Type' = 'application/json'
        And request {"mrn": "mrn004","taskCategory": "AVAILABLE"}
        When method post
        Then status 200
        Then def respId = get response[0].id
        Then match respId == "##notnull"
        Then def instId = get response[0].caseInstanceId
        Then match instId == "##notnull"
        Given url baseUrl
        And path '/tasks/start'
        And header Authorization = 'Bearer ' + accessToken
        And header 'Content-Type' = 'application/json'
        And request
            """
            {
            "caseInstanceId" : "#(instId)"
            }
            """
        When method put
        Then status 400
        Then match response.error == "Bad Request"
        Then match response.path == "/api/tasks/start"
        Then match response.timestamp == "#present"
        Then match response.status == 400
        Then match response.message == "Invalid taskList" 

    Scenario: Verify if the /api/tasks/start api fails activation with appropriate error if caseInstanceId is invalid
        Given url baseUrl
        And path '/tasks'
        And header Authorization = 'Bearer ' + accessToken
        And header 'Content-Type' = 'application/json'
        And request {"mrn": "mrn004","taskCategory": "AVAILABLE"}
        When method post
        Then status 200
        Then def respId = get response[0].id
        Then match respId == "##notnull"
        Then def instId = get response[0].caseInstanceId
        Then match instId == "##notnull"
        Given url baseUrl
        And path '/tasks/start'
        And header Authorization = 'Bearer ' + accessToken
        And header 'Content-Type' = 'application/json'
        And request
            """
            {
            "caseInstanceId" : "123",
            "taskIdList" : ["#(respId)"]
            }
            """
        When method put
        Then status 500
        Then match response.error == "Internal Server Error"
        Then match response.message contains "There does not exist any case execution with id: \'123\': caseExecution is null"
        Then match response.path == "/api/tasks/start"
        Then match response.timestamp == "#present"
        Then match response.status == 500

    Scenario: Verify if the /api/tasks/start api fails activation with appropriate error if caseInstanceId is null
        Given url baseUrl
        And path '/tasks'
        And header Authorization = 'Bearer ' + accessToken
        And header 'Content-Type' = 'application/json'
        And request {"mrn": "mrn004","taskCategory": "AVAILABLE"}
        When method post
        Then status 200
        Then def respId = get response[0].id
        Then match respId == "##notnull"
        Then def instId = get response[0].caseInstanceId
        Then match instId == "##notnull"
        Given url baseUrl
        And path '/tasks/start'
        And header Authorization = 'Bearer ' + accessToken
        And header 'Content-Type' = 'application/json'
        And request
            """
            {
            "taskIdList" : ["#(respId)"]
            }
            """
        When method put
        Then status 400
        Then match response.error == "Bad Request"
        Then match response.path == "/api/tasks/start"
        Then match response.timestamp == "#present"
        Then match response.status == 400
        Then match response.message == "Invalid caseInstanceId"

    Scenario: Verify the pagination for historic tasks : maxResults
        Given url baseUrl
        And path '/tasks'
        And header Authorization = 'Bearer ' + accessToken
        And header 'Content-Type' = 'application/json'
        And request {"mrn": "mrn004","taskCategory": "HISTORY"}
        When method post
        Then status 200
        Then def noOfTasks = response.length

        Given url baseUrl
        And path '/tasks'
        And header Authorization = 'Bearer ' + accessToken
        And header 'Content-Type' = 'application/json'
        And request {"mrn": "mrn004","taskCategory": "HISTORY","firstResult": "0","maxResults": "1"}
        When method post
        Then status 200
        Then def noOfTasksNext = response.length
        Then match noOfTasksNext == 1
        Then assert noOfTasksNext != noOfTasks

    Scenario: Verify the pagination for historic tasks : firstResult
        Given url baseUrl
        And path '/tasks'
        And header Authorization = 'Bearer ' + accessToken
        And header 'Content-Type' = 'application/json'
        And request {"taskCategory": "HISTORY"}
        When method post
        Then status 200
        Then def taskId = $[1].id

        Given url baseUrl
        And path '/tasks'
        And header Authorization = 'Bearer ' + accessToken
        And header 'Content-Type' = 'application/json'
        And request {"taskCategory": "HISTORY","firstResult": "1","maxResults": "1"}
        When method post
        Then status 200
        Then def taskIdNew = $[0].id
        Then match taskIdNew == taskId

    Scenario: Complete a task for valid task id and valid form data Also retry with same id should fail with status 500.
        # Fetch task id
        Given url baseUrl
        And path '/tasks'
        And header Authorization = 'Bearer ' + accessToken
        And request {"taskCategory": "TODO"}
        When method post
        Then status 200
        Then def task_id = get response[0].id

        Given url baseUrl
        And path '/tasks/'+ task_id +'/complete'
        And header Authorization = 'Bearer ' + accessToken
        And header 'Content-Type' = 'application/json'
        And request {}
        When method post
        Then status 200

        # Execute the POST method to check if the completed task can be re completed
        Given url baseUrl
        And def taskId = task_id
        And path '/tasks/'+ taskId +'/complete'
        And header Authorization = 'Bearer ' + accessToken
        And header 'Content-Type' = 'application/json'
        And request {}
        When method post
        Then status 500
        Then match response == 
            """
            {
            "timestamp": "#string",
            "status": 500,
            "error": "Internal Server Error",
            "message": "#present",
            "path": '#present'
            }		
            """
        Then match response.message contains 'Cannot find task with id '+taskId
        Then match response.path contains '/api/tasks/'+taskId+'/complete'

        # Check if the task is not present in available list
        Given url camundaUrl
        And def pastTaskId = task_id
        And path '/task'
        And header Authorization = 'Bearer ' + accessToken
        When method get
        Then status 200
        Then def req = get[0] response[?(@.id == '"+ pastTaskId +"')]
        Then match req == []


        #Check if the task id is available in history list
        Given url baseUrl
        And path '/tasks'
        And header Authorization = 'Bearer ' + accessToken
        And header 'Content-Type' = 'application/json'
        And request {"mrn": "mrn004","taskCategory": "HISTORY","firstResult": "0","maxResults": "5"}
        When method post
        Then status 200
        Then match response[*].id contains pastTaskId
        Then match response[*].id == "#present"
        Then match response[*].name == "#present"
        Then match response[*].taskDefinitionKey == "#present"
        Then match response[*].caseInstanceId == "#present"
        Then match response[*].due == "#present"

    Scenario: Verify if the task entities are correctly deleted
        Then def deletedep = call read('classpath:caremanagement/cmmn/deleteDeployment.feature')