package guru.qa.niffler.utils;

import com.codeborne.selenide.SelenideConfig;

import static com.codeborne.selenide.Browsers.CHROME;
import static com.codeborne.selenide.Browsers.FIREFOX;

public class SelenideUtils {
  public static final SelenideConfig chromeConfig = new SelenideConfig()
      .browser(CHROME)
      .browserSize("1920x1080");

  public static final SelenideConfig firefoxConfig = new SelenideConfig()
      .browser(FIREFOX)
      .browserSize("1920x1080");
}
