package guru.qa.niffler.service;

import guru.qa.jaxb.userdata.*;
import guru.qa.niffler.api.UserSoapApi;
import guru.qa.niffler.api.core.RestClient;
import guru.qa.niffler.api.core.converter.SoapConverterFactory;
import guru.qa.niffler.config.Config;
import io.qameta.allure.Step;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.List;

import static okhttp3.logging.HttpLoggingInterceptor.Level;

public class UserSoapClient extends RestClient {
  private static final Config CFG = Config.getInstance();
  private final UserSoapApi userdataSoapApi;

  public UserSoapClient() {
    super(CFG.userdataUrl(), false, SoapConverterFactory.create("niffler-userdata"), Level.BODY);
    this.userdataSoapApi = retrofit.create(UserSoapApi.class);
  }

  @NotNull
  @Step("Get current user info using SOAP")
  public UserResponse currentUser(CurrentUserRequest request) throws IOException {
    return userdataSoapApi.currentUser(request).execute().body();
  }

  @NotNull
  @Step("Get all users using SOAP")
  public UsersResponse allUsers(AllUsersRequest request) throws IOException {
    return userdataSoapApi.allUsers(request).execute().body();
  }

  @NotNull
  @Step("Get all friends using SOAP")
  public UsersResponse allFriendsPage(FriendsPageRequest request) throws IOException {
    return userdataSoapApi.allFriends(request).execute().body();
  }

  @NotNull
  @Step("Get all confirmed friends using SOAP")
  public List<User> allConfirmedFriends(FriendsRequest request) throws IOException {
    return filterByStatus(userdataSoapApi.allFriends(request).execute().body(), FriendshipStatus.FRIEND);
  }

  @NotNull
  @Step("Get all incoming friendship invitations using SOAP")
  public List<User> allIncomeInvitations(FriendsRequest request) throws IOException {
    return filterByStatus(userdataSoapApi.allFriends(request).execute().body(), FriendshipStatus.INVITE_RECEIVED);
  }

  @NotNull
  @Step("Get all outgoing friendship invitations using SOAP")
  public List<User> allOutcomeInvitations(AllUsersRequest request) throws IOException {
    return filterByStatus(userdataSoapApi.allUsers(request).execute().body(), FriendshipStatus.INVITE_SENT);
  }

  @NotNull
  @Step("Remove confirmed friend using SOAP")
  public void removeFriend(RemoveFriendRequest request) throws IOException {
    userdataSoapApi.removeFriend(request).execute();
  }

  @NotNull
  @Step("Decline friendship request using SOAP")
  public UserResponse declineInvitation(DeclineInvitationRequest request) throws IOException {
    return userdataSoapApi.declineInvitation(request).execute().body();
  }

  @NotNull
  @Step("Accept friendship request using SOAP")
  public UserResponse acceptInvitation(AcceptInvitationRequest request) throws IOException {
    return userdataSoapApi.acceptInvitation(request).execute().body();
  }

  @NotNull
  @Step("Send friendship invitation using SOAP")
  public UserResponse sendInvitation(SendInvitationRequest sendInvitationRequest) {
    try {
      return userdataSoapApi.sendInvitation(sendInvitationRequest).execute().body();
    } catch (IOException e) {
      throw new RuntimeException("Failed to send invitation", e);
    }
  }

  private List<User> filterByStatus(UsersResponse response, FriendshipStatus status) {
    return response.getUser().stream()
        .filter(user -> status.equals(user.getFriendshipStatus()))
        .toList();
  }
}