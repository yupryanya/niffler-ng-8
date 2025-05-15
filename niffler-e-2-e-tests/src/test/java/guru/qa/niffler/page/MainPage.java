package guru.qa.niffler.page;

import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.SelenideElement;
import guru.qa.niffler.page.components.AlertDialog;
import guru.qa.niffler.page.components.StatisticsComponent;
import guru.qa.niffler.utils.ScreenDiffResult;

import java.awt.image.BufferedImage;

import static com.codeborne.selenide.CollectionCondition.empty;
import static com.codeborne.selenide.CollectionCondition.exactTextsCaseSensitiveInAnyOrder;
import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$$;
import static org.junit.jupiter.api.Assertions.assertFalse;

public class MainPage {
  private final ElementsCollection tableRows = $$("#spendings tbody tr");
  private final SelenideElement statistics = $("#stat");
  private final SelenideElement spends = $("#spendings");
  private final SelenideElement searchField = $("input[aria-label='search']");
  private final SelenideElement clearSearchButton = $("#input-clear");
  private final SelenideElement addSpendingLink = $("a[href*='/spending']");
  private final SelenideElement deleteSpendButton = $("#delete");

  private StatisticsComponent statisticsComponent = new StatisticsComponent();

  public MainPage searchSpendByDescription(String description) {
    if (clearSearchButton.isDisplayed()) {
      clearSearchButton.click();
    }
    searchField.setValue(description).pressEnter();
    return this;
  }

  public EditSpendingPage editSpend(String spendDescription) {
    searchSpendByDescription(spendDescription);
    tableRows.find(text(spendDescription))
        .$$("td")
        .get(5)
        .click();
    return new EditSpendingPage();
  }

  public AddSpendingPage addSpend() {
    addSpendingLink.click();
    return new AddSpendingPage();
  }

  public MainPage deleteSpend(String spendDescription) {
    tableRows.find(text(spendDescription))
        .$$("td")
        .get(0)
        .click();
    deleteSpendButton.click();
    new AlertDialog().getDeleteButton().click();
    return this;
  }

  public void checkThatTableContains(String spendDescription) {
    searchSpendByDescription(spendDescription);
    tableRows.find(text(spendDescription))
        .should(visible);
  }

  public MainPage verifyMainPageIsOpened() {
    statistics.should(visible);
    spends.should(visible);
    return this;
  }

  public MainPage verifyStatisticsImageMatchesExpected(BufferedImage expectedImage) {
    BufferedImage actualImage = statisticsComponent.getStatisticImage();
    assertFalse(new ScreenDiffResult(expectedImage, actualImage));
    return this;
  }

  public MainPage verifyStatisticsLegendContains(String... spends) {
    statisticsComponent.getStatisticsLegend()
        .should(exactTextsCaseSensitiveInAnyOrder(spends));
    return this;
  }

  public MainPage verifyStatisticsLegendIsEmpty() {
    statisticsComponent.getStatisticsLegend()
        .should(empty);
    return this;
  }
}
