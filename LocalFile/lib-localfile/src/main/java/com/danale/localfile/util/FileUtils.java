package com.danale.localfile.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class FileUtils {
	public static final String MainDir = "DanaleCamera";
	public static final String VideoDir = "DanaleVideo";
	public static final String VideoThumbsDir = "DanaleVideoThumb";
	public static final String SnapshotDir = "DanaleSnapshot";
	public static final String CacheDir = "DanaleCache";
	public static final String PersonPortrait = "DanalePortrait";
	public static final String RomDir = "Rom";

	/**
	 * 获得存储主目录名称
	 * 
	 * @return
	 */
	public static String getMainDir() {
		return MainDir;
	}

	/**
	 * 获取指定用户名下的录像目录
	 * 
	 * @param account
	 * @return
	 */
	public static File getRecordDir(Context context, String account) {
		String videoDir = null;
		if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)
				|| !Environment.isExternalStorageRemovable()) {
			videoDir = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + MainDir
					+ File.separator + account + File.separator + VideoDir;
		} else {
			videoDir = context.getFilesDir().getAbsolutePath() + File.separator + MainDir
					+ File.separator + account + File.separator + VideoDir;
		}

		File file = null;
		file = new File(videoDir);
		if (!file.exists() || !file.isDirectory()) {
			file.mkdirs();
		}
		return file;
	}


	/**
	 * 获得录像缩略图的目录
	 * 
	 * @param account
	 * @return
	 */
	public static File getRecordThumbsDir(Context context, String account) {
		String videoThumbsDir = null;
		if (Environment.getExternalStorageState().equals((Environment.MEDIA_MOUNTED))
				|| !Environment.isExternalStorageRemovable()) {
			videoThumbsDir = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + MainDir
					+ File.separator + account + File.separator + VideoThumbsDir;
			Log.e("SPLASH","equals((Environment.MEDIA_MOUNTED) : " + videoThumbsDir );
		} else {
			videoThumbsDir = context.getFilesDir().getAbsolutePath() + File.separator + MainDir
					+ File.separator + account + File.separator + VideoThumbsDir;
		}

		File file = null;
		file = new File(videoThumbsDir);
		if (!file.exists() || !file.isDirectory()) {
			file.mkdirs();
		}
		return file;
	}



	/**
	 * 获取指定用户名下的抓拍目录
	 * 
	 * @param account
	 * @return
	 */
	public static File getSnapshotDir(Context context, String account) {
		String snapshotDir = null;
		if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
			snapshotDir = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + MainDir
					+ File.separator + account + File.separator + SnapshotDir;
		} else {
			snapshotDir = context.getFilesDir().getAbsolutePath() + File.separator + MainDir
					+ File.separator + account + File.separator + SnapshotDir;
		}
		File file = new File(snapshotDir);
		if (!file.exists() || !file.isDirectory()) {
			file.mkdirs();
		}
		return file;
	}

	/**
	 * 获取指定用户名下的缓存目录
	 * 
	 * @param account
	 * @return
	 */
	public static File getCacheDir(Context context, String account) {
		String cacheDir = null;
		if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
			cacheDir = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + MainDir
					+ File.separator + account + File.separator + CacheDir;
		} else {
			cacheDir = context.getCacheDir().getAbsolutePath() + File.separator + MainDir
					+ File.separator + account + File.separator + CacheDir;
		}
		File file = new File(cacheDir);
		if (!file.exists() || !file.isDirectory()) {
			file.mkdirs();
		}
		return file;
	}

	/**
	 * 获得头像的存储路径
	 * 
	 * @param account
	 * @return
	 */
	public static File getPersonPortrait(String account) {
		File file = null;
		String portrait = Environment.getExternalStorageDirectory().getPath() + File.separator + MainDir
				+ File.separator + account + File.separator + PersonPortrait;
		file = new File(portrait);
		if (!file.exists() || !file.isDirectory()) {
			file.mkdirs();
		}
		return file;
	}

	public static boolean isPortraitDirExist(String account) {
		File file = null;
		String portrait = Environment.getExternalStorageDirectory().getPath() + File.separator + MainDir
				+ File.separator + account + File.separator + PersonPortrait;
		file = new File(portrait);
		return file.exists() && file.isDirectory();
	}

	/**
	 * 获得以用户名命名的头像的存储路径
	 * 
	 * @param userName
	 * @return
	 */
	public static String getAccountNamePortrait(String userName) {
		String name = TextUtils.isEmpty(userName) ? "" : userName;
		File portraitDir = getPersonPortrait(name);
		String namePP = name + ".jpg";
		StringBuffer buffer = new StringBuffer();
		buffer.append(portraitDir.getAbsolutePath()).append('/').append(namePP);
		return buffer.toString();
	}

	/**
	 * 保存图片到指定路径(bitmap)
	 * 
	 * @param bitmap
	 * @param path
	 */
	public static void savePic(Bitmap bitmap, String path) {
		try {
			FileOutputStream out = null;
			out = new FileOutputStream(path);
			bitmap.compress(CompressFormat.JPEG, 100, out);
			out.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 删除文件
	 * 
	 * @param filePath
	 * @return
	 */
	public static boolean deleteFile(String filePath) {
		return deleteFile(new File(filePath));
	}

	/**
	 * 删除文件
	 * 
	 * @param file
	 * @return
	 */
	public static boolean deleteFile(File file) {
		if (file.exists()) {
			return file.delete();
		}
		return false;
	}

//	public static String getSavedScreenShotPath(File file, Device device) {
//		SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd_HHmmss");
////		String devName = device.getAlias();
//		String devName = device.getDeviceId();
//		String channel = String.format("ch%1$02d", device.getChannelNum());
//		String time = format.format(new Date());
//		String fileName = devName + '_' + channel + '_' + time + ".png";
//		final String path = file.getAbsolutePath() + File.separatorChar + fileName;
////		try {
////			new File(path).createNewFile();
////		} catch (IOException e) {
////			e.printStackTrace();
////		}
//		return path;
//	}

	/**
	 * 获得录像文件存储路径
	 * @param account
	 * @param deviceAlias
	 * @param channelNum
	 * @return
	 */
	public static String getRecordFilePath(Context context, String account, String deviceAlias, int channelNum, String suffix) {
		File file = FileUtils.getRecordDir(context, account);
		SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd_HHmmss");
		String channel = String.format("ch%1$02d", channelNum);
		String time = format.format(new Date());

		String fileName = deviceAlias + '_' + channel + '_' + time + suffix;
		return (file.getAbsolutePath() + File.separatorChar + fileName);
	}

	/**
	 * 获得录像文件存储路径
	 * @param account
	 * @param deviceAlias
	 * @param channelNum
	 * @return
	 */
	public static String getRecordFilePathPortraitBell(Context context, String account, String deviceAlias, int channelNum, String suffix) {
		File file = FileUtils.getRecordDir(context, account);
		SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd_HHmmss");
		String channel = String.format("ch%1$02d", channelNum);
		String time = format.format(new Date());

		String fileName = deviceAlias + '_' + channel + '_' + time + '_' + "bell" + suffix;
		return (file.getAbsolutePath() + File.separatorChar + fileName);
	}

}
