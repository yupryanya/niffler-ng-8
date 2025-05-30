package guru.qa.niffler.test.web;

import guru.qa.niffler.common.values.Color;
import guru.qa.niffler.jupiter.annotation.Category;
import guru.qa.niffler.jupiter.annotation.ScreenshotTest;
import guru.qa.niffler.jupiter.annotation.Spend;
import guru.qa.niffler.jupiter.annotation.User;
import guru.qa.niffler.model.CategoryJson;
import guru.qa.niffler.model.SpendJson;
import guru.qa.niffler.model.UserJson;
import guru.qa.niffler.model.statistics.Bubble;
import org.junit.jupiter.api.Test;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Date;

import static guru.qa.niffler.common.values.CurrencyValues.RUB;

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
    login(user);
    mainPage
        .getStatisticsComponent()
        .verifyStatisticsImageMatchesExpected(expectedImage)
        .verifyStatisticsLegendMatches(
            new Bubble(Color.yellow, "Clothes 1699 " + RUB.getSymbol()),
            new Bubble(Color.green, "Grocery 199.68 " + RUB.getSymbol())
        );
  }

  @User()
  @Test
  @ScreenshotTest(value = "img/empty-stat.png")
  void statisticsChartShouldBeEmptyForNewUser(UserJson user, BufferedImage expectedImage) throws IOException {
    login(user);
    mainPage
        .getStatisticsComponent()
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
    login(user);
    mainPage
        .getSpendTable()
        .editSpend(user.testData().spends().getFirst().description());
    editSpendingPage
        .editAmount("200.00");
    mainPage
        .getStatisticsComponent()
        .verifyStatisticsImageMatchesExpected(expectedImage)
        .verifyStatisticsLegendMatches(
            new Bubble(Color.yellow, "Grocery 299.1 " + RUB.getSymbol())
        );
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
        RUB,
        500.00,
        "Fish",
        null
    );
    login(user);
    mainPage
        .getSpendTable()
        .addSpend();
    addSpendingPage
        .fillAllFields(spendJson);
    mainPage
        .getStatisticsComponent()
        .verifyStatisticsImageMatchesExpected(expectedImage)
        .verifyStatisticsLegendMatches(
            new Bubble(Color.yellow, "Grocery 600 " + RUB.getSymbol())
        );
  }

  @User(
      spends = {
          @Spend(category = "Grocery", description = "Bread", amount = 100.00),
          @Spend(category = "Grocery", description = "Milk", amount = 99.10),
          @Spend(category = "Kids", description = "Toys", amount = 1109.00)
      }
  )
  @Test
  @ScreenshotTest(value = "img/delete-spend-stat.png")
  void statisticsChartShouldChangeWhenDeleteSpend(UserJson user, BufferedImage expectedImage) throws IOException {
    login(user);
    mainPage
        .getSpendTable()
        .deleteSpend("Bread");
    mainPage
        .getStatisticsComponent()
        .verifyStatisticsImageMatchesExpected(expectedImage)
        .verifyStatisticsLegendContains(
            new Bubble(Color.green, "Grocery 99.1 " + RUB.getSymbol())
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
    login(user);
    mainPage
        .getStatisticsComponent()
        .verifyStatisticsImageMatchesExpected(expectedImage)
        .verifyStatisticsLegendMatchesInAnyOrder(
            new Bubble(Color.green, "Archived 599.1 " + RUB.getSymbol()),
            new Bubble(Color.yellow, "Grocery 100 " + RUB.getSymbol())
        );
  }
}
