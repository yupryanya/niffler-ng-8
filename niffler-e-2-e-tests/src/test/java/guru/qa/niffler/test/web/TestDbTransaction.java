package guru.qa.niffler.test.web;

import guru.qa.niffler.config.Config;
import guru.qa.niffler.model.UserJson;
import guru.qa.niffler.service.UserDbClient;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static guru.qa.niffler.utils.RandomDataUtils.newValidPassword;
import static guru.qa.niffler.utils.RandomDataUtils.nonExistentUserName;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class TestDbTransaction {
  protected static final Config CFG = Config.getInstance();

  @Test
  void createUser() {
    UserJson user = new UserJson(
        null,
        nonExistentUserName(),
        newValidPassword()
    );
    var createdUser = new UserDbClient().createUser(user);

    assertNotNull(createdUser.id());
  }

  @Test
  void createFriends() {
    UserJson friend1 = new UserJson(
        UUID.fromString("808d236e-1c7f-11f0-8ed2-267cff83d5df"),
        "testUser1",
        null
    );

    UserJson friend2 = new UserJson(
        null,
        nonExistentUserName(),
        newValidPassword()
    );

    new UserDbClient().createFriendship(friend1, friend2);

  }
}