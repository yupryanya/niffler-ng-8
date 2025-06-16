package guru.qa.niffler.test.api;

import guru.qa.niffler.api.AuthApi;
import guru.qa.niffler.api.core.RestClient;
import guru.qa.niffler.api.core.ThreadSafeCookieStore;
import guru.qa.niffler.config.Config;
import guru.qa.niffler.jupiter.annotation.User;
import guru.qa.niffler.model.UserJson;
import guru.qa.niffler.utils.OAuthUtils;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertNotNull;

public class OAuthTest {
  private final static Config CFG = Config.getInstance();

  private final AuthApi authApi = new RestClient(CFG.authUrl(), true).getRetrofit().create(AuthApi.class);

  private final String redirectUrl = CFG.frontUrl() + "authorized";
  private final String clientId = "client";
  private final String codeVerifier = OAuthUtils.generateCodeVerifier();
  private final String codeChallenge = OAuthUtils.generateCodeChallenge(codeVerifier);

  @User
  @Test
  void verifyTokenIsReturned(UserJson user) throws IOException {
    authApi
        .preRequest("code", clientId, "openid", redirectUrl, codeChallenge, "S256")
        .execute();

    var response = authApi
        .login(ThreadSafeCookieStore.INSTANCE.getCookieValue("XSRF-TOKEN"), user.username(), user.testData().password())
        .execute();
    String code = response.raw().request().url().queryParameter("code");

    var token = authApi
        .token(code, redirectUrl, codeVerifier, "authorization_code", clientId)
        .execute();

    assertNotNull(token.body().idToken());
  }
}