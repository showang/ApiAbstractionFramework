package tw.showang.apiabstrationframework;

import java.util.Map;

public interface Api<T> {

	class Priority {
		public static final int LOW = 0;
		public static final int NORMAL = 1;
		public static final int HIGH = 2;
		public static final int IMMEDIATE = 3;
	}

	int getPriority();

	String getProtocol();

	int getHttpMethod();

	String getDomainName();

	String getUrl();

	String getRequestBody();

	String getContentType();

	void getHeaders(Map<String, String> headerMap);

	void getParameter(Map<String, String> parameterMap);

	Api success(ApiListener<T> listener);

	Api fail(ApiErrorListener listener);

	void onRequestSuccess(String result);

	void onRequestFail(int cause, String errorMessage);

	int getTimeout();

	int getRetryCount();

	ApiCipher getApiCipher();

	boolean isRequestBodyEncrypt();

	boolean isResponseBodyDecrypt();

	interface ApiListener<T> {
		void onSuccess(T response);
	}

	interface ApiErrorListener {
		void onFail(int errorType, String message);
	}

}
