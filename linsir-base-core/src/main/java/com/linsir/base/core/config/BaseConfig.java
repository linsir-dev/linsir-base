package com.linsir.base.core.config;

import com.linsir.base.core.util.PropertiesUtils;
import com.linsir.base.core.util.S;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.DefaultResourceLoader;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author linsir
 * @title: BaseConfig
 * @projectName linsir
 * @description: 系统默认配置
 * @date 2022/3/13 22:22
 */
@Slf4j
public class BaseConfig {

    /** 从当前配置文件获取配置参数值
	 * @param key
	 * @return
             */
    public static String getProperty(String key){
        return PropertiesUtils.get(key);
    }

    /**
     * 从当前配置文件获取配置参数值
     * @param key
     * @param defaultValue 默认值
     * @return
     */
    public static String getProperty(String key, String defaultValue){
        String value = PropertiesUtils.get(key);
        return value != null? value : defaultValue;
    }

    /***
     *  从默认的/指定的 Properties文件获取boolean值
     * @param key
     * @return
     */
    public static boolean isTrue(String key){
        return PropertiesUtils.getBoolean(key);
    }

    /***
     * 获取int类型
     * @param key
     * @return
     */
    public static Integer getInteger(String key){
        return PropertiesUtils.getInteger(key);
    }

    /***
     * 获取int类型
     * @param key
     * @return
     */
    public static Integer getInteger(String key, int defaultValue){
        Integer value = PropertiesUtils.getInteger(key);
        return value != null? value : defaultValue;
    }

    private static Integer cutLength = null;
    /***
     * 获取截取长度
     * @return
     */
    public static int getCutLength(){
        if(cutLength == null){
            cutLength = PropertiesUtils.getInteger("linsir.core.cut-length");
            if(cutLength == null){
                cutLength = 20;
            }
        }
        return cutLength;
    }

    private static Integer pageSize = null;
    /***
     * 默认页数
     * @return
     */
    public static int getPageSize() {
        if(pageSize == null){
            pageSize = PropertiesUtils.getInteger("linsir.core.page-size");
            if(pageSize == null){
                pageSize = 20;
            }
        }
        return pageSize;
    }

    private static Integer batchSize = null;
    /***
     * 获取批量插入的每批次数量
     * @return
     */
    public static int getBatchSize() {
        if(batchSize == null){
            batchSize = PropertiesUtils.getInteger("linsir.core.batch-size");
            if(batchSize == null){
                batchSize = 1000;
            }
        }
        return batchSize;
    }

    private static String ACTIVE_FLAG_VALUE = null;
    /**
     * 获取有效记录的标记值，如 0
     * @return
     */
    public static String getActiveFlagValue(){
        if(ACTIVE_FLAG_VALUE == null){
            ACTIVE_FLAG_VALUE = getProperty("mybatis-plus.global-config.db-config.logic-not-delete-value", "0");
        }
        return ACTIVE_FLAG_VALUE;
    }

    public static String getProjectPath(){
        // 如果配置了工程路径，则直接返回，否则自动获取。
        String projectPath = BaseConfig.getProperty("projectPath");

        if (S.isNotBlank(projectPath)){
            return projectPath;
        }
        try {
            File file = new DefaultResourceLoader().getResource("").getFile();
            if (file != null){
                while(true){
                    File f = new File(file.getPath() + File.separator + "src" + File.separator + "main");
                    if (f == null || f.exists()){
                        break;
                    }
                    if (file.getParentFile() != null){
                        file = file.getParentFile();
                    }else{
                        break;
                    }
                }
                projectPath = file.toString();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return projectPath;
    }



    /**
    * @Description:
    * @Param: java.util.List<java.lang.String>
    * @return: []
    * @Author: linsir
    * @Date: 2:58 2023/5/1
    */
    //忽略的表
    public static List<String> getIgnoreTables()
    {
        List<String> ignoreTables = new ArrayList<>();
        String ignoreTableStrs = getProperty("linsir.core.ignoreTables");
        if(S.isNotBlank(ignoreTableStrs))
        {
            ignoreTables = S.splitToList(ignoreTableStrs);
        }
        return ignoreTables;
    }
}
