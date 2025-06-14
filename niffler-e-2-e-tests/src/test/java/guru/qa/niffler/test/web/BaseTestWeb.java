package guru.qa.niffler.test.web;

import com.codeborne.selenide.Selenide;
import guru.qa.niffler.config.Config;
import guru.qa.niffler.jupiter.extension.BrowserExtension;
import guru.qa.niffler.model.UserJson;
import guru.qa.niffler.page.LoginPage;
import guru.qa.niffler.page.MainPage;
import io.qameta.allure.Step;
import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith(BrowserExtension.class)
public class BaseTestWeb {
  protected static final Config CFG = Config.getInstance();

  protected static final String TEST_USER_NAME = "testuser";

  protected final MainPage mainPage = new MainPage();

  @Step("Login and verify that main page is opened}")
  protected MainPage login(UserJson user) {
    Selenide.open(CFG.frontUrl(), LoginPage.class)
        .doLogin(user.username(), user.testData().password());
    mainPage
        .verifyMainPageIsOpened();
    return mainPage;
  }
}
