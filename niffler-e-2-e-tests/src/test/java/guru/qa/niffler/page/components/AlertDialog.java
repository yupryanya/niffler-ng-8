package guru.qa.niffler.page.components;

import com.codeborne.selenide.SelenideElement;

import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Selenide.$;

public class AlertDialog extends BaseComponent<AlertDialog> {

  public AlertDialog() {
    super($("div[role='dialog']"));
  }

  public AlertDialog(SelenideElement self) {
    super(self);
  }

  public SelenideElement getDeleteButton() {
    return self.$$("button").findBy(text("Delete"));
  }

  public SelenideElement getDeclineButton() {
    return self.$$("button").findBy(text("Decline"));
  }
}
