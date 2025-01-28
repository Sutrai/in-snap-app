package com.oous.imageservice.domain.response.error;

import com.oous.imageservice.domain.response.Response;
import lombok.AllArgsConstructor;
import lombok.Builder;

@lombok.Data
@Builder
@AllArgsConstructor
public class ErrorResponse implements Response {

    private Data data;
    private boolean success;
    private int status;

}
