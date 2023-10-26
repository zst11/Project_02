package com.briup.cms.web.controller;


import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.api.R;
import com.briup.cms.bean.Slideshow;
import com.briup.cms.dao.SlideshowDao;
import com.briup.cms.service.SlideshowService;
import com.briup.cms.util.Result;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author briup
 * @since 2023-08-31
 */
@Api(tags = "轮播图模块")
@Slf4j
@RestController
@RequestMapping("/slideshow")
public class SlideshowController {

    @Autowired
    private SlideshowDao slideshowDao;

    @Autowired
    private SlideshowService slideshowService;
    @GetMapping("/test")
    public Result test() {
        System.out.println("in slideshow test...");

        Slideshow slideshow = slideshowDao.selectById(1);

        return Result.success(slideshow);
    }
    @ApiOperation(value = "查询所有可用轮播图")
    @GetMapping("/queryAllEnable")
    public Result queryAllEnable(){
        List<Slideshow> list = slideshowService.queryAllEnable();
        return Result.success(list);
    }

    @ApiOperation(value = "条件+分页查询轮播图")
    @ApiImplicitParams({ // 表示一组参数说明  @ApiImplicitParam表示请求参数的各个方面
            @ApiImplicitParam(name = "pageNum", value = "当前页", dataType = "int", required = true, defaultValue = "1", paramType = "query"),
            @ApiImplicitParam(name = "pageSize", value = "每页数量", dataType = "int", required = true, defaultValue = "4", paramType = "query"),
            @ApiImplicitParam(name = "status", value = "状态值", paramType = "query"),
            @ApiImplicitParam(name = "desc", value = "描述信息", paramType = "query")
    })
    @GetMapping("/query")
    public Result query(Integer pageNum,Integer pageSize,String status,String desc){
        IPage<Slideshow> p = slideshowService.query(pageNum, pageSize, status, desc);
        return Result.success(p);
    }

    @ApiOperation(value = "根据id查询" ,notes = "用于更新时的数据回显")
    @GetMapping("/queryById/{id}")
    public Result queryById(@PathVariable Integer id){
        return Result.success(slideshowService.queryById(id));
    }

    @ApiOperation(value = "新增或者修改轮播图",notes = "slideShow参数包含id则为更新，不包含则为修改")
    @PostMapping("/saveOrUpdate")
    public Result saveOrUpdate(@RequestBody Slideshow slideshow){
        slideshowService.saveOrUpdate(slideshow);
        return Result.success("操作成功");
    }

    @ApiOperation(value = "删除轮播图",notes = "可以删除一个或多个")
    @DeleteMapping("/deleteByBatch/{ids}")
    public Result deleteByBatch(@PathVariable List<Integer> ids){
        slideshowService.deleteInBatch(ids);
        return Result.success("操作成功");
    }

}

