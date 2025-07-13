package guru.qa.niffler.service.api.gateway;

import com.fasterxml.jackson.databind.JsonNode;
import guru.qa.niffler.api.core.RestClient;
import guru.qa.niffler.api.gateway.GwSpendApi;
import guru.qa.niffler.model.CategoryJson;
import guru.qa.niffler.model.SpendJson;
import io.qameta.allure.Step;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Optional;

import static org.apache.hc.core5.http.HttpStatus.SC_OK;

public class GwSpendApiClient extends RestClient {

  private GwSpendApi gwSpendApi;

  public GwSpendApiClient() {
    super(CFG.gatewayUrl());
    this.gwSpendApi = retrofit.create(GwSpendApi.class);
  }

  @Step("Get all spends with external 'GET /api/categories/all'")
  public @NotNull List<CategoryJson> allCategories(String token, boolean excludeArchived) {
    return execute(gwSpendApi.allCategories(token, excludeArchived), SC_OK);
  }

  @Step("Add category with external API 'POST api/categories/add'")
  public Optional<CategoryJson> addCategory(String token, CategoryJson category) {
    return Optional.of(execute(gwSpendApi.addCategory(token, category), SC_OK));
  }

  @Step("Get all currencies with external 'GET api/currencies/all'")
  public @NotNull List<JsonNode> allCurrencies(String token) {
    return (execute(gwSpendApi.allCurrencies(token), SC_OK));
  }

  @Step("Add spend with external API 'POST /api/spends/add'")
  public Optional<SpendJson> addSpend(String token, SpendJson spend) {
    return Optional.of(execute(gwSpendApi.addSpend(token, spend), SC_OK));
  }

  @Step("Edit spend with external API 'PATCH /api/spends/edit'")
  public Optional<SpendJson> editSpend(String token, SpendJson spend) {
    return Optional.of(execute(gwSpendApi.editSpend(token, spend), SC_OK));
  }

  @Step("Remove spends with external API 'DELETE /api/spends/remove'")
  public void removeSpends(String token, @NotNull List<String> ids) {
    executeVoid(gwSpendApi.removeSpends(token, ids), SC_OK);
  }
}
