package guru.qa.niffler.service;

import guru.qa.niffler.model.UserJson;
import guru.qa.niffler.service.api.UserApiClient;
import guru.qa.niffler.service.db.UserDbClient;

import java.util.List;
import java.util.Optional;

public interface UserClient {
  static UserClient getInstance() {
    return "api".equals(System.getProperty("client.impl"))
        ? new UserApiClient()
        : new UserDbClient();
  }

  List<UserJson> createFriends(UserJson user, int count);

  List<UserJson> createIncomeInvitations(UserJson requester, int count);

  List<UserJson> createOutcomeInvitations(UserJson addressee, int count);

  Optional<UserJson> findUserByUsername(String username);

  UserJson createUser(String username, String password);
}
