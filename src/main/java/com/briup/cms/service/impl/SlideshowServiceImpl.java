package com.briup.cms.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.briup.cms.bean.Slideshow;
import com.briup.cms.dao.SlideshowDao;
import com.briup.cms.exception.ServiceException;
import com.briup.cms.service.SlideshowService;
import com.briup.cms.util.ResultCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
// 实现轮播的接口
public class SlideshowServiceImpl implements SlideshowService {
    @Autowired
    private SlideshowDao slideshowDao;
    @Override
    public List<Slideshow> queryAllEnable() {
        LambdaQueryWrapper<Slideshow> qw = new LambdaQueryWrapper<>();
        qw.eq(Slideshow::getStatus,"启用");
        qw.orderByDesc(Slideshow::getUploadTime);
        List<Slideshow> list = slideshowDao.selectList(qw);
        if (list==null||list.size()==0){
            throw new ServiceException(ResultCode.DATA_NONE);
        }
        return list;
    }

    @Override
    public IPage<Slideshow> query(Integer page,Integer pageSize,String status,String desc) {
        Page<Slideshow> p = new Page<>(page, pageSize);
        LambdaQueryWrapper<Slideshow> qw = new LambdaQueryWrapper<>();
        qw.eq(StringUtils.hasText(status),Slideshow::getStatus,status).like(StringUtils.hasText(desc),
                Slideshow::getDescription,desc).orderByDesc(Slideshow::getUploadTime);
        slideshowDao.selectPage(p,qw);
        if (p.getTotal()==0){
            throw new ServiceException(ResultCode.DATA_NONE);
        }
        return p;
    }

    @Override
    public Slideshow queryById(Integer id) {
        return slideshowDao.selectById(id);
    }

    @Override
    public void saveOrUpdate(Slideshow slideshow) {
        // 需判断url是否唯一
        Integer id = slideshow.getId();
        String url = slideshow.getUrl();
        boolean flag = false;
        if (url!=null){
            if (id!=null){
                Slideshow oldSlideShow = slideshowDao.selectById(id);
                if (oldSlideShow!=null && url.equals(oldSlideShow.getUrl())){
                    // 代表还是原来的轮播图
                    flag = true;
                }
            }
            if (!flag){
                // 如果不是就更新重置url的时间
                LambdaQueryWrapper<Slideshow> qw = new LambdaQueryWrapper<>();
                qw.eq(Slideshow::getUrl,url);
                Slideshow s = slideshowDao.selectOne(qw);
                if (s!=null){
                    throw new ServiceException(ResultCode.SLIDESHOW_URL_EXISTED);
                }
                slideshow.setUploadTime(LocalDateTime.now());
            }
        }
        if (id!=null){
            // 修改操作
            Slideshow s = slideshowDao.selectById(id);
            // 判断当前轮播图是否有效
            if (s == null)
                throw new ServiceException(ResultCode.SLIDESHOW_NOT_EXISTED);
            //3.2 更新操作
            slideshowDao.updateById(slideshow);
        }else {
            // 新增操作
            if (slideshow.getStatus() == null)
                slideshow.setStatus("启用");
            slideshowDao.insert(slideshow);
        }
    }

    @Override
    public void deleteInBatch(List<Integer> ids) {
        if (ids==null||ids.size()==0){
            throw new ServiceException(ResultCode.PARAM_IS_BLANK);
        }
        LambdaQueryWrapper<Slideshow> qw = new LambdaQueryWrapper<>();
        qw.in(Slideshow::getId,ids);
        int len = slideshowDao.selectCount(qw);
        log.info("len:{}",len);
        if (len <= 0) {
            throw new ServiceException(ResultCode.SLIDESHOW_NOT_EXISTED);
        }
        slideshowDao.deleteBatchIds(ids);
    }
}
