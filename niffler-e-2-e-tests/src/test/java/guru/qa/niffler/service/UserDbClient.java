package guru.qa.niffler.service;

import guru.qa.niffler.common.values.AuthorityType;
import guru.qa.niffler.config.Config;
import guru.qa.niffler.data.Databases;
import guru.qa.niffler.data.dao.impl.AuthAuthorityDaoJdbc;
import guru.qa.niffler.data.dao.impl.UserAuthDaoJdbc;
import guru.qa.niffler.data.dao.impl.UserDataDaoJdbc;
import guru.qa.niffler.data.entity.auth.AuthAuthorityEntity;
import guru.qa.niffler.data.entity.auth.UserAuthEntity;
import guru.qa.niffler.data.entity.user.UserDataEntity;
import guru.qa.niffler.model.UserJson;

import java.sql.Connection;
import java.util.UUID;

import static guru.qa.niffler.data.Databases.xaTransaction;

public class UserDbClient {
  private static final Config CFG = Config.getInstance();

  public void createUser(UserJson user) {
    xaTransaction(
        Connection.TRANSACTION_READ_COMMITTED,

        new Databases.XaConsumer(
            connection -> {
              new UserDataDaoJdbc(connection)
                  .createUser(UserDataEntity.fromJson(user));
            },
            CFG.userdataJdbcUrl()
        ),

        new Databases.XaConsumer(
            connection -> {
              UserAuthDaoJdbc authDao = new UserAuthDaoJdbc(connection);
              AuthAuthorityDaoJdbc authorityDao = new AuthAuthorityDaoJdbc(connection);

              UUID userId = authDao.createUserAuth(UserAuthEntity.fromJson(user)).getId();

              for (AuthorityType authority : AuthorityType.values()) {
                AuthAuthorityEntity authAuthority = new AuthAuthorityEntity();
                authAuthority.setUserId(userId);
                authAuthority.setAuthority(authority.getValue());
                authorityDao.createAuthAuthority(authAuthority);
              }
            },
            CFG.authJdbcUrl()
        )
    );
  }
}