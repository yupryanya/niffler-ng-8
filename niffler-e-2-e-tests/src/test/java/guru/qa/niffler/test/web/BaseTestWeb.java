package guru.qa.niffler.test.web;

import com.codeborne.selenide.SelenideDriver;
import guru.qa.niffler.config.Config;
import guru.qa.niffler.jupiter.extension.BrowserExtension;
import guru.qa.niffler.model.UserJson;
import guru.qa.niffler.page.*;
import org.junit.jupiter.api.extension.RegisterExtension;

import static guru.qa.niffler.utils.SelenideUtils.chromeConfig;

public class BaseTestWeb {
  protected static final Config CFG = Config.getInstance();

  @RegisterExtension
  protected final BrowserExtension browserExtension = new BrowserExtension();

  protected static final String TEST_USER_NAME = "testuser";

  private SelenideDriver cromeDriver = new SelenideDriver(chromeConfig);

  MainPage mainPage = new MainPage(cromeDriver);
  LoginPage loginPage = new LoginPage(cromeDriver);
  SignUpPage signUpPage = new SignUpPage(cromeDriver);
  ProfilePage profilePage = new ProfilePage(cromeDriver);
  SignUpSuccessPage signUpSuccessPage = new SignUpSuccessPage(cromeDriver);
  EditSpendingPage editSpendingPage = new EditSpendingPage(cromeDriver);
  AddSpendingPage addSpendingPage = new AddSpendingPage(cromeDriver);

  protected void login(UserJson user) {
    login(user, cromeDriver);
  }

  protected void login(UserJson user, SelenideDriver driver) {
    browserExtension.addDriver(cromeDriver);
    driver.open(CFG.frontUrl());
    new LoginPage(driver)
        .doSuccessLogin(user.username(), user.testData().password());
    new MainPage(driver)
        .verifyMainPageIsOpened();
  }

  public void openLoginPage() {
    browserExtension.addDriver(cromeDriver);
    cromeDriver.open(CFG.authUrl());
  }

  public void openSignUpPage() {
    browserExtension.addDriver(cromeDriver);
    cromeDriver.open(SignUpPage.URL);
  }
}
