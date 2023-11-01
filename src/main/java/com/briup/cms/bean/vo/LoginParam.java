package com.briup.cms.bean.vo;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 登录参数bean类
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoginParam {
    private String username;
    private String password;
}
