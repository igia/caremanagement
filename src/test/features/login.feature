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

Feature: Login to OIDC server using client credentials

    Scenario: Get access token using client credentials

        Given url tokenUrl
        And form field grant_type = 'client_credentials'
        And header Authorization = call read('classpath:basic-auth.js') { username: '#(clientId)', password: '#(clientSecret)' }
        And header 'Content-Type' = 'application/x-www-form-urlencoded'
        When method post
        Then status 200
        Then match response ==
        """
        {
            "access_token":"#present",
            "expires_in": '#number',
            "refresh_expires_in":'#number',
            "refresh_token":"#present",
            "token_type":"bearer",
            "not-before-policy":0,
            "session_state":"#uuid",
            "scope":"email profile"
        }
        """
        And def accessToken = response.access_token
