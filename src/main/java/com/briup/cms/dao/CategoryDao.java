package com.briup.cms.dao;

import com.briup.cms.bean.Category;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.briup.cms.bean.extend.CategoryExtend;

import java.util.List;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author briup
 * @since 2023-08-31
 */
public interface CategoryDao extends BaseMapper<Category> {
    Integer getMaxOrderNum();
    List<CategoryExtend> queryAllWithCates();
}
