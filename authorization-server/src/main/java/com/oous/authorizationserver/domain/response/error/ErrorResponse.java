package com.oous.authorizationserver.domain.response.error;

import com.oous.authorizationserver.domain.response.Response;
import lombok.AllArgsConstructor;
import lombok.Builder;

@lombok.Data
@Builder
@AllArgsConstructor
public class ErrorResponse implements Response{

    private Data data;
    private boolean informative;
    private boolean success;
    private int status;

}
