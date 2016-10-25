package tw.showang.apiabstrationframework.support.okhttp;


import android.support.v4.util.ArrayMap;
import android.util.Log;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.FormBody;
import okhttp3.Headers;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;
import tw.showang.apiabstrationframework.Api;
import tw.showang.apiabstrationframework.Api.HttpMethod;
import tw.showang.apiabstrationframework.ApiCipher;
import tw.showang.apiabstrationframework.RequestExecutor;
import tw.showang.apiabstrationframework.error.ApiCipherException;
import tw.showang.apiabstrationframework.error.RequestError;
import tw.showang.apiabstrationframework.error.RequestException;
import tw.showang.apiabstrationframework.logger.Logger;
import tw.showang.apiabstrationframework.util.AsyncManager;
import tw.showang.apiabstrationframework.util.AsyncManager.AsyncWork;
import tw.showang.apiabstrationframework.util.AsyncManager.PostWork;

public class OkHttp3RequestExecutor implements RequestExecutor {

	private final String CONTENT_TYPE_JSON;
	private final String URL_CHAR_ENCODE;
	private final String AND;
	private final String EQUAL;
	private final String QUESTION;

	private OkHttpClient mOkHttpClient;
	private Logger mLogger;
	private boolean mIsAsync;

	public OkHttp3RequestExecutor(OkHttpClient client, Logger logger) {
		mOkHttpClient = client;
		mLogger = logger;
		CONTENT_TYPE_JSON = "application/json";
		URL_CHAR_ENCODE = "utf-8";
		AND = "&";
		EQUAL = "=";
		QUESTION = "?";
	}

	public OkHttp3RequestExecutor setAsync(boolean isAsync) {
		mIsAsync = isAsync;
		return this;
	}

	@Override
	public void request(final Api api, final Object tag) {
		new AsyncManager<RequestResult>()
				.setAsync(mIsAsync)
				.background(new AsyncWork<RequestResult>() {
					@Override
					public RequestResult doInBackground() {
						Request request = createRequest(api, tag);
						RequestResult result = new RequestResult();
						try {
							result.response = doRequest(request, api);
						} catch (IOException e) {
							e.printStackTrace();
							result.exception = new RequestException(RequestError.NETWORK_NOT_AVAILABLE, e);
						}
						if (result.response != null) {
							try {
								result.decodedBody = decodeBody(api, result);
							} catch (Exception e) {
								e.printStackTrace();
								result.exception = new RequestException(RequestError.DECODE_ERROR, e);
							}
						}
						return result;
					}
				})
				.post(new PostWork<RequestResult>() {
					@Override
					public void onPostExecute(RequestResult response) {
						processResult(api, response);
					}
				})
				.start();
	}

	private String decodeBody(Api api, RequestResult result) throws Exception {
		Response response = result.response;
		ApiCipher cipher = api.getApiCipher();
		ResponseBody responseBody = response.body();
		if (responseBody.contentLength() == 0) {
			return null;
		}
		return api.isResponseBodyDecrypt() && cipher != null ?
				new String(cipher.decode(responseBody.bytes()), URL_CHAR_ENCODE) : responseBody.string();
	}

	private void processResult(Api api, RequestResult result) {
		if (result.exception == null) {
			Response response = result.response;
			int responseCode = response.code();
			if (responseCode >= 200 && responseCode < 300) {
				api.onRequestSuccess(result.decodedBody);
			} else if (responseCode == 408) {
				api.onRequestFail(RequestError.TIMEOUT_ERROR, "408 timeout.");
			} else {
				System.out.println("responseCode: " + responseCode + " - " + result.decodedBody);
				api.onRequestFail(RequestError.UNKNOWN_SERVER_ERROR, "");
			}
		} else {
			mLogger.e(Log.getStackTraceString(result.exception));
			api.onRequestFail(result.exception.errorCode, "Exception.");
		}
	}

	private Request createRequest(Api api, Object tag) {
		Request.Builder requestBuilder = new Request.Builder();
		String url = api.getUrl();
		switch (api.getHttpMethod()) {
			case HttpMethod.GET:
				requestBuilder.get();
				url = getParameterUrlString(api, url);
				break;
			case HttpMethod.POST:
				requestBuilder.post(createRequestBody(api));
				break;
			case HttpMethod.PUT:
				requestBuilder.put(createRequestBody(api));
				break;
			case HttpMethod.DELETE:
				requestBuilder.delete(createRequestBody(api));
				break;
			case HttpMethod.HEAD:
				requestBuilder.head();
				break;
			case HttpMethod.PATCH:
				requestBuilder.patch(createRequestBody(api));
				break;
			case HttpMethod.OPTIONS:
			default:
				throw new RuntimeException("Not support yet.");
		}
		Map<String, String> headers = new ArrayMap<>();
		api.getHeaders(headers);
		mLogger.i("Request: " + url);
		return requestBuilder
				.url(url)
				.tag(tag)
				.headers(Headers.of(headers))
				.build();
	}

	private RequestBody createRequestBody(Api api) {
		if (api.getContentType().toLowerCase().contains(CONTENT_TYPE_JSON)) {
			ApiCipher cipher = api.getApiCipher();
			byte[] bodyBytes = api.getRequestBody().getBytes();
			try {
				bodyBytes = cipher != null ? cipher.encode(bodyBytes) : bodyBytes;
			} catch (ApiCipherException e) {
				e.printStackTrace();
			}
			return RequestBody.create(MediaType.parse(api.getContentType()), bodyBytes);
		} else {
			FormBody.Builder builder = new FormBody.Builder();
			Map<String, String> paraMap = new ArrayMap<>();
			api.getParameter(paraMap);
			for (String key : paraMap.keySet()) {
				builder.add(key, paraMap.get(key));
			}
			return builder.build();
		}
	}

	private Response doRequest(Request request, Api api) throws IOException {
		Map<String, String> headers = new ArrayMap<>();
		api.getHeaders(headers);
		return mOkHttpClient.newBuilder().readTimeout(api.getTimeout(), TimeUnit.MILLISECONDS)
				.build().newCall(request).execute();
	}

	private String getParameterUrlString(Api api, String baseUrl) {
		String url = baseUrl;
		Map<String, String> paramsMap = new ArrayMap<>();
		api.getParameter(paramsMap);
		if (paramsMap.size() == 0) {
			return url;
		}
		if (!url.contains(QUESTION)) {
			url += QUESTION;
		} else if (!url.endsWith(AND)) {
			url += AND;
		}
		try {
			boolean isFirstParam = true;
			for (String key : paramsMap.keySet()) {
				url += (isFirstParam ? "" : AND) +
						key + EQUAL + URLEncoder.encode(paramsMap.get(key), URL_CHAR_ENCODE);
				isFirstParam = false;
			}
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return url;
	}

	@Override
	public void cancel(Object tag) {
		for (Call call : mOkHttpClient.dispatcher().queuedCalls()) {
			if (call.request().tag().equals(tag)) {
				call.cancel();
			}
		}
		for (Call call : mOkHttpClient.dispatcher().runningCalls()) {
			if (call.request().tag().equals(tag)) {
				call.cancel();
			}
		}
	}

	@Override
	public void cancel(Api api) {
		cancel((Object) api);
	}

	private class RequestResult {
		Response response;
		RequestException exception;
		String decodedBody;
	}

}
