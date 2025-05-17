package guru.qa.niffler.test.web;

import com.codeborne.selenide.Selenide;
import guru.qa.niffler.common.values.CurrencyValues;
import guru.qa.niffler.jupiter.annotation.Category;
import guru.qa.niffler.jupiter.annotation.ScreenshotTest;
import guru.qa.niffler.jupiter.annotation.Spend;
import guru.qa.niffler.jupiter.annotation.User;
import guru.qa.niffler.model.CategoryJson;
import guru.qa.niffler.model.SpendJson;
import guru.qa.niffler.model.UserJson;
import guru.qa.niffler.page.LoginPage;
import guru.qa.niffler.page.MainPage;
import org.junit.jupiter.api.Test;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Date;

public class StatisticsTest extends BaseTestWeb {
  @User(
      spends = {
          @Spend(category = "Grocery", description = "Bread", amount = 100.56),
          @Spend(category = "Grocery", description = "Milk", amount = 99.12),
          @Spend(category = "Clothes", description = "T-shirt", amount = 1699.00)
      }
  )
  @Test
  @ScreenshotTest(value = "img/expected-stat.png")
  void multipleCategoriesShouldBeDisplayedInStatisticsChart(UserJson user, BufferedImage expectedImage) throws IOException {
    Selenide.open(CFG.frontUrl(), LoginPage.class)
        .doSuccessLogin(user.username(), user.testData().password())
        .verifyMainPageIsOpened()
        .verifyStatisticsImageMatchesExpected(expectedImage)
        .verifyStatisticsLegendContains(
            "Grocery 199.68 " + CurrencyValues.RUB.getSymbol(),
            "Clothes 1699 " + CurrencyValues.RUB.getSymbol()
        );
  }

  @User()
  @Test
  @ScreenshotTest(value = "img/empty-stat.png")
  void statisticsChartShouldBeEmptyForNewUser(UserJson user, BufferedImage expectedImage) throws IOException {
    Selenide.open(CFG.frontUrl(), LoginPage.class)
        .doSuccessLogin(user.username(), user.testData().password())
        .verifyMainPageIsOpened()
        .verifyStatisticsImageMatchesExpected(expectedImage)
        .verifyStatisticsLegendIsEmpty();
  }

  @User(
      spends = {
          @Spend(category = "Grocery", description = "Bread", amount = 100.00),
          @Spend(category = "Grocery", description = "Milk", amount = 99.10)
      }
  )
  @Test
  @ScreenshotTest(value = "img/edited-amount-stat.png")
  void statisticsChartShouldChangeWhenEditSpendAmount(UserJson user, BufferedImage expectedImage) throws IOException {
    Selenide.open(CFG.frontUrl(), LoginPage.class)
        .doSuccessLogin(user.username(), user.testData().password())
        .verifyMainPageIsOpened()
        .editSpend(user.testData().spends().getFirst().description())
        .editAmount("200.00");
    new MainPage().verifyStatisticsImageMatchesExpected(expectedImage)
        .verifyStatisticsLegendContains("Grocery 299.1 " + CurrencyValues.RUB.getSymbol());
  }

  @User(
      spends = {
          @Spend(category = "Grocery", description = "Bread", amount = 100.00)
      }
  )
  @Test
  @ScreenshotTest(value = "img/add-spend-stat.png")
  void statisticsChartShouldChangeWhenAddSpend(UserJson user, BufferedImage expectedImage) throws IOException {
    SpendJson spendJson = new SpendJson(
        null,
        new Date(),
        new CategoryJson(
            null,
            "Grocery",
            null,
            false
        ),
        CurrencyValues.RUB,
        500.00,
        "Fish",
        null
    );
    Selenide.open(CFG.frontUrl(), LoginPage.class)
        .doSuccessLogin(user.username(), user.testData().password())
        .verifyMainPageIsOpened()
        .addSpend()
        .fillAllFields(spendJson)
        .verifyStatisticsImageMatchesExpected(expectedImage)
        .verifyStatisticsLegendContains(
            "Grocery 600 " + CurrencyValues.RUB.getSymbol()
        );
  }

  @User(
      spends = {
          @Spend(category = "Grocery", description = "Bread", amount = 100.00),
          @Spend(category = "Grocery", description = "Milk", amount = 99.10)
      }
  )
  @Test
  @ScreenshotTest(value = "img/delete-spend-stat.png")
  void statisticsChartShouldChangeWhenDeleteSpend(UserJson user, BufferedImage expectedImage) throws IOException {
    Selenide.open(CFG.frontUrl(), LoginPage.class)
        .doSuccessLogin(user.username(), user.testData().password())
        .verifyMainPageIsOpened()
        .deleteSpend("Bread")
        .verifyStatisticsImageMatchesExpected(expectedImage)
        .verifyStatisticsLegendContains(
            "Grocery 99.1 " + CurrencyValues.RUB.getSymbol()
        );
  }

  @User(
      spends = {
          @Spend(category = "Grocery", description = "Bread", amount = 100.00),
          @Spend(category = "DIY", description = "Hammer", amount = 599.10)
      },
      categories = {
          @Category(name = "DIY", archived = true)
      }
  )
  @Test
  @ScreenshotTest(value = "img/archive-spend-stat.png")
  void statisticsChartShouldDisplayArchivedSpends(UserJson user, BufferedImage expectedImage) throws IOException {
    Selenide.open(CFG.frontUrl(), LoginPage.class)
        .doSuccessLogin(user.username(), user.testData().password())
        .verifyMainPageIsOpened()
        .verifyStatisticsImageMatchesExpected(expectedImage)
        .verifyStatisticsLegendContains(
            "Grocery 100 " + CurrencyValues.RUB.getSymbol(),
            "Archived 599.1 " + CurrencyValues.RUB.getSymbol()
        );
  }
}
