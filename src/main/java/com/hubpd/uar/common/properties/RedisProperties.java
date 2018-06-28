package com.hubpd.uar.common.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * redis属性读取
 *
 * @author cpc
 * @create 2018-06-27 10:20
 **/
//@Component
//@ConfigurationProperties(prefix = "spring.redis.pool")
public class RedisProperties {
    /**
     * redis集群节点
     */
    private String clusterNodes;
    /**
     * 连接超时时间
     */
    private Integer commandTimeout;
    /**
     * 重连次数
     */
    private Integer maxAttempts;

    public String getClusterNodes() {
        return clusterNodes;
    }

    public void setClusterNodes(String clusterNodes) {
        this.clusterNodes = clusterNodes;
    }

    public Integer getCommandTimeout() {
        return commandTimeout;
    }

    public void setCommandTimeout(Integer commandTimeout) {
        this.commandTimeout = commandTimeout;
    }

    public Integer getMaxAttempts() {
        return maxAttempts;
    }

    public void setMaxAttempts(Integer maxAttempts) {
        this.maxAttempts = maxAttempts;
    }
}
