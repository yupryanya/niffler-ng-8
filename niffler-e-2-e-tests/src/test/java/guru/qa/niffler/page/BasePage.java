package guru.qa.niffler.page;

import com.codeborne.selenide.SelenideElement;

import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;

public class BasePage<T extends BasePage> {
  protected final SelenideElement alert = $("div[role='alert']");

  public T checkAlertMessage(String message) {
    alert.should(visible)
        .should(text(message));
    return (T) this;
  }
}
