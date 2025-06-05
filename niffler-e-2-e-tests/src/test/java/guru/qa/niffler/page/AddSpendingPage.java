package guru.qa.niffler.page;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.SelenideElement;
import guru.qa.niffler.model.SpendJson;

import static com.codeborne.selenide.Selenide.$;

public class AddSpendingPage {
  private final SelenideElement amountInput = $("#amount");
  private final SelenideElement currencySelect = $("#currency");
  private final ElementsCollection currencyOptions = $("ul[role='listbox']").$$("li");
  private final SelenideElement categoryInput = $("#category");
  private final SelenideElement dateInput = $("input[name='date']");
  private final SelenideElement descriptionInput = $("#description");
  private final SelenideElement addButton = $("#save");

  public MainPage fillAllFields(SpendJson spend) {
    amountInput.setValue(spend.amount().toString());
    currencySelect.click();
    currencyOptions.find(Condition.partialText(spend.currency().name())).click();
    categoryInput.setValue(spend.category().name());
    dateInput.setValue(String.valueOf(spend.spendDate()));
    descriptionInput.setValue(spend.description());
    addButton.click();
    return new MainPage();
  }
}
