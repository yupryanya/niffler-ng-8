package guru.qa.niffler.test.web;

import guru.qa.niffler.jupiter.annotation.User;
import guru.qa.niffler.model.UserJson;
import org.junit.jupiter.api.Test;

public class FriendsWebTest extends BaseTestWeb {
  @User(
      friends = 3
  )
  @Test
  void friendShouldBePresentInFriendsTable(UserJson user) {
    login(user);
    mainPage
        .getHeader()
        .toFriendsPage()
        .verifyFriendsPresent(user.testData().friends());
  }

  @User()
  @Test
  void friendTableShouldBeEmptyForNewUser(UserJson user) {
    login(user);
    mainPage
        .getHeader()
        .toFriendsPage()
        .verifyNoFriendsPresent();
  }

  @User(
      incomeInvitations = 2
  )
  @Test
  void incomeInviteShouldBePresentInAllPeoplesTable(UserJson user) {
    login(user);
    mainPage
        .getHeader()
        .toFriendsPage()
        .verifyIncomingRequests(user.testData().incomeInvitations());
  }

  @User(
      outcomeInvitations = 1
  )
  @Test
  void outcomeInviteShouldBePresentInAllPeoplesTable(UserJson user) {
    login(user);
    mainPage
        .getHeader()
        .toAllPeoplesPage()
        .verifyOutcomeRequests(user.testData().outcomeInvitations());
  }
}