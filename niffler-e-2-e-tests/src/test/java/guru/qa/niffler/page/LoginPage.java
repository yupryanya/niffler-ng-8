package guru.qa.niffler.page;

import com.codeborne.selenide.SelenideElement;

import static com.codeborne.selenide.Condition.*;
import static com.codeborne.selenide.Selenide.$;
import static guru.qa.niffler.data.constants.ApplicationWarnings.LoginWarnings.BAD_CREDENTIALS;
import static guru.qa.niffler.data.constants.ApplicationWarnings.SignupWarnings.VALIDATION_MESSAGE;

public class LoginPage {
    private final String LOGIN_TITLE_TEXT = "Log in";

    private final SelenideElement loginTitle = $("h1.header");
    private final SelenideElement badCredentialsErrorMessage = $("p[class='form__error']");
    private final SelenideElement usernameInput = $("input[name='username']");
    private final SelenideElement passwordInput = $("input[name='password']");
    private final SelenideElement submitButton = $("button[type='submit']");
    private final SelenideElement signUpLink = $("a[class='form__register']");

    public LoginPage setUsername(String username) {
        usernameInput.setValue(username);
        return this;
    }

    public LoginPage setPassword(String password) {
        passwordInput.setValue(password);
        return this;
    }

    public LoginPage clickSubmitButton() {
        submitButton.click();
        return this;
    }

    public LoginPage doLogin(String username, String password) {
        setUsername(username);
        setPassword(password);
        clickSubmitButton();
        return this;
    }

    public MainPage doSuccessLogin(String username, String password) {
        doLogin(username, password);
        return new MainPage();
    }

    public LoginPage verifyBadCredentialsErrorMessage() {
        badCredentialsErrorMessage.shouldHave(text(BAD_CREDENTIALS));
        return this;
    }

    public LoginPage verifyFillUsernameMessage() {
        usernameInput.shouldHave(attribute("validationMessage", VALIDATION_MESSAGE));
        return this;
    }

    public LoginPage verifyFillPasswordMessage() {
        passwordInput.shouldHave(attribute("validationMessage", VALIDATION_MESSAGE));
        return this;
    }

    public LoginPage verifyLoginPageIsOpened() {
        loginTitle.shouldHave(text(LOGIN_TITLE_TEXT));
        return this;
    }

    public SignUpPage navigateToSignUpPage() {
        signUpLink.click();
        return new SignUpPage();
    }
}
