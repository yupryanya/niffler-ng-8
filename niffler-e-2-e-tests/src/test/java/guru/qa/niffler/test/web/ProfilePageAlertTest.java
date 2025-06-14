package guru.qa.niffler.test.web;

import guru.qa.niffler.jupiter.annotation.User;
import guru.qa.niffler.model.UserJson;
import guru.qa.niffler.page.ProfilePage;
import org.junit.jupiter.api.Test;

import static guru.qa.niffler.common.messages.ApplicationWarnings.ProfileAlertMessages.*;
import static guru.qa.niffler.utils.RandomDataUtils.newCategoryName;
import static guru.qa.niffler.utils.RandomDataUtils.randomString;

public class ProfilePageAlertTest extends BaseTestWeb {
  @User()
  @Test
  void shouldDisplaySuccessAlertWhenProfileNameUpdated(UserJson user) {
    login(user);
    mainPage
        .getHeader()
        .toProfilePage()
        .updateName("NewUserName")
        .checkAlertMessage(PROFILE_UPDATED);
  }

  @User()
  @Test
  void shouldDisplaySuccessAlertWhenCategoryAdded(UserJson user) {
    String categoryName = newCategoryName();
    login(user);
    mainPage
        .getHeader()
        .toProfilePage()
        .getCategoriesTable()
        .addCategory(categoryName);
    new ProfilePage()
        .checkAlertMessage(String.format(CATEGORY_ADDED, categoryName));
  }

  @User()
  @Test
  void shouldDisplayErrorAlertWhenCategoryNameTooLong(UserJson user) {
    String categoryName = randomString(100);
    login(user);
    mainPage
        .getHeader()
        .toProfilePage()
        .getCategoriesTable()
        .addCategory(categoryName);
    new ProfilePage()
        .checkAlertMessage(String.format(ERROR_WHILE_ADDING_CATEGORY, categoryName));
  }
}