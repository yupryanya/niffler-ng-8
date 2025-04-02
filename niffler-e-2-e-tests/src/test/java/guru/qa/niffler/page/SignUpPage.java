package guru.qa.niffler.page;

import com.codeborne.selenide.SelenideElement;
import guru.qa.niffler.config.Config;

import static com.codeborne.selenide.Condition.attribute;
import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Selenide.$;
import static guru.qa.niffler.data.constants.ApplicationWarnings.SignupWarnings.VALIDATION_MESSAGE;

public class SignUpPage {
    protected static final Config CFG = Config.getInstance();
    public static final String URL = CFG.authUrl() + "register";

    public static final String SIGN_UP_TITLE_TEXT = "Sign up";

    private final SelenideElement signUpTitle = $("h1.header");
    private final SelenideElement logInLink = $("a[class=form__link]");
    private final SelenideElement usernameInput = $("#username");
    private final SelenideElement usernameWarning = $("#username + .form__error");
    private final SelenideElement passwordInput = $("#password");
    private final SelenideElement showPasswordButton = $("#passwordBtn");
    private final SelenideElement passwordWarning = $("#password").closest("label").$(".form__error");
    ;
    private final SelenideElement passwordSubmitInput = $("#passwordSubmit");
    private final SelenideElement showPasswordSubmitButton = $("#passwordSubmitBtn");
    private final SelenideElement passwordSubmitWarning = $("#passwordSubmit + .form__error");
    private final SelenideElement confirmSignUpButton = $("button[type='submit']");

    public SignUpPage setUsername(String username) {
        usernameInput.setValue(username);
        return this;
    }

    public SignUpPage setPassword(String password) {
        passwordInput.setValue(password);
        return this;
    }

    public SignUpPage setPasswordSubmit(String passwordSubmit) {
        passwordSubmitInput.setValue(passwordSubmit);
        return this;
    }

    public void clickSubmitButton() {
        confirmSignUpButton.click();
    }

    public SignUpPage doSignUp(String username, String password, String passwordSubmit) {
        setUsername(username);
        setPassword(password);
        setPasswordSubmit(passwordSubmit);
        clickSubmitButton();
        return this;
    }

    public SignUpPage doSignUp(String username, String password) {
        doSignUp(username, password, password);
        return this;
    }

    public SignUpSuccessPage doSuccessSignUp(String username, String password) {
        doSignUp(username, password, password);
        return new SignUpSuccessPage();
    }

    public SignUpPage verifyUsernameWarning(String expectedText) {
        usernameWarning.shouldHave(text(expectedText));
        return this;
    }

    public SignUpPage verifyPasswordWarning(String expectedText) {
        passwordWarning.shouldHave(text(expectedText));
        return this;
    }

    public SignUpPage verifyFillUsernameMessage() {
        usernameInput.shouldHave(attribute("validationMessage", VALIDATION_MESSAGE));
        return this;
    }

    public SignUpPage verifyFillPasswordMessage() {
        passwordInput.shouldHave(attribute("validationMessage", VALIDATION_MESSAGE));
        return this;
    }

    public SignUpPage verifyFillPasswordSubmitMessage() {
        passwordSubmitInput.shouldHave(attribute("validationMessage", VALIDATION_MESSAGE));
        return this;
    }

    public SignUpPage verifySignupPageIsOpened() {
        signUpTitle.shouldHave(text(SIGN_UP_TITLE_TEXT));
        return this;
    }

    public LoginPage navigateToLoginPage() {
        logInLink.click();
        return new LoginPage();
    }
}
