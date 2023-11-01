package com.briup.cms.web.controller;


import com.briup.cms.service.RoleService;
import com.briup.cms.util.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.web.bind.annotation.RestController;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author briup
 * @since 2023-08-31
 */
@RestController
@RequestMapping("/auth/role")
public class RoleController {
    @Autowired
    private RoleService roleService;

    @GetMapping("/getAll")
    public Result getAll(){
        return Result.success(roleService.list());
    }
}

