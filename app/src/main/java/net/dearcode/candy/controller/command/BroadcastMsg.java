package net.dearcode.candy.controller.command;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by lujinfei on 2016/10/22.
 */

public class BroadcastMsg implements Parcelable {

    public enum TYPE {
        LOGIN, SENDMSG, ADDFRIEND
    }

    private int type;
    private int  iret;
    private String msg;
    private long data;

    public static final Parcelable.Creator<BroadcastMsg> CREATOR = new Parcelable.Creator<BroadcastMsg>() {
        public BroadcastMsg createFromParcel(Parcel in) {
            return new BroadcastMsg(in);
        }

        public BroadcastMsg[] newArray(int size) {
            return new BroadcastMsg[size];
        }
    };

    public BroadcastMsg() {

    }


    public BroadcastMsg(Parcel in) {
        this.type = in.readInt();
        this.iret = in.readInt();
        this.msg = in.readString();
        this.data = in.readLong();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(type);
        dest.writeInt(iret);
        dest.writeString(msg);
        dest.writeLong(data);
    }

    public void setType(int type) {
        this.type = type;
    }

    public void setIret(int iret) {
        this.iret = iret;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public void setData(long data) {
        this.data = data;
    }

    public int getType() {
        return type;
    }

    public long getData() {
        return data;
    }

    public String getMsg() {
        return msg;
    }

    public int getIret() {
        return iret;
    }
}
