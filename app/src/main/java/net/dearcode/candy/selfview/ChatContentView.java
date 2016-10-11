package net.dearcode.candy.selfview;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import net.dearcode.candy.R;
import net.dearcode.candy.model.Message;
import net.dearcode.candy.selfview.adapter.ChatMsgAdapter;
import net.dearcode.candy.util.FaceUtil;
import net.dearcode.candy.util.SoundMeter;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ChatContentView extends LinearLayout {

	private Activity activity;
	private Drawable dl1, dl2, dl3, dr1, dr2, dr3;
	private int position;
	private SoundMeter voiceUtil;
	private Handler handler;
	private List<Message> dataList;
	private ChatMsgAdapter adapter;
	private Message bean;
	private String jidFrom;
	private ViewGroup vgParent;
	private String roomManager;//群主和群管的ID之和
	private String ownerId;//群主ID

	public ChatContentView(Context context) {
		super(context);
	}

	public ChatContentView(Context context, AttributeSet attr){
		super(context, attr);
		this.setOrientation(LinearLayout.HORIZONTAL);
	}

	@SuppressLint("NewApi")
	public ChatContentView(Context context, AttributeSet attr, int defStyle){
		super(context, attr, defStyle);
		this.setOrientation(LinearLayout.HORIZONTAL);
	}

	public void setProperties(Activity activity, SoundMeter voiceUtil,
							  Handler handler, List<Message> dataList, ChatMsgAdapter adapter,
							  String jidFrom, ViewGroup vgParent, String roomManager, String ownerId) {
		this.activity = activity;
		this.voiceUtil = voiceUtil;
		this.handler = handler;
		this.dataList = dataList;
		this.adapter = adapter;
		this.jidFrom = jidFrom;
		this.vgParent = vgParent;
		this.roomManager = roomManager;
		this.ownerId = ownerId;
	}

	/**
	 * 根据类型，设置不同的聊天内容，nowPosition 语音播放布局会用 
	 * @param position
	 * @param nowPosition
	 */
	public void setChatContent(int position, int nowPosition) {
		this.position = position;
		bean = dataList.get(position);

		removeAllViews();

		setText();
		//根据类型显示文本还是图片，后续如果有新类型再增加
//		switch (bean.getMessageType()) {
//			case XmppValue.Msg_Type_Plain:
//				setText();
//				break;
//
//			case XmppValue.Msg_Type_Image:
//				setImage(bAccused);
//				break;
//
//			case XmppValue.Msg_Type_Voice:
//				setVoice(nowPosition, bAccused);
//				break;

//			default:
//				break;
//		}
	}

	private void setText() {
		TextView tvText = new TextView(activity);
		FaceUtil.replaceTextToFace(activity, bean.getMsg(), tvText, false);
		tvText.setLineSpacing(15, 1.1f);
		tvText.setGravity(Gravity.CENTER | Gravity.LEFT);
		HashMap<Integer, String> map = new HashMap<Integer, String>();
		map.put(1, "删除");
		map.put(2, "复制");


		if(true){
			tvText.setTextColor(getResources().getColor(R.color.new_color_deep));
			tvText.setBackgroundResource(R.drawable.chat_from_bg);
		}else{
			tvText.setTextColor(getResources().getColor(R.color.white));
			tvText.setBackgroundResource(R.drawable.chat_to_bg);
		}

		addView(tvText);
	}
}
