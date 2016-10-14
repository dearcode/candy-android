package net.dearcode.candy.modelview;

/**
 * Created by lujinfei on 2016/10/14.
 * 用于界面流通的基本数据----消息
 */

public class MessageBean {
    private long id;
    private String chatid;   // 标识为聊天会话id，可分为个人聊天和群聊天
    private String content;  // 聊天内容
//    private int userid;      // 发言人
    private int type;        // 聊天类型
    private long time;       // 发言时间

    private boolean isread;  // 标识次消息未读，主要在于语音是否播放
    private UserBean user;   //发言人

    public long getId() {
        return id;
    }

    public String getChatid() {
        return chatid;
    }

    public String getContent() {
        return content;
    }

    public int getType() {
        return type;
    }

    public long getTime() {
        return time;
    }

    public boolean isread() {
        return isread;
    }

    public UserBean getUser() {
        return user;
    }

    public void setId(long id) {
        this.id = id;
    }

    public void setChatid(String chatid) {
        this.chatid = chatid;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public void setType(int type) {
        this.type = type;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public void setIsread(boolean isread) {
        this.isread = isread;
    }

    public void setUser(UserBean user) {
        this.user = user;
    }
}
