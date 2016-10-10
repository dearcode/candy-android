package net.dearcode.candy.controller;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import net.dearcode.candy.R;
import net.dearcode.candy.controller.base.BaseActivity;

/**
 * Created by lujinfei on 2016/10/8.
 */
public class HumanActivity extends BaseActivity {

    private Button btnToChat;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.layout_human);
        initView();
    }

    private void initView() {
        btnToChat = (Button)findViewById(R.id.btn_gotochat);
        btnToChat.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent i = new Intent(HumanActivity.this, ChatActivity.class);
                        Bundle b = new Bundle();
                        b.putString("uid", "0");
                        i.putExtras(b);
                        startActivity(i);
                    }
                }
        );
    }
}
