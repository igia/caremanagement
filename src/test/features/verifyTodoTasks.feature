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

Feature: Test the creation of task entities
    Background:
    * def signIn = call read('classpath:login.feature')
    * def accessToken = signIn.accessToken
    
    Scenario: Test structure of the todoTask response
        Given url camundaUrl
        And path '/task/'+ id
        And header Authorization = 'Bearer ' + accessToken
        And header 'Content-Type' = 'application/json'
        And request {}
        When method get
        Then status 200
        Then match response.name == name
        Then match response.assignee == assignee
        Then match response.created == created
        Then match response.due == due
        Then match response.followUp == followUp
        Then match response.taskDefinitionKey == taskDefinitionKey
        Then match response.caseExecutionId == caseExecutionId
        Then match response.caseInstanceId == caseInstanceId
        Then match response.caseDefinitionId == caseDefinitionId
        Then match response.priority == priority
        Then match response.suspended == suspended

