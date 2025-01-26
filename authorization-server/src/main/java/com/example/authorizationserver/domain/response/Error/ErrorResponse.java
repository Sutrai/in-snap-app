package com.example.authorizationserver.domain.response.Error;

import com.example.authorizationserver.domain.response.Response;
import lombok.AllArgsConstructor;
import lombok.Builder;
import org.springframework.http.HttpStatus;

@lombok.Data
@Builder
@AllArgsConstructor
public class ErrorResponse implements Response{

    private Data data;
    private String success;
    private int status;

}
