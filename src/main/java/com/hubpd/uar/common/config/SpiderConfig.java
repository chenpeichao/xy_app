package com.hubpd.uar.common.config;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.PropertyPlaceholderConfigurer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import java.io.File;

/**
 * 爬虫的配置文件
 * Created by MuRui on 2017/6/6.
 */
@Component
public class SpiderConfig {
    /** 需要补充的天数 */
    public static String SPIDE_ONLY_ONE;
    /** 需要补充的天数 */
    public static String SPIDER_START_DAY;
    /** 需要补充的天数 */
    public static String SPIDER_END_DAY;
    /** 需要补充的微信 */
    public static String SPIDER_NICKNAME_ID;
    /** 核心线程数 */
    public static Integer THREAD_CORE_NUM;
    /** 最大运行线程数 */
    public static Integer THREAD_MAX_NUM;
    /** 队列数 */
    public static Integer THREAD_QUEUE_NUM;
    /** 查询指定分组的公众号（不指定查询所有分组） */
    public static String SPIDER_WX_GROUP_ID;


    @Value("${spider_only_one}")
    public void setSpideOnlyOne(String spideOnlyOne) {
        this.SPIDE_ONLY_ONE = spideOnlyOne;
    }

    @Value("${spider_start_day}")
    public void setSpiderStartDay(String spiderStartDay) {
        this.SPIDER_START_DAY = spiderStartDay;
    }

    @Value("${spider_end_day}")
    public void setSpiderEndDay(String spiderEndDay) {
        this.SPIDER_END_DAY = spiderEndDay;
    }

    @Value("${spider_nickname_id}")
    public void setSpiderNickNameId(String spiderNickNameId) {
        this.SPIDER_NICKNAME_ID = spiderNickNameId;
    }

    @Value("${thread_core_num}")
    public void setThreadCoreNum(Integer threadCoreNum) {
        this.THREAD_CORE_NUM = threadCoreNum;
    }

    @Value("${thread_max_num}")
    public void setThreadMaxNum(Integer threadMaxNum) {
        this.THREAD_MAX_NUM = threadMaxNum;
    }

    @Value("${thread_queue_num}")
    public void setThreadQueueNum(Integer threadQueueNum) {
        this.THREAD_QUEUE_NUM = threadQueueNum;
    }

    @Value("${spider_wx_group_id}")
    public void setSpiderWxGroupId(String spiderWxGroupId) {
        this.SPIDER_WX_GROUP_ID = spiderWxGroupId;
    }
}
