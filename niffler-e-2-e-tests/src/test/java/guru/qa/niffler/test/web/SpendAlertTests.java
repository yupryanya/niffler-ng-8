package guru.qa.niffler.test.web;

import guru.qa.niffler.jupiter.annotation.ApiLogin;
import guru.qa.niffler.jupiter.annotation.Spend;
import guru.qa.niffler.jupiter.annotation.User;
import guru.qa.niffler.model.SpendJson;
import guru.qa.niffler.model.UserJson;
import org.junit.jupiter.api.Test;

import static guru.qa.niffler.common.messages.ApplicationWarnings.SpendPageAlertMessages.*;

public class SpendAlertTests extends BaseTestWeb {
  @User
  @ApiLogin
  @Test
  void shouldDisplayAlertWhenSpendingIsAdded(UserJson user) {
    SpendJson spend = SpendJson.randomSpendWithUsername(user.username());
    mainPage
        .getHeader()
        .navigateToAddSpendingPage()
        .fillAllFields(spend)
        .clickConfirmButton()
        .checkAlertMessage(SPEND_CREATED);
  }

  @User(
      spends = {
          @Spend(category = "Grocery", description = "Bread", amount = 100.56)
      }
  )
  @ApiLogin
  @Test
  void shouldDisplayAlertWhenSpendingDeleted(UserJson user) {
    mainPage
        .getSpendsTable()
        .deleteSpend(user.testData().spends().getFirst().description())
        .checkAlertMessage(SPEND_DELETED);
  }

  @User(
      spends = {
          @Spend(category = "Grocery", description = "Bread", amount = 100.56)
      }
  )
  @ApiLogin
  @Test
  void shouldDisplayAlertWhenSpendingUpdated(UserJson user) {
    mainPage
        .getSpendsTable()
        .editSpend(user.testData().spends().getFirst().description())
        .editAmount("2000")
        .checkAlertMessage(SPEND_UPDATED);
  }
}