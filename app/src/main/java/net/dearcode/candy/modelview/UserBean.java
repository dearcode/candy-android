package net.dearcode.candy.modelview;

import android.util.Log;

import net.dearcode.candy.controller.CustomeApplication;

/**
 * Created by lujinfei on 2016/10/14.
 * 用于界面流通的基本数据----用户（基本信息）
 */

public class UserBean {
    private long userId;
    private String userName;
    private String nickName;
    private String headUrl;

    public long getUserId() {
        return userId;
    }

    public String getUserName() {
        return userName;
    }

    public String getNickName() {
        return nickName;
    }

    public String getHeadUrl() {
        return headUrl;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public void setHeadUrl(String headUrl) {
        this.headUrl = headUrl;
    }
}
