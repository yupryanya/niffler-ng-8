package guru.qa.niffler.page.components;

import com.codeborne.selenide.SelenideElement;

import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Selenide.$;

public class AlertDialog {
  private final SelenideElement dialog = $("div[role='dialog']");

  public SelenideElement getDeleteButton() {
    return dialog.$$("button").findBy(text("Delete"));
  }
}
