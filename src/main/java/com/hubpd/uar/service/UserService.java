package com.hubpd.uar.service;

import java.util.Map;

/**
 * 用户中心
 *
 * @author cpc
 * @create 2018-06-27 9:01
 **/
public interface UserService {
    /**
     * 手机号获取验证码---并且将手机号的md5值作为key，验证码为value保存到redis中
     *
     * @param phoneNum  手机号
     * @param modelSign 模块标识：redis中验证码保存的前缀，用于区分不同模块之间保存的验证码
     * @return
     */
    public Map<String, Object> getMessageCode(String phoneNum, String modelSign);

    /**
     * 用户登录
     *
     * @param phoneNum 手机号
     * @param password 密码
     * @return
     */
    public Map<String, Object> login(String phoneNum, String password);

    /**
     * 用户注册
     *
     * @param phoneNum         用户注册手机号
     * @param password         用户注册密码
     * @param verificationCode 用户注册时手机号的验证码
     * @param modelSign        模块标识：redis中验证码保存的前缀，用于区分不同模块之间保存的验证码
     * @return
     */
    public Map<String, Object> register(String phoneNum, String password, String verificationCode, String modelSign);

    /**
     * 跳转用户修改密码
     *
     * @param phoneNum         用户修改密码的手机号
     * @param verificationCode 用户修改密码时手机号的验证码
     * @param modelSign        模块标识：redis中验证码保存的前缀，用于区分不同模块之间保存的验证码
     * @return
     */
    public Map<String, Object> forwardEditPassword(String phoneNum, String verificationCode, String modelSign);

    /**
     * 修改用户手机号对应的密码
     *
     * @param phoneNum 手机号
     * @param password 密码
     * @return
     */
    public Map<String, Object> updatePassword(String phoneNum, String password);
}
