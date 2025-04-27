package guru.qa.niffler.data.repository;

import guru.qa.niffler.data.entity.auth.AuthUserEntity;

import java.util.Optional;

public interface AuthUserRepository {
  AuthUserEntity create(AuthUserEntity authUser);

  Optional<AuthUserEntity> findById(String id);
}
