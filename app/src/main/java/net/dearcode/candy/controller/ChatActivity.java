package net.dearcode.candy.controller;

import android.content.Context;
import android.media.AudioManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.view.ViewPager;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AbsListView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import net.dearcode.candy.R;
import net.dearcode.candy.controller.base.BaseActivity;
import net.dearcode.candy.controller.component.RPC;
import net.dearcode.candy.model.Event;
import net.dearcode.candy.model.Message;
import net.dearcode.candy.model.Relation;
import net.dearcode.candy.model.ServiceResponse;
import net.dearcode.candy.selfview.PullToRefreshListView;
import net.dearcode.candy.selfview.adapter.ChatFaceAdapter;
import net.dearcode.candy.util.BitMapCacheUtil;
import net.dearcode.candy.util.Common;
import net.dearcode.candy.util.FaceUtil;
import net.dearcode.candy.util.PageManager;
import net.dearcode.candy.util.ThreadPool;

import java.lang.ref.WeakReference;
import java.util.Date;
import java.util.List;

/**
 * Created by lujinfei on 2016/10/8.
 */
public class ChatActivity extends BaseActivity {

    public AudioManager mAudioManager = null;
    protected ViewPager vpFace;
    protected ImageView ivVolumn;  //展示声音音量
    protected View voicePop;
    private Button btnSend;
    protected EditText etReply;
    protected ImageView ivImg;
    private PullToRefreshListView lvChat;
    protected ImageView ivFace;
    protected View ivFaceDelete;
    private PageManager pm;
    //未加载列表时转圈
    protected ProgressBar pvLoading;

    private MyHandler handler;

    private long mUid;
    private long mGid;
    private boolean mIsGroup = false;
    private boolean isBottom = true;
    private int unReadNum = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle b = getIntent().getExtras();
        mIsGroup = b.getBoolean("isGroup");
        if(!mIsGroup) {
            mUid = b.getLong("uid");
        }else {
            mGid = b.getLong("gid");
        }

        mAudioManager = (AudioManager) getSystemService(AUDIO_SERVICE);
        setContentView(R.layout.layout_chat);


        initView();
    }

    private void initView() {

        pm = new PageManager();

        pvLoading = (ProgressBar)findViewById(R.id.pb_loading);

        voicePop = findViewById(R.id.voice_pop);
        ivVolumn = (ImageView)voicePop.findViewById(R.id.iv_voiceSize);

        lvChat = (PullToRefreshListView)findViewById(R.id.lv_chat);
        lvChat.setOnTouchListener(touchListener);

        btnSend = (Button)findViewById(R.id.btn_send);
        btnSend.setOnClickListener(this);

        ivImg = (ImageView)findViewById(R.id.iv_img);
        ivImg.setOnClickListener(this);

        ivFace = (ImageView)findViewById(R.id.iv_face);
        ivFace.setOnClickListener(this);

        etReply = (EditText)findViewById(R.id.et_reply);
        etReply.requestFocus();
        etReply.setOnClickListener(this);
        etReply.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.toString().trim().length() != 0) {
                    btnSend.setVisibility(View.VISIBLE);
                    ivImg.setVisibility(View.GONE);
                } else {
                    btnSend.setVisibility(View.GONE);
                    ivImg.setVisibility(View.VISIBLE);
                }
            }
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }
            @Override
            public void afterTextChanged(Editable s) { }
        });

        vpFace = (ViewPager)findViewById(R.id.vp_face);
        vpFace.setAdapter(new ChatFaceAdapter(this, etReply));

        ivFaceDelete = findViewById(R.id.iv_face_delete);
        ivFaceDelete.setOnTouchListener(new FaceUtil.MyBackTouchListener(this, etReply));


        // 会话列表的初始化
        lvChat = (PullToRefreshListView)findViewById(R.id.lv_chat);
        lvChat.setOnRefreshListener(new PullToRefreshListView.OnRefreshListener() {
            @Override
            public void onRefresh() {
                initData(false);
            }
        });
        lvChat.setOnScrollListener(new AbsListView.OnScrollListener() {   //监听当前是否在最底部
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem,
                                 int visibleItemCount, int totalItemCount) {
                //到底部了，隐藏未读消息提示
                if (firstVisibleItem + visibleItemCount >= totalItemCount) {
                    isBottom = true;
                    unReadNum = 0;
//                    tvUnread.setVisibility(View.GONE);
                    return;
                }
                //未到达底部，随着向下滚动，显示未读消息数量
                isBottom = false;
                if ((firstVisibleItem+visibleItemCount)>(totalItemCount-unReadNum)){
                    unReadNum = (unReadNum-((firstVisibleItem+visibleItemCount)-(totalItemCount-unReadNum)));
//                    tvUnread.setText(unReadNum+"");
                }
            }
        });
        lvChat.setOnTouchListener(touchListener);
    }


    // 聊天数据的初始化
    private void initData(final boolean isFirst) {
        // 获取输入框内容
        String retext = etReply.getText().toString();

        ThreadPool.getShortPool().execute(new Runnable() {
            @Override
            public void run() {
                try {

//                    if(isFirst) {   // 如果是第一次进来则查下信息
//                        //对方信息
//                        user = userDao.getOne(jid, jidFrom, cyApp.getCyjUserBean().getCyjId());
//                        if (user == null) {
//                            Thread thread = new Thread(new Runnable() {
//
//                                @Override
//                                public void run() {
//                                    try {
//                                        if (jidFrom.startsWith("cyj_")) {
//                                            String uid = XmppValue.CyjUser.getCyjId();
//                                            String dPwd = ZZBUtil.getDynamicPwd(mActivity, XmppValue.CyjUser.getUserId());
//                                            String token = XmppValue.CyjUser.getToken();
//
//                                            XmppTool.getInstance().getFriendList(mActivity, uid, dPwd, token, true);
//                                            user = userDao.getOne(jid, jidFrom, cyApp.getCyjUserBean().getCyjId());
//                                            handler.sendEmptyMessage(6);
//                                        } else {
//                                            XmppRoleBean role = XmppTool.getInstance().getRole(mActivity, jid, XmppValue.jid).get(0);
//                                            if (role != null) {
//                                                user = new XmppUserBean();
//                                                user.setNickName(role.getName());
//                                                user.setUserHead(role.getAvatar());
//                                            }
//                                        }
//                                    } catch (Exception e) {
//                                        e.printStackTrace();
//                                    }
//                                }
//                            });
//                            thread.start();
//                        }
//                        handler.sendEmptyMessage(6);
//                    }

                    //将错误的状态设置下，没有发送出去的消息
//                    messageDao.updateMsgSendingToFail();

                    //首次来，设置好页码，是否有下页等
                    if (isFirst) {
                        long historyTime = new Date().getTime();
                        pm.setTotalCount(CustomeApplication.db.getUserMessageCount(mUid));
                        pm.setNowPage("1");
                        int pageSize = (int)Math.ceil((double)pm.getTotalCount()/20);
                        pm.setTotalPage(pageSize);
                        if (pageSize > 1) {
                            pm.setHasNext(true);
                        } else {
                            pm.setHasNext(false);
                        }
                    }

                    //没有下页了
                    if (!isFirst && !pm.isHasNext()) {
                        handler.sendEmptyMessage(2);
                        return;
                    }

                    //获取数据
                    List<Message> tmpList = CustomeApplication.db.loadUserMessage(mUid);
                    if (tmpList == null) {
                        handler.sendEmptyMessage(3);
                        return;
                    }

//                    //清空红点
//                    if(isFirst) {
//                        chatListDao.setUnread(jid, jidFrom, cyApp.getCyjUserBean().getCyjId(), 0);
//                    }

                    //记录ListView的位置（即拿回来数据的数量）
//                    lvPosition = tmpList.size();
//                    for (Message bean : tmpList) {
//                        Message userBean = new Message();
//                        String nickName;
//                        String userHead;
//                        if(bean.isFrom() && jid.startsWith("pub_")){
//                            nickName = accountBean.getName();
//                            userHead = accountBean.getUserHead();
//                        }else{
//                            if (bean.isFrom()) {
//                                if (user != null) {
//                                    nickName = user.isHasRemark() ? user.getRemark() : user.getNickName();
//                                    userHead = user.getUserHead();
//                                }else{
//                                    nickName = username;
//                                    userHead = userhead;
//                                }
//                            } else {
//                                if (myRole == null) {
//                                    nickName = cyApp.getCyjUserInfoBean().getNickName();
//                                } else {
//                                    nickName = myRole.getName();
//                                }
//                                userHead = myAvatar;
//                            }
//                        }
//                        userBean.setNickName(nickName);
//                        userBean.setUserHead(userHead);
//                        bean.setUserBean(userBean);
//                        dataList.add(0, bean);
//                    }
                    handler.obtainMessage(1, isFirst).sendToTarget();

                } catch (Exception e) {
                    e.printStackTrace();
                    handler.sendEmptyMessage(3);
                }
            }
        });

        // 草稿的处理
//        etReply.setText(mCaogao);
//        if(!StringUtil.isEmpty(mCaogao)){
//            ShareFunc func = new ShareFunc(mActivity);
//            func.softKeyboard(ShareFunc.TIME_200);
//            etReply.setSelection(mCaogao.length());
//        }
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()) {
            case R.id.btn_send: // 普通发送按钮
                final String msg = Common.GetString(etReply.getText());
                ServiceResponse sr = new RPC() {
                    @Override
                    public ServiceResponse getResponse() throws Exception {
                        return CustomeApplication.getService().sendMessage(mGid, mUid, msg);
                    }
                }.Call();
//                if (sr.hasError()) {
//                    Log.e(Common.LOG_TAG, "sendMessage error:" + sr.getError());
//                    Toast.makeText(ChatActivity.this, sr.getError(), Toast.LENGTH_LONG).show();
//                    return;
//                }
//
//                if (mIsGroup) {
//                    CustomeApplication.db.saveGroupMessage(sr.getId(), group, Base.account.getID(), 0, msg);
//                    CustomeApplication.db.saveSession(group, true, msg);
//
//                } else {
//                    CustomeApplication.db.saveUserMessage(sr.getId(), user, Base.account.getID(), msg);
//                    CustomeApplication.db.saveSession(user, false, msg);
//                }
//                Log.e(Common.LOG_TAG, "new Message id:" + sr.getId() + " group:" + group + " user:" + user + " msg:" + msg);
//                etReply.setText("");
//                msgs.add(new Message(Event.None, Relation.DEL, sr.getId(), group, Base.account.getID(), user, msg));
//                rvTalk.getAdapter().notifyDataSetChanged();
//                rvTalk.smoothScrollToPosition(msgs.size() - 1);
                break;
            case R.id.iv_face:
                showFace();
                break;
            default:
                super.onClick(v);
        }
    }

    //点击空白处，隐藏软键盘
    protected View.OnTouchListener touchListener = new View.OnTouchListener() {

        @Override
        public boolean onTouch(View view, MotionEvent event) {
            if(event.getAction() == MotionEvent.ACTION_DOWN){
                View v = getCurrentFocus();
                if (isShouldHideInput(v, event)) {
                    hideKeyboardFocus();
                    vpFace.setVisibility(View.GONE);
                }
            }
            return false;
        }

    };
    private boolean isShouldHideInput(View v, MotionEvent event) {
        if (v != null && (v instanceof EditText)) {
            int[] l = { 0, 0 };
            v.getLocationInWindow(l);
            int left = l[0], top = l[1], bottom = top + v.getHeight(), right = left
                    + v.getWidth();
            if (event.getX() > left && event.getX() < right
                    && event.getY() > top && event.getY() < bottom) {
                // 点击EditText的事件，忽略它。
                return false;
            } else {
                return true;
            }
        }
        // 如果焦点不是EditText则忽略，这个发生在视图刚绘制完，第一个焦点不在EditView上，和用户用轨迹球选择其他的焦点
        return false;
    }

    public AudioManager.OnAudioFocusChangeListener afChangeListener = new AudioManager.OnAudioFocusChangeListener() {

        public void onAudioFocusChange(int focusChange) {

        }
    };

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        AudioManager mAudioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        int currentVolume = mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
        switch (keyCode) {
            case KeyEvent.KEYCODE_BACK: // 返回键
                //activityBack();
                finish();
                return true;
            case KeyEvent.KEYCODE_VOLUME_UP:// 音量增大
                mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, currentVolume+1, 1);
                break;
            case KeyEvent.KEYCODE_VOLUME_DOWN:// 音量减小
                mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, currentVolume-1, 1);
                break;
        }
        return super.onKeyDown(keyCode, event);
    }

    public void requestAudioFocus() {
        mAudioManager.requestAudioFocus(afChangeListener,
                AudioManager.STREAM_MUSIC, // Use the music stream.
                AudioManager.AUDIOFOCUS_GAIN_TRANSIENT);
    }

    public void abandonAudioFocus() {
        mAudioManager.abandonAudioFocus(afChangeListener);
    }

    //展示表情框
    protected boolean isFaceShow = false;
    protected void showFace() {
        //隐藏添加图片
//        isImgShow = false;
//        llAddImage.setVisibility(View.GONE);

        //隐藏语音相关，展示打字相关
//        whichText = 0;
//        ivWhich.setImageBitmap(BitMapCacheUtil.get(this, R.drawable.chat_add_voice));
        etReply.setVisibility(View.VISIBLE);
        etReply.requestFocus();
//        tvVoice.setVisibility(View.GONE);
//        ivWhich.setImageBitmap(BitMapCacheUtil.get(mActivity, R.drawable.chat_add_voice));

        isFaceShow = !isFaceShow;
        if (isFaceShow) {
            ivFace.setImageBitmap(BitMapCacheUtil.get(this, R.drawable.chat_add_keyboard));
            vpFace.setVisibility(View.VISIBLE);
            hideKeyboardFocus();  //隐藏键盘
        } else {
            ivFace.setImageBitmap(BitMapCacheUtil.get(this, R.drawable.chat_add_face));
            softKeyboard(200);
            vpFace.setVisibility(View.GONE);
        }
    }

    static class MyHandler extends Handler {
        WeakReference<ChatActivity> wr;
        public MyHandler(ChatActivity a) {
            wr = new WeakReference<ChatActivity>(a);
        }
        @Override
        public void handleMessage(android.os.Message msg) {
            ChatActivity activity = wr.get();
            if (activity == null) {
                return;
            }
            activity.handleMsg(msg);
        }

    }

    public void handleMsg(android.os.Message msg) {
//        cancleLoading();
        lvChat.onRefreshComplete();
        switch (msg.what) {
            case 1:
                pvLoading.setVisibility(View.GONE);
                boolean isFirst = (Boolean)msg.obj;
                //是首次来，滚动到最后，否则滚动到指定位置
                if (isFirst) {
                    adapter = new XmppChatAdapter(this, dataList, "chat", chat, mSensor, jidFrom, "", handler);
                    lvChat.setAdapter(adapter);
                    lvChat.setSelection(lvChat.getCount() - 1);
                } else {
                    adapter.notifyDataSetChanged();
                    lvChat.setSelection(lvPosition);
                }
                if(StringUtil.isEmpty(etReply.getText().toString())){
                    etReply.setText(retext);
                    etReply.setSelection(retext.length());
                }
                //apapter中语音播放时那个动态小喇叭要用
                adapter.nowPosition += lvPosition;
                break;

            case 2:
                if(StringUtil.isEmpty(etReply.getText().toString())){
                    etReply.setText(retext);
                    etReply.setSelection(retext.length());
                }
                toastDisplay("已加载全部");
                break;

            case 3:
                if(StringUtil.isEmpty(etReply.getText().toString())){
                    etReply.setText(retext);
                    etReply.setSelection(retext.length());
                }
                pvLoading.setVisibility(View.GONE);
                toastDisplay("获取聊天记录失败，请稍后再试");
                break;
            case 4:
                toastDisplay("操作失败，请重试");
                break;
            case 5:
                setResult(2);
                toastDisplay(toMsg);
                finish();
                break;
            case ConstantValue.MSGIDTOAST:
                String toast = (String) msg.obj;
                if(StringUtil.isNotEmpty(toast)){
                    toastDisplay(toast);
                }
                break;
            case ConstantValue.ADDFRIEND:
                if ("ok".equals(msg.obj)) {
                    rlFriendNote.setVisibility(View.GONE);
                    toastDisplay("好友添加成功！");
                }
                break;
            case ConstantValue.FORBIDDENMESSAGE:
                setForbiddenAndDis();
                setFriendNote();
                toastDisplay(getResources().getString(R.string.chat_manage_forbidden));
                break;
            case 9:
                if (adapter != null) {
                    adapter.notifyDataSetChanged();
                }
                break;

            default:
                break;
        }
    }
}
