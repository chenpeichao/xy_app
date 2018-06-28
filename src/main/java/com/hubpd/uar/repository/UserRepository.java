package com.hubpd.uar.repository;

import com.hubpd.uar.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

/**
 * 用户
 *
 * @author cpc
 * @create 2018-06-28 8:31
 **/
public interface UserRepository extends JpaRepository<User, Integer> {
    /**
     * 根据登录手机号和密码的md5值，查询用户信息
     *
     * @param phoneNum    手机号
     * @param passwordMd5 密码md5加密值
     * @return
     */
    @Query(" FROM com.hubpd.uar.domain.User user WHERE user.phoneNum = :phoneNum and user.passwordMd5 = :passwordMd5 ")
    public User findUserByPhoneNumAndPasswordMd5(@Param("phoneNum") String phoneNum, @Param("passwordMd5") String passwordMd5);

    /**
     * 根据用户手机号，查询用户信息
     *
     * @param phoneNum 用户手机号
     * @return
     */
    @Query(" FROM com.hubpd.uar.domain.User user WHERE user.phoneNum = :phoneNum ")
    public User findUserByPhoneNum(@Param("phoneNum") String phoneNum);
}
