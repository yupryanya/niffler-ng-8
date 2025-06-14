package guru.qa.niffler.page;

import com.codeborne.selenide.SelenideElement;
import guru.qa.niffler.page.components.Header;
import guru.qa.niffler.page.components.SpendsTable;
import guru.qa.niffler.page.components.StatisticsDiagram;
import io.qameta.allure.Step;
import lombok.Getter;

import static com.codeborne.selenide.Selenide.$;

public class MainPage extends BasePage<MainPage> {
  private final SelenideElement spendsTableContainer = $("#spendings");
  private final SelenideElement statisticsDiagramContainer = $("#stat");

  @Getter
  private Header header = new Header();
  @Getter
  private StatisticsDiagram statisticsDiagram = new StatisticsDiagram(statisticsDiagramContainer);
  @Getter
  private SpendsTable spendsTable = new SpendsTable(spendsTableContainer);

  @Step("Verify that main page is opened")
  public MainPage verifyMainPageIsOpened() {
    getHeader().verifyHeaderTitle();
    getSpendsTable().verifySpendTableDisplayed();
    return this;
  }
}
