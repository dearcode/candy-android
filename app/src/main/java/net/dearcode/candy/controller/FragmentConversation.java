package net.dearcode.candy.controller;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import net.dearcode.candy.R;
import net.dearcode.candy.controller.base.BaseFragment;
import net.dearcode.candy.model.ConversationListItem;
import net.dearcode.candy.selfview.adapter.ChatListAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by lujinfeifly on 16/9/22.
 */

public class FragmentConversation extends BaseFragment implements
        AdapterView.OnItemClickListener, AdapterView.OnItemLongClickListener{

    private ViewGroup root = null;
    private ListView lvChatList = null;
    private ChatListAdapter adapter;
    private List<ConversationListItem> dataList = new ArrayList<ConversationListItem>();

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        dataList = CustomeApplication.db.getChatList();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        root = (ViewGroup) inflater.inflate(R.layout.fragment_conversation,null);

        return root;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        initView();
    }

    private void initView() {
        lvChatList = (ListView) root.findViewById(R.id.lv_conversation);
        lvChatList.setOnItemClickListener(this);
        lvChatList.setOnItemLongClickListener(this);



        if(adapter == null) {
            adapter = new ChatListAdapter(getActivity(), dataList);
        }

        lvChatList.setAdapter(adapter);

    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Intent i = new Intent(getActivity(), ChatActivity.class);
        Bundle b = new Bundle();
        b.putString("uid", dataList.get(position).getmUserId() + "");
        i.putExtras(b);
        startActivity(i);
    }

    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
        return false;
    }
}
