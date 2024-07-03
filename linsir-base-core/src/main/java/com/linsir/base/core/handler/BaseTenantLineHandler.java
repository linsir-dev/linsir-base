package com.linsir.base.core.handler;

import com.baomidou.mybatisplus.extension.plugins.handler.TenantLineHandler;
import com.linsir.base.core.config.BaseConfig;
import com.linsir.base.core.dto.SysUserDto;
import lombok.extern.slf4j.Slf4j;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.expression.StringValue;


/**
 * @author ：linsir
 * @date ：Created in 2022/4/15 12:16
 * @description：租户拦截器实现
 * @modified By：
 * @version: 0.0.1
 */

@Slf4j
public class BaseTenantLineHandler implements TenantLineHandler {

    private SysUserDto sysUserDto = null;

    public void getUseInfo() {
        /*log.info("getUseInfo查看是否获得用户信息" + SecurityContextHolder.getContext().getAuthentication());
        if (SecurityContextHolder.getContext().getAuthentication() != null) {
            try {
                log.info("获得到用户信息，并且加入到sysUserDto");
                ObjectMapper objectMapper = new ObjectMapper();
                Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
                this.sysUserDto = objectMapper.convertValue(principal,SysUserDto.class) ;
            } catch (Exception e) {
                log.error("SysUserDto 转化失败" + e.getMessage());
            }
        }*/
    }


    @Override
    public Expression getTenantId() {

        String tenantCode = "00000000000";
       // log.info("Expression查看是否获得用户信息" + SecurityContextHolder.getContext().getAuthentication());
        if (sysUserDto!=null) {
            tenantCode = this.sysUserDto.getTenantCode();
        }
        return new StringValue(tenantCode);
    }

    @Override
    public String getTenantIdColumn() {
        return "tenant_code";
    }

    @Override
    public boolean ignoreTable(String tableName) {

        if(sysUserDto==null)
        {
           // log.info("ignoreTable查看是否获得用户信息" + SecurityContextHolder.getContext().getAuthentication());
            getUseInfo();
        }
        /*查看是否能够获得用户西信息*/


        boolean result = false;
        /*系统表*/
        if (tableName.startsWith("sys_")) {
            log.info("以sys_开头的表，忽略租户查询");
            result = true;
        }

        /*白名单*/
        if (BaseConfig.getIgnoreTables().contains(tableName)) {
            log.info("白名单的表，忽略租户查询");
            result = true;
        }


        if (sysUserDto==null) {
            log.info("未获得用户信息时候，忽略租户查询");
            result = true;
        }

        // 特殊固定表
        if (tableName.equals("dictionary")) {
            log.info("特殊的表，忽略租户查询");
            result = true;
        }

        log.info("最终是否忽略：" + result);
        return result;
    }


}
