package tw.showang.apiabstractionframework.example.api;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;

import tw.showang.apiabstractionframework.example.api.base.ApiException;
import tw.showang.apiabstractionframework.example.api.base.ExampleApiBase;

public class GitHubPatchUserApi extends ExampleApiBase<GitHubPatchUserApi, String> {
	@Override
	protected String parseResult(Gson gson, String result) throws ApiException {
		return null;
	}

	@Override
	public int getHttpMethod() {
		return HttpMethod.PATCH;
	}

	@Override
	protected String getBody() {
		PostEntity entity = new PostEntity();
		entity.name = "William Wang";
		entity.blog = "https://www.facebook.com/gu.banzou";
		entity.bio = "Good Philosopher";
		entity.email = "showang730@gmail.com";
		entity.company = "KKBOX";
		entity.location = "Taiwan";
		entity.isHireable = true;
		return getGson().toJson(entity);
	}

	@Override
	public String getUrl() {
		return getBaseUri() + "/user";
	}

	private class PostEntity {

		@SerializedName("name")
		String name;
		@SerializedName("email")
		String email;
		@SerializedName("blog")
		String blog;
		@SerializedName("company")
		String company;
		@SerializedName("location")
		String location;
		@SerializedName("bio")
		String bio;
		@SerializedName("hireable")
		boolean isHireable;

	}
}
