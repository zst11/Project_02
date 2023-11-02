package com.briup.cms.bean.extend;

import com.briup.cms.bean.Subcomment;
import com.briup.cms.bean.User;
import lombok.Data;

@Data
public class SubCommentExtend extends Subcomment {
    User author;
}
