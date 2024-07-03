package com.linsir.base.core.secure;

import lombok.Data;

import java.io.Serializable;

/**
 * @ClassName : LinsirUser
 * @Description :
 * @Author : Linsir
 * @Date: 2023-12-20 01:10
 */
@Data
public class LinsirUser implements Serializable {
    private static final long serialVersionUID = 1L;
    /**
     * 客户端id
     */
    private String clientId;

    /**
     * 用户id
     */
    private Long userId;
    /**
     * 账号
     */
    private String account;
    /**
     * 用户名
     */
    private String userName;
    /**
     * 昵称
     */
    private String nickName;
    /**
     * 租户ID
     */
    private String tenantId;
    /**
     * 第三方认证ID
     */
    private String oauthId;
    /**
     * 部门id
     */
    private String deptId;
    /**
     * 岗位id
     */
    private String postId;
    /**
     * 角色id
     */
    private String roleId;
    /**
     * 角色名
     */
    private String roleName;
    /**
     * 用户类型
     */
    private String type;
}
