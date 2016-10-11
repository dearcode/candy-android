package net.dearcode.candy.controller.base;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Toast;

import java.util.Timer;
import java.util.TimerTask;

/**
 * 所有Activity的基类
 * @author lxq_x
 *
 */
public class BaseActivity extends AppCompatActivity implements
		View.OnClickListener, AdapterView.OnItemClickListener {

	private BaseController mBaseController;

	protected String mActivityName = "基础类";

	public BaseActivity() {
		super();
		mBaseController = new BaseController();
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mBaseController.onCreate(this);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		mBaseController.onDestroy(this);
	}

	@Override
	protected void onStart() {
		super.onStart();
		mBaseController.mIsTop = true;
	}

	@Override
	protected void onStop() {
		super.onStop();
		mBaseController.mIsTop = false;
	}

	@Override
	protected void onResume() {
		super.onResume();
		mBaseController.onResume(this);
	}

	@Override
	protected void onPause() {
		super.onPause();
		mBaseController.onPause(this);
	}

	public void onEventMainThread(Message message){
		mBaseController.onEventMainThread(this, message);
	}

	@Override
	public void onClick(View v) {

	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

	}

	public void hideKeyboardFocus() {
		InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
		if(getCurrentFocus() != null && getCurrentFocus().getWindowToken() != null){
			imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
		}
	}

	public void softKeyboard(int nTime){
		Timer timer = new Timer();
		timer.schedule(new TimerTask() {

			@Override
			public void run() {
				InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
				imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
			}
		}, nTime);
	}

	public void toastDisplay(String abc) {
		Toast.makeText(getApplicationContext(), abc, Toast.LENGTH_SHORT).show();
	}

}
