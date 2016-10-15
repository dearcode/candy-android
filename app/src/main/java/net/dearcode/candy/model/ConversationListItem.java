package net.dearcode.candy.model;

/**
 * Created by lujinfeifly on 16/9/24.
 * 对话列表的单个数据
 */

public class ConversationListItem {

    private int mUnreadCount = 0;
    private long mUserId = 0;
    private String mUserName = "";
    private String mUserImg = "";
    private String mLastWord = "";
    private String mContent = "";
    private long mDate;

    public ConversationListItem() {

    }

    public ConversationListItem(String userName, String userImg, String content, long date) {
        this.mUserImg = userImg;
        this.mUserName = userName;
        this.mContent = content;
        this.mDate = date;
    }

    public long getmDate() {
        return mDate;
    }

    public int getmUnreadCount() {
        return mUnreadCount;
    }

    public long getmUserId() {
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


    public void setmUnreadCount(int mUnreadCount) {
        this.mUnreadCount = mUnreadCount;
    }

    public void setmUserId(long mUserId) {
        this.mUserId = mUserId;
    }

    public void setmUserName(String mUserName) {
        this.mUserName = mUserName;
    }

    public void setmUserImg(String mUserImg) {
        this.mUserImg = mUserImg;
    }

    public void setmLastWord(String mLastWord) {
        this.mLastWord = mLastWord;
    }

    public void setmContent(String mContent) {
        this.mContent = mContent;
    }

    public void setmDate(long mDate) {
        this.mDate = mDate;
    }
}
