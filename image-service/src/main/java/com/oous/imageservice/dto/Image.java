package com.oous.imageservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Image {

    private String imageHash;
    private Long accountId;
    private Long accountUrl;
    private String title;
    private String description;
    private String type;
    private int width;
    private int height;
    private long size;
    private int views;
    private String link;
}
