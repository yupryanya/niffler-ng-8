package guru.qa.niffler.page;

import com.codeborne.selenide.SelenideElement;
import io.qameta.allure.Step;

import static com.codeborne.selenide.Condition.attribute;
import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Selenide.$;
import static guru.qa.niffler.common.messages.ApplicationMessages.LoginWarnings.BAD_CREDENTIALS;
import static guru.qa.niffler.common.messages.ApplicationMessages.SignupWarnings.VALIDATION_MESSAGE;

public class LoginPage extends BasePage<LoginPage> {
    private final String LOGIN_TITLE_TEXT = "Log in";

    private final SelenideElement loginTitle = $("h1.header");
    private final SelenideElement badCredentialsErrorMessage = $("p[class='form__error']");
    private final SelenideElement usernameInput = $("input[name='username']");
    private final SelenideElement passwordInput = $("input[name='password']");
    private final SelenideElement submitButton = $("button[type='submit']");
    private final SelenideElement signUpLink = $("a[class='form__register']");

    @Step("Set username input field")
    public LoginPage setUsername(String username) {
        usernameInput.setValue(username);
        return this;
    }

    @Step("Set password input field")
    public LoginPage setPassword(String password) {
        passwordInput.setValue(password);
        return this;
    }

    @Step("Click on submit button")
    public LoginPage clickSubmitButton() {
        submitButton.click();
        return this;
    }

    @Step("Login with username and password")
    public LoginPage doLogin(String username, String password) {
        setUsername(username);
        setPassword(password);
        clickSubmitButton();
        return this;
    }

    @Step("Verify bad credentials error message")
    public LoginPage verifyBadCredentialsErrorMessage() {
        badCredentialsErrorMessage.shouldHave(text(BAD_CREDENTIALS));
        return this;
    }

    @Step("Verify empty username error message")
    public LoginPage verifyFillUsernameMessage() {
        usernameInput.shouldHave(attribute("validationMessage", VALIDATION_MESSAGE));
        return this;
    }

    @Step("Verify empty password error message")
    public LoginPage verifyFillPasswordMessage() {
        passwordInput.shouldHave(attribute("validationMessage", VALIDATION_MESSAGE));
        return this;
    }

    @Step("Verify login page is opened")
    public LoginPage verifyLoginPageIsOpened() {
        loginTitle.shouldHave(text(LOGIN_TITLE_TEXT));
        return this;
    }

    @Step("Navigate to Sign Up page")
    public SignUpPage navigateToSignUpPage() {
        signUpLink.click();
        return new SignUpPage();
    }
}
