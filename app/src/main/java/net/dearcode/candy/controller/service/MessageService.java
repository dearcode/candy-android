package net.dearcode.candy.controller.service;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import net.dearcode.candy.CandyMessage;
import net.dearcode.candy.R;
import net.dearcode.candy.controller.command.BroadcastMsg;
import net.dearcode.candy.model.Event;
import net.dearcode.candy.model.Message;
import net.dearcode.candy.model.Relation;
import net.dearcode.candy.model.ServiceResponse;
import net.dearcode.candy.util.Common;
import net.dearcode.candy.util.ResourceUtil;

import go.client.CandyClient;
import go.client.MessageHandler;

import static go.client.Client.newCandyClient;


/**
 * Created by Administrator on 2016/9/12.
 */
public class MessageService extends Service {
    private MessageClient msgClient;


    private class MessageClient implements MessageHandler {

        @Override
        public void onHealth() {

        }

        @Override
        public void onError(String s) {
            Log.e(Common.LOG_TAG, "onError");

        }

        @Override
        public void onUnHealth(String s) {
            Log.e(Common.LOG_TAG, "onUnHealth");

        }

        @Override
        public void onRecv(int event, int relation, long id, long group, long from, long to, String msg) {
            Log.e(Common.LOG_TAG, "onRecv" +","+from+","+ msg);
            Intent i = new Intent("net.dearcode.candy.message");
            Message m = new Message(Event.values()[event], Relation.values()[relation] , id, group, from, to, msg);
            Bundle b = new Bundle();
            b.putParcelable("message", m);
            i.putExtras(b);
            sendBroadcast(i);

            NotificationManager mNotificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(MessageService.this);
            mBuilder.setContentTitle(from + "")//设置通知栏标题
                    .setContentText(msg)
                    //.setContentIntent(getDefalutIntent(Notification.FLAG_AUTO_CANCEL)) //设置通知栏点击意图
                 //  .setNumber(number) //设置通知集合的数量
                    .setTicker("测试通知来啦") //通知首次出现在通知栏，带上升动画效果的
                    .setWhen(System.currentTimeMillis())//通知产生的时间，会在通知信息里显示，一般是系统获取到的时间
                    .setPriority(Notification.PRIORITY_DEFAULT) //设置该通知优先级
                //  .setAutoCancel(true)//设置这个标志当用户单击面板就可以让通知将自动取消
                    .setOngoing(false)//ture，设置他为一个正在进行的通知。他们通常是用来表示一个后台任务,用户积极参与(如播放音乐)或以某种方式正在等待,因此占用设备(如一个文件下载,同步操作,主动网络连接)
                    .setDefaults(Notification.DEFAULT_VIBRATE)//向通知添加声音、闪灯和振动效果的最简单、最一致的方式是使用当前的用户默认设置，使用defaults属性，可以组合
                    //Notification.DEFAULT_ALL  Notification.DEFAULT_SOUND 添加声音 // requires VIBRATE permission
                    .setSmallIcon(ResourceUtil.getDrawableId("pic" + from % 8));//设置通知小ICON
            mNotificationManager.notify((int)(from % Integer.MAX_VALUE), mBuilder.build());
        }
    }

    private CandyMessage.Stub serviceBinder = new CandyMessage.Stub() {
        @Override
        public ServiceResponse ConfirmFriend(long ID) throws RemoteException {
            Log.e(Common.LOG_TAG, "begin confirm friend:" + ID );
            ServiceResponse sr = new ServiceResponse();
            try {
                client.friend(ID, Relation.CONFIRM.ordinal(), "");
                Log.e(Common.LOG_TAG, "confirm friend success");
            } catch (Exception e) {
                Log.e(Common.LOG_TAG, "confirm friend error:" + e.getMessage());
                sr.setError(e.getMessage());
            }
            return sr;
        }

        @Override
        public ServiceResponse RefuseFriend(long ID, String msg) throws RemoteException {
            Log.e(Common.LOG_TAG, "begin refuse friend:" + ID );
            ServiceResponse sr = new ServiceResponse();
            try {
                client.friend(ID, Relation.CONFIRM.ordinal(), "");
                Log.e(Common.LOG_TAG, "refuse friend success");
            } catch (Exception e) {
                Log.e(Common.LOG_TAG, "refuse friend error:" + e.getMessage());
                sr.setError(e.getMessage());
            }
            return sr;
        }

        @Override
        public ServiceResponse sendMessage(long group, long to, String msg) throws RemoteException {
            Log.e(Common.LOG_TAG, "will send message group:" + group + " to:" + to + " msg:" + msg);
            ServiceResponse sr = new ServiceResponse();
            try {
                long id = client.sendMessage(group, to, msg);
                sr.setId(id);
                Log.e(Common.LOG_TAG, "connect ok");
            } catch (Exception e) {
                Log.e(Common.LOG_TAG, "connect error:" + e.getMessage());
                sr.setError(e.getMessage());
            }
            return sr;
        }

        @Override
        public ServiceResponse connect() throws RemoteException {
            Log.e(Common.LOG_TAG, "will connect server");
            ServiceResponse sr = new ServiceResponse();
            try {
                client.start();
                Log.e(Common.LOG_TAG, "connect ok");
            } catch (Exception e) {
                Log.e(Common.LOG_TAG, "connect error:" + e.getMessage());
                sr.setError(e.getMessage());
            }
            return sr;
        }

        @Override
        public ServiceResponse addFriend(long ID, String msg) throws RemoteException {
            Log.e(Common.LOG_TAG, "will add friend user:" + ID);
            ServiceResponse sr = new ServiceResponse();
            try {
                client.friend(ID, Relation.ADD.ordinal(), msg);
                Log.e(Common.LOG_TAG, "add friend success");
            } catch (Exception e) {
                Log.e(Common.LOG_TAG, "add friend error:" + e.getMessage());
                sr.setError(e.getMessage());
            }
            return sr;
        }

        @Override
        public ServiceResponse loadUserInfo(long id) throws RemoteException {
            Log.e(Common.LOG_TAG, "will loadInfo user:" + id);
            ServiceResponse sr = new ServiceResponse();
            try {
                String data = client.getUserInfoByID(id);
                Log.e(Common.LOG_TAG, "getUserInfo ok , info:" + data);
                sr.setData(data);
            } catch (Exception e) {
                Log.e(Common.LOG_TAG, "register error:" + e.getMessage());
                sr.setError(e.getMessage());
            }

            return sr;
        }

        public ServiceResponse register(String user, String pass) throws RemoteException {
            Log.e(Common.LOG_TAG, "will register user:" + user + " pass:" + pass);
            ServiceResponse sr = new ServiceResponse();
            try {
                long id = client.register(user, pass);
                Log.e(Common.LOG_TAG, "register ok , id:" + id);
                sr.setId(id);
            } catch (Exception e) {
                Log.e(Common.LOG_TAG, "register error:" + e.getMessage());
                sr.setError(e.getMessage());
            }
            return sr;
        }

        public ServiceResponse loadFriendList() throws RemoteException {
            ServiceResponse sr = new ServiceResponse();
            try {
                sr.setData(client.loadFriendList());
                Log.e(Common.LOG_TAG, "loadFriendList ok recv:" + sr.getData());
            } catch (Exception e) {
                Log.e(Common.LOG_TAG, "loadFriendList error:" + e.getMessage());
                sr.setError(e.getMessage());
            }
            return sr;
        }

        @Override
        public ServiceResponse login(String user, String pass) throws RemoteException {
            ServiceResponse sr = new ServiceResponse();
            try {
                long id = client.login(user, pass);
                Log.e(Common.LOG_TAG, "login ok , id:" + id);
                sr.setId(id);
            } catch (Exception e) {
                Log.e(Common.LOG_TAG, "login error:" + e.getMessage());
                sr.setError(e.getMessage());
            }
            return sr;
        }

        @Override
        public ServiceResponse searchUser(String user) throws RemoteException {
            ServiceResponse sr = new ServiceResponse();
            try {
                sr.setData(client.findUser(user));
                Log.e(Common.LOG_TAG, "find user ok , recv:" + sr.getData());
            } catch (Exception e) {
                Log.e(Common.LOG_TAG, "find user error:" + e.getMessage());
                sr.setError(e.getMessage());
            }
            return sr;
        }

        @Override
        public void basicTypes(int anInt, long aLong, boolean aBoolean, float aFloat, double aDouble, String aString) throws RemoteException {

        }
    };

    @Override
    public IBinder onBind(Intent i) {
        Log.e(Common.LOG_TAG, "============> TestService.onBind");
        return serviceBinder;
    }
//
//    @Override
//    public boolean onUnbind(Intent i) {
//        Log.e(Common.LOG_TAG, "============> TestService.onUnbind");
//        return false;
//    }
//
//    @Override
//    public void onRebind(Intent i) {
//        Log.e(Common.LOG_TAG, "============> TestService.onRebind");
//    }

    private CandyClient client;
    private String account;
    private String passwd;

    private CandyMessage candy;

    @Override
    public void onCreate() {
        Log.i(Common.LOG_TAG, "service onCreate begin");
        msgClient = new MessageClient();
        try {
            client = newCandyClient("candy.dearcode.net:9000", msgClient);
            //client = newCandyClient("192.168.0.103:9000", msgClient);
        } catch (Exception e) {
            Log.e(Common.LOG_TAG, "service start candy client error:" + e.getMessage());
        }
        candy = CandyMessage.Stub.asInterface(serviceBinder);
        Log.i(Common.LOG_TAG, "service onCreate candy ok" + client);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        String type = intent.getStringExtra("type");
        if("1".equals(type)) {
            account = intent.getStringExtra("account");
            passwd = intent.getStringExtra("passwd");

            Log.i(Common.LOG_TAG, "onStartCommand:" + account + "," + passwd);

            BroadcastMsg m = new BroadcastMsg();
            m.setType(1);
            try {
                candy.connect();
                ServiceResponse sr = candy.login(account, passwd);
                if(!sr.hasError()) {
                    m.setIret(0);
                    m.setData(sr.getId());
                } else {
                    m.setIret(1);
                    m.setMsg(sr.getError());
                }
            } catch (Exception ex) {
                Log.e(Common.LOG_TAG, "login error" + ex.getMessage());
                m.setIret(99);
                m.setMsg("Error other!");
            }


            notifyMsg(m);

            Log.i(Common.LOG_TAG, "login ok");
        }
        else if("2".equals(type)) {
            String id = intent.getStringExtra("uid");
            String msg = intent.getStringExtra("msg");
            try {
                candy.sendMessage(0, Long.parseLong(id), msg);
            } catch (Exception ex) {
                Log.e(Common.LOG_TAG, "send error" + ex);
            }
        }

        return super.onStartCommand(intent, flags, startId);
    }

    private void notifyMsg(BroadcastMsg bmsg) {
        Intent i = new Intent("net.dearcode.candy.message");
        Bundle b = new Bundle();
        b.putParcelable("broadcast", bmsg);
        i.putExtras(b);
        sendBroadcast(i);
    }

    @Override
    public void onDestroy() {
        Log.e(Common.LOG_TAG, "============> TestService.onDestroy");
    }

}
