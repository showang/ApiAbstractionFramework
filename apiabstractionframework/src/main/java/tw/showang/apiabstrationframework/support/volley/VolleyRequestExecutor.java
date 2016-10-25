package tw.showang.apiabstrationframework.support.volley;

import android.content.Context;
import android.support.v4.util.ArrayMap;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.NoConnectionError;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.Request.Method;
import com.android.volley.Request.Priority;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.HttpStack;
import com.android.volley.toolbox.JsonRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Map;

import okhttp3.OkHttpClient;
import tw.showang.apiabstrationframework.Api;
import tw.showang.apiabstrationframework.Api.HttpMethod;
import tw.showang.apiabstrationframework.ApiCipher;
import tw.showang.apiabstrationframework.BuildConfig;
import tw.showang.apiabstrationframework.RequestExecutor;
import tw.showang.apiabstrationframework.error.ApiCipherException;
import tw.showang.apiabstrationframework.error.RequestErrorCode;
import tw.showang.apiabstrationframework.logger.Logger;

import static tw.showang.apiabstrationframework.support.volley.VolleyRequestExecutor.ParameterOperations.AND;
import static tw.showang.apiabstrationframework.support.volley.VolleyRequestExecutor.ParameterOperations.EQUAL;
import static tw.showang.apiabstrationframework.support.volley.VolleyRequestExecutor.ParameterOperations.QUESTION;

public class VolleyRequestExecutor implements RequestExecutor {

	private static final String CONTENT_TYPE_JSON = "application/json";
	private static final String URL_CHAR_ENCODE = "utf-8";

	class ParameterOperations {
		static final String AND = "&";
		static final String EQUAL = "=";
		static final String QUESTION = "?";
	}

	private static final String DEBUG_SUCCESS_MESSAGE = BuildConfig.DEBUG ? " request success." : null;
	private static final String DEBUG_CONNECT_MESSAGE = BuildConfig.DEBUG ? "Connect API url " : null;
	private static final String DEBUG_REQUEST_ERROR_MESSAGE = BuildConfig.DEBUG ? "Request error " : null;
	private static final String DEBUG_ERROR_BODY_MESSAGE = BuildConfig.DEBUG ? " error with statusCode[%s]: " : null;

	private RequestQueue mRequestQueue;
	private Logger mLogger;

	public VolleyRequestExecutor(Context context, Logger logger) {
		this(context, logger, new OkHttp3Stack(new OkHttpClient()));
	}

	public VolleyRequestExecutor(Context context, Logger logger, HttpStack httpStack) {
		mRequestQueue = Volley.newRequestQueue(context, httpStack);
		mLogger = logger;
	}

	protected RequestQueue getRequestQueue() {
		return mRequestQueue;
	}

	@Override
	public void request(Api api, Object tag) {
		Request<String> request =
				isRequestByJson(api) ?
						createJsonStringRequest(api) :
						createStringRequest(api);
		request.setRetryPolicy(new DefaultRetryPolicy(api.getTimeout(), api.getRetryCount(), DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
		request.setTag(tag);
		mRequestQueue.add(request);
	}

	private Request<String> createJsonStringRequest(final Api api) {
		return new JsonRequest<String>(getVolleyMethodType(api.getHttpMethod()), api.getUrl(), api.getRequestBody(), new Response.Listener<String>() {

			@Override
			public void onResponse(String response) {
				api.onRequestSuccess(response);
			}
		}, new Response.ErrorListener() {
			@Override
			public void onErrorResponse(VolleyError error) {
				onResponseError(api, error);
			}
		}) {
			@Override
			public Priority getPriority() {
				return getVolleyPriority(api.getPriority());
			}

			@Override
			public byte[] getBody() {
				ApiCipher cipher = api.getApiCipher();
				String requestBody = api.getRequestBody();
				byte[] bodyBytes = requestBody.getBytes();
				try {
					return cipher != null && api.isRequestBodyEncrypt() ? cipher.encode(bodyBytes) : bodyBytes;
				} catch (ApiCipherException e) {
					mLogger.e("Request body encode error.");
				}
				return bodyBytes;
			}

			@Override
			public Map<String, String> getHeaders() throws AuthFailureError {
				Map<String, String> headerMap = new ArrayMap<>();
				api.getHeaders(headerMap);
				return headerMap;
			}

			@Override
			protected Response<String> parseNetworkResponse(NetworkResponse response) {
				try {
					String jsonString = new String(response.data,
							HttpHeaderParser.parseCharset(response.headers, PROTOCOL_CHARSET));
					return Response.success(jsonString,
							HttpHeaderParser.parseCacheHeaders(response));
				} catch (UnsupportedEncodingException e) {
					return Response.error(new ParseError(e));
				}
			}
		};
	}

	private boolean isRequestByJson(Api api) {
		String requestBody = api.getRequestBody();
		return api.getContentType().contains(CONTENT_TYPE_JSON) && requestBody != null && !requestBody.isEmpty();
	}

	private Request<String> createStringRequest(final Api api) {
		int method = getVolleyMethodType(api.getHttpMethod());
		String url = method == Request.Method.GET ? getGetUrlString(api) : api.getUrl();
		mLogger.i(DEBUG_CONNECT_MESSAGE + url);
		return new StringRequest(method, url, new Response.Listener<String>() {
			@Override
			public void onResponse(String response) {
				mLogger.i(api.getClass().getCanonicalName() + DEBUG_SUCCESS_MESSAGE);
				api.onRequestSuccess(response);
			}
		}, new Response.ErrorListener() {
			@Override
			public void onErrorResponse(VolleyError error) {
				onResponseError(api, error);
			}
		}) {
			@Override
			public Priority getPriority() {
				return getVolleyPriority(api.getPriority());
			}

			@Override
			protected Response<String> parseNetworkResponse(NetworkResponse response) {
				try {
					String result = parseResponseBody(api, response.data);
					return Response.success(result, HttpHeaderParser.parseCacheHeaders(response));
				} catch (IOException | ApiCipherException e) {
					mLogger.e(Log.getStackTraceString(e));
				}
				return super.parseNetworkResponse(response);
			}

			@Override
			public Map<String, String> getHeaders() throws AuthFailureError {
				Map<String, String> headerMap = new ArrayMap<>();
				api.getHeaders(headerMap);
				return headerMap;
			}

			@Override
			protected Map<String, String> getParams() throws AuthFailureError {
				Map<String, String> paramMap = new ArrayMap<>();
				api.getParameter(paramMap);
				return paramMap;
			}

		};
	}

	private String getGetUrlString(Api api) {
		String url = api.getUrl();
		boolean firstFlag = !url.contains(QUESTION);
		Map<String, String> paramsMap = new ArrayMap<>();
		api.getParameter(paramsMap);
		try {
			for (String key : paramsMap.keySet()) {
				if (firstFlag) {
					url += QUESTION;
					firstFlag = false;
				} else {
					url += AND;
				}
				url += key + EQUAL + URLEncoder.encode(paramsMap.get(key), URL_CHAR_ENCODE);
			}
		} catch (UnsupportedEncodingException e) {
			mLogger.e(Log.getStackTraceString(e));
		}

		return url;
	}

	private Priority getVolleyPriority(int priority) {
		switch (priority) {
			case Api.Priority.LOW:
				return Priority.LOW;
			case Api.Priority.HIGH:
				return Priority.HIGH;
			case Api.Priority.IMMEDIATE:
				return Priority.IMMEDIATE;
			case Api.Priority.NORMAL:
			default:
				return Priority.NORMAL;
		}
	}

	private int getVolleyMethodType(int httpMethod) {
		int volleyMethod = Method.GET;
		switch (httpMethod) {
			case HttpMethod.POST:
				volleyMethod = Method.POST;
				break;
			case HttpMethod.PUT:
				volleyMethod = Method.PUT;
				break;
			case HttpMethod.DELETE:
				volleyMethod = Method.DELETE;
				break;
			case HttpMethod.HEAD:
				volleyMethod = Method.HEAD;
				break;
			case HttpMethod.OPTIONS:
				volleyMethod = Method.OPTIONS;
				break;
			case HttpMethod.PATCH:
				volleyMethod = Method.PATCH;
				break;
			case HttpMethod.TRACE:
				volleyMethod = Method.TRACE;
		}
		return volleyMethod;
	}

	private void onResponseError(Api api, VolleyError volleyError) {
		mLogger.e(DEBUG_REQUEST_ERROR_MESSAGE + api.getUrl());
		if (volleyError == null) {
			api.onRequestFail(RequestErrorCode.SERVER_ERROR, "");
			return;
		}
		int statusCode = 0;
		String responseBody = null;
		if (volleyError instanceof TimeoutError) {
			api.onRequestFail(RequestErrorCode.TIMEOUT_ERROR, "");
		} else if (volleyError instanceof NoConnectionError) {
			api.onRequestFail(RequestErrorCode.NETWORK_NOT_AVAILABLE, "");
		} else {
			if (volleyError.networkResponse == null) {
				api.onRequestFail(RequestErrorCode.SERVER_ERROR, "");
				return;
			}
			statusCode = volleyError.networkResponse.statusCode;
			try {
				responseBody = parseResponseBody(api, volleyError.networkResponse.data);
			} catch (UnsupportedEncodingException e) {
				mLogger.e(Log.getStackTraceString(e));
			} catch (ApiCipherException e) {
				mLogger.e(Log.getStackTraceString(e));
				api.onRequestFail(RequestErrorCode.DECODE_ERROR, "Decode response body error.");
			}
			api.onRequestFail(RequestErrorCode.SERVER_ERROR, responseBody);
		}
		mLogger.e(api.getClass().getSimpleName() + String.format(DEBUG_ERROR_BODY_MESSAGE, statusCode) + responseBody);
	}

	private String parseResponseBody(Api api, byte[] body) throws UnsupportedEncodingException, ApiCipherException {
		ApiCipher cipher = api.getApiCipher();
		return new String(cipher != null && api.isResponseBodyDecrypt() ? cipher.decode(body) : body, URL_CHAR_ENCODE);
	}

	@Override
	public void cancel(Object tag) {
		mRequestQueue.cancelAll(tag);
	}

	@Override
	public void cancel(final Api api) {
		mRequestQueue.cancelAll(new RequestQueue.RequestFilter() {
			@Override
			public boolean apply(Request<?> request) {
				return api.getUrl().equals(request.getUrl());
			}
		});
	}

}
