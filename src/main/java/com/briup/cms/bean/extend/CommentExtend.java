package com.briup.cms.bean.extend;

import com.briup.cms.bean.Comment;
import com.briup.cms.bean.User;
import lombok.Data;

import java.util.List;

@Data
public class CommentExtend extends Comment {
    User author;

    List<SubCommentExtend> childComments;
}
