package com.briup.cms.dao;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.briup.cms.bean.User;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.briup.cms.bean.extend.UserExtend;
import org.apache.ibatis.annotations.Param;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author briup
 * @since 2023-08-31
 */
public interface UserDao extends BaseMapper<User> {
    IPage<UserExtend> queryAllUserWithRole(IPage<UserExtend> page,
                                           @Param("username") String username,
                                           @Param("status") String status,
                                           @Param("roleId") Integer roleId,
                                           @Param("isVip") Integer isVip);

    User queryUserById(Long id);
}
