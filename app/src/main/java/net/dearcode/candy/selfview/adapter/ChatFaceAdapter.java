package net.dearcode.candy.selfview.adapter;

import android.app.Activity;
import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.SimpleAdapter;
import android.widget.TextView;


import net.dearcode.candy.R;
import net.dearcode.candy.util.FaceUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ChatFaceAdapter extends PagerAdapter {

	Activity activity;
	ArrayList<View> viewList;
	EditText etReply;

	public ChatFaceAdapter(Activity activity, EditText et) {

		this.activity = activity;
		etReply = et;
		viewList = new ArrayList<View>();

		LayoutInflater mInflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		for (int i=0; i<5; i++) {
			View v = mInflater.inflate(R.layout.layout_chat_etc_face_adapter, null);

			List<Map<String,Object>> faceItemList = null;
			switch (i) {
				case 0:
					faceItemList = FaceUtil.faceItemList1;
					break;
				case 1:
					faceItemList = FaceUtil.faceItemList2;
					break;
				case 2:
					faceItemList = FaceUtil.faceItemList3;
					break;
				case 3:
					faceItemList = FaceUtil.faceItemList4;
					break;
				case 4:
					faceItemList = FaceUtil.faceItemList5;
					break;
			}

			GridView gvFace = (GridView)v.findViewById(R.id.gv_face);
			gvFace.setOnItemClickListener(new FaceUtil.MyFaceItemClickListener(activity, etReply, faceItemList));
			SimpleAdapter simpleAdapter = new SimpleAdapter(activity, faceItemList, R.layout.layout_chat_adapter_face, new String[]{"imageId"}, new int[]{R.id.image});
			gvFace.setAdapter(simpleAdapter);
			gvFace.setSelector(R.drawable.hide_listview_yellow);

			TextView tv = (TextView)v.findViewById(R.id.tv_facePage);
			tv.setText((i+1)+"/5");

			viewList.add(v);
		}

	}

	@Override
	public int getCount() {
		return viewList.size();
	}

	@Override
	public boolean isViewFromObject(View arg0, Object arg1) {
		return arg0 == arg1;
	}

	@Override
	public Object instantiateItem(View arg0, int position) {
		((ViewPager) arg0).addView(viewList.get(position));
		return viewList.get(position);
	}

	@Override
	public void destroyItem(View arg0, int position, Object arg2) {
		((ViewPager) arg0).removeView(viewList.get(position));
	}
}
