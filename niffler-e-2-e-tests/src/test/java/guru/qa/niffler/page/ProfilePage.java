package guru.qa.niffler.page;

import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.SelenideDriver;
import com.codeborne.selenide.SelenideElement;
import guru.qa.niffler.config.Config;
import guru.qa.niffler.utils.ScreenDiffResult;
import lombok.SneakyThrows;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;

import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Condition.visible;
import static org.junit.jupiter.api.Assertions.assertFalse;

public class ProfilePage extends BasePage {
  protected static final Config CFG = Config.getInstance();
  public static final String URL = CFG.frontUrl() + "profile";

  private final SelenideElement showArchivedCategoriesToggle;
  private final ElementsCollection categories;
  private final SelenideElement profilePicture;
  private final SelenideElement defaultIcon;
  private final SelenideElement uploadNewPictureButton;
  private final SelenideElement saveChangesButton;

  public ProfilePage(SelenideDriver driver) {
    super(driver);
    this.showArchivedCategoriesToggle = driver.$("input[type='checkbox']");
    this.categories = driver.$$("div.MuiButtonBase-root[role='button']");
    this.profilePicture = driver.$("#image__input").parent().$("img");
    this.defaultIcon = driver.$("svg[data-testid='PersonIcon']");
    this.uploadNewPictureButton = driver.$("#image__input");
    this.saveChangesButton = driver.$("button[type='submit']");
  }

  private String getUrl() {
    return CFG.frontUrl() + "people/friends";
  }

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
