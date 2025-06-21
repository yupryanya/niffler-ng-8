package guru.qa.niffler.test.web;

import guru.qa.niffler.jupiter.annotation.ApiLogin;
import guru.qa.niffler.jupiter.annotation.ScreenshotTest;
import guru.qa.niffler.jupiter.annotation.User;
import guru.qa.niffler.model.UserJson;
import org.junit.jupiter.api.Test;

import java.awt.image.BufferedImage;
import java.io.IOException;

public class ProfilePictureTest extends BaseTestWeb {
  @User
  @ApiLogin
  @Test
  @ScreenshotTest(value = "img/default-profile-picture.png")
  void defaultProfilePictureIsDisplayed(UserJson user, BufferedImage expectedImage) throws IOException {
    mainPage
        .getHeader()
        .toProfilePage()
        .verifyDefaultProfilePictureIsDisplayed(expectedImage);
  }

  @User
  @ApiLogin
  @Test
  @ScreenshotTest(value = "img/profile-picture.png")
  void profilePictureShouldBeDisplayedAfterLoad(UserJson user, BufferedImage expectedImage) throws IOException {
    mainPage
        .getHeader()
        .toProfilePage()
        .setProfilePicture("img/profile-picture.png")
        .verifyProfilePictureIsDisplayed(expectedImage);
  }
}
