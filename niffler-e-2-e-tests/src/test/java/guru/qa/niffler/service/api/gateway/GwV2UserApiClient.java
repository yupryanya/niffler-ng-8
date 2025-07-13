package guru.qa.niffler.service.api.gateway;

import guru.qa.niffler.api.core.RestClient;
import guru.qa.niffler.api.gatewayv2.GwV2UserApi;
import guru.qa.niffler.model.UserJson;
import guru.qa.niffler.model.pageable.RestResponsePage;
import io.qameta.allure.Step;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

import static org.apache.hc.core5.http.HttpStatus.SC_OK;

@ParametersAreNonnullByDefault
public class GwV2UserApiClient extends RestClient {

  private GwV2UserApi gwV2UserApi;

  public GwV2UserApiClient() {
    super(CFG.gatewayUrl());
    this.gwV2UserApi = retrofit.create(GwV2UserApi.class);
  }

  @Step("Find all users with external API 'GET /api/v2/users/all'")
  public @Nonnull RestResponsePage<UserJson> allUsers(String token,
                                                      int page,
                                                      int size,
                                                      String sort,
                                                      @Nullable String searchQuery) {
    return execute(gwV2UserApi.allUsers(token, page, size, sort, searchQuery), SC_OK);
  }

  @Step("Get all users with external API 'GET /api/v2/friends/all'")
  public @Nonnull RestResponsePage<UserJson> allFriends(String token,
                                                        int page,
                                                        int size,
                                                        String sort,
                                                        @Nullable String searchQuery) {
    return execute(gwV2UserApi.allFriends(token, page, size, sort, searchQuery), SC_OK);
  }

}