package guru.qa.niffler.test.web;

import com.codeborne.selenide.SelenideDriver;
import guru.qa.niffler.common.configuration.Browser;
import guru.qa.niffler.jupiter.annotation.User;
import guru.qa.niffler.model.UserJson;
import guru.qa.niffler.page.AllPeoplePage;
import guru.qa.niffler.page.FriendsPage;
import guru.qa.niffler.page.MainPage;
import guru.qa.niffler.utils.BrowserToDriverConverter;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.converter.ConvertWith;
import org.junit.jupiter.params.provider.EnumSource;

public class FriendsWebTest extends BaseTestWeb {
  @User(
      friends = 3
  )
  @ParameterizedTest
  @EnumSource(Browser.class)
  void friendShouldBePresentInFriendsTable(@ConvertWith(BrowserToDriverConverter.class) SelenideDriver driver, UserJson user) {
    login(user, driver);
    new MainPage(driver)
        .navigateToFriendsPage();
    new FriendsPage(driver)
        .verifyFriendsPresent(user.testData().friends());
  }

  @User()
  @ParameterizedTest
  @EnumSource(Browser.class)
  void friendTableShouldBeEmptyForNewUser(@ConvertWith(BrowserToDriverConverter.class) SelenideDriver driver, UserJson user) {
    login(user, driver);
    new MainPage(driver)
        .navigateToFriendsPage();
    new FriendsPage(driver)
        .verifyNoFriendsPresent();
  }

  @User(
      incomeInvitations = 2
  )
  @ParameterizedTest
  @EnumSource(Browser.class)
  void incomeInviteShouldBePresentInAllPeoplesTable(@ConvertWith(BrowserToDriverConverter.class) SelenideDriver driver, UserJson user) {
    login(user, driver);
    new MainPage(driver)
        .navigateToFriendsPage();
    new FriendsPage(driver)
        .verifyIncomingRequests(user.testData().incomeInvitations());
  }

  @User(
      outcomeInvitations = 1
  )
  @ParameterizedTest
  @EnumSource(Browser.class)
  void outcomeInviteShouldBePresentInAllPeoplesTable(@ConvertWith(BrowserToDriverConverter.class) SelenideDriver driver, UserJson user) {
    login(user, driver);
    new MainPage(driver)
        .navigateToAllPeoplePage();
    new AllPeoplePage(driver)
        .verifyOutcomeRequests(user.testData().outcomeInvitations());
  }
}