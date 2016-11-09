package tw.showang.apiabstractionframework.example;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import java.util.List;

import tw.showang.apiabstractionframework.example.api.ExampleApiBase.ApiErrorListener;
import tw.showang.apiabstractionframework.example.api.ExampleApiBase.ApiSuccessListener;
import tw.showang.apiabstractionframework.example.api.GetCommentsApi;
import tw.showang.apiabstractionframework.example.api.GetCommentsApi.GetCommentsResult;

public class MainActivity extends AppCompatActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		new GetCommentsApi()
				.success(new ApiSuccessListener<List<GetCommentsResult>>() {
					@Override
					public void onSuccess(List<GetCommentsResult> response) {
						Log.e("GetCommentsApi", "onSuccess");
					}
				})
				.fail(new ApiErrorListener() {
					@Override
					public void onFail(int errorType, String message) {
						Log.e("GetCommentsApi", "onFail");
					}
				})
				.start();
	}
}
