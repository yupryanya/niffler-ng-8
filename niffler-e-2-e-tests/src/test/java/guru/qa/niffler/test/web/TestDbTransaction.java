package guru.qa.niffler.test.web;

import guru.qa.niffler.config.Config;
import guru.qa.niffler.model.UserJson;
import guru.qa.niffler.service.db.UserDbClient;
import org.junit.jupiter.api.Test;

import static guru.qa.niffler.utils.RandomDataUtils.newValidPassword;
import static guru.qa.niffler.utils.RandomDataUtils.nonExistentUserName;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class TestDbTransaction extends BaseTestWeb {
  protected static final Config CFG = Config.getInstance();

  @Test
  void createUser() {
    var createdUser = new UserDbClient().createUser(nonExistentUserName(), newValidPassword());

    assertNotNull(createdUser.id());
  }

  @Test
  void createFriends() {
    UserDbClient client = new UserDbClient();
    UserJson user = client.findUserByUsername(TEST_USER_NAME)
        .orElseThrow(() -> new IllegalArgumentException("User not found: " + TEST_USER_NAME));

    client.createFriends(user, 1);
  }
}