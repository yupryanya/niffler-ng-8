package guru.qa.niffler.test.web;

import com.codeborne.selenide.Selenide;
import guru.qa.niffler.config.Config;
import guru.qa.niffler.jupiter.extension.BrowserExtension;
import guru.qa.niffler.model.UserJson;
import guru.qa.niffler.page.AllPeoplePage;
import guru.qa.niffler.page.FriendsPage;
import guru.qa.niffler.page.LoginPage;
import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith(BrowserExtension.class)
public class BaseTestWeb {
  protected static final Config CFG = Config.getInstance();

  protected static final String TEST_USER_NAME = "testuser";

  protected final AllPeoplePage allPeoplePage = new AllPeoplePage();
  protected final FriendsPage friendsPage = new FriendsPage();

  protected void login(UserJson user) {
    Selenide.open(CFG.frontUrl(), LoginPage.class)
        .doSuccessLogin(user.username(), user.testData().password())
        .verifyMainPageIsOpened();
  }
}
