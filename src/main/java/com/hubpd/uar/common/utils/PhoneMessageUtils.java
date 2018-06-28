package com.hubpd.uar.common.utils;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.apache.http.HttpEntity;
import org.apache.http.ParseException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

/**
 * 手机验证码获取工具类
 *
 * @author cpc
 * @create 2018-06-27 16:02
 **/
public class PhoneMessageUtils {
    private static Logger logger = Logger.getLogger(PhoneMessageUtils.class);

    public static Map<String, Object> getPhoneMessage(String phoneNum) {
        Map<String, Object> resultMap = new HashMap<String, Object>();

        CloseableHttpClient httpClient = HttpClients.createDefault();
        CloseableHttpResponse response = null;
        String url = Constants.MESSAGE_URL;
        String appId = Constants.MESSAGE_APP_ID;
        //生成验证码-随机四个阿拉伯数据
        String strPhoneCode = get4IntegerString();
        String time = new Date().getTime() + "";
        String content = Constants.MESSAGE_CONTENT_MOULD + strPhoneCode;

        try {
            //封装请求参数
            String md5Sign = encodeAndMD5Param(appId, content, phoneNum, time);
            StringBuilder httpGetParam = new StringBuilder();
            httpGetParam.append("appid=" + appId);
            httpGetParam.append("&content=" + content);
            httpGetParam.append("&mobile=" + phoneNum);
            httpGetParam.append("&timestamp=" + time);
            httpGetParam.append("&sign=" + md5Sign.toLowerCase());
            //创建Get请求
            HttpGet httpGet = new HttpGet(url + "?" + httpGetParam.toString());
            //执行Get请求，
            response = httpClient.execute(httpGet);
            //得到响应体
            HttpEntity entity = response.getEntity();
            if (entity != null) {
                String result = EntityUtils.toString(response.getEntity(), "utf-8");
                result = result.replace("\\\\", "\\");
                result = result.replace("\\\"", "\"");
                //result要解析成{"statue":1,"result":{"mobile":"17602987789","content":"\u9a8c\u8bc1\u7801\u4e3a:1732","ctime":1530095200,"res":"\u6210\u529f"}}这种
                JsonObject jo = (JsonObject) new JsonParser().parse(result.substring(1, result.length() - 1));
                //正常请求，结果拆装
                String statue = jo.get("statue").getAsString();
                JsonObject asJsonObject = jo.get("result").getAsJsonObject();
                String res = asJsonObject.get("res").getAsString();
                System.out.println(res);
                //当返回结果正确时，返回并向redis中保存的验证码
                if ("1".equals(statue)) {
                    resultMap.put("status", 1);
                    resultMap.put("msg", strPhoneCode);
                    logger.info("====================用户手机号：" + phoneNum + "=====验证码：" + strPhoneCode + "====================");
                    return resultMap;
                } else {
                    resultMap.put("status", 0);
                    resultMap.put("msg", res);
                    return resultMap;
                }
            }
            //手机号码有问题，返回null
            resultMap.put("status", "0");
            resultMap.put("msg", "手机号码验证失败");
            return resultMap;
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            //消耗实体内容
            if (response != null) {
                try {
                    response.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            //关闭相应 丢弃http连接
            if (httpClient != null) {
                try {
                    httpClient.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        resultMap.put("status", "0");
        resultMap.put("msg", "验证码获取失败");
        return resultMap;
    }

    /**
     * 获取4个整形数字的字符串
     *
     * @return
     */
    private static String get4IntegerString() {
        Random random = new Random();
        String strCode = "";
        for (int i = 0; i < 4; i++) {
            String rand = String.valueOf(random.nextInt(10));
            strCode = strCode + rand;
        }
        return strCode;
    }

    /**
     * 短信接口请求参数封装-编码以及对请求参数封装
     *
     * @param appId    应用程序签名
     * @param content  短信发送内容
     * @param phoneNum 用户手机号码
     * @param time     请求参数时间
     * @return
     * @throws UnsupportedEncodingException
     */
    private static String encodeAndMD5Param(String appId, String content, String phoneNum, String time) throws UnsupportedEncodingException {
        String encodeParam = "appid=first&content=second&mobile=third&timestamp=forth";
        encodeParam = encodeParam.replace("first", appId);
        encodeParam = encodeParam.replace("second", URLEncoder.encode(content, "utf-8"));
        encodeParam = encodeParam.replace("third", phoneNum);
        encodeParam = encodeParam.replace("forth", time);
        return Md5Utils.getMD5OfStr(Constants.MESSAGE_SALT_MSG + URLEncoder.encode(encodeParam, "utf-8").toLowerCase() + Constants.MESSAGE_SALT_MSG);
    }
}
