package guru.qa.niffler.service;

import guru.qa.niffler.common.values.AuthorityType;
import guru.qa.niffler.config.Config;
import guru.qa.niffler.data.entity.auth.AuthAuthorityEntity;
import guru.qa.niffler.data.entity.auth.AuthUserEntity;
import guru.qa.niffler.data.entity.user.UserDataEntity;
import guru.qa.niffler.data.repository.AuthUserRepository;
import guru.qa.niffler.data.repository.UserDataRepository;
import guru.qa.niffler.data.repository.impl.auth.AuthUserRepositorySpringJdbc;
import guru.qa.niffler.data.repository.impl.userdata.UserDataRepositorySpringJdbc;
import guru.qa.niffler.data.tpl.XaTransactionTemplate;
import guru.qa.niffler.model.UserJson;

import java.util.Arrays;
import java.util.Optional;

public class UserDbClient {
  private static final Config CFG = Config.getInstance();

  AuthUserRepository userAuth = new AuthUserRepositorySpringJdbc();
  UserDataRepository userData = new UserDataRepositorySpringJdbc();

  private final XaTransactionTemplate xaTransactionTemplate = new XaTransactionTemplate(
      CFG.authJdbcUrl(), CFG.userdataJdbcUrl()
  );

  public UserJson createUser(UserJson user) {
    return xaTransactionTemplate.execute(() -> {
      UserDataEntity userEntity = userData.create(UserDataEntity.fromJson(user));

      AuthUserEntity authUserEntity = AuthUserEntity.fromJson(user);
      authUserEntity.setAuthorities(Arrays.stream(AuthorityType.values())
          .map(authority -> {
            AuthAuthorityEntity authAuthority = new AuthAuthorityEntity();
            authAuthority.setUser(authUserEntity);
            authAuthority.setAuthority(authority);
            return authAuthority;
          })
          .toList());

      userAuth.create(authUserEntity);

      return UserJson.fromEntity(userEntity);
    });
  }

  public void createFriendship(UserJson friend1, UserJson friend2) {
    xaTransactionTemplate.execute(() -> {
      UserDataEntity friendEntity1 = findOrCreate(friend1);
      UserDataEntity friendEntity2 = findOrCreate(friend2);

      userData.addFriendship(friendEntity1, friendEntity2);

      return null;
    });
  }

  private UserDataEntity findOrCreate(UserJson friend) {
    UserDataEntity newEntity = UserDataEntity.fromJson(friend);
    return Optional.ofNullable(friend.id())
        .flatMap(userData::findById)
        .orElseGet(() -> userData.create(newEntity));
  }
}