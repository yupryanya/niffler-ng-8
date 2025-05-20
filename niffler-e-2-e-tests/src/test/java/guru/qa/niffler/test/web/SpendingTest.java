package guru.qa.niffler.test.web;

import com.codeborne.selenide.Selenide;
import guru.qa.niffler.common.values.CurrencyValues;
import guru.qa.niffler.jupiter.annotation.Spend;
import guru.qa.niffler.jupiter.annotation.User;
import guru.qa.niffler.model.SpendJson;
import guru.qa.niffler.model.UserJson;
import guru.qa.niffler.page.LoginPage;
import guru.qa.niffler.page.MainPage;
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

    Selenide.open(CFG.frontUrl(), LoginPage.class)
        .doSuccessLogin(user.username(), user.testData().password())
        .editSpend(user.testData().spends().getFirst().description())
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
    new MainPage().checkThatTableContains(updatedSpend);
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
    Selenide.open(CFG.frontUrl(), LoginPage.class)
        .doSuccessLogin(user.username(), user.testData().password())
        .verifyMainPageIsOpened()
        .verifySpendTableMatches(user.testData().spends());
  }
}