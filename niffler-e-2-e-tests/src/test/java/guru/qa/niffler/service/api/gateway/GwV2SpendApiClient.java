package guru.qa.niffler.service.api.gateway;

import com.fasterxml.jackson.databind.JsonNode;
import guru.qa.niffler.api.core.RestClient;
import guru.qa.niffler.api.gatewayv2.GwV2SpendApi;
import guru.qa.niffler.common.values.CurrencyValues;
import guru.qa.niffler.common.values.DataFilterValues;
import guru.qa.niffler.model.UserJson;
import guru.qa.niffler.model.pageable.RestResponsePage;
import io.qameta.allure.Step;

import javax.annotation.Nullable;

import static org.apache.hc.core5.http.HttpStatus.SC_OK;

public class GwV2SpendApiClient extends RestClient {

  private GwV2SpendApi gwV2SpendApi;

  public GwV2SpendApiClient() {
    super(CFG.gatewayUrl());
    this.gwV2SpendApi = retrofit.create(GwV2SpendApi.class);
  }

  @Step("Get all spends with external API 'GET /api/v2/spends/all'")
  public RestResponsePage<UserJson> allSpends(String token,
                                              int page,
                                              @Nullable String filterCurrency,
                                              @Nullable DataFilterValues filterPeriod,
                                              @Nullable String searchQuery) {
    return execute(gwV2SpendApi.allSpends(token, page, filterCurrency, filterPeriod, searchQuery), SC_OK);
  }

  @Step("Get total spend statistics with external API 'GET /api/v2/stat/total'")
  public RestResponsePage<JsonNode> totalStat(String token,
                                              CurrencyValues statCurrency,
                                              @Nullable CurrencyValues filterCurrency,
                                              @Nullable DataFilterValues filterPeriod) {
    return execute(gwV2SpendApi.totalStat(token, statCurrency, filterCurrency, filterPeriod), SC_OK);
  }
}