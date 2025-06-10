package guru.qa.niffler.data.repository.impl.userdata;

import guru.qa.niffler.common.values.FriendshipStatus;
import guru.qa.niffler.config.Config;
import guru.qa.niffler.data.entity.user.UserDataEntity;
import guru.qa.niffler.data.jpa.EntityManagers;
import guru.qa.niffler.data.repository.UserDataRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Optional;
import java.util.UUID;

@ParametersAreNonnullByDefault
public class UserDataRepositoryHibernate implements UserDataRepository {
  private static final Config CFG = Config.getInstance();

  private final EntityManager entityManager = EntityManagers.em(CFG.userdataJdbcUrl());

  @Override
  public @Nonnull UserDataEntity create(UserDataEntity user) {
    entityManager.joinTransaction();
    entityManager.persist(user);
    return user;
  }

  @Override
  public Optional<UserDataEntity> findById(UUID id) {
    return Optional.ofNullable(
        entityManager.find(UserDataEntity.class, id)
    );
  }

  @Override
  public @Nonnull UserDataEntity update(UserDataEntity user) {
    entityManager.joinTransaction();
    entityManager.merge(user);
    return user;
  }

  @Override
  public Optional<UserDataEntity> findByUsername(String username) {
    try {
      return Optional.of(
          entityManager.createQuery("select u from UserDataEntity u where u.username =: username", UserDataEntity.class)
              .setParameter("username", username)
              .getSingleResult()
      );
    } catch (NoResultException e) {
      return Optional.empty();
    }
  }

@Override
public void addInvitation(UserDataEntity requester, UserDataEntity addressee) {
  entityManager.joinTransaction();
  requester.addFriends(FriendshipStatus.PENDING, addressee);
  entityManager.merge(requester);
  entityManager.merge(addressee);
}

  @Override
  public void addFriendship(UserDataEntity friend1, UserDataEntity friend2) {
    entityManager.joinTransaction();
    friend1.addFriends(FriendshipStatus.ACCEPTED, friend2);
    friend2.addFriends(FriendshipStatus.ACCEPTED, friend1);
    entityManager.merge(friend1);
    entityManager.merge(friend2);
  }

  @Override
  public void remove(UserDataEntity user) {
    entityManager.joinTransaction();
    entityManager.remove(entityManager.contains(user) ? user : entityManager.merge(user));
  }
}
