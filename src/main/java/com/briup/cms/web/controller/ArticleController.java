package com.briup.cms.web.controller;


import com.baomidou.mybatisplus.core.metadata.IPage;
import com.briup.cms.bean.Article;
import com.briup.cms.aop.Logging;
import com.briup.cms.bean.dto.ArticleParam;
import com.briup.cms.bean.extend.ArticleExtend;
import com.briup.cms.service.ArticleService;
import com.briup.cms.util.Result;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
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
@RestController
@RequestMapping("/auth/article")
@Api("资讯模块")
public class ArticleController {
    @Autowired
    ArticleService articleService;

    @PostMapping("/saveOrUpdate")
    @ApiOperation(value = "新增或者修改文章信息",notes = "无id新增；反之")
    public Result saveOrUpdate(@RequestBody Article article){
        articleService.saveOrUpdate(article);
        return null;
    }

    @ApiOperation(value = "审核文章", notes = "文章id必须有效， status: 审核通过、审核未通过")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "文章id", required = true, dataType = "String"),
            @ApiImplicitParam(name = "status", value = "审核状态", required = true, dataType = "String")
    })
    @PutMapping("/review")
    public Result reviewArticle(Long id, String status) {
        articleService.reviewArticle(id, status);
        return Result.success("审核完成");
    }

    @ApiOperation(value = "批量删除文章", notes = "需要提供多个id 值")
    @DeleteMapping("/deleteByBatch/{ids}")
    public Result deleteInBatch(@RequestParam("ids") List<Long> ids) {
        articleService.deleteInBatch(ids);
        return Result.success("删除成功");
    }

    @Logging("查询指定文章")
    @ApiOperation(value = "查询指定文章", notes = "文章要包含3条一级评论")
    @GetMapping("/queryById/{id}")
    public Result queryById(@PathVariable Long id) {
        ArticleExtend articleExtend = articleService.queryByIdWithComment(id);
        return Result.success(articleExtend);
    }

    @ApiOperation(value = "分页+条件查询文章", notes = "")
    @PostMapping("/query")
    public Result queryById(@RequestBody ArticleParam
                                    articleParam) {
        IPage<ArticleExtend> page = articleService.query(articleParam);
        return Result.success(page);
    }

    @GetMapping("/getAll")
    public Result getAll(){
        return Result.success(articleService.getAll());
    }
}

