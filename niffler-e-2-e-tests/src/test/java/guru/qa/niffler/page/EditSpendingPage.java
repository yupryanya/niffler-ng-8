package guru.qa.niffler.page;

import com.codeborne.selenide.SelenideElement;
import guru.qa.niffler.page.components.SpendForm;
import io.qameta.allure.Step;

import java.util.Date;

import static com.codeborne.selenide.Selenide.$;

public class EditSpendingPage {

  private final SelenideElement saveButton = $("#save");

  private SpendForm spendForm = new SpendForm();

  @Step("Edit spending description: {description}")
  public EditSpendingPage editDescription(String description) {
    spendForm
        .setDescription(description);
    saveButton.click();
    return this;
  }

  @Step("Edit spending amount: {amount}")
  public EditSpendingPage editAmount(String amount) {
    spendForm
        .setAmount(amount);
    saveButton.click();
    return this;
  }

  @Step("Edit spending date: {date}")
  public EditSpendingPage editDateWithCalendar(Date date) {
    spendForm
        .updateDateWithCalendar(date);
    saveButton.click();
    return this;
  }
}
