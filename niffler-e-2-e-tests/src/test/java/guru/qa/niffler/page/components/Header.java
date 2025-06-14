package guru.qa.niffler.page.components;

import com.codeborne.selenide.SelenideElement;
import guru.qa.niffler.page.*;
import io.qameta.allure.Step;

import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Selenide.$;

public class Header extends BaseComponent<Header> {

  private static final String HEADER_TITLE = "Niffler";

  private final String addSpendingLink = "a[href*='/spending']";
  private final String mainPageLink = "a[href*='/main']";
  private final String menuButton = "button[aria-label='Menu']";

  private final SelenideElement profileLink = $("a[href*='/profile']");
  private final SelenideElement friendsLink = $("a[href*='/people/friends']");
  private final SelenideElement allPeopleLink = $("a[href*='/people/all']");

  public Header(SelenideElement self) {
    super(self);
  }

  public Header() {
    super($("#root header"));
  }

  @Step("Navigate to 'Add spending' page")
  public AddSpendingPage navigateToAddSpendingPage() {
    self.$(addSpendingLink).click();
    return new AddSpendingPage();
  }

  @Step("Verify header title is displayed")
  public void verifyHeaderTitle() {
    self.$("h1").shouldHave(text(HEADER_TITLE));
  }

  @Step("Navigate to Friends page")
  public FriendsPage toFriendsPage() {
    self.$(menuButton).click();
    friendsLink.click();
    return new FriendsPage();
  }

  @Step("Navigate to All People page")
  public AllPeoplePage toAllPeoplesPage() {
    self.$(menuButton).click();
    allPeopleLink.click();
    return new AllPeoplePage();
  }

  @Step("Navigate to Profile page")
  public ProfilePage toProfilePage() {
    self.$(menuButton).click();
    profileLink.click();
    return new ProfilePage();
  }

  @Step("Sign out")
  public LoginPage signOut() {
    self.$(menuButton).click();
    return new LoginPage();
  }

  @Step("Navigate to Main page")
  public MainPage toMainPage() {
    self.$(mainPageLink).click();
    return new MainPage();
  }
}