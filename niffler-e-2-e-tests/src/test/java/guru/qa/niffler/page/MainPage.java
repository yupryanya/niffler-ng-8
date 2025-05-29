package guru.qa.niffler.page;

import com.codeborne.selenide.SelenideDriver;
import com.codeborne.selenide.SelenideElement;
import guru.qa.niffler.page.components.SpendTable;
import guru.qa.niffler.page.components.StatisticsComponent;
import lombok.Getter;

import static com.codeborne.selenide.Condition.visible;

public class MainPage extends BasePage {
  private final SelenideElement statistics;
  private final SelenideElement spends;
  private final SelenideElement menuButton;
  private final SelenideElement profileLink;
  private final SelenideElement friendsLink;
  private final SelenideElement allPeopleLink;


  @Getter
  private final SpendTable spendTable;
  @Getter
  private final StatisticsComponent statisticsComponent;


  public MainPage(SelenideDriver driver) {
    super(driver);
    this.statistics = driver.$("#stat");
    this.spends = driver.$("#spendings");
    this.menuButton = driver.$("button[aria-label='Menu']");
    this.profileLink = driver.$(".nav-link[href*='profile']");
    this.friendsLink = driver.$(".nav-link[href*='friends']");
    this.allPeopleLink = driver.$(".nav-link[href*='all']");
    this.spendTable = new SpendTable(driver);
    this.statisticsComponent = new StatisticsComponent(driver);
  }

  public MainPage verifyMainPageIsOpened() {
    statistics.should(visible);
    spends.should(visible);
    return this;
  }

  public void navigateToProfilePage() {
    menuButton.click();
    profileLink.click();
  }

  public void navigateToFriendsPage() {
    menuButton.click();
    friendsLink.click();
  }

  public void navigateToAllPeoplePage() {
    menuButton.click();
    allPeopleLink.click();
  }
}