package guru.qa.niffler.test.rest;

import guru.qa.niffler.jupiter.annotation.User;
import guru.qa.niffler.model.UserJson;
import guru.qa.niffler.service.api.UserApiClient;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;

@Order(1)
public class EmptyUsersListTest extends BaseTestRest {
  @Disabled("Default 'admin' user is created in the database")
  @User
  @Test
  void verifyAllUsersResponseIsEmpty(UserJson user) {
    List<UserJson> users = userApiClient.allUsers(user.username(), "");
    assertTrue(users.isEmpty());
  }
}
