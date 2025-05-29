package guru.qa.niffler.page;

import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.SelenideDriver;
import com.codeborne.selenide.SelenideElement;
import guru.qa.niffler.config.Config;
import guru.qa.niffler.model.UserJson;

import java.util.List;

import static com.codeborne.selenide.Condition.*;
import static com.codeborne.selenide.Selectors.byLinkText;

public class FriendsPage extends BasePage {
  protected static final Config CFG = Config.getInstance();

  private static final String FRIENDS_TAB_LABEL = "Friends";
  private static final String NO_FRIENDS_TEXT = "There are no users yet";

  private final SelenideElement friendsTab;
  private final SelenideElement friendsTable;
  private final ElementsCollection friendsTableItems;
  private final ElementsCollection incomingRequestsTableItems;
  private final SelenideElement searchField;
  private final SelenideElement clearSearchButton;
  private final SelenideElement friendsTabLabel;

  public FriendsPage(SelenideDriver driver) {
    super(driver);
    this.friendsTab = driver.$("#simple-tabpanel-friends");
    this.friendsTable = driver.$("#friends");
    this.friendsTableItems = driver.$$("#friends tr");
    this.incomingRequestsTableItems = driver.$$("#requests");
    this.searchField = driver.$("input[aria-label='search']");
    this.clearSearchButton = driver.$("#input-clear");
    this.friendsTabLabel = driver.$(byLinkText(FRIENDS_TAB_LABEL));
  }

  public FriendsPage searchFriend(String friend) {
    if (clearSearchButton.isDisplayed()) {
      clearSearchButton.click();
    }
    searchField.setValue(friend).pressEnter();
    return this;
  }

  public FriendsPage verifyPageIsOpened() {
    friendsTabLabel.shouldHave(attribute("aria-selected", "true"));
    return this;
  }

  public FriendsPage verifyFriendIsPresent(String friendName) {
    searchFriend(friendName);
    friendsTableItems
        .find(text(friendName))
        .shouldBe(visible);
    return this;
  }

  public FriendsPage verifyNoFriendsPresent() {
    friendsTab.shouldHave(text(NO_FRIENDS_TEXT));
    friendsTable.shouldNotBe(visible);
    return this;
  }

  public FriendsPage verifyIncomingRequestFrom(String incomeRequest) {
    searchFriend(incomeRequest);
    incomingRequestsTableItems
        .find(text(incomeRequest))
        .shouldBe(visible);
    return this;
  }

  public FriendsPage verifyFriendsPresent(List<UserJson> friends) {
    for (UserJson friend : friends) {
      verifyFriendIsPresent(friend.username());
    }
    return this;
  }

  public FriendsPage verifyIncomingRequests(List<UserJson> incomeInvite) {
    for (UserJson income : incomeInvite) {
      verifyIncomingRequestFrom(income.username());
    }
    return this;
  }
}