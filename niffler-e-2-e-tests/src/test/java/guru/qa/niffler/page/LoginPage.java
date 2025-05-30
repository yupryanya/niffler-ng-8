package guru.qa.niffler.page;

import com.codeborne.selenide.SelenideDriver;
import com.codeborne.selenide.SelenideElement;

import static com.codeborne.selenide.Condition.attribute;
import static com.codeborne.selenide.Condition.text;
import static guru.qa.niffler.common.messages.ApplicationWarnings.LoginWarnings.BAD_CREDENTIALS;
import static guru.qa.niffler.common.messages.ApplicationWarnings.SignupWarnings.VALIDATION_MESSAGE;

public class LoginPage extends BasePage {
  private final String LOGIN_TITLE_TEXT = "Log in";

  private final SelenideElement loginTitle;
  private final SelenideElement badCredentialsErrorMessage;
  private final SelenideElement usernameInput;
  private final SelenideElement passwordInput;
  private final SelenideElement submitButton;
  private final SelenideElement signUpLink;

  public LoginPage(SelenideDriver driver) {
    super(driver);
    this.loginTitle = driver.$("h1.header");
    this.badCredentialsErrorMessage = driver.$("p[class='form__error']");
    this.usernameInput = driver.$("input[name='username']");
    this.passwordInput = driver.$("input[name='password']");
    this.submitButton = driver.$("button[type='submit']");
    this.signUpLink = driver.$("a[class='form__register']");
  }

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

  public void doSuccessLogin(String username, String password) {
    doLogin(username, password);
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

  public void navigateToSignUpPage() {
    signUpLink.click();
  }
}
