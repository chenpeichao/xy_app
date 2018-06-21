package com.hubpd.uar.service.impl;

import com.hubpd.uar.domain.CbWxContent;
import com.hubpd.uar.repository.CbWxContentRepository;
import com.hubpd.uar.service.CbWxContentService;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 微信文章详情
 *
 * @author cpc
 * @create 2018-06-21 10:46
 **/
@Service
public class CbWxContentServiceImpl implements CbWxContentService {
    private Logger logger = Logger.getLogger(CbWxContentServiceImpl.class);

    @Autowired
    private CbWxContentRepository cbWxContentRepository;

    /**
     * 根据微信文章地址获取唯一文章信息
     * @param url       微信文章地址
     * @return
     */
    public CbWxContent findOneByUrl(String url) {
        if(StringUtils.isBlank(url)) {
            return null;
        }
        return cbWxContentRepository.findOneByUrl(url);
    }

    /**
     * 保存微信文章信息
     * @param cbWxContent       微信文章实体
     */
    public void saveOne(CbWxContent cbWxContent) {
        cbWxContentRepository.saveAndFlush(cbWxContent);
    }
}
