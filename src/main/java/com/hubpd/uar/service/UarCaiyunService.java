package com.hubpd.uar.service;

import com.hubpd.uar.domain.CbWxContent;

/**
 * uar的caiyun内容库
 *
 * @author cpc
 * @create 2018-06-22 16:44
 **/
public interface UarCaiyunService {
    /**
     * 内容库彩云数据插入
     * @param cbWxContent       微信文章详情信息
     * @return
     */
    public String insertCaiyun(CbWxContent cbWxContent) throws Exception;
}
