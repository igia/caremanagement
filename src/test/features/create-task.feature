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

@ignore
Feature: Test the creation of task entities
    Background:
    * def signIn = call read('classpath:login.feature')
    * def accessToken = signIn.accessToken
    Scenario: Test the creation of task for particular request
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
            "assignee": "#(assignee)",
            "description": "#(description)",
            "dueDate": "#(dueDate)",
            "name": "#(name)",
            "repeatFrequencyValue": "#(repeatFrequencyValue)",
            "repeatFrequencyUnit": "#(repeatFrequencyUnit)",
            "repeatEvent": "#(repeatEvent)",
            "taskIdU": "#(taskIdU)",
            "type": "#(taskType)",
            "typeRef": "#(typeRef)",
            "goalId" : "#(goalId)",
            "sla" : "#(sla)",
            "repeatEvent" : "#(repeatEvent)",
            "isRepeat" : true,
            "lookup" : "#(lookup)"
            } 
            """
        When method post
        Then status 201