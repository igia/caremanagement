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

Feature: To test API related to get All deployments.
  I want to test API realted to get all deployments.

  Background: 
    * def signIn = call read('classpath:login.feature')
    * def accessToken = signIn.accessToken

  Scenario: To create CMMN files from XLSX files and then deploy those cmmn in Camunda server for testing
    #Create the CMMN XML from the XLSX file.
    Given url baseUrl
    And path '/file/program'
    And header Authorization = 'Bearer ' + accessToken
    And header 'Content-Type' = 'multipart/form-datamultipart/form-data; boundary=----WebKitFormBoundary7MA4YWxkTrZu0gW'
    And multipart file file = { read: 'classpath:caremanagement/caseInstance/DeploymentCMMN1.xlsx', filename: 'DeploymentCMMN1.xlsx', contentType:'form-data'}
    And request {}
    When method post
    Then status 200
    Then def program1 = response[0]["Program 1"].id
    Then def program2 = response[1]["Program 2"].id
    Then def program3 = response[2]["Program 3"].id
    #Write the first CMMN XML into file
    Given url baseUrl
    And path '/file/program/'+ program1
    And header Authorization = 'Bearer ' + accessToken
    Then method get
    Then status 200
    Then match header Content-Type == 'text/xml'
    Then xmlstring programXml1 = response
    Then def CreateFile = Java.type('io.igia.caremanagement.CreateFile')
    Then string filePath1 = "target/Deployment1.cmmn"
    Then def result = CreateFile.createFile(programXml1,filePath1)
    #Write the second CMMN XML into file
    Given url baseUrl
    And path '/file/program/'+ program2
    And header Authorization = 'Bearer ' + accessToken
    Then method get
    Then status 200
    Then match header Content-Type == 'text/xml'
    Then xmlstring programXml2 = response
    Then def CreateFile = Java.type('io.igia.caremanagement.CreateFile')
    Then string filePath2 = "target/Deployment2.cmmn"
    Then def result = CreateFile.createFile(programXml2,filePath2)
    #Write the third CMMN XML into file
    Given url baseUrl
    And path '/file/program/'+ program3
    And header Authorization = 'Bearer ' + accessToken
    Then method get
    Then status 200
    Then match header Content-Type == 'text/xml'
    Then xmlstring programXml3 = response
    Then def CreateFile = Java.type('io.igia.caremanagement.CreateFile')
    Then string filePath3 = "target/Deployment3.cmmn"
    Then def result = CreateFile.createFile(programXml3,filePath3)
    #Deploy first CMMN file into camunda server
    Given url camundaUrl
    And path '/deployment/create'
    And header Authorization = 'Bearer ' + accessToken
    And header 'Content-Type' = 'multipart/form-datamultipart/form-data; boundary=----WebKitFormBoundary7MA4YWxkTrZu0gW'
    And multipart file DepCmmn1 = {read: 'file: target/Deployment1.cmmn', filename: 'Deployment1.cmmn', contentType:'form-data'}
    And request {}
    When method post
    Then status 200
    #Deploy second CMMN file into camunda server
    Given url camundaUrl
    And path '/deployment/create'
    And header Authorization = 'Bearer ' + accessToken
    And header 'Content-Type' = 'multipart/form-datamultipart/form-data; boundary=----WebKitFormBoundary7MA4YWxkTrZu0gW'
    And multipart file DepCmmn2 = { read: 'file: target/Deployment2.cmmn', filename: 'Deployment2.cmmn', contentType:'form-data'}
    And request {}
    When method post
    Then status 200
    #Deploy third CMMN file into camunda server
    Given url camundaUrl
    And path '/deployment/create'
    And header Authorization = 'Bearer ' + accessToken
    And header 'Content-Type' = 'multipart/form-datamultipart/form-data; boundary=----WebKitFormBoundary7MA4YWxkTrZu0gW'
    And multipart file DepCmmn3 = { read: 'file: target/Deployment3.cmmn', filename: 'Deployment3.cmmn', contentType:'form-data'}
    And request {}
    When method post
    Then status 200

  Scenario: To test get case instances API's (all, by id, by mrn) when there is single, terminated and closed case instance present for the deployment on camunda server.
    # To create the one case instance for the deployment.
    Given url baseUrl
    And path 'case-definition/create'
    And header Authorization = 'Bearer ' + accessToken
    And header 'Content-Type' = 'application/json'
    And def req = read('CreateCaseReq.json')
    And set req.mrn = "test001"
    And set req.programId = "P1"
    And request req
    When method post
    Then status 200
    #To test get all case instances API when there is single case instance present for the deployment in Camunda.
    Given url baseUrl
    And path '/case-instance'
    And header Authorization = 'Bearer ' + accessToken
    And header 'Content-Type' = 'application/json'
    And request {}
    When method get
    Then status 200
    Then match each response contains {"id": "#present" , "caseDefinitionId": "#present" , "businessKey": "#present" , "active":"true" , "terminated": "false" , "completed": "false"}
    Then assert response.length == 1
    Then def CaseInstanceId = response[0].id
    Then def mrn = response[0].businessKey
    #To test get case instance by id API when there is single case instance for the deployment is present in Camunda.
    Given url baseUrl
    And path '/case-instance'
    And param caseInstanceId = CaseInstanceId
    And header Authorization = 'Bearer ' + accessToken
    And header 'Content-Type' = 'application/json'
    And request {}
    When method get
    Then status 200
    Then assert response.length == 1
    Then match response contains {"id": "#present" , "caseDefinitionId": "#present" , "businessKey": "#present" , "active":"true" , "terminated": "false" , "completed": "false"}
    #To test get case instance by mrn API when there is single case instance present for the deployment in Camunda.
    Given url baseUrl
    And path '/case-instance'
    And param mrn = mrn
    And header Authorization = 'Bearer ' + accessToken
    And header 'Content-Type' = 'application/json'
    And request {}
    When method get
    Then status 200
    Then assert response.length == 1
    Then match response contains {"id": "#present" , "caseDefinitionId": "#present" , "businessKey": "#present" , "active":"true" , "terminated": "false" , "completed": "false"}
    #terminate the created case instance
    Given url baseUrl
    And path '/case-instance/terminate'
    And param mrn = mrn
    And param programId = "P1"
    And header Authorization = 'Bearer ' + accessToken
    And header 'Content-Type' = 'application/json'
    And request {}
    When method post
    Then status 200
    #To test get all case instances API when there is terminated case instance is present for deployment in Camunda.
    Given url baseUrl
    And path '/case-instance'
    And header Authorization = 'Bearer ' + accessToken
    And header 'Content-Type' = 'application/json'
    And request {}
    When method get
    Then status 200
    Then assert response.length == 1
    Then match response contains {"id": "#present" , "caseDefinitionId": "#present" , "businessKey": "#present" , "active":"false" , "terminated": "true" , "completed": "false"}
    #To test get case instances by Id API when there is terminated case instance is present for the deployment in Camunda.
    Given url baseUrl
    And path '/case-instance'
    And param caseInstanceId = CaseInstanceId
    And header Authorization = 'Bearer ' + accessToken
    And header 'Content-Type' = 'application/json'
    And request {}
    When method get
    Then status 200
    Then match response contains {"id": "#present" , "caseDefinitionId": "#present" , "businessKey": "#present" , "active":"false" , "terminated": "true" , "completed": "false"}
    Then assert response.length == 1
    #To test get case instances by mrn API when there is terminated case instance is present for the deployment in Camunda.
    Given url baseUrl
    And path '/case-instance'
    And param mrn = mrn
    And header Authorization = 'Bearer ' + accessToken
    And header 'Content-Type' = 'application/json'
    And request {}
    When method get
    Then status 200
    Then assert response.length == 1
    Then match response contains {"id": "#present" , "caseDefinitionId": "#present" , "businessKey": "#present" , "active":"false" , "terminated": "true" , "completed": "false"}
    #To close the terminated case instance
    Given url baseUrl
    And path '/case-instance/close'
    And param mrn = mrn
    And param programId = "P1"
    And header Authorization = 'Bearer ' + accessToken
    And header 'Content-Type' = 'application/json'
    And request {}
    When method post
    Then status 200
    #To test get all case instances API when there is closed case instance is present in Camunda.
    Given url baseUrl
    And path '/case-instance'
    And header Authorization = 'Bearer ' + accessToken
    And header 'Content-Type' = 'application/json'
    And request {}
    When method get
    Then status 200
    Then match response == []
    #To test get case instances by Id API when there is closed case instance is present in Camunda.
    Given url baseUrl
    And path '/case-instance'
    And param caseInstanceId = CaseInstanceId
    And header Authorization = 'Bearer ' + accessToken
    And header 'Content-Type' = 'application/json'
    And request {}
    When method get
    Then status 200
    Then match response == []
    #To test get case instances by mrn API when there is closed case instance is present in Camunda.
    Given url baseUrl
    And path '/case-instance'
    And param mrn = mrn
    And header Authorization = 'Bearer ' + accessToken
    And header 'Content-Type' = 'application/json'
    And request {}
    When method get
    Then status 200
    Then match response == []

  Scenario Outline: : To create 2 case instances for the deployment
    Given url baseUrl
    And path 'case-definition/create'
    And header Authorization = 'Bearer ' + accessToken
    And header 'Content-Type' = 'application/json'
    And def req = read('CreateCaseReq.json')
    And set req.mrn = "test001"
    And set req.programId = "<program>"
    And request req
    When method post
    Then status 200

    Examples: 
      | program |
      | P1      |
      | P2      |

  Scenario: To test get case instances API's (all, by id, by mrn) when there are 2 case instances present for the deployment in Camunda.
    #To test get all case instances API when there are 2 case instances present for the deployment in Camunda.
    Given url baseUrl
    And path '/case-instance'
    And header Authorization = 'Bearer ' + accessToken
    And header 'Content-Type' = 'application/json'
    And request {}
    When method get
    Then status 200
    Then match each response contains {"id": "#present" , "caseDefinitionId": "#present" , "businessKey": "#present" , "active":"true" , "terminated": "false" , "completed": "false"}
    Then assert response.length == 2
    Then def CaseInstanceId = response[0].id
    Then def mrn = response[0].businessKey
    #To test get case instance by id API when there are 2 case instances present for the deployment in Camunda.
    Given url baseUrl
    And path '/case-instance'
    And param caseInstanceId = CaseInstanceId
    And header Authorization = 'Bearer ' + accessToken
    And header 'Content-Type' = 'application/json'
    And request {}
    When method get
    Then status 200
    Then assert response.length == 1
    Then match response contains {"id": "#present" , "caseDefinitionId": "#present" , "businessKey": "#present" , "active":"true" , "terminated": "false" , "completed": "false"}
    #To test get case instance by mrn API when there are 2 case instances present for the deployment in Camunda.
    Given url baseUrl
    And path '/case-instance'
    And param mrn = mrn
    And header Authorization = 'Bearer ' + accessToken
    And header 'Content-Type' = 'application/json'
    And request {}
    When method get
    Then status 200
    Then assert response.length == 2
    Then match response contains {"id": "#present" , "caseDefinitionId": "#present" , "businessKey": "#present" , "active":"true" , "terminated": "false" , "completed": "false"}

  Scenario Outline: creating multiple case instances for one program
    Given url baseUrl
    And path 'case-definition/create'
    And header Authorization = 'Bearer ' + accessToken
    And header 'Content-Type' = 'application/json'
    And def req = read('CreateCaseReq.json')
    And set req.mrn = "<mrn>"
    And set req.programId = "<program>"
    And request req
    When method post
    Then status 200

    Examples: 
      | mrn     | program |
      | test002 | P1      |
      | test003 | P3      |

  Scenario: To test get all case instances API when there are more than one case instances(3) are  present for than one mrn in Camunda.
    Given url baseUrl
    And path '/case-instance'
    And header Authorization = 'Bearer ' + accessToken
    And header 'Content-Type' = 'application/json'
    And request {}
    When method get
    Then status 200
    Then assert response.length == 4
    Then match each response contains {"id": "#present" , "caseDefinitionId": "#present" , "businessKey": "#present" , "active":"true" , "terminated": "false" , "completed": "false"}
    Then def CaseInstanceId = response[0].id
    Then def mrn = response[0].businessKey
    #To test get case instance by id API when there are more than one case instances present for more than one program in Camunda.
    Given url baseUrl
    And path '/case-instance'
    And param caseInstanceId = CaseInstanceId
    And header Authorization = 'Bearer ' + accessToken
    And header 'Content-Type' = 'application/json'
    And request {}
    When method get
    Then status 200
    Then assert response.length == 1
    Then match response contains {"id": "#present" , "caseDefinitionId": "#present" , "businessKey": "#present" , "active":"true" , "terminated": "false" , "completed": "false"}
    #To test get case instance by mrn API when there are more than one case instances present for more than one program in Camunda.
    Given url baseUrl
    And path '/case-instance'
    And param mrn = mrn
    And header Authorization = 'Bearer ' + accessToken
    And header 'Content-Type' = 'application/json'
    And request {}
    When method get
    Then status 200
    Then match response contains {"id": "#present" , "caseDefinitionId": "#present" , "businessKey": "#present" , "active":"true" , "terminated": "false" , "completed": "false"}
    Then assert response.length == 2

  Scenario: To test get all deployments API is fetching the correct details when more than one (3) deployments are present on the server
    Given url baseUrl
    And path '/deployment'
    And header Authorization = 'Bearer ' + accessToken
    And header 'Content-Type' = 'application/json'
    And request {}
    When method get
    Then status 200
    Then assert response.length == 3
    Then match each response contains {"id":"#present"}
    Then def deployId1 = response[0].id
    Then def deployId2 = response[1].id
    Then def deployId3 = response[2].id
    #To test delete deployment API when there are 3 deployments are present on the server
    Given url baseUrl
    And path '/deployment/' + deployId1
    And param cascade = true
    And header Authorization = 'Bearer ' + accessToken
    And header 'Content-Type' = 'application/json'
    And request {}
    When method delete
    Then status 200
    #To check if correct deployment is deleted
    Given url baseUrl
    And path '/deployment'
    And header Authorization = 'Bearer ' + accessToken
    And header 'Content-Type' = 'application/json'
    And request {}
    When method get
    Then status 200
    Then assert response.length == 2

  Scenario: To test get all deployments API is fetching the correct details when more than one (2) deployments are present on the server
    Given url baseUrl
    And path '/deployment'
    And header Authorization = 'Bearer ' + accessToken
    And header 'Content-Type' = 'application/json'
    And request {}
    When method get
    Then status 200
    Then match each response contains {"id":"#present"}
    Then assert response.length == 2
    Then def deployId1 = response[0].id
    Then def deployId2 = response[1].id
    #To test delete deployment API when there are 2 deployments are present on the server
    Given url baseUrl
    And path '/deployment/' + deployId1
    And param cascade = true
    And header Authorization = 'Bearer ' + accessToken
    And header 'Content-Type' = 'application/json'
    And request {}
    When method delete
    Then status 200
    #To check if correct deployment is deleted
    Given url baseUrl
    And path '/deployment'
    And header Authorization = 'Bearer ' + accessToken
    And header 'Content-Type' = 'application/json'
    And request {}
    When method get
    Then status 200
    Then assert response.length == 1

  Scenario: To test get all deployments API is fetching the correct details when single deployment is present on the server
    Given url baseUrl
    And path '/deployment'
    And header Authorization = 'Bearer ' + accessToken
    And header 'Content-Type' = 'application/json'
    And request {}
    When method get
    Then status 200
    Then assert response.length == 1
    Then match each response contains {"id":"#present"}
    Then def deployId1 = response[0].id
    #To fetch all todo task of the case instance for the deployment present on the server and complete the task
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
    Then def CaseId = response[0].id
    # complete the task which is in TODO List
    Given url baseUrl
    And path '/tasks/' +  CaseId + '/complete'
    And header Authorization = 'Bearer ' + accessToken
    And header 'Content-Type' = 'application/json'
    And request {}
    When method post
    Then status 200

  Scenario: To test get all deployments API, Get case instances API(all, by Id, By mrn) when deployment is present on the server and there are no goals/tasks in the cmmn.
    Given url baseUrl
    And path '/deployment'
    And header Authorization = 'Bearer ' + accessToken
    And header 'Content-Type' = 'application/json'
    And request {}
    When method get
    Then status 200
    Then assert response.length == 1
    Then match each response contains {"id":"#present"}
    Then def deployId1 = response[0].id
    #To test get all case instances API when case intance is present in Camunda and all the goals/tasks for that case instances are completed
    Given url baseUrl
    And path '/case-instance'
    And header Authorization = 'Bearer ' + accessToken
    And header 'Content-Type' = 'application/json'
    And request {}
    When method get
    Then status 200
    Then assert response.length == 1
    Then match each response contains {"id": "#present" , "caseDefinitionId": "#present" , "businessKey": "#present" , "active":"false" , "terminated": "false" , "completed": "true"}
    Then def CaseInstanceId = response[0].id
    Then def mrn = response[0].businessKey
    #To test get case instance by id API when case intance is present in Camunda and all the goals/tasks for that case instances are completed
    Given url baseUrl
    And path '/case-instance'
    And param caseInstanceId = CaseInstanceId
    And header Authorization = 'Bearer ' + accessToken
    And header 'Content-Type' = 'application/json'
    And request {}
    When method get
    Then status 200
    Then assert response.length == 1
    Then match response contains {"id": "#present" , "caseDefinitionId": "#present" , "businessKey": "#present" , "active":"false" , "terminated": "false" , "completed": "true"}
    #To test get case instance by mrn API when case intance is present in Camunda and all the goals/tasks for that case instances are completed
    Given url baseUrl
    And path '/case-instance'
    And param mrn = mrn
    And header Authorization = 'Bearer ' + accessToken
    And header 'Content-Type' = 'application/json'
    And request {}
    When method get
    Then status 200
    Then assert response.length == 1
    Then match response contains {"id": "#present" , "caseDefinitionId": "#present" , "businessKey": "#present" , "active":"false" , "terminated": "false" , "completed": "true"}
    #To test delete deployment API when deployment is present on the server and there are no goals/tasks in the cmmn.
    Given url baseUrl
    And path '/deployment/' + deployId1
    And param cascade = true
    And header Authorization = 'Bearer ' + accessToken
    And header 'Content-Type' = 'application/json'
    And request {}
    When method delete
    Then status 200
    #To check if correct deployment is deleted
    Given url baseUrl
    And path '/deployment'
    And header Authorization = 'Bearer ' + accessToken
    And header 'Content-Type' = 'application/json'
    And request {}
    When method get
    Then status 200
    Then assert response.length == 0

  Scenario: To test get all deployments API when no deployment is present on the server
    Given url baseUrl
    And path '/deployment'
    And header Authorization = 'Bearer ' + accessToken
    And header 'Content-Type' = 'application/json'
    And request {}
    When method get
    Then status 200
    Then match response == []

  Scenario: To test get all deployments API when invalid parameter is sent in the URL
    Given url baseUrl
    And path '/deployment/invalid/test'
    And header Authorization = 'Bearer ' + accessToken
    And header 'Content-Type' = 'application/json'
    And request {}
    When method get
    Then status 404
    Then match response.error == "Not Found"

  Scenario: To test get all deployments API is getting failed with reason "Method Not Allowed" when wrong method name is sent in the request.
    Given url baseUrl
    And path '/deployment'
    And header Authorization = 'Bearer ' + accessToken
    And header 'Content-Type' = 'application/json'
    And request {}
    When method post
    Then status 405
    Then match response.title == "Method Not Allowed"

  Scenario: To test delete deployment API when invalid parameter is sent in the URL
    Given url baseUrl
    And path '/deploymentsss/08c1a96f-1333-11e9-adcc-18cf5eb265d1'
    And header Authorization = 'Bearer ' + accessToken
    And header 'Content-Type' = 'application/json'
    And request {}
    When method delete
    Then status 404
    Then match response.error == "Not Found"

  Scenario: To test delete deployment API is getting failed with reason "Method Not Allowed" when wrong method name is sent in the request.
    Given url baseUrl
    And path '/deployment/08c1a96f-1333-11e9-adcc-18cf5eb265d1'
    And header Authorization = 'Bearer ' + accessToken
    And header 'Content-Type' = 'application/json'
    And request {}
    When method post
    Then status 405
    Then match response.title == "Method Not Allowed"

  Scenario: To test get all case instances API when invalid parameter is sent in the URL.
    Given url baseUrl
    And path '/case-instancess'
    And header Authorization = 'Bearer ' + accessToken
    And header 'Content-Type' = 'application/json'
    And request {}
    When method get
    Then status 404
    Then match response.error == "Not Found"

  Scenario: To test get all case instances API is getting failed with reason "Method Not Allowed" when wrong method name is sent in the request.
    Given url baseUrl
    And path '/case-instance'
    And header Authorization = 'Bearer ' + accessToken
    And header 'Content-Type' = 'application/json'
    And request {}
    When method post
    Then status 405
    Then match response.title == "Method Not Allowed"

  Scenario: To test get case instance By Id API when invalid parameter is sent in the URL
    Given url baseUrl
    And path '/case-instancessss/0047553d-1498-11e9-9ea1-18cf5eb265d1'
    And header Authorization = 'Bearer ' + accessToken
    And header 'Content-Type' = 'application/json'
    And request {}
    When method get
    Then status 404
    Then match response.error == "Not Found"

  Scenario: To test get case instance By mrn API when invalmrn parameter is sent in the URL
    Given url baseUrl
    And path '/case-instancessss/0047553d-1498-11e9-9ea1-18cf5eb265d1'
    And header Authorization = 'Bearer ' + accessToken
    And header 'Content-Type' = 'application/json'
    And request {}
    When method get
    Then status 404
    Then match response.error == "Not Found"
