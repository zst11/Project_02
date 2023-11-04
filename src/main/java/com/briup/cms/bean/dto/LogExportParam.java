package com.briup.cms.bean.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * @author horry
 * @Description 日志导出参数实体
 * @date 2023/8/23-15:38
 */
@Data
public class LogExportParam {

    //发送请求的用户
    private String username;
    //请求的url
    private String requestUrl;

    //待导出数据的条数
    private Integer count;

    //日志时间
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
    private LocalDateTime startTime;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
    private LocalDateTime endTime;
}
