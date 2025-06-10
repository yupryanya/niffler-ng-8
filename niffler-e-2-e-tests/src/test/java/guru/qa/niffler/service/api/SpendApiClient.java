package guru.qa.niffler.service.api;

import guru.qa.niffler.api.SpendApi;
import guru.qa.niffler.api.core.RestClient;
import guru.qa.niffler.model.CategoryJson;
import guru.qa.niffler.model.SpendJson;
import guru.qa.niffler.service.SpendClient;
import io.qameta.allure.Step;
import retrofit2.Call;
import retrofit2.Response;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.io.IOException;
import java.util.List;
import java.util.Objects;

import static org.apache.hc.core5.http.HttpStatus.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

@ParametersAreNonnullByDefault
public class SpendApiClient extends RestClient implements SpendClient {

  private SpendApi spendApi;

  public SpendApiClient() {
    super(CFG.spendUrl());
    this.spendApi = retrofit.create(SpendApi.class);
  }

  private @Nonnull <T> T execute(Call<T> call, int expectedStatusCode) {
    try {
      Response<T> response = call.execute();
      assertEquals(expectedStatusCode, response.code(), "Unexpected HTTP status code");
      return Objects.requireNonNull(response.body());
    } catch (IOException e) {
      throw new AssertionError("Failed to execute API request", e);
    }
  }

  @Override
  @Step("Create spend with API")
  public SpendJson createSpend(SpendJson spend) {
    return execute(spendApi.addSpend(spend), SC_CREATED);
  }

  @Override
  @Step("Create category with API")
  public CategoryJson createCategory(CategoryJson category) {
    return execute(spendApi.addCategory(category), SC_OK);
  }

  @Override
  @Step("Update category with API")
  public CategoryJson updateCategory(CategoryJson category) {
    return execute(spendApi.updateCategory(category), SC_OK);
  }

  @Step("Edit spend with API")
  public SpendJson editSpend(SpendJson spendJson) {
    return execute(spendApi.editSpend(spendJson), SC_OK);
  }

  @Step("Get spend with API")
  public SpendJson getSpend(String username, String id) {
    return execute(spendApi.getSpend(id, username), SC_OK);
  }

  @Step("Get spends with API")
  public List<SpendJson> getSpends(String username,
                                   @Nullable String from,
                                   @Nullable String to,
                                   @Nullable String filterCurrency) {
    return execute(spendApi.getSpends(username, from, to, filterCurrency), SC_OK);
  }

  @Step("Remove spends with API")
  public void removeSpends(String username, List<String> ids) {
    execute(spendApi.removeSpends(username, ids), SC_ACCEPTED);
  }

  @Step("Get categories with API")
  public List<CategoryJson> getCategories(String username, boolean excludeArchived) {
    return execute(spendApi.getCategories(username, excludeArchived), SC_OK);
  }
}
