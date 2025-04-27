package guru.qa.niffler.data.dao.auth;

import guru.qa.niffler.data.entity.auth.AuthUserEntity;

import java.util.List;
import java.util.Optional;

public interface AuthUserDao {
  AuthUserEntity createUserAuth(AuthUserEntity authUser);

  void deleteAuthUser(AuthUserEntity authUser);

  Optional<AuthUserEntity> findUserById(String id);

  List<AuthUserEntity> findAll();
}
