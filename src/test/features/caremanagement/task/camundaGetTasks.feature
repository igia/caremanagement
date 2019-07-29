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

Feature: Getlist of tasks using camunda 

    Background:
    * def signIn = call read('classpath:login.feature')
    * def accessToken = signIn.accessToken


    Scenario: Fetch task with Capture blood pressure reading

        Given url camundaUrl
        And path '/task'
        And header Authorization = 'Bearer ' + accessToken
        When method get
        Then status 200

        * def findBpTask = response
        * def task = get[0] findBpTask[?(@.name == "Capture blood pressure reading ")]