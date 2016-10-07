package net.dearcode.candy.controller.service;

import android.app.Service;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;

import net.dearcode.candy.CandyMessage;
import net.dearcode.candy.model.Event;
import net.dearcode.candy.model.Message;
import net.dearcode.candy.model.Relation;
import net.dearcode.candy.model.ServiceResponse;
import net.dearcode.candy.util.Common;

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
            Log.e(Common.LOG_TAG, "onRecv");
            Intent i = new Intent("net.dearcode.candy.message");
            Message m = new Message(Event.values()[event], Relation.values()[relation] , id, group, from, to, msg);
            Bundle b = new Bundle();
            b.putParcelable("message", m);
            i.putExtras(b);
            sendBroadcast(i);
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

    @Override
    public boolean onUnbind(Intent i) {
        Log.e(Common.LOG_TAG, "============> TestService.onUnbind");
        return false;
    }

    @Override
    public void onRebind(Intent i) {
        Log.e(Common.LOG_TAG, "============> TestService.onRebind");
    }

    CandyClient client;

    @Override
    public void onCreate() {
        Log.e(Common.LOG_TAG, "service onCreate begin");
        msgClient = new MessageClient();
        try {
            client = newCandyClient("candy.dearcode.net:9000", msgClient);
            //client = newCandyClient("192.168.0.103:9000", msgClient);
        } catch (Exception e) {
            Log.e(Common.LOG_TAG, "service start candy client error:" + e.getMessage());
        }
        Log.e(Common.LOG_TAG, "service onCreate candy ok");
    }

    @Override
    public void onStart(Intent intent, int startId) {
        Log.e(Common.LOG_TAG, "============> TestService.onStart");
    }

    @Override
    public void onDestroy() {
        Log.e(Common.LOG_TAG, "============> TestService.onDestroy");
    }

}
