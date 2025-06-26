package guru.qa.niffler.data.repository;

import guru.qa.niffler.data.entity.auth.AuthUserEntity;
import guru.qa.niffler.data.repository.impl.auth.AuthUserRepositoryHibernate;
import guru.qa.niffler.data.repository.impl.auth.AuthUserRepositoryJdbc;
import guru.qa.niffler.data.repository.impl.auth.AuthUserRepositorySpringJdbc;

import java.util.Optional;

public interface AuthUserRepository {
  static AuthUserRepository getInstance() {
    return switch (System.getProperty("repository.impl", "jpa")) {
      case "jpa" -> new AuthUserRepositoryHibernate();
      case "jdbc" -> new AuthUserRepositoryJdbc();
      case "sjdbc" -> new AuthUserRepositorySpringJdbc();
      default -> throw new IllegalStateException("Unexpected value: " + System.getProperty("repository.impl"));
    };
  }

  AuthUserEntity create(AuthUserEntity authUser);

  AuthUserEntity update(AuthUserEntity authUser);

  Optional<AuthUserEntity> findById(String id);

  Optional<AuthUserEntity> findByUsername(String username);

  void remove(AuthUserEntity authUser);
}
