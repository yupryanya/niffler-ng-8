package guru.qa.niffler.test.web;

import guru.qa.niffler.jupiter.annotation.ApiLogin;
import guru.qa.niffler.jupiter.annotation.User;
import guru.qa.niffler.model.UserJson;
import org.junit.jupiter.api.Test;

public class IncomeInvitationTest extends BaseTestWeb {
  @User(
      incomeInvitations = 1
  )
  @ApiLogin
  @Test
  void userCanAcceptIncomeFriendInvitation(UserJson user) {
    String incomeUsername = user.testData().incomeInvitations().getFirst().username();
    mainPage
        .getHeader()
        .toFriendsPage()
        .acceptIncomingRequestFrom(incomeUsername)
        .verifyFriendIsPresent(incomeUsername);
  }

  @User(
      incomeInvitations = 1
  )
  @ApiLogin
  @Test
  void userCanDeclineIncomeFriendInvitation(UserJson user) {
    String incomeUsername = user.testData().incomeInvitations().getFirst().username();
    mainPage
        .getHeader()
        .toFriendsPage()
        .declineIncomingRequestFrom(incomeUsername)
        .verifyNoFriendsPresent();
  }
}
