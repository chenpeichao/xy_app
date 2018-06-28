package com.hubpd.uar.controller;

import com.hubpd.uar.common.utils.Constants;
import com.hubpd.uar.service.UserService;
import com.hubpd.uar.service.impl.UserServiceImpl;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * 用户中心
 *
 * @author cpc
 * @create 2018-06-27 9:00
 **/
@Controller
@RequestMapping("/user")
public class UserController {
    private static Logger logger = Logger.getLogger(UserController.class);

    @Autowired
    private UserService userService;

    /**
     * 获取短信验证码
     *
     * @param request
     * @param response
     * @return
     */
    @PostMapping("/getMessageCode")
    @ResponseBody
    public Map<String, Object> getMessageCode(HttpServletResponse response, HttpServletRequest request) {
        Map<String, Object> result = new HashMap<String, Object>();
        //解决跨域问题
        response.setHeader("Access-Control-Allow-Origin", "*");
        try {
            //前台用于验证验证码校验后台的key，用于获取生成的验证码是否正确
            String phoneNum = request.getParameter("phoneNum");
            String modelSign = request.getParameter("modelSign") == null ? "" : request.getParameter("modelSign");
            if (StringUtils.isBlank(phoneNum)) {
                result.put("status", 0);
                result.put("msg", "手机号不能为空！！");
                return result;
            }
            if (StringUtils.isNotBlank(Constants.PHONE_NUM_REG) && !Pattern.matches(Constants.PHONE_NUM_REG, phoneNum)) {
                result.put("status", 0);
                result.put("msg", "手机号格式错误！");
                return result;
            }

            return userService.getMessageCode(phoneNum, modelSign);
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("获取短信验证码异常====", e);
            result.put("status", 0);
            result.put("msg", "获取验证码失败，请重试！！");
            return result;
        }
    }

    /**
     * 用户登录
     *
     * @param request
     * @param response
     * @return
     */
    @PostMapping("/login")
    @ResponseBody
    public Map<String, Object> login(HttpServletResponse response, HttpServletRequest request) {
        Map<String, Object> result = new HashMap<String, Object>();
        //解决跨域问题
        response.setHeader("Access-Control-Allow-Origin", "*");
        try {
            //前台用于验证验证码校验后台的key，用于获取生成的验证码是否正确
            String phoneNum = request.getParameter("phoneNum");
            String password = request.getParameter("password");
            if (StringUtils.isBlank(phoneNum)) {
                result.put("status", 0);
                result.put("msg", "手机号不能为空！！");
                return result;
            }
            if (StringUtils.isNotBlank(Constants.PHONE_NUM_REG) && !Pattern.matches(Constants.PHONE_NUM_REG, phoneNum)) {
                result.put("status", 0);
                result.put("msg", "手机号格式错误！");
                return result;
            }
            if (StringUtils.isBlank(password)) {
                result.put("status", 0);
                result.put("msg", "密码必须填写！");
                return result;
            }

            return userService.login(phoneNum, password);
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("用户登录异常====", e);
            result.put("status", 0);
            result.put("msg", "登录异常，请稍后再试！！");
            return result;
        }
    }

    /**
     * 用户注册
     *
     * @param request
     * @param response
     * @return
     */
    @PostMapping("/register")
    @ResponseBody
    public Map<String, Object> register(HttpServletResponse response, HttpServletRequest request) {
        Map<String, Object> result = new HashMap<String, Object>();
        //解决跨域问题
        response.setHeader("Access-Control-Allow-Origin", "*");
        try {
            //前台用于验证验证码校验后台的key，用于获取生成的验证码是否正确
            String phoneNum = request.getParameter("phoneNum");
            String password = request.getParameter("password");
            String verificationCode = request.getParameter("verificationCode");
            String modelSign = request.getParameter("modelSign") == null ? "" : request.getParameter("modelSign");
            if (StringUtils.isBlank(phoneNum)) {
                result.put("status", 0);
                result.put("msg", "手机号不能为空！！");
                return result;
            }
            if (StringUtils.isNotBlank(Constants.PHONE_NUM_REG) && !Pattern.matches(Constants.PHONE_NUM_REG, phoneNum)) {
                result.put("status", 0);
                result.put("msg", "手机号格式错误！");
                return result;
            }
            if (StringUtils.isBlank(password)) {
                result.put("status", 0);
                result.put("msg", "密码必须填写！");
                return result;
            }
            if (StringUtils.isBlank(verificationCode)) {
                result.put("status", 0);
                result.put("msg", "请填写验证码！");
                return result;
            }
            if (StringUtils.isNotBlank(Constants.USER_REGISTER_PASSWORD_REGIX)) {
                if (!Pattern.matches(Constants.USER_REGISTER_PASSWORD_REGIX, password.trim())) {
                    result.put("status", 0);
                    result.put("msg", "密码为6-12位的数字字母或下划线！");
                    return result;
                }
            }

            return userService.register(phoneNum, password, verificationCode, modelSign);
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("用户注册异常====", e);
            result.put("status", 0);
            result.put("msg", "用户注册异常，请稍后再试！！");
            return result;
        }
    }

    /**
     * 跳转用户修改密码
     *
     * @param request
     * @param response
     * @return
     */
    @PostMapping("/forwardEditPassword")
    @ResponseBody
    public Map<String, Object> forwardEditPassword(HttpServletResponse response, HttpServletRequest request) {
        Map<String, Object> result = new HashMap<String, Object>();
        //解决跨域问题
        response.setHeader("Access-Control-Allow-Origin", "*");
        try {
            //前台用于验证验证码校验后台的key，用于获取生成的验证码是否正确
            String phoneNum = request.getParameter("phoneNum");
            String verificationCode = request.getParameter("verificationCode");
            String modelSign = request.getParameter("modelSign") == null ? "" : request.getParameter("modelSign");
            if (StringUtils.isBlank(phoneNum)) {
                result.put("status", 0);
                result.put("msg", "手机号不能为空！！");
                return result;
            }
            if (StringUtils.isNotBlank(Constants.PHONE_NUM_REG) && !Pattern.matches(Constants.PHONE_NUM_REG, phoneNum)) {
                result.put("status", 0);
                result.put("msg", "手机号格式错误！");
                return result;
            }
            if (StringUtils.isBlank(verificationCode)) {
                result.put("status", 0);
                result.put("msg", "请填写验证码！");
                return result;
            }

            return userService.forwardEditPassword(phoneNum, verificationCode, modelSign);
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("跳转到重置密码页面异常====", e);
            result.put("status", 0);
            result.put("msg", "修改密码异常，请稍后再试！！");
            return result;
        }
    }

    /**
     * 用户修改密码
     *
     * @param request
     * @param response
     * @return
     */
    @PostMapping("/updatePassword")
    @ResponseBody
    public Map<String, Object> updatePassword(HttpServletResponse response, HttpServletRequest request) {
        Map<String, Object> result = new HashMap<String, Object>();
        //解决跨域问题
        response.setHeader("Access-Control-Allow-Origin", "*");
        try {
            //前台用于验证验证码校验后台的key，用于获取生成的验证码是否正确
            String phoneNum = request.getParameter("phoneNum");
            String password = request.getParameter("password");
            String repassword = request.getParameter("repassword");
            if (StringUtils.isBlank(phoneNum)) {
                result.put("status", 0);
                result.put("msg", "未填写指定项！！");
                return result;
            }

            if (StringUtils.isBlank(password)) {
                result.put("status", 0);
                result.put("msg", "新密码为必填项！！");
                return result;
            }
            if (StringUtils.isBlank(repassword)) {
                result.put("status", 0);
                result.put("msg", "再次确认新密码为必填项！！");
                return result;
            }

            if (StringUtils.isNotBlank(Constants.USER_REGISTER_PASSWORD_REGIX)) {
                if (!Pattern.matches(Constants.USER_REGISTER_PASSWORD_REGIX, password.trim())) {
                    result.put("status", 0);
                    result.put("msg", "密码为6-12位的数字字母或下划线！");
                    return result;
                }
            }

            if (!password.trim().equals(repassword.trim())) {
                result.put("status", 0);
                result.put("msg", "两次输入密码不一致！");
                return result;
            }

            return userService.updatePassword(phoneNum, password);
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("修改密码异常====", e);
            result.put("status", 0);
            result.put("msg", "重置密码异常，请稍后再试！！");
            return result;
        }
    }
}
