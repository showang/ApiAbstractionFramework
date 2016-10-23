package tw.showang.apiabstrationframework.logger;

public interface Logger {

	void e(String message);

	void e(String tag, String message);

	void i(String message);

	void i(String tag, String message);

	void w(String message);

	void w(String tag, String message);

	void d(String message);

	void d(String tag, String message);

	void v(String message);

	void v(String tag, String message);

	void wtf(String message);

	void wtf(String tag, String message);

}
