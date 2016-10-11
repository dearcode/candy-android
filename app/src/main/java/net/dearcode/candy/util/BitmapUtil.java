package net.dearcode.candy.util;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.media.ExifInterface;
import android.util.Base64;
import android.view.Surface;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Hashtable;

public class BitmapUtil {

	public static byte[] file2Byte (File f) {
		if (f == null) {
			return null;
		}
		try {
			FileInputStream stream = new FileInputStream(f);
			ByteArrayOutputStream out = new ByteArrayOutputStream(2000);
			byte[] b = new byte[1000];
			for (int n; (n = stream.read(b)) != -1;) {
				out.write(b, 0, n);
			}
			stream.close();
			out.close();
			return out.toByteArray();
		} catch (OutOfMemoryError e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public static InputStream bitmap2IS(Bitmap bm) {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		bm.compress(Bitmap.CompressFormat.PNG, 100, baos);
		InputStream sbs = new ByteArrayInputStream(baos.toByteArray());
		return sbs;
	}

	public static byte[] bitmap2Bytes(Bitmap bm) {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		bm.compress(Bitmap.CompressFormat.PNG, 100, baos);
		return baos.toByteArray();
	}

	public static byte[] input2byte(InputStream inStream) throws Exception {
		ByteArrayOutputStream swapStream = new ByteArrayOutputStream();
		byte[] buff = new byte[100];
		int rc = 0;
		while ((rc = inStream.read(buff, 0, 100)) > 0) {
			swapStream.write(buff, 0, rc);
		}
		byte[] in2b = swapStream.toByteArray();

		swapStream.close();

		return in2b;
	}

	/**
	 * 加载文件中的图片，默认1000 * 700
	 * @param srcPath 图片路径
	 * @param adjustOritation 是否根据当时的镜头旋转照片
	 * @return bitmap
	 */
	public static Bitmap file2Bitmap(String srcPath, boolean adjustOritation, Boolean bCompress) {
		if(bCompress){
			return file2Bitmap(srcPath, 1000, 700, adjustOritation, true);
		}else{
			return file2Bitmap(srcPath, adjustOritation);
		}
	}

	/**
	 * 加载文件中的图片
	 * @param srcPath 图片路径
	 * @param w 要压缩的宽度
	 * @param h 要压缩的高度
	 * @param adjustOritation 是否根据当时的镜头旋转照片
	 * @return bitmap
	 */
	public static Bitmap file2Bitmap(String srcPath, float w, float h, boolean adjustOritation, boolean bFirst) {
		try {
			File file = new File(srcPath);
			if(file == null
					|| !file.isFile()){
				return null;
			}
			byte[] b = file2Byte(file);
			BitmapFactory.Options opts = new BitmapFactory.Options();
			opts.inJustDecodeBounds = true;
			BitmapFactory.decodeByteArray(b, 0, b.length, opts);

			// 得到图片的宽度、高度；
			int imgWidth = opts.outWidth;
			int imgHeight = opts.outHeight;
			// 分别计算图片宽度、高度与目标宽度、高度的比例；取大于该比例的最小整数；
			int widthRatio = (int) Math.ceil(imgWidth / (float) w);
			int heightRatio = (int) Math.ceil(imgHeight / (float) h);
			if (widthRatio > 1 && heightRatio > 1) {
				if (widthRatio > heightRatio) {
					opts.inSampleSize = widthRatio;
				} else {
					opts.inSampleSize = heightRatio;
				}
			}
			opts.inPreferredConfig = Config.ARGB_8888;//该模式是默认的,可不设
			opts.inPurgeable = true;// 同时设置才会有效
			opts.inInputShareable = true;//。当系统内存不够时候图片自动被回收
			opts.inJustDecodeBounds = false;  // 设置好缩放比例后，加载图片进内存；

			Bitmap bmp = BitmapFactory.decodeByteArray(b, 0, b.length, opts);
			if(bmp == null && file.exists()){
				file.delete();
				return null;
			}
			//根据照相时的角度旋转图片
			if (adjustOritation) {
				int digree = 0;
				ExifInterface exif = null;
				try {
					exif = new ExifInterface(srcPath);
				} catch (IOException e) {
					e.printStackTrace();
					exif = null;
				}
				if (exif != null) {
					// 读取图片中相机方向信息
					int ori = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION,
							ExifInterface.ORIENTATION_UNDEFINED);
					// 计算旋转角度
					switch (ori) {
						case ExifInterface.ORIENTATION_ROTATE_90:
							digree = 90;
							break;
						case ExifInterface.ORIENTATION_ROTATE_180:
							digree = 180;
							break;
						case ExifInterface.ORIENTATION_ROTATE_270:
							digree = 270;
							break;
						default:
							digree = 0;
							break;
					}
				}
				if (digree != 0) {
					// 旋转图片
					Matrix m = new Matrix();
					m.postRotate(digree);
					bmp = Bitmap.createBitmap(bmp, 0, 0, bmp.getWidth(), bmp.getHeight(), m, true);
				}
				return bmp;

			} else {
				return bmp;
			}
//			return BitmapFactory.decodeByteArray(b, 0, b.length, opts);
		} catch (OutOfMemoryError e){
			if(bFirst){
				try {
					return file2Bitmap(srcPath, 800, 500, adjustOritation, false);
				} catch (Exception e1) {
					e1.printStackTrace();
					return null;
				}
			}else{
				return null;
			}
		}
		catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * 不压缩图片
	 * @param srcPath
	 * @param adjustOritation
	 * @return
	 */
	public static Bitmap file2Bitmap(String srcPath, boolean adjustOritation) {
		try {
			Bitmap bmp = BitmapFactory.decodeFile(srcPath);
			if(bmp == null){
				return null;
			}
			//根据照相时的角度旋转图片
			if (adjustOritation) {
				int digree = 0;
				ExifInterface exif = null;
				try {
					exif = new ExifInterface(srcPath);
				} catch (IOException e) {
					e.printStackTrace();
					exif = null;
				}
				if (exif != null) {
					// 读取图片中相机方向信息
					int ori = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION,
							ExifInterface.ORIENTATION_UNDEFINED);
					// 计算旋转角度
					switch (ori) {
						case ExifInterface.ORIENTATION_ROTATE_90:
							digree = 90;
							break;
						case ExifInterface.ORIENTATION_ROTATE_180:
							digree = 180;
							break;
						case ExifInterface.ORIENTATION_ROTATE_270:
							digree = 270;
							break;
						default:
							digree = 0;
							break;
					}
				}
				if (digree != 0) {
					// 旋转图片
					Matrix m = new Matrix();
					m.postRotate(digree);
					bmp = Bitmap.createBitmap(bmp, 0, 0, bmp.getWidth(), bmp.getHeight(), m, true);
				}
				return bmp;

			} else {
				return bmp;
			}
//			return BitmapFactory.decodeByteArray(b, 0, b.length, opts);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	// 提供一个静态方法，用于根据手机方向获得相机预览画面旋转的角度
	public static int getPreviewDegree(Activity activity) {
		// 获得手机的方向
		int rotation = activity.getWindowManager().getDefaultDisplay()
				.getRotation();
		int degree = 0;
		// 根据手机的方向计算相机预览画面应该选择的角度
		switch (rotation) {
			case Surface.ROTATION_0:
				degree = 90;
				break;
			case Surface.ROTATION_90:
				degree = 0;
				break;
			case Surface.ROTATION_180:
				degree = 270;
				break;
			case Surface.ROTATION_270:
				degree = 180;
				break;
		}
		return degree;
	}

	/**
	 * 缩放图片
	 * @param bitmap
	 * @param zf
	 * @return
	 */
	public static Bitmap zoom(Bitmap bitmap, float zf) {
		Matrix matrix = new Matrix();
		matrix.postScale(zf, zf);
		return Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(),
				bitmap.getHeight(), matrix, true);
	}

	/**
	 * 缩放图片
	 * @param bitmap
	 * @param wf
	 * @return
	 */
	public static Bitmap zoom(Bitmap bitmap, float wf, float hf) {
		Matrix matrix = new Matrix();
		matrix.postScale(wf, hf);
		return Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(),
				bitmap.getHeight(), matrix, true);
	}

	/**
	 * 缩放图片
	 * @param bitmap
	 * @param newWidth
	 * @return
	 */
	public static Bitmap resizeBitmap(Bitmap bitmap, int newWidth) {
		int width = bitmap.getWidth();
		int height = bitmap.getHeight();
		float temp = ((float) height) / ((float) width);
		int newHeight = (int) ((newWidth) * temp);
		float scaleWidth = ((float) newWidth) / width;
		float scaleHeight = ((float) newHeight) / height;
		Matrix matrix = new Matrix();
		// resize the bit map
		matrix.postScale(scaleWidth, scaleHeight);
		// matrix.postRotate(45);
		Bitmap resizedBitmap = Bitmap.createBitmap(bitmap, 0, 0, width, height,
				matrix, true);
//		bitmap.recycle();
		return resizedBitmap;
	}

	/**
	 * 图片圆角处理，默认的角度：12
	 * @param bitmap
	 * @return
	 */
	public static Bitmap getRCB(Bitmap bitmap) {
		return getRCB(bitmap, 12, 0);
	}

	/**
	 * 图片圆角处理
	 * @param bitmap
	 * @param roundPX
	 * @param addExtend 高度要添加的部分，比如画上圆下方的图
	 * @return
	 */
	public static Bitmap getRCB(Bitmap bitmap, float roundPX, int addExtend) {
		Bitmap output = Bitmap.createBitmap(bitmap.getWidth(),bitmap.getHeight(), Config.ARGB_8888);
		//得到画布
		Canvas canvas = new Canvas(output);
		//将画布的四角圆化
		final int color = Color.RED;
		final Paint paint = new Paint();
		//得到与图像相同大小的区域  由构造的四个值决定区域的位置以及大小
		final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight()+addExtend);
		final RectF rectF = new RectF(rect);
		paint.setAntiAlias(true);
		canvas.drawARGB(0, 0, 0, 0);
		paint.setColor(color);
		//drawRoundRect的第2,3个参数一样则画的是正圆的一角，如果数值不同则是椭圆的一角
		canvas.drawRoundRect(rectF, roundPX, roundPX, paint);
		paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
		canvas.drawBitmap(bitmap, rect, rect, paint);
		return output;
	}


	/**
	 * 将图片变成圆形图片
	 * @param bitmap
	 * @return
	 */
	public static Bitmap toRoundBitmap(Bitmap bitmap) {
		int width = bitmap.getWidth();
		int height = bitmap.getHeight();
		float roundPx;
		float left,top,right,bottom,dst_left,dst_top,dst_right,dst_bottom;
		if (width <= height) {
			roundPx = width / 2;
			top = 0;
			bottom = width;
			left = 0;
			right = width;
			height = width;
			dst_left = 0;
			dst_top = 0;
			dst_right = width;
			dst_bottom = width;
		} else {
			roundPx = height / 2;
			float clip = (width - height) / 2;
			left = clip;
			right = width - clip;
			top = 0;
			bottom = height;
			width = height;
			dst_left = 0;
			dst_top = 0;
			dst_right = height;
			dst_bottom = height;
		}
		Bitmap output = Bitmap.createBitmap(width,height, Config.ARGB_8888);
		Canvas canvas = new Canvas(output);

		final int color = 0xff424242;
		final Paint paint = new Paint();
		final Rect src = new Rect((int)left, (int)top, (int)right, (int)bottom);
		final Rect dst = new Rect((int)dst_left, (int)dst_top, (int)dst_right, (int)dst_bottom);
		final RectF rectF = new RectF(dst);

		paint.setAntiAlias(true);

		canvas.drawARGB(0, 0, 0, 0);
		paint.setColor(color);
		canvas.drawRoundRect(rectF, roundPx, roundPx, paint);

		paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
		canvas.drawBitmap(bitmap, src, dst, paint);
		return output;
	}

	/**
	 * 获取网落图片资源
	 * @param url
	 * @return
	 */
	public static Bitmap getHttpBitmap(String url, int time) {
		if(url.contains("/man.png")){
			return null;
		}
		URL myFileURL;
		Bitmap bitmap = null;
		try {
//			url = url.replace("files.bbs.tl.changyou.com", "10.127.65.179");

			myFileURL = new URL(url);
			// 获得连接
			HttpURLConnection conn = (HttpURLConnection) myFileURL.openConnection();
			// 设置超时时间为6000毫秒，conn.setConnectionTiem(0);表示没有时间限制
			conn.setConnectTimeout(time);
			conn.setReadTimeout(time);
			// 连接设置获得数据流
			conn.setDoInput(true);
			// 不使用缓存
			conn.setUseCaches(false);
			// 这句可有可无，没有影响
			// conn.connect();
			// 得到数据流转为byte
			InputStream is = conn.getInputStream();
			byte[] imgByte = input2byte(is);
			// 关闭数据流
			is.close();

			//按1000 * 700 压缩图片，防止图片过大而OOM
			BitmapFactory.Options opts = new BitmapFactory.Options();
			opts.inJustDecodeBounds = true;
			BitmapFactory.decodeByteArray(imgByte, 0, imgByte.length, opts);
			// 得到图片的宽度、高度；
			int imgWidth = opts.outWidth;
			int imgHeight = opts.outHeight;
			// 分别计算图片宽度、高度与目标宽度、高度的比例；取大于该比例的最小整数；
			int widthRatio = (int) Math.ceil(imgWidth / (float) 1000);
			int heightRatio = (int) Math.ceil(imgHeight / (float) 700);
			if (widthRatio > 1 && heightRatio > 1) {
				if (widthRatio > heightRatio) {
					opts.inSampleSize = widthRatio;
				} else {
					opts.inSampleSize = heightRatio;
				}
			}
			// 设置好缩放比例后，加载图片进内存；
			opts.inJustDecodeBounds = false;

			bitmap = BitmapFactory.decodeByteArray(imgByte, 0, imgByte.length, opts);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return bitmap;
	}


	/**
	 * 获取网落图片资源，有MD5校验
	 * @param url
	 * @return
	 */
//	public static Bitmap getHttpBitmap(String url, int time, String md5Str) {
//		URL myFileURL;
//		Bitmap bitmap = null;
//		try {
//			myFileURL = new URL(url);
//			// 获得连接
//			HttpURLConnection conn = (HttpURLConnection) myFileURL.openConnection();
//			// 设置超时时间为time毫秒，conn.setConnectionTiem(0);表示没有时间限制
//			conn.setConnectTimeout(time);
//			conn.setReadTimeout(time);
//			// 连接设置获得数据流
//			conn.setDoInput(true);
//			// 不使用缓存
//			conn.setUseCaches(false);
//			// 这句可有可无，没有影响
//			// conn.connect();
//			// 得到数据流
//			InputStream is = conn.getInputStream();
//			byte[] orignalByte = input2byte(is);
//			String md5 = ShareFunc.MD5(orignalByte);
//			if (md5 == null || !md5.equals(md5Str)) {
//				bitmap = null;
//			} else {
//				// 解析得到图片
//				bitmap = BitmapFactory.decodeByteArray(orignalByte, 0, orignalByte.length);
//			}
//			// 关闭数据流
//			is.close();
//		} catch (Exception e) {
//			e.printStackTrace();
//			bitmap = null;
//		}
//		return bitmap;
//
//	}

	/**
	 * 图片写本地
	 * @param fileDirPath 文件根路径
	 * @param fileName 文件名
	 * @param mBitmap
	 */
	public static void saveBitmap(String fileDirPath, Bitmap mBitmap, String fileName){
		try {
			File file = new File(fileDirPath);
			if(!file.exists()){
				file.mkdirs();
			}
			saveBitmap(fileDirPath+fileName, mBitmap);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 图片写本地
	 * @param filePath
	 * @param mBitmap
	 */
	public static void saveBitmap(String filePath,Bitmap mBitmap){
		try {
			if (mBitmap == null) {
				return;
			}
			File f = new File(filePath);
			//删掉从新写，以便最近最少
			if(f.exists()) {
				f.delete();
			}
			f.createNewFile();
			FileOutputStream fOut = new FileOutputStream(f);
			mBitmap.compress(Bitmap.CompressFormat.PNG, 100, fOut);
			fOut.flush();
			fOut.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 *
	 * @param drawable
	 * @return
	 */
	public static Bitmap convertDrawable2BitmapByCanvas(Drawable drawable) {

		Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(),
				drawable.getOpacity() != PixelFormat.OPAQUE ? Config.ARGB_8888 : Config.RGB_565);

		Canvas canvas = new Canvas(bitmap);
		drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
		drawable.draw(canvas);

		return bitmap;
	}

//	public static void changeChatBgByFrom(Context context, Bitmap src, boolean beFrom) {
//
//		Drawable bg;
//		if (beFrom) {
//			bg = context.getResources().getDrawable(R.drawable.chat_from_bg);
//		} else {
//			bg = context.getResources().getDrawable(R.drawable.chat_to_bg);
//		}
//
//		Bitmap fBm = Bitmap.createBitmap(src.getWidth(), src.getHeight(), Config.ARGB_8888);
//
//		Canvas fCanvas = new Canvas(fBm);
//		bg.setBounds(0, 0, src.getWidth(), src.getHeight());
//		bg.draw(fCanvas);
//
//		Paint paint = new Paint();
//		paint.setXfermode(new PorterDuffXfermode(Mode.DST_IN));
//		Canvas canvas = new Canvas(src);
//		canvas.drawBitmap(fBm,  0,  0,  paint);
//		canvas.save();
//
//		fBm.recycle();
//	}

	/**
	 * 置灰
	 * @param bitmap
	 * @return
	 */
	public static Bitmap getGrayBitmap(Bitmap bitmap) {
		try {
			int width = bitmap.getWidth();
			int height = bitmap.getHeight();
			Bitmap grayImg = Bitmap.createBitmap(width, height, Config.ARGB_8888);
			// 
			Canvas canvas = new Canvas(grayImg);

			Paint paint = new Paint();
			ColorMatrix colorMatrix = new ColorMatrix();
			colorMatrix.setSaturation(0);
			ColorMatrixColorFilter colorMatrixFilter = new ColorMatrixColorFilter(
					colorMatrix);
			paint.setColorFilter(colorMatrixFilter);
			canvas.drawBitmap(bitmap, 0, 0, paint);
			return grayImg;

		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public static String bitmaptoString(Bitmap bitmap, int bitmapQuality) {
		String result = null;
		ByteArrayOutputStream baos = null;
		try {
			if (bitmap != null) {
				baos = new ByteArrayOutputStream();
				bitmap.compress(Bitmap.CompressFormat.PNG, bitmapQuality, baos);

				baos.flush();
				baos.close();

				byte[] bitmapBytes = baos.toByteArray();
				result = Base64.encodeToString(bitmapBytes, Base64.NO_WRAP);
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (baos != null) {
					baos.flush();
					baos.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return result;
	}

	/**
	 * 将base64转换成bitmap图片
	 *
	 * @param string base64字符串
	 * @return bitmap
	 */
	public static Bitmap stringtoBitmap(String string) {
		byte[] bytes = Base64.decode(string, Base64.DEFAULT);
		return BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
	}

//	/**
//	 * 根据URL从本地或者网络下载图片
//	 *
//	 * @param context
//	 * @param url
//	 * @param isRoundedCorner
//	 * @return
//	 */
//	public static Bitmap getFromUrlAndLocal(Context context, String url,
//											boolean isRoundedCorner, From from) {
//		try {
//			Bitmap bmp = ZZBUtil.commonCache.get(from + "_" + url);
//			if (bmp != null) {
//				return bmp;
//			}
//
//			String imgDir = ImageLoaderLocal.getImgDir(context);
//			if (imgDir != null) {
//				// 获取图片名称（以Url中的图片名为名字）
//				String[] sa = url.split("/");
//				String imgName = from + "_" + sa[sa.length - 1];
//
//				// 本地图片存在，则展示，否则下载
//				File imgFile;
//				if (imgName.endsWith(".jpg") || imgName.endsWith(".JPG")
//						|| imgName.endsWith(".png") || imgName.endsWith(".PNG")) {
//					imgFile = new File(imgDir + "/" + imgName);
//				} else {
//					imgFile = new File(imgDir + "/" + imgName + ".jpg");
//				}
//
//				if (imgFile.exists()) {
//					// 从软引用中取
//					bmp = BitMapCacheUtil.get(imgName, imgFile.getPath());
//				}
//
//				if (bmp == null) {
//					// 从指定的url中下载图片
//					bmp = BitmapUtil.getHttpBitmap(url, 10 * 1000);
//					bmp = ImageCompressUtil.zoomImageByMax(bmp,
//							getImgMaxScale(context), true);
//				}
//
//				// 写入本地
//				BitmapUtil.saveBitmap(imgFile.getPath(), bmp);
//			} else {
//				// 从指定的url中下载图片
//				bmp = BitmapUtil.getHttpBitmap(url, 10 * 1000);
//			}
//
//			// 圆角化，需要在判断下
//			if (isRoundedCorner && bmp != null) {
//				bmp = BitmapUtil.getRCB(bmp, 12, 0);
//			}
//			// 如果还是空，则返回默认头像
//			if (bmp == null) {
//				bmp = BitMapCacheUtil.get(context, R.drawable.default_head);
//			}
//			ZZBUtil.commonCache.put(from + "_" + url, bmp);
//			return bmp;
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//		return BitMapCacheUtil.get(context, R.drawable.default_head);
//	}

//	public static int getImgMaxScale(Context c) {
//		return c.getResources().getDimensionPixelSize(R.dimen.dip_150);
//	}

	//解析二维码图片
//	public static Result scanningImage(String path) {
//		try {
//			BitmapFactory.Options options = new BitmapFactory.Options();
//			options.inJustDecodeBounds = true; // 先获取原大小
//			Bitmap scanBitmap = BitmapFactory.decodeFile(path, options);
//			options.inJustDecodeBounds = false; // 获取新的大小
//
//			int sampleSize = (int) (options.outHeight / (float) 400);
//
//			if (sampleSize <= 0)
//				sampleSize = 1;
//			options.inSampleSize = sampleSize;
//			scanBitmap = BitmapFactory.decodeFile(path, options);
//
//			RGBLuminanceSource source = new RGBLuminanceSource(scanBitmap);
//			BinaryBitmap bitmap1 = new BinaryBitmap(new HybridBinarizer(source));
//			QRCodeReader reader = new QRCodeReader();
//
//			// DecodeHintType 和EncodeHintType
//			Hashtable<DecodeHintType, String> hints = new Hashtable<DecodeHintType, String>();
//			hints.put(DecodeHintType.CHARACTER_SET, "utf-8"); // 设置二维码内容的编码
//			return reader.decode(bitmap1, hints);
//		} catch (NotFoundException e) {
//			e.printStackTrace();
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//
//		return null;
//
//	}

	//解析二维码图片
//	public static Result scanningImage(Bitmap bitmap) {
//		try {
//			// DecodeHintType 和EncodeHintType
//			Hashtable<DecodeHintType, String> hints = new Hashtable<DecodeHintType, String>();
//			hints.put(DecodeHintType.CHARACTER_SET, "utf-8"); // 设置二维码内容的编码
//
//			RGBLuminanceSource source = new RGBLuminanceSource(bitmap);
//			BinaryBitmap bitmap1 = new BinaryBitmap(new HybridBinarizer(source));
//			QRCodeReader reader = new QRCodeReader();
//
//			return reader.decode(bitmap1, hints);
//		} catch (NotFoundException e) {
//			e.printStackTrace();
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//
//		return null;
//
//	}

	public static Bitmap createMaskImage(Bitmap source) {
		final Paint paint = new Paint();
		paint.setAntiAlias(true);

//		ColorDrawable drawable = new ColorDrawable(Color.parseColor("#FF717171"));
//		Bitmap bmp = Bitmap.createBitmap(300,300,Bitmap.Config.ARGB_8888);
//		drawable.draw(new Canvas(bmp)); //TODO  这样你就可以拿到这个bitmap了

		// 注意一定要用ARGB_8888，否则因为背景不透明导致遮罩失败
		Bitmap target = Bitmap.createBitmap(source.getWidth(), source.getHeight(), Config.ARGB_8888);

		// 产生一个同样大小的画布
		Canvas canvas = new Canvas(target);
		// 首先绘制圆形
//		canvas.drawCircle(min / 2, min / 2, min / 2, paint);
		//灰色背景图，与需要展示的图，同大小
//		Rect rect = new Rect(0,0,source.getWidth(),source.getHeight());
//		canvas.drawBitmap(bmp, null, rect, paint);
		// 使用DST_OVER
//		paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_OVER));
		// 绘制图片
		canvas.drawBitmap(source, 0, 0, paint);
		return target;
	}

	//图片做高斯模糊算法  radius
	public static Bitmap bmpDoFastBlur(Bitmap sentBitmap, int radius) {
		if (sentBitmap == null || radius < 1) {
			return null;
		}
//Bitmap.createScaledBitmap(sentBitmap, )
		Bitmap bitmap =null;
		try{
			bitmap = sentBitmap.copy(sentBitmap.getConfig(), true);

			int w = bitmap.getWidth();
			int h = bitmap.getHeight();

			int[] pix = new int[w * h];
			bitmap.getPixels(pix, 0, w, 0, 0, w, h);

			int wm = w - 1;
			int hm = h - 1;
			int wh = w * h;
			int div = radius + radius + 1;

			int r[] = new int[wh];
			int g[] = new int[wh];
			int b[] = new int[wh];
			int rsum, gsum, bsum, x, y, i, p, yp, yi, yw;
			int vmin[] = new int[Math.max(w, h)];
			int divsum = (div + 1) >> 1;
			divsum *= divsum;
			int dv[] = new int[256 * divsum];
			for (i = 0; i < 256 * divsum; i++) {
				dv[i] = (i / divsum);
			}

			yw = yi = 0;

			int[][] stack = new int[div][3];
			int stackpointer;
			int stackstart;
			int[] sir;
			int rbs;
			int r1 = radius + 1;
			int routsum, goutsum, boutsum;
			int rinsum, ginsum, binsum;

			for (y = 0; y < h; y++) {
				rinsum = ginsum = binsum = routsum = goutsum = boutsum = rsum = gsum = bsum = 0;
				for (i = -radius; i <= radius; i++) {
					p = pix[yi + Math.min(wm, Math.max(i, 0))];
					sir = stack[i + radius];
					sir[0] = (p & 0xff0000) >> 16;
					sir[1] = (p & 0x00ff00) >> 8;
					sir[2] = (p & 0x0000ff);
					rbs = r1 - Math.abs(i);
					rsum += sir[0] * rbs;
					gsum += sir[1] * rbs;
					bsum += sir[2] * rbs;
					if (i > 0) {
						rinsum += sir[0];
						ginsum += sir[1];
						binsum += sir[2];
					} else {
						routsum += sir[0];
						goutsum += sir[1];
						boutsum += sir[2];
					}
				}
				stackpointer = radius;

				for (x = 0; x < w; x++) {

					r[yi] = dv[rsum];
					g[yi] = dv[gsum];
					b[yi] = dv[bsum];

					rsum -= routsum;
					gsum -= goutsum;
					bsum -= boutsum;

					stackstart = stackpointer - radius + div;
					sir = stack[stackstart % div];

					routsum -= sir[0];
					goutsum -= sir[1];
					boutsum -= sir[2];

					if (y == 0) {
						vmin[x] = Math.min(x + radius + 1, wm);
					}
					p = pix[yw + vmin[x]];

					sir[0] = (p & 0xff0000) >> 16;
					sir[1] = (p & 0x00ff00) >> 8;
					sir[2] = (p & 0x0000ff);

					rinsum += sir[0];
					ginsum += sir[1];
					binsum += sir[2];

					rsum += rinsum;
					gsum += ginsum;
					bsum += binsum;

					stackpointer = (stackpointer + 1) % div;
					sir = stack[(stackpointer) % div];

					routsum += sir[0];
					goutsum += sir[1];
					boutsum += sir[2];

					rinsum -= sir[0];
					ginsum -= sir[1];
					binsum -= sir[2];

					yi++;
				}
				yw += w;
			}
			for (x = 0; x < w; x++) {
				rinsum = ginsum = binsum = routsum = goutsum = boutsum = rsum = gsum = bsum = 0;
				yp = -radius * w;
				for (i = -radius; i <= radius; i++) {
					yi = Math.max(0, yp) + x;

					sir = stack[i + radius];

					sir[0] = r[yi];
					sir[1] = g[yi];
					sir[2] = b[yi];

					rbs = r1 - Math.abs(i);

					rsum += r[yi] * rbs;
					gsum += g[yi] * rbs;
					bsum += b[yi] * rbs;

					if (i > 0) {
						rinsum += sir[0];
						ginsum += sir[1];
						binsum += sir[2];
					} else {
						routsum += sir[0];
						goutsum += sir[1];
						boutsum += sir[2];
					}

					if (i < hm) {
						yp += w;
					}
				}
				yi = x;
				stackpointer = radius;
				for (y = 0; y < h; y++) {
					// Preserve alpha channel: ( 0xff000000 & pix[yi] )
					pix[yi] = (0xff000000 & pix[yi]) | (dv[rsum] << 16)
							| (dv[gsum] << 8) | dv[bsum];

					rsum -= routsum;
					gsum -= goutsum;
					bsum -= boutsum;

					stackstart = stackpointer - radius + div;
					sir = stack[stackstart % div];

					routsum -= sir[0];
					goutsum -= sir[1];
					boutsum -= sir[2];

					if (x == 0) {
						vmin[y] = Math.min(y + r1, hm) * w;
					}
					p = x + vmin[y];

					sir[0] = r[p];
					sir[1] = g[p];
					sir[2] = b[p];

					rinsum += sir[0];
					ginsum += sir[1];
					binsum += sir[2];

					rsum += rinsum;
					gsum += ginsum;
					bsum += binsum;

					stackpointer = (stackpointer + 1) % div;
					sir = stack[stackpointer];

					routsum += sir[0];
					goutsum += sir[1];
					boutsum += sir[2];

					rinsum -= sir[0];
					ginsum -= sir[1];
					binsum -= sir[2];

					yi += w;
				}
			}

			bitmap.setPixels(pix, 0, w, 0, 0, w, h);
		}catch (Exception e){
			e.printStackTrace();
		}

		return bitmap;
	}
}
