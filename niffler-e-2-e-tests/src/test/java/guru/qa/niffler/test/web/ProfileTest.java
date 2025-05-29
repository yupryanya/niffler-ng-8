package guru.qa.niffler.test.web;

import guru.qa.niffler.jupiter.annotation.Category;
import guru.qa.niffler.jupiter.annotation.User;
import guru.qa.niffler.model.CategoryJson;
import guru.qa.niffler.model.UserJson;
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
    mainPage
        .navigateToProfilePage();
    profilePage
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
        .navigateToProfilePage();
    profilePage
        .switchArchivedCategoriesToVisible()
        .verifyCategoryIsDisplayed(category.name());
  }
}