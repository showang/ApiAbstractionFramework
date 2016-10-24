package tw.showang.apiabstractionframework.example.api.base;


import android.support.v4.util.ArrayMap;
import android.util.Log;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import okhttp3.FormBody;
import okhttp3.Headers;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import tw.showang.apiabstrationframework.Api;
import tw.showang.apiabstrationframework.Api.HttpMethod;
import tw.showang.apiabstrationframework.ApiCipher;
import tw.showang.apiabstrationframework.RequestExecutor;
import tw.showang.apiabstrationframework.error.ApiCipherException;
import tw.showang.apiabstrationframework.error.RequestError;
import tw.showang.apiabstrationframework.logger.Logger;

class OkHttp3SyncRequestExecutor implements RequestExecutor {
	private OkHttpClient okHttpClient;
	private Logger logger;

	OkHttp3SyncRequestExecutor(OkHttpClient client, Logger logger) {
		okHttpClient = client;
		this.logger = logger;
	}

	@Override
	public void request(Api api, Object tag) {
		Request.Builder requestBuilder = new Request.Builder();
		switch (api.getHttpMethod()) {
			case HttpMethod.GET:
				requestBuilder.get();
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
			default:
				throw new RuntimeException("Not support yet.");
		}
		doRequest(requestBuilder, api);

	}

	private RequestBody createRequestBody(Api api) {
		if (api.getContentType().contains("json")) {
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

	private void doRequest(Request.Builder builder, Api api) {
		Map<String, String> headers = new ArrayMap<>();
		api.getHeaders(headers);
		String url = api.getHttpMethod() == com.android.volley.Request.Method.GET ? getGetUrlString(api) : api.getUrl();
		logger.i("Request: " + url);
		Request request = builder
				.url(url)
				.headers(Headers.of(headers))
				.build();
		try {
			Response response = okHttpClient.newBuilder().readTimeout(api.getTimeout(), TimeUnit.MILLISECONDS).build().newCall(request).execute();
			ApiCipher cipher = api.getApiCipher();
			String body = cipher != null ? new String(cipher.decode(response.body().bytes()), "UTF-8") : response.body().string();
			int responseCode = response.code();
			if (responseCode <= 200) {
				api.onRequestSuccess(body);
			} else if (responseCode == 408) {
				api.onRequestFail(RequestError.TIMEOUT_ERROR, "408 timeout.");
			} else {
				byte[] responseBody = response.body().bytes();
				String errorMessage = new String(cipher == null ? responseBody : cipher.decode(responseBody), "UTF-8");
				System.out.println("responseCode: " + responseCode + " - " + errorMessage);
				api.onRequestFail(RequestError.SERVER_ERROR, "");
			}
		} catch (IOException | ApiCipherException e) {
			System.out.println(Log.getStackTraceString(e));
			api.onRequestFail(RequestError.DECODE_ERROR, "Exception.");
		}
	}

	private String getGetUrlString(Api api) {
		String url = api.getUrl();
		if (!url.contains("?")) {
			url += "?";
		} else if (!url.endsWith("&")) {
			url += "&";
		}
		Map<String, String> paramsMap = new ArrayMap<>();
		api.getParameter(paramsMap);
		try {
			for (String key : paramsMap.keySet()) {
				url += key + "=" + URLEncoder.encode(paramsMap.get(key), "UTF-8") + "&";
			}
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}

		return url;
	}

	@Override
	public void cancel(Object tag) {

	}

	@Override
	public void cancel(Api api) {

	}

}
