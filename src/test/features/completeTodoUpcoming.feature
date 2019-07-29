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

Feature: Test the completion of todo and upcoming tasks
    Background:
    * def signIn = call read('classpath:login.feature')
    * def accessToken = signIn.accessToken
    Scenario: Complete the todo and upcoming task
        # Complete the todo tasks
        Given url baseUrl
        And path '/tasks'
        And header Authorization = 'Bearer ' + accessToken
        And header 'Content-Type' = 'application/json'
        And request {"taskCategory": "TODO", "firstResult": "0"}
        When method post
        Then status 200
        Then def resp = response
        Then def completeFunction1 = call read('classpath:completeTasks.feature') resp

        # Complete the upcoming tasks
        Given url baseUrl
        And path '/tasks'
        And header Authorization = 'Bearer ' + accessToken
        And header 'Content-Type' = 'application/json'
        And request {"taskCategory": "UPCOMING", "firstResult": "0"}
        When method post
        Then status 200
        Then def resp2 = response
        Then print resp2
        Then def completeFunction2 = call read('classpath:completeTasks.feature') resp2
