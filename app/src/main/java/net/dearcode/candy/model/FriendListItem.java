package net.dearcode.candy.model;

/**
 * Created by lujinfeifly on 16/9/24.
 * 好友列表的单个数据
 */

public class FriendListItem {
    private long mUserID = 0;
    private String mName = "";
    private String mImg = "";

    public long getmUserID() {
        return mUserID;
    }

    public String getmName() {
        return mName;
    }

    public String getmImg() {
        return mImg;
    }

    public FriendListItem(String name, String img) {
        this.mName = name;
        this.mImg = img;
    }
}
