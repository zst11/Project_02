package com.briup.cms.service.impl;

import com.briup.cms.bean.Role;
import com.briup.cms.dao.RoleDao;
import com.briup.cms.service.RoleService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public class RoleServiceImpl implements RoleService {
    @Autowired
    private RoleDao roleDao;
    @Override
    public List<Role> list() {
        return roleDao.selectList(null);
    }
}
