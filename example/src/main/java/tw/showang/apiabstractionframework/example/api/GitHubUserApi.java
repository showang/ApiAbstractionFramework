package tw.showang.apiabstractionframework.example.api;

import com.google.gson.Gson;

import tw.showang.apiabstractionframework.example.api.base.ApiException;
import tw.showang.apiabstractionframework.example.api.base.ExampleApiBase;

public class GitHubUserApi extends ExampleApiBase<GitHubUserApi, String> {
	@Override
	protected String parseResult(Gson gson, String result) throws ApiException {
		System.out.println(result);
		return result;
	}

	@Override
	public int getHttpMethod() {
		return HttpMethod.GET;
	}

	@Override
	public String getUrl() {
		return getBaseUri() + "/user";
	}
}
