package com.cloud.cloudstorage.dto;

import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

public record  StreamResourceDto(
        StreamingResponseBody body,
        String filename
) {}
