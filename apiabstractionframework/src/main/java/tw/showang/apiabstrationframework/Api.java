package tw.showang.apiabstrationframework;

import java.util.Map;

public interface Api {

	int getPriority();

	String getProtocol();

	int getHttpMethod();

	String getDomainName();

	String getUrl();

	byte[] getRequestBody();

	String getContentType();

	void getHeaders(Map<String, String> headerMap);

	void getParameter(Map<String, String> parameterMap);

	void onRequestSuccess(byte[] result);

	void onRequestFail(int cause, String errorMessage);

	int getTimeout();

	int getRetryCount();

	ApiCipher getApiCipher();

	boolean isRequestBodyEncrypt();

	boolean isResponseBodyDecrypt();

	class HttpMethod {
		public static final int GET = 0;
		public static final int POST = 1;
		public static final int PUT = 2;
		public static final int DELETE = 3;
		public static final int HEAD = 4;
		public static final int OPTIONS = 5;
		public static final int TRACE = 6;
		public static final int PATCH = 7;
	}

	class Priority {
		public static final int LOW = 0;
		public static final int NORMAL = 1;
		public static final int HIGH = 2;
		public static final int IMMEDIATE = 3;
	}

	class Protocol {
		public static final String HTTP = "http://";
		public static final String HTTPS = "https://";
	}
}
