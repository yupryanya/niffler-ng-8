package guru.qa.niffler.test.web;

import guru.qa.niffler.jupiter.annotation.User;
import guru.qa.niffler.model.UserJson;
import org.junit.jupiter.api.Test;

public class ProfileUsernameTest extends BaseTestWeb {
  @User()
  @Test
  void verifyUserNameIsDisplayedOnProfilePage(UserJson user) {
    login(user);
    mainPage
        .getHeader()
        .toProfilePage()
        .verifyUserNameIsDisplayed(user.username());
  }

  @User()
  @Test
  void verifyUserNameFieldIsDisabledOnProfilePage(UserJson user) {
    login(user);
    mainPage
        .getHeader()
        .toProfilePage()
        .verifyUserNameIsDisabled();
  }

  @User()
  @Test
  void verifyUserCanEditUserNameOnProfilePage(UserJson user) {
    login(user);
    mainPage
        .getHeader()
        .toProfilePage()
        .updateName("NewUserName")
        .verifyNameIsUpdated("NewUserName");
  }
}
