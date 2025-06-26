package guru.qa.niffler.data.repository;

import guru.qa.niffler.data.entity.user.UserDataEntity;
import guru.qa.niffler.data.repository.impl.userdata.UserDataRepositoryHibernate;
import guru.qa.niffler.data.repository.impl.userdata.UserDataRepositoryJdbc;
import guru.qa.niffler.data.repository.impl.userdata.UserDataRepositorySpringJdbc;

import java.util.Optional;
import java.util.UUID;

public interface UserDataRepository {
  static UserDataRepository getInstance() {
    return switch (System.getProperty("repository.impl", "jpa")) {
      case "jpa" -> new UserDataRepositoryHibernate();
      case "jdbc" -> new UserDataRepositoryJdbc();
      case "sjdbc" -> new UserDataRepositorySpringJdbc();
      default -> throw new IllegalStateException("Unexpected value: " + System.getProperty("repository.impl"));
    };
  }

  UserDataEntity create(UserDataEntity user);

  Optional<UserDataEntity> findById(UUID id);

  UserDataEntity update(UserDataEntity user);

  Optional<UserDataEntity> findByUsername(String username);

  void addInvitation(UserDataEntity requester, UserDataEntity addressee);

  void addFriendship(UserDataEntity friend1, UserDataEntity friend2);

  void remove(UserDataEntity user);
}
