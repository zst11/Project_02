package com.briup.cms.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.briup.cms.bean.dto.LogExportParam;
import com.briup.cms.bean.dto.LogParam;
import com.briup.cms.bean.vo.LogVO;

import java.util.List;

public interface LogService {
    // 所展示的是logVo实体类
    IPage<LogVO> query( LogParam param);

    List<LogVO> queryForExport(LogExportParam param);
}
