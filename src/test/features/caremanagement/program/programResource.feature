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

Feature: Test CURD operations on program resource

    Background:
    * def signIn = call read('classpath:login.feature')
    * def accessToken = signIn.accessToken

    Scenario: Test creation of program resource
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
        Then def respId = get response.id

        # Get created program
        Given url baseUrl
        And path '/definitions/programs/' + respId
        And header Authorization = 'Bearer ' + accessToken
        When method get
        Then status 200
        Then match response.description == "Program to analyze the BP"
        Then match response.programIdU == "BP_Assessment"
        Then match response.name == "BP_Assessment"
        Then match response.id == respId

        # Get all programs
        Given url baseUrl
        And path '/definitions/programs'
        And header Authorization = 'Bearer ' + accessToken
        When method get
        Then status 200
        Then match response[0].description == "Program to analyze the BP"
        Then match response[0].programIdU == "BP_Assessment"
        Then match response[0].name == "BP_Assessment"
        Then match response[0].id == respId
        Then match header X-Total-Count == '1'

    Scenario: Test creation of another program resource without description
        Given url baseUrl
        And path '/definitions/programs'
        And header Authorization = 'Bearer ' + accessToken
        And header 'Content-Type' = 'application/json'
        And request {"programIdU": "HR_Assessment","name": "HR_Assessment"}
        When method post
        Then status 201
        Then match response.programIdU == "HR_Assessment"
        Then match response.name == "HR_Assessment"
        Then match response.id == "##notnull"
        Then def respId = response.id

        # Get created program
        Given url baseUrl
        And path '/definitions/programs/' + respId
        And header Authorization = 'Bearer ' + accessToken
        When method get
        Then status 200
        Then match response.programIdU == "HR_Assessment"
        Then match response.name == "HR_Assessment"
        Then match response.id == respId

        # Get all programs
        Given url baseUrl
        And path '/definitions/programs'
        And header Authorization = 'Bearer ' + accessToken
        When method get
        Then status 200
        Then match header X-Total-Count == '2'

    Scenario: Test recreating program resource same payload as above
        Given url baseUrl
        And path '/definitions/programs'
        And header Authorization = 'Bearer ' + accessToken
        And header 'Content-Type' = 'application/json'
        And request {"description": "Program to analyze the HR","programIdU": "HR_Assessment","name": "HR_Assessment"}
        When method post
        Then status 500
        Then match response.type == "https://www.jhipster.tech/problem/problem-with-message"
        Then match response.title == "Internal Server Error"
        Then match response.detail contains "could not execute statement; SQL [n/a]; constraint [unique_program]; nested exception is org.hibernate.exception.ConstraintViolationException: could not execute statement"

    Scenario: Test recreating program resource without programIdU
        Given url baseUrl
        And path '/definitions/programs'
        And header Authorization = 'Bearer ' + accessToken
        And header 'Content-Type' = 'application/json'
        And request {"description": "Program to analyze the PR1","name": "PR_Assessment1"}
        When method post
        Then status 400
        Then match response.fieldErrors[0].field == "programIdU"
        Then match response.fieldErrors[0].message == "NotNull"

    Scenario: Test recreating program resource without name
        Given url baseUrl
        And path '/definitions/programs'
        And header Authorization = 'Bearer ' + accessToken
        And header 'Content-Type' = 'application/json'
        And request {"description": "Program to analyze the PR","programIdU": "PR_Assessment"}
        When method post
        Then status 400
        Then match response.fieldErrors[0].field == "name"
        Then match response.fieldErrors[0].message == "NotNull"

    Scenario:Test fetching of program by invalid program id
        Given url baseUrl
        And path '/definitions/programs/1'
        And header Authorization = 'Bearer ' + accessToken
        When method get
        Then status 404

    Scenario:Test Update of program for valid program id
        #Get the program created
        Given url baseUrl
        And path '/definitions/programs'
        And header Authorization = 'Bearer ' + accessToken
        When method get
        Then status 200
        Then def programId = get response[0].id

        Given url baseUrl
        And path '/definitions/programs'
        And header Authorization = 'Bearer ' + accessToken
        And header 'Content-Type' = 'application/json'
        And request {"description": "Program to analyze BP",  "programIdU": "BP_Check",  "name": "BP_Assessment","id": '#(programId)'}
        When method put
        Then status 200

        Given url baseUrl
        And path '/definitions/programs/' + programId
        And header Authorization = 'Bearer ' + accessToken
        When method get
        Then status 200
        Then match response.description == "Program to analyze BP"
        Then match response.programIdU == "BP_Check"
        Then match response.name == "BP_Assessment"
        Then match response.id == programId

    Scenario: Test Update of program for invalid program id
        Given url baseUrl
        And path '/definitions/programs'
        And header Authorization = 'Bearer ' + accessToken
        And header 'Content-Type' = 'application/json'
        And request {"description": "Program to analyze potassium",  "programIdU": "K_Assessment",  "name": "K_Assessment", "id" : "1" }
        When method put
        Then status 400
        Then match response.message == "Program does not exist"

    Scenario: Test Update of program to set null program idU
        Given url baseUrl
        And path '/definitions/programs'
        And header Authorization = 'Bearer ' + accessToken
        When method get
        Then status 200
        Then def programId = get response[0].id

        Given url baseUrl
        And path '/definitions/programs'
        And header Authorization = 'Bearer ' + accessToken
        And header 'Content-Type' = 'application/json'
        And request {"description": "Program to analyze potassium",  "programIdU": null,  "name": "K_Assessment", "id": '#(programId)'}
        When method put
        Then status 400
        Then match response.fieldErrors[0].field == "programIdU"
        Then match response.fieldErrors[0].message == "NotNull"

    Scenario:Test delete programs 
        Given url baseUrl
        And path '/definitions/programs'
        And header Authorization = 'Bearer ' + accessToken
        When method get
        Then status 200
        Then def programsId = get response[0].id

        Given url baseUrl
        And path '/definitions/programs/' + programsId
        And header Authorization = 'Bearer ' + accessToken
        When method delete
        Then status 200

        Given url baseUrl
        And path '/definitions/programs'
        And header Authorization = 'Bearer ' + accessToken
        When method get
        Then match response[*].id != programsId

    Scenario: Delete the rest of the programs
        Given url baseUrl
        And path '/definitions/programs'
        And header Authorization = 'Bearer ' + accessToken
        When method get
        Then status 200
        Then def pgIdArray = response
        Then def runDelete = call read('classpath:deleteProgram.feature') pgIdArray   

    Scenario:Test delete program for invalid form id
        Given url baseUrl
        And path '/definitions/programs/1'
        And header Authorization = 'Bearer ' + accessToken
        When method delete
        Then status 500
        Then match response.detail == "No class io.igia.caremanagement.domain.Program entity with id 1 exists!"
