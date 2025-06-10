package guru.qa.niffler.test.web;

import guru.qa.niffler.jupiter.annotation.ScreenshotTest;
import guru.qa.niffler.jupiter.annotation.User;
import guru.qa.niffler.model.UserJson;
import org.junit.jupiter.api.Test;

import java.awt.image.BufferedImage;
import java.io.IOException;

public class ProfilePictureTest extends BaseTestWeb {
  @User()
  @Test
  @ScreenshotTest(value = "img/default-profile-picture.png")
  void defaultProfilePictureIsDisplayed(UserJson user, BufferedImage expectedImage) throws IOException {
    login(user);
    mainPage
        .getHeader()
        .toProfilePage()
        .verifyDefaultProfilePictureIsDisplayed(expectedImage);
  }

  @User()
  @Test
  @ScreenshotTest(value = "img/profile-picture.png")
  void profilePictureShouldBeDisplayedAfterLoad(UserJson user, BufferedImage expectedImage) throws IOException {
    login(user);
    mainPage
        .getHeader()
        .toProfilePage()
        .setProfilePicture("img/profile-picture.png")
        .verifyProfilePictureIsDisplayed(expectedImage);
  }
}
