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

Feature: Test CMMN creation and deployment

    Background:
    * def signIn = call read('classpath:login.feature')
    * def accessToken = signIn.accessToken

    Scenario: Test the cmmn creation when xlsx is empty (no sheets)

        Given url baseUrl
        And path '/file/program'
        And header Authorization = 'Bearer ' + accessToken
        And header 'Content-Type' = 'multipart/form-data; boundary=----WebKitFormBoundary7MA4YWxkTrZu0gW'
        And multipart file file = { read: 'classpath:testData/Test1.xlsx', filename: 'Test1.xlsx', contentType:'form-data'}
        And request {}
        When method post
        Then status 200
        Then match response[0].Sheet1.status == 422
        Then match response[0].Sheet1.error == "Unprocessable Entity"
        Then match response[0].Sheet1.message == "Error importing worksheet at index# 1, Worksheet: Sheet1, Response status 422 with reason \"Worksheet: Sheet1, undefined program\""

    Scenario: Test the cmmn creation when xlsx just has program without episode goal and task
        Given url baseUrl
        And path '/file/program'
        And header Authorization = 'Bearer ' + accessToken
        And header 'Content-Type' = 'multipart/form-data; boundary=----WebKitFormBoundary7MA4YWxkTrZu0gW'
        And multipart file file = { read: 'classpath:testData/Test2.xlsx', filename: 'Test2.xlsx', contentType:'form-data'}
        And request {}
        When method post
        Then status 200
        Then match response[0].Program.status == 422
        Then match response[0].Program.error == "Unprocessable Entity"
        Then match response[0].Program.message == "Error importing worksheet at index# 1, Worksheet: Program, Response status 422 with reason \"Worksheet: Program, undefined program\""

    Scenario: Test the cmmn creation when xlsx just has program without goal and task
        Given url baseUrl
        And path '/file/program'
        And header Authorization = 'Bearer ' + accessToken
        And header 'Content-Type' = 'multipart/form-data; boundary=----WebKitFormBoundary7MA4YWxkTrZu0gW'
        And multipart file file = { read: 'classpath:testData/Test3.xlsx', filename: 'Test3.xlsx', contentType:'form-data'}
        And request {}
        When method post
        Then status 200
        Then match response[0].Program.status == 422
        Then match response[0].Program.error == "Unprocessable Entity"
        Then match response[0].Program.message == "Error importing worksheet at index# 1, Worksheet: Program, Response status 422 with reason \"Worksheet: Program, undefined tasks\""

    Scenario: Test the cmmn creation program has 1 task and goal is successfully created
        Given url baseUrl
        And path '/file/program'
        And header Authorization = 'Bearer ' + accessToken
        And header 'Content-Type' = 'multipart/form-data; boundary=----WebKitFormBoundary7MA4YWxkTrZu0gW'
        And multipart file file = { read: 'classpath:testData/Test4.xlsx', filename: 'Test4.xlsx', contentType:'form-data'}
        And request {}
        When method post
        Then status 200
        Then def program1 = response[0].Program.id
        
        Given url baseUrl
        And path '/file/program/'+ program1
        And header Authorization = 'Bearer ' + accessToken
        Then method get
        Then status 200
        Then match header Content-Type == 'text/xml'
        Then xmlstring programXml1 = response
        Then def CreateFile = Java.type('io.igia.caremanagement.CreateFile')
        Then string filePath1 = "target/test1.cmmn"
        Then def result = CreateFile.createFile(programXml1,filePath1)

        Given url camundaUrl
        And path '/deployment/create'
        And header Authorization = 'Bearer ' + accessToken
        And header 'Content-Type' = 'multipart/form-data; boundary=----WebKitFormBoundary7MA4YWxkTrZu0gW'
        And multipart file DepCmmn = { read: 'file:target/test1.cmmn', filename: 'test1.cmmn', contentType:'form-data'}
        And request {}
        When method post
        Then status 200

        Given url baseUrl
        And path 'case-definition/create'
        And header Authorization = 'Bearer ' + accessToken
        And header 'Content-Type' = 'application/json'
        And request 
            """
            {
            "variables": 
            {
            "Navigator" : {"value" : "service-account-internal", "type": "String"}
            },
            "mrn" : "mrn004",
            "programId": "#(program1)"
            }
            """
        When method post
        Then status 200

        # Verify the program data created P1       
        Given url baseUrl
        And path '/definitions/programs'
        And header Authorization = 'Bearer ' + accessToken
        When method get
        Then status 200
        Then match response[0].description == "CKD Description"
        Then match response[0].programIdU == "P1"
        Then match response[0].name == "CKD"
        Then def programId = response[0].id

        Given url baseUrl
        And path '/definitions/episodes'
        And header Authorization = 'Bearer ' + accessToken
        When method get
        Then status 200
        Then def episodeId = response[0].id

        Given url baseUrl
        And path '/definitions/goals'
        And header Authorization = 'Bearer ' + accessToken
        When method get
        Then status 200
        Then match response[0].name == "Aggressive blood pressure control to target values 130/80"
        Then match response[0].description == "#null"
        Then match response[0].etaValue == 1
        Then match response[0].etaUnit == "MONTHS"
        Then match response[0].episodeId == episodeId

        #Verify the tasks created
        Given url baseUrl
        And path '/definitions/tasks'
        And header Authorization = 'Bearer ' + accessToken
        When method get
        Then status 200
        Then match response[0].name == "Capture blood pressure reading"
        Then match response[0].description == "#null"
        Then match response[0].type == "HUMAN"
        Then match response[0].typeRef == "#null"
        Then match response[0].assignee == "##notnull"
        Then match response[0].repeatFrequencyUnit == "WEEKS"
        Then match response[0].repeatFrequencyValue == 3
        Then match response[0].repeatEvent == "COMPLETE"
        Then match response[0].isRepeat == true

        Then def completeFunction1 = call read('classpath:completeTodoUpcoming.feature')

        #Delete deployment
        Then def deletedep = call read('classpath:caremanagement/cmmn/deleteDeployment.feature')

    Scenario: Test the cmmn creation when program P1 and P2 have the same goals/tasks
        Given url baseUrl
        And path '/file/program'
        And header Authorization = 'Bearer ' + accessToken
        And header 'Content-Type' = 'multipart/form-data; boundary=----WebKitFormBoundary7MA4YWxkTrZu0gW'
        And multipart file file = { read: 'classpath:testData/Test4.xlsx', filename: 'Test4.xlsx', contentType:'form-data'}
        And request {}
        When method post
        Then status 200
        Then def program1 = response[0].Program.id
        
        Given url baseUrl
        And path '/file/program/'+ program1
        And header Authorization = 'Bearer ' + accessToken
        Then method get
        Then status 200
        Then match header Content-Type == 'text/xml'
        Then xmlstring programXml1 = response
        Then def CreateFile = Java.type('io.igia.caremanagement.CreateFile')
        Then string filePath1 = "target/test2.cmmn"
        Then def result = CreateFile.createFile(programXml1,filePath1)

        Given url baseUrl
        And path '/file/program'
        And header Authorization = 'Bearer ' + accessToken
        And header 'Content-Type' = 'multipart/form-data; boundary=----WebKitFormBoundary7MA4YWxkTrZu0gW'
        And multipart file file = { read: 'classpath:testData/Test5.xlsx', filename: 'Test5.xlsx', contentType:'form-data'}
        And request {}
        When method post
        Then status 200
        Then def program2 = response[0].Program.id
        
        Given url baseUrl
        And path '/file/program/'+ program2
        And header Authorization = 'Bearer ' + accessToken
        Then method get
        Then status 200
        Then match header Content-Type == 'text/xml'
        Then xmlstring programXml2 = response
        Then def CreateFile2 = Java.type('io.igia.caremanagement.CreateFile')
        Then string filePath2 = "target/test3.cmmn"
        Then def result2 = CreateFile2.createFile(programXml2,filePath2)

        Given url camundaUrl
        And path '/deployment/create'
        And header Authorization = 'Bearer ' + accessToken
        And header 'Content-Type' = 'multipart/form-data; boundary=----WebKitFormBoundary7MA4YWxkTrZu0gW'
        And multipart file Cmmn1 = { read: 'file:target/test2.cmmn', filename: 'test2.cmmn', contentType:'form-data'}
        And request {}
        When method post
        Then status 200

        Given url camundaUrl
        And path '/deployment/create'
        And header Authorization = 'Bearer ' + accessToken
        And header 'Content-Type' = 'multipart/form-data; boundary=----WebKitFormBoundary7MA4YWxkTrZu0gW'
        And multipart file Cmmn2 = { read: 'file:target/test3.cmmn', filename: 'test3.cmmn', contentType:'form-data'}
        And request {}
        When method post
        Then status 200

        Given url baseUrl
        And path '/case-definition/create'
        And header Authorization = 'Bearer ' + accessToken
        And header 'Content-Type' = 'application/json'
        And request
            """
            {
            "variables":
            {
            "Navigator" : {"value" : "service-account-internal", "type": "String"}
            },
            "mrn" : "mrn004",
            "programId": "#(program1)"
            }
            """
        When method post
        Then status 200

        Given url baseUrl
        And path '/case-definition/create'
        And header Authorization = 'Bearer ' + accessToken
        And header 'Content-Type' = 'application/json'
        And request
            """
            {
            "variables":
            {
            "Navigator" : {"value" : "service-account-internal", "type": "String"}
            },
            "mrn" : "mrn004",
            "programId": "#(program2)"
            }
            """
        When method post
        Then status 200

        # Check the active goals
        Given url baseUrl
        And path '/goals'
        And header Authorization = 'Bearer ' + accessToken
        And header 'Content-Type' = 'application/json'
        And request {"category" : "ACTIVE", "programId" : "P1"}
        When method post
        Then status 200
        Then def noOfGoals1 = response.length
        Then match noOfGoals1 == 1

        Given url baseUrl
        And path '/goals'
        And header Authorization = 'Bearer ' + accessToken
        And header 'Content-Type' = 'application/json'
        And request {"category" : "ACTIVE", "programId" : "P2"}
        When method post
        Then status 200
        Then def noOfGoals2 = response.length
        Then match noOfGoals2 == 1

        Given url baseUrl
        And path '/tasks'
        And header Authorization = 'Bearer ' + accessToken
        And header 'Content-Type' = 'application/json'
        And request {"taskCategory": "TODO", "firstResult": "0", "programId" : "P2"}
        When method post
        Then status 200
        Then def respL1 = response.length
        Then match respL1 == 1

        Given url baseUrl
        And path '/tasks'
        And header Authorization = 'Bearer ' + accessToken
        And header 'Content-Type' = 'application/json'
        And request {"taskCategory": "TODO", "firstResult": "0", "programId" : "P1"}
        When method post
        Then status 200
        Then def respL2 = response.length
        Then match respL2 == 1

        Given url baseUrl
        And path '/tasks'
        And header Authorization = 'Bearer ' + accessToken
        And header 'Content-Type' = 'application/json'
        And request {"taskCategory": "TODO", "firstResult": "0"}
        When method post
        Then status 200
        Then def resp1 = response
        Then def respL3 = response.length
        Then match respL3 == 2
        Then def completeFunction1 = call read('classpath:completeTasks.feature') resp1

        Given url baseUrl
        And path '/tasks'
        And header Authorization = 'Bearer ' + accessToken
        And header 'Content-Type' = 'application/json'
        And request {"taskCategory": "UPCOMING", "firstResult": "0", "programId" : "P2"}
        When method post
        Then status 200
        Then def respL4 = response.length
        Then match respL4 == 1

        Given url baseUrl
        And path '/tasks'
        And header Authorization = 'Bearer ' + accessToken
        And header 'Content-Type' = 'application/json'
        And request {"taskCategory": "UPCOMING", "firstResult": "0", "programId" : "P1"}
        When method post
        Then status 200
        Then def respL5 = response.length
        Then match respL5 == 1

        Given url baseUrl
        And path '/tasks'
        And header Authorization = 'Bearer ' + accessToken
        And header 'Content-Type' = 'application/json'
        And request {"taskCategory": "UPCOMING", "firstResult": "0"}
        When method post
        Then status 200
        Then def resp2 = response
        Then def respL6 = response.length
        Then match respL6 == 2

        Then def completeFunction2 = call read('classpath:completeTasks.feature') resp2

        Given url baseUrl
        And path '/tasks'
        And header Authorization = 'Bearer ' + accessToken
        And header 'Content-Type' = 'application/json'
        And request {"taskCategory": "HISTORY", "firstResult": "0", "programId" : "P1"}
        When method post
        Then status 200
        Then def respL7 = response.length
        Then match respL7 == 2

        Given url baseUrl
        And path '/tasks'
        And header Authorization = 'Bearer ' + accessToken
        And header 'Content-Type' = 'application/json'
        And request {"taskCategory": "HISTORY", "firstResult": "0"}
        When method post
        Then status 200
        Then def respL8 = response.length
        Then match respL8 == 4

        #Delete deployment
        Then def deletedep = call read('classpath:caremanagement/cmmn/deleteDeployment.feature')

    Scenario: Test the cmmn creation when goal 2 is associated with goal1 and goal 3 is associated with goal1 and 2
        Given url baseUrl
        And path '/file/program'
        And header Authorization = 'Bearer ' + accessToken
        And header 'Content-Type' = 'multipart/form-data; boundary=----WebKitFormBoundary7MA4YWxkTrZu0gW'
        And multipart file file = { read: 'classpath:testData/Test6.xlsx', filename: 'Test6.xlsx', contentType:'form-data'}
        And request {}
        When method post
        Then status 200
        Then def program3 = response[0].Program.id
        
        Given url baseUrl
        And path '/file/program/'+ program3
        And header Authorization = 'Bearer ' + accessToken
        Then method get
        Then status 200
        Then match header Content-Type == 'text/xml'
        Then xmlstring programXml1 = response
        Then def CreateFile = Java.type('io.igia.caremanagement.CreateFile')
        Then string filePath1 = "target/test6.cmmn"
        Then def result = CreateFile.createFile(programXml1,filePath1)

        Given url camundaUrl
        And path '/deployment/create'
        And header Authorization = 'Bearer ' + accessToken
        And header 'Content-Type' = 'multipart/form-data; boundary=----WebKitFormBoundary7MA4YWxkTrZu0gW'
        And multipart file Cmmn1 = { read: 'file:target/test6.cmmn', filename: 'test6.cmmn', contentType:'form-data'}
        And request {}
        When method post
        Then status 200

        Given url baseUrl
        And path '/case-definition/create'
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
            "mrn" : "mrn004",
            "programId": "#(program3)"
            }
            """
        When method post
        Then status 200

        # Check the active goals
        Given url baseUrl
        And path '/goals'
        And header Authorization = 'Bearer ' + accessToken
        And header 'Content-Type' = 'application/json'
        And request {"category" : "ACTIVE"}
        When method post
        Then status 200
        Then def noOfGoalsAc = response[0].caseInstances[0].goals.length
        Then match noOfGoalsAc == 1

        # Check the available goals
        Given url baseUrl
        And path '/goals'
        And header Authorization = 'Bearer ' + accessToken
        And header 'Content-Type' = 'application/json'
        And request {"category" : "AVAILABLE"}
        When method post
        Then status 200
        Then def noOfGoalsAv = response[0].caseInstances[0].goals.length
        Then match noOfGoalsAv == 2

        # Complete todo and upcoming task in goal 1
        Then def completeFunction1 = call read('classpath:completeTodoUpcoming.feature')

        # Check the active goals
        Given url baseUrl
        And path '/goals'
        And header Authorization = 'Bearer ' + accessToken
        And header 'Content-Type' = 'application/json'
        And request {"category" : "ACTIVE"}
        When method post
        Then status 200
        Then def noOfGoalsAc1 = response[0].caseInstances[0].goals.length
        Then match noOfGoalsAc1 == 1

        # Check the available goals
        Given url baseUrl
        And path '/goals'
        And header Authorization = 'Bearer ' + accessToken
        And header 'Content-Type' = 'application/json'
        And request {"category" : "AVAILABLE"}
        When method post
        Then status 200
        Then def noOfGoalsAv1 = response[0].caseInstances[0].goals.length
        Then match noOfGoalsAv1 == 1

        Then def completeFunction2 = call read('classpath:completeTodoUpcoming.feature')

        # Check the active goals
        Given url baseUrl
        And path '/goals'
        And header Authorization = 'Bearer ' + accessToken
        And header 'Content-Type' = 'application/json'
        And request {"category" : "ACTIVE"}
        When method post
        Then status 200
        Then def noOfGoals = response[0].caseInstances[0].goals.length
        Then match noOfGoals == 1

        Then def completeFunction2 = call read('classpath:completeTodoUpcoming.feature')

        #Delete deployment
        Then def deletedep = call read('classpath:caremanagement/cmmn/deleteDeployment.feature')

    Scenario: Test the cmmn creation when task is associated so is the goal
        Given url baseUrl
        And path '/file/program'
        And header Authorization = 'Bearer ' + accessToken
        And header 'Content-Type' = 'multipart/form-data; boundary=----WebKitFormBoundary7MA4YWxkTrZu0gW'
        And multipart file file = { read: 'classpath:testData/Test7.xlsx', filename: 'Test7.xlsx', contentType:'form-data'}
        And request {}
        When method post
        Then status 200
        Then def program3 = response[0].Program.id
        
        Given url baseUrl
        And path '/file/program/'+ program3
        And header Authorization = 'Bearer ' + accessToken
        Then method get
        Then status 200
        Then match header Content-Type == 'text/xml'
        Then xmlstring programXml1 = response
        Then def CreateFile = Java.type('io.igia.caremanagement.CreateFile')
        Then string filePath1 = "target/test7.cmmn"
        Then def result = CreateFile.createFile(programXml1,filePath1)

        Given url camundaUrl
        And path '/deployment/create'
        And header Authorization = 'Bearer ' + accessToken
        And header 'Content-Type' = 'multipart/form-data; boundary=----WebKitFormBoundary7MA4YWxkTrZu0gW'
        And multipart file Cmmn1 = { read: 'file:target/test7.cmmn', filename: 'test7.cmmn', contentType:'form-data'}
        And request {}
        When method post
        Then status 200

        Given url baseUrl
        And path '/case-definition/create'
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
            "mrn" : "mrn004",
            "programId": "#(program3)"
            }
            """
        When method post
        Then status 200

        Given url baseUrl
        And path '/definitions/goals'
        And header Authorization = 'Bearer ' + accessToken
        When method get
        Then status 200
        Then def goalsPresent = get response
        Then def goal1 = "Aggressive blood pressure control to target values 130/80"
        Then def goal2 = "Treatment of hyperlipidemia to target Total Cholesterol 200mg/dL"
        Then def goal3 = "Aggressive glycemic control target hemoglobin A1c [HbA1C] < 7%"
        Then def goalG1 = karate.jsonPath(goalsPresent, "$[?(@.name=='" + goal1 + "')]")
        Then def goalG2 = karate.jsonPath(goalsPresent, "$[?(@.name=='" + goal2 + "')]")
        Then def goalG3 = karate.jsonPath(goalsPresent, "$[?(@.name=='" + goal3 + "')]")

        Given url baseUrl
        And path '/definitions/goal-associates'
        And header Authorization = 'Bearer ' + accessToken
        When method get
        Then status 200
        Then def goalAssociates = get response
        Then match each goalAssociates[*].goalId == goalG2[0].id
        Then match goalAssociates[0].associateOn == goalG1[0].id
        Then match goalAssociates[1].associateOn == goalG3[0].id

        #Verify the tasks created
        Given url baseUrl
        And path '/definitions/tasks'
        And header Authorization = 'Bearer ' + accessToken
        When method get
        Then status 200
        Then def tasksInG2 = response
        Then def tasksG2 = karate.jsonPath(tasksInG2, "$[?(@.goalId =='" + goalG2[0].id + "')]")
        Then def taskT1 = karate.jsonPath(tasksG2, "$[?(@.name=='Check Lipids')]")
        Then def taskT2 = karate.jsonPath(tasksG2, "$[?(@.name=='Alert PCP if Total cholesterol >225mg/dL')]")
        Then def taskT3 = karate.jsonPath(tasksG2, "$[?(@.name=='Prescription renewal for Statins')]")

        Given url baseUrl
        And path '/definitions/task-associates'
        And header Authorization = 'Bearer ' + accessToken
        When method get
        Then status 200
        Then def taskAssociates = get response
        Then def taskAssociateG2 = karate.jsonPath(taskAssociates, "$[?(@.taskId =='" + taskT3[0].id + "')]")
        Then match taskAssociateG2 != []
        Then match taskAssociateG2[0].associateOn == taskT1[0].id
        Then match taskAssociateG2[1].associateOn == taskT2[0].id

        Given url baseUrl
        And path '/tasks'
        And header Authorization = 'Bearer ' + accessToken
        And header 'Content-Type' = 'application/json'
        And request {"taskCategory": "TODO", "firstResult": "0"}
        When method post
        Then status 200
        Then print response
        Then def resp = response
        Then def todoLength = resp.length
        Then match todoLength == 3

        Given url baseUrl
        And path '/tasks'
        And header Authorization = 'Bearer ' + accessToken
        And header 'Content-Type' = 'application/json'
        And request {"taskCategory": "AVAILABLE", "firstResult": "0"}
        When method post
        Then status 200
        Then def resp2 = response
        Then def todoLength2 = resp2.length
        Then match todoLength2 == 2

        Given url baseUrl
        And path '/tasks'
        And header Authorization = 'Bearer ' + accessToken
        And header 'Content-Type' = 'application/json'
        And request {"taskCategory": "AVAILABLE", "firstResult": "0", "programId" : "P2"}
        When method post
        Then status 200
        Then def resp3 = response
        Then def todoLength3 = resp3.length
        Then match todoLength3 == 0

        Then def completeFunction1 = call read('classpath:completeTasks.feature') resp

        Given url baseUrl
        And path '/tasks'
        And header Authorization = 'Bearer ' + accessToken
        And header 'Content-Type' = 'application/json'
        And request {"taskCategory": "TODO", "firstResult": "0"}
        When method post
        Then status 200
        Then def resp1 = response
        Then def todoLength1 = resp1.length
        Then match todoLength1 == 1

        Then def completeFunction2 = call read('classpath:completeTodoUpcoming.feature')

        Given url baseUrl
        And path '/tasks'
        And header Authorization = 'Bearer ' + accessToken
        And header 'Content-Type' = 'application/json'
        And request {"taskCategory": "UPCOMING", "firstResult": "0"}
        When method post
        Then status 200
        Then def resp2 = response
        Then print resp2
        Then def completeFunction3 = call read('classpath:completeTasks.feature') resp2

        Given url baseUrl
        And path '/tasks'
        And header Authorization = 'Bearer ' + accessToken
        And header 'Content-Type' = 'application/json'
        And request {"taskCategory": "TODO", "firstResult": "0"}
        When method post
        Then status 200
        Then def resp3 = response
        Then def todoLength3 = resp1.length
        Then match todoLength3 == 1

        Then def completeFunction2 = call read('classpath:completeTodoUpcoming.feature')

        #Delete deployment
        Then def deletedep = call read('classpath:caremanagement/cmmn/deleteDeployment.feature')

    Scenario: Test the cmmn creation when task assignee is empty should give appropriate error
        Given url baseUrl
        And path '/file/program'
        And header Authorization = 'Bearer ' + accessToken
        And header 'Content-Type' = 'multipart/form-data; boundary=----WebKitFormBoundary7MA4YWxkTrZu0gW'
        And multipart file file = { read: 'classpath:testData/Test8.xlsx', filename: 'Test8.xlsx', contentType:'form-data'}
        And request {}
        When method post
        Then status 200
        Then match response[0].Program.status == 422
        Then match response[0].Program.error == "Unprocessable Entity"
        Then match response[0].Program.message == "Error importing worksheet at index# 1, Worksheet: Program, Response status 422 with reason \"Row# 10 col# 4 undefined task assignee\""
        

    Scenario: Test the cmmn creation when goal is duplicate should give appropriate error
        Given url baseUrl
        And path '/file/program'
        And header Authorization = 'Bearer ' + accessToken
        And header 'Content-Type' = 'multipart/form-data; boundary=----WebKitFormBoundary7MA4YWxkTrZu0gW'
        And multipart file file = { read: 'classpath:testData/Test9.xlsx', filename: 'Test9.xlsx', contentType:'form-data'}
        And request {}
        When method post
        Then status 200
        Then match response[0].Program.status == 422
        Then match response[0].Program.error == "Unprocessable Entity"
        Then match response[0].Program.message == "Error importing worksheet at index# 1, Worksheet: Program, Response status 422 with reason \"Row# 13 col# 1, duplicate goal name Aggressive blood pressure control to target values 130/80\""
        
    Scenario: Test the cmmn creation when task is duplicate should give appropriate error
        Given url baseUrl
        And path '/file/program'
        And header Authorization = 'Bearer ' + accessToken
        And header 'Content-Type' = 'multipart/form-data; boundary=----WebKitFormBoundary7MA4YWxkTrZu0gW'
        And multipart file file = { read: 'classpath:testData/Test10.xlsx', filename: 'Test10.xlsx', contentType:'form-data'}
        And request {}
        When method post
        Then status 200
        Then match response[0].Program.status == 422
        Then match response[0].Program.error == "Unprocessable Entity"
        Then match response[0].Program.message == "Error importing worksheet at index# 1, Worksheet: Program, Response status 422 with reason \"Row# 11 col# 1, duplicate task name Capture blood pressure reading\""

    Scenario: Test the cmmn creation when program P1 and P2 have the same goals/tasks
        Given url baseUrl
        And path '/file/program'
        And header Authorization = 'Bearer ' + accessToken
        And header 'Content-Type' = 'multipart/form-data; boundary=----WebKitFormBoundary7MA4YWxkTrZu0gW'
        And multipart file file = { read: 'classpath:testData/Test4.xlsx', filename: 'Test4.xlsx', contentType:'form-data'}
        And request {}
        When method post
        Then status 200
        Then def program1 = response[0].Program.id

        Given url baseUrl
        And path '/file/program'
        And header Authorization = 'Bearer ' + accessToken
        And header 'Content-Type' = 'multipart/form-data; boundary=----WebKitFormBoundary7MA4YWxkTrZu0gW'
        And multipart file file = { read: 'classpath:testData/Test5.xlsx', filename: 'Test5.xlsx', contentType:'form-data'}
        And request {}
        When method post
        Then status 200
        Then def program2 = response[0].Program.id
                
        Given url baseUrl
        And path '/file/program/'+ program1
        And header Authorization = 'Bearer ' + accessToken
        Then method get
        Then status 200
        Then match header Content-Type == 'text/xml'
        Then xmlstring programXml1 = response
        Then def CreateFile = Java.type('io.igia.caremanagement.CreateFile')
        Then string filePath1 = "target/test8.cmmn"
        Then def result = CreateFile.createFile(programXml1,filePath1)
        
        Given url baseUrl
        And path '/file/program/'+ program2
        And header Authorization = 'Bearer ' + accessToken
        Then method get
        Then status 200
        Then match header Content-Type == 'text/xml'
        Then xmlstring programXml2 = response
        Then def CreateFile2 = Java.type('io.igia.caremanagement.CreateFile')
        Then string filePath2 = "target/test9.cmmn"
        Then def result2 = CreateFile2.createFile(programXml2,filePath2)
        

        Given url camundaUrl
        And path '/deployment/create'
        And header Authorization = 'Bearer ' + accessToken
        And header 'Content-Type' = 'multipart/form-data; boundary=----WebKitFormBoundary7MA4YWxkTrZu0gW'
        And multipart file Cmmn1 = { read: 'file:target/test8.cmmn', filename: 'test8.cmmn', contentType:'form-data'}
        And request {}
        When method post
        Then status 200

        Given url camundaUrl
        And path '/deployment/create'
        And header Authorization = 'Bearer ' + accessToken
        And header 'Content-Type' = 'multipart/form-data; boundary=----WebKitFormBoundary7MA4YWxkTrZu0gW'
        And multipart file Cmmn2 = { read: 'file:target/test9.cmmn', filename: 'test9.cmmn', contentType:'form-data'}
        And request {}
        When method post
        Then status 200

        Given url baseUrl
        And path '/case-definition/create'
        And header Authorization = 'Bearer ' + accessToken
        And header 'Content-Type' = 'application/json'
        And request
            """
            {
            "variables":
            {
            "Navigator" : {"value" : "service-account-internal", "type": "String"}
            },
            "mrn" : "mrn0011",
            "programId": "#(program1)"
            }
            """
        When method post
        Then status 200

        Given url baseUrl
        And path '/case-definition/create'
        And header Authorization = 'Bearer ' + accessToken
        And header 'Content-Type' = 'application/json'
        And request
            """
            {
            "variables":
            {
            "Navigator" : {"value" : "service-account-internal", "type": "String"}
            },
            "mrn" : "mrn0012",
            "programId": "#(program2)"
            }
            """
        When method post
        Then status 200

        # Check the active goals
        Given url baseUrl
        And path '/goals'
        And header Authorization = 'Bearer ' + accessToken
        And header 'Content-Type' = 'application/json'
        And request {"category" : "ACTIVE", "programId" : "P1"}
        When method post
        Then status 200
        Then def noOfGoals1 = response.length
        Then match noOfGoals1 == 1

        Given url baseUrl
        And path '/goals'
        And header Authorization = 'Bearer ' + accessToken
        And header 'Content-Type' = 'application/json'
        And request {"category" : "ACTIVE", "programId" : "P2"}
        When method post
        Then status 200
        Then def noOfGoals2 = response.length
        Then match noOfGoals2 == 1

        Given url baseUrl
        And path '/tasks'
        And header Authorization = 'Bearer ' + accessToken
        And header 'Content-Type' = 'application/json'
        And request {"taskCategory": "TODO", "firstResult": "0", "mrn" : "mrn0012"}
        When method post
        Then status 200
        Then def respL1 = response.length
        Then match respL1 == 1

        Given url baseUrl
        And path '/tasks'
        And header Authorization = 'Bearer ' + accessToken
        And header 'Content-Type' = 'application/json'
        And request {"taskCategory": "TODO", "firstResult": "0", "mrn" : "mrn0011"}
        When method post
        Then status 200
        Then def respL2 = response.length
        Then match respL2 == 1

        Given url baseUrl
        And path '/tasks'
        And header Authorization = 'Bearer ' + accessToken
        And header 'Content-Type' = 'application/json'
        And request {"taskCategory": "TODO", "firstResult": "0"}
        When method post
        Then status 200
        Then def resp1 = response
        Then def respL3 = response.length
        Then match respL3 == 2
        Then def completeFunction1 = call read('classpath:completeTasks.feature') resp1

        Given url baseUrl
        And path '/tasks'
        And header Authorization = 'Bearer ' + accessToken
        And header 'Content-Type' = 'application/json'
        And request {"taskCategory": "UPCOMING", "firstResult": "0", "mrn" : "mrn0011"}
        When method post
        Then status 200
        Then def respL4 = response.length
        Then match respL4 == 1

        Given url baseUrl
        And path '/tasks'
        And header Authorization = 'Bearer ' + accessToken
        And header 'Content-Type' = 'application/json'
        And request {"taskCategory": "UPCOMING", "firstResult": "0", "mrn" : "mrn0012"}
        When method post
        Then status 200
        Then def respL5 = response.length
        Then match respL5 == 1

        Given url baseUrl
        And path '/tasks'
        And header Authorization = 'Bearer ' + accessToken
        And header 'Content-Type' = 'application/json'
        And request {"taskCategory": "UPCOMING", "firstResult": "0"}
        When method post
        Then status 200
        Then def resp2 = response
        Then def respL6 = response.length
        Then match respL6 == 2

        Then def completeFunction2 = call read('classpath:completeTasks.feature') resp2

        Given url baseUrl
        And path '/tasks'
        And header Authorization = 'Bearer ' + accessToken
        And header 'Content-Type' = 'application/json'
        And request {"taskCategory": "HISTORY", "firstResult": "0", "mrn" : "mrn0012"}
        When method post
        Then status 200
        Then def respL7 = response.length
        Then match respL7 == 2

        Given url baseUrl
        And path '/tasks'
        And header Authorization = 'Bearer ' + accessToken
        And header 'Content-Type' = 'application/json'
        And request {"taskCategory": "HISTORY", "firstResult": "0"}
        When method post
        Then status 200
        Then def respL8 = response.length
        Then match respL8 == 4

        #Delete deployment
        Then def deletedep = call read('classpath:caremanagement/cmmn/deleteDeployment.feature')
