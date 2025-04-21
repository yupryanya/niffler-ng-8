package guru.qa.niffler.service;

import guru.qa.niffler.common.values.AuthorityType;
import guru.qa.niffler.config.Config;
import guru.qa.niffler.data.dao.auth.AuthAuthorityDao;
import guru.qa.niffler.data.dao.auth.UserAuthDao;
import guru.qa.niffler.data.dao.impl.AuthAuthorityDaoJdbc;
import guru.qa.niffler.data.dao.impl.UserAuthDaoJdbc;
import guru.qa.niffler.data.dao.impl.UserDataDaoJdbc;
import guru.qa.niffler.data.dao.user.UserDataDao;
import guru.qa.niffler.data.entity.auth.AuthAuthorityEntity;
import guru.qa.niffler.data.entity.auth.AuthUserEntity;
import guru.qa.niffler.data.entity.user.UserDataEntity;
import guru.qa.niffler.data.tpl.DataSources;
import guru.qa.niffler.data.tpl.XaTransactionTemplate;
import guru.qa.niffler.model.UserJson;
import org.springframework.data.transaction.ChainedTransactionManager;
import org.springframework.jdbc.support.JdbcTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.Arrays;
import java.util.UUID;

public class UserDbClient {
  private static final Config CFG = Config.getInstance();

//  AuthAuthorityDao authAuthority = new AuthAuthorityDaoSpringJdbc();
//  UserAuthDao userAuth = new UserAuthDaoSpringJdbc();
//  UserDataDao userData = new UserDataDaoSpringJdbc();

  AuthAuthorityDao authAuthority = new AuthAuthorityDaoJdbc();
  UserAuthDao userAuth = new UserAuthDaoJdbc();
  UserDataDao userData = new UserDataDaoJdbc();

  private final TransactionTemplate txTemplate = new TransactionTemplate(
      new JdbcTransactionManager(
          DataSources.dataSource(CFG.authJdbcUrl())
      )
  );

  @SuppressWarnings("deprecation")
  TransactionTemplate txChainedTxManagerTemplate = new TransactionTemplate(
      new ChainedTransactionManager(
          new JdbcTransactionManager(
              DataSources.dataSource(CFG.authJdbcUrl())
          ),
          new JdbcTransactionManager(
              DataSources.dataSource(CFG.userdataJdbcUrl())
          )
      )
  );

  private final XaTransactionTemplate xaTransactionTemplate = new XaTransactionTemplate(
      CFG.authJdbcUrl(), CFG.userdataJdbcUrl()
  );

  public void createUser(UserJson user) {
    xaTransactionTemplate.execute(() -> {
      userData.createUser(UserDataEntity.fromJson(user));

      UUID userId = userAuth.createUserAuth(AuthUserEntity.fromJson(user)).getId();

      var authorities = Arrays.stream(AuthorityType.values())
          .map(authority -> {
            AuthAuthorityEntity authAuthority = new AuthAuthorityEntity();
            authAuthority.setUserId(userId);
            authAuthority.setAuthority(authority);
            return authAuthority;
          })
          .toArray(AuthAuthorityEntity[]::new);
      authAuthority.createAuthAuthorities(authorities);
      return null;
    });
  }

  public void createUserWithChainedTxManager(UserJson user) {
    txChainedTxManagerTemplate.execute(status -> {
      userData.createUser(UserDataEntity.fromJson(user));

      UUID userId = userAuth.createUserAuth(AuthUserEntity.fromJson(user)).getId();

      if (true) {
        throw new RuntimeException("Simulated error");
      }

      var authorities = Arrays.stream(AuthorityType.values())
          .map(authority -> {
            AuthAuthorityEntity authAuthority = new AuthAuthorityEntity();
            authAuthority.setUserId(userId);
            authAuthority.setAuthority(authority);
            return authAuthority;
          })
          .toArray(AuthAuthorityEntity[]::new);
      authAuthority.createAuthAuthorities(authorities);
      return null;
    });
  }
}