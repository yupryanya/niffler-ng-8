package guru.qa.niffler.test.web;

import guru.qa.niffler.config.Config;
import guru.qa.niffler.model.UserJson;
import guru.qa.niffler.service.UserDbClient;
import org.junit.jupiter.api.Test;

import static guru.qa.niffler.utils.RandomDataUtils.newValidPassword;
import static guru.qa.niffler.utils.RandomDataUtils.nonExistentUserName;

public class TestDbTransaction {
  protected static final Config CFG = Config.getInstance();

  @Test
  void createUser() {
    UserJson user = new UserJson(
        nonExistentUserName(),
        newValidPassword()
    );
    new UserDbClient().createUser(user);
  }

  @Test
  public void testChainedTransactionRollback() {
    UserJson user = new UserJson(
        nonExistentUserName(),
        newValidPassword()
    );
    new UserDbClient().createUserWithChainedTxManager(user);
  }
}