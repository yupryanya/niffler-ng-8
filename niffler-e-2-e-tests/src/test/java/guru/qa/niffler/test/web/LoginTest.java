package guru.qa.niffler.test.web;

import com.codeborne.selenide.Selenide;
import guru.qa.niffler.jupiter.annotation.User;
import guru.qa.niffler.model.UserJson;
import guru.qa.niffler.page.LoginPage;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EmptySource;

import static guru.qa.niffler.utils.RandomDataUtils.newValidPassword;
import static guru.qa.niffler.utils.RandomDataUtils.nonExistentUserName;

public class LoginTest extends BaseTestWeb {
  @User()
  @Test
  void shouldLoginWithValidCredentials(UserJson user) {
    Selenide.open(CFG.authUrl(), LoginPage.class)
        .doLogin(user.username(), user.testData().password());
    mainPage
        .verifyMainPageIsOpened();
  }

  @User()
  @Test
  void shouldNotLoginWithInvalidUsername(UserJson user) {
    Selenide.open(CFG.authUrl(), LoginPage.class)
        .doLogin(nonExistentUserName(), user.testData().password())
        .verifyBadCredentialsErrorMessage()
        .verifyLoginPageIsOpened();
  }

  @User()
  @Test
  void shouldNotLoginWithInvalidPassword(UserJson user) {
    Selenide.open(CFG.authUrl(), LoginPage.class)
        .doLogin(user.username(), newValidPassword())
        .verifyBadCredentialsErrorMessage()
        .verifyLoginPageIsOpened();
  }

  @ParameterizedTest
  @EmptySource
  void shouldNotLoginWithEmptyUsername(String username) {
    Selenide.open(CFG.authUrl(), LoginPage.class)
        .doLogin(username, newValidPassword())
        .verifyFillUsernameMessage()
        .verifyLoginPageIsOpened();
  }

  @User()
  @ParameterizedTest
  @EmptySource
  void shouldNotLoginWithEmptyPassword(String password, UserJson user) {
    Selenide.open(CFG.authUrl(), LoginPage.class)
        .doLogin(user.username(), password)
        .verifyFillPasswordMessage()
        .verifyLoginPageIsOpened();
  }

  @Test
  void shouldNavigateToSignUpPage() {
    Selenide.open(CFG.authUrl(), LoginPage.class)
        .navigateToSignUpPage()
        .verifySignupPageIsOpened();
  }
}
