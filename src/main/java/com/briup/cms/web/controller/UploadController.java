package com.briup.cms.web.controller;

import com.briup.cms.util.Result;
import com.briup.cms.util.UploadUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@Api(tags = "文件上传模块")
@Slf4j
public class UploadController {

    @Autowired
    private UploadUtils uploadUtils;

    @PostMapping("/auth/upload")
    @SneakyThrows
    @ApiOperation(value = "文件上传")
    public Result upload(@RequestPart MultipartFile img){
        return Result.success(uploadUtils.fileToOSS(img));
    }
}
