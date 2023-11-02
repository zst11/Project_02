package com.briup.cms.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.briup.cms.bean.Comment;
import com.briup.cms.bean.Subcomment;
import com.briup.cms.bean.dto.CommentDeleteParam;
import com.briup.cms.bean.dto.CommentQueryParam;
import com.briup.cms.bean.extend.CommentExtend;
import com.briup.cms.bean.extend.SubCommentExtend;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;


public interface CommentService {

    void saveComment(Comment comment);

    void saveSubComment(Subcomment subcomment);

    void deleteById(CommentDeleteParam commentDeleteParam);

    void deleteInBatch(List<CommentDeleteParam> list);

    List<SubCommentExtend> queryByCommentId(Long id);

    Page<CommentExtend> queryByArticleId(Integer pageNum, Integer pageSize,Long id);

    Page<CommentExtend> query(CommentQueryParam param);
}
