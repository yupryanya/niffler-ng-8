package guru.qa.niffler.page;

import com.codeborne.selenide.SelenideDriver;
import com.codeborne.selenide.SelenideElement;
import guru.qa.niffler.config.Config;

import static com.codeborne.selenide.Condition.attribute;
import static com.codeborne.selenide.Condition.text;
import static guru.qa.niffler.common.messages.ApplicationWarnings.SignupWarnings.VALIDATION_MESSAGE;

public class SignUpPage extends BasePage {
  protected static final Config CFG = Config.getInstance();
  public static final String URL = CFG.authUrl() + "register";

  public static final String SIGN_UP_TITLE_TEXT = "Sign up";

  private final SelenideElement signUpTitle;
  private final SelenideElement logInLink;
  private final SelenideElement usernameInput;
  private final SelenideElement usernameWarning;
  private final SelenideElement passwordInput;
  private final SelenideElement showPasswordButton;
  private final SelenideElement passwordWarning;
  private final SelenideElement passwordSubmitInput;
  private final SelenideElement showPasswordSubmitButton;
  private final SelenideElement passwordSubmitWarning;
  private final SelenideElement confirmSignUpButton;

  public SignUpPage(SelenideDriver driver) {
    super(driver);
    this.signUpTitle = driver.$("h1.header");
    this.logInLink = driver.$("a[class=form__link]");
    this.usernameInput = driver.$("#username");
    this.usernameWarning = driver.$("#username + .form__error");
    this.passwordInput = driver.$("#password");
    this.showPasswordButton = driver.$("#passwordBtn");
    this.passwordWarning = driver.$("#password").closest("label").$(".form__error");
    this.passwordSubmitInput = driver.$("#passwordSubmit");
    this.showPasswordSubmitButton = driver.$("#passwordSubmitBtn");
    this.passwordSubmitWarning = driver.$("#passwordSubmit + .form__error");
    this.confirmSignUpButton = driver.$("button[type='submit']");
  }

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

  public void doSuccessSignUp(String username, String password) {
    doSignUp(username, password, password);
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

  public void navigateToLoginPage() {
    logInLink.click();
  }
}
