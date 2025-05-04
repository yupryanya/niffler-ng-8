package guru.qa.niffler.service.api;

import guru.qa.niffler.api.UserApi;
import guru.qa.niffler.config.Config;
import guru.qa.niffler.model.UserJson;
import guru.qa.niffler.service.UserClient;
import guru.qa.niffler.service.db.UserDbClient;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;

import java.io.IOException;
import java.util.Optional;

import static guru.qa.niffler.model.UserJson.generateUserJson;
import static org.apache.hc.core5.http.HttpStatus.SC_OK;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class UserApiClient implements UserClient {
  private static final Config CFG = Config.getInstance();

  private final OkHttpClient client = new OkHttpClient.Builder()
      .addInterceptor(new HttpLoggingInterceptor()
          .setLevel(HttpLoggingInterceptor.Level.BODY))
      .build();

  private final Retrofit retrofit = new Retrofit.Builder()
      .baseUrl(CFG.userdataUrl())
      .client(client)
      .addConverterFactory(JacksonConverterFactory.create())
      .build();
  private final Retrofit retrofitAuth = new Retrofit.Builder()
      .baseUrl(CFG.authUrl())
      .client(client)
      .addConverterFactory(JacksonConverterFactory.create())
      .build();

  private final UserApi userDataApi = retrofit.create(UserApi.class);

  private <T> T execute(Call<T> call, int expectedStatusCode) {
    try {
      Response<T> response = call.execute();
      assertEquals(expectedStatusCode, response.code(), "Unexpected HTTP status code");
      return response.body();
    } catch (IOException e) {
      throw new AssertionError("Failed to execute API request", e);
    }
  }

  //TODO: workaround - implement with API
  @Override
  public UserJson createUser(UserJson user) {
    UserDbClient userDbClient = new UserDbClient();
    return userDbClient.createUser(user);
  }

  @Override
  public UserJson createUser(String username, String password) {
    return createUser(generateUserJson(username, password));
  }

  @Override
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
  public Optional<UserJson> findUserByUsername(String username) {
    if (username == null || username.isEmpty()) {
      throw new IllegalArgumentException("Username cannot be null or empty");
    }
    UserJson userData = execute(userDataApi.getUser(username), SC_OK);
    return Optional.of(userData.withEmptyTestData());
  }
}
