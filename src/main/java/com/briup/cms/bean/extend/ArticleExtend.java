package com.briup.cms.bean.extend;

import com.briup.cms.bean.Article;
import com.briup.cms.bean.Comment;
import com.briup.cms.bean.User;
import lombok.Data;

import java.util.List;

@Data
public class ArticleExtend extends Article {
    List<Comment> comments;

    User author;
}
