package guru.qa.niffler.page;

import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.Selenide;
import guru.qa.niffler.config.Config;
import guru.qa.niffler.model.UserJson;
import guru.qa.niffler.page.components.SearchField;
import io.qameta.allure.Step;

import java.util.List;

import static com.codeborne.selenide.Condition.*;
import static com.codeborne.selenide.Selectors.byLinkText;
import static com.codeborne.selenide.Selectors.byText;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$$;

public class AllPeoplePage {
  protected static final Config CFG = Config.getInstance();

  private static final String ALL_PEOPLE_TAB_LABEL = "All people";

  private final ElementsCollection allPeopleTableItems = $$("#all tr");

  private SearchField searchField = new SearchField();

  private String getUrl() {
    return CFG.frontUrl() + "people/all";
  }

  @Step("Open 'All People' page")
  public AllPeoplePage open() {
    return Selenide.open(getUrl(), AllPeoplePage.class)
        .verifyPageIsOpened();
  }

  @Step("Verify that 'All People' label is displayed")
  public AllPeoplePage verifyPageIsOpened() {
    $(byLinkText(ALL_PEOPLE_TAB_LABEL))
        .shouldHave(attribute("aria-selected", "true"));
    return this;
  }

  @Step("Verify that request to {outcome} is sent")
  public AllPeoplePage verifyOutcomeRequestTo(String outcome) {
    searchField.search(outcome);
    allPeopleTableItems
        .find(text(outcome))
        .$(byText("Waiting..."))
        .shouldBe(visible);
    return this;
  }

  @Step("Verify that all outcome requests are sent to the following users: {outcomeInvitations}")
  public AllPeoplePage verifyOutcomeRequests(List<UserJson> outcomeInvitations) {
    for (UserJson outcome : outcomeInvitations) {
      verifyOutcomeRequestTo(outcome.username());
    }
    return this;
  }
}