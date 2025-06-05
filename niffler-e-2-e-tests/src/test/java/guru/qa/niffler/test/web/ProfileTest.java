package guru.qa.niffler.test.web;

import com.codeborne.selenide.Selenide;
import guru.qa.niffler.jupiter.annotation.Category;
import guru.qa.niffler.jupiter.annotation.User;
import guru.qa.niffler.model.CategoryJson;
import guru.qa.niffler.model.UserJson;
import guru.qa.niffler.page.LoginPage;
import guru.qa.niffler.page.ProfilePage;
import org.junit.jupiter.api.Test;

public class ProfileTest extends BaseTestWeb {
  @User(
      username = TEST_USER_NAME,
      categories = {
          @Category(
              archived = false
          )
      }
  )
  @Test
  void activeCategoryShouldBePresentInCategoriesList(UserJson user) {
    final CategoryJson category = user.testData().categories().getFirst();

    Selenide.open(CFG.authUrl(), LoginPage.class)
        .doSuccessLogin(user.username(), user.testData().password())
        .verifyMainPageIsOpened();
    Selenide.open(ProfilePage.URL, ProfilePage.class)
        .verifyCategoryIsDisplayed(category.name());
  }

  @User(
      username = TEST_USER_NAME,
      categories = {
          @Category(
              archived = true
          )
      }
  )
  @Test
  void archivedCategoryShouldBePresentInCategoriesList(UserJson user) {
    final CategoryJson category = user.testData().categories().getFirst();

    Selenide.open(CFG.authUrl(), LoginPage.class)
        .doSuccessLogin(user.username(), user.testData().password())
        .verifyMainPageIsOpened();
    Selenide.open(ProfilePage.URL, ProfilePage.class)
        .switchArchivedCategoriesToVisible()
        .verifyCategoryIsDisplayed(category.name());
  }
}