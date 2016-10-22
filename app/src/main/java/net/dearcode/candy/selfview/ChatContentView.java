package net.dearcode.candy.selfview;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import net.dearcode.candy.R;
import net.dearcode.candy.modelview.MessageBean;
import net.dearcode.candy.selfview.adapter.ChatMsgAdapter;
import net.dearcode.candy.util.FaceUtil;
import net.dearcode.candy.util.SoundMeter;

import java.util.HashMap;
import java.util.List;

public class ChatContentView extends LinearLayout {

	private Activity activity;
	private Drawable dl1, dl2, dl3, dr1, dr2, dr3;
	private int position;
	private SoundMeter voiceUtil;
	private Handler handler;
	private List<MessageBean> dataList;
	private ChatMsgAdapter adapter;
	private MessageBean bean;
	private String jidFrom;
	private ViewGroup vgParent;
	private String roomManager;//群主和群管的ID之和
	private String ownerId;//群主ID
	private boolean isMe; // 是否是别人说的

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
							  Handler handler, List<MessageBean> dataList, ChatMsgAdapter adapter,
							  String jidFrom, ViewGroup vgParent, String roomManager, String ownerId, boolean isMe) {
		this.activity = activity;
		this.voiceUtil = voiceUtil;
		this.handler = handler;
		this.dataList = dataList;
		this.adapter = adapter;
		this.jidFrom = jidFrom;
		this.vgParent = vgParent;
		this.roomManager = roomManager;
		this.ownerId = ownerId;
		this.isMe = isMe;
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
		FaceUtil.replaceTextToFace(activity, bean.getContent(), tvText, false);
		tvText.setLineSpacing(15, 1.1f);
		tvText.setGravity(Gravity.CENTER | Gravity.LEFT);
		HashMap<Integer, String> map = new HashMap<Integer, String>();
		map.put(1, "删除");
		map.put(2, "复制");


		if(!isMe){
			tvText.setTextColor(getResources().getColor(R.color.new_color_deep));
			tvText.setBackgroundResource(R.drawable.chat_from_bg);
		}else{
			tvText.setTextColor(getResources().getColor(R.color.white));
			tvText.setBackgroundResource(R.drawable.chat_to_bg);
		}

		addView(tvText);
	}
}
