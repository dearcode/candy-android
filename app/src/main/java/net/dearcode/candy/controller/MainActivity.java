package net.dearcode.candy.controller;

import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTabHost;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TabHost;

import net.dearcode.candy.R;
import net.dearcode.candy.controller.base.BaseFragmentActivity;

/**
 * Created by 水寒 on 2016/9/17.
 * 主Activity
 */
public class MainActivity extends BaseFragmentActivity {

    private FragmentTabHost mTabHost;

    private Class<?> fragmentArray[] = {FragmentConversation.class, FragmentFriends.class,
            FragmentExplore.class, FragmentOther.class};

    private int mImageViewArray[] = {R.drawable.tab_conversation, R.drawable.tab_people,
            R.drawable.tab_find, R.drawable.tab_set};

    private String mTextviewArray[] = {"对话","好友","发现","更多"};


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
        setContentView(R.layout.activity_main);

        //getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.title_bar);

        initView();
    }

    private void initView() {
        mTabHost = (FragmentTabHost) findViewById(android.R.id.tabhost);
        mTabHost.setup(this, getSupportFragmentManager(), R.id.realtabcontent);

        setTabInfo();
    }

    private void setTabInfo() {
        int count = fragmentArray.length;

        mTabHost.setCurrentTab(0);
        mTabHost.clearAllTabs();
        for(int i=0;i<count; i++) {
            TabHost.TabSpec tabSpec= mTabHost.newTabSpec(mTextviewArray[i]).setIndicator(getTabItemView(i));
            mTabHost.addTab(tabSpec, fragmentArray[i], null);
        }
    }

    private View getTabItemView(int index) {
        View view = LayoutInflater.from(this).inflate(R.layout.tab_item_view, null);
        ImageView imageView = (ImageView) view.findViewById(R.id.imageview);
        imageView.setContentDescription(mTextviewArray[index]);
        imageView.setImageResource(mImageViewArray[index]);

        return view;
    }
}
