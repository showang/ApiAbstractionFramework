package tw.showang.apiabstractionframework.example;

import android.app.Application;

import tw.showang.apiabstractionframework.example.api.ExampleApiBase;
import tw.showang.apiabstrationframework.logger.Logger;
import tw.showang.apiabstrationframework.logger.internal.AndroidLogger;
import tw.showang.apiabstrationframework.support.volley.VolleyRequestExecutor;

/**
 * Created by williamwang on 2016/11/8.
 */

public class ExampleApp extends Application {

	@Override
	public void onCreate() {
		super.onCreate();
		Logger logger = new AndroidLogger("Example");
		ExampleApiBase.init(new VolleyRequestExecutor(getApplicationContext(), logger), logger);
	}
}
