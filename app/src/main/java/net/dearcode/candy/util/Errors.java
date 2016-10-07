package net.dearcode.candy.util;

/**
 * Created by guowei on 16-9-30.
 */

import android.content.Context;
import android.content.res.Resources;

import net.dearcode.candy.R;

import go.client.Client;
import go.client.Error;

public class Errors {
    // ErrorFailure 未知错误
    public static final int ErrorFailure = 1;
    // ErrorUserNameFormat 用户名格式错误
    public static final int ErrorUserNameFormat = 1000;
    // ErrorUserNameLen 用户名长度错误
    public static final int ErrorUserNameLen = 1001;
    // ErrorUserPasswdFormat 用户密码格式错误
    public static final int ErrorUserPasswdFormat = 1010;
    // ErrorUserPasswdLen 用户密码长度错误
    public static final int ErrorUserPasswdLen = 1011;
    // ErrorUserNickFormat 昵称格式错误
    public static final int ErrorUserNickFormat = 1020;
    // ErrorUserNickLen 昵称长度错误
    public static final int ErrorUserNickLen = 1021;
    // ErrorMasterNewID 生成ID失败
    public static final int ErrorMasterNewID = 1031;
    // ErrorRegister 注册失败
    public static final int ErrorRegister = 1032;
    // ErrorOffline 已经离线
    public static final int ErrorOffline = 1033;
    // ErrorUpdateUserInfo 更新用户信息失败
    public static final int ErrorUpdateUserInfo = 1034;
    // ErrorUpdateUserPasswd 更新用户密码失败
    public static final int ErrorUpdateUserPasswd = 1035;
    // ErrorGetUserInfo 获取用户信息失败
    public static final int ErrorGetUserInfo = 1036;
    // ErrorAuth 用户认证失败（提示用户名或密码错误）
    public static final int ErrorAuth = 1037;
    // ErrorSubscribe 消息订阅失败
    public static final int ErrorSubscribe = 1038;
    // ErrorUnSubscribe 取消消息订阅失败
    public static final int ErrorUnSubscribe = 1039;
    // ErrorNewMessage 发送消息失败
    public static final int ErrorNewMessage = 1040;
    // ErrorFriendSelf 不能添加自己为好友
    public static final int ErrorFriendSelf = 1041;
    // ErrorAddFriend 添加好友失败
    public static final int ErrorAddFriend = 1042;
    // ErrorGetOnlineSession 用户不在线
    public static final int ErrorGetOnlineSession = 1043;
    // ErrorLoadFriendList 加载好友列表失败
    public static final int ErrorLoadFriendList = 1044;
    // ErrorFindUser 查找用户失败
    public static final int ErrorFindUser = 1045;
    // ErrorCreateGroup 创建分组失败
    public static final int ErrorCreateGroup = 1046;
    // ErrorLoadGroup 加载群组列表失败
    public static final int ErrorLoadGroup = 1047;
    // ErrorUploadFile 上传文件失败
    public static final int ErrorUploadFile = 1048;
    // ErrorCheckFile 文件检查失败
    public static final int ErrorCheckFile = 1049;
    // ErrorDownloadFile 下载文件失败
    public static final int ErrorDownloadFile = 1050;
    // ErrorLoadMessage 加载消息列表失败
    public static final int ErrorLoadMessage = 1051;


    public static String ParseError(Context context, String msg) {
        String retError = new String();
        Error err  = Client.errorParse(msg);
        Resources resources = context.getResources();
        switch ((int)err.getCode())
        {
             case ErrorFailure:
                retError = resources.getString(R.string.err_failure);
                break;
            case ErrorUserNameFormat:
                retError = resources.getString(R.string.err_username_format);
                break;
            case ErrorUserNameLen:
                retError = resources.getString(R.string.err_username_len);
                break;
            case ErrorUserPasswdFormat:
                retError = resources.getString(R.string.err_userpasswd_format);
                break;
            case ErrorUserPasswdLen:
                retError = resources.getString(R.string.err_userpasswd_len);
                break;
            case ErrorUserNickFormat:
                retError = resources.getString(R.string.err_usernick_format);
                break;
            case ErrorUserNickLen:
                retError = resources.getString(R.string.err_usernick_len);
                break;
            case ErrorMasterNewID:
                retError = resources.getString(R.string.err_masternewid);
                break;
            case ErrorRegister:
                retError = resources.getString(R.string.err_register);
                break;
            case ErrorOffline:
                retError = resources.getString(R.string.err_offline);
                break;
            case ErrorUpdateUserInfo:
                retError = resources.getString(R.string.err_updateuserinfo);
                break;
            case ErrorUpdateUserPasswd:
                retError = resources.getString(R.string.err_updateuserpasswd);
                break;
            case ErrorGetUserInfo:
                retError = resources.getString(R.string.err_getuserinfo);
                break;
            case ErrorAuth:
                retError = resources.getString(R.string.err_auth);
                break;
            case ErrorSubscribe:
                retError = resources.getString(R.string.err_subscribe);
                break;
            case ErrorUnSubscribe:
                retError = resources.getString(R.string.err_unsubscribe);
                break;
            case ErrorNewMessage:
                retError = resources.getString(R.string.err_newmessage);
                break;
            case ErrorFriendSelf:
                retError = resources.getString(R.string.err_friendself);
                break;
            case ErrorAddFriend:
                retError = resources.getString(R.string.err_addfriend);
                break;
            case ErrorGetOnlineSession:
                retError = resources.getString(R.string.err_getonlinesession);
                break;
            case ErrorLoadFriendList:
                retError = resources.getString(R.string.err_loadfriendlist);
                break;
            case ErrorFindUser:
                retError = resources.getString(R.string.err_finduser);
                break;
            case ErrorCreateGroup:
                retError = resources.getString(R.string.err_creategroup);
                break;
            case ErrorLoadGroup:
                retError = resources.getString(R.string.err_loadgroup);
                break;
            case ErrorUploadFile:
                retError = resources.getString(R.string.err_uploadfile);
                break;
            case ErrorCheckFile:
                retError = resources.getString(R.string.err_checkfile);
                break;
            case ErrorDownloadFile:
                retError = resources.getString(R.string.err_downloadfile);
                break;
            case ErrorLoadMessage:
                retError = resources.getString(R.string.err_loadmessage);
                break;
            default:
                retError = resources.getString(R.string.err_failure);
                break;
        }

        return retError;
    }
}
