package tw.showang.apiabstractionframework.example.api.base;


import android.os.Build;

import junit.framework.Assert;

import org.junit.BeforeClass;
import org.robolectric.annotation.Config;

import okhttp3.OkHttpClient;
import tw.showang.apiabstractionframework.example.api.ExampleApiBase;
import tw.showang.apiabstractionframework.example.api.ExampleApiError;
import tw.showang.apiabstrationframework.RequestExecutor;
import tw.showang.apiabstrationframework.logger.Logger;
import tw.showang.apiabstrationframework.util.AsyncManager;

@Config(manifest = Config.NONE, sdk = Build.VERSION_CODES.LOLLIPOP)
public class ApiTestBase {

	@BeforeClass
	public static void setup() {
		Logger logger = new UnitTestLogger();
		AsyncManager.setIsAsync(false);
		RequestExecutor executor = new OkHttp3SyncRequestExecutor(new OkHttpClient(), logger);
		ExampleApiBase.init(executor, logger);
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
			case ExampleApiError.SERVER_ERROR:
				errorType = "UNKNOWN_SERVER_ERROR";
				break;
			case ExampleApiError.TIMEOUT_ERROR:
				errorType = "TIMEOUT_ERROR";
				break;
			case ExampleApiError.RESULT_EMPTY:
				errorType = "RESULT_EMPTY";
				break;
		}

		System.out.printf("[" + errorCode + "] " + errorType + ": " + message);
		Assert.assertTrue(false);
	}

}
