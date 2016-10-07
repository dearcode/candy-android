package net.dearcode.candy.controller.component;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.util.Log;

import net.dearcode.candy.CandyMessage;
import net.dearcode.candy.controller.service.MessageService;
import net.dearcode.candy.util.Common;

/**
 *  * Created by c-wind on 2016/9/21 15:47
 *  * mail：root@codecn.org
 *  
 */
public class ServiceBinder implements ServiceConnection {
    private CandyMessage candy = null;
    private Context ctx;

    public ServiceBinder(Context ctx) {
        this.ctx = ctx;
        Intent i = new Intent(ctx, MessageService.class);
        if (!ctx.bindService(i, this, Context.BIND_AUTO_CREATE)) {
            Log.e(Common.LOG_TAG, "bindService state");
        }
    }

    @Override
    public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
        candy = CandyMessage.Stub.asInterface(iBinder);
    }


    public CandyMessage getCandy() {
        return this.candy;
    }

    @Override
    public void onServiceDisconnected(ComponentName componentName) {
    }

    public void Disconnect() {
        ctx.unbindService(this);
    }
}
