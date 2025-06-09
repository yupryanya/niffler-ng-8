package guru.qa.niffler.service.api;

import guru.qa.niffler.api.UserApi;
import guru.qa.niffler.api.core.RestClient;
import guru.qa.niffler.model.UserJson;
import guru.qa.niffler.service.UserClient;
import guru.qa.niffler.service.db.UserDbClient;
import io.qameta.allure.Step;
import retrofit2.Call;
import retrofit2.Response;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import java.io.IOException;
import java.util.Objects;
import java.util.Optional;

import static guru.qa.niffler.model.UserJson.generateUserJson;
import static org.apache.hc.core5.http.HttpStatus.SC_OK;
import static org.junit.jupiter.api.Assertions.assertEquals;

@ParametersAreNonnullByDefault
public class UserApiClient extends RestClient implements UserClient {

  private final UserApi userDataApi;

  public UserApiClient() {
    super(CFG.userdataUrl());
    this.userDataApi = retrofit.create(UserApi.class);
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

  //TODO: workaround - to run tests with API client
  // implement with API after 7.2 lesson
  @Override
  @Step("Create user with API")
  public UserJson createUser(UserJson user) {
    UserDbClient userDbClient = new UserDbClient();
    return userDbClient.createUser(user);
  }

  @Override
  @Step("Create user with API")
  public UserJson createUser(String username, String password) {
    return createUser(generateUserJson(username, password));
  }

  @Override
  @Step("Create friends with API")
  public void createFriends(UserJson user, int count) {
    if (count < 1) {
      throw new IllegalArgumentException("Count must be greater than 0");
    }
    for (int i = 0; i < count; i++) {
      UserJson randomUser = createUser(UserJson.generateRandomUserJson());
      UserJson response = execute(userDataApi
          .sendInvitation(user.username(), randomUser.username()), SC_OK);
      execute(userDataApi
          .acceptInvitation(randomUser.username(), user.username()), SC_OK);

      user.testData().friends().add(response);
    }
  }

  @Override
  @Step("Create outcome invitations with API")
  public void createOutcomeInvitations(UserJson user, int count) {
    if (count < 1) {
      throw new IllegalArgumentException("Count must be greater than 0");
    }
    for (int i = 0; i < count; i++) {
      UserJson addressee = createUser(UserJson.generateRandomUserJson());
      UserJson response = execute(userDataApi
          .sendInvitation(user.username(), addressee.username()), SC_OK);

      user.testData().outcomeInvitations().add(response);
    }
  }

  @Override
  @Step("Create income invitations with API")
  public void createIncomeInvitations(UserJson user, int count) {
    if (count < 1) {
      throw new IllegalArgumentException("Count must be greater than 0");
    }
    for (int i = 0; i < count; i++) {
      UserJson requester = createUser(UserJson.generateRandomUserJson());
      UserJson response = execute(userDataApi
          .sendInvitation(requester.username(), user.username()), SC_OK);

      user.testData().incomeInvitations().add(requester);
    }
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
}
