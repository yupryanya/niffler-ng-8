package guru.qa.niffler.test.web;

import com.codeborne.selenide.Selenide;
import guru.qa.niffler.jupiter.annotation.ScreenshotTest;
import guru.qa.niffler.jupiter.annotation.User;
import guru.qa.niffler.model.UserJson;
import guru.qa.niffler.page.LoginPage;
import guru.qa.niffler.page.ProfilePage;
import org.junit.jupiter.api.Test;

import java.awt.image.BufferedImage;
import java.io.IOException;

public class ProfilePictureTest extends BaseTestWeb {
  @User()
  @Test
  @ScreenshotTest(value = "img/default-profile-picture.png", rewriteExpected = true)
  void defaultProfilePictureIsDisplayed(UserJson user, BufferedImage expectedImage) throws IOException {
    Selenide.open(CFG.authUrl(), LoginPage.class)
        .doSuccessLogin(user.username(), user.testData().password())
        .verifyMainPageIsOpened();
    Selenide.open(ProfilePage.URL, ProfilePage.class)
        .verifyDefaultProfilePictureIsDisplayed(expectedImage);
  }

  @User()
  @Test
  @ScreenshotTest(value = "img/profile-picture.png")
  void profilePictureShouldBeDisplayedAfterLoad(UserJson user, BufferedImage expectedImage) throws IOException {
    Selenide.open(CFG.authUrl(), LoginPage.class)
        .doSuccessLogin(user.username(), user.testData().password())
        .verifyMainPageIsOpened();
    Selenide.open(ProfilePage.URL, ProfilePage.class)
        .setProfilePicture("img/profile-picture.png")
        .verifyProfilePictureIsDisplayed(expectedImage);
  }
}
