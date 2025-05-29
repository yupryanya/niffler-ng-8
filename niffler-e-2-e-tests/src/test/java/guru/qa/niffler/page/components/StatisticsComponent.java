package guru.qa.niffler.page.components;

import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.SelenideDriver;
import com.codeborne.selenide.SelenideElement;
import guru.qa.niffler.model.statistics.Bubble;
import guru.qa.niffler.utils.ScreenDiffResult;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;

import static com.codeborne.selenide.CollectionCondition.empty;
import static guru.qa.niffler.condition.StatConditions.*;
import static java.lang.Thread.sleep;
import static org.junit.jupiter.api.Assertions.assertFalse;

public class StatisticsComponent extends BaseComponent {
  private final SelenideElement statisticsDoughnutChart;
  private final SelenideElement statisticsLegend;

  public StatisticsComponent(SelenideDriver driver) {
    super(driver);
    this.statisticsDoughnutChart = driver.$("canvas[role='img']");
    this.statisticsLegend = driver.$("#legend-container");
  }

  public BufferedImage getStatisticImage() {
    try {
      sleep(3000);
      return ImageIO.read(statisticsDoughnutChart.screenshot());
    } catch (IOException e) {
      throw new RuntimeException("Failed to read the image", e);
    } catch (Exception e) {
      throw new RuntimeException("An unexpected error occurred", e);
    }
  }

  public ElementsCollection getStatisticsLegend() {
    return statisticsLegend.$$("li");
  }

  public StatisticsComponent verifyStatisticsImageMatchesExpected(BufferedImage expectedImage) {
    BufferedImage actualImage = getStatisticImage();
    assertFalse(new ScreenDiffResult(expectedImage, actualImage));
    return this;
  }

  public StatisticsComponent verifyStatisticsLegendMatches(Bubble... bubbles) {
    getStatisticsLegend()
        .should(statBubblesExactOrder(bubbles));
    return this;
  }

  public StatisticsComponent verifyStatisticsLegendMatchesInAnyOrder(Bubble... bubbles) {
    getStatisticsLegend()
        .should(statBubblesAnyOrder(bubbles));
    return this;
  }

  public StatisticsComponent verifyStatisticsLegendContains(Bubble... bubbles) {
    getStatisticsLegend()
        .should(statBubblesContainsAll(bubbles));
    return this;
  }

  public StatisticsComponent verifyStatisticsLegendIsEmpty() {
    getStatisticsLegend()
        .shouldBe(empty);
    return this;
  }
}