package com.example.eurekaclient.domain.response.Error;

import com.example.eurekaclient.domain.response.Response;
import lombok.AllArgsConstructor;
import lombok.Builder;

@lombok.Data
@Builder
@AllArgsConstructor
public class ErrorResponse implements Response {

    private Data data;
    private String success;
    private int status;

}
