package guru.qa.niffler.page;

import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.SelenideElement;
import guru.qa.niffler.config.Config;
import guru.qa.niffler.utils.ScreenDiffResult;
import lombok.SneakyThrows;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;

import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.*;
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

  public SelenideElement findCategory(String name) {
    return categories.findBy(text(name));
  }

  public ProfilePage verifyCategoryIsDisplayed(String name) {
    findCategory(name).should(visible);
    return this;
  }

  public ProfilePage switchArchivedCategoriesToVisible() {
    showArchivedCategoriesToggle.click();
    return this;
  }

  @SneakyThrows
  public ProfilePage verifyProfilePictureIsDisplayed(BufferedImage expectedImage) {
    BufferedImage actualImage = ImageIO.read(profilePicture.screenshot());
    assertFalse(new ScreenDiffResult(expectedImage, actualImage));
    return this;
  }

  @SneakyThrows
  public ProfilePage verifyDefaultProfilePictureIsDisplayed(BufferedImage expectedImage) {
    BufferedImage actualImage = ImageIO.read(defaultIcon.screenshot());
    assertFalse(new ScreenDiffResult(expectedImage, actualImage));
    return this;
  }

  public ProfilePage setProfilePicture(String path) {
    uploadNewPictureButton.uploadFromClasspath(path);
    saveChangesButton.click();
    return this;
  }
}
