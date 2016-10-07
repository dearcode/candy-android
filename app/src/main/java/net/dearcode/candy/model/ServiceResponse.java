package net.dearcode.candy.model;

import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;
import android.util.Log;

import com.alibaba.fastjson.JSON;

/**
 *  * Created by c-wind on 2016/9/19 14:31
 *  * mail：root@codecn.org
 *  
 */
public class ServiceResponse implements Parcelable {
    private long id;
    private String data;
    private String error;

    private static final String TAG = "CandyMessage";

    public static final Parcelable.Creator<ServiceResponse> CREATOR = new
            Parcelable.Creator<ServiceResponse>() {
                public ServiceResponse createFromParcel(Parcel in) {
                    return new ServiceResponse(in);
                }

                public ServiceResponse[] newArray(int size) {
                    return new ServiceResponse[size];
                }
            };

    public ServiceResponse() {
    }

    protected ServiceResponse(Parcel in) {
        this.id = in.readLong();
        this.data = in.readString();
        this.error = in.readString();
    }

    public String getError() {
        Log.e(TAG, "getError:" + error);

        return error;
    }

    public void setError(String error) {
        this.error = error;
        Log.e(TAG, "setErr:" + error);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(this.id);
        dest.writeString(this.data);
        dest.writeString(this.error);
        Log.e(TAG, "write data");
    }

    public boolean hasError() {
        return !TextUtils.isEmpty(this.error);
    }

    public long getId() {
        return id;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public void setId(long id) {
        this.id = id;
    }


    public User getUser() {
        if (data == null || TextUtils.isEmpty(data)) {
            return null;
        }
        return JSON.parseObject(data, User.class);
    }

    public UserList getUserList() {
        if (data == null || TextUtils.isEmpty(data)) {
            return null;
        }
        return JSON.parseObject(data, UserList.class);
    }

    public FriendList getFriendList() {
        if (data == null || TextUtils.isEmpty(data)) {
            return null;
        }
        return JSON.parseObject(data, FriendList.class);
    }
}
