package guru.qa.niffler.test.web;

import guru.qa.niffler.jupiter.annotation.User;
import guru.qa.niffler.model.UserJson;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.EmptySource;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static guru.qa.niffler.common.messages.ApplicationWarnings.SignupWarnings.*;
import static guru.qa.niffler.utils.RandomDataUtils.*;

public class SignUpTest extends BaseTestWeb {
  @Test
  void shouldRegisterNewUser() {
    openSignUpPage();
    signUpPage
        .doSuccessSignUp(nonExistentUserName(), newValidPassword());
    signUpSuccessPage
        .verifySuccessSignUpMessage();
  }

  @User(username = TEST_USER_NAME)
  @Test
  void shouldNotRegisterUserWithExistingUsername(UserJson user) {
    openSignUpPage();
    signUpPage
        .doSignUp(user.username(), newValidPassword())
        .verifyUsernameWarning(String.format(USER_EXISTS, user.username()))
        .verifySignupPageIsOpened();
  }

  @ParameterizedTest
  @EmptySource
  void shouldNotRegisterWithEmptyUsername(String username) {
    openSignUpPage();
    signUpPage
        .doSignUp(username, newValidPassword())
        .verifyFillUsernameMessage()
        .verifySignupPageIsOpened();
  }

  @ParameterizedTest
  @EmptySource
  void shouldNotRegisterWithEmptyPassword(String password) {
    openSignUpPage();
    signUpPage
        .doSignUp(nonExistentUserName(), password, newValidPassword())
        .verifyFillPasswordMessage()
        .verifySignupPageIsOpened();
  }

  @ParameterizedTest
  @EmptySource
  void shouldNotRegisterWithEmptyConfirmationPassword(String passwordSubmit) {
    openSignUpPage();
    signUpPage
        .doSignUp(nonExistentUserName(), newValidPassword(), passwordSubmit)
        .verifyFillPasswordSubmitMessage()
        .verifySignupPageIsOpened();
  }

  @Test
  void shouldNotRegisterIfPasswordAndConfirmationPasswordNotEqual() {
    openSignUpPage();
    signUpPage
        .doSignUp(nonExistentUserName(), newValidPassword(), newValidPassword())
        .verifyPasswordWarning(PASSWORDS_DO_NOT_MATCH)
        .verifySignupPageIsOpened();
  }

  @ParameterizedTest
  @MethodSource("invalidUsernames")
  void shouldNotRegisterWithInvalidUsername(String username) {
    openSignUpPage();
    signUpPage
        .doSignUp(username, newValidPassword())
        .verifyUsernameWarning(INVALID_USERNAME)
        .verifySignupPageIsOpened();
  }

  @ParameterizedTest
  @MethodSource("invalidPasswords")
  void shouldNotRegisterWithInvalidPassword(String password) {
    openSignUpPage();
    signUpPage
        .doSignUp(nonExistentUserName(), password)
        .verifyPasswordWarning(INVALID_PASSWORD)
        .verifySignupPageIsOpened();
  }

  @Test
  void shouldNavigateToLoginPage() {
    openSignUpPage();
    signUpPage
        .navigateToLoginPage();
    loginPage
        .verifyLoginPageIsOpened();
  }

  private static Stream<Arguments> invalidUsernames() {
    return Stream.of(
        Arguments.of(shortUsername()),
        Arguments.of(longUsername())
    );
  }

  private static Stream<Arguments> invalidPasswords() {
    return Stream.of(
        Arguments.of(shortPassword()),
        Arguments.of(longPassword())
    );
  }
}
