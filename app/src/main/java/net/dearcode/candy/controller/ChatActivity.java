package net.dearcode.candy.controller;

import android.os.Bundle;

import net.dearcode.candy.R;
import net.dearcode.candy.controller.base.BaseActivity;

/**
 * Created by lujinfei on 2016/10/8.
 */
public class ChatActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.layout_chat);
        initView();
    }

    private void initView() {

    }
}
