package guru.qa.niffler.service.db;

import guru.qa.niffler.common.values.AuthorityType;
import guru.qa.niffler.config.Config;
import guru.qa.niffler.data.entity.auth.AuthAuthorityEntity;
import guru.qa.niffler.data.entity.auth.AuthUserEntity;
import guru.qa.niffler.data.entity.user.UserDataEntity;
import guru.qa.niffler.data.repository.AuthUserRepository;
import guru.qa.niffler.data.repository.UserDataRepository;
import guru.qa.niffler.data.tpl.XaTransactionTemplate;
import guru.qa.niffler.model.UserJson;
import guru.qa.niffler.service.UserClient;
import io.qameta.allure.Step;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.*;

import static guru.qa.niffler.data.entity.user.UserDataEntity.fromEntity;
import static guru.qa.niffler.model.UserJson.generateUserJson;
import static guru.qa.niffler.utils.RandomDataUtils.newValidPassword;
import static guru.qa.niffler.utils.RandomDataUtils.nonExistentUserName;

@ParametersAreNonnullByDefault
public class UserDbClient implements UserClient {
  private static final Config CFG = Config.getInstance();

  AuthUserRepository userAuth = AuthUserRepository.getInstance();
  UserDataRepository userData = UserDataRepository.getInstance();

  private final XaTransactionTemplate xaTransactionTemplate = new XaTransactionTemplate(
      CFG.authJdbcUrl(), CFG.userdataJdbcUrl()
  );

  @Override
  @Step("Create friends with SQL")
  public @Nonnull List<UserJson> createFriends(UserJson user, int count) {
    if (count < 1) {
      throw new IllegalArgumentException("Count must be greater than 0");
    }
    return xaTransactionTemplate.execute(() -> {
      UserDataEntity userEntity = findUserById(user.id());
      List<UserJson> addedFriends = new ArrayList<>();
      for (int i = 0; i < count; i++) {
        final String password = newValidPassword();
        UserDataEntity friendEntity = UserDataEntity.fromJson(createUser(nonExistentUserName(), password));
        userData.addFriendship(userEntity, friendEntity);
        addedFriends.add(fromEntity(friendEntity).withPassword(password));
      }
      return addedFriends;
    });
  }

  @Override
  @Step("Create outcome invitations with SQL")
  public @Nonnull List<UserJson> createOutcomeInvitations(UserJson user, int count) {
    if (count < 1) {
      throw new IllegalArgumentException("Count must be greater than 0");
    }
    return xaTransactionTemplate.execute(() -> {
      UserDataEntity userEntity = findUserById(user.id());
      List<UserJson> addedInvitations = new ArrayList<>();
      for (int i = 0; i < count; i++) {
        UserDataEntity addresseeEntity = UserDataEntity.fromJson(createUser(nonExistentUserName(), newValidPassword()));
        userData.addInvitation(userEntity, addresseeEntity);
        addedInvitations.add(fromEntity(addresseeEntity));
      }
      return addedInvitations;
    });
  }

  @Override
  @Step("Create income invitations with SQL")
  public @Nonnull List<UserJson> createIncomeInvitations(UserJson user, int count) {
    if (count < 1) {
      throw new IllegalArgumentException("Count must be greater than 0");
    }
    return xaTransactionTemplate.execute(() -> {
      UserDataEntity userEntity = findUserById(user.id());
      List<UserJson> createdInvitations = new ArrayList<>();
      for (int i = 0; i < count; i++) {
        final String password = newValidPassword();
        UserDataEntity requesterEntity = UserDataEntity.fromJson(createUser(nonExistentUserName(), password));
        userData.addInvitation(requesterEntity, userEntity);
        createdInvitations.add(fromEntity(requesterEntity).withPassword(password));
      }
      return createdInvitations;
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
      return userJson.withEmptyTestData().withPassword(password);
    });
  }

  @Step("Find user by ID with SQL")
  private UserDataEntity findUserById(UUID id) {
    return userData.findById(id)
        .orElseThrow(() -> new IllegalArgumentException("User not found: " + id));
  }
}