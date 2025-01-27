package com.example.eurekaclient.domain.api.image;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UploadReq {
    private Object image;
    private String title;
    private String description;

}
