package com.example.authorizationserver.domain.response.Error;

import com.example.authorizationserver.domain.constant.Code;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

@lombok.Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Data {

    private Code code;
    private String error;
    private String techMessage;
}
