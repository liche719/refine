package com.achobeta.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "pgvector.config", ignoreInvalidFields = true)
public class RagConfigProperties {

    /** host:ip */
    private String host;
    /** 端口 */
    private int port;
    /** 数据库名 */
    private String database;
    /** 用户名 */
    private String user;
    /** 账密 */
    private String password;
    /** 向量维度 */
    //private String vectorDimension;

}
