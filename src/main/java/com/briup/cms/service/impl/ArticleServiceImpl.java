package com.briup.cms.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.briup.cms.bean.Article;
import com.briup.cms.bean.Category;
import com.briup.cms.bean.Comment;
import com.briup.cms.bean.User;
import com.briup.cms.bean.dto.ArticleParam;
import com.briup.cms.bean.extend.ArticleExtend;
import com.briup.cms.dao.ArticleDao;
import com.briup.cms.dao.CategoryDao;
import com.briup.cms.dao.CommentDao;
import com.briup.cms.dao.UserDao;
import com.briup.cms.exception.ServiceException;
import com.briup.cms.service.ArticleService;
import com.briup.cms.util.JwtUtil;
import com.briup.cms.util.RedisUtil;
import com.briup.cms.util.ResultCode;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class ArticleServiceImpl implements ArticleService {

    @Autowired
    ArticleDao articleDao;
    @Autowired
    CategoryDao categoryDao;

//    @Autowired
//    RedisUtil redisUtil;

    private final String REDIS_KEY = "Article_Read_Num";

    private static String getToken(){
        // 返回token
        ServletRequestAttributes requestAttributes = (ServletRequestAttributes)RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = requestAttributes.getRequest();
        return request.getHeader("Authorization");
    }
    @Override
    public void saveOrUpdate(Article article) {
        Map<String,Object> info = JwtUtil.parseJWT(getToken());
        // 1、判断article
        if (article == null){
            throw new ServiceException(ResultCode.PARAM_IS_INVALID);
        }
        // 2、判断用户是否存在
        Integer userId = (Integer)info.get("userId");
        // userId 可以不存在，那就是要新增了 !!!!看一下明天
        if (userId != null && articleDao.selectById(userId)==null){
            throw new ServiceException(ResultCode.USER_NOT_EXIST);
        }
        article.setUserId(userId.longValue());
        // 3.栏目判断：如果栏目不存在，或栏目不是2级栏目，则抛异常
        Integer categoryId = article.getCategoryId();
        if (categoryId != null){
            Category category = categoryDao.selectById(categoryId);
            if(category == null || category.getParentId() == null)
                throw new ServiceException(ResultCode.CATEGORY_NOT_EXIST);
        }
        // 4、判断是更新还是创建
        Long id = article.getId();
        if (id == null){
            // 新增
            article.setPublishTime(LocalDateTime.now());
            articleDao.insert(article);
        }else {
            // 修改
            Article article1 = articleDao.selectById(id);
            if (article1==null){
                throw new ServiceException(ResultCode.DATA_EXISTED);
            }
            Integer roleId = (Integer) info.get("roleId");
            // 文章审核通过之后普通用户不能再修改
            if (roleId == 3 && "审核通过".equals(article1.getStatus())){
                throw new ServiceException(ResultCode.PARAM_IS_INVALID);
            }
            articleDao.updateById(article);
        }
    }

    @Override
    public void reviewArticle(Long id, String status) {
        // 1.参数判断
        if(id == null || status == null)
            throw new
                    ServiceException(ResultCode.PARAM_IS_BLANK);
        // 2.文章必须存在
        if(articleDao.selectById(id) == null)
            throw new
                    ServiceException(ResultCode.ARTICLE_NOT_EXIST);
        // 3.修改文章审核状态
        Article article = new Article();
        article.setId(id);
        article.setStatus(status);
        articleDao.updateById(article);
    }

    @Override
    public void deleteInBatch(List<Long> ids) {
        if (ids == null|| ids.isEmpty()){
            throw new ServiceException(ResultCode.PARAM_IS_INVALID);
        }
        int i = articleDao.deleteBatchIds(ids);
        if (i == 0){
            throw new ServiceException(ResultCode.PARAM_IS_INVALID);
        }
    }

    @Autowired
    UserDao userDao;

    @Autowired
    CommentDao commentDao;

    @Autowired
    RedisUtil redisUtil;


    @Override
    public ArticleExtend queryByIdWithComment(Long id) {
        // 1.参数判断
        if(id == null)
            throw new ServiceException(ResultCode.PARAM_IS_BLANK);
        Article article = articleDao.selectById(id);
        if(article == null)
            throw new ServiceException(ResultCode.ARTICLE_NOT_EXIST);
        // 3.判断文章审核状态是否为”审核通过“，如果不是则不能查看
        if(!"审核通过".equals(article.getStatus()))
            throw new ServiceException(ResultCode.ARTICLE_IS_NOT_VISIBLE);
        // 4、判断发布文章得人是否还存在
        Long userId = article.getUserId();
        User author = userDao.selectById(userId);
        if (author==null){
            throw new ServiceException(ResultCode.USER_NOT_EXIST);
        }
        //
        String token = getToken();
        String userId1 = JwtUtil.getUserId(token);
        Integer isVip = userDao.selectById(userId1).getIsVip();
        // 如果当前用户不是文章的拥有者，同时文章收费，当前用户还不是Vip，查看失败
        // 注意：Long值比较使用 equals方法进行
        if(!userId.equals(article.getUserId()) && article.getCharged() == 1 && isVip == 0) {
            throw new ServiceException(ResultCode.ARTICLE_IS_NOT_VISIBLE);
        }
        ArticleExtend articleExtend = new ArticleExtend();
        BeanUtils.copyProperties(article,articleExtend);
        author.setPassword(null);
        articleExtend.setAuthor(author);
        LambdaQueryWrapper<Comment> lqw = new LambdaQueryWrapper<>();
        lqw.eq(Comment::getArticleId,id).orderByDesc(Comment::getPublishTime).last("limit 3");
        List<Comment> comments = commentDao.selectList(lqw);
        articleExtend.setComments(comments);


        // 优化，加入redis
        articleExtend.setReadNum(redisUtil.increment(REDIS_KEY,article.getId().toString()));
        return articleExtend;
    }

    @Override
    public IPage<ArticleExtend> query(ArticleParam articleParam) {
        // ArticleExtend 中有一个作者类，我们先将article分页之后将作者单独加入，这样可以省去多表联立
        Page<Article> page = new Page<>(articleParam.getPageNum(),articleParam.getPageSize());
        // 准备查询条件
        LambdaQueryWrapper<Article> wrapper = new
                LambdaQueryWrapper<>();
        wrapper.eq(articleParam.getUserId() != null, Article::getUserId,
                articleParam.getUserId());
        wrapper.eq(StringUtils.hasText(articleParam.getStatus()),
                Article::getStatus, articleParam.getStatus());
        wrapper.like(StringUtils.hasText(articleParam.getTitle()),
                Article::getTitle, articleParam.getTitle());
        wrapper.eq(articleParam.getCharged() != null,
                Article::getCharged, articleParam.getCharged());
        wrapper.eq(articleParam.getCategoryId() != null,
                Article::getCategoryId, articleParam.getCategoryId());
        wrapper.le(articleParam.getEndTime() != null,
                Article::getPublishTime, articleParam.getEndTime());
        wrapper.ge(articleParam.getStartTime() != null,
                Article::getPublishTime, articleParam.getStartTime());
        articleDao.selectPage(page,wrapper);
        List<Article> records = page.getRecords();
        ArrayList<ArticleExtend> list = new ArrayList<>();
        // 遍历所有文章添加作者信息
        for (Article record : records) {
            // 先判断文章得作者是否还存在，不存在就不能看
            Long userId = record.getUserId();
//            User user = userDao.selectById(userId);
            User user = userDao.queryUserById(record.getUserId());
            if (user == null){
                continue;
            }
            ArticleExtend articleExtend = new ArticleExtend();
            BeanUtils.copyProperties(record,articleExtend);
            // 额外注释密码
            user.setPassword(null);
            articleExtend.setAuthor(user);
            list.add(articleExtend);
        }
        Page<ArticleExtend> pageInfo = new Page<>();
        pageInfo.setRecords(list);
        pageInfo.setTotal(page.getTotal());
        // 设置当前页码和page得页码一样
        pageInfo.setCurrent(page.getCurrent());
        return pageInfo;
    }

    @Override
    public List<Article> getAll() {
        return articleDao.selectList(null);
    }


}
