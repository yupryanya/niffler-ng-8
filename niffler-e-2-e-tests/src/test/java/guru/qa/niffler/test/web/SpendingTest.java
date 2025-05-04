package guru.qa.niffler.test.web;

import com.codeborne.selenide.Selenide;
import guru.qa.niffler.common.values.CurrencyValues;
import guru.qa.niffler.jupiter.annotation.Spend;
import guru.qa.niffler.jupiter.annotation.User;
import guru.qa.niffler.model.UserJson;
import guru.qa.niffler.page.LoginPage;
import guru.qa.niffler.page.MainPage;
import org.junit.jupiter.api.Test;

public class SpendingTest extends BaseTestWeb {
  @User(
      username = TEST_USER_NAME,
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
        .editSpending(user.testData().spends().getFirst().description())
        .editDescription(newDescription);

    new MainPage().checkThatTableContains(newDescription);
  }
}