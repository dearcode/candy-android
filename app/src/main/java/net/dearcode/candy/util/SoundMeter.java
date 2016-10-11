package net.dearcode.candy.util;

import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Environment;

import net.dearcode.candy.controller.base.BaseActivity;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

public  class SoundMeter {
	static final private double EMA_FILTER = 0.6;

	private MediaRecorder mRecorder = null;
	private MediaPlayer mMediaPlayer = new MediaPlayer();
	private double mEMA = 0.0;

	public void start(String name) {
		if (!Environment.getExternalStorageState().equals(
				Environment.MEDIA_MOUNTED)) {
			return;
		}
		if (mRecorder == null) {
			mRecorder = new MediaRecorder();
			mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
			mRecorder.setOutputFormat(MediaRecorder.OutputFormat.RAW_AMR);
			mRecorder.setAudioChannels(1);
			mRecorder.setAudioSamplingRate(8000);  //采样率，最小得是8000，再小就报错了
			mRecorder.setAudioEncodingBitRate(1024);  //比特率（不懂，但是写1024就小了很多，一分钟大概47K左右）
			mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
			mRecorder.setOutputFile(name);

//			mRecorder = new MediaRecorder();
//			mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
//			mRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
//			mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
//			mRecorder.setOutputFile(name);
			try {
				mRecorder.prepare();
				mRecorder.start();

				if (voiceUtilAfterInitListener != null) {
					voiceUtilAfterInitListener.onAfterInit();
				}

				mEMA = 0.0;
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public void stop() {
		try {
			if (mRecorder != null) {
				mRecorder.stop();
				mRecorder.release();
				mRecorder = null;
			}
		} catch (Exception e) {
			e.printStackTrace();
			mRecorder = null;
		}

	}

	public void pause() {
		if (mRecorder != null) {
			mRecorder.stop();
		}
	}

	public void start() {
		if (mRecorder != null) {
			mRecorder.start();
		}
	}

	public double getAmplitude() {
		if (mRecorder != null)
			return (mRecorder.getMaxAmplitude() / 2700.0);
		else
			return 0;

	}

	public double getAmplitudeEMA() {
		double amp = getAmplitude();
		mEMA = EMA_FILTER * amp + (1.0 - EMA_FILTER) * mEMA;
		return mEMA;
	}

	public static String dir = "/cyou/arm";
	public static String checkDir() {
		File f = new File(Environment.getExternalStorageDirectory()+dir);
		if (!f.exists()) {
			f.mkdirs();
		}
		return f.getAbsolutePath();
	}

	/**
	 * 上传
	 * @param activity
	 * @param file
	 * @return
	 */
	public static String uploadVoice(BaseActivity activity, File file) {
//		String url = activity.mShareFunc.getUseHttpFunc().uploadImg(BitmapUtil.file2Byte(file), activity, ConstantValue.CYJUSER_UPLOAD_AMR);
//		if (url != null && url.startsWith("http://")) {
//			return url;
//		} else {
//			return null;
//		}
		return null;
	}

	/**
	 * 下载
	 * @param amrUrl
	 */
	public static void downloadVoice(String amrUrl) {
		InputStream is = null;
		FileOutputStream fos = null;
		try {
			String[] sa = amrUrl.split("/");
			String fileName = checkDir() + "/" + sa[sa.length-1];

			URL u = new URL(amrUrl);
			URLConnection conn = u.openConnection();
			conn.connect();
			is = conn.getInputStream();
			int fileSize = conn.getContentLength();
			if(fileSize<1||is==null) {
				return;
			}else{
				fos = new FileOutputStream(fileName);
				byte[] bytes = new byte[1024];
				int len = -1;
				while((len = is.read(bytes))!=-1) {
					fos.write(bytes, 0, len);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (is != null) {
					is.close();
				}
				if (fos != null) {
					fos.close();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * 自己说话上传成功后文件名会变
	 * @param of
	 * @param url
	 * @return
	 */
	public static boolean rename(File of, String url) {
		String[] tmpArr = url.split("/");
		String newName = tmpArr[tmpArr.length-1];
		File nf = new File(of.getParent()+"/"+newName);
		return of.renameTo(nf);
	}


//	public void startPlay(String url, MyOnCompletionListener l) {
//		try {
//			String arr[] = url.split("/");
//			String path = SoundMeter.checkDir()+"/"+arr[arr.length-1];
//
//			if (mMediaPlayer.isPlaying()) {
//				mMediaPlayer.stop();
//			}
//			mMediaPlayer.reset();
//			mMediaPlayer.setDataSource(path);
//			if (l != null) {
//				mMediaPlayer.setOnCompletionListener(l);
//			}
//			mMediaPlayer.prepare();
//			mMediaPlayer.start();
//
//		} catch (Exception e) {
//			e.printStackTrace();
//			if (l != null) {
//				l.onCompletion(mMediaPlayer);
//				l.onError();
//			}
//		}
//	}

	public void stopPlay() {
		try {
			if (voiceUtilStopListener != null) {
				voiceUtilStopListener.onStopPlay();
			}

			if (mMediaPlayer.isPlaying()) {
				mMediaPlayer.stop();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	//手动停止播放后回调
	public interface VoiceUtilStopListener  {
		public void onStopPlay();
	}

	private VoiceUtilStopListener voiceUtilStopListener;
	public VoiceUtilStopListener getVoiceUtilStopListener() {
		return voiceUtilStopListener;
	}
	public void setVoiceUtilStopListener(VoiceUtilStopListener voiceUtilStopListener) {
		this.voiceUtilStopListener = voiceUtilStopListener;
	}

	//初始化Recorder后回调（初始化有点慢，稍微有点延迟，为了UI只能这么来下）
	public interface VoiceUtilAfterInitListener {
		public void onAfterInit();
	}

	private VoiceUtilAfterInitListener voiceUtilAfterInitListener;
	public VoiceUtilAfterInitListener getVoiceUtilAfterInitListener() {
		return voiceUtilAfterInitListener;
	}
	public void setVoiceUtilAfterInitListener(
			VoiceUtilAfterInitListener voiceUtilAfterInitListener) {
		this.voiceUtilAfterInitListener = voiceUtilAfterInitListener;
	}

}
