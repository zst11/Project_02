package com.briup.cms.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.briup.cms.bean.User;
import com.briup.cms.bean.extend.UserExtend;

import java.util.List;

public interface UserService {

    User login(String username, String password);

    User queryById(Long id);

    void save(User user);

    void setVip(Long id);

    void update(User user);

    void deleteByBatch(List<Long> ids);

    IPage<UserExtend> query(Integer pageNum, Integer pageSize, String username, String status, Integer roleId,
                            Integer isVip);

    List<User> getAllUser();
}
