package com.briup.cms.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.briup.cms.bean.Log;
import com.briup.cms.bean.dto.LogExportParam;
import com.briup.cms.bean.dto.LogParam;
import com.briup.cms.bean.vo.LogVO;
import com.briup.cms.dao.LogDao;
import com.briup.cms.exception.ServiceException;
import com.briup.cms.service.LogService;
import com.briup.cms.util.BeanCopyUtils;
import com.briup.cms.util.Result;
import com.briup.cms.util.ResultCode;
import com.google.gson.Gson;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

@Service
public class LogServiceImpl implements LogService {

    @Autowired
    LogDao logDao;
    @Autowired
    Gson gson;

    @Override
    public IPage<LogVO> query(LogParam param) {
        // 1.参数判断
        if (param == null || param.getPageNum() == null || param.getPageSize() == null)
            throw new ServiceException(ResultCode.PARAM_IS_BLANK);
        IPage<Log> page = new Page<>(param.getPageNum(), param.getPageSize());
        //获取查询条件
        LambdaQueryWrapper<Log> wrapper = getQueryWrapper(param.getUsername(),
                param.getRequestUrl(),
                param.getStartTime(),
                param.getEndTime());
// 3.执行分页查询
        logDao.selectPage(page, wrapper);


        // 前端展示时所需的参数有限,所以在返回前将 将Log分页对象 转换为 LogVO分页对象
        IPage<LogVO> logVOPage = BeanCopyUtils.copyPage(page, LogVO.class);
//前端展示无需转换ResultJson中的data数据,所以resultJson 拆分成 code 及 msg
        parseResultJson(logVOPage.getRecords());
        return logVOPage;
    }

    @Override
    public List<LogVO> queryForExport(LogExportParam param) {
        //条件判断
        if (param == null) {
            throw new ServiceException(ResultCode.PARAM_IS_BLANK);
        }
        //获取查询条件
        LambdaQueryWrapper<Log> wrapper = getQueryWrapper(param.getUsername(),
                param.getRequestUrl(),param.getStartTime(), param.getEndTime());
        //设置日志导出条数
        wrapper.last(Objects.nonNull(param.getCount()), "limit " + param.getCount());
        //根据条件获取数据
        List<Log> logList = logDao.selectList(wrapper);
        //Bean拷贝
        List<LogVO> logVOList = BeanCopyUtils.copyBeanList(logList, LogVO.class);
//转换 resultJson 拆分成 code 及 msg
        parseResultJson(logVOList);
        return logVOList;
    }

    private void parseResultJson(List<LogVO> list){
        list.forEach(logVO -> {
            Result result = gson.fromJson(logVO.getResultJson(), Result.class);
            logVO.setCode(result.getCode());
            logVO.setMsg(result.getMsg());
            logVO.setResultJson(null);
        });
    }
    private LambdaQueryWrapper<Log> getQueryWrapper(String username, String url,
                                                    LocalDateTime startTime, LocalDateTime endTime) {
        LambdaQueryWrapper<Log> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(StringUtils.hasText(username), Log::getUsername, username)
                .eq(StringUtils.hasText(url), Log::getRequestUrl, url)
                .le(endTime != null, Log::getCreateTime, endTime)
                .ge(startTime != null, Log::getCreateTime, startTime)
                .orderByDesc(Log::getCreateTime);
        return wrapper;
    }
}
