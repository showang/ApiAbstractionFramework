package tw.showang.apiabstractionframework.example.api.base;


import android.os.Build;

import junit.framework.Assert;

import org.junit.BeforeClass;
import org.robolectric.annotation.Config;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

import okhttp3.OkHttpClient;
import tw.showang.apiabstrationframework.RequestExecutor;
import tw.showang.apiabstrationframework.logger.Logger;
import tw.showang.apiabstrationframework.support.okhttp.OkHttp3RequestExecutor;

@Config(manifest = Config.NONE, sdk = Build.VERSION_CODES.LOLLIPOP)
public class ApiTestBase {

	private static RequestExecutor executor;
	private static String YOUR_GITHUB_ACCESS_TOKEN;

	@BeforeClass
	public static void setup() {
		initToken();
		if (executor == null) {
			Logger logger = new UnitTestLogger();
			executor = new OkHttp3RequestExecutor(new OkHttpClient(), logger).setAsync(false);
			ExampleApiBase.init(YOUR_GITHUB_ACCESS_TOKEN, executor, logger);
		}
	}

	private static void initToken() {
		File file = new File("./../local.properties");
		if (YOUR_GITHUB_ACCESS_TOKEN == null && file.exists()) {
			Properties properties = new Properties();
			try {
				properties.load(new FileInputStream(file));
				YOUR_GITHUB_ACCESS_TOKEN = properties.getProperty("github.token", null);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		System.out.println("Token: " + YOUR_GITHUB_ACCESS_TOKEN);
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
