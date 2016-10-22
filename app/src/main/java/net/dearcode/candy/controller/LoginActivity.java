package net.dearcode.candy.controller;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.RemoteException;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import net.dearcode.candy.R;
import net.dearcode.candy.controller.base.BaseActivity;
import net.dearcode.candy.controller.command.BroadcastMsg;
import net.dearcode.candy.controller.service.MessageService;
import net.dearcode.candy.model.Message;
import net.dearcode.candy.model.ServiceResponse;
import net.dearcode.candy.modelview.MessageBean;
import net.dearcode.candy.util.Common;

/**
 * Created by lujinfeifly on 16/9/27.
 */

public class LoginActivity extends BaseActivity {

    private Button buttonLogin;
    private EditText editTextAccount;
    private EditText editTextPwd;

    private String account = "";
    private String pwd = "";


    private MyBrocastReciver myBrocastReciver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_login);

        initView();

        myBrocastReciver = new MyBrocastReciver();
        IntentFilter filter = new IntentFilter();
        filter.addAction("net.dearcode.candy.message");
        registerReceiver(myBrocastReciver,filter);
    }

    private void initView() {
        buttonLogin = (Button) findViewById(R.id.bt_login);
        editTextAccount = (EditText) findViewById(R.id.et_account);
        editTextPwd = (EditText) findViewById(R.id.et_passwd);

        buttonLogin.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()) {
            case R.id.bt_login:
                account = editTextAccount.getText().toString();
                pwd = editTextPwd.getText().toString();

                Intent i = new Intent(this, MessageService.class);
                i.putExtra("type", "1");
                i.putExtra("account", account);
                i.putExtra("passwd", pwd);
                this.startService(i);
                break;
            default:
                super.onClick(v);
        }

    }

    private void goToCandyActivity(Bundle b) {
        Intent i = new Intent(this, MainActivity.class);
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        i.putExtras(b);
        startActivity(i);
        finish();
    }

    private class MyBrocastReciver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            Bundle b = intent.getExtras();
            if(b == null) {
                return ;
            }
            BroadcastMsg m = b.getParcelable("broadcast");
            if(m == null) {
                return;
            }

            if(m.getIret() == 0) {
                CustomeApplication.getInstance().setLogin(true);
                CustomeApplication.getInstance().getLocalPreferences().save("login",
                        account + "#@#" + pwd + "#@#" + m.getData());

                goToCandyActivity(b);
            } else {
                toastDisplay(m.getMsg());
            }

        }
    }

    @Override
    protected void onStop() {
        unregisterReceiver(myBrocastReciver);
        super.onStop();
    }
}
