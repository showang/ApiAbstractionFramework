package tw.showang.apiabstrationframework.error;

public class ApiException extends Exception {

	public int mErrorCode;

	public ApiException(int errorCode, String message) {
		super(message);
		mErrorCode = errorCode;
	}

	public ApiException(int errorCode, Throwable cause) {
		super(cause);
		mErrorCode = errorCode;
	}

}
