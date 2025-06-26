package guru.qa.niffler.test.rest;

import guru.qa.niffler.jupiter.annotation.ApiLogin;
import guru.qa.niffler.jupiter.annotation.Token;
import guru.qa.niffler.jupiter.annotation.User;
import guru.qa.niffler.model.UserJson;
import guru.qa.niffler.model.pageable.RestResponsePage;
import guru.qa.niffler.service.api.UserApiClient;
import org.junit.jupiter.api.Test;

import java.util.List;

import static guru.qa.niffler.common.values.FriendshipStatus.*;
import static guru.qa.niffler.utils.RandomDataUtils.newValidPassword;
import static guru.qa.niffler.utils.RandomDataUtils.nonExistentUserName;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.SoftAssertions.assertSoftly;

public class FriendshipTest extends BaseTestRest {

  @User(friends = 3, incomeInvitations = 3, outcomeInvitations = 3)
  @ApiLogin
  @Test
  void shouldReturnFriendWhenSearchingByUsername(UserJson user, @Token String userToken) {
    String existingFriendUsername = user.testData().friends().getFirst().username();
    List<UserJson> searchResult = gwUserApiClient.allFriends(userToken, existingFriendUsername);
    assertSoftly(softly -> {
      softly.assertThat(searchResult).hasSize(1);
      softly.assertThat(searchResult.getFirst().friendshipStatus()).isEqualTo(FRIEND.name());
      softly.assertThat(searchResult.getFirst().username()).isEqualTo(existingFriendUsername);
    });
  }

  @User(friends = 3, incomeInvitations = 3, outcomeInvitations = 3)
  @ApiLogin
  @Test
  void shouldReturnReceivedInvitationWhenSearchingByUsername(UserJson user, @Token String userToken) {
    String senderUsername = user.testData().incomeInvitations().getFirst().username();
    List<UserJson> searchResult = gwUserApiClient.allFriends(userToken, senderUsername);
    assertSoftly(softly -> {
      softly.assertThat(searchResult).hasSize(1);
      softly.assertThat(searchResult.getFirst().friendshipStatus()).isEqualTo(INVITE_RECEIVED.name());
      softly.assertThat(searchResult.getFirst().username()).isEqualTo(senderUsername);
    });
  }

  @User(friends = 3, incomeInvitations = 3, outcomeInvitations = 3)
  @ApiLogin
  @Test
  void shouldReturnSentInvitationWhenSearchingByUsername(UserJson user, @Token String userToken) {
    String invitedUsername = user.testData().outcomeInvitations().getFirst().username();
    List<UserJson> searchResult = gwUserApiClient.allUsers(userToken, invitedUsername);
    assertSoftly(softly -> {
      softly.assertThat(searchResult).hasSize(1);
      softly.assertThat(searchResult.getFirst().friendshipStatus()).isEqualTo(INVITE_SENT.name());
      softly.assertThat(searchResult.getFirst().username()).isEqualTo(invitedUsername);
    });
  }

  @User(friends = 3, incomeInvitations = 3)
  @ApiLogin
  @Test
  void shouldReturnEmptyListWhenSearchQueryDoesNotMatchAnyFriend(UserJson user, @Token String userToken) {
    List<UserJson> searchResult = gwUserApiClient.allFriends(userToken, nonExistentUserName());
    assertThat(searchResult).isEmpty();
  }

  @User(outcomeInvitations = 3)
  @ApiLogin
  @Test
  void shouldReturnEmptyListWhenSearchQueryDoesNotMatchAnyInvitationSent(UserJson user, @Token String userToken) {
    List<UserJson> searchResult = gwUserApiClient.getOutcomeInvitations(userToken, nonExistentUserName());
    assertThat(searchResult).isEmpty();
  }

  @User(friends = 1)
  @ApiLogin
  @Test
  void shouldRemoveConfirmedFriendSuccessfully(UserJson user, @Token String userToken) {
    String friendUsername = user.testData().friends().getFirst().username();
    List<UserJson> friendsBeforeRemoval = gwUserApiClient.getConfirmedFriends(userToken, null);
    assertThat(friendsBeforeRemoval).hasSize(1);

    gwUserApiClient.removeFriend(userToken, friendUsername);

    List<UserJson> friendsAfterRemoval = gwUserApiClient.allFriends(userToken, null);
    UserJson friendAfterRemoval = gwUserApiClient.allUsers(userToken, friendUsername).getFirst();

    assertSoftly(softly -> {
      softly.assertThat(friendsAfterRemoval).isEmpty();
      softly.assertThat(friendAfterRemoval.friendshipStatus()).isNull();
    });
  }

  @User(incomeInvitations = 1)
  @ApiLogin
  @Test
  void shouldUpdateFriendshipStatusAfterConfirmingFriendship(UserJson recipient, @Token String recipientToken) {
    List<UserJson> friendsBeforeAcceptance = gwUserApiClient.getConfirmedFriends(recipientToken, null);
    assertThat(friendsBeforeAcceptance).isEmpty();

    UserJson sender = recipient.testData().incomeInvitations().getFirst();
    gwUserApiClient.acceptInvitation(recipientToken, sender);

    List<UserJson> friendsAfterAcceptance = gwUserApiClient.getConfirmedFriends(recipientToken, null);
    assertSoftly(softly -> {
      softly.assertThat(friendsAfterAcceptance).hasSize(1);
      softly.assertThat(friendsAfterAcceptance.getFirst().username()).isEqualTo(sender.username());
      softly.assertThat(friendsAfterAcceptance.getFirst().friendshipStatus()).isEqualTo(FRIEND.name());
    });
  }

  @User
  @ApiLogin
  @Test
  void shouldUpdateFriendshipStatusAfterSendingInvitation(@Token String senderToken) {
    List<UserJson> outcomeInvitations = gwUserApiClient.getOutcomeInvitations(senderToken, null);
    assertThat(outcomeInvitations).isEmpty();

    String recipientUsername = nonExistentUserName();
    UserJson recipient = new UserApiClient().createUser(recipientUsername, newValidPassword());
    gwUserApiClient.sendInvitation(senderToken, recipient);

    List<UserJson> outcomeInvitationsAfterSending = gwUserApiClient.getOutcomeInvitations(senderToken, null);
    assertSoftly(softly -> {
      softly.assertThat(outcomeInvitationsAfterSending).hasSize(1);
      softly.assertThat(outcomeInvitationsAfterSending.getFirst().username()).isEqualTo(recipient.username());
    });
  }

  @User(incomeInvitations = 1)
  @ApiLogin
  @Test
  void shouldUpdateFriendshipStatusIfInvitationDeclined(@Token String token, UserJson user) {
    List<UserJson> incomeInvitations = gwUserApiClient.getIncomeInvitations(token, null);

    assertThat(incomeInvitations.size()).isEqualTo(1);

    UserJson sender = user.testData().incomeInvitations().getFirst();
    gwUserApiClient.declineInvitation(token, sender);

    List<UserJson> incomeInvitationsAfterDecline = gwUserApiClient.getIncomeInvitations(token, null);
    UserJson senderAfterDecline = gwUserApiClient.allUsers(token, sender.username()).getFirst();

    assertSoftly(softly -> {
      softly.assertThat(incomeInvitationsAfterDecline).isEmpty();
      softly.assertThat(senderAfterDecline.friendshipStatus()).isNull();
    });
  }

  @User(friends = 12)
  @ApiLogin
  @Test
  void shouldSupportPaginationForFriendsList(UserJson user, @Token String userToken) {
    final int page = 0;
    final int size = 5;

    RestResponsePage<UserJson> pagedResponse = gwV2UserApiClient.allFriends(userToken, page, size, "username", null);

    assertSoftly(softly -> {
      softly.assertThat(pagedResponse.getContent()).hasSize(size);
      softly.assertThat(pagedResponse.getPageable().getPageNumber()).isEqualTo(page);
      softly.assertThat(pagedResponse.getPageable().getPageSize()).isEqualTo(size);
      softly.assertThat(pagedResponse.getTotalElements()).isEqualTo(user.testData().friends().size());
      softly.assertThat(pagedResponse.getTotalPages()).isEqualTo(3);
    });
  }
}
