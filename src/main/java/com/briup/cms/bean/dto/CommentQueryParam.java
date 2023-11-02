package com.briup.cms.bean.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class CommentQueryParam {

    Integer pageNum;
    Integer pageSize;

    String keyword;

    Long userId;

    Long articleId;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
    LocalDateTime startTime;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
    LocalDateTime endTime;
}
