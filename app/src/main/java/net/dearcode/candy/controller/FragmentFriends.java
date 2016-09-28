package net.dearcode.candy.controller;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import net.dearcode.candy.R;
import net.dearcode.candy.controller.base.BaseFragment;
import net.dearcode.candy.model.FriendListItem;
import net.dearcode.candy.selfview.adapter.FriendListAdpter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by lujinfeifly on 16/9/22.
 */

public class FragmentFriends extends BaseFragment implements
        AdapterView.OnItemClickListener, AdapterView.OnItemLongClickListener{

    private ViewGroup root = null;
    private ListView lvChatList = null;
    private FriendListAdpter adapter;
    private List<FriendListItem> dataList = new ArrayList<FriendListItem>();

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        dataList.add(new FriendListItem("Jobs", ""));
        dataList.add(new FriendListItem("steven", ""));
        dataList.add(new FriendListItem("Tim Jack", ""));
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        root = (ViewGroup) inflater.inflate(R.layout.fragment_friends,null);

        return root;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        initView();
    }

    private void initView() {
        lvChatList = (ListView) root.findViewById(R.id.lv_friends);
        lvChatList.setOnItemClickListener(this);
        lvChatList.setOnItemLongClickListener(this);

        getFriendList();

        if(adapter == null) {
            adapter = new FriendListAdpter(getActivity(), dataList);
        }

        lvChatList.setAdapter(adapter);

    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

    }

    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
        return false;
    }

    public void getFriendList() {
        FriendListItem[] friendListArray = new FriendListItem[] {
                new FriendListItem("唐僧", ""),
                new FriendListItem("猪师弟", ""),
                new FriendListItem("阿呆", ""),
                new FriendListItem("8899", ""),
                new FriendListItem("孙悟空", ""),
                new FriendListItem("阿三", ""),
                new FriendListItem("张三", ""),
                new FriendListItem("张二B", ""),
                new FriendListItem("阿三", ""),
                new FriendListItem("张三", ""),
                new FriendListItem("张二B", ""),
                new FriendListItem("阿三", ""),
                new FriendListItem("张三", ""),
                new FriendListItem("张二B", ""),
                new FriendListItem("阿三", ""),
                new FriendListItem("张三", ""),
                new FriendListItem("张二B", ""),
                new FriendListItem("李四", ""),
                new FriendListItem("王小二", ""),
                new FriendListItem("张三丰", ""),
                new FriendListItem("郭靖", ""),
                new FriendListItem("张无忌", ""),
                new FriendListItem("黄小贤", "")};

        //dataList = Arrays.asList(friendListArray);
    }
}
