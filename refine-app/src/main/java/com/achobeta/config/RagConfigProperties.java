package com.achobeta.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "weaviate.config", ignoreInvalidFields = true)
public class RagConfigProperties {

    /** host:ip */
    private String host;
    /** 端口 */
    private int port;
    /** scheme */
    private String scheme;
    /** api-key */
    private String apiKey;
    /** timeout */
    private int timeout;
    /** class-name */
    private String className;

}
