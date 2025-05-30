package guru.qa.niffler.page.components;

import com.codeborne.selenide.SelenideDriver;
import com.codeborne.selenide.SelenideElement;

import static com.codeborne.selenide.Condition.text;

public class AlertDialog extends BaseComponent {
  private final SelenideElement dialog;

  public AlertDialog(SelenideDriver driver) {
    super(driver);
    this.dialog = driver.$("div[role='dialog']");
  }

  public SelenideElement getDeleteButton() {
    return dialog.$$("button").findBy(text("Delete"));
  }
}
