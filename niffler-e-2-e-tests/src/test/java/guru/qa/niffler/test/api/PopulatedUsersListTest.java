package guru.qa.niffler.test.api;

import guru.qa.niffler.jupiter.annotation.User;
import guru.qa.niffler.model.UserJson;
import guru.qa.niffler.service.api.UserApiClient;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Order.DEFAULT;

@Order(DEFAULT + 1)
public class PopulatedUsersListTest {
  @User()
  @Test
  void verifyAllUsersResponseIsNotEmpty(UserJson user) {
    final UserApiClient client = new UserApiClient();
    List<UserJson> users = client.allUsers(user.username(), "");
    assertFalse(users.isEmpty());
  }
}
