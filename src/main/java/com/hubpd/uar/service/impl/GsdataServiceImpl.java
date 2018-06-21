package com.hubpd.uar.service.impl;

import cn.pdmi.crt.utils.BaseMyBatisUtils;
import com.hubpd.uar.common.config.SpiderConfig;
import com.hubpd.uar.domain.CbWxContent;
import com.hubpd.uar.domain.CbWxList;
import com.hubpd.uar.repository.CbWxListRepository;
import com.hubpd.uar.service.CbWxContentService;
import com.hubpd.uar.service.CbWxListService;
import com.hubpd.uar.service.GsdataService;
import iims.crt.gsdata.DataApi;
import iims.crt.gsdata.GroupMonitorAddResult;
import iims.crt.gsdata.ResNickNameOneResult;
import iims.crt.gsdata.WxUrlMonitorResult;
import org.apache.commons.lang.time.DateUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 清博接口数据获取实现类
 *
 * @author cpc
 * @create 2018-06-20 17:34
 **/
@Service
public class GsdataServiceImpl implements GsdataService {
    private Logger logger = Logger.getLogger(GsdataServiceImpl.class);

    @Autowired
    private CbWxListService cbWxListService;
    @Autowired
    private CbWxContentService cbWxContentService;

    public void execute() {
        logger.info("GsdataPushOriginNewsKeywordsSpider start");
        // 设置commons-logging的日志不进行输出
        System.setProperty("org.apache.commons.logging.Log", "org.apache.commons.logging.impl.NoOpLog");

        if ("1".equals(SpiderConfig.SPIDE_ONLY_ONE)) {
            // 抓取单天的
            spiderSingle();
        } else {
            // 抓取全部的
            spiderAll();
        }

        logger.info("GsdataPushOriginNewsKeywordsSpider end");
    }

    /**
     * 抓取所有的数据
     */
    private void spiderAll() {
        try {
            String gsdataNickNameId = null;
            //group_id公众号分组名称，status：0：有效；1：失效
            List<CbWxList> cbWxListList = cbWxListService.findAll(SpiderConfig.SPIDER_WX_GROUP_ID, 1);

            // 循环处理
            for (CbWxList cbWxList : cbWxListList) {

                // 获取查询的nickname_id
                gsdataNickNameId = getGsdataNickNameId(cbWxList);
                if (StringUtils.isBlank(gsdataNickNameId)) {
                    continue;
                }

                // 获取3天的数据
                int lengh = 3;
                for (int i = 0; i < lengh; i++) {
                    int pageNo = 1;
                    while (true) {
                        // 获取数据
                        List<WxUrlMonitorResult> results =
                                DataApi.getInstance().getResponseData(pageNo,
                                        gsdataNickNameId,
                                        DateUtils.addDays(new Date(), -i),
                                        DateUtils.addDays(new Date(), -i));

                        // 获取结果
                        if (results != null && results.size() > 0) {

                            for (WxUrlMonitorResult wxUrlMonitorResult : results) {
                                CbWxContent cbWxContent = new CbWxContent();
                                cbWxContent.setName(wxUrlMonitorResult.getName());
                                cbWxContent.setWxName(wxUrlMonitorResult.getWxName());
                                cbWxContent.setNicknameId(wxUrlMonitorResult.getNickNameId());
                                cbWxContent.setPosttime(wxUrlMonitorResult.getPostTime());
                                cbWxContent.setTitle(wxUrlMonitorResult.getTitle());
                                cbWxContent.setContent(wxUrlMonitorResult.getContent());
                                cbWxContent.setUrl(wxUrlMonitorResult.getUrl());
                                cbWxContent.setAddTime(wxUrlMonitorResult.getAddTime());
                                cbWxContent.setMonitorTime(wxUrlMonitorResult.getMonitorTime());
                                cbWxContent.setReadnum(wxUrlMonitorResult.getReadNum());
                                cbWxContent.setLikenum(wxUrlMonitorResult.getLikeNum());
                                cbWxContent.setTop(wxUrlMonitorResult.getTop());
                                cbWxContent.setIspush(wxUrlMonitorResult.getIsPush());
                                cbWxContent.setPicurl(wxUrlMonitorResult.getPicUrl());
                                cbWxContent.setSourceurl(wxUrlMonitorResult.getSourceUrl());
                                cbWxContent.setAuthor(wxUrlMonitorResult.getAuthor());
                                cbWxContent.setSummary(wxUrlMonitorResult.getDesc());
                                cbWxContent.setVideourl(wxUrlMonitorResult.getVideoUrl());
                                cbWxContent.setImgsurl(wxUrlMonitorResult.getImgsUrl());
                                CbWxContent result = cbWxContentService.findOneByUrl(cbWxContent.getUrl());
                                if (result == null) {
                                    cbWxContentService.saveOne(cbWxContent);
                                }
                            }
                            // 如果没有内容了，直接跳出
                            if (results.size() < DataApi.MaxRows_Request) {
                                break;
                            }
                        } else {
                            break;
                        }

                        pageNo++;
                    }
                }
            }

        } catch (Exception ex) {
            logger.error("error:", ex);
        }
    }

    /**
     * 抓取单个公众号的指定日期间的文章数据
     */
    private void spiderSingle() {
        try {
            String gsdataNickNameId = null;
            List<CbWxList> entities;
            if (StringUtils.isNotBlank(SpiderConfig.SPIDER_NICKNAME_ID)) {
                //根据配置文件配置，查询指定微信公众号信息
                entities = cbWxListService.findOneByNicknameId(SpiderConfig.SPIDER_NICKNAME_ID);
            } else {
                //当配置文件中没有配置指定文章信息时，读取指定组id的有效微信公众号列表数据
                entities = cbWxListService.findAll(SpiderConfig.SPIDER_WX_GROUP_ID, 0);
            }

//            Date startDay = DateUtils.parseDate(SpiderConfig.SPIDER_START_DAY, new String[]{"yyyy-MM-dd"});
//            Date endDay = DateUtils.parseDate(SpiderConfig.SPIDER_END_DAY, new String[]{"yyyy-MM-dd"});
            Date startDay = new Date();
            Date endDay = startDay;
            Date date = startDay;
            while (date.compareTo(endDay) <= 0) {
                // 循环处理
                for (CbWxList entity : entities) {
                    // 获取查询的nickname_id
                    gsdataNickNameId = getGsdataNickNameId(entity);
                    if (StringUtils.isBlank(gsdataNickNameId)) {
                        continue;
                    }
                    int pageNo = 1;
                    while (true) {
                        List<WxUrlMonitorResult> results = new ArrayList<WxUrlMonitorResult>();
                        // 获取数据
                        try {
                            results = DataApi.getInstance().getResponseData(pageNo,
                                    gsdataNickNameId,
                                    date,
                                    date);
                        } catch (Exception e) {
                            logger.error("公众号【" + entity.getNickname() + "---" + entity.getNicknameId() + "】数据抓取异常！！",e);
                        }

                        // 获取结果
                        if (results != null && results.size() > 0) {

                            for (WxUrlMonitorResult wxUrlMonitorResult : results) {
                                CbWxContent newEntity = new CbWxContent();
                                newEntity.setName(wxUrlMonitorResult.getName());
                                newEntity.setWxName(wxUrlMonitorResult.getWxName());
                                newEntity.setNicknameId(wxUrlMonitorResult.getNickNameId());
                                newEntity.setPosttime(wxUrlMonitorResult.getPostTime());
                                newEntity.setTitle(wxUrlMonitorResult.getTitle());
                                newEntity.setContent(wxUrlMonitorResult.getContent());
                                newEntity.setUrl(wxUrlMonitorResult.getUrl());
                                newEntity.setAddTime(wxUrlMonitorResult.getAddTime());
                                newEntity.setMonitorTime(wxUrlMonitorResult.getMonitorTime());
                                newEntity.setReadnum(wxUrlMonitorResult.getReadNum());
                                newEntity.setLikenum(wxUrlMonitorResult.getLikeNum());
                                newEntity.setTop(wxUrlMonitorResult.getTop());
                                newEntity.setIspush(wxUrlMonitorResult.getIsPush());
                                newEntity.setPicurl(wxUrlMonitorResult.getPicUrl());
                                newEntity.setSourceurl(wxUrlMonitorResult.getSourceUrl());
                                newEntity.setAuthor(wxUrlMonitorResult.getAuthor());
                                newEntity.setSummary(wxUrlMonitorResult.getDesc());
                                newEntity.setVideourl(wxUrlMonitorResult.getVideoUrl());
                                newEntity.setImgsurl(wxUrlMonitorResult.getImgsUrl());
                                CbWxContent result = cbWxContentService.findOneByUrl(newEntity.getUrl());
                                if (result == null) {
                                    cbWxContentService.saveOne(newEntity);
                                }
                            }
                            // 如果没有内容了，直接跳出
                            if (results.size() < DataApi.MaxRows_Request) {
                                break;
                            }
                        } else {
                            break;
                        }

                        pageNo++;
                    }

                }

                // 添加1天
                date = DateUtils.addDays(date, 1);
            }

        } catch (Exception ex) {
            logger.error("error:", ex);
        }
    }

    /**
     * 根据entity的信息获取清博的nickname
     *
     * @param cbWxList 实体信息
     * @return 返回gsdataNameId
     */
    private String getGsdataNickNameId(CbWxList cbWxList) {
        String gsdataNickNameId = null;
        try {
            if (StringUtils.isBlank(cbWxList.getGsdataNicknameId()) && StringUtils.isNotBlank(cbWxList.getNewsUrl())) {

                // 添加微信帐号
                GroupMonitorAddResult groupMonitorAddResult = DataApi.getInstance().addWeixin2Group(cbWxList.getNewsUrl());
                if (groupMonitorAddResult == null || groupMonitorAddResult.getWxNickname() == null) {
                    return gsdataNickNameId;
                }

                // 获取查询的nickname_id
                ResNickNameOneResult resNickNameOneResult =
                        DataApi.getInstance().getNickNameOne(cbWxList.getNicknameId());

                if (resNickNameOneResult == null || resNickNameOneResult.getId() < 0) {
                    // 添加微信帐号
                    groupMonitorAddResult = DataApi.getInstance().addWeixin2Group(cbWxList.getNewsUrl());
                    if (groupMonitorAddResult == null || groupMonitorAddResult.getWxNickname() == null) {
                        return gsdataNickNameId;
                    }

                    // 再获取一次
                    resNickNameOneResult =
                            DataApi.getInstance().getNickNameOne(cbWxList.getNicknameId());

                    if (resNickNameOneResult == null || resNickNameOneResult.getId() < 0) {
                        return gsdataNickNameId;

                    }
                    // 设置对象
                    cbWxList.setGsdataNicknameId(String.valueOf(resNickNameOneResult.getId()));

                } else {
                    cbWxList.setGsdataNicknameId(String.valueOf(resNickNameOneResult.getId()));
                }

                // 保存数据
                cbWxListService.update(cbWxList);
            }
        } catch (Exception ex) {
            logger.error("getGsdataNickNameId error:");
        }

        gsdataNickNameId = cbWxList.getGsdataNicknameId();

        return gsdataNickNameId;
    }
}
