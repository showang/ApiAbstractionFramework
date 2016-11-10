package tw.showang.apiabstractionframework.example.api;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;

import tw.showang.apiabstractionframework.example.api.base.ApiException;
import tw.showang.apiabstractionframework.example.api.base.ExampleApiBase;


public class GitHubCreateRepoApi extends ExampleApiBase<GitHubCreateRepoApi, String> {


	@Override
	protected String parseResult(Gson gson, String result) throws ApiException {
		System.out.println(result);
		return result;
	}

	@Override
	public int getHttpMethod() {
		return HttpMethod.POST;
	}

	@Override
	protected String getBody() {
		PostEntity entity = new PostEntity();
		entity.name = "Test-api-post-example.";
		entity.isAutoInit = true;
		entity.isPrivate = false;
		entity.gitIgnoreTemplate = "";
		return getGson().toJson(entity);
	}

	@Override
	public String getUrl() {
		return getBaseUri() + "/user/repos";
	}

	private class PostEntity {
		@SerializedName("name")
		String name;
		@SerializedName("auto_init")
		boolean isAutoInit;
		@SerializedName("private")
		boolean isPrivate;
		@SerializedName("gitignore_template")
		String gitIgnoreTemplate;
	}
}
