package tw.showang.apiabstractionframework.example.api.base;


import android.os.Build;

import junit.framework.Assert;

import org.junit.BeforeClass;
import org.robolectric.annotation.Config;

import okhttp3.OkHttpClient;
import tw.showang.apiabstrationframework.RequestExecutor;
import tw.showang.apiabstrationframework.logger.Logger;
import tw.showang.apiabstrationframework.support.okhttp.OkHttp3RequestExecutor;

@Config(manifest = Config.NONE, sdk = Build.VERSION_CODES.LOLLIPOP)
public class ApiTestBase {

	private static RequestExecutor executor;
	private static final String YOUR_GITHUB_ACCESS_TOKEN = "YOUR_GITHUB_ACCESS_TOKEN";

	@BeforeClass
	public static void setup() {
		if (executor == null) {
			Logger logger = new UnitTestLogger();
			executor = new OkHttp3RequestExecutor(new OkHttpClient(), logger).setAsync(false);
			ExampleApiBase.init(YOUR_GITHUB_ACCESS_TOKEN, executor, logger);
		}
	}

	protected void onRequestFail(int errorCode, String message) {
		String errorType = "UNKNOWN";
		switch (errorCode) {
			case ExampleApiError.NETWORK_NOT_AVAILABLE:
				errorType = "NETWORK_NOT_AVAILABLE";
				break;
			case ExampleApiError.PARSE_DATA_ERROR:
				errorType = "PARSE_API_FAIL";
				break;
			case ExampleApiError.UNKNOWN_SERVER_ERROR:
				errorType = "UNKNOWN_SERVER_ERROR";
				break;
			case ExampleApiError.REQUEST_TIMEOUT:
				errorType = "REQUEST_TIMEOUT";
				break;
			case ExampleApiError.RESULT_EMPTY:
				errorType = "RESULT_EMPTY";
				break;
		}

		System.out.printf("[" + errorCode + "] " + errorType + ": " + message);
		Assert.assertTrue(false);
	}

}
