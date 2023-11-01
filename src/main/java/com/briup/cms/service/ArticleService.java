package com.briup.cms.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.briup.cms.bean.Article;
import com.briup.cms.bean.dto.ArticleParam;
import com.briup.cms.bean.extend.ArticleExtend;

import java.util.List;

public interface ArticleService {
    void saveOrUpdate(Article article);

    void reviewArticle(Long id, String status);

    void deleteInBatch(List<Long> ids);

    ArticleExtend queryByIdWithComment(Long id);

    IPage<ArticleExtend> query(ArticleParam articleParam);
}
