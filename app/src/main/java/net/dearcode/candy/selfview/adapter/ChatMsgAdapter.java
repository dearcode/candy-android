package net.dearcode.candy.selfview.adapter;

import android.app.Activity;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import net.dearcode.candy.R;
import net.dearcode.candy.controller.base.BaseActivity;
import net.dearcode.candy.model.Message;
import net.dearcode.candy.modelview.MessageBean;
import net.dearcode.candy.selfview.ChatContentView;
import net.dearcode.candy.util.SoundMeter;


import java.io.File;
import java.util.List;

import deer.milu.freejava.basic.MTime;

public class ChatMsgAdapter extends BaseAdapter {

	private List<MessageBean> dataList;
	private BaseActivity context;
	private SoundMeter voiceUtil;
	//群聊还是单聊
	private String which;
	private String roomId;
//	private Chat chat;
	public int nowPosition;
	private Handler handler = null;
	private String jidFrom;
//	protected RequestManager gidleRequest;
	private String roomManager;//群主和群管ID
	private String ownerId;//群主ID

	public ChatMsgAdapter(BaseActivity ctx, List<MessageBean> d, String which,
						  /*Chat chat SoundMeter voiceUtil,,*/ String jidFrom, String roomId, Handler handler) {
		context = ctx;
		dataList = d;
//		gidleRequest = Glide.with(ctx.getApplicationContext());
		this.voiceUtil = voiceUtil;

		this.which = which;
//		this.chat = chat;
		this.jidFrom = jidFrom;
		this.roomId = roomId;
		this.handler = handler;

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
	public View getView(int position, View convertView, ViewGroup parent) {
		final MessageBean bean = dataList.get(position);
//		XmppUserBean userBean = bean.getUserBean();
//		if(userBean == null){
//			userBean = new XmppUserBean();
//		}

		//根据from区分左边还是右边 true 左边  false 右边
		boolean isOther = (bean.getUser().getUserId() == 1);

		ViewHolder viewHolder = null;
		if (convertView == null ) {
			viewHolder = new ViewHolder();
			if (isOther) {
				convertView = LayoutInflater.from(context).inflate(R.layout.layout_chat_adapter_msg_left, null);
			} else {
				convertView = LayoutInflater.from(context).inflate(R.layout.layout_chat_adapter_msg_right, null);
				viewHolder.pbSending = (ProgressBar)convertView.findViewById(R.id.pb_sending);
				viewHolder.ivFailed = (ImageView)convertView.findViewById(R.id.iv_failed);
			}
			viewHolder.tvSendTime = (TextView) convertView.findViewById(R.id.tv_sendtime);
			viewHolder.tvSendTime.setTextSize(10);
			viewHolder.llChatEtc = convertView.findViewById(R.id.rl_chatEtc);
			viewHolder.ivUserHeader = (ImageView)convertView.findViewById(R.id.iv_userhead);
			viewHolder.ivGameIcon = (ImageView)convertView.findViewById(R.id.iv_gameIcon);
			viewHolder.tvUserName = (TextView) convertView.findViewById(R.id.tv_username);
			viewHolder.isOther = isOther;
			viewHolder.vContent = (ChatContentView)convertView.findViewById(R.id.v_chat_content);
			viewHolder.vContent.setProperties(context, voiceUtil, handler, dataList, this,
					jidFrom, parent, roomManager, ownerId, isOther);

			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}

//		viewHolder.ivUserHeader.setOnClickListener(new HeaderOnclickListener(bean.getMessageFrom()));
//		viewHolder.ivUserHeader.setOnLongClickListener(new HeaderOnLongclickListener(bean));

		//提前隐藏的
		viewHolder.ivGameIcon.setVisibility(View.GONE);
		viewHolder.tvUserName.setVisibility(View.GONE);
		//通知类的消息，到此为止
//		if (XmppValue.Msg_Type_Notice == bean.getMessageType()) {
//			viewHolder.llChatEtc.setVisibility(View.GONE);
//			viewHolder.tvSendTime.setVisibility(View.VISIBLE);
//			viewHolder.tvSendTime.setText(TimeUtil.getHumenTime(bean.getMessageDate()) + "  " + bean.getMessageContent());
//			return convertView;
//		}
//		//添加好友消息
//		if(XmppValue.Msg_Type_Add_Friend == bean.getMessageType()){
//			viewHolder.llChatEtc.setVisibility(View.GONE);
//			viewHolder.tvSendTime.setVisibility(View.VISIBLE);
//			viewHolder.tvSendTime.setText("您已成功添加"+userBean.getNickName()+"为好友，快打个招呼吧~");
//			return convertView;
//		}

		viewHolder.llChatEtc.setVisibility(View.VISIBLE);
		viewHolder.tvSendTime.setVisibility(View.GONE);

		viewHolder.vContent.setChatContent(position, nowPosition);

//		//展示头像
//		if (beFrom && (bean.getMessageFrom().startsWith("tl_")
//				|| bean.getMessageFrom().startsWith("qz-tl") || bean.getMessageFrom().startsWith("gh-tl"))) {
//			ImageLoaderLocal.setImageView(context, gidleRequest, userBean.getUserHead(),
//					R.drawable.chat_icon_role_default, viewHolder.ivUserHeader, 0);
//		} else {
//			ImageLoaderLocal.setImageView(context, gidleRequest, userBean.getUserHead(),
//					R.drawable.default_head, viewHolder.ivUserHeader, 0);
//		}
		viewHolder.ivUserHeader.setImageBitmap(BitmapFactory.decodeResource(context.getResources(), R.mipmap.ic_launcher));
		//如果是左面的，并且是天龙里面的，则显示游戏图标
//		if (beFrom && isShowGameIcon(bean.getMessageFrom())) {
//			viewHolder.ivGameIcon.setVisibility(View.VISIBLE);
//		}

//		viewHolder.tvUserName.setText(userBean.getNickName());
//		viewHolder.tvUserName.setVisibility(View.VISIBLE);

		//是否显示发送时间
		viewHolder.tvSendTime.setText(MTime.getHumenTime(bean.getTime()));
		if (isShowTime(position)) {
			viewHolder.tvSendTime.setVisibility(View.VISIBLE);
		}

		//消息发送状态
//		if (!beFrom) {
//			viewHolder.ivFailed.setOnClickListener(new ResendOnclickListener(bean));
//			viewHolder.pbSending.setVisibility(View.GONE);
//			viewHolder.ivFailed.setVisibility(View.GONE);
//		}

		return convertView;
	}

	/**
	 * 是否显示发送时间
	 * @param position
	 * @return
	 */
	private boolean isShowTime(int position) {
		//第一个，显示
		if (position == 0) {
			return true;
		}
		//如果距离上一条消息3分钟，显示
		MessageBean now = dataList.get(position);
		MessageBean pre = dataList.get(position-1);
		if ((now.getTime()/1000) - (pre.getTime()/1000) > (5 * 60)) {
			return true;
		}
		return false;
	}

	class ViewHolder {
		public TextView tvSendTime;
		public View llChatEtc;
		public ImageView ivUserHeader;
		public ImageView ivGameIcon;
		public TextView tvUserName;
		public ProgressBar pbSending;
		public ImageView ivFailed;
		public ChatContentView vContent;

		public boolean isOther = true;
	}

	//群主ID
	public void setRoomManager(String roomManager) {
		this.roomManager = roomManager;
	}

	public String getRoomManager() {
		return roomManager;
	}

	public String getOwnerId() {
		return ownerId;
	}

	public void setOwnerId(String ownerId) {
		this.ownerId = ownerId;
	}
}
