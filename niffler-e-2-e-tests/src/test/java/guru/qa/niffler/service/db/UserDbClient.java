package guru.qa.niffler.service.db;

import guru.qa.niffler.common.values.AuthorityType;
import guru.qa.niffler.config.Config;
import guru.qa.niffler.data.entity.auth.AuthAuthorityEntity;
import guru.qa.niffler.data.entity.auth.AuthUserEntity;
import guru.qa.niffler.data.entity.user.UserDataEntity;
import guru.qa.niffler.data.repository.AuthUserRepository;
import guru.qa.niffler.data.repository.UserDataRepository;
import guru.qa.niffler.data.repository.impl.auth.AuthUserRepositoryHibernate;
import guru.qa.niffler.data.repository.impl.userdata.UserDataRepositoryHibernate;
import guru.qa.niffler.data.tpl.XaTransactionTemplate;
import guru.qa.niffler.model.UserJson;
import guru.qa.niffler.service.UserClient;

import java.util.Arrays;
import java.util.Optional;
import java.util.UUID;

import static guru.qa.niffler.data.entity.user.UserDataEntity.fromEntity;
import static guru.qa.niffler.model.UserJson.generateRandomUserJson;
import static guru.qa.niffler.model.UserJson.generateUserJson;

public class UserDbClient implements UserClient {
  private static final Config CFG = Config.getInstance();

//  AuthUserRepository userAuth = new AuthUserRepositoryJdbc();
//  UserDataRepository userData = new UserDataRepositoryJdbc();

  AuthUserRepository userAuth = new AuthUserRepositoryHibernate();
  UserDataRepository userData = new UserDataRepositoryHibernate();

  private final XaTransactionTemplate xaTransactionTemplate = new XaTransactionTemplate(
      CFG.authJdbcUrl(), CFG.userdataJdbcUrl()
  );

  @Override
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
      UserJson userJson = fromEntity(userEntity);
      return userJson;
    });
  }

  @Override
  public void createFriends(UserJson user, int count) {
    if (count < 1) {
      throw new IllegalArgumentException("Count must be greater than 0");
    }
    xaTransactionTemplate.execute(() -> {
      UserDataEntity userEntity = findUserById(user.id());
      for (int i = 0; i < count; i++) {
        UserDataEntity friendEntity = UserDataEntity.fromJson(createUser(generateRandomUserJson()));
        userData.addFriendship(userEntity, friendEntity);
        user.testData().friends().add(fromEntity(friendEntity));
      }
      return null;
    });
  }

  @Override
  public void createOutcomeInvitations(UserJson user, int count) {
    if (count < 1) {
      throw new IllegalArgumentException("Count must be greater than 0");
    }
    xaTransactionTemplate.execute(() -> {
      UserDataEntity userEntity = findUserById(user.id());
      for (int i = 0; i < count; i++) {
        UserDataEntity addresseeEntity = UserDataEntity.fromJson(createUser(generateRandomUserJson()));
        userData.addInvitation(userEntity, addresseeEntity);
        user.testData().outcomeInvitations().add(fromEntity(addresseeEntity));
      }
      return user;
    });
  }

  @Override
  public void createIncomeInvitations(UserJson user, int count) {
    if (count < 1) {
      throw new IllegalArgumentException("Count must be greater than 0");
    }
    xaTransactionTemplate.execute(() -> {
      UserDataEntity userEntity = findUserById(user.id());
      for (int i = 0; i < count; i++) {
        UserDataEntity requesterEntity = UserDataEntity.fromJson(createUser(generateRandomUserJson()));
        userData.addInvitation(requesterEntity, userEntity);
        user.testData().incomeInvitations().add(fromEntity(requesterEntity));
      }
      return null;
    });
  }

@Override
  public Optional<UserJson> findUserByUsername(String username) {
    return userData.findByUsername(username)
        .map(UserDataEntity::fromEntity);
  }

  public UserJson createUser(String username, String password) {
    return createUser(generateUserJson(username, password));
  }

  private UserDataEntity findUserById(UUID id) {
    return userData.findById(id)
        .orElseThrow(() -> new IllegalArgumentException("User not found: " + id));
  }


}