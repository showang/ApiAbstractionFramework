package tw.showang.apiabstrationframework;

public interface RequestExecutor {

	void request(Api api, Object tag);

	void cancel(Object tag);

	void cancel(Api api);

}
