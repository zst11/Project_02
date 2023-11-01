package com.briup.cms.service.impl;

import com.alibaba.excel.EasyExcel;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.briup.cms.bean.Article;
import com.briup.cms.bean.Category;
import com.briup.cms.bean.extend.CategoryExtend;
import com.briup.cms.dao.ArticleDao;
import com.briup.cms.dao.CategoryDao;
import com.briup.cms.exception.ServiceException;
import com.briup.cms.service.CategoryService;
import com.briup.cms.util.ResultCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class CategoryServiceImpl implements CategoryService {

    @Autowired
    CategoryDao categoryDao;
    @Override
    public void insert(Category category) {
        String categoryName = category.getName();
        LambdaQueryWrapper<Category> lqw = new LambdaQueryWrapper<>();
        lqw.eq(Category::getName,categoryName);
        Category category1 = categoryDao.selectOne(lqw);
        if (category1!=null){
            throw new ServiceException(ResultCode.CATEGORY_HAS_EXISTED);
        }
        // 判断是否有父id
        Integer parentId = category.getParentId();
        if (parentId!=null&&categoryDao.selectById(parentId)==null){
            throw new ServiceException(ResultCode.PARAM_IS_INVALID);
        }
        // 设置order_num
        int order_num = 1;
        if (categoryDao.selectCount(null)!=0){
            Integer mo = categoryDao.getMaxOrderNum();
            order_num = mo + 1;
        }
        category.setOrderNum(order_num);
        categoryDao.insert(category);
    }

    @Override
    public Category getCategoryById(Integer id) {
        return categoryDao.selectById(id);
    }

    @Override
    public void update(Category category) {
        //1.id判断：不能为空 必须有效
        Integer id = category.getId();
        Category oldCategory = categoryDao.selectById(id);
        if (id == null || oldCategory == null)
            throw new
                    ServiceException(ResultCode.CATEGORY_NOT_EXIST);
        //2.name判断：如果存在则必须唯一
        LambdaQueryWrapper<Category> qw = new LambdaQueryWrapper<>();
        //当待修改的栏目名字与原栏目名不一致时,需要判断待修改的名字在数据库中是否已被使用
        if (category.getName() != null && !category.getName().equals(oldCategory.getName())) {
            qw.eq(Category::getName, category.getName());
            if (categoryDao.selectOne(qw) != null)
                throw new
                        ServiceException(ResultCode.CATEGORYNAME_HAS_EXISTED);
        }
        Integer parentId = category.getParentId();
        //3.如果当前栏目为1级，则不能更改为2级
        if (oldCategory.getParentId() == null && parentId != null) {
            throw new
                    ServiceException(ResultCode.CATEGORY_LEVEL_SETTING_ERROR);
        }
        //4.如果需要修改的栏目为2级，且要修改其父栏目
        if (oldCategory.getParentId() != null && parentId != null) {
            Category pCategory = categoryDao.selectById(parentId);
            // 需要更新的父栏目不存在，或 需要更新的父栏目为2级栏目，则失败
            if (pCategory == null || pCategory.getParentId() != null)
                throw new
                        ServiceException(ResultCode.PCATEGORY_IS_INVALID);
        }
        //5.执行更新操作
        categoryDao.updateById(category);
    }

    @Autowired
    ArticleDao articleDao;
    @Override
    public void deleteById(Integer id) {
        //1.栏目id判断
        Category category = categoryDao.selectById(id);
        if (category == null)
            throw new
                    ServiceException(ResultCode.CATEGORY_NOT_EXIST);
        //2.栏目级别判断
        if (category.getParentId() == null) {
            //一级栏目
            // 2.1 如果该1级栏目下存在2级栏目，删除失败
            // select count(*) from cms_category where deleted =0 and parent_id = ? ;
            LambdaQueryWrapper<Category> qw = new LambdaQueryWrapper<>();
            qw.eq(Category::getParentId, id);
            if (categoryDao.selectCount(qw) > 0)
                throw new
                        ServiceException(ResultCode.PARAM_IS_INVALID);
        } else {
            //二级栏目
            // 2.2 如果2级栏目下不存在任何资讯，可以删除成功
            // select count(*) from cms_article where deleted = 0and category_id = ?
            // 2.3 如果2级栏目下存在资讯，且发表资讯的用户存在(未删除状态)，则删除失败
            Integer num = articleDao.getArticleNumByCategoryId(id);
            if (num > 0)
                throw new
                        ServiceException(ResultCode.PARAM_IS_INVALID);
        }
        //3.栏目删除
        categoryDao.deleteById(id);
    }

    @Override
    public void deleteInBatch(List<Integer> ids) {
        // 调用deleteById 循环删除 ,只要有一个被删除就成功
        // 加入try catch 可以将删除失败得id打印
        boolean flag = false;
        for (Integer id : ids) {
            try {
                deleteById(id);
                flag = true;
            }catch (ServiceException e){
                System.out.println("delete 失败得id："+id);
            }
        }
        // 一个都没有删除
        if (!flag){
            throw new ServiceException(ResultCode.PARAM_IS_INVALID);
        }
    }

    @Override
    public IPage<Category> query(Integer pageNum, Integer pageSize, Integer parentId) {
        //1.参数判断
        if (pageNum == null || pageNum <= 0 || pageSize == null || pageSize <= 0)
            throw new ServiceException(ResultCode.PARAM_IS_INVALID);
        IPage<Category> page = new Page<>(pageNum,pageSize);
        LambdaQueryWrapper<Category> lqw = new LambdaQueryWrapper<>();
        lqw.eq(parentId!=null,Category::getParentId,parentId);
        lqw.orderByAsc(Category::getParentId).orderByAsc(Category::getOrderNum);
        categoryDao.selectPage(page, lqw);
        if (page.getTotal()==0){
            throw new ServiceException(ResultCode.CATEGORY_NOT_EXIST);
        }
        return page;
    }

    @Override
    public List<CategoryExtend> queryAllParent() {
        List<CategoryExtend> categoryExtends = categoryDao.queryAllWithCates();
        if (categoryExtends==null||categoryExtends.size()==0){
            throw new ServiceException(ResultCode.CATEGORY_NOT_EXIST);
        }
        return categoryExtends;
    }

    @Override
    public List<Category> queryAllOneLevel() {
        LambdaQueryWrapper<Category> queryWrapper = new
                LambdaQueryWrapper<>();
        queryWrapper.isNull(Category::getParentId);
        List<Category> list =
                categoryDao.selectList(queryWrapper);
        if (list == null || list.size() == 0)
            throw new
                    ServiceException(ResultCode.CATEGORY_NOT_EXIST);
        return list;
    }

    @Override
    public void InsertInBatch(List<Category> list) {
        if (list.isEmpty()) {
            throw new ServiceException(ResultCode.PARAM_IS_INVALID);
        }
        list.forEach(category -> categoryDao.insert(category));
    }

    @Override
    public List<Category> queryAll() {
        return categoryDao.selectList(null);
    }
}
