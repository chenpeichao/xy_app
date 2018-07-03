package com.hubpd.uar.service.impl;

import com.hubpd.uar.common.utils.Constants;
import com.hubpd.uar.common.utils.DateUtils;
import com.hubpd.uar.common.utils.Md5Utils;
import com.hubpd.uar.common.utils.PhoneMessageUtils;
import com.hubpd.uar.domain.User;
import com.hubpd.uar.repository.UserRepository;
import com.hubpd.uar.service.UserService;
import com.hubpd.uar.vo.LoginUserVO;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import redis.clients.jedis.JedisCluster;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * 用户中心
 *
 * @author cpc
 * @create 2018-06-27 9:02
 **/
@Service
public class UserServiceImpl implements UserService {
    private static Logger logger = Logger.getLogger(UserServiceImpl.class);

    @Autowired
    private JedisCluster jedisCluster;
    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    @Autowired
    private UserRepository userRepository;

    /**
     * 手机号获取验证码---并且将手机号的md5值作为key，验证码为value保存到redis中
     *
     * @param phoneNum  手机号
     * @param modelSign 模块标识：redis中验证码保存的前缀，用于区分不同模块之间保存的验证码
     * @return
     */
    public Map<String, Object> getMessageCode(String phoneNum, String modelSign) {
        Map<String, Object> resultMap = new HashMap<String, Object>();
        String phoneNumMD5 = Md5Utils.getMD5OfStr(phoneNum);
        //手机号的验证码在redis中存储的key，指定前缀+手机号md5值
        String phoneNumRedisKey = modelSign + phoneNumMD5;

        //首先判断手机号对于验证码是否已经获取
        Boolean phoneNumHasKeyRedis = redisTemplate.hasKey(phoneNumRedisKey);
        //如果在指定时间存在，则不让其重复发送短信获取验证码
        if (phoneNumHasKeyRedis) {
            //redis中的key的当前过时时间必须小于预定时间超过60秒，才可继续发送验证短信
            //redis中当前手机验证码时效
            Long redisPhoneMsgKeyExpireMinutes = redisTemplate.getExpire(phoneNumRedisKey, TimeUnit.SECONDS);
            //redis中默认存储的手机验证码时效--单位：分钟
            Long phoneMsgKeyExpireMinutesDefault = Constants.MESSAGE_CODE_SAVE_CYCLE;
            //当第二次发送时间与上一次发送时间间隔不超过Constants.MESSAGE_CODE_RESEND_CYCLE时，不能重发
            if (redisPhoneMsgKeyExpireMinutes != null && phoneMsgKeyExpireMinutesDefault - redisPhoneMsgKeyExpireMinutes < Constants.MESSAGE_CODE_RESEND_CYCLE) {
                resultMap.put("status", "0");
                resultMap.put("msg", "新验证码请在" + (Constants.MESSAGE_CODE_RESEND_CYCLE + redisPhoneMsgKeyExpireMinutes - phoneMsgKeyExpireMinutesDefault) + "s后获取！");
                return resultMap;
            }
            //存在，则首先应该删除
            redisTemplate.delete(phoneNumRedisKey);
        }
        //调用短信发送接口
        Map<String, Object> tempPhoneResultMap = PhoneMessageUtils.getPhoneMessage(phoneNum);
        //短信接口返回状态标识，用于判断短信接口调用是否正确
        Integer messageResultStatus = (Integer) tempPhoneResultMap.get("status");
        if (1 == messageResultStatus) {
            //调用正确
            redisTemplate.opsForValue().set(phoneNumRedisKey, tempPhoneResultMap.get("msg").toString());
            logger.info("redis中key为【" + phoneNumRedisKey + "】保存的【" + phoneNum + "】的验证码为：" + redisTemplate.opsForValue().get(phoneNumRedisKey));
            //设置短信验证码默认保存时间--10分钟
            redisTemplate.expire(phoneNumRedisKey, Constants.MESSAGE_CODE_SAVE_CYCLE, TimeUnit.SECONDS);

            resultMap.put("status", 1);
            resultMap.put("msg", "验证码获取成功！");
//            Map<String, Object> messageCodeMap = new HashMap<String, Object>();
//            messageCodeMap.put("phoneMsgCode", tempPhoneResultMap.get("msg").toString());
//            resultMap.put("data", messageCodeMap);
        } else {
            logger.error("短信验证码获取失败！：" + tempPhoneResultMap.get("msg").toString());
            resultMap.put("status", 0);
            resultMap.put("msg", "手机验证码获取失败，请稍后再试！");
        }
        return resultMap;
    }

    /**
     * 用户登录
     *
     * @param phoneNum 手机号
     * @param password 密码
     * @return
     */
    public Map<String, Object> login(String phoneNum, String password) {
        Map<String, Object> resultMap = new HashMap<String, Object>();

        User userFromDB = userRepository.findUserByPhoneNumAndPasswordMd5(phoneNum, Md5Utils.getMD5OfStr(password));

        if (userFromDB == null) {
            resultMap.put("status", 0);
            resultMap.put("msg", "请检查登录手机号和密码！");
            return resultMap;
        }

        //封装用户登录信息
        LoginUserVO loginUserVO = new LoginUserVO();
        loginUserVO.setPhoneNum(userFromDB.getPhoneNum());
        if (userFromDB.getRegisterDate() != null) {
            loginUserVO.setRegisterDate(DateUtils.parseDate2StringByPattern(userFromDB.getRegisterDate(), "yyyy-MM-dd HH:mm:ss"));
        }
        loginUserVO.setNickname(userFromDB.getNickname());
        resultMap.put("status", 1);
        resultMap.put("msg", "用户登录成功！");
        resultMap.put("data", loginUserVO);
        return resultMap;
    }

    /**
     * 用户注册
     *
     * @param phoneNum         用户注册手机号
     * @param password         用户注册密码
     * @param verificationCode 用户注册时手机号的验证码
     * @param modelSign        模块标识：redis中验证码保存的前缀，用于区分不同模块之间保存的验证码
     * @return
     */
    public Map<String, Object> register(String phoneNum, String password, String verificationCode, String modelSign) {
        Map<String, Object> resultMap = new HashMap<String, Object>();

        //1、首先查看该用户是否已经注册过
        User userFromDB = userRepository.findUserByPhoneNum(phoneNum);
        if (userFromDB != null) {
            resultMap.put("status", 0);
            resultMap.put("msg", "该手机号已经注册，请跟换手机号再试！");
            return resultMap;
        }

        //2、用户第一次注册
        //2.1、从redis中查询手机号的验证码
        //手机号的验证码在redis中存储的key，指定前缀+手机号md5值
        String phoneNumRedisKey = modelSign + Md5Utils.getMD5OfStr(phoneNum);
        //首先判断手机号对于验证码是否已经获取
        if (redisTemplate.hasKey(phoneNumRedisKey) && verificationCode.equals(redisTemplate.opsForValue().get(phoneNumRedisKey))) {
            //如果验证码在指定时间存在，并且与用户输入的一致，则注册用户
            User user = new User();
            user.setPhoneNum(phoneNum);
            user.setPassword(password);
            user.setPasswordMd5(Md5Utils.getMD5OfStr(password));
            user.setNickname(phoneNum.substring(0, 3) + "****" + phoneNum.substring(7, phoneNum.length()));
            user.setRegisterDate(new Date());
            userRepository.saveAndFlush(user);

            //删除redis中的验证码信息
            redisTemplate.delete(phoneNumRedisKey);

            resultMap.put("status", 1);
            resultMap.put("msg", "用户注册成功！");
        } else {
            //redis中未找到对应的验证码
            resultMap.put("status", 0);
            resultMap.put("msg", "请确认验证码是否正确！");
        }

        return resultMap;
    }

    /**
     * 跳转用户修改密码
     *
     * @param phoneNum         用户修改密码的手机号
     * @param verificationCode 用户修改密码时手机号的验证码
     * @param modelSign        模块标识：redis中验证码保存的前缀，用于区分不同模块之间保存的验证码
     * @return
     */
    public Map<String, Object> forwardEditPassword(String phoneNum, String verificationCode, String modelSign) {
        Map<String, Object> resultMap = new HashMap<String, Object>();

        //1、首先查看该用户是否已经注册过
        User userFromDB = userRepository.findUserByPhoneNum(phoneNum);
        if (userFromDB == null) {
            resultMap.put("status", 0);
            resultMap.put("msg", "该手机号未注册过，请注册后再试！");
            return resultMap;
        }
        //2.1、从redis中查询手机号的验证码
        //手机号的验证码在redis中存储的key，指定前缀+手机号md5值
        String phoneNumRedisKey = modelSign + Md5Utils.getMD5OfStr(phoneNum);
        //首先判断手机号对于验证码是否已经获取
        if (redisTemplate.hasKey(phoneNumRedisKey) && verificationCode.equals(redisTemplate.opsForValue().get(phoneNumRedisKey))) {
            resultMap.put("status", 1);
            resultMap.put("msg", "验证通过！");

            redisTemplate.delete(phoneNumRedisKey);
            return resultMap;
        } else if (!redisTemplate.hasKey(phoneNumRedisKey)) {
            resultMap.put("status", 0);
            resultMap.put("msg", "验证码失效，请重新获取！");
            return resultMap;
        } else {
            resultMap.put("status", 0);
            resultMap.put("msg", "验证码错误！");
            return resultMap;
        }
    }

    /**
     * 修改用户手机号对应的密码
     *
     * @param phoneNum 手机号
     * @param password 密码
     * @return
     */
    public Map<String, Object> updatePassword(String phoneNum, String password) {
        Map<String, Object> resultMap = new HashMap<String, Object>();

        //1、首先查看该用户是否已经注册过
        User userFromDB = userRepository.findUserByPhoneNum(phoneNum);
        if (userFromDB == null) {
            resultMap.put("status", 0);
            resultMap.put("msg", "密码修改错误，不存在该手机号！");
            return resultMap;
        }
        userFromDB.setPassword(password);
        userFromDB.setPasswordMd5(Md5Utils.getMD5OfStr(password));
        try {
            userRepository.saveAndFlush(userFromDB);
        } catch (Exception e) {
            logger.error("用户密码修改失败！", e);
            e.printStackTrace();
            resultMap.put("status", 0);
            resultMap.put("msg", "密码修改失败，请重试！");
            return resultMap;
        }
        resultMap.put("status", 1);
        resultMap.put("msg", "密码修改成功！");
        return resultMap;
    }
}
