package net.dearcode.candy.selfview.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import net.dearcode.candy.R;
import net.dearcode.candy.model.FriendListItem;

import java.util.List;

/**
 * Created by lujinfeifly on 16/9/27.
 */

public class FriendListAdpter extends BaseAdapter {

    private Context context;
    private List<FriendListItem> dataList;

    public FriendListAdpter(Context c, List<FriendListItem> l) {
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
            converView = LayoutInflater.from(context).inflate(R.layout.listitem_friend, null);
            item = new ViewItem();
            item.ivUserHead = (ImageView)converView.findViewById(R.id.iv_userhead);
            item.tvUserName = (TextView)converView.findViewById(R.id.tv_username);

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

        public void setData(FriendListItem bean) {
            tvUserName.setText(bean.getmName());
        }
    }
}
