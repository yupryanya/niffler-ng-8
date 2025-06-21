package guru.qa.niffler.test.api;

import guru.qa.niffler.jupiter.annotation.*;
import guru.qa.niffler.model.UserJson;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class OAuthTest {
  @User(
      friends = 3,
      incomeInvitations = 2,
      outcomeInvitations = 1,
      categories = {
          @Category(name = "Grocery"),
          @Category(name = "Entertainment")
      },
      spends = {
          @Spend(category = "Grocery", description = "Milk", amount = 90.10)
      }
  )
  @ApiLogin
  @Test
  void verifyTokenIsReturnedForNewUser(@Token String token, UserJson user) throws IOException {
    assertNotNull(token);
    assertNotNull(user.username());
    assertFalse(user.testData().categories().isEmpty());
    assertFalse(user.testData().spends().isEmpty());
    assertFalse(user.testData().friends().isEmpty());
    assertFalse(user.testData().incomeInvitations().isEmpty());
    assertFalse(user.testData().outcomeInvitations().isEmpty());
  }

  @ApiLogin(username = "admin", password = "testpassword")
  @Test
  void verifyTokenIsReturnedIfNoUserGenerated(@Token String token, UserJson user) throws IOException {
    assertNotNull(token);
    assertNotNull(user.username());
  }
}