package guru.qa.niffler.page;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.SelenideDriver;
import com.codeborne.selenide.SelenideElement;
import guru.qa.niffler.model.SpendJson;

public class AddSpendingPage extends BasePage{
  private final SelenideElement amountInput;
  private final SelenideElement currencySelect;
  private final ElementsCollection currencyOptions;
  private final SelenideElement categoryInput;
  private final SelenideElement dateInput;
  private final SelenideElement descriptionInput;
  private final SelenideElement addButton;

  public AddSpendingPage(SelenideDriver driver) {
    super(driver);
    this.amountInput = driver.$("#amount");
    this.currencySelect = driver.$("#currency");
    this.currencyOptions = driver.$("ul[role='listbox']").$$("li");
    this.categoryInput = driver.$("#category");
    this.dateInput = driver.$("input[name='date']");
    this.descriptionInput = driver.$("#description");
    this.addButton = driver.$("#save");
  }

  public void fillAllFields(SpendJson spend) {
    amountInput.setValue(spend.amount().toString());
    currencySelect.click();
    currencyOptions.find(Condition.partialText(spend.currency().name())).click();
    categoryInput.setValue(spend.category().name());
    dateInput.setValue(String.valueOf(spend.spendDate()));
    descriptionInput.setValue(spend.description());
    addButton.click();
  }
}
