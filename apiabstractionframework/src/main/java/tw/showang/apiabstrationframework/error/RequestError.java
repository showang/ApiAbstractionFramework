package tw.showang.apiabstrationframework.error;

public class RequestError {
	public static final int NETWORK_NOT_AVAILABLE = Integer.MAX_VALUE - 1;
	public static final int SERVER_ERROR = Integer.MAX_VALUE - 2;
	public static final int TIMEOUT_ERROR = Integer.MAX_VALUE - 3;
	public static final int DECODE_ERROR = Integer.MAX_VALUE - 4;
}
