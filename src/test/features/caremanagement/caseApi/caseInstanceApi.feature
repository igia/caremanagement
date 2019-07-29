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


    Scenario: Test the deployment of the program
        Given url baseUrl
        And path '/file/program'
        And header Authorization = 'Bearer ' + accessToken
        And header 'Content-Type' = 'multipart/form-data; boundary=----WebKitFormBoundary7MA4YWxkTrZu0gW'
        And multipart file file = { read: 'classpath:testData/Test4.xlsx', filename: 'Test4.xlsx', contentType:'form-data'}
        And request {}
        When method post
        Then status 200
        Then print response
        Then def program1 = response[0].Program.id
        
        Given url baseUrl
        And path '/file/program/'+ program1
        And header Authorization = 'Bearer ' + accessToken
        Then method get
        Then status 200
        Then match header Content-Type == 'text/xml'
        Then xmlstring programXml1 = response
        Then def CreateFile = Java.type('io.igia.caremanagement.CreateFile')
        Then string filePath1 = "target/test4.cmmn"
        Then def result = CreateFile.createFile(programXml1,filePath1)

        Given url camundaUrl
        And path '/deployment/create'
        And header Authorization = 'Bearer ' + accessToken
        And header 'Content-Type' = 'multipart/form-data; boundary=----WebKitFormBoundary7MA4YWxkTrZu0gW'
        And multipart file DepCmmn = { read: 'file:target/test4.cmmn', filename: 'test4.cmmn', contentType:'form-data'}
        And request {}
        When method post
        Then status 200

    Scenario: Test creation of case instance for Program
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
            "programId": "P1"
            }
            """
        When method post
        Then status 200
        Then def caseId = response.id
        Then match response.caseDefinitionId == "#notnull"
        Then match response.businessKey == "mrn004"
        Then match response.active == "true"
        Then match response.terminated == "false"
        Then match response.completed == "false"

    Scenario: Test recreation of same case instance for Program should fail with appropriate error
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
            "programId": "P1"
            }
            """
        When method post
        Then status 500
        Then match response.message contains "Instance with mrn already present for a given program"

    Scenario: Test termination and closing of of case instance for Program
        # Terminate when the tasks/goals are complete        
        Given url baseUrl
        And path '/case-instance/terminate'
        And param mrn = "mrn004"
        And param programId = "P1"
        And request {}
        And header Authorization = 'Bearer ' + accessToken
        And header 'Content-Type' = 'application/json'
        When method post
        Then status 200

        # Reterminate the same case instance
        Given url baseUrl
        And path '/case-instance/terminate'
        And param mrn = "mrn004"
        And param programId = "P1"
        And request {}
        And header Authorization = 'Bearer ' + accessToken
        And header 'Content-Type' = 'application/json'
        When method post
        Then status 500

        # Terminate an invalid case instance mrn
        Given url baseUrl
        And path '/case-instance/terminate'
        And param mrn = "mrn005"
        And param programId = "P1"
        And request {}
        And header Authorization = 'Bearer ' + accessToken
        And header 'Content-Type' = 'application/json'
        When method post
        Then status 500

        # Close a terminated case instance
        And path '/case-instance/close'
        And param mrn = "mrn004"
        And param programId = "P1"
        And request {}
        And header Authorization = 'Bearer ' + accessToken
        And header 'Content-Type' = 'application/json'
        When method post
        Then status 200

    Scenario: Test creation of case instance for same mrn that was terminated earlier
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
            "programId": "P1"
            }
            """
        When method post
        Then status 200
        Then def caseId = response.id
        Then match response.caseDefinitionId == "#notnull"
        Then match response.businessKey == "mrn004"
        Then match response.active == "true"
        Then match response.terminated == "false"
        Then match response.completed == "false"

        # Close a case instance
        And path '/case-instance/close'
        And param mrn = "mrn004"
        And param programId = "P1"
        And request {}
        And header Authorization = 'Bearer ' + accessToken
        And header 'Content-Type' = 'application/json'
        When method post
        Then status 500
        Then match response.message contains "The case instance must be in state \'[completed|terminated|suspended]\' to close it, but the state is \'active\'"

        Given url baseUrl
        And path '/case-instance/terminate'
        And param mrn = "mrn004"
        And param programId = "P1"
        And request {}
        And header Authorization = 'Bearer ' + accessToken
        And header 'Content-Type' = 'application/json'
        When method post
        Then status 200

        # Close a case instance
        And path '/case-instance/close'
        And param mrn = "mrn004"
        And param programId = "P1"
        And request {}
        And header Authorization = 'Bearer ' + accessToken
        And header 'Content-Type' = 'application/json'
        When method post
        Then status 200

    Scenario: Test termination and closing of of case instance for Program when tasks are completed should fail with appropriate error
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
            "mrn" : "mrn005",
            "programId": "P1"
            }
            """
        When method post
        Then status 200

        Given url baseUrl
        And path '/tasks'
        And header Authorization = 'Bearer ' + accessToken
        And header 'Content-Type' = 'application/json'
        And request {"mrn" : "mrn005", "taskCategory": "TODO","firstResult": "0"}
        When method post
        Then status 200
        Then def resp1 = response
        Then def completeFunction1 = call read('classpath:completeTodoUpcoming.feature')

        Given url baseUrl
        And path '/case-instance'
        And param mrn = "mrn005"
        And param programId = "P1"
        And request {}
        And header Authorization = 'Bearer ' + accessToken
        And header 'Content-Type' = 'application/json'
        When method get
        Then status 200
        Then match response[0].completed == "true" 

        Given url baseUrl
        And path '/case-instance/terminate'
        And param mrn = "mrn005"
        And param programId = "P1"
        And request {}
        And header Authorization = 'Bearer ' + accessToken
        And header 'Content-Type' = 'application/json'
        When method post
        Then status 500
        Then match response.message contains "Reason: The case execution must be in state \'active\' to terminate, but it was in state \'completed\'"

        # Close a completed case instance
        And path '/case-instance/close'
        And param mrn = "mrn005"
        And param programId = "P1"
        And request {}
        And header Authorization = 'Bearer ' + accessToken
        And header 'Content-Type' = 'application/json'
        When method post
        Then status 200

        # Close a case instance which is already closed
        And path '/case-instance/close'
        And param mrn = "mrn005"
        And param programId = "P1"
        And request {}
        And header Authorization = 'Bearer ' + accessToken
        And header 'Content-Type' = 'application/json'
        When method post
        Then status 500

    Scenario: Test creation of case instance for invalid program id
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
            "mrn" : "mrn006",
            "programId": "P14"
            }
            """
        When method post
        Then status 500
        Then match response.message contains "No matching case definition with key: Case_P14"

    Scenario: Test creation of case instance without mrn
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
            "programId": "P1"
            }
            """
        When method post
        Then status 400

    Scenario: Test creation of case instance without program id
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
            "mrn" : "mrn007"
            }
            """
        When method post
        Then status 400

    Scenario: Terminate without program id should fail with appropriate error
        Given url baseUrl
        And path '/case-instance/terminate'
        And param mrn = "mrn005"
        And request {}
        And header Authorization = 'Bearer ' + accessToken
        And header 'Content-Type' = 'application/json'
        When method post
        Then status 400
        Then match response.message == "Required String parameter \'programId\' is not present"

    Scenario: Terminate without mrn should fail with appropriate error
        Given url baseUrl
        And path '/case-instance/terminate'
        And param programId = "P1"
        And request {}
        And header Authorization = 'Bearer ' + accessToken
        And header 'Content-Type' = 'application/json'
        When method post
        Then status 400
        Then match response.message == "Required String parameter \'mrn\' is not present"

    Scenario: Close without program id should fail with appropriate error
        Given url baseUrl
        And path '/case-instance/close'
        And param mrn = "mrn005"
        And request {}
        And header Authorization = 'Bearer ' + accessToken
        And header 'Content-Type' = 'application/json'
        When method post
        Then status 400
        Then match response.message == "Required String parameter \'programId\' is not present"

    Scenario: Close without mrn should fail with appropriate error
        Given url baseUrl
        And path '/case-instance/close'
        And param programId = "P1"
        And request {}
        And header Authorization = 'Bearer ' + accessToken
        And header 'Content-Type' = 'application/json'
        When method post
        Then status 400
        Then match response.message == "Required String parameter \'mrn\' is not present"

    Scenario: Add and delete the variable in the same request must fail with appropriate error
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
            "mrn" : "mrn008",
            "programId": "P1"
            }
            """
        When method post
        Then status 200
        Then def caseId = response.id

        Given url baseUrl
        And path '/case-instance/' + caseId + '/update'
        And header Authorization = 'Bearer ' + accessToken
        And header 'Content-Type' = 'application/json'
        And request
            """
            {
            "modifications" :{
            "PCP": {
            "type": "String",
            "value": "service-account-internal"
            }
            },
            "deletions": ["PCP"]
            }
            """
        When method post
        Then status 500
        Then match response.message contains "Cannot set and remove a variable with the same variable name: \'PCP\' within a command"

        #Terminate
        Given url baseUrl
        And path '/case-instance/terminate'
        And param programId = "P1"
        And param mrn = "mrn008"
        And request {}
        And header Authorization = 'Bearer ' + accessToken
        And header 'Content-Type' = 'application/json'
        When method post
        Then status 200

        #Close
        Given url baseUrl
        And path '/case-instance/close'
        And param programId = "P1"
        And param mrn = "mrn008"
        And request {}
        And header Authorization = 'Bearer ' + accessToken
        And header 'Content-Type' = 'application/json'
        When method post
        Then status 200

    Scenario: Update the datatype of the variable verify the value and delete the variable
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
            "mrn" : "mrn009",
            "programId": "P1"
            }
            """
        When method post
        Then status 200
        Then def caseId = response.id

        Given url baseUrl
        And path '/case-instance/' + caseId + '/update'
        And header Authorization = 'Bearer ' + accessToken
        And header 'Content-Type' = 'application/json'
        And request
            """
            {
            "modifications" :{
            "PCP": {
            "type": "Integer",
            "value": 1
            }
            }
            }
            """
        When method post
        Then status 200

        Given url camundaUrl
        And path '/case-instance/' + caseId + '/variables/PCP'
        And header Authorization = 'Bearer ' + accessToken
        And header 'Content-Type' = 'application/json'
        And method get
        Then status 200
        Then match response.type == "Integer"
        Then match response.value == 1


        Given url baseUrl
        And path '/case-instance/' + caseId + '/update'
        And header Authorization = 'Bearer ' + accessToken
        And header 'Content-Type' = 'application/json'
        And request
            """
            {
            "deletion" : ["PCP"]
            }
            """
        When method post
        Then status 200

        #Terminate
        Given url baseUrl
        And path '/case-instance/terminate'
        And param programId = "P1"
        And param mrn = "mrn009"
        And request {}
        And header Authorization = 'Bearer ' + accessToken
        And header 'Content-Type' = 'application/json'
        When method post
        Then status 200

        #Close
        Given url baseUrl
        And path '/case-instance/close'
        And param programId = "P1"
        And param mrn = "mrn009"
        And request {}
        And header Authorization = 'Bearer ' + accessToken
        And header 'Content-Type' = 'application/json'
        When method post
        Then status 200

    Scenario: Add a variable of non primitive data type
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
            "mrn" : "mrn010",
            "programId": "P1"
            }
            """
        When method post
        Then status 200
        Then def caseId = response.id

        Given url baseUrl
        And path '/case-instance/' + caseId + '/update'
        And header Authorization = 'Bearer ' + accessToken
        And header 'Content-Type' = 'application/json'
        And request
            """
            {
            "modifications" :{
            "cars":
            {
            "type": "Object",
            "value": "[{\"make\":\"Ford\",\"model\":\"escort\"},{\"make\":\"Ford\",\"model\":\"fiesta\"}]",
            "valueInfo":
            {
            "objectTypeName": "java.util.ArrayList",
            "serializationDataFormat": "application/json"
            }
            }
            }
            }
            """
        When method post
        Then status 200

        Given url camundaUrl
        And path '/case-instance/' + caseId + '/variables/cars'
        And header Authorization = 'Bearer ' + accessToken
        And header 'Content-Type' = 'application/json'
        And method get
        Then status 200
        Then match response.type == "Object"
        Then match response.value[0].make == "Ford"
        Then match response.value[0].model == "escort"
        Then match response.value[1].make == "Ford"
        Then match response.value[1].model == "fiesta"
        Then match response.valueInfo.objectTypeName == "java.util.ArrayList<java.util.LinkedHashMap<java.lang.Object,java.lang.Object>>"
        Then match response.valueInfo.serializationDataFormat == "application/json"

        Given url baseUrl
        And path '/case-instance/' + caseId + '/update'
        And header Authorization = 'Bearer ' + accessToken
        And header 'Content-Type' = 'application/json'
        And request
            """
            {
            "modifications" :{
            "cars":
            {
            "type": "String",
            "value": "Ford"
            }
            }
            }
            """
        When method post
        Then status 200

        Given url camundaUrl
        And path '/case-instance/' + caseId + '/variables/cars'
        And header Authorization = 'Bearer ' + accessToken
        And header 'Content-Type' = 'application/json'
        And method get
        Then status 200
        Then match response.type == "String"
        Then match response.value == "Ford"

        #Terminate the instance
        Given url baseUrl
        And path '/case-instance/terminate'
        And param programId = "P1"
        And param mrn = "mrn010"
        And request {}
        And header Authorization = 'Bearer ' + accessToken
        And header 'Content-Type' = 'application/json'
        When method post
        Then status 200

        #Close the instance
        Given url baseUrl
        And path '/case-instance/close'
        And param programId = "P1"
        And param mrn = "mrn010"
        And request {}
        And header Authorization = 'Bearer ' + accessToken
        And header 'Content-Type' = 'application/json'
        When method post
        Then status 200

    Scenario: Add a variable of non primitive data type while creating case instance
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
            "cars":
            {
            "type": "Object",
            "value": "[{\"make\":\"Ford\",\"model\":\"escort\"},{\"make\":\"Ford\",\"model\":\"fiesta\"}]",
            "valueInfo":
            {
            "objectTypeName": "java.util.ArrayList",
            "serializationDataFormat": "application/json"
            }
            }
            },
            "mrn" : "mrn010",
            "programId": "P1"
            }
            """
        When method post
        Then status 200
        Then def caseId = response.id

        Given url camundaUrl
        And path '/case-instance/' + caseId + '/variables/cars'
        And header Authorization = 'Bearer ' + accessToken
        And header 'Content-Type' = 'application/json'
        And method get
        Then status 200
        Then match response.type == "Object"
        Then match response.value[0].make == "Ford"
        Then match response.value[0].model == "escort"
        Then match response.value[1].make == "Ford"
        Then match response.value[1].model == "fiesta"
        Then match response.valueInfo.objectTypeName == "java.util.ArrayList<java.util.LinkedHashMap<java.lang.Object,java.lang.Object>>"
        Then match response.valueInfo.serializationDataFormat == "application/json"

        #Terminate the instance
        Given url baseUrl
        And path '/case-instance/terminate'
        And param programId = "P1"
        And param mrn = "mrn010"
        And request {}
        And header Authorization = 'Bearer ' + accessToken
        And header 'Content-Type' = 'application/json'
        When method post
        Then status 200

        #Close the instance
        Given url baseUrl
        And path '/case-instance/close'
        And param programId = "P1"
        And param mrn = "mrn010"
        And request {}
        And header Authorization = 'Bearer ' + accessToken
        And header 'Content-Type' = 'application/json'
        When method post
        Then status 200

    Scenario: With no variable modified or deleted
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
            "mrn" : "mrn010",
            "programId": "P1"
            }
            """
        When method post
        Then status 200
        Then def caseId = response.id

        Given url baseUrl
        And path '/case-instance/1/update'
        And header Authorization = 'Bearer ' + accessToken
        And header 'Content-Type' = 'application/json'
        And request
            """
            {

            }
            """
        When method post
        Then status 500

        #Terminate the instance
        Given url baseUrl
        And path '/case-instance/terminate'
        And param programId = "P1"
        And param mrn = "mrn010"
        And request {}
        And header Authorization = 'Bearer ' + accessToken
        And header 'Content-Type' = 'application/json'
        When method post
        Then status 200

        #Close the instance
        Given url baseUrl
        And path '/case-instance/close'
        And param programId = "P1"
        And param mrn = "mrn010"
        And request {}
        And header Authorization = 'Bearer ' + accessToken
        And header 'Content-Type' = 'application/json'
        When method post
        Then status 200


    Scenario: Multiple programs same mrns
        Given url baseUrl
        And path '/file/program'
        And header Authorization = 'Bearer ' + accessToken
        And header 'Content-Type' = 'multipart/form-data; boundary=----WebKitFormBoundary7MA4YWxkTrZu0gW'
        And multipart file file = { read: 'classpath:testData/Test5.xlsx', filename: 'Test5.xlsx', contentType:'form-data'}
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
        Then string filePath1 = "target/test5.cmmn"
        Then def result = CreateFile.createFile(programXml1,filePath1)

        Given url camundaUrl
        And path '/deployment/create'
        And header Authorization = 'Bearer ' + accessToken
        And header 'Content-Type' = 'multipart/form-data; boundary=----WebKitFormBoundary7MA4YWxkTrZu0gW'
        And multipart file DepCmmn = { read: 'file:target/test5.cmmn', filename: 'test4.cmmn', contentType:'form-data'}
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
            "mrn" : "mrn010",
            "programId": "P1"
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
            "mrn" : "mrn010",
            "programId": "P2"
            }
            """
        When method post
        Then status 200

        #Terminate the instance
        Given url baseUrl
        And path '/case-instance/terminate'
        And param programId = "P1"
        And param mrn = "mrn010"
        And request {}
        And header Authorization = 'Bearer ' + accessToken
        And header 'Content-Type' = 'application/json'
        When method post
        Then status 200

        #Close the instance
        Given url baseUrl
        And path '/case-instance/close'
        And param programId = "P1"
        And param mrn = "mrn010"
        And request {}
        And header Authorization = 'Bearer ' + accessToken
        And header 'Content-Type' = 'application/json'
        When method post
        Then status 200

        #Terminate the instance
        Given url baseUrl
        And path '/case-instance/terminate'
        And param programId = "P2"
        And param mrn = "mrn010"
        And request {}
        And header Authorization = 'Bearer ' + accessToken
        And header 'Content-Type' = 'application/json'
        When method post
        Then status 200

        #Close the instance
        Given url baseUrl
        And path '/case-instance/close'
        And param programId = "P2"
        And param mrn = "mrn010"
        And request {}
        And header Authorization = 'Bearer ' + accessToken
        And header 'Content-Type' = 'application/json'
        When method post
        Then status 200

        #Delete deployment
        Then def deletedep = call read('classpath:caremanagement/cmmn/deleteDeployment.feature')

