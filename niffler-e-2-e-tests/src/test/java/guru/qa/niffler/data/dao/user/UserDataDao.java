package guru.qa.niffler.data.dao.user;

import guru.qa.niffler.data.entity.user.UserDataEntity;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserDataDao {
  UserDataEntity createUser(UserDataEntity user);

  Optional<UserDataEntity> findUserById(UUID id);

  Optional<UserDataEntity> findUserByUsername(String username);

  void deleteUser(UserDataEntity user);

  List<UserDataEntity> findAll();
}
