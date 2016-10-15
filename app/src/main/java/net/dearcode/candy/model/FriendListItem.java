package net.dearcode.candy.model;

import net.dearcode.candy.modelview.UserBean;

/**
 * Created by lujinfeifly on 16/9/24.
 * 好友列表的单个数据
 */

public class FriendListItem {
    private long mUserID = 0;
    private String mName = "";
    private String mPiny = "";
    private String mImg = "";

    private UserBean user;

    public long getmUserID() {
        return mUserID;
    }

    public String getmName() {
        return mName;
    }

    public String getmImg() {
        return mImg;
    }

    public FriendListItem(long userId, String name, String img) {
        this.mUserID = userId;
        this.mName = name;
        this.mImg = img;
    }

    public UserBean getUser() {
        return user;
    }

    public void setUser(UserBean user) {
        this.user = user;
    }
}
