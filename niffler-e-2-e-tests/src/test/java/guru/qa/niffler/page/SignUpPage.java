package guru.qa.niffler.page;

import com.codeborne.selenide.SelenideElement;
import guru.qa.niffler.config.Config;
import io.qameta.allure.Step;

import static com.codeborne.selenide.Condition.attribute;
import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Selenide.$;
import static guru.qa.niffler.common.messages.ApplicationWarnings.SignupWarnings.VALIDATION_MESSAGE;

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
  private final SelenideElement passwordSubmitInput = $("#passwordSubmit");
  private final SelenideElement showPasswordSubmitButton = $("#passwordSubmitBtn");
  private final SelenideElement passwordSubmitWarning = $("#passwordSubmit + .form__error");
  private final SelenideElement confirmSignUpButton = $("button[type='submit']");

  @Step("Set username input field")
  public SignUpPage setUsername(String username) {
    usernameInput.setValue(username);
    return this;
  }

  @Step("Set password input field")
  public SignUpPage setPassword(String password) {
    passwordInput.setValue(password);
    return this;
  }

  @Step("Set password submit input field")
  public SignUpPage setPasswordSubmit(String passwordSubmit) {
    passwordSubmitInput.setValue(passwordSubmit);
    return this;
  }

  @Step("Click on submit button")
  public void clickSubmitButton() {
    confirmSignUpButton.click();
  }

  @Step("Signup with username, password and password confirmation")
  public SignUpPage doSignUp(String username, String password, String passwordSubmit) {
    setUsername(username);
    setPassword(password);
    setPasswordSubmit(passwordSubmit);
    clickSubmitButton();
    return this;
  }

  @Step("Signup with username and password")
  public SignUpPage doSignUp(String username, String password) {
    doSignUp(username, password, password);
    return this;
  }

  @Step("Verify username warning displayed")
  public SignUpPage verifyUsernameWarning(String expectedText) {
    usernameWarning.shouldHave(text(expectedText));
    return this;
  }

  @Step("Verify password warning displayed")
  public SignUpPage verifyPasswordWarning(String expectedText) {
    passwordWarning.shouldHave(text(expectedText));
    return this;
  }

  @Step("Verify empty username warning displayed")
  public SignUpPage verifyFillUsernameMessage() {
    usernameInput.shouldHave(attribute("validationMessage", VALIDATION_MESSAGE));
    return this;
  }

  @Step("Verify empty password warning displayed")
  public SignUpPage verifyFillPasswordMessage() {
    passwordInput.shouldHave(attribute("validationMessage", VALIDATION_MESSAGE));
    return this;
  }

  @Step("Verify empty password submit warning displayed")
  public SignUpPage verifyFillPasswordSubmitMessage() {
    passwordSubmitInput.shouldHave(attribute("validationMessage", VALIDATION_MESSAGE));
    return this;
  }

  @Step("Verify Sign Up page title is displayed")
  public SignUpPage verifySignupPageIsOpened() {
    signUpTitle.shouldHave(text(SIGN_UP_TITLE_TEXT));
    return this;
  }

  @Step("Navigate to Login page")
  public LoginPage navigateToLoginPage() {
    logInLink.click();
    return new LoginPage();
  }
}
