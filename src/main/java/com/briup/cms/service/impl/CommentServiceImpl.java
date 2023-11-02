package com.briup.cms.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.briup.cms.bean.Article;
import com.briup.cms.bean.Comment;
import com.briup.cms.bean.Subcomment;
import com.briup.cms.bean.User;
import com.briup.cms.bean.dto.CommentDeleteParam;
import com.briup.cms.bean.dto.CommentQueryParam;
import com.briup.cms.bean.extend.CommentExtend;
import com.briup.cms.bean.extend.SubCommentExtend;
import com.briup.cms.dao.ArticleDao;
import com.briup.cms.dao.CommentDao;
import com.briup.cms.dao.SubcommentDao;
import com.briup.cms.dao.UserDao;
import com.briup.cms.exception.ServiceException;
import com.briup.cms.service.CommentService;
import com.briup.cms.util.ResultCode;
import com.sun.org.apache.regexp.internal.RE;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class CommentServiceImpl implements CommentService {
    @Autowired
    UserDao userDao;

    @Autowired
    ArticleDao articleDao;

    @Autowired
    CommentDao commentDao;
    @Autowired
    SubcommentDao subcommentDao;
    @Override
    public void saveComment(Comment comment) {
        // 1.参数判断
        if(comment == null)
            throw new ServiceException(ResultCode.PARAM_IS_BLANK);
        User user = userDao.selectById(comment.getUserId());
        if (user == null){
            throw new ServiceException(ResultCode.USER_NOT_EXIST);
        }
        if("禁用".equals(user.getStatus()))
            throw new ServiceException(ResultCode.USER_ACCOUNT_FORBIDDEN);
        // 3.文章存在判断
        if(articleDao.selectById(comment.getArticleId()) == null)
            throw new ServiceException(ResultCode.ARTICLE_NOT_EXIST);
        comment.setPublishTime(LocalDateTime.now());
        commentDao.insert(comment);
    }

    @Override
    public void saveSubComment(Subcomment subcomment) {
        if (subcomment == null){
            throw new ServiceException(ResultCode.PARAM_IS_INVALID);
        }
        if (commentDao.selectById(subcomment.getParentId()) == null){
            throw new ServiceException(ResultCode.COMMENT_NOT_EXIST);
        }
        User user = userDao.selectById(subcomment.getUserId());
        if (user == null ){
            throw new ServiceException(ResultCode.USER_NOT_EXIST);
        }
        if ("禁用".equals(user.getStatus())){
            throw new ServiceException(ResultCode.USER_ACCOUNT_FORBIDDEN);
        }
        Long replyId = subcomment.getReplyId();
        if (replyId !=null && subcommentDao.selectById(replyId)==null){
            throw new ServiceException(ResultCode.REPLYCOMMENT_NOT_EXIST);
        }
        subcomment.setPublishTime(LocalDateTime.now());
        subcommentDao.insert(subcomment);
    }

    @Override
    public void deleteById(CommentDeleteParam commentDeleteParam) {
        if (commentDeleteParam == null){
            throw new ServiceException(ResultCode.PARAM_IS_INVALID);
        }
        Integer type = commentDeleteParam.getType();
        Long id = commentDeleteParam.getId();
        if (type == 1){
            // 删除一级
            if (commentDao.selectById(id)==null)
                throw new ServiceException(ResultCode.COMMENT_NOT_EXIST);
            commentDao.deleteById(id);
        }else if (type == 2){
            // 删除二级
            if (subcommentDao.selectById(id)==null)
                throw new ServiceException(ResultCode.SUBCOMMENT_NOT_EXIST);
            subcommentDao.deleteById(id);
        }else {
            throw new ServiceException(ResultCode.PARAM_IS_INVALID);
        }
    }

    @Override
    public void deleteInBatch(List<CommentDeleteParam> list) {
        if (list == null){
            throw new ServiceException(ResultCode.PARAM_IS_INVALID);
        }
        boolean flag = false;
        for (CommentDeleteParam commentDeleteParam : list) {
            try {
                deleteById(commentDeleteParam);
                flag =true;
            }catch (Exception e){
                System.out.println("删除失败，id："+commentDeleteParam.getId());
            }
        }
        if (!flag){
            throw new ServiceException(ResultCode.COMMENT_NOT_EXIST);
        }
    }

    @Override
    public List<SubCommentExtend> queryByCommentId(Long id) {
        if (id == null)
            throw new ServiceException(ResultCode.COMMENT_NOT_EXIST);
        Comment comment = commentDao.selectById(id);
        Long articleId = comment.getArticleId();
        Article article = articleDao.selectById(articleId);
        if (article == null){
            throw new ServiceException(ResultCode.ARTICLE_NOT_EXIST);
        }
        LambdaQueryWrapper<Subcomment> lqw = new LambdaQueryWrapper<>();
        lqw.eq(Subcomment::getParentId,id).orderByAsc(Subcomment::getPublishTime);
        List<Subcomment> list = subcommentDao.selectList(lqw);

        ArrayList<SubCommentExtend> subCommentExtends = new ArrayList<>();
        for (Subcomment subcomment : list) {
            SubCommentExtend subCommentExtend = new SubCommentExtend();
            Long userId = subcomment.getUserId();
            User user = userDao.selectById(userId);
            BeanUtils.copyProperties(subcomment,subCommentExtend);
            subCommentExtend.setAuthor(user);
            subCommentExtends.add(subCommentExtend);
        }
        return subCommentExtends;
    }

    @Override
    public Page<CommentExtend> queryByArticleId(Integer pageNum, Integer pageSize, Long id) {
        Comment comment = commentDao.selectByArticleIdComment(id);
        System.out.println(comment);
        if (comment == null){
            throw new ServiceException(ResultCode.COMMENT_NOT_EXIST);
        }
        Page<Comment> commentPage = new Page<>(pageNum,pageSize);
        commentDao.selectPage(commentPage,null);
        List<Comment> records = commentPage.getRecords();
        ArrayList<CommentExtend> commentExtends = new ArrayList<>();
        for (Comment record : records) {
            Long userId = record.getUserId();
            User author = userDao.selectById(userId);
            if (author == null){
                throw new ServiceException(ResultCode.USER_NOT_EXIST);
            }
            CommentExtend commentExtend = new CommentExtend();
            BeanUtils.copyProperties(record,commentExtend);
            commentExtend.setAuthor(author);
            LambdaQueryWrapper<Subcomment> lqw = new LambdaQueryWrapper<>();
            lqw.eq(Subcomment::getParentId,record.getId());
            List<Subcomment> list = subcommentDao.selectList(lqw);
            ArrayList<SubCommentExtend> subCommentExtends = new ArrayList<>();
            for (Subcomment subcomment : list) {
                Long userId1 = subcomment.getUserId();
                User user = userDao.selectById(userId1);
                SubCommentExtend subCommentExtend = new SubCommentExtend();
                BeanUtils.copyProperties(subcomment,subCommentExtend);
                subCommentExtend.setAuthor(user);
                subCommentExtends.add(subCommentExtend);
            }
            commentExtend.setChildComments(subCommentExtends);
            commentExtends.add(commentExtend);
        }

        Page<CommentExtend> commentExtendPage = new Page<>();
        commentExtendPage.setRecords(commentExtends);
        commentExtendPage.setTotal(commentPage.getTotal());
        commentExtendPage.setCurrent(commentPage.getCurrent());
        return commentExtendPage;
    }

    @Override
    public Page<CommentExtend> query(CommentQueryParam param) {
        Integer pageNum = param.getPageNum();
        Integer pageSize = param.getPageSize();
        Page<CommentExtend> commentExtendPage = new Page<>(pageNum, pageSize);
        commentDao.query(commentExtendPage,param.getKeyword(),param.getUserId(),param.getArticleId()
        ,param.getStartTime(),param.getEndTime());
        return commentExtendPage;
    }
}
