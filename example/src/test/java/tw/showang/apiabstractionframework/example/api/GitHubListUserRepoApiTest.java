package tw.showang.apiabstractionframework.example.api;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

import java.util.List;

import tw.showang.apiabstractionframework.example.api.GitHubGetAllRepoBySinceApi.GetRepoResult;
import tw.showang.apiabstractionframework.example.api.base.ApiTestBase;
import tw.showang.apiabstractionframework.example.api.base.ExampleApiBase.ApiErrorListener;
import tw.showang.apiabstractionframework.example.api.base.ExampleApiBase.ApiSuccessListener;

@RunWith(RobolectricTestRunner.class)
public class GitHubListUserRepoApiTest extends ApiTestBase {

	@Test
	public void testRequest() {
		new GitHubListUserRepoApi()
				.success(new ApiSuccessListener<List<GetRepoResult>>() {
					@Override
					public void onSuccess(List<GetRepoResult> response) {
						System.out.println("Success");
						for (GetRepoResult repoInfo : response) {
							System.out.println("Reop Name: " + repoInfo.fullName);
						}
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
