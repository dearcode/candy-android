package net.dearcode.candy.model;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import net.dearcode.candy.R;

/**
 *  * Created by c-wind on 2016/9/21 18:39
 *  * mail：root@codecn.org
 *  
 */
public class Session {
    private byte[] avatar;
    private String title;
    private String date;
    private String msg;

    private long group;
    private long user;

    public Session(byte[] avatar, String title, String date, String msg) {
        this.avatar = avatar;
        this.title = title;
        this.date = date;
        this.msg = msg;
    }

    public Session(long group, long user, String date, String msg) {
        this.group = group;
        this.user = user;
        this.date = date;
        this.msg = msg;
    }

    public boolean isGroup() {
        return group != 0;
    }

    public void setAvatar(byte[] avatar) {
        this.avatar = avatar;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public long getGroup() {
        return group;
    }

    public void setGroup(long group) {
        this.group = group;
    }

    public long getUser() {
        return user;
    }

    public void setUser(long user) {
        this.user = user;
    }

    public Bitmap getAvatarBitmap(Resources def) {
        Bitmap bitmap;
        if (avatar != null) {
            bitmap = BitmapFactory.decodeByteArray(avatar, 0, avatar.length);
        } else {
            bitmap = BitmapFactory.decodeResource(def, R.mipmap.ic_launcher);
        }
        return bitmap;
    }

    public byte[] getAvatar() {
        return avatar;
    }

    public String getTitle() {
        return title;
    }

    public String getDate() {
        return date;
    }

    public String getMsg() {
        return msg;
    }
}
