package net.dearcode.candy.model;

/**
 * Created by Tian on 2016/10/2.
 */

// Event 事件（消息类型）
public enum Event {
    None("普通聊天的消息下推", 0), Group("群消息", 1), Friend("好友关系", 2), Online("上线", 3), Offline("下线", 4), Notice("通知", 5);

    private String name;
    private int index;

    private Event(String name, int index) {
        this.name = name;
        this.index = index;
    }

    @Override
    public String toString() {
        return this.index + "_" + this.name;
    }
}
