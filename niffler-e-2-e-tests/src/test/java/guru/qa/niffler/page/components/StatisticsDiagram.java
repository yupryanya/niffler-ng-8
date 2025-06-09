package guru.qa.niffler.page.components;

import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.SelenideElement;
import guru.qa.niffler.model.statistics.Bubble;
import guru.qa.niffler.utils.ScreenDiffResult;
import io.qameta.allure.Step;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;

import static com.codeborne.selenide.CollectionCondition.empty;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.sleep;
import static guru.qa.niffler.condition.StatConditions.*;
import static org.junit.jupiter.api.Assertions.assertFalse;

public class StatisticsDiagram {
  private final SelenideElement statisticsDoughnutChart = $("canvas[role='img']");
  private final SelenideElement statisticsLegend = $("#legend-container");

  public BufferedImage getStatisticImage() {
    sleep(3000);
    try {
      return ImageIO.read(statisticsDoughnutChart.screenshot());
    } catch (IOException e) {
      throw new RuntimeException("Failed to read the image", e);
    }
  }

  public ElementsCollection getStatisticsLegend() {
    return statisticsLegend.$$("li");
  }

  @Step("Verify statistics diagram matches expected image")
  public StatisticsDiagram verifyStatisticsImageMatchesExpected(BufferedImage expectedImage) {
    BufferedImage actualImage = getStatisticImage();
    assertFalse(new ScreenDiffResult(expectedImage, actualImage));
    return this;
  }

  @Step("Verify statistics diagram contains legend bubbles in the expected order")
  public StatisticsDiagram verifyStatisticsLegendMatches(Bubble... bubbles) {
    getStatisticsLegend()
        .should(statBubblesExactOrder(bubbles));
    return this;
  }

  @Step("Verify statistics diagram has legend bubbles in any order")
  public StatisticsDiagram verifyStatisticsLegendMatchesInAnyOrder(Bubble... bubbles) {
    getStatisticsLegend()
        .should(statBubblesAnyOrder(bubbles));
    return this;
  }

  @Step("Verify statistics legend contains all specified bubbles")
  public StatisticsDiagram verifyStatisticsLegendContains(Bubble... bubbles) {
    getStatisticsLegend()
        .should(statBubblesContainsAll(bubbles));
    return this;
  }

  @Step("Verify statistics legend is empty")
  public StatisticsDiagram verifyStatisticsLegendIsEmpty() {
    getStatisticsLegend()
        .shouldBe(empty);
    return this;
  }
}