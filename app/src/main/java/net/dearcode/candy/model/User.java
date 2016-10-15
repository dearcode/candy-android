package net.dearcode.candy.model;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.text.TextUtils;

import net.dearcode.candy.R;

/**
 *  * Created by c-wind on 2016/9/21 18:34
 *  * mail：root@codecn.org
 *  
 */
public class User {
    private long ID;
    private byte[] Avatar;
    private String Name;
    private String NickName;
    private String Password;

    public User() {

    }
    public User(String name, String password) {
        this.Name = name;
        this.Password = password;
    }

    public User(long ID, byte[] avatar, String name, String nickname) {
        this.ID = ID;
        this.Avatar = avatar;
        this.Name = name;
        this.NickName = nickname;
    }


    public long getID() {
        return ID;
    }

    public byte[] getAvatar() {
        return Avatar;
    }

    public Bitmap getAvatarBitmap(Resources def) {
        Bitmap bitmap;
        if (Avatar != null) {
            bitmap = BitmapFactory.decodeByteArray(Avatar, 0, Avatar.length);
        } else {
            bitmap = BitmapFactory.decodeResource(def, R.mipmap.ic_launcher);
        }
        return bitmap;
    }

    public String getNickName() {
        if (NickName == null || TextUtils.isEmpty(NickName))
            return getName();
        return NickName;
    }

    public String getName() {
        if (Name == null|| TextUtils.isEmpty(Name))
            return "Undefine";
        return Name;
    }

    public void setID(long ID) {
        this.ID = ID;
    }

    public void setAvatar(byte[] avatar) {
        Avatar = avatar;
    }

    public void setName(String name) {
        Name = name;
    }

    public void setNickName(String nickName) {
        NickName = nickName;
    }

    public void setToBundle(Bundle bundle) {
        bundle.putLong("uid", ID);
        bundle.putByteArray("avatar", Avatar);
        bundle.putString("name", Name);
        bundle.putString("nickname", NickName);
    }

    public String getPassword() {
        return Password;
    }

    public void setPassword(String password) {
        Password = password;
    }

    public FriendListItem getFriendListItem() {
        return new FriendListItem(ID, getNickName(), "");
    }
}
