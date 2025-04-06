package guru.qa.niffler.test.web;

import com.codeborne.selenide.Selenide;
import guru.qa.niffler.data.values.CurrencyValues;
import guru.qa.niffler.jupiter.annotation.Spend;
import guru.qa.niffler.model.SpendJson;
import guru.qa.niffler.page.LoginPage;
import guru.qa.niffler.page.MainPage;
import org.junit.jupiter.api.Test;

public class SpendingTest extends BaseTestWeb {
    @Spend(
            username = TEST_USER_NAME,
            category = "Обучение",
            description = "Обучение Niffler 2.0",
            amount = 89000.00,
            currency = CurrencyValues.RUB
    )
    @Test
    void spendingDescriptionShouldBeUpdatedByTableAction(SpendJson spend) {
        final String newDescription = "Обучение Niffler NG";

        Selenide.open(CFG.frontUrl(), LoginPage.class)
                .doSuccessLogin(TEST_USER_NAME, TEST_USER_PASSWORD)
                .editSpending(spend.description())
                .editDescription(newDescription);

        new MainPage().checkThatTableContains(newDescription);
    }
}
