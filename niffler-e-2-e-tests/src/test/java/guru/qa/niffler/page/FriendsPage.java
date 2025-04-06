package guru.qa.niffler.page;

import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.Selenide;
import com.codeborne.selenide.SelenideElement;
import guru.qa.niffler.config.Config;

import static com.codeborne.selenide.Condition.*;
import static com.codeborne.selenide.Selectors.byLinkText;
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

    private String getUrl() {
        return CFG.frontUrl() + "people/friends";
    }

    public FriendsPage open() {
        return Selenide.open(getUrl(), FriendsPage.class)
                .verifyPageIsOpened();
    }

    public FriendsPage verifyPageIsOpened() {
        $(byLinkText(FRIENDS_TAB_LABEL))
                .shouldHave(attribute("aria-selected", "true"));
        return this;
    }

    public FriendsPage verifyFriendIsPresent(String friend) {
        friendsTableItems
                .find(text(friend))
                .shouldBe(visible);
        return this;
    }

    public FriendsPage verifyNoFriendsPresent() {
        friendsTab.shouldHave(text(NO_FRIENDS_TEXT));
        friendsTable.shouldNotBe(visible);
        return this;
    }

    public FriendsPage verifyIncomingRequestFrom(String incomeFriend) {
        incomingRequestsTableItems
                .find(text(incomeFriend))
                .shouldBe(visible);
        return this;
    }
}