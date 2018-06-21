package com.hubpd.uar.repository;

import com.hubpd.uar.domain.CbWxContent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

/**
 * 微信文章
 *
 * @author cpc
 * @create 2018-06-21 10:51
 **/
public interface CbWxContentRepository extends JpaRepository<CbWxContent, Integer> {
    /**
     * 根据微信文章地址获取唯一文章信息
     * @param url       微信文章地址
     * @return
     */
    @Query(" FROM com.hubpd.uar.domain.CbWxContent cbWxContent WHERE cbWxContent.url = :url ")
    public CbWxContent findOneByUrl(@Param("url")String url);
}
