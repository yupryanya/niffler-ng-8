package guru.qa.niffler.service.api;

import guru.qa.niffler.api.AuthApi;
import guru.qa.niffler.api.UserApi;
import guru.qa.niffler.api.core.RestClient;
import guru.qa.niffler.api.core.ThreadSafeCookieStore;
import guru.qa.niffler.config.Config;
import guru.qa.niffler.model.UserJson;
import guru.qa.niffler.service.UserClient;
import io.qameta.allure.Step;
import org.apache.commons.lang3.time.StopWatch;
import retrofit2.Call;
import retrofit2.Response;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static com.codeborne.selenide.Selenide.sleep;
import static guru.qa.niffler.utils.RandomDataUtils.newValidPassword;
import static guru.qa.niffler.utils.RandomDataUtils.nonExistentUserName;
import static org.apache.hc.core5.http.HttpStatus.SC_OK;
import static org.junit.jupiter.api.Assertions.assertEquals;

@ParametersAreNonnullByDefault
public class UserApiClient implements UserClient {
  protected static final Config CFG = Config.getInstance();

  private final UserApi userDataApi;
  private final AuthApi authApi;

  public UserApiClient() {
    userDataApi = RestClient.RestClientFactory.retrofit(CFG.userdataUrl()).create(UserApi.class);
    authApi = RestClient.RestClientFactory.retrofit(CFG.authUrl()).create(AuthApi.class);
  }

  private @Nonnull <T> T execute(Call<T> call, int expectedStatusCode) {
    try {
      Response<T> response = call.execute();
      assertEquals(expectedStatusCode, response.code(), "Unexpected HTTP status code");
      return Objects.requireNonNull(response.body());
    } catch (IOException e) {
      throw new AssertionError("Failed to execute API request", e);
    }
  }

  @Override
  @Step("Create user with API")
  public @Nonnull UserJson createUser(String username, String password) {
    try {
      authApi.requestRegisterForm().execute();
      authApi.register(
              username,
              password,
              password,
              ThreadSafeCookieStore.INSTANCE.getCookieValue("XSRF-TOKEN"))
          .execute();

      StopWatch sw = StopWatch.createStarted();
      while (sw.getTime(TimeUnit.SECONDS) < 5) {
        Response<UserJson> response = userDataApi.getUser(username).execute();
        UserJson userJson = response.body();
        if (userJson != null && userJson.id() != null) {
          return userJson.withEmptyTestData().withPassword(password);
        }
        sleep(100);
      }
      throw new AssertionError("User creation timed out");
    } catch (IOException e) {
      throw new RuntimeException("Failed to create user", e);
    }
  }

  @Override
  @Step("Create friends with API")
  public @Nonnull List<UserJson> createFriends(UserJson user, int count) {
    if (count < 1) {
      throw new IllegalArgumentException("Count must be greater than 0");
    }
    List<UserJson> addedFriends = new ArrayList<>();
    for (int i = 0; i < count; i++) {
      UserJson randomUser = createUser(nonExistentUserName(), newValidPassword());
      execute(userDataApi.sendInvitation(user.username(), randomUser.username()), SC_OK);
      execute(userDataApi.acceptInvitation(randomUser.username(), user.username()), SC_OK);
      addedFriends.add(randomUser);
    }
    user.testData().friends().addAll(addedFriends);
    return addedFriends;
  }

  @Override
  @Step("Create outcome invitations with API")
  public @Nonnull List<UserJson> createOutcomeInvitations(UserJson user, int count) {
    if (count < 1) {
      throw new IllegalArgumentException("Count must be greater than 0");
    }
    List<UserJson> addedInvitations = new ArrayList<>();
    for (int i = 0; i < count; i++) {
      UserJson addressee = createUser(nonExistentUserName(), newValidPassword());
      execute(userDataApi.sendInvitation(user.username(), addressee.username()), SC_OK);
      addedInvitations.add(addressee);
    }
    user.testData().outcomeInvitations().addAll(addedInvitations);
    return addedInvitations;
  }


  @Override
  @Step("Create income invitations with API")
  public @Nonnull List<UserJson> createIncomeInvitations(UserJson user, int count) {
    if (count < 1) {
      throw new IllegalArgumentException("Count must be greater than 0");
    }
    List<UserJson> addedInvitations = new ArrayList<>();
    for (int i = 0; i < count; i++) {
      final String requesterUsername = nonExistentUserName();
      final String requesterPassword = newValidPassword();
      UserJson requester = createUser(requesterUsername, requesterPassword);
      execute(userDataApi.sendInvitation(requesterUsername, user.username()), SC_OK);
      addedInvitations.add(requester.withEmptyTestData().withPassword(requesterPassword));
    }
    user.testData().incomeInvitations().addAll(addedInvitations);
    return addedInvitations;
  }

  @Override
  @Step("Find user by username with API")
  public Optional<UserJson> findUserByUsername(String username) {
    if (username == null || username.isEmpty()) {
      throw new IllegalArgumentException("Username cannot be null or empty");
    }
    UserJson userData = execute(userDataApi.getUser(username), SC_OK);
    return Optional.of(userData.withEmptyTestData());
  }

  @Step("Find all users with API")
  public List<UserJson> allUsers(String username, @Nullable String searchQuery) {
    if (username == null || username.isEmpty()) {
      throw new IllegalArgumentException("Username cannot be null or empty");
    }
    return execute(userDataApi.allUsers(username, searchQuery), SC_OK);
  }

  public List<UserJson> allFriends(String username, @Nullable String searchQuery) {
    if (username == null || username.isEmpty()) {
      throw new IllegalArgumentException("Username cannot be null or empty");
    }
    return execute(userDataApi.friends(username, searchQuery), SC_OK);
  }

  public List<UserJson> getFriends(String username) {
    return allFriends(username, null).stream()
        .filter(u -> "FRIEND".equals(u.friendshipStatus()))
        .collect(Collectors.toList());
  }

  public List<UserJson> getIncomeInvitations(String username) {
    return allFriends(username, null).stream()
        .filter(u -> "INVITE_RECEIVED".equals(u.friendshipStatus()))
        .collect(Collectors.toList());
  }

  public List<UserJson> getOutcomeInvitations(String username) {
    return allUsers(username, null).stream()
        .filter(u -> "INVITE_SENT".equals(u.friendshipStatus()))
        .collect(Collectors.toList());
  }

}