package guru.qa.niffler.test.web;

import guru.qa.niffler.jupiter.annotation.User;
import guru.qa.niffler.model.UserJson;
import org.junit.jupiter.api.Test;

public class FriendsWebTest extends BaseTestWeb {
  @User(
      username = TEST_USER_NAME,
      friends = 3
  )
  @Test
  void friendShouldBePresentInFriendsTable(UserJson user) {
    login(user);
    friendsPage.open()
        .verifyFriendsPresent(user.testData().friends());
  }

  @User()
  @Test
  void friendTableShouldBeEmptyForNewUser(UserJson user) {
    login(user);
    friendsPage.open()
        .verifyNoFriendsPresent();
  }

  @User(
      username = TEST_USER_NAME,
      incomeInvitations = 2
  )
  @Test
  void incomeInviteShouldBePresentInAllPeoplesTable(UserJson user) {
    login(user);
    friendsPage.open()
        .verifyIncomingRequests(user.testData().incomeInvitations());
  }

  @User(
      outcomeInvitations = 1
  )
  @Test
  void outcomeInviteShouldBePresentInAllPeoplesTable(UserJson user) {
    login(user);
    allPeoplePage.open()
        .verifyOutcomeRequests(user.testData().outcomeInvitations());
  }
}