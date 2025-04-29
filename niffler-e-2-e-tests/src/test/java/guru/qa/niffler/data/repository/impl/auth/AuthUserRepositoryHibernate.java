package guru.qa.niffler.data.repository.impl.auth;

import guru.qa.niffler.config.Config;
import guru.qa.niffler.data.entity.auth.AuthUserEntity;
import guru.qa.niffler.data.jpa.EntityManagers;
import guru.qa.niffler.data.repository.AuthUserRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;

import java.util.Optional;

public class AuthUserRepositoryHibernate implements AuthUserRepository {
  private static final Config CFG = Config.getInstance();

  private final EntityManager entityManager = EntityManagers.em(CFG.authJdbcUrl());

  @Override
  public AuthUserEntity create(AuthUserEntity authUser) {
    entityManager.joinTransaction();
    entityManager.persist(authUser);
    return authUser;
  }

  @Override
  public AuthUserEntity update(AuthUserEntity authUser) {
    entityManager.joinTransaction();
    entityManager.merge(authUser);
    return authUser;
  }

  @Override
  public Optional<AuthUserEntity> findById(String id) {
    return Optional.ofNullable(
        entityManager.find(AuthUserEntity.class, id)
    );
  }

  @Override
  public Optional<AuthUserEntity> findByUsername(String username) {
    try {
      return Optional.of(
          entityManager.createQuery("select u from AuthUserEntity u where u.username =: username", AuthUserEntity.class)
              .setParameter("username", username)
              .getSingleResult()
      );
    } catch (NoResultException e) {
      return Optional.empty();
    }
  }

  @Override
  public void remove(AuthUserEntity authUser) {
    entityManager.joinTransaction();
    entityManager.remove(entityManager.contains(authUser) ? authUser : entityManager.merge(authUser));
  }
}