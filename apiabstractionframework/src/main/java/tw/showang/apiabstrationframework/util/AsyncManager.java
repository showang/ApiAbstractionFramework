package tw.showang.apiabstrationframework.util;

import android.os.AsyncTask;

public class AsyncManager<BackgroundResult> {

	private boolean mIsAsync = true;
	private AsyncWork<BackgroundResult> mAsyncWork;
	private PostWork<BackgroundResult> mPostWork;

	public AsyncManager<BackgroundResult> setAsync(boolean isAsync) {
		mIsAsync = isAsync;
		return this;
	}

	public AsyncManager<BackgroundResult> background(AsyncWork<BackgroundResult> work) {
		mAsyncWork = work;
		return this;
	}

	public AsyncManager<BackgroundResult> post(PostWork<BackgroundResult> work) {
		mPostWork = work;
		return this;
	}

	public void start() {
		if (mAsyncWork == null) {
			throw new IllegalStateException("Plz init background work before start.");
		}
		if (mIsAsync) {
			doAsync();
		} else {
			doSync();
		}
	}

	private void doSync() {
		BackgroundResult result = mAsyncWork.doInBackground();
		if (mPostWork != null) {
			mPostWork.onPostExecute(result);
		}
	}

	private void doAsync() {
		new AsyncTask<Void, Void, BackgroundResult>() {
			@Override
			protected BackgroundResult doInBackground(Void... params) {
				return mAsyncWork.doInBackground();
			}

			@Override
			protected void onPostExecute(BackgroundResult backgroundResult) {
				super.onPostExecute(backgroundResult);
				if (mPostWork != null) {
					mPostWork.onPostExecute(backgroundResult);
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
