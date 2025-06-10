package guru.qa.niffler.test.web;

import guru.qa.niffler.common.values.CurrencyValues;
import guru.qa.niffler.jupiter.annotation.Spend;
import guru.qa.niffler.jupiter.annotation.User;
import guru.qa.niffler.model.SpendJson;
import guru.qa.niffler.model.UserJson;
import org.junit.jupiter.api.Test;

import java.util.Date;

import static guru.qa.niffler.utils.RandomDataUtils.randomDate;

public class SpendingTest extends BaseTestWeb {
  @User(
      spends = {
          @Spend(
              category = "Обучение8",
              description = "Обучение Niffler 2.0",
              amount = 89000.00,
              currency = CurrencyValues.RUB
          )
      }
  )
  @Test
  void shouldUpdateSpendingDescriptionWhenEditedInTable(UserJson user) {
    final String newDescription = "Обучение Niffler NG2";
    login(user);
    mainPage.getSpendsTable()
        .editSpend(user.testData().spends().getFirst().description())
        .editDescription(newDescription);
    SpendJson originalSpend = user.testData().spends().getFirst();
    SpendJson updatedSpend = originalSpend.withDescription(newDescription);
    mainPage
        .getSpendsTable()
        .checkThatTableContains(updatedSpend);
  }

  @User(
      spends = {
          @Spend(category = "Grocery", description = "Milk", amount = 90.10),
          @Spend(category = "Grocery", description = "Bread", amount = 89.99),
          @Spend(category = "Clothes", description = "Pants", amount = 2089.99)
      }
  )
  @Test
  void shouldDisplayAllSpendsInTable(UserJson user) {
    login(user);
    mainPage
        .getSpendsTable()
        .verifySpendTableMatches(user.testData().spends());
  }

  @User()
  @Test
  void shouldSuccessfullyAddNewSpending(UserJson user) {
    SpendJson spend = SpendJson.randomSpendWithUsername(user.username());
    login(user);
    mainPage
        .getHeader()
        .navigateToAddSpendingPage()
        .fillAllFields(spend)
        .clickConfirmButton()
        .getSpendsTable()
        .checkThatTableContains(spend);
  }

  @User(
      spends = {
          @Spend(category = "Grocery", description = "Milk", amount = 90.10)
      }
  )
  @Test
  void shouldUpdateSpendingDateWhenEditedWithCalendar(UserJson user) {
    SpendJson spend = user.testData().spends().getFirst();
    Date newDate = randomDate();

    login(user);
    mainPage
        .getSpendsTable()
        .editSpend(spend.description())
        .editDateWithCalendar(newDate);
    mainPage
        .getSpendsTable()
        .checkThatTableContains(spend.withSpendDate(newDate));
  }
}
