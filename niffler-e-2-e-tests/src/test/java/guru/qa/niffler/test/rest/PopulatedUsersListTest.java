package guru.qa.niffler.test.rest;

import guru.qa.niffler.jupiter.annotation.User;
import guru.qa.niffler.model.UserJson;
import guru.qa.niffler.service.api.UserApiClient;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Order.DEFAULT;

@Order(DEFAULT + 1)
public class PopulatedUsersListTest extends BaseTestRest{
  @User
  @Test
  void verifyAllUsersResponseIsNotEmpty(UserJson user) {
    List<UserJson> users = userApiClient.allUsers(user.username(), "");
    assertFalse(users.isEmpty());
  }
}
