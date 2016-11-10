package tw.showang.apiabstractionframework.example.api;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.List;

import tw.showang.apiabstractionframework.example.api.GitHubGetAllRepoBySinceApi.GetRepoResult;
import tw.showang.apiabstractionframework.example.api.base.ApiException;
import tw.showang.apiabstractionframework.example.api.base.ExampleApiBase;

public class GitHubListUserRepoApi extends ExampleApiBase<GitHubListUserRepoApi, List<GetRepoResult>> {

	private String userName = "showang";

	@Override
	protected List<GetRepoResult> parseResult(Gson gson, String result) throws ApiException {
		Type type = new TypeToken<List<GetRepoResult>>() {
		}.getType();
		return gson.fromJson(result, type);
	}

	@Override
	public int getHttpMethod() {
		return HttpMethod.GET;
	}

	@Override
	public String getUrl() {
		return getBaseUri() + "/users/" + userName + "/repos";
	}
}
