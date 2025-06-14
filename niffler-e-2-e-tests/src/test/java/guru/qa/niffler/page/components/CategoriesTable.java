package guru.qa.niffler.page.components;

import com.codeborne.selenide.SelenideElement;
import io.qameta.allure.Step;

import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;

public class CategoriesTable extends BaseComponent<CategoriesTable> {
  private final String addCategoryInput = "#category";
  private final String categories = "div.MuiButtonBase-root[role='button']";
  private final SelenideElement showArchivedCategoriesToggle = $("input[type='checkbox']");

  public CategoriesTable(SelenideElement self) {
    super(self);
  }

  private SelenideElement findCategory(String name) {
    return self.$$(categories).findBy(text(name));
  }

  @Step("Verify category with name {name} is displayed")
  public CategoriesTable verifyCategoryIsDisplayed(String name) {
    findCategory(name).should(visible);
    return this;
  }

  @Step("Switch archived categories to visible")
  public CategoriesTable switchArchivedCategoriesToVisible() {
    showArchivedCategoriesToggle.click();
    return this;
  }

  @Step("Add a new category with name {categoryName}")
  public CategoriesTable addCategory(String categoryName) {
    self.$(addCategoryInput).setValue(categoryName).pressEnter();
    return this;
  }
}
