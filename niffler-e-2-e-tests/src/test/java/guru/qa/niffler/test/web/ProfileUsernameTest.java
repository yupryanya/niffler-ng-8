package guru.qa.niffler.test.web;

import guru.qa.niffler.jupiter.annotation.ApiLogin;
import guru.qa.niffler.jupiter.annotation.User;
import guru.qa.niffler.model.UserJson;
import org.junit.jupiter.api.Test;

public class ProfileUsernameTest extends BaseTestWeb {
  @User
  @ApiLogin
  @Test
  void verifyUserNameIsDisplayedOnProfilePage(UserJson user) {
    mainPage
        .getHeader()
        .toProfilePage()
        .verifyUserNameIsDisplayed(user.username());
  }

  @User
  @ApiLogin
  @Test
  void verifyUserNameFieldIsDisabledOnProfilePage(UserJson user) {
    mainPage
        .getHeader()
        .toProfilePage()
        .verifyUserNameIsDisabled();
  }

  @User
  @ApiLogin
  @Test
  void verifyUserCanEditUserNameOnProfilePage(UserJson user) {
    mainPage
        .getHeader()
        .toProfilePage()
        .updateName("NewUserName")
        .verifyNameIsUpdated("NewUserName");
  }
}
