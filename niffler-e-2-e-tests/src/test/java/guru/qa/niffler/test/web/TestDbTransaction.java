package guru.qa.niffler.test.web;

import guru.qa.niffler.model.UserJson;
import guru.qa.niffler.service.UserDbClient;
import org.junit.jupiter.api.Test;

public class TestDbTransaction {
  @Test
  void testDbFunction() {
    UserJson user = new UserJson(
        "testuser4",
        "testpassword4"
    );

    new UserDbClient().createUser(user);
  }
}
