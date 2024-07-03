package com.linsir.base.core.boot.props;

import com.linsir.base.core.util.PathUtil;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.lang.Nullable;

/**
 * @ClassName : LinsirUploadProperties
 * @Description :
 * @Author : Linsir
 * @Date: 2023-12-19 22:27
 */

@Getter
@Setter
@RefreshScope
@ConfigurationProperties("linsir.upload")
public class LinsirUploadProperties {


    /**
     * 文件保存目录，默认：jar 包同级目录
     */
    @Nullable
    private String savePath = PathUtil.getJarPath();

}
