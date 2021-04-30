// Copyright (c) Microsoft Corporation. All rights reserved.
// Licensed under the MIT License.

package com.microsoft.azuresamples.msal4j.msidentityspringbootwebapi;

import java.util.Date;
import org.springframework.security.oauth2.server.resource.authentication.BearerTokenAuthentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class SampleController {

    @GetMapping("/api/date")
    @ResponseBody
    // @PreAuthorize("hasAuthority('SCOPE_access_as_user')")
    public String date(BearerTokenAuthentication btAuth) {
        return new DateResponse().toString();
    }

    private class DateResponse {
        private String humanReadable;
        private String timeStamp;

        public DateResponse() {
            Date now = new Date();
            this.humanReadable = now.toString();
            this.timeStamp = Long.toString(now.getTime());
        }

        public String toString() {
            return String.format("{\"humanReadable\": \"%s\", \"timeStamp\": \"%s\"}", humanReadable, timeStamp);
        }
    }
}
