package guru.qa.niffler.service.api;

import guru.qa.niffler.api.SpendApi;
import guru.qa.niffler.config.Config;
import guru.qa.niffler.model.CategoryJson;
import guru.qa.niffler.model.SpendJson;
import guru.qa.niffler.service.SpendClient;
import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;

import java.io.IOException;
import java.util.List;

import static org.apache.hc.core5.http.HttpStatus.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class SpendApiClient implements SpendClient {
  private static final Config CFG = Config.getInstance();

  private final OkHttpClient client = new OkHttpClient.Builder()
      .build();
  private final Retrofit retrofit = new Retrofit.Builder()
      .baseUrl(CFG.spendUrl())
      .client(client)
      .addConverterFactory(JacksonConverterFactory.create())
      .build();

  private final SpendApi spendApi = retrofit.create(SpendApi.class);

  private <T> T execute(Call<T> call, int expectedStatusCode) {
    try {
      Response<T> response = call.execute();
      assertEquals(expectedStatusCode, response.code(), "Unexpected HTTP status code");
      return response.body();
    } catch (IOException e) {
      throw new AssertionError("Failed to execute API request", e);
    }
  }

  @Override
  public SpendJson createSpend(SpendJson spend) {
    return execute(spendApi.addSpend(spend), SC_CREATED);
  }

  @Override
  public CategoryJson createCategory(CategoryJson category) {
    return execute(spendApi.addCategory(category), SC_OK);
  }

  @Override
  public CategoryJson updateCategory(CategoryJson category) {
    return execute(spendApi.updateCategory(category), SC_OK);
  }

  public SpendJson editSpend(SpendJson spendJson) {
    return execute(spendApi.editSpend(spendJson), SC_OK);
  }

  public SpendJson getSpend(String username, String id) {
    return execute(spendApi.getSpend(id, username), SC_OK);
  }

  public List<SpendJson> getSpends(String username, String from, String to, String filterCurrency) {
    return execute(spendApi.getSpends(username, from, to, filterCurrency), SC_OK);
  }

  public void removeSpends(String username, List<String> ids) {
    execute(spendApi.removeSpends(username, ids), SC_ACCEPTED);
  }

  public List<CategoryJson> getCategories(String username, boolean excludeArchived) {
    return execute(spendApi.getCategories(username, excludeArchived), SC_OK);
  }
}
