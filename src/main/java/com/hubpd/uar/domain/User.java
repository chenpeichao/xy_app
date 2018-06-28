package com.hubpd.uar.domain;

import javax.persistence.*;
import java.util.Date;

/**
 * 用户
 *
 * @author cpc
 * @create 2018-06-28 8:25
 **/
@Entity(name = "t_user")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;                 //自增主键
    @Column(name = "phone_num")
    private String phoneNum;            //用户登录账号--手机号码
    @Column(name = "nickname")
    private String nickname;        //用户别名--手机号中间隐藏四位
    @Column
    private String password;            //用户密码
    @Column(name = "password_md5")
    private String passwordMd5;         //用户密码md5加密值
    @Column(name = "register_date")
    private Date registerDate;          //注册时间

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

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

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPasswordMd5() {
        return passwordMd5;
    }

    public void setPasswordMd5(String passwordMd5) {
        this.passwordMd5 = passwordMd5;
    }

    public Date getRegisterDate() {
        return registerDate;
    }

    public void setRegisterDate(Date registerDate) {
        this.registerDate = registerDate;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        User user = (User) o;

        if (id != null ? !id.equals(user.id) : user.id != null) return false;
        if (phoneNum != null ? !phoneNum.equals(user.phoneNum) : user.phoneNum != null) return false;
        if (nickname != null ? !nickname.equals(user.nickname) : user.nickname != null) return false;
        if (password != null ? !password.equals(user.password) : user.password != null) return false;
        if (passwordMd5 != null ? !passwordMd5.equals(user.passwordMd5) : user.passwordMd5 != null) return false;
        return registerDate != null ? registerDate.equals(user.registerDate) : user.registerDate == null;

    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (phoneNum != null ? phoneNum.hashCode() : 0);
        result = 31 * result + (nickname != null ? nickname.hashCode() : 0);
        result = 31 * result + (password != null ? password.hashCode() : 0);
        result = 31 * result + (passwordMd5 != null ? passwordMd5.hashCode() : 0);
        result = 31 * result + (registerDate != null ? registerDate.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", phoneNum='" + phoneNum + '\'' +
                ", nickname='" + nickname + '\'' +
                ", password='" + password + '\'' +
                ", passwordMd5='" + passwordMd5 + '\'' +
                ", registerDate=" + registerDate +
                '}';
    }
}
