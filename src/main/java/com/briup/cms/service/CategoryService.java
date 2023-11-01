package com.briup.cms.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.briup.cms.bean.Category;
import com.briup.cms.bean.extend.CategoryExtend;
import io.swagger.models.auth.In;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

public interface CategoryService {
    void insert(Category category);

    Category getCategoryById(Integer id);

    void update(Category category);

    void deleteById(Integer id);

    void deleteInBatch(List<Integer> ids);

    IPage<Category> query(Integer pageNum, Integer pageSize, Integer parentId);

    List<CategoryExtend> queryAllParent();

    List<Category> queryAllOneLevel();

    void InsertInBatch(List<Category> list);

    List<Category> queryAll();
}
