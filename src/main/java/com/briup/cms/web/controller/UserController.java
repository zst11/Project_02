package com.briup.cms.web.controller;


import com.baomidou.mybatisplus.core.metadata.IPage;
import com.briup.cms.bean.User;
import com.briup.cms.bean.extend.UserExtend;
import com.briup.cms.service.UserService;
import com.briup.cms.util.MD5Utils;
import com.briup.cms.util.Result;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author briup
 * @since 2023-08-31
 */
@Api(tags = "用户模块")
@RestController
@RequestMapping("/auth/user")
@Slf4j
public class UserController {
    // 用于登录成功之后将用户的信息在页面上显示出来

    @Autowired
    UserService userService;
    @ApiOperation("获取用户个人信息")
    @GetMapping("/info")
    public Result getInfo(@RequestAttribute("userId") Long id){
        log.info("id:{}",id);
        return Result.success(userService.queryById(id));
    }

    @ApiOperation(value = "新增用户信息",notes = "username、password必须存在，且用户名唯一")
    @PostMapping("/save")
    public Result save(@RequestBody User user){
        // 首先需要对密码进行MD5的加密,之后才能进行使用
        user.setPassword(MD5Utils.MD5(user.getPassword()));
        // 增加用户
        userService.save(user);
        return Result.success("新增成功");
    }

    @ApiOperation(value = "根据id查询用户信息",notes = "id必须存在且有效")
    @GetMapping("/queryById/{id}")
    public Result queryById(@PathVariable Long id){
        User user = userService.queryById(id);
        return Result.success(user);
    }

    @ApiOperation(value = "根据id修改vip",notes = "id存在且有效")
    @PutMapping("/setVip/{id}")
    public Result setVip(@PathVariable Long id){
        userService.setVip(id);
        return Result.success("更新vip信息成功");
    }

    @ApiOperation(value = "修改用户信息",notes = "id存在且有效,username唯一")
    @PutMapping("/update")
    public Result update(User user){
        userService.update(user);
        return Result.success("更新用户信息成功");
    }

    @ApiOperation(value = "根据id删除用户", notes = "id必须存在且有效")
    @DeleteMapping("/deleteByBatch/{ids}")
    public Result deleteByBatch(@PathVariable("ids") List<Long> ids) {
        userService.deleteByBatch(ids);
        return Result.success("删除成功");
    }

    //资讯模块需要使用
    @ApiOperation(value = "查询全部用户", notes = "id必须存在且有效")
    @GetMapping("/getAllUser")
    public Result getAllUser() {
        List<User> allUser = userService.getAllUser();
        return Result.success(allUser);
    }

    @ApiOperation(value = "分页+条件查询",notes = "用户中要含角色信息")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "pageNum", value = "页码", required = true, dataType = "int", paramType = "query"),
            @ApiImplicitParam(name = "pageSize", value = "每页条数", required = true, dataType = "int", paramType = "query"),
            @ApiImplicitParam(name = "username", value = "用户名", dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "status", value = "状态：启用| 禁用", dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "roleId", value = "角色id", dataType = "int", paramType = "query"),
            @ApiImplicitParam(name = "isVip", value = "是否为会员: 0|1", dataType = "int", paramType = "query")
    })
    @GetMapping("/query")
    public Result query(Integer pageNum,Integer pageSize, String
            username, String status, Integer roleId, Integer isVip){
        IPage<UserExtend> query = userService.query(pageNum, pageSize, username, status, roleId, isVip);
        return Result.success(query);
    }
}

