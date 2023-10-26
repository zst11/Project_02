package com.briup.cms.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.briup.cms.bean.Slideshow;

import java.util.List;

public interface SlideshowService {
    // 使用轮播展示出所有启用的图
    List<Slideshow> queryAllEnable();
    // 分页查询、条件查询（按status，description）、按时间倒序
    IPage<Slideshow> query(Integer page,Integer pageSize,String status,String desc);
    // 根据id查询
    Slideshow queryById(Integer id);
    // 新增或更新轮播图
    void saveOrUpdate(Slideshow slideshow);
    // 删除轮播图
    // 删除单个和批量删除共用一个
    void  deleteInBatch(List<Integer> ids);
}
