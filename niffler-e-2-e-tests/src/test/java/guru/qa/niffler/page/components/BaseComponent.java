package guru.qa.niffler.page.components;

import com.codeborne.selenide.SelenideDriver;

public class BaseComponent<T extends BaseComponent<T>> {

  public BaseComponent(SelenideDriver driver) {
  }
}
