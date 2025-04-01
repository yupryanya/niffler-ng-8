package guru.qa.niffler.test.web;

import com.codeborne.selenide.Selenide;
import guru.qa.niffler.page.LoginPage;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EmptySource;

import static guru.qa.niffler.data.randomData.UserData.newValidPassword;
import static guru.qa.niffler.data.randomData.UserData.nonExistentUserName;

public class LoginTest extends BaseTestWeb{
    @Test
    void shouldLoginWithValidCredentials() {
        Selenide.open(CFG.authUrl(), LoginPage.class)
                .doSuccessLogin(TEST_USER_NAME, TEST_USER_PASSWORD)
                .verifyMainPageIsOpened();
    }

    @Test
    void shouldNotLoginWithInvalidUsername() {
        Selenide.open(CFG.authUrl(), LoginPage.class)
                .doLogin(nonExistentUserName(), TEST_USER_PASSWORD)
                .verifyBadCredentialsErrorMessage()
                .verifyLoginPageIsOpened();
    }

    @Test
    void shouldNotLoginWithInvalidPassword() {
        Selenide.open(CFG.authUrl(), LoginPage.class)
                .doLogin(TEST_USER_NAME, newValidPassword())
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

    @ParameterizedTest
    @EmptySource
    void shouldNotLoginWithEmptyPassword(String password) {
        Selenide.open(CFG.authUrl(), LoginPage.class)
                .doLogin(TEST_USER_NAME, password)
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
