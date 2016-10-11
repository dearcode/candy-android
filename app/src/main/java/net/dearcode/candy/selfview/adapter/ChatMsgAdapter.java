package net.dearcode.candy.selfview.adapter;

import android.app.Activity;
import android.content.Intent;
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
import net.dearcode.candy.selfview.ChatContentView;
import net.dearcode.candy.util.SoundMeter;


import java.io.File;
import java.util.List;

public class ChatMsgAdapter extends BaseAdapter {

	private List<Message> dataList;
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

	public ChatMsgAdapter(BaseActivity ctx, List<Message> d, String which,
						  /*Chat chat,*/ SoundMeter voiceUtil, String jidFrom, String roomId, Handler handler) {
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
		final Message bean = dataList.get(position);
//		XmppUserBean userBean = bean.getUserBean();
//		if(userBean == null){
//			userBean = new XmppUserBean();
//		}

		//根据from区分左边还是右边 true 左边  false 右边
		boolean beFrom = (bean.getFrom() == 1);

		ViewHolder viewHolder = null;
		if (convertView == null ) {
			viewHolder = new ViewHolder();
			if (beFrom) {
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
			viewHolder.isFrom = beFrom;
			viewHolder.vContent = (ChatContentView)convertView.findViewById(R.id.v_chat_content);
			viewHolder.vContent.setProperties(context, voiceUtil, handler, dataList, this,
					jidFrom, parent, roomManager, ownerId);

			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}

		viewHolder.ivUserHeader.setOnClickListener(new HeaderOnclickListener(bean.getMessageFrom()));
		viewHolder.ivUserHeader.setOnLongClickListener(new HeaderOnLongclickListener(bean));

		//提前隐藏的
		viewHolder.ivGameIcon.setVisibility(View.GONE);
		viewHolder.tvUserName.setVisibility(View.GONE);
		//通知类的消息，到此为止
		if (XmppValue.Msg_Type_Notice == bean.getMessageType()) {
			viewHolder.llChatEtc.setVisibility(View.GONE);
			viewHolder.tvSendTime.setVisibility(View.VISIBLE);
			viewHolder.tvSendTime.setText(TimeUtil.getHumenTime(bean.getMessageDate()) + "  " + bean.getMessageContent());
			return convertView;
		}
		//添加好友消息
		if(XmppValue.Msg_Type_Add_Friend == bean.getMessageType()){
			viewHolder.llChatEtc.setVisibility(View.GONE);
			viewHolder.tvSendTime.setVisibility(View.VISIBLE);
			viewHolder.tvSendTime.setText("您已成功添加"+userBean.getNickName()+"为好友，快打个招呼吧~");
			return convertView;
		}

		viewHolder.llChatEtc.setVisibility(View.VISIBLE);
		viewHolder.tvSendTime.setVisibility(View.GONE);

		viewHolder.vContent.setChatContent(position, nowPosition);

		//展示头像
		if (beFrom && (bean.getMessageFrom().startsWith("tl_")
				|| bean.getMessageFrom().startsWith("qz-tl") || bean.getMessageFrom().startsWith("gh-tl"))) {
			ImageLoaderLocal.setImageView(context, gidleRequest, userBean.getUserHead(),
					R.drawable.chat_icon_role_default, viewHolder.ivUserHeader, 0);
		} else {
			ImageLoaderLocal.setImageView(context, gidleRequest, userBean.getUserHead(),
					R.drawable.default_head, viewHolder.ivUserHeader, 0);
		}
		//如果是左面的，并且是天龙里面的，则显示游戏图标
//		if (beFrom && isShowGameIcon(bean.getMessageFrom())) {
//			viewHolder.ivGameIcon.setVisibility(View.VISIBLE);
//		}

		//用户名(如果是群聊，别人的名称要展示，自己的角色名称展示；如果是单聊，只展示自己角色名称)
		if ("groupChat".equals(which)) {  /** 群聊：别人的消息，总是展示名称 ;  自己的角色消息，展示名称*/
			if (beFrom || bean.getMessageTo().startsWith("qz-tl") || bean.getMessageTo().startsWith("gh-tl")) {
				viewHolder.tvUserName.setVisibility(View.VISIBLE);
				//判断认证群组的群主ID，红色特殊处理
				if(ConstantValue.ROOM_OWNER.equals(userBean.getDescription())){
					SpannableString ss = new SpannableString("[群主] " + userBean.getNickName());
					ForegroundColorSpan span = new ForegroundColorSpan(context.getResources().getColor(R.color.red));
					ss.setSpan(span, 0, 4, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
					viewHolder.tvUserName.setText(ss);
				}else if(ConstantValue.ROOM_ADMIN.equals(userBean.getDescription())){
					SpannableString ss = new SpannableString("[群管] " + userBean.getNickName());
					ForegroundColorSpan span = new ForegroundColorSpan(context.getResources().getColor(R.color.new_color_orange));
					ss.setSpan(span, 0, 4, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
					viewHolder.tvUserName.setText(ss);
				}else{
					viewHolder.tvUserName.setText(userBean.getNickName());
				}
			}
		} else if (!beFrom && bean.getMessageFrom().startsWith("tl_")) {  /** 单聊：只展示自己的角色名 */
			viewHolder.tvUserName.setText(userBean.getNickName());
			viewHolder.tvUserName.setVisibility(View.VISIBLE);
		}

		//是否显示发送时间
		viewHolder.tvSendTime.setText(TimeUtil.getHumenTime(bean.getMessageDate()));
		if (isShowTime(position)) {
			viewHolder.tvSendTime.setVisibility(View.VISIBLE);
		}

		//消息发送状态
		if (!beFrom) {
			viewHolder.ivFailed.setOnClickListener(new ResendOnclickListener(bean));
			viewHolder.pbSending.setVisibility(View.GONE);
			viewHolder.ivFailed.setVisibility(View.GONE);

			if (bean.getMessageState() == XmppValue.Msg_State_Sending) {
				viewHolder.pbSending.setVisibility(View.VISIBLE);
			} else if (bean.getMessageState() == XmppValue.Msg_State_Failed) {
				viewHolder.ivFailed.setImageResource(R.drawable.chat_resend_icon);
				viewHolder.ivFailed.setVisibility(View.VISIBLE);
			} else if(bean.getMessageState() == XmppValue.Msg_State_Banned) {
				viewHolder.ivFailed.setImageResource(R.drawable.chat_banned_failed);
				viewHolder.ivFailed.setVisibility(View.VISIBLE);
			}
		}

		if(StringUtil.isEmpty(userBean.getNickName())){
			ThreadPool.getLongPool().execute(new Runnable() {
				@Override
				public void run() {
					CYSecurity_Application cyApp = (CYSecurity_Application)((Activity)context).getApplication();
					String cyjId = cyApp.getCyjUserBean().getCyjId();
					String chatId = bean.getMessageFrom();
					//如果是群聊的，则截取后面的人
					try {
						if (chatId.startsWith("qz") || chatId.startsWith("gh")) {
							chatId = chatId.split("/")[1] + "@" + XmppTool.HOST;
						}
						if(!chatId.startsWith("tl_")){
							String userId = (chatId.split("@")[0]).split("_")[1];
							XmppUserBean u = XmppTool.getInstance().getUserFromRose(context, userId);
							if (u != null) {
								u.setUserId(chatId);
								XmppDao_RoomMember roomMemberDao = new XmppDao_RoomMember(context);
								Boolean bHas = roomMemberDao.getRoomOneUser(XmppValue.muc.getRoom(), cyjId, chatId);
								if(!bHas){
									roomMemberDao.insertRoomUser(u, XmppValue.muc.getRoom(), cyjId, 1);
								}
								roomMemberDao.closeDBService();
							}
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			});
		}
		if(StringUtil.isEmpty(userBean.getUserHead())){
			XmppDao_RoomMember memDao = new XmppDao_RoomMember(context);
			try {
				CYSecurity_Application cyApp = (CYSecurity_Application)((Activity)context).getApplication();
				String cyjId = cyApp.getCyjUserBean().getCyjId();
				String chatId = bean.getMessageFrom();
				//如果是群聊的，则截取后面的人
				if (chatId.startsWith("qz") || chatId.startsWith("gh")) {
					chatId = chatId.split("/")[1] + "@" + XmppTool.HOST;
				}
				if (chatId.contains(XmppValue.jid)) {
					memDao.updateUserHead(cyApp.getCyjUserInfoBean().getUserHead(), chatId, cyjId);
				} else if (chatId.contains(jidFrom.split("/")[0])) {
					XmppDao_MyRole myRoleDao = new XmppDao_MyRole(context);
					XmppRoleBean myRole = myRoleDao.getRoleByRoleId(chatId, cyjId);
					myRoleDao.closeDBService();
					if(myRole!=null && !StringUtil.isEmpty(myRole.getAvatar())){
						memDao.updateUserHead(myRole.getAvatar(), chatId, cyjId);
					}
				} else {
					XmppDao_Friend fDao = new XmppDao_Friend(context);
					//先从本地联系人中找，没有再从群成员中找
					XmppUserBean user = fDao.getOne(chatId, jidFrom, cyApp.getCyjUserBean().getCyjId());
					fDao.closeDBService();
					//如果为空，再从群成员中找
					if (XmppValue.muc != null && (user == null || StringUtil.isEmpty(user.getUserId()))) {
						XmppDao_RoomMember roomMemberDao = new XmppDao_RoomMember(context);
						user = roomMemberDao.getRoomUser(XmppValue.muc.getRoom(), chatId, cyApp.getCyjUserBean().getCyjId());
						roomMemberDao.closeDBService();
					}
					if(user!=null && !StringUtil.isEmpty(user.getUserHead())){
						memDao.updateUserHead(user.getUserHead(), chatId, cyjId);
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}finally{
				memDao.closeDBService();
			}
		}

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
		XmppMessageBean now = dataList.get(position);
		XmppMessageBean pre = dataList.get(position-1);
		if ((now.getMessageDate()/1000) - (pre.getMessageDate()/1000) > (5 * 60)) {
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

		public boolean isFrom = true;
	}

	/**
	 * 头像点击
	 * @author gaoguichun
	 */
	class HeaderOnclickListener implements OnClickListener {

		private String chatId;
		private boolean bPubAccount = false;//代表是否为公众号

		public HeaderOnclickListener(String id){
			chatId = id;
			//如果是群聊的，则截取后面的人
			try {
				if (chatId.startsWith("qz") || chatId.startsWith("gh")) {
					chatId = chatId.split("/")[1] + "@" + XmppTool.HOST;
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		public void onClick(View v) {
			try {
			String cyjId = context.cyApp.getCyjUserBean().getCyjId();
			if(StringUtil.isEmpty(cyjId)){
				Intent intentRegister = ZZBUtil.getRegisterIntent(context, ConstantValue.CYJUSER_REGISTER_FROM_MAINTAB);
				context.startActivity(intentRegister);
				return;
			}
			String name = context.cyApp.getCyjUserInfoBean().getNickName();
			if(StringUtil.isEmpty(name)){
				Intent intentUserInfo = new Intent(context, CYSecurity_CyjUserInfo.class);
				intentUserInfo.putExtra("FROM", ConstantValue.CYJUSER_REGISTER_FROM_MAINTAB);
				context.startActivity(intentUserInfo);
				return;
			}
			XmppUserBean user = null;
			XmppPublicAccountBean bean = null;
			//如果是自己，则自己构建，否则从数据库中取
			if (chatId.equals(XmppValue.jid)) {
				user = new XmppUserBean();
				user.setUserId(XmppValue.jid);
				user.setNickName(name);
				user.setUserHead(context.cyApp.getCyjUserInfoBean().getUserHead());
			} else if (chatId.contains(jidFrom.split("/")[0])) {
				XmppDao_MyRole myRoleDao = new XmppDao_MyRole(context);
				XmppRoleBean myRole = myRoleDao.getRoleByRoleId(chatId, cyjId);
				myRoleDao.closeDBService();
				if(myRole != null){
					user = new XmppUserBean();
					user.setUserId(jidFrom);
					user.setNickName(myRole.getName());
					user.setUserHead(myRole.getAvatar());
				}
			} else if(chatId.startsWith("pub")){
				bPubAccount = true;
				XmppDao_PubAccount dao_PubAccount = new XmppDao_PubAccount(context);
				bean = dao_PubAccount.getNameById(chatId.split("@")[0]);
				dao_PubAccount.closeDBService();
			} else if (chatId.startsWith("qz") || chatId.startsWith("gh")){
				//先从本地联系人中找，没有再从群成员中找
				XmppDao_Friend fDao = new XmppDao_Friend(context);
				user = fDao.getOne(chatId, jidFrom, context.cyApp.getCyjUserBean().getCyjId());
				fDao.closeDBService();
				//如果为空，再从群成员中找
				if ((user == null || StringUtil.isEmpty(user.getUserId()) && XmppValue.muc != null)) {
					XmppDao_RoomMember roomMemberDao = new XmppDao_RoomMember(context);
					user = roomMemberDao.getRoomUser(XmppValue.muc.getRoom(), chatId, context.cyApp.getCyjUserBean().getCyjId());
					roomMemberDao.closeDBService();
				}
			}
			Intent intent = null;
			//如果不是公众号且user不为空
			if(!bPubAccount && user != null && StringUtil.isNotEmpty(user.getUserId())){
				intent = new Intent(context, CYSecurity_UserInfo.class);
				if("groupChat".equals(which)){
					intent.putExtra(ConstantValue.Relation_From_Key, ConstantValue.Relation_Group);
				}
				intent.putExtra("commentBean", user);
				intent.putExtra("jidFrom", jidFrom);
				context.startActivity(intent);
				return;
			}else if(bPubAccount && bean!=null && StringUtil.isNotEmpty(bean.getId())){
				intent = new Intent(context,CYSecurity_pubinfo.class);
				intent.putExtra("name", bean.getName());
				intent.putExtra("pubNews", false);
				context.startActivity(intent);
				return;
			}

			if(!NetworkUtil.isNetAvailable(context)){
				return;
			}

			//如果都没获取到，则从服务器加载
			context.showLoading();
			ThreadPool.getLongPool().execute(new Runnable() {
				@Override
				public void run() {
					try {
						//如果是角色，从XMPP服务器要
						if (chatId.startsWith("tl_")) {
							XmppRoleBean role = XmppTool.getInstance().getRole(context, chatId, jidFrom).get(0);
							if (role == null) {
								context.runOnUiThread(new UIRunnable(90, null));
								return;
							}
							XmppUserBean u = new XmppUserBean();
							u.setUserId(chatId);
							u.setNickName(role.getName());
							u.setUserHead(role.getAvatar());
							context.runOnUiThread(new UIRunnable(0, u));
							return;
						}
						//如果是普通自然人或公众号，从Rose要
						String userId = (chatId.split("@")[0]).split("_")[1];
						//公众号
						if (bPubAccount) {
							XmppDao_PubAccount accountDao = new XmppDao_PubAccount(context);
							XmppPublicAccountBean bean = accountDao.getNameById("pub_" + userId);
							accountDao.closeDBService();
							if (bean == null) {
								bean = XmppTool.getInstance().getAccountById(context, userId);
								accountDao.insertPubAccount(bean);
							}

							bean.setId(chatId);
							context.runOnUiThread(new UIRunnable(0, bean, true));
							return;
						}
						//自然人
						XmppUserBean u = XmppTool.getInstance().getUserFromRose(context, userId);
						if (u == null) {
							context.runOnUiThread(new UIRunnable(90, null));
							return;
						}
						u.setUserId(chatId);
						context.runOnUiThread(new UIRunnable(0, u));
					} catch (Exception threadE) {
						threadE.printStackTrace();
						context.runOnUiThread(new UIRunnable(99, null));
					}
				}
			});

			} catch (Exception e) {
				e.printStackTrace();
				context.runOnUiThread(new UIRunnable(99, null));
			}
		}
	}

	class UIRunnable implements Runnable {
		int flag;
		XmppUserBean user;
		XmppPublicAccountBean bean;
		private boolean isPub = false;

		public UIRunnable(int f, XmppUserBean u) {
			flag = f;
			user = u;
		}

		public UIRunnable(int f, XmppPublicAccountBean b, Boolean isPub) {
			flag = f;
			bean = b;
			this.isPub = isPub;
		}
		@Override
		public void run() {
			context.cancleLoading();
			switch (flag) {
				case 0:
					Intent intent;
					if(isPub && bean.getId().startsWith("pub")){
						intent = new Intent(context,CYSecurity_pubinfo.class);
						intent.putExtra("name", bean.getName());
						intent.putExtra("pubNews", false);
					}else{
						intent = new Intent(context, CYSecurity_UserInfo.class);
						intent.putExtra("commentBean", user);
						intent.putExtra("jidFrom", jidFrom);
					}
					context.startActivity(intent);
					break;

				case 90:
					context.mShareFunc.toastDisplay("该成员已退出");
					break;

				case 94:
				case 95:
					context.mShareFunc.toastDisplay(context.getResources().getString(R.string.StrNewRet94));
					break;

				case 99:
					context.mShareFunc.toastDisplay("获取失败，请稍后再试");
					break;

				default:
					break;
			}
		}
	}

	/**
	 * 重发点击
	 */
	class ResendOnclickListener implements OnClickListener {

		private XmppMessageBean bean;
		public ResendOnclickListener(XmppMessageBean msgBean) {
			bean = msgBean;
		}
		@Override
		public void onClick(View v) {
			//是成功状态，则什么也不做
			if (bean == null || bean.getMessageState() == XmppValue.Msg_State_Success
					|| bean.getMessageState() == XmppValue.Msg_State_Banned) {
				return;
			}

			if(!HttpFunc.isNetAvailable(context)){
				DownloadDilaog apkDownload = new DownloadDilaog(context);
				apkDownload.setDlgHandler(ConstantValue.DLGIDNETSETTING, context.getResources().getString(R.string.NoteNetwork));
				return;
			}
			bean.setMessageState(XmppValue.Msg_State_Sending);
			notifyDataSetChanged();

			//构建Xmpp新消息对象
			final Message newMessage = new Message();
			newMessage.setPacketID(bean.getMessageId());
			final String msgId = newMessage.getPacketID();

			try {
				newMessage.addExtension(new DefaultPacketExtension("request", "urn:xmpp:receipts"));
				newMessage.setFrom(jidFrom);

				//ID添加进map，以便服务器回执后更新数据库
				XmppValue.MsgIdMap.put(msgId, bean);

				//先将聊天页更新为成功状态，如果不成功检验线程会给置为失败状态
				XmppDao_ChatList xcd = new XmppDao_ChatList(context);
				xcd.updateStatus(bean.getMessageTo(), jidFrom,
						((CYSecurity_Application)context.getApplication()).getCyjUserBean().getCyjId(), XmppValue.Msg_State_Sending);
				xcd.closeDBService();

				//发送消息
				if (bean.getMessageType() == XmppValue.Msg_Type_Plain) {  //普通消息，直接发送
					newMessage.setBody(bean.getMessageContent(),"");
					if ("chat".equals(which)) {
						newMessage.setType(Type.chat);
						chat.sendMessage(newMessage);
					} else {
						newMessage.setTo(bean.getMessageTo());
						newMessage.setType(Type.groupchat);
						XmppValue.muc.sendMessage(newMessage);
					}
				} else if (bean.getMessageType() == XmppValue.Msg_Type_Image) {  //图片消息，如果还没上传成功，则上传，否则直接发送
					if (bean.getMessageContent() == null) {  //空，则不做处理

					} else if (bean.getMessageContent().startsWith("http://")) {  //以http://开头，说明上传成功了，但是没有发送成功
						DefaultPacketExtension exten = new DefaultPacketExtension("x", "jabber:x:oob");
						exten.setValue("url", bean.getMessageContent());
						newMessage.addExtension(exten);
						if ("chat".equals(which)) {
							chat.sendMessage(newMessage);
						} else {
							newMessage.setTo(bean.getMessageTo());
							newMessage.setType(Type.groupchat);
							XmppValue.muc.sendMessage(newMessage);
						}

					} else {  //否则是上传都没成功

						final String imgPath = bean.getMessageContent();

						//起线程传图片
						ThreadPool.getDownloadPool().execute(new Runnable() {
							@Override
							public void run() {
								try {
//									Bitmap image = BitmapUtil.file2Bitmap(imgPath, 480, 480, true, true);
									String imgUrl = new ShareFunc(context).getUseHttpFunc().uploadImg(
											LaunchUtil.bitmap2Bytes(BitMapCacheUtil.get(imgPath+"0", imgPath, false), 100), context, ConstantValue.CYJUSER_IM_CHAT_IMG);
									if (imgUrl == null) {
										return;
									}

									//复制
									FileUtil.copyChatImg(context, imgPath, imgUrl);

									//将图片的内容改为URL
									XmppDao_Message xmd = new XmppDao_Message(context);
									xmd.updateMsgContent(bean.getMessageId(), imgUrl);
									xmd.closeDBService();

									//发送消息
									DefaultPacketExtension exten = new DefaultPacketExtension("x", "jabber:x:oob");
									exten.setValue("url", imgUrl);
									newMessage.addExtension(exten);
									if ("chat".equals(which)) {
										chat.sendMessage(newMessage);
									} else {
										newMessage.setTo(bean.getMessageTo());
										newMessage.setType(Type.groupchat);
										XmppValue.muc.sendMessage(newMessage);
									}

								} catch (Exception e) {
									e.printStackTrace();
									if (e.getMessage() != null && e.getMessage().contains("Not connected to server")) {
										XmppTool.getInstance().checkLogin(e.getMessage());
									}
								}
							}
						});
					}
				}  else if (bean.getMessageType() == XmppValue.Msg_Type_Voice) {  //语音消息，如果还没上传成功，则上传，否则直接发送
					if (bean.getMessageContent() == null) {  //空，则不做处理

					} else if (bean.getMessageContent().startsWith("http://")) {  //以http://开头，说明上传成功了，但是没有发送成功
						DefaultPacketExtension exten = new DefaultPacketExtension("x", "jabber:x:oob3");
						exten.setValue("url", bean.getMessageContent());
						exten.setValue("mime", XmppValue.Mime_Speex);
						exten.setValue("desc", bean.getVoiceTime()+"");
						exten.setValue("size", bean.getVoiceSize()+"");
						newMessage.addExtension(exten);
						if ("chat".equals(which)) {
							chat.sendMessage(newMessage);
						} else {
							newMessage.setTo(bean.getMessageTo());
							newMessage.setType(Type.groupchat);
							XmppValue.muc.sendMessage(newMessage);
						}

					} else {  //否则是上传都没成功
						//起线程传语音
						ThreadPool.getDownloadPool().execute(new Runnable() {
							@Override
							public void run() {
								try {
									//上传音频文件
									File of = new File(bean.getMessageContent());
									String voiceUrl = SoundMeter.uploadVoice(context, of);
									if (voiceUrl == null) {
										return;
									}

									//上传成功后，文件名变了，所以修改下文件名  
									if (!SoundMeter.rename(of, voiceUrl)) {
										return;
									}

									//更新Bean
									bean.setMessageContent(voiceUrl);

									//将内容改为URL
									XmppDao_Message xmd = new XmppDao_Message(context);
									xmd.updateMsgContent(bean.getMessageId(), voiceUrl);
									xmd.closeDBService();

									//发送消息
									DefaultPacketExtension exten = new DefaultPacketExtension("x", "jabber:x:oob3");
									exten.setValue("url", bean.getMessageContent());
									exten.setValue("mime", XmppValue.Mime_Speex);
									exten.setValue("desc", bean.getVoiceTime()+"");
									exten.setValue("size", bean.getVoiceSize()+"");
									newMessage.addExtension(exten);
									if ("chat".equals(which)) {
										chat.sendMessage(newMessage);
									} else {
										newMessage.setTo(bean.getMessageTo());
										newMessage.setType(Type.groupchat);
										XmppValue.muc.sendMessage(newMessage);
									}

								} catch (Exception e) {
									e.printStackTrace();
									if (e.getMessage() != null && e.getMessage().contains("Not connected to server")) {
										XmppTool.getInstance().checkLogin(e.getMessage());
									}
								}
							}
						});
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
				if (e.getMessage() != null && e.getMessage().contains("Not connected to server")) {
					XmppTool.getInstance().checkLogin(e.getMessage());
					Intent i = new Intent(XmppValue.Type_ReConnection_Broadcast);
					i.putExtra("msg", "show");
					context.sendBroadcast(i);

					//更新UI
					bean.setMessageState(XmppValue.Msg_State_Failed);
					context.runOnUiThread(new Runnable() {
						@Override
						public void run() {
							ShareFunc.toast(context, "发送失败，请重试");
							notifyDataSetChanged();
						}
					});
				}
				try {
					if (chat == null && bean != null) {
						chat = XmppValue.chatManager.createChat(bean.getMessageTo(),null);
					}
					if (XmppValue.muc == null) {
						XmppValue.muc = XmppTool.getInstance().joinMultiUserChat(roomId);
						XmppValue.putMuc(context, XmppValue.muc);
					}
				} catch (Exception ex) {
					ex.printStackTrace();
				}
			}

			//开启检查是否回执了的线程
			ThreadPool.getShortPool().execute(new Runnable() {
				@Override
				public void run() {
					try {
						//睡11秒
						Thread.sleep(11 * 1000);

						XmppMessageBean msgBean = XmppValue.MsgIdMap.get(msgId);
						//已经回执了，则什么也不做
						if (msgBean == null) {
							return;
						}
						//没有回执，更新数据库中为失败状态，并从XmppValue.MsgIdMap中删除
						XmppDao_Message xmd = new XmppDao_Message(context);
						xmd.updateMsgStatus(msgBean.getMessageId(), XmppValue.Msg_State_Failed);
						xmd.closeDBService();

						//聊天列表失败
						XmppDao_ChatList xcd = new XmppDao_ChatList(context);
						xcd.updateStatus(msgBean.getMessageTo(), jidFrom,
								((CYSecurity_Application)context.getApplication()).getCyjUserBean().getCyjId(), XmppValue.Msg_State_Success);
						xcd.closeDBService();
						//发送聊天列表更新的广播
						Intent chatListIntent = new Intent();
						chatListIntent.setAction(XmppValue.Type_ChatList_Broadcast);
						chatListIntent.putExtra("ChatMsg", true);
						context.sendBroadcast(chatListIntent);

						//移除map
						XmppValue.MsgIdMap.remove(msgId);
						//更新UI
						bean.setMessageState(XmppValue.Msg_State_Failed);
						context.runOnUiThread(new Runnable() {
							@Override
							public void run() {
								ShareFunc.toast(context, "发送失败，请重试");
								notifyDataSetChanged();
							}
						});

					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			});
		}

	}

	/**
	 * 长按头像进行@
	 */
	class HeaderOnLongclickListener implements OnLongClickListener{
		private XmppMessageBean messageBean;
		private String id;
		CYSecurity_Application cyApp = (CYSecurity_Application)((Activity)context).getApplication();
		public HeaderOnLongclickListener(XmppMessageBean bean){
			messageBean = bean;
		}
		@Override
		public boolean onLongClick(View v) {
			//将at人的昵称显示在输入框中
			try {
				id = messageBean.getMessageFrom();
				if (id.startsWith("qz") || id.startsWith("gh")) {
					id = id.split("/")[1] + "@" + XmppTool.HOST;
					XmppDao_RoomMember roomMemberDao = new XmppDao_RoomMember(context);
					XmppUserBean user = roomMemberDao.getRoomUser(XmppValue.muc.getRoom(), id, cyApp.getCyjUserBean().getCyjId());
					roomMemberDao.closeDBService();
					if(user == null || StringUtil.isEmpty(user.getNickName())){
						return true;
					}

					Bundle bundle = new Bundle();
					bundle.putString("userid", id);
					bundle.putString("nickname", user.getNickName());
					bundle.putString("pd", "0");

					android.os.Message msg = new android.os.Message();
					msg.what = 10;
					msg.setData(bundle);
					handler.sendMessage(msg);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			return true;
		}
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
