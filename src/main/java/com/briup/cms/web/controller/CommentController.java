package com.briup.cms.web.controller;


import com.baomidou.mybatisplus.core.metadata.IPage;
import com.briup.cms.bean.Comment;
import com.briup.cms.bean.Subcomment;
import com.briup.cms.bean.aop.Logging;
import com.briup.cms.bean.dto.CommentDeleteParam;
import com.briup.cms.bean.dto.CommentQueryParam;
import com.briup.cms.bean.extend.CommentExtend;
import com.briup.cms.bean.extend.SubCommentExtend;
import com.briup.cms.service.CommentService;
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
@RequestMapping("/auth/comment")
@Api(tags = "评论模块")
public class CommentController {

    @Autowired
    CommentService commentService;
    @ApiOperation(value = "新增一级评论", notes = "一级评论直接对文章进行 评论")
    @Logging("评论文章")
    @PostMapping("/saveComment")
    public Result saveComment(@RequestBody Comment comment) {
        commentService.saveComment(comment);
        return Result.success("新增成功");
    }

    @ApiOperation(value = "新增二级评论", notes = "二级评论是对评论的回 复")
    @Logging("回复评论")
    @PostMapping("/saveSubComment")
    public Result saveSubComment(@RequestBody Subcomment comment) {
        commentService.saveSubComment(comment);
        return Result.success("新增成功");
    }

    @ApiOperation(value = "根据id删除评论", notes = "type为1表示1级评 论，为2表示2级评论")
    @Logging("通过id删除评论")
    @DeleteMapping("/deleteById")
    public Result deleteById(@RequestBody CommentDeleteParam param) {
        commentService.deleteById(param);
        return Result.success("删除成功");
    }

    @ApiOperation(value = "根据id批量删除评论", notes = "type为1表示1级 评论，为2表示2级评论")
    @Logging("批量删除评论")
    @DeleteMapping("/deleteByIdAll")
    public Result deleteByIdAll(@RequestBody List<CommentDeleteParam> list) {
        commentService.deleteInBatch(list);
        return Result.success("删除成功");
    }

    @ApiOperation(value = "查询指定1级评论下的所有2级评论", notes = "2级 评论含作者")
    @Logging("根据id查询一级评论及其二级评论")
    @GetMapping("/queryById/{id}/child_comments")
    public Result queryByCommentId(@PathVariable Long id) {
        List<SubCommentExtend> list = commentService.queryByCommentId(id);
        return Result.success(list);
    }

    @ApiOperation(value = "分页查询指定文章下的所有1级评论", notes = "1级 评论包含发表人及2条二级评论")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "pageNum", value = "页码",
                    dataType = "int", required = true, paramType = "query",
                    defaultValue = "1"),
            @ApiImplicitParam(name = "pageSize", value = "每页数量",
                    dataType = "int", required = true, paramType = "query",
                    defaultValue = "4"),
            @ApiImplicitParam(name = "id", value = "文章id", dataType
                    = "long", required = true, paramType = "path")
    })
    @GetMapping("/queryByArticleId/{id}")
    public Result queryByArticleId(Integer pageNum, Integer pageSize, @PathVariable Long id) {
        IPage<CommentExtend> page = commentService.queryByArticleId(pageNum, pageSize, id);
        return Result.success(page);
    }

    @ApiOperation(value = "分页+条件查询", notes = "查询条件：关键字、userId、articleId、发表时间范围")
    @Logging("分页查询评论信息")
    @PostMapping("/query")
    public Result query(@RequestBody CommentQueryParam param) {
        IPage<CommentExtend> page = commentService.query(param);
        return Result.success(page);
    }
}

