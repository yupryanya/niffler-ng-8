package guru.qa.niffler.service;

import guru.qa.niffler.model.UserJson;

import java.util.Optional;

public interface UserClient {
  UserJson createUser(UserJson user);

  void createFriends(UserJson user, int count);

  void createIncomeInvitations(UserJson requester, int count);

  void createOutcomeInvitations(UserJson addressee, int count);

  Optional<UserJson> findUserByUsername(String username);
}
