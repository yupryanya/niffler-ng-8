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
import io.qameta.allure.Step;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Arrays;
import java.util.Optional;
import java.util.UUID;

import static guru.qa.niffler.data.entity.user.UserDataEntity.fromEntity;
import static guru.qa.niffler.model.UserJson.generateUserJson;
import static guru.qa.niffler.utils.RandomDataUtils.newValidPassword;
import static guru.qa.niffler.utils.RandomDataUtils.nonExistentUserName;

@ParametersAreNonnullByDefault
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
  @Step("Create friends with SQL")
  public void createFriends(UserJson user, int count) {
    if (count < 1) {
      throw new IllegalArgumentException("Count must be greater than 0");
    }
    xaTransactionTemplate.execute(() -> {
      UserDataEntity userEntity = findUserById(user.id());
      for (int i = 0; i < count; i++) {
        UserDataEntity friendEntity = UserDataEntity.fromJson(createUser(nonExistentUserName(), newValidPassword()));
        userData.addFriendship(userEntity, friendEntity);
        user.testData().friends().add(fromEntity(friendEntity));
      }
      return null;
    });
  }

  @Override
  @Step("Create outcome invitations with SQL")
  public void createOutcomeInvitations(UserJson user, int count) {
    if (count < 1) {
      throw new IllegalArgumentException("Count must be greater than 0");
    }
    xaTransactionTemplate.execute(() -> {
      UserDataEntity userEntity = findUserById(user.id());
      for (int i = 0; i < count; i++) {
        UserDataEntity addresseeEntity = UserDataEntity.fromJson(createUser(nonExistentUserName(), newValidPassword()));
        userData.addInvitation(userEntity, addresseeEntity);
        user.testData().outcomeInvitations().add(fromEntity(addresseeEntity));
      }
      return user;
    });
  }

  @Override
  @Step("Create income invitations with SQL")
  public void createIncomeInvitations(UserJson user, int count) {
    if (count < 1) {
      throw new IllegalArgumentException("Count must be greater than 0");
    }
    xaTransactionTemplate.execute(() -> {
      UserDataEntity userEntity = findUserById(user.id());
      for (int i = 0; i < count; i++) {
        UserDataEntity requesterEntity = UserDataEntity.fromJson(createUser(nonExistentUserName(), newValidPassword()));
        userData.addInvitation(requesterEntity, userEntity);
        user.testData().incomeInvitations().add(fromEntity(requesterEntity));
      }
      return null;
    });
  }

  @Override
  @Step("Find user by username with SQL")
  public Optional<UserJson> findUserByUsername(String username) {
    return userData.findByUsername(username)
        .map(UserDataEntity::fromEntity);
  }

  @Step("Create user with SQL")
  public UserJson createUser(String username, String password) {
    UserJson user = generateUserJson(username, password);
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

  @Step("Find user by ID with SQL")
  private UserDataEntity findUserById(UUID id) {
    return userData.findById(id)
        .orElseThrow(() -> new IllegalArgumentException("User not found: " + id));
  }
}