package guru.qa.niffler.page;

import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.Selenide;
import com.codeborne.selenide.SelenideElement;
import guru.qa.niffler.config.Config;
import guru.qa.niffler.utils.ScreenDiffResult;
import io.qameta.allure.Step;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;

import static com.codeborne.selenide.Condition.*;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$$;
import static org.junit.jupiter.api.Assertions.assertFalse;

public class ProfilePage {
  protected static final Config CFG = Config.getInstance();
  public static final String URL = CFG.frontUrl() + "profile";

  private final SelenideElement showArchivedCategoriesToggle = $("input[type='checkbox']");
  private final ElementsCollection categories = $$("div.MuiButtonBase-root[role='button']");
  private final SelenideElement profilePicture = $("#image__input").parent().$("img");
  private final SelenideElement defaultIcon = $("svg[data-testid='PersonIcon']");
  private final SelenideElement uploadNewPictureButton = $("#image__input");
  private final SelenideElement saveChangesButton = $("button[type='submit']");
  private final SelenideElement nameInput = $("#name");
  private final SelenideElement usernameInput = $("#username");

  public SelenideElement findCategory(String name) {
    return categories.findBy(text(name));
  }

  @Step("Verify category with name {name} is displayed")
  public ProfilePage verifyCategoryIsDisplayed(String name) {
    findCategory(name).should(visible);
    return this;
  }

  @Step("Switch archived categories to visible")
  public ProfilePage switchArchivedCategoriesToVisible() {
    showArchivedCategoriesToggle.click();
    return this;
  }

  @Step("Verify profile picture is displayed")
  public ProfilePage verifyProfilePictureIsDisplayed(BufferedImage expectedImage) {
    BufferedImage actualImage = null;
    try {
      actualImage = ImageIO.read(profilePicture.screenshot());
    } catch (IOException e) {
      throw new RuntimeException("Failed to read the profile picture image", e);
    }
    assertFalse(new ScreenDiffResult(expectedImage, actualImage));
    return this;
  }

  @Step("Verify default profile picture is displayed")
  public ProfilePage verifyDefaultProfilePictureIsDisplayed(BufferedImage expectedImage) {
    BufferedImage actualImage = null;
    try {
      actualImage = ImageIO.read(defaultIcon.screenshot());
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
    assertFalse(new ScreenDiffResult(expectedImage, actualImage));
    return this;
  }

  @Step("Set profile picture from path {path}")
  public ProfilePage setProfilePicture(String path) {
    uploadNewPictureButton.uploadFromClasspath(path);
    saveChangesButton.click();
    return this;
  }

  @Step("Edit user name to {newUserName}")
  public ProfilePage updateName(String newName) {
    nameInput.setValue(newName);
    saveChangesButton.click();
    return this;
  }

  @Step("Verify user name is updated to {newUserName}")
  public ProfilePage verifyNameIsUpdated(String newUserName) {
    Selenide.refresh();
    nameInput.shouldHave(value(newUserName));
    return this;
  }

  @Step("Verify user name is disabled")
  public ProfilePage verifyUserNameIsDisabled() {
    usernameInput.shouldBe(disabled);
    return this;
  }

  @Step("Verify user name is displayed")
  public ProfilePage verifyUserNameIsDisplayed(String username) {
    usernameInput.shouldHave(value(username));
    return this;
  }
}
