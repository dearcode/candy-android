package net.dearcode.candy.model;

/**
 * Created by lujinfeifly on 16/9/24.
 * 对话列表的单个数据
 */

public class ConversationListItem {

    private int mUnreadCount = 0;
    private int mUserId = 0;
    private String mUserName = "";
    private String mUserImg = "";
    private String mLastWord = "";
    private String mContent = "";
    private String mDate = "";

    public ConversationListItem() {

    }

    public ConversationListItem(String userName, String userImg, String content, String date) {
        this.mUserImg = userImg;
        this.mUserName = userName;
        this.mContent = content;
        this.mDate = date;
    }

    public String getmDate() {
        return mDate;
    }

    public int getmUnreadCount() {
        return mUnreadCount;
    }

    public int getmUserId() {
        return mUserId;
    }

    public String getmUserName() {
        return mUserName;
    }

    public String getmUserImg() {
        return mUserImg;
    }

    public String getmLastWord() {
        return mLastWord;
    }

    public String getmContent() {
        return mContent;
    }
}
