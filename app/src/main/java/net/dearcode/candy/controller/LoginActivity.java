package net.dearcode.candy.controller;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.RemoteException;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import net.dearcode.candy.R;
import net.dearcode.candy.controller.base.BaseActivity;
import net.dearcode.candy.model.ServiceResponse;

/**
 * Created by lujinfeifly on 16/9/27.
 */

public class LoginActivity extends BaseActivity {

    private Button buttonLogin;
    private EditText editTextAccount;
    private EditText editTextPwd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_login);

        initView();
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
                try {

                    Log.i("@@@@@@@@@","111111111111");
                    String account = editTextAccount.getText().toString();
                    String pwd = editTextPwd.getText().toString();

                    CustomeApplication.getService().connect();
                    ServiceResponse sr = CustomeApplication.getService().login(account, pwd);
                    if (sr.hasError()) {
                        //Snackbar.make(view, Errors.ParseError(getApplicationContext(),sr.getError()), Snackbar.LENGTH_LONG).show();
                        Log.i("@@@@@@@@@",sr.getError());
                        return;
                    }

                    Bundle b = new Bundle();
                    b.putString("user", account);
                    b.putString("password", pwd);
                    b.putLong("uid", sr.getId());

                    CustomeApplication.getInstance().setLogin(true);

                    CustomeApplication.getInstance().getLocalPreferences().save("login",
                            account + "#@#" + pwd + "#@#" + sr.getId());

                    //b.putInt("from", CandyActivity.FromLogin);
                    goToCandyActivity(b);
                } catch (RemoteException e) {
                    //Snackbar.make(view, e.getMessage(), Snackbar.LENGTH_LONG).show();
                    Log.i("@@@@@@@@@","111" + e.getMessage());
                }
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
}
