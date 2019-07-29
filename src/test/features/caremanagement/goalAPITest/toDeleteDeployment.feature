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

Feature: To delete deployed CMMN and entities
  I want to delete all deployed CMMN and entities

  Background: 
    * def signIn = call read('classpath:login.feature')
    * def accessToken = signIn.accessToken

  Scenario: To delete the deployment from server
    #To fetch the deployment from server
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
    # To delete the deployment from server
    Given url baseUrl
    And path '/deployment/' + deployId1
    And param cascade = true
    And header Authorization = 'Bearer ' + accessToken
    And header 'Content-Type' = 'application/json'
    And request {}
    When method delete
    Then status 200

  Scenario: Test delete task associates resource
    Given url baseUrl
    And path '/definitions/task-associates'
    And header Authorization = 'Bearer ' + accessToken
    When method get
    Then status 200
    Then def resp = response
    Then def functDelete = call read('classpath:deleteTaskAssociate.feature') resp

  Scenario: Test delete task resource
    Given url baseUrl
    And path '/definitions/tasks'
    And header Authorization = 'Bearer ' + accessToken
    When method get
    Then status 200
    Then def resp = response
    Then def functDelete = call read('classpath:deleteTask.feature') resp

  Scenario: Test delete goal associates resource
    Given url baseUrl
    And path '/definitions/goal-associates'
    And header Authorization = 'Bearer ' + accessToken
    When method get
    Then status 200
    Then def resp = response
    Then def functDelete = call read('classpath:deleteGoalAssociate.feature') resp

  Scenario: Test delete goal resource
    Given url baseUrl
    And path '/definitions/goals'
    And header Authorization = 'Bearer ' + accessToken
    When method get
    Then status 200
    Then def resp = response
    Then def functDelete = call read('classpath:deleteGoal.feature') resp

  Scenario: Test delete episode associate resource
    Given url baseUrl
    And path '/definitions/episode-associates'
    And header Authorization = 'Bearer ' + accessToken
    When method get
    Then status 200
    Then def resp = response
    Then def functDelete = call read('classpath:deleteEpisodeAssociate.feature') resp

  Scenario: Test delete episode resource
    Given url baseUrl
    And path '/definitions/episodes'
    And header Authorization = 'Bearer ' + accessToken
    When method get
    Then status 200
    Then def resp = response
    Then def functDelete = call read('classpath:deleteEpisode.feature') resp

  Scenario: Test delete programs resource
    Given url baseUrl
    And path '/definitions/programs'
    And header Authorization = 'Bearer ' + accessToken
    When method get
    Then status 200
    Then def resp = response
    Then def functDelete = call read('classpath:deleteProgram.feature') resp
