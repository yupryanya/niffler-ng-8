package guru.qa.niffler.test.web;

import com.codeborne.selenide.Selenide;
import guru.qa.niffler.jupiter.annotation.Category;
import guru.qa.niffler.jupiter.annotation.User;
import guru.qa.niffler.model.CategoryJson;
import guru.qa.niffler.model.UserJson;
import guru.qa.niffler.page.ProfilePage;
import org.junit.jupiter.api.Test;

public class ProfileTest extends BaseTestWeb {
  @User(
      categories = {
          @Category(
              archived = false
          )
      }
  )
  @Test
  void activeCategoryShouldBePresentInCategoriesList(UserJson user) {
    final CategoryJson category = user.testData().categories().getFirst();
    login(user);
    Selenide.open(ProfilePage.URL, ProfilePage.class)
        .getCategoriesTable()
        .verifyCategoryIsDisplayed(category.name());
  }

  @User(
      categories = {
          @Category(
              archived = true
          )
      }
  )
  @Test
  void archivedCategoryShouldBePresentInCategoriesList(UserJson user) {
    final CategoryJson category = user.testData().categories().getFirst();
    login(user);
    mainPage
        .getHeader()
        .toProfilePage()
        .getCategoriesTable()
        .switchArchivedCategoriesToVisible()
        .verifyCategoryIsDisplayed(category.name());
  }
}