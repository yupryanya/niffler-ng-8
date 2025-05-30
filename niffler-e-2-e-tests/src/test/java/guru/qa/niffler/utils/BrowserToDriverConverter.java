package guru.qa.niffler.utils;

import com.codeborne.selenide.SelenideConfig;
import com.codeborne.selenide.SelenideDriver;
import guru.qa.niffler.common.configuration.Browser;
import org.junit.jupiter.api.extension.ParameterContext;
import org.junit.jupiter.params.converter.ArgumentConversionException;
import org.junit.jupiter.params.converter.ArgumentConverter;

public class BrowserToDriverConverter implements ArgumentConverter {
  @Override
  public SelenideDriver convert(Object source, ParameterContext context) throws ArgumentConversionException {
    if (!(source instanceof Browser browser)) {
      throw new ArgumentConversionException("Source must be of type Browser, but was: " + source.getClass().getName());
    }

    SelenideConfig config = switch (browser) {
      case FIREFOX -> SelenideUtils.firefoxConfig;
      case CHROME -> SelenideUtils.chromeConfig;
      default -> throw new IllegalArgumentException("Unsupported browser: " + browser);
    };

    return new SelenideDriver(config);
  }
}

