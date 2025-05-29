package guru.qa.niffler.page.components;

import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.SelenideDriver;
import com.codeborne.selenide.SelenideElement;
import guru.qa.niffler.condition.SpendsConditions;
import guru.qa.niffler.model.SpendJson;
import lombok.Getter;

import java.util.List;

import static com.codeborne.selenide.Condition.text;

public class SpendTable extends BaseComponent {
  @Getter
  private final ElementsCollection tableRows;
  private final SelenideElement addSpendingLink;
  private final SelenideElement deleteSpendButton;
  private final SelenideElement searchField;
  private final SelenideElement clearSearchButton;

  @Getter
  AlertDialog alertDialog;

  public SpendTable(SelenideDriver driver) {
    super(driver);
    this.tableRows = driver.$$("#spendings tbody tr");
    this.addSpendingLink = driver.$("a[href*='/spending']");
    this.deleteSpendButton = driver.$("#delete");
    this.searchField = driver.$("input[aria-label='search']");
    this.clearSearchButton = driver.$("#input-clear");
    this.alertDialog = new AlertDialog(driver);
  }

  public ElementsCollection getCellsBySpendDescription(String spendDescription) {
    return tableRows.findBy(text(spendDescription))
        .$$("td");
  }

  public void editSpend(String spendDescription) {
    searchSpendByDescription(spendDescription);
    getCellsBySpendDescription(spendDescription)
        .get(5)
        .click();
  }

  public void addSpend() {
    addSpendingLink.click();
  }

  public SpendTable deleteSpend(String spendDescription) {
    getCellsBySpendDescription(spendDescription)
        .get(0)
        .click();
    deleteSpendButton.click();
    alertDialog.getDeleteButton().click();
    return this;
  }

  public SpendTable searchSpendByDescription(String description) {
    if (clearSearchButton.isDisplayed()) {
      clearSearchButton.click();
    }
    searchField.setValue(description).pressEnter();
    return this;
  }

  public void checkThatTableContains(SpendJson spend) {
    searchSpendByDescription(spend.description());
    getTableRows()
        .should(SpendsConditions.spends(spend));
  }

  public void verifySpendTableMatches(List<SpendJson> spends) {
    getTableRows()
        .should(SpendsConditions.spends(spends.toArray(new SpendJson[0])));
  }
}
