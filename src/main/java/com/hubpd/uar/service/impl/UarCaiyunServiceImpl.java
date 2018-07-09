package com.hubpd.uar.service.impl;

import com.google.gson.JsonObject;
import com.hubpd.uar.common.utils.Constants;
import com.hubpd.uar.domain.CbWxContent;
import com.hubpd.uar.service.UarCaiyunService;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpStatus;
import org.apache.http.StatusLine;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.springframework.stereotype.Service;

import java.io.IOException;

/**
 * uar的caiyun内容库
 *
 * @author cpc
 * @create 2018-06-22 16:45
 **/
@Service
public class UarCaiyunServiceImpl implements UarCaiyunService{

    /**
     * 内容库彩云数据插入
     * @param cbWxContent       微信文章详情信息
     * @return
     */
    public String insertCaiyun(CbWxContent cbWxContent) throws Exception {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("publishTime",cbWxContent.getPosttime());
        jsonObject.addProperty("source", cbWxContent.getName());
        jsonObject.addProperty("title", cbWxContent.getTitle());
        jsonObject.addProperty("url", cbWxContent.getUrl());
        jsonObject.addProperty("cover", cbWxContent.getPicurl());
        jsonObject.addProperty("author", cbWxContent.getAuthor());
        if (StringUtils.isNotBlank(cbWxContent.getContentHtml())) {
//            jsonObject.addProperty("body", "<p class='txt'>" + cbWxContent.getContentHtml() + "</p>");
            jsonObject.addProperty("body", "<p class='txt'>" + cbWxContent.getContentHtml().replace("data-", "") + "</p>");
//            jsonObject.addProperty("body", cbWxContent.getContentHtml());
        } else {
            jsonObject.addProperty("body", cbWxContent.getContentHtml());
        }
//        jsonObject.addProperty("bodyAsHtml", );
        //媒体为：党媒公共平台
        jsonObject.addProperty("taskId", -1);
//        jsonObject.addProperty("video", "https://v.qq.com/iframe/preview.html?vid=u06371hxh3z&width=500&height=375&auto=0");
//        jsonObject.addProperty("mediaId", Constants.UAR_CAIYUN_INSERT_MEDIA_ID);
        jsonObject.addProperty("columnName", "测试");
        String result= this.doPost(Constants.UAR_CAIYUN_INSERT_URL, jsonObject.toString());
        System.err.println("==========插入内容库===============" + cbWxContent.getUrl());
        System.out.println(result);
        return result;
    }

    /**
     * post请求（用于请求json格式的参数）
     * @param url
     * @param params
     * @return
     */
    private String doPost(String url, String params) throws Exception {
        CloseableHttpClient httpclient = HttpClients.createDefault();
        HttpPost httpPost = new HttpPost(url);// 创建httpPost
        httpPost.setHeader("Accept", "application/json");
        httpPost.setHeader("Content-Type", "application/json");
        String charSet = "UTF-8";
        StringEntity entity = new StringEntity(params, charSet);
        httpPost.setEntity(entity);
        CloseableHttpResponse response = null;
        try {

            response = httpclient.execute(httpPost);
            StatusLine status = response.getStatusLine();
            int state = status.getStatusCode();
            if (state == HttpStatus.SC_OK) {
                HttpEntity responseEntity = response.getEntity();
                String jsonString = EntityUtils.toString(responseEntity);
                return jsonString;
            }
            else{
                System.out.println("请求返回:"+state+"("+url+")");
            }
        } finally {
            if (response != null) {
                try {
                    response.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            try {
                httpclient.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }
}