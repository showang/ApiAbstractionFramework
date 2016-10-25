package tw.showang.apiabstractionframework.example.api;

import com.google.gson.Gson;

import java.util.Map;

import tw.showang.apiabstrationframework.Api;
import tw.showang.apiabstrationframework.ApiCipher;
import tw.showang.apiabstrationframework.RequestExecutor;
import tw.showang.apiabstrationframework.error.ApiException;
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
		new AsyncManager<Result>()
				.background(new AsyncWork<Result>() {
					@Override
					public Result doInBackground() {
						Result parsedResult = null;
						try {
							parsedResult = parseResult(sGson, result);
						} catch (ApiException e) {
							e.printStackTrace();
						}
						return parsedResult;
					}
				})
				.post(new PostWork<Result>() {
					@Override
					public void onPostExecute(Result result) {
						if (result != null && mSuccessListener != null) {
							mSuccessListener.onSuccess(result);
						} else {

						}
					}
				})
				.start();
	}

	protected abstract Result parseResult(Gson gson, String result) throws ApiException;


	@Override
	public void onRequestFail(int cause, String errorMessage) {

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
}
