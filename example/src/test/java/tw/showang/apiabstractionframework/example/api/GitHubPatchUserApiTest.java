package tw.showang.apiabstractionframework.example.api;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

import tw.showang.apiabstractionframework.example.api.base.ApiTestBase;
import tw.showang.apiabstractionframework.example.api.base.ExampleApiBase.ApiErrorListener;
import tw.showang.apiabstractionframework.example.api.base.ExampleApiBase.ApiSuccessListener;

@RunWith(RobolectricTestRunner.class)
public class GitHubPatchUserApiTest extends ApiTestBase {

	@Test
	public void testRequestData() {
		new GitHubPatchUserApi()
				.success(new ApiSuccessListener<String>() {
					@Override
					public void onSuccess(String response) {
						System.out.println("Success: " + response);
					}
				})
				.fail(new ApiErrorListener() {
					@Override
					public void onFail(int errorType, String message) {
						onRequestFail(errorType, message);
					}
				})
				.start();
	}

}
