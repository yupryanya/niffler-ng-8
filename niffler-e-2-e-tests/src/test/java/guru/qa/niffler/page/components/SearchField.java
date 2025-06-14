package guru.qa.niffler.page.components;

import com.codeborne.selenide.SelenideElement;
import io.qameta.allure.Step;

public class SearchField extends BaseComponent<SearchField> {
  private final String searchInput = "input[aria-label='search']";
  private final String clearButton = "#input-clear";

  public SearchField(SelenideElement self) {
    super(self);
  }

  @Step("Enter search input: {description}")
  public SearchField enterSearchValue(String value) {
    self.$(searchInput).setValue(value).pressEnter();
    return this;
  }

  @Step("Clear search input if not empty")
  public SearchField clearIfNotEmpty() {
    if (self.$(clearButton).isDisplayed()) {
      self.$(clearButton).click();
    }
    return this;
  }

  @Step("Search for description: {description}")
  public SearchField search(String value) {
    clearIfNotEmpty();
    enterSearchValue(value);
    return this;
  }
}