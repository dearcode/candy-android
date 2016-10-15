package net.dearcode.candy.selfview.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import net.dearcode.candy.R;
import net.dearcode.candy.model.ConversationListItem;

import java.util.List;

import deer.milu.freejava.basic.MTime;

/**
 * Created by lujinfeifly on 16/9/26.
 */

public class ChatListAdapter extends BaseAdapter {
    private Context context;
    private List<ConversationListItem> dataList;

    public ChatListAdapter(Context c, List<ConversationListItem> l) {
        context = c;
        dataList = l;
    }

    @Override
    public int getCount() {
        return dataList.size();
    }

    @Override
    public Object getItem(int position) {
        return dataList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View converView, ViewGroup parent) {
        ViewItem item;
        if(converView == null) {
            converView = LayoutInflater.from(context).inflate(R.layout.listitem_chat, null);
            item = new ViewItem();
            item.ivUserHead = (ImageView)converView.findViewById(R.id.iv_userhead_chatlist);
            item.tvUserName = (TextView)converView.findViewById(R.id.tv_username);
            item.tvContent  = (TextView)converView.findViewById(R.id.tv_content);
            item.tvDate = (TextView)converView.findViewById(R.id.tv_date);

            converView.setTag(item);

        }else {
            item = (ViewItem)converView.getTag();
        }

        item.setData(dataList.get(position));



        return converView;
    }

    public class ViewItem {
        public ImageView ivUserHead;
        public TextView tvUserName;
        public TextView tvContent;
        public TextView tvDate;

        public void setData(ConversationListItem bean) {
            tvUserName.setText(bean.getmUserName());
            tvDate.setText(MTime.getHumenTime(bean.getmDate()));
            tvContent.setText(bean.getmContent());
        }
    }
}
