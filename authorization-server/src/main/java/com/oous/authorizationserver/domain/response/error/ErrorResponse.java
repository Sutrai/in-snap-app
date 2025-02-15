package com.oous.authorizationserver.domain.response.error;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.oous.authorizationserver.domain.response.Response;
import lombok.AllArgsConstructor;
import lombok.Builder;

@lombok.Data
@Builder
@AllArgsConstructor
public class ErrorResponse implements Response{

    private Data data;
    private boolean informative;
    private StackTraceElement[] stacktrace;
    private boolean success;
    private int status;
}
