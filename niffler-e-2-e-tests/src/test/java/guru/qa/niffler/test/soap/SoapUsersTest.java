package guru.qa.niffler.test.soap;

import guru.qa.jaxb.userdata.*;
import guru.qa.niffler.jupiter.annotation.User;
import guru.qa.niffler.jupiter.annotation.meta.SoapTest;
import guru.qa.niffler.model.UserJson;
import guru.qa.niffler.service.UserSoapClient;
import guru.qa.niffler.service.api.UserApiClient;
import io.qameta.allure.Description;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static guru.qa.jaxb.userdata.FriendshipStatus.FRIEND;
import static guru.qa.jaxb.userdata.FriendshipStatus.VOID;
import static guru.qa.niffler.utils.RandomDataUtils.newValidPassword;
import static guru.qa.niffler.utils.RandomDataUtils.nonExistentUserName;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.SoftAssertions.assertSoftly;

@SoapTest
public class SoapUsersTest {

  private final UserSoapClient userSoapClient = new UserSoapClient();

  @Test
  @User
  @Description("Verify that the current user can be fetched via SOAP")
  void shouldReturnCurrentUserViaSoap(UserJson user) throws IOException {
    CurrentUserRequest request = new CurrentUserRequest();
    request.setUsername(user.username());
    UserResponse response = userSoapClient.currentUser(request);

    assertThat(response.getUser().getUsername()).isEqualTo(user.username());
  }

  @Test
  @User(friends = 4, incomeInvitations = 2, outcomeInvitations = 3)
  @Description("Friend list is returned as a Page when page and size parameters are provided")
  void shouldReturnFriendsPageGivenPageAndSizeParams(UserJson user) throws IOException {
    FriendsPageRequest friendsPageRequest = new FriendsPageRequest();

    PageInfo pageInfo = new PageInfo();
    pageInfo.setPage(0);
    pageInfo.setSize(7);

    friendsPageRequest.setUsername(user.username());
    friendsPageRequest.setPageInfo(pageInfo);
    friendsPageRequest.setSearchQuery(null);

    UsersResponse friendsPage = userSoapClient.allFriendsPage(friendsPageRequest);

    assertThat(friendsPage.getUser()).hasSize(6);
  }

  @Test
  @User(friends = 4)
  @Description("Friend list should be filtered by username if searchQuery is provided")
  void shouldFilterFriendsListByUsernameWhenSearchQueryProvided(UserJson user) throws IOException {
    String friendUsername = user.testData().friends().getFirst().username();

    FriendsRequest friendsRequest = new FriendsRequest();

    friendsRequest.setUsername(user.username());
    friendsRequest.setSearchQuery(friendUsername);
    var friends = userSoapClient.allConfirmedFriends(friendsRequest);

    assertSoftly(softly -> {
      assertThat(friends).hasSize(1);
      assertThat(friends.getFirst().getUsername()).isEqualTo(friendUsername);
    });
  }

  @Test
  @User(friends = 1)
  @Description("Friendship should be deleted")
  void shouldDeleteFriendship(UserJson user) throws IOException {
    String friendUsername = user.testData().friends().getFirst().username();

    FriendsRequest friendsRequest = new FriendsRequest();

    friendsRequest.setUsername(user.username());
    friendsRequest.setSearchQuery(null);
    var friendsBeforeRemoval = userSoapClient.allConfirmedFriends(friendsRequest);

    assertThat(friendsBeforeRemoval).hasSize(1);

    RemoveFriendRequest removeFriendRequest = new RemoveFriendRequest();
    removeFriendRequest.setUsername(user.username());
    removeFriendRequest.setFriendToBeRemoved(friendUsername);
    userSoapClient.removeFriend(removeFriendRequest);

    var friendsAfterRemoval = userSoapClient.allConfirmedFriends(friendsRequest);

    AllUsersRequest allUsersRequest = new AllUsersRequest();
    allUsersRequest.setUsername(user.username());
    allUsersRequest.setSearchQuery(friendUsername);
    guru.qa.jaxb.userdata.User friendAfterRemoval = userSoapClient.allUsers(allUsersRequest).getUser().getFirst();

    assertSoftly(softly -> {
      softly.assertThat(friendsAfterRemoval).isEmpty();
      softly.assertThat(friendAfterRemoval.getFriendshipStatus()).isEqualTo(VOID);
    });
  }

  @Test
  @User(incomeInvitations = 1)
  @Description("Accept a friend request")
  void shouldAcceptFriendRequest(UserJson user) throws IOException {
    String senderUsername = user.testData().incomeInvitations().getFirst().username();

    AcceptInvitationRequest acceptInvitationRequest = new AcceptInvitationRequest();
    acceptInvitationRequest.setUsername(user.username());
    acceptInvitationRequest.setFriendToBeAdded(senderUsername);

    UserResponse response = userSoapClient.acceptInvitation(acceptInvitationRequest);

    assertThat(response.getUser().getFriendshipStatus()).isEqualTo(FRIEND);
  }

  @Test
  @User(incomeInvitations = 1)
  @Description("Decline a friend request")
  void shouldDeclineFriendRequest(UserJson user) throws IOException {
    String senderUsername = user.testData().incomeInvitations().getFirst().username();

    DeclineInvitationRequest declineInvitationRequest = new DeclineInvitationRequest();
    declineInvitationRequest.setUsername(user.username());
    declineInvitationRequest.setInvitationToBeDeclined(senderUsername);

    UserResponse response = userSoapClient.declineInvitation(declineInvitationRequest);

    assertThat(response.getUser().getFriendshipStatus()).isEqualTo(VOID);
  }

  @Test
  @User
  @Description("Send a friend invitation")
  void shouldSendFriendInvitation(UserJson user) throws IOException {
    String recipientUsername = nonExistentUserName();
    UserJson recipient = new UserApiClient().createUser(recipientUsername, newValidPassword());

    AllUsersRequest allUsersRequest = new AllUsersRequest();
    allUsersRequest.setUsername(user.username());
    allUsersRequest.setSearchQuery(null);
    var outcomeInvitations = userSoapClient.allOutcomeInvitations(allUsersRequest);

    assertThat(outcomeInvitations).hasSize(0);

    SendInvitationRequest sendInvitationRequest = new SendInvitationRequest();
    sendInvitationRequest.setUsername(user.username());
    sendInvitationRequest.setFriendToBeRequested(recipientUsername);
    UserResponse invitedUser = userSoapClient.sendInvitation(sendInvitationRequest);
    var outcomeInvitationsAfterSending = userSoapClient.allOutcomeInvitations(allUsersRequest);

    assertSoftly(softly -> {
      softly.assertThat(invitedUser.getUser().getUsername()).isEqualTo(recipient.username());
      softly.assertThat(outcomeInvitationsAfterSending).hasSize(1);
      softly.assertThat(outcomeInvitationsAfterSending.getFirst().getUsername()).isEqualTo(recipient.username());
    });

  }
}

