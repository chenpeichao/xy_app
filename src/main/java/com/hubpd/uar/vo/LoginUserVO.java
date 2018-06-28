package com.hubpd.uar.vo;

import javax.persistence.Column;
import java.util.Date;

/**
 * 用户登录返回VO
 *
 * @author cpc
 * @create 2018-06-28 8:39
 **/
public class LoginUserVO {
    private String phoneNum;            //用户登录账号--手机号码
    private String nickname;            //用户别名--手机号中间隐藏四位
    private String registerDate;          //注册时间

    public String getPhoneNum() {
        return phoneNum;
    }

    public void setPhoneNum(String phoneNum) {
        this.phoneNum = phoneNum;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getRegisterDate() {
        return registerDate;
    }

    public void setRegisterDate(String registerDate) {
        this.registerDate = registerDate;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        LoginUserVO that = (LoginUserVO) o;

        if (phoneNum != null ? !phoneNum.equals(that.phoneNum) : that.phoneNum != null) return false;
        if (nickname != null ? !nickname.equals(that.nickname) : that.nickname != null) return false;
        return registerDate != null ? registerDate.equals(that.registerDate) : that.registerDate == null;

    }

    @Override
    public int hashCode() {
        int result = phoneNum != null ? phoneNum.hashCode() : 0;
        result = 31 * result + (nickname != null ? nickname.hashCode() : 0);
        result = 31 * result + (registerDate != null ? registerDate.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "LoginUserVO{" +
                "phoneNum='" + phoneNum + '\'' +
                ", nickname='" + nickname + '\'' +
                ", registerDate='" + registerDate + '\'' +
                '}';
    }
}
