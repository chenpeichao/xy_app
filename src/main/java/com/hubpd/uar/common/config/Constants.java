package com.hubpd.uar.common.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * 系统常量
 *
 * @author cpc
 * @create 2018-06-22 16:55
 **/
@Component
public class Constants {
    /** uar的caiyun入库链接 */
    public static String UAR_CAIYUN_INSERT_URL;
    /** uar的caiyun入库的媒体id */
    public static Long UAR_CAIYUN_INSERT_MEDIA_ID;

    @Value("${uar_caiyun_insert_url}")
    public void setUarCaiyunInsertUrl(String uarCaiyunInsertUrl) {
        this.UAR_CAIYUN_INSERT_URL = uarCaiyunInsertUrl;
    }
    @Value("${uar_caiyun_insert_media_id}")
    public void setUarCaiyunInsertMediaId(Long uarCaiyunInsertMediaId) {
        this.UAR_CAIYUN_INSERT_MEDIA_ID = uarCaiyunInsertMediaId;
    }
}
