package guru.qa.niffler.test.web;

import guru.qa.niffler.jupiter.annotation.User;
import guru.qa.niffler.model.UserJson;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EmptySource;

import static guru.qa.niffler.utils.RandomDataUtils.newValidPassword;
import static guru.qa.niffler.utils.RandomDataUtils.nonExistentUserName;

public class LoginTest extends BaseTestWeb {
  @User(username = TEST_USER_NAME)
  @Test
  void shouldLoginWithValidCredentials(UserJson user) {
    login(user);
  }

  @User(username = TEST_USER_NAME)
  @Test
  void shouldNotLoginWithInvalidUsername(UserJson user) {
    openLoginPage();
    loginPage.doLogin(nonExistentUserName(), user.testData().password())
        .verifyBadCredentialsErrorMessage()
        .verifyLoginPageIsOpened();
  }

  @User(username = TEST_USER_NAME)
  @Test
  void shouldNotLoginWithInvalidPassword(UserJson user) {
    openLoginPage();
    loginPage
        .doLogin(user.username(), newValidPassword())
        .verifyBadCredentialsErrorMessage()
        .verifyLoginPageIsOpened();
  }

  @ParameterizedTest
  @EmptySource
  void shouldNotLoginWithEmptyUsername(String username) {
    openLoginPage();
    loginPage
        .doLogin(username, newValidPassword())
        .verifyFillUsernameMessage()
        .verifyLoginPageIsOpened();
  }

  @User(username = TEST_USER_NAME)
  @ParameterizedTest
  @EmptySource
  void shouldNotLoginWithEmptyPassword(String password, UserJson user) {
    openLoginPage();
    loginPage
        .doLogin(user.username(), password)
        .verifyFillPasswordMessage()
        .verifyLoginPageIsOpened();
  }

  @Test
  void shouldNavigateToSignUpPage() {
    openLoginPage();
    loginPage
        .navigateToSignUpPage();
    signUpPage
        .verifySignupPageIsOpened();
  }
}
