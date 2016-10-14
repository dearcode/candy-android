package net.dearcode.candy.model;

import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.provider.ContactsContract;

import net.dearcode.candy.modelview.MessageBean;
import net.dearcode.candy.modelview.UserBean;

/**
 *  * Created by c-wind on 2016/9/30 10:59
 *  * mail：root@codecn.org
 *  
 */
public class Message implements Parcelable {
    public static final Parcelable.Creator<Message> CREATOR = new Parcelable.Creator<Message>() {
        public Message createFromParcel(Parcel in) {
            return new Message(in);
        }

        public Message[] newArray(int size) {
            return new Message[size];
        }
    };
    private Event event;
    private Relation relation;
    private long id;
    private long group;
    private long from;
    private long to;
    private String msg;

    public Message(Event event, Relation relation, long id, long group, long from, long to, String msg) {
        this.event = event;
        this.relation = relation;
        this.id = id;
        this.group = group;
        this.from = from;
        this.to = to;
        this.msg = msg;
    }

    public Message() {
    }

    protected Message(Parcel in) {
        this.event = Event.values()[in.readInt()];
        this.relation = Relation.values()[in.readInt()];
        this.id = in.readLong();
        this.group = in.readLong();
        this.from = in.readLong();
        this.to = in.readLong();
        this.msg = in.readString();
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getGroup() {
        return group;
    }

    public void setGroup(long group) {
        this.group = group;
    }

    public long getFrom() {
        return from;
    }

    public void setFrom(long from) {
        this.from = from;
    }

    public long getTo() {
        return to;
    }

    public void setTo(long to) {
        this.to = to;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        // Event event; Relation relation; long id; long group; long from; long to; String msg;
        dest.writeInt(event.ordinal());
        dest.writeInt(relation.ordinal());
        dest.writeLong(id);
        dest.writeLong(group);
        dest.writeLong(from);
        dest.writeLong(to);
        dest.writeString(msg);
    }

    public boolean isGroupMessage() {
        return group != 0;
    }

    public void setToBundle(Bundle b) {
        b.putInt("event", event.ordinal());
        b.putInt("relation", relation.ordinal());
        b.putLong("mid", id);
        b.putLong("group", group);
        b.putLong("from", from);
        b.putLong("to", to);
        b.putString("msg", msg);
    }

    public String getDate() {
        return new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new java.util.Date((id >> 32) * 1000));
    }

    public Event getEvent() {
        return event;
    }

    public void setEvent(Event event) {
        this.event = event;
    }
    public void setEvent(int e) {
        this.event = Event.values()[e];
    }

    public Relation getRelation() {
        return relation;
    }

    public void setRelation(Relation relation) {
        this.relation = relation;
    }

    public void setRelation(int r) {
        this.relation = Relation.values()[r];
    }

    /**
     * 转换成界面需要使用的类
     * @return
     */
    public MessageBean getMessageBean() {
        MessageBean bean = new MessageBean();
        bean.setTime(id);
        bean.setChatid("");
        bean.setContent(msg);
        bean.setId(id);
        bean.setIsread(false);
        bean.setType(1);
        UserBean user = new UserBean();
        user.setUserId(from);
        bean.setUser(user);
        return bean;
    }
}
