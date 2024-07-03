package com.linsir.base.core.config;

import com.baomidou.mybatisplus.annotation.DbType;
import com.baomidou.mybatisplus.core.MybatisConfiguration;
import com.baomidou.mybatisplus.core.config.GlobalConfig;
import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.TenantLineInnerInterceptor;
import com.baomidou.mybatisplus.extension.spring.MybatisSqlSessionFactoryBean;
import com.linsir.base.core.constant.CommonConstant;
import com.linsir.base.core.handler.BaseTenantLineHandler;
import com.linsir.base.core.handler.FillMetaObjectHandler;
import jakarta.annotation.Resource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;

/**
 * @author ：linsir
 * @date ：Created in 2022/4/1 0:22
 * @description：基础mybatisplus基础配置
 * @modified By：
 * @version: 0.0.1
 */
@Configuration
public class BaseMybatisplusConfig {


    @Resource
    private DataSource dataSource;

    /**
    * @Description: mybatisSqlSessionFactoryBean
    * @Param: []
    * @return: com.baomidou.mybatisplus.extension.spring.MybatisSqlSessionFactoryBean
    * @Author: Linsir
    * @Date: 2022/4/1 16:48
    */
    @Bean
    public MybatisSqlSessionFactoryBean mybatisSqlSessionFactoryBean()
    {
        MybatisSqlSessionFactoryBean mybatisSqlSessionFactoryBean = new MybatisSqlSessionFactoryBean();
        /*...*/
        mybatisSqlSessionFactoryBean.setConfiguration(mybatisConfiguration());
        mybatisSqlSessionFactoryBean.setGlobalConfig(globalConfig());
        mybatisSqlSessionFactoryBean.setPlugins(mybatisPlusInterceptor());
        mybatisSqlSessionFactoryBean.setDataSource(dataSource);
        return mybatisSqlSessionFactoryBean;
    }


    /**
    * @Description:
    * @Param: []
    * @return: com.baomidou.mybatisplus.core.MybatisConfiguration
    * @Author: Linsir
    * @Date: 2022/4/1 16:48
    */
    @Bean
    public MybatisConfiguration mybatisConfiguration()
    {
        /*MyBatis 配置文件位置，如果您有单独的 MyBatis 配置，请将其路径配置到 configLocation  继承Mybatis*/
        MybatisConfiguration mybatisConfiguration = new MybatisConfiguration();
        /*
        * setLogImpl 日志配置
        * setMapUnderscoreToCamelCase 驼峰数据库字段
        *  */
        mybatisConfiguration.setLogImpl(org.apache.ibatis.logging.stdout.StdOutImpl.class);
        mybatisConfiguration.setMapUnderscoreToCamelCase(true);
        return mybatisConfiguration;
    }

    /***
    * @Description: 全局配置
    * @Param: []
    * @return: com.baomidou.mybatisplus.core.config.GlobalConfig.DbConfig
    * @Author: Linsir
    * @Date: 2022/4/15 12:27
    */
    @Bean
    public GlobalConfig.DbConfig dbConfig()
    {
        GlobalConfig.DbConfig dbConfig = new GlobalConfig.DbConfig();
        /*
        * setLogicDeleteField 逻辑删除字段
        * */
        dbConfig.setLogicDeleteField(CommonConstant.COLUMN_IS_DELETED);

        return dbConfig;
    }

    /***
    * @Description:
    * @Param: []
    * @return: com.baomidou.mybatisplus.core.config.GlobalConfig
    * @Author: Linsir
    * @Date: 2022/4/15 12:29
    */
    @Bean
    public GlobalConfig globalConfig()
    {
        GlobalConfig globalConfig = new GlobalConfig();
        /*...*/
        globalConfig.setMetaObjectHandler(new FillMetaObjectHandler());
        return  globalConfig;
    }

    @Bean
    public MybatisPlusInterceptor mybatisPlusInterceptor()
    {
        MybatisPlusInterceptor mybatisPlusInterceptor = new MybatisPlusInterceptor();
        mybatisPlusInterceptor.addInnerInterceptor(paginationInnerInterceptor());
        mybatisPlusInterceptor.addInnerInterceptor(tenantLineInnerInterceptor());
        return mybatisPlusInterceptor;
    }

    /**
     * 自动分页: PaginationInnerInterceptor
     * 多租户: TenantLineInnerInterceptor
     * 动态表名: DynamicTableNameInnerInterceptor
     * 乐观锁: OptimisticLockerInnerInterceptor
     * sql 性能规范: IllegalSQLInnerInterceptor
     * 防止全表更新与删除: BlockAttackInnerInterceptor
     *
    * @Description: 分页插件
    * @Param: [] 
    * @return: com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor 
    * @Author: linsir
    * @Date: 2022/4/1 1:40
    */
    @Bean
    public PaginationInnerInterceptor paginationInnerInterceptor()
    {
        return new PaginationInnerInterceptor(DbType.MYSQL);
    }

    
    /**
    * @Description: 租户插件
    * @Param: []
    * @return: com.baomidou.mybatisplus.extension.plugins.inner.TenantLineInnerInterceptor
    * @Author: Linsir
    * @Date: 2022/4/1 17:17
    */
    @Bean
    public TenantLineInnerInterceptor tenantLineInnerInterceptor()
    {
        return new TenantLineInnerInterceptor (new BaseTenantLineHandler());
    }
}
