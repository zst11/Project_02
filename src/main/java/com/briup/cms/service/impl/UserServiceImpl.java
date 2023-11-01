package com.briup.cms.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.briup.cms.bean.User;
import com.briup.cms.bean.extend.UserExtend;
import com.briup.cms.dao.UserDao;
import com.briup.cms.exception.ServiceException;
import com.briup.cms.service.UserService;
import com.briup.cms.util.ResultCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Slf4j
public class UserServiceImpl implements UserService {
    @Autowired
    private UserDao userDao;
    @Override
    public User login(String username, String password) {

        LambdaQueryWrapper<User> lqw = new LambdaQueryWrapper<>();
        LambdaQueryWrapper<User> eq = lqw.eq(User::getUsername, username).eq(User::getPassword, password);
        User user = userDao.selectOne(eq);
        if (user == null){
            System.out.println("进来了");
            throw new ServiceException(ResultCode.USER_LOGIN_ERROR);
        }
        return user;
    }

    @Override
    public User queryById(Long id) {
        //1.有效参数判断
        if (id == null)
            throw new ServiceException(ResultCode.PARAM_IS_BLANK);
        User user = userDao.selectById(id);
        if (user == null)
            throw new ServiceException(ResultCode.DATA_NONE);
        return user;
    }

    @Override
    public void save(User user) {
        // 1、拿到其中name，密码值判断是否为空
        String username = user.getUsername();
        String password = user.getPassword();
        if (username == null || password == null){
            throw new ServiceException(ResultCode.PARAM_IS_BLANK);
        }
        // 2、先对其进行一个头尾的空格去除，判断是否为存在
        username = username.trim();
        password = password.trim();
        if ("".equals(username)||"".equals(password)){
            throw new ServiceException(ResultCode.PARAM_IS_BLANK);
        }
        //3、将上一步去除了空格的值重新set
        user.setUsername(username);
        user.setPassword(password);
        //4、判断username是否唯一，使用userdao的查询，看是否有结果
        LambdaQueryWrapper<User> lqw = new LambdaQueryWrapper<>();
        lqw.eq(User::getUsername,username);
        User user1 = userDao.selectOne(lqw);
        if (user1!=null){
            throw new ServiceException(ResultCode.USER_HAS_EXISTED);
        }
        // 5、保存值
        // 先进行注册时间的保存
        user.setRegisterTime(LocalDateTime.now());
        userDao.insert(user);
    }

    @Override
    public void setVip(Long id) {
        // 1、得到user，判断是否存在
        User user = userDao.selectById(id);
        if (user==null){
            throw new ServiceException(ResultCode.PARAM_IS_BLANK);
        }
        // 2、判断此user是否已为vip
        if (user.getIsVip()==1){
            throw new ServiceException(ResultCode.PARAM_IS_INVALID);
        }
        // 3、修改用户信息
        User user1 = new User();
        user1.setId(id);
        user1.setIsVip(1);
        LocalDateTime expiresTime = LocalDateTime.now().plusMonths(1);
        user1.setExpiresTime(expiresTime);
        userDao.updateById(user1);
    }

    @Override
    public void update(User user) {
        // 1、id和user判断
        Long id = user.getId();
        User dbUser = userDao.selectById(id);
        if (id == null || dbUser == null)
            throw new ServiceException(ResultCode.PARAM_IS_INVALID);
        // 2、
        String newName = user.getUsername();
        String oldName = dbUser.getUsername();
        if (newName != null && !newName.equals(oldName)) {
            String trimName = newName.trim();
            if ("".equals(trimName))
                throw new ServiceException(ResultCode.PARAM_IS_INVALID);
            //更新username(已经去除前后空白字符)
            user.setUsername(trimName);
            //3.用户名唯一判断: 查询username是否唯一(除当前user外)
            LambdaQueryWrapper<User> qw = new LambdaQueryWrapper<>();
            qw.eq(User::getUsername, trimName);
            if (userDao.selectOne(qw) != null)
                throw new ServiceException(ResultCode.USERNAME_HAS_EXISTED);
        }
        userDao.updateById(user);
    }

    @Override
    public void deleteByBatch(List<Long> ids) {
        //1.有效参数判断
        if (ids == null || ids.isEmpty()) {
            throw new ServiceException(ResultCode.PARAM_IS_BLANK);
        }
        //2.删除指定用户
        userDao.deleteBatchIds(ids);
    }

    @Override
    public IPage<UserExtend> query(Integer pageNum, Integer pageSize, String username, String status, Integer roleId, Integer isVip) {
        if (pageNum == null || pageSize == null)
            throw new ServiceException(ResultCode.PARAM_IS_BLANK);
        IPage<UserExtend> page = new Page<>(pageNum, pageSize);
        userDao.queryAllUserWithRole(page,username, status, roleId, isVip);
        return page;
    }

    @Override
    public List<User> getAllUser() {
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(User::getStatus,"启用");
        return userDao.selectList(queryWrapper);
    }


}
