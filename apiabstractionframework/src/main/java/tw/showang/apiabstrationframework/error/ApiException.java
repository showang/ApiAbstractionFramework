package tw.showang.apiabstrationframework.error;

public class ApiException extends Exception {

	public int cause;

	public ApiException(int cause, String message) {
		super(message);
		this.cause = cause;
	}

}
