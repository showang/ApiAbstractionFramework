package tw.showang.apiabstractionframework.example.api;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;

import tw.showang.apiabstractionframework.example.api.GitHubGetAllRepoBySinceApi.GetRepoResult;
import tw.showang.apiabstractionframework.example.api.base.ApiException;
import tw.showang.apiabstractionframework.example.api.base.ExampleApiBase;

/**
 * https://developer.github.com/v3/repos/#list-all-public-repositories
 */
public class GitHubGetAllRepoBySinceApi extends ExampleApiBase<GitHubGetAllRepoBySinceApi, List<GetRepoResult>> {

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
	public void getParameter(Map<String, String> parameterMap) {
		parameterMap.put("since", "364");
	}

	@Override
	public String getUrl() {
		return getBaseUri() + "/repositories";
	}

	public class GetRepoResult {
		@SerializedName("id")
		int id;
		@SerializedName("name")
		String name;
		@SerializedName("full_name")
		String fullName;
		@SerializedName("description")
		String description;
		@SerializedName("private")
		boolean isPrivate;
		@SerializedName("fork")
		boolean isFork;
		@SerializedName("url")
		String url;
		@SerializedName("html_url")
		String html_url;
	}
}
