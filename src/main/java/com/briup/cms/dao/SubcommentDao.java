package com.briup.cms.dao;

import com.briup.cms.bean.Subcomment;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.briup.cms.bean.extend.SubCommentExtend;

import java.util.List;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author briup
 * @since 2023-08-31
 */
public interface SubcommentDao extends BaseMapper<Subcomment> {
    List<SubCommentExtend> queryByParentId(Long parentId);
}
