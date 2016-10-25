package tw.showang.apiabstractionframework.example.api;

public class ApiException extends Exception {

	public int apiErrorCode;

	public ApiException(int errorCode, String message) {
		super(message);
		this.apiErrorCode = errorCode;
	}

}
