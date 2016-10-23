package tw.showang.apiabstrationframework.logger.internal;

import android.util.Log;

import tw.showang.apiabstrationframework.logger.Logger;

public class AndroidLogger implements Logger {

	private String mAppName;

	public AndroidLogger(String appName) {
		mAppName = appName;
	}

	@Override
	public void e(String message) {
		e("[" + mAppName + "]", message);
	}

	@Override
	public void e(String tag, String message) {
		Log.e(tag, message);
	}

	@Override
	public void i(String message) {
		i("[" + mAppName + "]", message);
	}

	@Override
	public void i(String tag, String message) {
		Log.i(tag, message);
	}

	@Override
	public void w(String message) {
		w("[" + mAppName + "]", message);
	}

	@Override
	public void w(String tag, String message) {
		Log.w(tag, message);
	}

	@Override
	public void d(String message) {
		d("[" + mAppName + "]", message);
	}

	@Override
	public void d(String tag, String message) {
		Log.d(tag, message);
	}

	@Override
	public void v(String message) {
		v("[" + mAppName + "]", message);
	}

	@Override
	public void v(String tag, String message) {
		Log.v(tag, message);
	}

	@Override
	public void wtf(String message) {
		wtf("[" + mAppName + "]", message);
	}

	@Override
	public void wtf(String tag, String message) {
		Log.wtf(tag, message);
	}
}
