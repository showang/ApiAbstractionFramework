package tw.showang.apiabstractionframework.example.api;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;

import tw.showang.apiabstractionframework.example.api.GetCommentsApi.GetCommentsResult;

public class GetCommentsApi extends ExampleApiBase<GetCommentsApi, List<GetCommentsResult>> {

	@Override
	protected List<GetCommentsResult> parseResult(Gson gson, String result) throws ApiException {
		Type type = new TypeToken<List<GetCommentsResult>>() {
		}.getType();
		List<GetCommentsResult> apiResult = gson.fromJson(result, type);
		if (apiResult.size() == 0) {
			throw new ApiException(ExampleApiError.RESULT_EMPTY, "Empty Result");
		}
		return apiResult;
	}

	@Override
	public int getHttpMethod() {
		return HttpMethod.GET;
	}

	@Override
	public String getUrl() {
		return getBaseUri() + "/comments";
	}

	@Override
	public void getParameter(Map<String, String> parameterMap) {
		parameterMap.put("postId", "1");
	}


	public class GetCommentsResult {
		@SerializedName("postId")
		public int postId;
		@SerializedName("id")
		public int id;
		@SerializedName("name")
		public String name;
		@SerializedName("email")
		public String email;
		@SerializedName("body")
		public String body;
	}

}
