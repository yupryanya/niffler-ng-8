package guru.qa.niffler.test.web;

import guru.qa.niffler.common.values.CurrencyValues;
import guru.qa.niffler.jupiter.annotation.Spend;
import guru.qa.niffler.jupiter.annotation.User;
import guru.qa.niffler.model.SpendJson;
import guru.qa.niffler.model.UserJson;
import org.junit.jupiter.api.Test;

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
  void spendingDescriptionShouldBeUpdatedByTableAction(UserJson user) {
    final String newDescription = "Обучение Niffler NG2";

    login(user);
    mainPage
        .getSpendTable()
        .editSpend(user.testData().spends().getFirst().description());
    editSpendingPage
        .editDescription(newDescription);

    SpendJson originalSpend = user.testData().spends().getFirst();
    SpendJson updatedSpend = new SpendJson(
        originalSpend.id(),
        originalSpend.spendDate(),
        originalSpend.category(),
        originalSpend.currency(),
        originalSpend.amount(),
        newDescription,
        originalSpend.username()
    );
    mainPage
        .getSpendTable()
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
  void allSpendsShouldBeDisplayed(UserJson user) {
    login(user);
    mainPage
        .getSpendTable()
        .verifySpendTableMatches(user.testData().spends());
  }
}