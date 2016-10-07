package net.dearcode.candy.controller;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import net.dearcode.candy.R;
import net.dearcode.candy.controller.base.BaseFragment;

/**
 * Created by lujinfeifly on 16/9/22.
 */

public class FragmentOther extends BaseFragment {

    private ViewGroup root = null;
    private Button btnExit;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        root = (ViewGroup) inflater.inflate(R.layout.fragment_other,null);

        return root;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        initView();
    }

    private void initView() {
        // 退出按钮的初始化
        btnExit = (Button)root.findViewById(R.id.btn_exit);
        btnExit.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ((MainActivity)getActivity()).exit();
                    }
                }
        );
    }


}
