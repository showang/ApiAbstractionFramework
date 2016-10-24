package tw.showang.apiabstractionframework.example.api.base;


import tw.showang.apiabstrationframework.logger.Logger;

public class UnitTestLogger implements Logger {
	@Override
	public void e(String message) {
		print("[error] " + message);
	}

	@Override
	public void e(String tag, String message) {
		print("[error] (" + tag + ") " + message);
	}

	@Override
	public void i(String message) {
		print("[info] " + message);
	}

	@Override
	public void i(String tag, String message) {
		print("[info] (" + tag + ") " + message);
	}

	@Override
	public void w(String message) {
		print("[warn] " + message);
	}

	@Override
	public void w(String tag, String message) {
		print("[warn] (" + tag + ") " + message);
	}

	@Override
	public void d(String message) {
		print("[debug] " + message);
	}

	@Override
	public void d(String tag, String message) {
		print("[debug] (" + tag + ") " + message);
	}

	@Override
	public void v(String message) {
		print("[verbose] " + message);
	}

	@Override
	public void v(String tag, String message) {
		print("[verbose] (" + tag + ") " + message);
	}

	@Override
	public void wtf(String message) {
		print("[wtf] " + message);
	}

	@Override
	public void wtf(String tag, String message) {
		print("[wtf] (" + tag + ") " + message);
	}

	private void print(String msg) {
		System.out.println(msg);
	}
}
