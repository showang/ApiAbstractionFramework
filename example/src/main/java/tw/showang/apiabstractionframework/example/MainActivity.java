package tw.showang.apiabstractionframework.example;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import tw.showang.apiabstractionframework.example.api.GitHubUserApi;
import tw.showang.apiabstractionframework.example.api.base.ExampleApiBase.ApiErrorListener;
import tw.showang.apiabstractionframework.example.api.base.ExampleApiBase.ApiSuccessListener;

public class MainActivity extends AppCompatActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		new GitHubUserApi()
				.success(new ApiSuccessListener<String>() {
					@Override
					public void onSuccess(String response) {
						Log.e("TEST", "onSuccess: " + response);
					}
				})
				.fail(new ApiErrorListener() {
					@Override
					public void onFail(int errorType, String message) {
						Log.e("TEST", "onFail");
					}
				})
				.start();
	}
}
