package tw.showang.apiabstrationframework.error;

public class RequestException extends Exception {

	public int errorCode;

	public RequestException(int errorCode, String message) {
		super(message);
		this.errorCode = errorCode;
	}

	public RequestException(int errorCode, Throwable cause) {
		super(cause);
		this.errorCode = errorCode;
	}

}
