package tw.showang.apiabstractionframework.example.api;

import android.support.annotation.Nullable;
import android.util.Log;

import com.google.gson.Gson;

import java.util.Map;

import tw.showang.apiabstrationframework.Api;
import tw.showang.apiabstrationframework.ApiCipher;
import tw.showang.apiabstrationframework.RequestExecutor;
import tw.showang.apiabstrationframework.error.RequestError;
import tw.showang.apiabstrationframework.logger.Logger;
import tw.showang.apiabstrationframework.util.AsyncManager;
import tw.showang.apiabstrationframework.util.AsyncManager.AsyncWork;
import tw.showang.apiabstrationframework.util.AsyncManager.PostWork;

public abstract class ExampleApiBase<SubClass extends ExampleApiBase<SubClass, Result>, Result> implements Api {

	private static Gson sGson;
	private static Logger sLogger;
	private static RequestExecutor sExecutor;

	public static void init(RequestExecutor executor, Logger logger) {
		sExecutor = executor;
		sLogger = logger;
		sGson = new Gson();
	}

	private ApiSuccessListener<Result> mSuccessListener;
	private ApiErrorListener mErrorListener;

	@Override
	public int getPriority() {
		return Priority.NORMAL;
	}

	@Override
	public String getProtocol() {
		return Protocol.HTTPS;
	}

	@Override
	public String getDomainName() {
		return "jsonplaceholder.typicode.com";
	}

	protected String getBaseUri() {
		return getProtocol() + getDomainName();
	}

	@Override
	public void onRequestSuccess(final String result) {
		new AsyncManager<MiddleResult<Result>>()
				.background(new AsyncWork<MiddleResult<Result>>() {
					@Override
					public MiddleResult<Result> doInBackground() {
						MiddleResult<Result> middleResult = new MiddleResult<>();
						try {
							middleResult.result = parseResult(sGson, result);
						} catch (Exception e) {
							sLogger.e(Log.getStackTraceString(e));
							middleResult.exception = e;
						}
						return middleResult;
					}
				})
				.post(new PostWork<MiddleResult<Result>>() {
					@Override
					public void onPostExecute(MiddleResult<Result> middleResult) {
						if (middleResult.exception == null && middleResult.result != null) {
							if (mSuccessListener != null) {
								mSuccessListener.onSuccess(middleResult.result);
							}
						} else {
							if (mErrorListener == null) {
								return;
							}
							mErrorListener.onFail(ExampleApiError.PARSE_DATA_ERROR, "");
						}
					}
				})
				.start();
	}

	protected abstract Result parseResult(Gson gson, String result) throws ApiException;

	@Override
	public void onRequestFail(int cause, String errorMessage) {
		if (mErrorListener == null) {
			return;
		}
		int errorCode;
		switch (cause) {
			case RequestError.NETWORK_NOT_AVAILABLE:
				errorCode = ExampleApiError.NETWORK_NOT_AVAILABLE;
				break;
			case RequestError.TIMEOUT_ERROR:
				errorCode = ExampleApiError.REQUEST_TIMEOUT;
				break;
			case RequestError.DECODE_ERROR:
			case RequestError.UNKNOWN_SERVER_ERROR:
			default:
				errorCode = ExampleApiError.UNKNOWN_SERVER_ERROR;
		}
		mErrorListener.onFail(errorCode, errorMessage);
	}

	@Override
	public String getRequestBody() {
		return null;
	}

	@Override
	public String getContentType() {
		return "application/json";
	}

	@Override
	public void getHeaders(Map<String, String> headerMap) {}

	@Override
	public void getParameter(Map<String, String> parameterMap) {}

	@Override
	public int getTimeout() {
		return 10 * 1000;
	}

	@Override
	public int getRetryCount() {
		return 2;
	}

	@Override
	@Nullable
	public ApiCipher getApiCipher() {
		return null;
	}

	@Override
	public boolean isRequestBodyEncrypt() {
		return false;
	}

	@Override
	public boolean isResponseBodyDecrypt() {
		return false;
	}

	@SuppressWarnings("unchecked")
	public SubClass success(ApiSuccessListener<Result> successListener) {
		mSuccessListener = successListener;
		return (SubClass) this;
	}

	@SuppressWarnings("unchecked")
	public SubClass fail(ApiErrorListener errorListener) {
		mErrorListener = errorListener;
		return (SubClass) this;
	}

	public SubClass start() {
		return start(this);
	}

	@SuppressWarnings("unchecked")
	public SubClass start(Object tag) {
		sExecutor.request(this, tag);
		return (SubClass) this;
	}

	@SuppressWarnings("unchecked")
	public SubClass cancel() {
		sExecutor.cancel(this);
		return (SubClass) this;
	}

	@SuppressWarnings("unchecked")
	public SubClass cancel(Object tag) {
		sExecutor.cancel(tag);
		return (SubClass) this;
	}

	interface ApiSuccessListener<T> {
		void onSuccess(T response);
	}

	interface ApiErrorListener {
		void onFail(int errorType, String message);
	}

	private class MiddleResult<T> {
		T result;
		Exception exception;
	}
}
