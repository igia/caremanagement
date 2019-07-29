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

Feature: Test CURD operations on episode associate

    Background:
    * def signIn = call read('classpath:login.feature')
    * def accessToken = signIn.accessToken

    Scenario: Test creation of program resource, episode and then creating a episode associate
        # Create program successfully
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

        # Create episodes successfully for the program
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
        Then def respId = response.id

        # Create episodes associate successfully for the episodes
        Given url baseUrl
        And path '/definitions/episode-associates'
        And header Authorization = 'Bearer ' + accessToken
        And header 'Content-Type' = 'application/json'
        And request {"associateEvent": "COMPLETE","associateOn": 123,"episodeId": '#(respId)'}
        When method post
        Then status 201
        Then match response.associateEvent == "COMPLETE"
        Then match response.associateOn == 123
        Then match response.episodeId == respId
        Then match response.id == "##notnull"
        Then def episodeAssociateId = response.id

        # Get created episode associate
        Given url baseUrl
        And path '/definitions/episode-associates/' + episodeAssociateId
        And header Authorization = 'Bearer ' + accessToken
        When method get
        Then status 200
        Then match response.associateEvent == "COMPLETE"
        Then match response.associateOn == 123
        Then match response.episodeId == respId

        # Get all episodes associate
        Given url baseUrl
        And path '/definitions/episode-associates'
        And header Authorization = 'Bearer ' + accessToken
        When method get
        Then status 200
        Then match response[0].associateEvent == "COMPLETE"
        Then match response[0].associateOn == 123
        Then match response[0].episodeId == respId
        Then match header X-Total-Count == '1'


    Scenario: Test recreating episode associate same payload as above
        Given url baseUrl
        And path '/definitions/episodes'
        And header Authorization = 'Bearer ' + accessToken
        When method get
        Then status 200
        Then def episodeId = response[0].id

        Given url baseUrl
        And path '/definitions/episode-associates'
        And header Authorization = 'Bearer ' + accessToken
        And header 'Content-Type' = 'application/json'
        And request {"associateEvent": "COMPLETE","associateOn": 123,"episodeId": '#(episodeId)'}
        When method post
        Then status 201

    Scenario:Test creating episode associates without associateOn should fail with appropriate error
        Given url baseUrl
        And path '/definitions/episodes'
        And header Authorization = 'Bearer ' + accessToken
        When method get
        Then status 200
        Then def episodeId = response[0].id

        Given url baseUrl
        And path '/definitions/episode-associates'
        And header Authorization = 'Bearer ' + accessToken
        And header 'Content-Type' = 'application/json'
        And request {"associateEvent": "COMPLETE","episodeId": '#(episodeId)'}
        When method post
        Then status 500

    Scenario:Test creating episode associates with associateEvent invalid should fail with appropriate error
        Given url baseUrl
        And path '/definitions/episodes'
        And header Authorization = 'Bearer ' + accessToken
        When method get
        Then status 200
        Then def episodeId = response[0].id

        Given url baseUrl
        And path '/definitions/episode-associates'
        And header Authorization = 'Bearer ' + accessToken
        And header 'Content-Type' = 'application/json'
        And request {"associateEvent": "123","episodeId": '#(episodeId)',"associateOn": 123}
        When method post
        Then status 400

    Scenario:Test creating episode associates with invalid episode id should fail with appropriate error
        Given url baseUrl
        And path '/definitions/episode-associates'
        And header Authorization = 'Bearer ' + accessToken
        And header 'Content-Type' = 'application/json'
        And request {"associateEvent": "COMPLETE","associateOn": 123,"episodeId": 1}
        When method post
        Then status 500

    Scenario:Test fetching of episodes associates by invalid program id
        Given url baseUrl
        And path '/definitions/episode-associates/1'
        And header Authorization = 'Bearer ' + accessToken
        When method get
        Then status 404

    Scenario:Test Update of episodes associates for valid program id and valid episode id
        Given url baseUrl
        And path '/definitions/episodes'
        And header Authorization = 'Bearer ' + accessToken
        When method get
        Then status 200
        Then def episodeId = response[0].id

        Given url baseUrl
        And path '/definitions/episode-associates'
        And header Authorization = 'Bearer ' + accessToken
        When method get
        Then status 200
        Then def episodeAssociateId = response[0].id

        Given url baseUrl
        And path '/definitions/episode-associates'
        And header Authorization = 'Bearer ' + accessToken
        And header 'Content-Type' = 'application/json'
        And request {"associateEvent": "COMPLETE","associateOn": 1234,"episodeId": '#(episodeId)',"id": '#(episodeAssociateId)'}
        When method put
        Then status 200
        Then match response.associateOn == 1234
        Then match response.episodeId == episodeId
        Then match response.id == episodeAssociateId
        Then match response.associateEvent == "COMPLETE"

        Given url baseUrl
        And path '/definitions/episode-associates/' + episodeAssociateId
        And header Authorization = 'Bearer ' + accessToken
        When method get
        Then status 200
        Then match response.associateOn == 1234
        Then match response.episodeId == episodeId
        Then match response.id == episodeAssociateId
        Then match response.associateEvent == "COMPLETE"
        
    Scenario:Test Update of episodes associates for valid program id and invalid episode id
        Given url baseUrl
        And path '/definitions/episodes'
        And header Authorization = 'Bearer ' + accessToken
        When method get
        Then status 200
        Then def episodeId = response[0].id

        Given url baseUrl
        And path '/definitions/episode-associates'
        And header Authorization = 'Bearer ' + accessToken
        And header 'Content-Type' = 'application/json'
        And request {"associateEvent": "COMPLETE","associateOn": 1234,"episodeId": '#(episodeId)',"id": 1}
        When method put
        Then status 400
        Then match response.message == "Episode Associate does not exist"

    Scenario:Test delete episode 
        Given url baseUrl
        And path '/definitions/episode-associates'
        And header Authorization = 'Bearer ' + accessToken
        When method get
        Then status 200
        Then def episodesAssociateId = get response[0].id

        Given url baseUrl
        And path '/definitions/episode-associates/' + episodesAssociateId
        And header Authorization = 'Bearer ' + accessToken
        When method delete
        Then status 200

        Given url baseUrl
        And path '/definitions/episode-associates'
        And header Authorization = 'Bearer ' + accessToken
        When method get
        Then match response[*].id != episodesAssociateId

    Scenario: Delete the rest for the episodes associates created 
        Given url baseUrl
        And path '/definitions/episode-associates'
        And header Authorization = 'Bearer ' + accessToken
        When method get
        Then status 200
        Then def episodeAssociateIdArray = response
        Then def runDeleteAssociate = call read('classpath:deleteEpisodeAssociate.feature') episodeAssociateIdArray 

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
        Then def programId = get response[0].id
        And path '/definitions/programs/' + programId
        And header Authorization = 'Bearer ' + accessToken
        When method delete
        Then status 200

    Scenario:Test delete form for invalid episodes Associate id
        Given url baseUrl
        And path '/definitions/episodes/1'
        And header Authorization = 'Bearer ' + accessToken
        When method delete
        Then status 500