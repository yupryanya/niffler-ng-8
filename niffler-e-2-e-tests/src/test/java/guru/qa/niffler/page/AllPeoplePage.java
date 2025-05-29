package guru.qa.niffler.page;

import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.SelenideDriver;
import com.codeborne.selenide.SelenideElement;
import guru.qa.niffler.config.Config;
import guru.qa.niffler.model.UserJson;

import java.util.List;

import static com.codeborne.selenide.Condition.*;
import static com.codeborne.selenide.Selectors.byLinkText;
import static com.codeborne.selenide.Selectors.byText;

public class AllPeoplePage extends BasePage {
  protected static final Config CFG = Config.getInstance();

  private static final String ALL_PEOPLE_TAB_LABEL = "All people";

  private final ElementsCollection allPeopleTableItems;
  private final SelenideElement searchField;
  private final SelenideElement clearSearchButton;
  private final SelenideElement allPeopleTabLabel;

  public AllPeoplePage(SelenideDriver driver) {
    super(driver);
    this.allPeopleTableItems = driver.$$("#all tr");
    this.searchField = driver.$("input[aria-label='search']");
    this.clearSearchButton = driver.$("#input-clear");
    this.allPeopleTabLabel = driver.$(byLinkText(ALL_PEOPLE_TAB_LABEL));
  }

  public AllPeoplePage verifyPageIsOpened() {
    allPeopleTabLabel
        .shouldHave(attribute("aria-selected", "true"));
    return this;
  }

  public AllPeoplePage searchFriend(String friend) {
    if (clearSearchButton.isDisplayed()) {
      clearSearchButton.click();
    }
    searchField.setValue(friend).pressEnter();
    return this;
  }

  public AllPeoplePage verifyOutcomeRequestTo(String outcome) {
    searchFriend(outcome);
    allPeopleTableItems
        .find(text(outcome))
        .$(byText("Waiting..."))
        .shouldBe(visible);
    return this;
  }

  public AllPeoplePage verifyOutcomeRequests(List<UserJson> outcomeInvitations) {
    for (UserJson outcome : outcomeInvitations) {
      verifyOutcomeRequestTo(outcome.username());
    }
    return this;
  }
}