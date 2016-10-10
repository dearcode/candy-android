package net.dearcode.candy.controller;

import android.content.Context;
import android.media.AudioManager;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import net.dearcode.candy.R;
import net.dearcode.candy.controller.base.BaseActivity;
import net.dearcode.candy.selfview.PullToRefreshListView;
import net.dearcode.candy.selfview.adapter.ChatFaceAdapter;

/**
 * Created by lujinfei on 2016/10/8.
 */
public class ChatActivity extends BaseActivity implements View.OnClickListener {

    public AudioManager mAudioManager = null;
    protected ViewPager vpFace;
    protected ImageView ivVolumn;  //展示声音音量
    protected View voicePop;
    private Button btnSend;
    protected EditText etReply;
    protected ImageView ivImg;
    private PullToRefreshListView lvChat;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAudioManager = (AudioManager) getSystemService(AUDIO_SERVICE);
        setContentView(R.layout.layout_chat);


        initView();
    }

    private void initView() {
        voicePop = findViewById(R.id.voice_pop);
        ivVolumn = (ImageView)voicePop.findViewById(R.id.iv_voiceSize);

        lvChat = (PullToRefreshListView)findViewById(R.id.lv_chat);
        lvChat.setOnTouchListener(touchListener);

        btnSend = (Button)findViewById(R.id.send);
        btnSend.setOnClickListener(this);

        ivImg = (ImageView)findViewById(R.id.iv_img);
        ivImg.setOnClickListener(this);

        vpFace = (ViewPager)findViewById(R.id.vp_face);
        vpFace.setAdapter(new ChatFaceAdapter(this, etReply));

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
}
