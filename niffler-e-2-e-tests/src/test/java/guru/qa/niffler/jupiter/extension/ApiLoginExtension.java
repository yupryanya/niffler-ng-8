package guru.qa.niffler.jupiter.extension;

import com.codeborne.selenide.Selenide;
import com.codeborne.selenide.WebDriverRunner;
import guru.qa.niffler.api.core.ThreadSafeCookieStore;
import guru.qa.niffler.config.Config;
import guru.qa.niffler.jupiter.annotation.ApiLogin;
import guru.qa.niffler.jupiter.annotation.Token;
import guru.qa.niffler.model.TestData;
import guru.qa.niffler.model.UserJson;
import guru.qa.niffler.page.MainPage;
import guru.qa.niffler.service.api.AuthApiClient;
import guru.qa.niffler.service.api.SpendApiClient;
import guru.qa.niffler.service.api.UserApiClient;
import org.junit.jupiter.api.extension.*;
import org.junit.platform.commons.support.AnnotationSupport;
import org.openqa.selenium.Cookie;

import static guru.qa.niffler.jupiter.extension.UserExtension.getContextUser;
import static guru.qa.niffler.jupiter.extension.UserExtension.setContextUser;

public class ApiLoginExtension implements BeforeEachCallback, ParameterResolver {
  private final static Config CFG = Config.getInstance();

  public static final ExtensionContext.Namespace NAMESPACE = ExtensionContext.Namespace.create(ApiLoginExtension.class);

  private AuthApiClient authApiClient = new AuthApiClient();
  private UserApiClient userApiClient = new UserApiClient();
  private SpendApiClient spendApiClient = new SpendApiClient();

  private final boolean setupBrowser;

  private ApiLoginExtension(boolean setupBrowser) {
    this.setupBrowser = setupBrowser;
  }

  public ApiLoginExtension() {
    this.setupBrowser = true;
  }

  public static ApiLoginExtension rest() {
    return new ApiLoginExtension(false);
  }

  @Override
  public void beforeEach(ExtensionContext context) throws Exception {
    AnnotationSupport.findAnnotation(context.getRequiredTestMethod(), ApiLogin.class)
        .ifPresent(apiLogin -> {
          final UserJson contextUser = getContextUser();
          final boolean hasApiLoginCredentials = !"".equals(apiLogin.username()) && !"".equals(apiLogin.password());

          final String username;
          final String password;

          if (contextUser != null) {
            if (!hasApiLoginCredentials) {
              username = contextUser.username();
              password = contextUser.testData().password();
            } else {
              throw new IllegalArgumentException("Both context user and @ApiLogin credentials are set, choose one");
            }
          } else {
            if (hasApiLoginCredentials) {
              username = apiLogin.username();
              password = apiLogin.password();
              final UserJson user = userApiClient.findUserByUsername(username)
                  .orElseThrow(() -> new RuntimeException("User not found: " + username));
              final TestData testData = getTestData(username, password);
              setContextUser(user.withTestData(testData));
            } else {
              throw new IllegalArgumentException("User is not set. Use username/password in @ApiLogin.");
            }
          }

          try {
            String token = authApiClient.login(username, password);
            setToken(token);
            if (setupBrowser) setupBrowserSession();
          } catch (RuntimeException e) {
            throw new RuntimeException("Failed to login user: " + username, e);
          }
        });
  }

  private TestData getTestData(String username, String password) {
    return TestData.emptyTestData()
        .withPassword(password)
        .withCategories(spendApiClient.getCategories(username, false))
        .withSpends(spendApiClient.getSpends(username, null, null, null))
        .withFriends(userApiClient.getFriends(username))
        .withIncomeInvitations(userApiClient.getIncomeInvitations(username))
        .withOutcomeInvitations(userApiClient.getOutcomeInvitations(username));
  }

  private static void setupBrowserSession() {
    Selenide.open(CFG.frontUrl() + "/assets/niffler-with-a-coin-Cb77k8MX.png");
    Selenide.localStorage().setItem("id_token", getToken());
    WebDriverRunner.getWebDriver().manage().addCookie(
        new Cookie("JSESSIONID", ThreadSafeCookieStore.INSTANCE.getCookieValue("JSESSIONID"))
    );
    Selenide.open(CFG.frontUrl(), MainPage.class)
        .verifyMainPageIsOpened();
  }

  @Override
  public boolean supportsParameter(ParameterContext parameterContext, ExtensionContext extensionContext) throws ParameterResolutionException {
    return parameterContext.getParameter().getType().isAssignableFrom(String.class)
           && AnnotationSupport.isAnnotated(parameterContext.getParameter(), Token.class);
  }

  @Override
  public String resolveParameter(ParameterContext parameterContext, ExtensionContext extensionContext) throws ParameterResolutionException {
    return "Bearer " + getToken();
  }

  public static void setToken(String token) {
    TestMethodContextExtension.context().getStore(NAMESPACE).put("token", token);
  }

  public static String getToken() {
    return TestMethodContextExtension.context().getStore(NAMESPACE).get("token", String.class);
  }

  public static void setCode(String code) {
    TestMethodContextExtension.context().getStore(NAMESPACE).put("code", code);
  }

  public static String getCode() {
    return TestMethodContextExtension.context().getStore(NAMESPACE).get("code", String.class);
  }

  public static Cookie getJSessionIdCookie() {
    return new Cookie("JSESSIONID", ThreadSafeCookieStore.INSTANCE.getCookieValue("JSESSIONID"));
  }
}