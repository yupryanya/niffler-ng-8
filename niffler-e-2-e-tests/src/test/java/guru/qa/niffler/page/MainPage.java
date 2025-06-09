package guru.qa.niffler.page;

import guru.qa.niffler.page.components.Header;
import guru.qa.niffler.page.components.SpendsTable;
import guru.qa.niffler.page.components.StatisticsDiagram;
import io.qameta.allure.Step;
import lombok.Getter;

public class MainPage {
  @Getter
  private Header header = new Header();
  @Getter
  private StatisticsDiagram statisticsDiagram = new StatisticsDiagram();
  @Getter
  private SpendsTable spendsTable = new SpendsTable();

  @Step("Verify that main page is opened")
  public MainPage verifyMainPageIsOpened() {
    getHeader().verifyHeaderTitle();
    getSpendsTable().verifySpendTableDisplayed();
    return this;
  }
}
