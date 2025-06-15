package guru.qa.niffler.service;

import guru.qa.niffler.model.UserJson;

import java.util.List;
import java.util.Optional;

public interface UserClient {
  List<UserJson> createFriends(UserJson user, int count);

  List<UserJson> createIncomeInvitations(UserJson requester, int count);

  List<UserJson> createOutcomeInvitations(UserJson addressee, int count);

  Optional<UserJson> findUserByUsername(String username);

  UserJson createUser(String username, String password);
}
