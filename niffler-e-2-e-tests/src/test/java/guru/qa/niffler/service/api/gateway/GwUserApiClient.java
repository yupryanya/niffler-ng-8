package guru.qa.niffler.service.api.gateway;

import guru.qa.niffler.api.core.RestClient;
import guru.qa.niffler.api.gateway.GwUserApi;
import guru.qa.niffler.model.UserJson;
import io.qameta.allure.Step;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.apache.hc.core5.http.HttpStatus.SC_OK;

@ParametersAreNonnullByDefault
public class GwUserApiClient extends RestClient {

  private GwUserApi gwUserApi;

  public GwUserApiClient() {
    super(CFG.gatewayUrl());
    this.gwUserApi = retrofit.create(GwUserApi.class);
  }

  @Step("Update user with external API /api/users/update")
  public Optional<UserJson> updateUser(String token, UserJson user) {
    return Optional.of(execute(gwUserApi.updateUser(token, user), SC_OK));
  }

  @Step("Find all users with external API /api/users/all")
  public @Nonnull List<UserJson> allUsers(String token, @Nullable String searchQuery) {
    return execute(gwUserApi.allUsers(token, searchQuery), SC_OK);
  }

  @Step("Get all users with external API 'GET /api/friends/all'")
  public @Nonnull List<UserJson> allFriends(String token, @Nullable String searchQuery) {
    return execute(gwUserApi.allFriends(token, searchQuery), SC_OK);
  }

  @Step("Remove friend with external API 'DELETE /api/friends/remove'")
  public void removeFriend(String token, @Nonnull String friendName) {
    executeVoid(gwUserApi.removeFriend(token, friendName), SC_OK);
  }

  @Step("Send invitation to user with external API 'POST /api/invitations/send'")
  public Optional<UserJson> sendInvitation(String token, UserJson friend) {
    return Optional.of(execute(gwUserApi.sendInvitation(token, friend), SC_OK));
  }

  @Step("Accept invitation from user with external API 'POST /api/invitations/accept'")
  public Optional<UserJson> acceptInvitation(String token, UserJson friend) {
    return Optional.of(execute(gwUserApi.acceptInvitation(token, friend), SC_OK));
  }

  @Step("Decline invitation from user with external API 'POST /api/invitations/decline'")
  public Optional<UserJson> declineInvitation(String token, UserJson sender) {
    return Optional.of(execute(gwUserApi.declineInvitation(token, sender), SC_OK));
  }

  @Step("Filter all confirmed friends for user received with 'GET /api/friends/all'")
  public @Nonnull List<UserJson> getConfirmedFriends(String username, @Nullable String searchQuery) {
    return allFriends(username, searchQuery).stream()
        .filter(u -> "FRIEND".equals(u.friendshipStatus()))
        .collect(Collectors.toList());
  }

  @Step("Filter all income invitations for user received with 'GET /api/users/all'")
  public @Nonnull List<UserJson> getIncomeInvitations(String username, @Nullable String searchQuery) {
    return allFriends(username, searchQuery).stream()
        .filter(u -> "INVITE_RECEIVED".equals(u.friendshipStatus()))
        .collect(Collectors.toList());
  }

  @Step("Filter all outcome invitations for user received with 'GET /api/users/all'")
  public @Nonnull List<UserJson> getOutcomeInvitations(String username, @Nullable String searchQuery) {
    return allUsers(username, searchQuery).stream()
        .filter(u -> "INVITE_SENT".equals(u.friendshipStatus()))
        .collect(Collectors.toList());
  }
}