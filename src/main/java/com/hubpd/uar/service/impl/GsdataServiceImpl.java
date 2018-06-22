package com.hubpd.uar.service.impl;

import com.hubpd.uar.common.config.SpiderConfig;
import com.hubpd.uar.common.utils.Md5Utils;
import com.hubpd.uar.domain.CbWxContent;
import com.hubpd.uar.domain.CbWxList;
import com.hubpd.uar.service.CbWxContentService;
import com.hubpd.uar.service.CbWxListService;
import com.hubpd.uar.service.GsdataService;
import com.hubpd.uar.service.UarCaiyunService;
import iims.crt.gsdata.DataApi;
import iims.crt.gsdata.GroupMonitorAddResult;
import iims.crt.gsdata.ResNickNameOneResult;
import iims.crt.gsdata.WxUrlMonitorResult;
import org.apache.commons.lang.time.DateUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

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
    @Autowired
    private UarCaiyunService uarCaiyunService;

    public void execute() {
        logger.info("GsdataPushOriginNewsKeywordsSpider start");
        List<CbWxList> gsDataCbWxListList = new ArrayList<CbWxList>();
        //1、首先判断是否设置了查询指定微信公众号的信息
        if (SpiderConfig.SPIDER_CATCH_IS_APPOINT == 1) {
            //1.1、抓取指定微信公众号的信息
            if(StringUtils.isBlank(SpiderConfig.SPIDER_CATCH_APPOINT_WX_NICKNAME_ID)) {
                logger.error("当查询指定微信号的文章数据时，必须设置微信id！！");
                return;
            }
            //1.1.1、查询指定微信号id公众号信息
            gsDataCbWxListList = cbWxListService.findOneByNicknameId(SpiderConfig.SPIDER_CATCH_APPOINT_WX_NICKNAME_ID);
        } else {
            //1.2、抓取全部的公众号
            //1.2.1、判断是否查询指定公众号分组的公众号
            if(StringUtils.isNotBlank(SpiderConfig.SPIDER_WX_GROUP_ID)) {
                //1.2.1.1、查询指定组的公众号--0:生效状态
                gsDataCbWxListList = cbWxListService.findAll(SpiderConfig.SPIDER_WX_GROUP_ID, 0);
            } else {
                //1.2.1.2、查询全部公众号--0:生效状态
                gsDataCbWxListList = cbWxListService.findAll(null, 0);
            }
    }

        //抓取公众号相关文章信息入库-并入内容库
        spiderGSDataAndInsertUar(gsDataCbWxListList);
        logger.info("GsdataPushOriginNewsKeywordsSpider end");
    }

    /**
     * 查询指定微信公众号集合的文章信息
     * @param gsDataCbWxListList        微信公众号集合
     */
    private void spiderGSDataAndInsertUar(List<CbWxList> gsDataCbWxListList) {
        if(gsDataCbWxListList == null || gsDataCbWxListList.size() == 0) {
            logger.error("未查找到有效的公众号信息！！");
            return;
        }
        String gsdataNickNameId = null;   //定义清博识别的微信公众号标识

        Date startDay = new Date();
        Date endDay = startDay;
        //2、查询对于日期周期是否进行判断--进行设置了，对于开始时间和结束时间进行设置
        if(SpiderConfig.SPIDER_DATA_IS_SET == 1) {
            try {
                startDay = DateUtils.parseDate(SpiderConfig.SPIDER_START_DAY, new String[]{"yyyy-MM-dd"});
                endDay = DateUtils.parseDate(SpiderConfig.SPIDER_END_DAY, new String[]{"yyyy-MM-dd"});
            } catch (ParseException e) {
                logger.error("日期格式错误请重新查看！！", e);
                //当日期格式有问题时，日期设置默认设置当天时间
                startDay = new Date();
                endDay = startDay;
            }
        }
        //用于日期流转变化的日期标识
        Date date = startDay;
        while (date.compareTo(endDay) <= 0) {
            // 循环处理待查询公众号集合
            for (CbWxList cbWxList : gsDataCbWxListList) {
                //1、获取查询的微信id在清博库中的nickname_id
                gsdataNickNameId = getGsdataNickNameId(cbWxList);
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
                        logger.error("公众号【" + cbWxList.getNickname() + "---" + cbWxList.getNicknameId() + "】"+ new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(date)+"数据抓取异常！！",e);
                    }

                    // 获取结果
                    if (results != null && results.size() > 0) {

                        for (WxUrlMonitorResult wxUrlMonitorResult : results) {
                            CbWxContent newEntity = new CbWxContent();
                            newEntity.setName(wxUrlMonitorResult.getName());
                            newEntity.setWxName(wxUrlMonitorResult.getWxName());
                            newEntity.setNicknameId(wxUrlMonitorResult.getNickNameId());
                            try {
                                newEntity.setPosttime(com.hubpd.uar.common.utils.DateUtils.parseDateStrByPattern(wxUrlMonitorResult.getPostTime(), "yyyy-MM-dd HH:mm:ss", "yyyyMMddHHmmss"));
                            } catch (ParseException e) {
                                logger.error("微信文章日期【"+wxUrlMonitorResult.getPostTime()+"】格式【yyyy-MM-dd HH:mm:ss】转换【yyyyMMddHHmmss】格式错误！！", e);
                            }
                            newEntity.setTitle(wxUrlMonitorResult.getTitle());
                            newEntity.setContent(wxUrlMonitorResult.getContent());
                            newEntity.setUrl(wxUrlMonitorResult.getUrl());
                            if(StringUtils.isNotBlank(Md5Utils.getMD5OfStr(wxUrlMonitorResult.getUrl()))) {
                                newEntity.setUrlMd5(Md5Utils.getMD5OfStr(wxUrlMonitorResult.getUrl()));
                            } else {
                                newEntity.setUrlMd5(Md5Utils.getMD5OfStr(UUID.randomUUID().toString()));
                            }
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
                            List<String> urlList = new ArrayList<String>();
                            urlList.add(wxUrlMonitorResult.getUrl());
                            String str = DataApi.getInstance().getWeixinContentByUrls(urlList);

                            JSONObject jsonObject = null;
                            try {
                                jsonObject = new JSONObject(str);
                                JSONArray articleListArray = jsonObject.getJSONArray("returnData");
                                if(articleListArray.length() > 0) {
                                    results = new ArrayList();
                                    WxUrlMonitorResult record = null;

                                    for (int i = 0; i < articleListArray.length(); ++i) {
                                        JSONObject returnData = (JSONObject) articleListArray.get(i);
                                        if (returnData.has("content")) {
                                            newEntity.setContentHtml(returnData.getString("content"));
                                        }
                                    }
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            CbWxContent result = cbWxContentService.findOneByUrlMd5(newEntity.getUrlMd5());
                            if (result == null) {
                                //当数据库中不存在该文章时，进行数据库保存以及向内容库插入
                                cbWxContentService.saveOne(newEntity);
                                //内容库文章数据插入
                                try {
                                    uarCaiyunService.insertCaiyun(newEntity);
                                } catch (Exception e) {
                                    logger.error("插入内容库文章错误：【" +newEntity.getId()+"】", e);
                                }
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
    }

    /**
     * 根据entity的信息获取清博的nickname
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
            logger.error("微信id为【"+cbWxList.getNicknameId()+"】的获取清博库nicknameId异常", ex);
        }

        gsdataNickNameId = cbWxList.getGsdataNicknameId();

        return gsdataNickNameId;
    }
}
