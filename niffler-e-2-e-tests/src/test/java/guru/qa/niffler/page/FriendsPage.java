package guru.qa.niffler.page;

import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.SelenideElement;
import guru.qa.niffler.config.Config;
import guru.qa.niffler.model.UserJson;
import guru.qa.niffler.page.components.AlertDialog;
import guru.qa.niffler.page.components.SearchField;
import io.qameta.allure.Step;

import java.util.List;

import static com.codeborne.selenide.Condition.*;
import static com.codeborne.selenide.Selectors.byLinkText;
import static com.codeborne.selenide.Selectors.byText;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$$;

public class FriendsPage {
  protected static final Config CFG = Config.getInstance();

  private static final String FRIENDS_TAB_LABEL = "Friends";
  private static final String NO_FRIENDS_TEXT = "There are no users yet";

  private final SelenideElement friendsTab = $("#simple-tabpanel-friends");
  private final SelenideElement friendsTable = $("#friends");
  private final ElementsCollection friendsTableItems = $$("#friends tr");
  private final ElementsCollection incomingRequestsTableItems = $$("#requests");

  private SearchField searchField = new SearchField();
  private AlertDialog alertDialog = new AlertDialog();

  private ElementsCollection getRowByUsername(String username) {
    return incomingRequestsTableItems
        .findBy(text(username))
        .$$("td");
  }

  @Step("Verify that 'Friends' label is displayed")
  public FriendsPage verifyPageIsOpened() {
    $(byLinkText(FRIENDS_TAB_LABEL))
        .shouldHave(attribute("aria-selected", "true"));
    return this;
  }

  @Step("Verify friend with name {friendName} is present in the friends list")
  public FriendsPage verifyFriendIsPresent(String friendName) {
    searchField.search(friendName);
    friendsTableItems
        .find(text(friendName))
        .shouldBe(visible);
    return this;
  }

  @Step("Verify friends table is empty")
  public FriendsPage verifyNoFriendsPresent() {
    friendsTab.shouldHave(text(NO_FRIENDS_TEXT));
    friendsTable.shouldNotBe(visible);
    return this;
  }

  @Step("Verify incoming request from {incomeRequest} is present")
  public FriendsPage verifyIncomingRequestFrom(String incomeUsername) {
    searchField.search(incomeUsername);
    incomingRequestsTableItems
        .find(text(incomeUsername))
        .shouldBe(visible);
    return this;
  }

  @Step("Verify all friends are present in the friends list")
  public FriendsPage verifyFriendsPresent(List<UserJson> friends) {
    for (UserJson friend : friends) {
      verifyFriendIsPresent(friend.username());
    }
    return this;
  }

  @Step("Verify incoming requests are present")
  public FriendsPage verifyIncomingRequests(List<UserJson> incomeInvite) {
    for (UserJson income : incomeInvite) {
      verifyIncomingRequestFrom(income.username());
    }
    return this;
  }

  @Step("Accept incoming request from {incomeUsername}")
  public FriendsPage acceptIncomingRequestFrom(String incomeUsername) {
    getRowByUsername(incomeUsername)
        .get(1)
        .$(byText("Accept"))
        .click();
    return this;
  }

  @Step("Decline incoming request from {incomeUsername}")
  public FriendsPage declineIncomingRequestFrom(String incomeUsername) {
    getRowByUsername(incomeUsername)
        .get(1)
        .$(byText("Decline"))
        .click();
    alertDialog.getDeclineButton().click();
    return this;
  }
}