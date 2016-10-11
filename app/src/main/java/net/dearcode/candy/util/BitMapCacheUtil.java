package net.dearcode.candy.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.SparseArray;


import java.io.InputStream;
import java.lang.ref.SoftReference;
import java.util.HashMap;
import java.util.Map;

public class BitMapCacheUtil {

	public static String titleLeftKey = "titleLeft";

	static Map<String, SoftReference<Bitmap>> imageCache = new HashMap<String, SoftReference<Bitmap>>();
	static SparseArray<SoftReference<Bitmap>> drawCache = new SparseArray<SoftReference<Bitmap>>();

	public static Bitmap get(String key, String imgPath){
		return get(key+"1", imgPath, true);
	}

	//文件路劲
	public static Bitmap get(String key, String imgPath, Boolean bCompress) {
		Bitmap bitmap = null;
		try {
			//从缓存中取软引用的Bitmap对象
			SoftReference<Bitmap> bitmapcache = imageCache.get(key);
			if (bitmapcache == null) {  //不存在，放置
				add(key, imgPath, bCompress);
				bitmapcache = imageCache.get(key);
			}
			//取出Bitmap对象，如果由于内存不足Bitmap被回收，将取得空
			bitmap = bitmapcache.get();
			if (bitmap == null) {  //不存在，放置
				add(key, imgPath, bCompress);
				bitmapcache = imageCache.get(key);
				bitmap = bitmapcache.get();
			}
		} catch(Exception e) {
			e.printStackTrace();
			bitmap = null;
		}

		return bitmap;
	}

	private static void add(String key, String imgPath, Boolean bCompress) {
		try {
//			InputStream is = new FileInputStream(new File(imgPath));
//			//获取强引用的Bitmap对象
//			Bitmap tempBitMap = BitmapFactory.decodeStream(is);

			//获取强引用的Bitmap对象
			Bitmap tempBitMap = BitmapUtil.file2Bitmap(imgPath, true, bCompress);
			//软引用的Bitmap对象
			SoftReference<Bitmap> bitmapcache = new SoftReference<Bitmap>(tempBitMap);
			//添加该对象到Map中使其缓存
			imageCache.put(key, bitmapcache);
//			is.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static  void remove(String key){
		imageCache.remove(key);
	}

	//Drawable资源
	public static Bitmap get(Context context, int id) {
		Bitmap bitmap = null;
		try {
			//从缓存中取软引用的Bitmap对象
			SoftReference<Bitmap> bitmapcache = drawCache.get(id);
			if (bitmapcache == null) {  //不存在，放置
				add(context, id);
				bitmapcache = drawCache.get(id);
			}

			//取出Bitmap对象，如果由于内存不足Bitmap被回收，将取得空
			bitmap = bitmapcache.get();
			if (bitmap == null) {  //不存在，放置
				add(context, id);
				bitmapcache = drawCache.get(id);
				bitmap = bitmapcache.get();
			}
		} catch(Exception e) {
			e.printStackTrace();
			bitmap = null;
		}
		return bitmap;
	}

	private static void add(Context context, int id) {
		try {//获取强引用的Bitmap对象
			Bitmap tempBitMap = readBitMap(context, id);
			//软引用的Bitmap对象
			SoftReference<Bitmap> bitmapcache = new SoftReference<Bitmap>(tempBitMap);
			//添加该对象到Map中使其缓存
			drawCache.put(id, bitmapcache);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 以最省内存的方式读取本地资源的图片
	 * @param context
	 * @param resId
	 * @return
	 */
	public static Bitmap readBitMap(Context context, int resId) {
		Bitmap b = null;
		try {
			BitmapFactory.Options opt = new BitmapFactory.Options();
			opt.inPreferredConfig = Bitmap.Config.ARGB_8888;
			opt.inPurgeable = true;
			opt.inInputShareable = true;
			// 获取资源图片
			InputStream is = context.getResources().openRawResource(resId);
			b = BitmapFactory.decodeStream(is, null, opt);
			is.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return b;
	}

}
