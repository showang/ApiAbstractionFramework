package tw.showang.apiabstractionframework.example;

import android.app.Application;

import tw.showang.apiabstractionframework.example.api.base.ExampleApiBase;
import tw.showang.apiabstrationframework.logger.Logger;
import tw.showang.apiabstrationframework.logger.internal.AndroidLogger;
import tw.showang.apiabstrationframework.support.volley.VolleyRequestExecutor;

public class ExampleApp extends Application {

	@Override
	public void onCreate() {
		super.onCreate();
		Logger logger = new AndroidLogger("Example");
		ExampleApiBase.init(getResources().getString(R.string.github_token), new VolleyRequestExecutor(getApplicationContext(), logger), logger);
	}
}
