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

Feature: Deploy and create case definition instance in camumda

    Background:
    * def signIn = call read('classpath:login.feature')
    * def accessToken = signIn.accessToken

    Scenario: Test deletion of deployment in loop
        Given url baseUrl
        And path '/deployment/'+ id 
        And param cascade = true
        And header Authorization = 'Bearer ' + accessToken
        When method delete
        Then status 200
