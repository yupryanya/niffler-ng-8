package guru.qa.niffler.page;

import com.codeborne.selenide.SelenideDriver;
import com.codeborne.selenide.SelenideElement;

public class EditSpendingPage extends BasePage {
  private final SelenideElement descriptionInput;
  private final SelenideElement amountInput;
  private final SelenideElement submitBtn;

  public EditSpendingPage(SelenideDriver driver) {
    super(driver);
    this.descriptionInput = driver.$("#description");
    this.amountInput = driver.$("#amount");
    this.submitBtn = driver.$("#save");
  }

  public void editDescription(String description) {
    descriptionInput.clear();
    descriptionInput.setValue(description);
    submitBtn.click();
  }

  public void editAmount(String amount) {
    amountInput.clear();
    amountInput.setValue(amount);
    submitBtn.click();
  }
}
