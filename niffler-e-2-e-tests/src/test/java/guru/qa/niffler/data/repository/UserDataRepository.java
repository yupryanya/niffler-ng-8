package guru.qa.niffler.data.repository;

import guru.qa.niffler.data.entity.user.UserDataEntity;

import java.util.Optional;
import java.util.UUID;

public interface UserDataRepository {
  UserDataEntity create(UserDataEntity user);

  Optional<UserDataEntity> findById(UUID id);

  void addInvitation(UserDataEntity requester, UserDataEntity addressee);

  void addFriendship(UserDataEntity friend1, UserDataEntity friend2);
}
