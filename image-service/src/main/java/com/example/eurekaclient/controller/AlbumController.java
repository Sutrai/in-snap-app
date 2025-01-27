package com.example.eurekaclient.controller;

import com.example.eurekaclient.service.ImageService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping()
@RequiredArgsConstructor
public class AlbumController {
    private final ImageService imageService;


}
