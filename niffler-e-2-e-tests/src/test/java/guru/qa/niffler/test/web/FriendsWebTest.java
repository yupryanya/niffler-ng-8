package guru.qa.niffler.test.web;

import guru.qa.niffler.jupiter.annotation.ApiLogin;
import guru.qa.niffler.jupiter.annotation.User;
import guru.qa.niffler.model.UserJson;
import org.junit.jupiter.api.Test;

public class FriendsWebTest extends BaseTestWeb {
  @User(
      friends = 3
  )
  @ApiLogin
  @Test
  void friendShouldBePresentInFriendsTable(UserJson user) {
    mainPage
        .getHeader()
        .toFriendsPage()
        .verifyFriendsPresent(user.testData().friends());
  }

  @User
  @ApiLogin
  @Test
  void friendTableShouldBeEmptyForNewUser(UserJson user) {
    mainPage
        .getHeader()
        .toFriendsPage()
        .verifyNoFriendsPresent();
  }

  @User(
      incomeInvitations = 2
  )
  @ApiLogin
  @Test
  void incomeInviteShouldBePresentInAllPeoplesTable(UserJson user) {
    mainPage
        .getHeader()
        .toFriendsPage()
        .verifyIncomingRequests(user.testData().incomeInvitations());
  }

  @User(
      outcomeInvitations = 1
  )
  @ApiLogin
  @Test
  void outcomeInviteShouldBePresentInAllPeoplesTable(UserJson user) {
    mainPage
        .getHeader()
        .toAllPeoplesPage()
        .verifyOutcomeRequests(user.testData().outcomeInvitations());
  }
}