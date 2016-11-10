package tw.showang.apiabstractionframework.example.api;

import com.google.gson.Gson;

import tw.showang.apiabstractionframework.example.api.base.ApiException;
import tw.showang.apiabstractionframework.example.api.base.ExampleApiBase;

public class GitHubDeleteRepoApi extends ExampleApiBase<GitHubDeleteRepoApi, String> {

	private String ownerName = "showang";
	private String repoName = "Test-api-post-example.";

	@Override
	protected String parseResult(Gson gson, String result) throws ApiException {
		return result;
	}

	@Override
	public int getHttpMethod() {
		return HttpMethod.DELETE;
	}

	@Override
	public String getUrl() {
		return getBaseUri() + "/repos/" + ownerName + "/" + repoName;
	}
}
