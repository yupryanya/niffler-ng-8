package guru.qa.niffler.page;

import com.codeborne.selenide.SelenideElement;
import guru.qa.niffler.model.SpendJson;
import guru.qa.niffler.page.components.SpendForm;
import io.qameta.allure.Step;

import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Selenide.$;

public class AddSpendingPage {
  private static final String ADD_SPENDING_LABEL = "Add new spending";
  private final SelenideElement addButton = $("#save");

  private SpendForm spendForm = new SpendForm();

  @Step("Verify 'Add spending' page is opened")
  public AddSpendingPage verifyPageIsOpened() {
    $("h2").shouldHave(text(ADD_SPENDING_LABEL));
    return this;
  }

  @Step("Fill all fields with spend data")
  public AddSpendingPage fillAllFields(SpendJson spend) {
    verifyPageIsOpened();
    spendForm
        .setAmount(spend.amount().toString())
        .setCurrency(spend.currency().name())
        .setCategory(spend.category().name())
        .setDate(String.valueOf(spend.spendDate()))
        .setDate(String.valueOf(spend.spendDate()))
        .setDescription(spend.description());
    return this;
  }

  @Step("Click confirm button")
  public MainPage clickConfirmButton() {
    addButton.click();
    return new MainPage();
  }
}