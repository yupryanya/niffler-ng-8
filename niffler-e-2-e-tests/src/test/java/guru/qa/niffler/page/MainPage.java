package guru.qa.niffler.page;

import com.codeborne.selenide.SelenideElement;
import guru.qa.niffler.condition.SpendsConditions;
import guru.qa.niffler.model.SpendJson;
import guru.qa.niffler.model.statistics.Bubble;
import guru.qa.niffler.page.components.AlertDialog;
import guru.qa.niffler.page.components.SpendTable;
import guru.qa.niffler.page.components.StatisticsComponent;
import guru.qa.niffler.utils.ScreenDiffResult;

import java.awt.image.BufferedImage;
import java.util.List;

import static com.codeborne.selenide.CollectionCondition.empty;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;
import static guru.qa.niffler.condition.StatConditions.*;
import static org.junit.jupiter.api.Assertions.assertFalse;

public class MainPage {
  private final SelenideElement statistics = $("#stat");
  private final SelenideElement spends = $("#spendings");
  private final SelenideElement searchField = $("input[aria-label='search']");
  private final SelenideElement clearSearchButton = $("#input-clear");
  private final SelenideElement addSpendingLink = $("a[href*='/spending']");
  private final SelenideElement deleteSpendButton = $("#delete");

  private StatisticsComponent statisticsComponent = new StatisticsComponent();
  private SpendTable spendTable = new SpendTable();

  public MainPage searchSpendByDescription(String description) {
    if (clearSearchButton.isDisplayed()) {
      clearSearchButton.click();
    }
    searchField.setValue(description).pressEnter();
    return this;
  }

  public EditSpendingPage editSpend(String spendDescription) {
    searchSpendByDescription(spendDescription);
    spendTable.getCellsBySpendDescription(spendDescription)
        .get(5)
        .click();
    return new EditSpendingPage();
  }

  public AddSpendingPage addSpend() {
    addSpendingLink.click();
    return new AddSpendingPage();
  }

  public MainPage deleteSpend(String spendDescription) {
    spendTable.getCellsBySpendDescription(spendDescription)
        .get(0)
        .click();
    deleteSpendButton.click();
    new AlertDialog().getDeleteButton().click();
    return this;
  }

  public void checkThatTableContains(SpendJson spend) {
    searchSpendByDescription(spend.description());
    spendTable.getTableRows()
        .should(SpendsConditions.spends(spend));
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

  public MainPage verifyStatisticsLegendMatches(Bubble... bubbles) {
    statisticsComponent.getStatisticsLegend()
        .should(statBubblesExactOrder(bubbles));
    return this;
  }

  public MainPage verifyStatisticsLegendMatchesInAnyOrder(Bubble... bubbles) {
    statisticsComponent.getStatisticsLegend()
        .should(statBubblesAnyOrder(bubbles));
    return this;
  }

  public MainPage verifyStatisticsLegendContains(Bubble... bubbles) {
    statisticsComponent.getStatisticsLegend()
        .should(statBubblesContainsAll(bubbles));
    return this;
  }

  public MainPage verifyStatisticsLegendIsEmpty() {
    statisticsComponent.getStatisticsLegend()
        .shouldBe(empty);
    return this;
  }

  public void verifySpendTableMatches(List<SpendJson> spends) {
    spendTable.getTableRows()
        .should(SpendsConditions.spends(spends.toArray(new SpendJson[0])));
  }
}
