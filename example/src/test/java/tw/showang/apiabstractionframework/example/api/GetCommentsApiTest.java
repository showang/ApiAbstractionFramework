package tw.showang.apiabstractionframework.example.api;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

import java.util.List;

import tw.showang.apiabstractionframework.example.api.ExampleApiBase.ApiErrorListener;
import tw.showang.apiabstractionframework.example.api.ExampleApiBase.ApiSuccessListener;
import tw.showang.apiabstractionframework.example.api.GetCommentsApi.GetCommentsResult;
import tw.showang.apiabstractionframework.example.api.base.ApiTestBase;

@RunWith(RobolectricTestRunner.class)
public class GetCommentsApiTest extends ApiTestBase {

	@Test
	public void testRequest() {
		new GetCommentsApi()
				.success(new ApiSuccessListener<List<GetCommentsResult>>() {
					@Override
					public void onSuccess(List<GetCommentsResult> response) {
						checkRequest(response);
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

	private void checkRequest(List<GetCommentsResult> response) {
		Assert.assertNotNull(response);
		Assert.assertTrue(response.size() != 0);
		GetCommentsResult resultItem = response.get(0);
		Assert.assertNotNull(resultItem);
		Assert.assertNotNull(resultItem.name);
		Assert.assertNotNull(resultItem.email);
		Assert.assertNotNull(resultItem.body);
	}

}
