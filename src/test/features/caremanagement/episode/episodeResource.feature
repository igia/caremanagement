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

Feature: Test CURD operations on episode resource

    Background:
    * def signIn = call read('classpath:login.feature')
    * def accessToken = signIn.accessToken

    Scenario: Test creation of program resource and then creating a episode
        Given url baseUrl
        And path '/definitions/programs'
        And header Authorization = 'Bearer ' + accessToken
        And header 'Content-Type' = 'application/json'
        And request {"description": "Program for BP",  "programIdU": "BP_Analyze",  "name": "BP_Analyze"}
        When method post
        Then status 201
        Then match response.description == "Program for BP"
        Then match response.programIdU == "BP_Analyze"
        Then match response.name == "BP_Analyze"
        Then match response.id == "##notnull"
        Then def programId = get response.id

        Given url baseUrl
        And path '/definitions/episodes'
        And header Authorization = 'Bearer ' + accessToken
        And header 'Content-Type' = 'application/json'
        And request {"description": "Episode Testing","episodeIdU": "Episode1","name": "Test Episode","programId": '#(programId)',"lookup" : "Episode2"}
        When method post
        Then status 201
        Then match response.description == "Episode Testing"
        Then match response.episodeIdU == "Episode1"
        Then match response.name == "Test Episode"
        Then match response.programId == programId
        Then match response.lookup == "##notnull"
        Then def respId = response.id

        # Get created episode
        Given url baseUrl
        And path '/definitions/episodes/' + respId
        And header Authorization = 'Bearer ' + accessToken
        When method get
        Then status 200
        Then match response.description == "Episode Testing"
        Then match response.episodeIdU == "Episode1"
        Then match response.name == "Test Episode"
        Then match response.programId == programId
        Then match response.lookup == "##notnull"
        Then match response.id == respId

        # Get all episodes
        Given url baseUrl
        And path '/definitions/episodes'
        And header Authorization = 'Bearer ' + accessToken
        When method get
        Then status 200
        Then match response[0].description == "Episode Testing"
        Then match response[0].episodeIdU == "Episode1"
        Then match response[0].name == "Test Episode"
        Then match response[0].programId == programId
        Then match response[0].lookup == "##notnull"
        Then match response[0].id == respId
        Then match header X-Total-Count == '1'

    Scenario: Test creation of another episode resource without description
        Given url baseUrl
        And path '/definitions/programs'
        And header Authorization = 'Bearer ' + accessToken
        When method get
        Then status 200
        Then def programId = response[0].id

        Given url baseUrl
        And path '/definitions/episodes'
        And header Authorization = 'Bearer ' + accessToken
        And header 'Content-Type' = 'application/json'
        And request {"episodeIdU": "Episode2","name": "Test Episode2","programId": '#(programId)',"lookup" : "Episode3"}
        When method post
        Then status 201
        Then match response.episodeIdU == "Episode2"
        Then match response.name == "Test Episode2"
        Then match response.programId == programId
        Then match response.lookup == "##notnull"
        Then def respId = response.id

        # Get created episode
        Given url baseUrl
        And path '/definitions/episodes/' + respId
        And header Authorization = 'Bearer ' + accessToken
        When method get
        Then status 200
        Then match response.episodeIdU == "Episode2"
        Then match response.name == "Test Episode2"
        Then match response.programId == programId
        Then match response.lookup == "##notnull"
        Then match response.id == respId

        # Get all episodes
        Given url baseUrl
        And path '/definitions/episodes'
        And header Authorization = 'Bearer ' + accessToken
        When method get
        Then status 200
        Then match header X-Total-Count == '2'

    Scenario: Test recreating episode resource same payload as above
        Given url baseUrl
        And path '/definitions/programs'
        And header Authorization = 'Bearer ' + accessToken
        When method get
        Then status 200
        Then def programId = response[0].id

        Given url baseUrl
        And path '/definitions/episodes'
        And header Authorization = 'Bearer ' + accessToken
        And header 'Content-Type' = 'application/json'
        And request {"episodeIdU": "Episode1","name": "Test Episode","programId": '#(programId)',"lookup" : "Episode4"}
        When method post
        Then status 500
        Then match response.type == "https://www.jhipster.tech/problem/problem-with-message"
        Then match response.title == "Internal Server Error"
        Then match response.detail contains "could not execute statement; SQL [n/a]; constraint [unique_episode]; nested exception is org.hibernate.exception.ConstraintViolationException: could not execute statement"


    Scenario: Test recreating episode resource same payload as above but different parent
        Given url baseUrl
        And path '/definitions/programs'
        And header Authorization = 'Bearer ' + accessToken
        And header 'Content-Type' = 'application/json'
        And request {"description": "Program for BP2",  "programIdU": "BP_Analyze2",  "name": "BP_Analyze2"}
        When method post
        Then status 201
        Then match response.description == "Program for BP2"
        Then match response.programIdU == "BP_Analyze2"
        Then match response.name == "BP_Analyze2"
        Then match response.id == "##notnull"
        Then def programId = get response.id

        Given url baseUrl
        And path '/definitions/episodes'
        And header Authorization = 'Bearer ' + accessToken
        And header 'Content-Type' = 'application/json'
        And request {"description": "Episode Testing","episodeIdU": "Episode1","name": "Test Episode","programId": '#(programId)',"lookup" : "Episode18"}
        When method post
        Then status 201
        Then match response.episodeIdU == "Episode1"
        Then match response.name == "Test Episode"
        Then match response.programId == programId
        Then match response.lookup == "##notnull"
        Then match response.description == "Episode Testing"

    Scenario:Test creating episode resource without programId should fail with appropriate error
        Given url baseUrl
        And path '/definitions/episodes'
        And header Authorization = 'Bearer ' + accessToken
        And header 'Content-Type' = 'application/json'
        And request {"description": "Episode Testing4","episodeIdU": "Episode4","name": "Test Episode3"}
        When method post
        Then status 400

    Scenario:Test creating episode resource without name should fail with appropriate error
        Given url baseUrl
        And path '/definitions/programs'
        And header Authorization = 'Bearer ' + accessToken
        When method get
        Then status 200
        Then def programId = response[0].id

        Given url baseUrl
        And path '/definitions/episodes'
        And header Authorization = 'Bearer ' + accessToken
        And header 'Content-Type' = 'application/json'
        And request {"description": "Episode Testing3","episodeIdU": "Episode3","programId": '#(programId)',"lookup" : "Episode19"}
        When method post
        Then status 400

    Scenario:Test creating episode resource without episodeIdU should fail with appropriate error
        Given url baseUrl
        And path '/definitions/programs'
        And header Authorization = 'Bearer ' + accessToken
        When method get
        Then status 200
        Then def programId = response[0].id

        Given url baseUrl
        And path '/definitions/episodes'
        And header Authorization = 'Bearer ' + accessToken
        And header 'Content-Type' = 'application/json'
        And request {"description": "Episode Testing5","name": "Test Episode5","programId": '#(programId)',"lookup" : "Episode20"}
        When method post
        Then status 400

    Scenario:Test creating episode resource with invalid programId should fail with appropriate error
        Given url baseUrl
        And path '/definitions/episodes'
        And header Authorization = 'Bearer ' + accessToken
        And header 'Content-Type' = 'application/json'
        And request {"description": "Episode Testing6","episodeIdU": "Episode6","name": "Test Episode6", "programId": 1,"lookup" : "Episode21"}
        When method post
        Then status 500

    Scenario:Test fetching of episodes by invalid program id
        Given url baseUrl
        And path '/definitions/episodes/1'
        And header Authorization = 'Bearer ' + accessToken
        When method get
        Then status 404

    Scenario:Test Update of episodes for valid program id and valid episode id
        #Get the program created
        Given url baseUrl
        And path '/definitions/programs'
        And header Authorization = 'Bearer ' + accessToken
        When method get
        Then status 200
        Then def programId = get response[0].id

        #Get the episode created
        Given url baseUrl
        And path '/definitions/episodes'
        And header Authorization = 'Bearer ' + accessToken
        When method get
        Then status 200
        Then def episodeId = get response[0].id

        Given url baseUrl
        And path '/definitions/episodes'
        And header Authorization = 'Bearer ' + accessToken
        And header 'Content-Type' = 'application/json'
        And request {"description": "Episode Testing1234","episodeIdU": "Episode8","name": "Test Episode","programId": '#(programId)',"id" : '#(episodeId)',"entryCriteria": "Entry","lookup" : "Episode22"}
        When method put
        Then status 200

        Given url baseUrl
        And path '/definitions/episodes/' + episodeId
        And header Authorization = 'Bearer ' + accessToken
        When method get
        Then status 200
        Then match response.description == "Episode Testing1234"
        Then match response.episodeIdU == "Episode8"
        Then match response.name == "Test Episode"
        Then match response.programId == programId
        Then match response.entryCriteria == "Entry"
        Then match response.id == episodeId
    
    Scenario:Test Update of episodes for valid program id and valid episode id
        #Get the program created
        Given url baseUrl
        And path '/definitions/programs'
        And header Authorization = 'Bearer ' + accessToken
        When method get
        Then status 200
        Then def programId = get response[0].id

        Given url baseUrl
        And path '/definitions/episodes'
        And header Authorization = 'Bearer ' + accessToken
        And header 'Content-Type' = 'application/json'
        And request {"description": "Episode Testing1234","episodeIdU": "Episode8","name": "Test Episode","programId": '#(programId)',"id" : 1,"entryCriteria": "Entry","lookup" : "Episode22"}
        When method put
        Then status 400
        Then match response.message == "Episode does not exist"
        
    Scenario:Test delete episode 
        Given url baseUrl
        And path '/definitions/episodes'
        And header Authorization = 'Bearer ' + accessToken
        When method get
        Then status 200
        Then def episodesId = get response[0].id

        Given url baseUrl
        And path '/definitions/episodes/' + episodesId
        And header Authorization = 'Bearer ' + accessToken
        When method delete
        Then status 200

        Given url baseUrl
        And path '/definitions/episodes'
        And header Authorization = 'Bearer ' + accessToken
        When method get
        Then match response[*].id != episodesId

    Scenario: Delete the rest for the episodes created 
        Given url baseUrl
        And path '/definitions/episodes'
        And header Authorization = 'Bearer ' + accessToken
        When method get
        Then status 200
        Then def episodeIdArray = response
        Then def runDelete = call read('classpath:deleteEpisode.feature') episodeIdArray 

        Given url baseUrl
        And path '/definitions/programs'
        And header Authorization = 'Bearer ' + accessToken
        When method get
        Then status 200
        Then def programId = response
        Then def runDeleteProgram = call read('classpath:deleteProgram.feature') programId 

    Scenario:Test delete form for invalid episodes id
        Given url baseUrl
        And path '/definitions/episodes/1'
        And header Authorization = 'Bearer ' + accessToken
        When method delete
        Then status 500