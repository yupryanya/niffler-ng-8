package guru.qa.niffler.test.web;

import com.codeborne.selenide.Selenide;
import guru.qa.niffler.page.LoginPage;
import guru.qa.niffler.page.SignUpPage;
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
        Selenide.open(CFG.frontUrl(), LoginPage.class)
                .navigateToSignUpPage()
                .doSuccessSignUp(nonExistentUserName(), newValidPassword())
                .verifySuccessSignUpMessage();
    }

    @Test
    void shouldNotRegisterUserWithExistingUsername() {
        Selenide.open(CFG.frontUrl(), LoginPage.class)
                .navigateToSignUpPage()
                .doSignUp(TEST_USER_NAME, newValidPassword())
                .verifyUsernameWarning(String.format(USER_EXISTS, TEST_USER_NAME))
                .verifySignupPageIsOpened();
    }

    @ParameterizedTest
    @EmptySource
    void shouldNotRegisterWithEmptyUsername(String username) {
        Selenide.open(SignUpPage.URL, SignUpPage.class)
                .doSignUp(username, newValidPassword())
                .verifyFillUsernameMessage()
                .verifySignupPageIsOpened();
    }

    @ParameterizedTest
    @EmptySource
    void shouldNotRegisterWithEmptyPassword(String password) {
        Selenide.open(SignUpPage.URL, SignUpPage.class)
                .doSignUp(nonExistentUserName(), password, newValidPassword())
                .verifyFillPasswordMessage()
                .verifySignupPageIsOpened();
    }

    @ParameterizedTest
    @EmptySource
    void shouldNotRegisterWithEmptyConfirmationPassword(String passwordSubmit) {
        Selenide.open(SignUpPage.URL, SignUpPage.class)
                .doSignUp(nonExistentUserName(), newValidPassword(), passwordSubmit)
                .verifyFillPasswordSubmitMessage()
                .verifySignupPageIsOpened();
    }

    @Test
    void shouldNotRegisterIfPasswordAndConfirmationPasswordNotEqual() {
        Selenide.open(SignUpPage.URL, SignUpPage.class)
                .doSignUp(nonExistentUserName(), newValidPassword(), newValidPassword())
                .verifyPasswordWarning(PASSWORDS_DO_NOT_MATCH)
                .verifySignupPageIsOpened();
    }

    @ParameterizedTest
    @MethodSource("invalidUsernames")
    void shouldNotRegisterWithInvalidUsername(String username) {
        Selenide.open(SignUpPage.URL, SignUpPage.class)
                .doSignUp(username, newValidPassword())
                .verifyUsernameWarning(INVALID_USERNAME)
                .verifySignupPageIsOpened();
    }

    @ParameterizedTest
    @MethodSource("invalidPasswords")
    void shouldNotRegisterWithInvalidPassword(String password) {
        Selenide.open(SignUpPage.URL, SignUpPage.class)
                .doSignUp(nonExistentUserName(), password)
                .verifyPasswordWarning(INVALID_PASSWORD)
                .verifySignupPageIsOpened();
    }

    @Test
    void shouldNavigateToLoginPage() {
        Selenide.open(SignUpPage.URL, SignUpPage.class)
                .navigateToLoginPage()
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
