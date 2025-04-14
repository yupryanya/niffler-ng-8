package guru.qa.niffler.data.dao.auth;

import guru.qa.niffler.data.entity.auth.UserAuthEntity;

public interface AuthUserDao {
  UserAuthEntity createUserAuth(UserAuthEntity authUser);

  void deleteAuthUser(UserAuthEntity authUser);
}
