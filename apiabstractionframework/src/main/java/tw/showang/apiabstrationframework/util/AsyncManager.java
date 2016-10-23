package tw.showang.apiabstrationframework.util;

import android.os.AsyncTask;

public class AsyncManager<BackgroundResult> {

	private static boolean isAsync = true;
	private AsyncWork<BackgroundResult> asyncWork;
	private PostWork<BackgroundResult> postWork;

	public static void setIsAsync(boolean isAsync) {
		AsyncManager.isAsync = isAsync;
	}

	public AsyncManager<BackgroundResult> background(AsyncWork<BackgroundResult> work) {
		asyncWork = work;
		return this;
	}

	public AsyncManager<BackgroundResult> post(PostWork<BackgroundResult> work) {
		postWork = work;
		return this;
	}

	public void start() {
		if (asyncWork == null) {
			throw new IllegalStateException("Plz init background work before start.");
		}
		if (isAsync) {
			doAsync();
		} else {
			doSync();
		}
	}

	private void doSync() {
		BackgroundResult result = asyncWork.doInBackground();
		if (postWork != null) {
			postWork.onPostExecute(result);
		}
	}

	private void doAsync() {
		new AsyncTask<Void, Void, BackgroundResult>() {
			@Override
			protected BackgroundResult doInBackground(Void... params) {
				return asyncWork.doInBackground();
			}

			@Override
			protected void onPostExecute(BackgroundResult backgroundResult) {
				super.onPostExecute(backgroundResult);
				if (postWork != null) {
					postWork.onPostExecute(backgroundResult);
				}
			}
		}.execute();
	}

	public interface AsyncWork<BackgroundResult> {
		BackgroundResult doInBackground();
	}

	public interface PostWork<BackgroundResult> {
		void onPostExecute(BackgroundResult result);
	}
}
