package guru.qa.niffler.test.web;

import guru.qa.niffler.config.Config;
import guru.qa.niffler.data.dao.impl.*;
import guru.qa.niffler.model.UserJson;
import guru.qa.niffler.service.UserDbClient;
import org.junit.jupiter.api.Test;

import static guru.qa.niffler.data.Databases.dataSource;

public class TestDbTransaction {
  protected static final Config CFG = Config.getInstance();

  @Test
  void testDbFunction() {
    UserJson user = new UserJson(
        "testuser9",
        "testpassword9"
    );
    new UserDbClient().createUser(user);
  }

  @Test
  void testFindAll() {
    System.out.println(new AuthAuthorityDaoSpringJdbc(dataSource(CFG.authJdbcUrl())).findAll());
    System.out.println(new UserAuthDaoSpringJdbc(dataSource(CFG.authJdbcUrl())).findAll());
    System.out.println(new UserDataDaoSpringJdbc(dataSource(CFG.userdataJdbcUrl())).findAll());
    System.out.println(new CategoryDaoSpringJdbc(dataSource(CFG.spendJdbcUrl())).findAll());
    System.out.println(new SpendDaoSpringJdbc(dataSource(CFG.spendJdbcUrl())).findAll());
  }
}