package com.briup.cms.web.controller;


import com.briup.cms.bean.Comment;
import com.briup.cms.bean.Subcomment;
import com.briup.cms.bean.aop.Logging;
import com.briup.cms.bean.dto.CommentDeleteParam;
import com.briup.cms.bean.extend.SubCommentExtend;
import com.briup.cms.service.CommentService;
import com.briup.cms.util.Result;
import io.swagger.annotations.Api;
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
}

