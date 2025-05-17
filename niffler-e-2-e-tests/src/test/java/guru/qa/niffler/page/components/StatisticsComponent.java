package guru.qa.niffler.page.components;

import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.SelenideElement;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.List;

import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.sleep;

public class StatisticsComponent {
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
}