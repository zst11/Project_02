package com.briup.cms.dao;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.briup.cms.bean.Comment;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.briup.cms.bean.extend.CommentExtend;
import org.apache.ibatis.annotations.Select;

import java.time.LocalDateTime;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author briup
 * @since 2023-08-31
 */
public interface CommentDao extends BaseMapper<Comment> {
    
    @Select("select * from cms_comment where article_id = #{articleId}")
    Comment selectByArticleIdComment(Long articleId);

    Page<CommentExtend> query(Page<CommentExtend> page,String keyword,
                              Long userId, Long articleId, LocalDateTime startTime,
                              LocalDateTime endTime);
}
