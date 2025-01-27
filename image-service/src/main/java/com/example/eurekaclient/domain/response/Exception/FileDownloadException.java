package com.example.eurekaclient.domain.response.Exception;

import com.example.eurekaclient.domain.constant.Code;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;

public class FileDownloadException extends CommonException {
    public FileDownloadException(HttpStatusCode status) {
        super(Code.FILE_DOWNLOAD_FAILED, "Failed to download file from URL", "Response status: " + status, HttpStatus.BAD_REQUEST);
    }
}
