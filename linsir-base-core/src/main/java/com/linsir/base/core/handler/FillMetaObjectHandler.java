package com.linsir.base.core.handler;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import com.baomidou.mybatisplus.core.metadata.TableInfo;
import com.linsir.base.core.config.BaseConfig;
import com.linsir.base.core.dto.SysUserDto;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.stereotype.Component;

import java.util.Date;

/**
 * @author ：linsir
 * @date ：Created in 2022/4/1 17:18
 * @description：自动填充
 * @modified By：
 * @version: 0.0.1
 */
@Component
@Slf4j
public class FillMetaObjectHandler implements MetaObjectHandler {


  private SysUserDto sysUserDto = new SysUserDto();


    /**
     * 增加时候插入
     * @param metaObject
     */
    @Override
    public void insertFill(MetaObject metaObject) {

        /*if(sysUserDto == null)
        {*/
            getUseInfo();
       /* }
*/
        log.info("无论是否获取用户信息，插入时间都需要放入");
        //时间是默认需要插入进去的
        this.strictInsertFill(metaObject, "createdTime", Date.class, new Date());
        this.strictInsertFill(metaObject, "updatedTime", Date.class, new Date());

        // 租户也有账户
        if(isFillMetaTable(metaObject) && isTenant())
        {
            log.info("能够获取用户信息，当前用户是租户");
            this.strictInsertFill(metaObject,"tenantCode",String.class,sysUserDto.getTenantCode());
            this.strictInsertFill(metaObject,"createdBy",String.class,sysUserDto.getUsername());
            this.strictInsertFill(metaObject,"updatedBy",String.class,sysUserDto.getUsername());
        }
        //有账户，但无租户信息
        if(sysUserDto!=null)
        {
            log.info("能够获取用户信息，平台租户");
            //无论是租户还是，系统用户，插入数据时候，都插入当前用户
            this.strictInsertFill(metaObject,"createdBy",String.class,sysUserDto.getUsername());
            this.strictInsertFill(metaObject,"updatedBy",String.class,sysUserDto.getUsername());
        }else
        {
            log.info("未能获得用户信息，自动插入sys");
            this.strictInsertFill(metaObject,"createdBy",String.class,"sys_auto");
            this.strictInsertFill(metaObject,"updatedBy",String.class,"sys_auto");
        }
    }

    /**
     * 跟新列表
     * @param metaObject
     */
    @Override
    public void updateFill(MetaObject metaObject) {

        /*if(sysUserDto == null)
        {*/
            getUseInfo();
       /* }*/

        this.strictInsertFill(metaObject, "updatedTime", Date.class, new Date());
        if(sysUserDto!=null)
        {
            log.info("无论是否获取用户信息，插入时间都需要放入");
            this.strictInsertFill(metaObject,"updatedBy",String.class,sysUserDto.getUsername());
        }
        else
        {
            log.info("未能获得用户信息，自动插入sys");
            this.strictInsertFill(metaObject,"updatedBy",String.class,"sys_auto");
        }
    }





    /***
    * @Description: 判断是否填充
    * @Param: [metaObject]
    * @return: boolean
    * @Author: Linsir
    * @Date: 2022/4/14 14:27
    */
    public boolean isFillMetaTable(MetaObject metaObject)
    {
        boolean isFillMeta = true;

        TableInfo tableInfo = findTableInfo(metaObject);

        /* 以sys_ 开头的不插入信息*/
        if (tableInfo.getTableName().startsWith("sys_"))
        {
            log.info("系统表：忽略租户编码的插入");
            isFillMeta = false;
        }

        if(sysUserDto == null)
        {
            log.info("未获的租户信息：不需要插入租户编码");
            isFillMeta = false;
        }

        // 特殊固定表
        if (tableInfo.getTableName().equals("dictionary"))
        {
            log.info("特殊固定表：不需要插入租户编码");
            isFillMeta = false;
        }

        // 系统配置的表
       if(BaseConfig.getIgnoreTables().contains(tableInfo.getTableName()))
       {
           log.info("系统配置的表：不需要插入租户编码");
           isFillMeta = false;
       }

        return isFillMeta;
    }



    public void getUseInfo()
    {
        /*log.info("插入书之前，利用getUseInfo查看是否获得用户信息" + SecurityContextHolder.getContext().getAuthentication());
        if(SecurityContextHolder.getContext().getAuthentication() != null)
        {
            try {
                log.info("获得到用户信息，并且加入到sysUserDto");
                ObjectMapper objectMapper = new ObjectMapper();
                Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
                this.sysUserDto = objectMapper.convertValue(principal,SysUserDto.class) ;
            } catch (Exception e) {
                log.error("转化失败" + e.getMessage());
            }
        }*/
        this.sysUserDto.setUserId(1L);
        this.sysUserDto.setUsername("linsir");
    }


    /**
     * 登录账户是系统管理员，还是租户
     * @param
     * @return
     */
    public boolean isTenant()
    {
        boolean isTenant = true;
        if(sysUserDto !=null && sysUserDto.getType() == 0)
        {
            isTenant = false;
        }
        return isTenant;
    }
}
