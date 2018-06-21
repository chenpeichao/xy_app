package com.hubpd.uar.service;

import com.hubpd.uar.domain.CbWxContent;

/**
 * 微信文章详情
 *
 * @author cpc
 * @create 2018-06-21 10:45
 **/
public interface CbWxContentService {
    /**
     * 根据微信文章地址获取唯一文章信息
     * @param url       微信文章地址
     * @return
     */
    public CbWxContent findOneByUrl(String url);

    /**
     * 保存微信文章信息
     * @param cbWxContent       微信文章实体
     */
    public void saveOne(CbWxContent cbWxContent);
}
