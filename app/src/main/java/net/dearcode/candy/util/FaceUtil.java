package net.dearcode.candy.util;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.text.style.ImageSpan;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.ViewFlipper;

import net.dearcode.candy.R;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FaceUtil {

//	static String qqFaceMatch = "^f00[1-9]|f01[0-9]|f02[0-7]$";

	//表情图片缩放比例
	static int scaleInt = 45;

	/**
	 * 对spanableString进行正则判断，如果符合要求，则以表情图片代替
	 * @param context
	 * @param spannableString
	 * @param patten
	 * @param start
	 * @throws SecurityException
	 * @throws NoSuchFieldException
	 * @throws NumberFormatException
	 * @throws IllegalArgumentException
	 * @throws IllegalAccessException
	 */
	public static void dealExpression(Context context,SpannableString spannableString, Pattern patten, int start) throws SecurityException, NoSuchFieldException, NumberFormatException, IllegalArgumentException, IllegalAccessException {

		scaleInt = getFaceSize(context);

		Matcher matcher = patten.matcher(spannableString);
		while (matcher.find()) {
			String key = matcher.group();
			if (matcher.start() < start) {
				continue;
			}
			//获取匹配上的图片的名字
			String value = FaceValue.faceMapContent.get(key);
			if (value == null || "".equals(value)) {
				continue;
			}

			Field field = R.drawable.class.getDeclaredField(value);
			int resId = Integer.parseInt(field.get(null).toString());       //通过上面匹配得到的字符串来生成图片资源id
			if (resId != 0) {
				Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(), resId);
				bitmap = Bitmap.createScaledBitmap(bitmap, scaleInt, scaleInt, false);
//                //自己的表情图片前27个，则缩放
//                if (key.matches(qqFaceMatch)) {
//                	//缩放表情图片
//                    bitmap = Bitmap.createScaledBitmap(bitmap, 55, 55, false);
//                }
				ImageSpan imageSpan = new ImageSpan(context, bitmap);                //通过图片资源id来得到bitmap，用一个ImageSpan来包装
				int end = matcher.start() + key.length();                   //计算该图片名字的长度，也就是要替换的字符串的长度
				spannableString.setSpan(imageSpan, matcher.start(), end, Spannable.SPAN_INCLUSIVE_EXCLUSIVE);   //将该图片替换字符串中规定的位置中
				if (end < spannableString.length()) {                        //如果整个字符串还未验证完，则继续。。
					dealExpression(context,spannableString,  patten, end);
				}
				break;
			}
		}
	}

	/**
	 * 得到一个SpanableString对象，通过传入的字符串,并进行正则判断
	 * @param context
	 * @param text
	 * @return
	 */
	public static SpannableString getExpressionString(Context context,String text,String zhengze){
		SpannableString spannableString = new SpannableString(text);
		Pattern sinaPatten = Pattern.compile(zhengze, Pattern.CASE_INSENSITIVE);        //通过传入的正则表达式来生成一个pattern
		try {
			dealExpression(context,spannableString, sinaPatten, 0);
		} catch (Exception e) {
			Log.e("dealExpression", e.getMessage());
		}
		return spannableString;
	}


	public static void replaceTextToFace(Context context, String text, TextView et, boolean isAddReply) {
		if (text == null || "".equals(text.trim()) || et == null) {
			return;
		}

		if (isAddReply) {
			text = "回复：" + text;
		}
		//表情正则 #1-#100、#v0101-#v0136/#v0201-#v0236/#v0301-#v0333/#v0401-#v0439
		String zhengze = "#(100|[1-9]\\d?)|#(v0[1-4]0[1-9]|v0[1-4][1-3][0-9])";  //正则Vip是0 1/2/3/4 01-0 1/2/3/4 39
		SpannableString spannableString = getExpressionString(context, text, zhengze);
		if(text.contains("[草稿]")){
			spannableString.setSpan(new ForegroundColorSpan(context.getResources().getColor(R.color.red)), 0, 4, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
		}
		if(text.contains("[有人@我]")){
			spannableString.setSpan(new ForegroundColorSpan(context.getResources().getColor(R.color.red)), 0, 6, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
		}
		if(FaceUtil.isPhoneNum(text)){
			spannableString.setSpan(new ForegroundColorSpan(context.getResources().getColor(R.color.graytext)), 0, text.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
		}
		if(FaceUtil.isMail(text)){
			spannableString.setSpan(new ForegroundColorSpan(context.getResources().getColor(R.color.graytext)), 0, text.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
		}
		if(spannableString != null) {
			et.setText(spannableString);
			et.setVisibility(View.VISIBLE);
		}
	}


	//表情列表
	public static List<Map<String,Object>> faceItemList1 = new ArrayList<Map<String,Object>>();
	public static List<Map<String,Object>> faceItemList2 = new ArrayList<Map<String,Object>>();
	public static List<Map<String,Object>> faceItemList3 = new ArrayList<Map<String,Object>>();
	public static List<Map<String,Object>> faceItemList4 = new ArrayList<Map<String,Object>>();
	public static List<Map<String,Object>> faceItemList5 = new ArrayList<Map<String,Object>>();
	static {
		//生成100个表情的id，封装
		for(int i = 1; i <= 100; i++){
			try {
				Map<String,Object> faceItemMap = new HashMap<String,Object>();

				if(i<10){  //第一页（前10个，对应f001-f009）
					String imgPath = "f00" + i;
					Field field = R.drawable.class.getDeclaredField(imgPath);
					int resourceId = Integer.parseInt(field.get(null).toString());

					faceItemMap.put("imageId", resourceId);
					faceItemMap.put("imagePath", imgPath);
					faceItemList1.add(faceItemMap);
				} else if (i <= 20) {  //第一页 (10-29，对应f010-f029)
					String imgPath = "f0" + i;
					Field field = R.drawable.class.getDeclaredField(imgPath);
					int resourceId = Integer.parseInt(field.get(null).toString());

					faceItemMap.put("imageId", resourceId);
					faceItemMap.put("imagePath", imgPath);
					faceItemList1.add(faceItemMap);
				} else if (i <= 40) {  //第二页 (30-58，对应f030-f058)
					String imgPath = "f0" + i;
					Field field = R.drawable.class.getDeclaredField(imgPath);
					int resourceId = Integer.parseInt(field.get(null).toString());

					faceItemMap.put("imageId", resourceId);
					faceItemMap.put("imagePath", imgPath);
					faceItemList2.add(faceItemMap);
				} else if (i <= 60) {  //第三页(58-87, 对应f058-f087)
					String imgPath = "f0" + i;
					Field field = R.drawable.class.getDeclaredField(imgPath);
					int resourceId = Integer.parseInt(field.get(null).toString());

					faceItemMap.put("imageId", resourceId);
					faceItemMap.put("imagePath", imgPath);
					faceItemList3.add(faceItemMap);
				} else if (i <= 80) {  //第三页(58-87, 对应f058-f087)
					String imgPath = "f0" + i;
					Field field = R.drawable.class.getDeclaredField(imgPath);
					int resourceId = Integer.parseInt(field.get(null).toString());

					faceItemMap.put("imageId", resourceId);
					faceItemMap.put("imagePath", imgPath);
					faceItemList4.add(faceItemMap);
				} else {  //第四页（88-100，对应f088-f100）
					String imgPath;
					if (i == 100) {
						imgPath = "f" + i;
					} else {
						imgPath = "f0" + i;
					}
					Field field = R.drawable.class.getDeclaredField(imgPath);
					int resourceId = Integer.parseInt(field.get(null).toString());

					faceItemMap.put("imageId", resourceId);
					faceItemMap.put("imagePath", imgPath);
					faceItemList5.add(faceItemMap);
				}

			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	//表情GridView的item点击事件
	public static class MyFaceItemClickListener implements OnItemClickListener {

		Activity activity;
		EditText et;
		List<Map<String,Object>> faceItemList;

		public MyFaceItemClickListener(Activity activity, EditText et, List<Map<String,Object>> faceItemList) {
			this.activity = activity;
			this.et = et;
			this.faceItemList = faceItemList;
		}

		public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {

//			LogBean.addTongji(activity, "clickIMEmoji");  //使用表情

			scaleInt = getFaceSize(activity);

			int imgId = (Integer)(faceItemList.get(arg2).get("imageId"));
			String imgPath=  (String)(faceItemList.get(arg2).get("imagePath"));

			if (imgPath == null) {
				return;
			}

			//表情key-value如果没有，则直接返回
			if (FaceValue.faceMap.get(imgPath) == null) {
				return;
			}

			Bitmap bitmap = BitmapFactory.decodeResource(activity.getResources(), imgId);

			//缩放表情图片
			bitmap = Bitmap.createScaledBitmap(bitmap, scaleInt, scaleInt, false);

			ImageSpan imageSpan = new ImageSpan(activity, bitmap);
			String str = FaceValue.faceMap.get(imgPath);
			SpannableString spannableString = new SpannableString(str);
			spannableString.setSpan(imageSpan, 0, str.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

			int index = et.getSelectionStart();// 获取光标所在位置
			Editable edit = et.getEditableText();// 获取EditText的文字
			if (index < 0 || index >= edit.length()) {
				edit.append(spannableString);
			} else {
				edit.insert(index, spannableString);// 光标所在位置插入文字
			}

//			et.append(spannableString);
		}

	}

	//删除文字
	public static class MyBackListener implements View.OnClickListener {
		Activity activity;
		EditText et;

		public MyBackListener(Activity activity, EditText et) {
			this.activity = activity;
			this.et = et;
		}

		public void onClick(View v) {
			int index = et.getSelectionStart();
			if (index == 0) {
				return;
			}
			Editable editable = et.getText();
			String str = et.getText().toString();
			//开头到光标处的字符串
			str = str.substring(0, index);
			//字符串如果以表情结尾（正则有个$），则删除时删除表情的长度，否则删除一个字符
			Pattern pattern = Pattern.compile("#(100|[1-9]\\d?)$");
			Matcher matcher = pattern.matcher(str);
			if(matcher.find()){
				String s = matcher.group();
				editable.delete(index-s.length(), index);
			} else{
				editable.delete(index-1, index);
			}

//			//获取光标处前4个字符，如果是表情，删除4个位置；否则删除一个位置
//			String str = et.getText().toString();
//			String tempStr = str.substring(index-4, index);
//			if (FaceValue.faceMapContent.get(tempStr) != null) {
//				editable.delete(index-4, index);
//			} else {
//				editable.delete(index-1, index);
//			}
		}
	}

	//删除文字，自己控制，支持长按
	public static class MyBackTouchListener implements View.OnTouchListener {

		private Context context;
		private EditText et;
		private boolean touch;

		private Thread delThread;
		private Runnable delRunnable = new Runnable() {
			@Override
			public void run() {
				try {
					while (touch) {
						int index = et.getSelectionStart();
						if (index == 0) {
							break;
						}
						handler.sendEmptyMessage(0);
						Thread.sleep(200);
					}

				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		};

		public MyBackTouchListener(Context c, EditText e) {
			context = c;
			et = e;
		}

		@Override
		public boolean onTouch(View v, MotionEvent event) {
			switch(event.getAction()){
				case MotionEvent.ACTION_DOWN:
					v.setBackgroundColor(context.getResources().getColor(R.color.new_color_gray));
					touch = true;
					delThread = new Thread(delRunnable);
					delThread.start();
					break;

				case MotionEvent.ACTION_UP:
					v.setBackgroundColor(Color.TRANSPARENT);
					touch = false;
					break;
			}
			return true;
		}

		private Handler handler = new Handler(){

			@Override
			public void handleMessage(Message msg) {
				int index = et.getSelectionStart();
				if (index == 0) {
					return;
				}
				Editable editable = et.getText();
				String str = et.getText().toString();
				//开头到光标处的字符串
				str = str.substring(0, index);
				//字符串如果以表情结尾（正则有个$），则删除时删除表情的长度，否则删除一个字符
				Pattern pattern = Pattern.compile("#(100|[1-9]\\d?)$");
				Matcher matcher = pattern.matcher(str);
				if(matcher.find()){
					String s = matcher.group();
					editable.delete(index-s.length(), index);
				} else{
					editable.delete(index-1, index);
				}
			}
		};
	}

	//表情滑动监听
	public static class MyTouchListener implements View.OnTouchListener {

		Context context;
		ViewFlipper viewFlipper=null;
		float touchUpX;
		boolean moveable;

		public MyTouchListener(Context context, ViewFlipper viewFlipper, float touchUpX, boolean moveable) {
			super();
			this.viewFlipper = viewFlipper;
			this.touchUpX = touchUpX;
			this.moveable = moveable;
			this.context = context;
		}

		public boolean onTouch(View v, MotionEvent event) {

			switch(event.getAction()){
				case MotionEvent.ACTION_DOWN:
					touchUpX = event.getX();
					moveable = true;
					break;
				case MotionEvent.ACTION_MOVE:
					if(moveable){
						if(event.getX()-touchUpX>60){
							moveable=false;
							int childIndex=viewFlipper.getDisplayedChild();
							/**
							 * 这里的这个if检测是防止表情列表循环滑动
							 */
							if(childIndex>0){
								viewFlipper.setInAnimation(AnimationUtils.loadAnimation(context, R.anim.push_left_in));
								viewFlipper.setOutAnimation(AnimationUtils.loadAnimation(context, R.anim.push_right_out));
								viewFlipper.showPrevious();
							}
						}
						else if(event.getX()-touchUpX<-60){
							moveable=false;
							int childIndex=viewFlipper.getDisplayedChild();
							/**
							 * 这里的这个if检测是防止表情列表循环滑动
							 */
							if(childIndex!=4){
								viewFlipper.setInAnimation(AnimationUtils.loadAnimation(context, R.anim.push_right_in));
								viewFlipper.setOutAnimation(AnimationUtils.loadAnimation(context, R.anim.push_left_out));
								viewFlipper.showNext();
							}
						}
					}
					break;
				case MotionEvent.ACTION_UP:
					moveable=true;
					break;
				default:
					break;
			}
			return false;
		}
	}


	public static int getFaceSize(Context c) {
		return c.getResources().getDimensionPixelSize(R.dimen.dip_22);
	}

	//判断聊天内容是否为特定的字符
	public static Boolean isPhoneNum(String phoneNum){
		Pattern p = Pattern.compile("^((13[0-9])|(15[^4,\\D])|(18[0-9]))\\d{8}$");
		Matcher m = p.matcher(phoneNum);
		return m.matches();
	}

	//判断聊天内容是否为邮箱
	public static Boolean isMail(String emailNum){
		Pattern p =  Pattern.compile("\\w+([-+.]\\w+)*@\\w+([-.]\\w+)*\\.\\w+([-.]\\w+)*");
		Matcher m = p.matcher(emailNum);
		return m.matches();
	}
}  