package guru.qa.niffler.condition;

import com.codeborne.selenide.CheckResult;
import com.codeborne.selenide.Driver;
import com.codeborne.selenide.WebElementsCondition;
import guru.qa.niffler.common.values.Color;
import guru.qa.niffler.model.statistics.Bubble;
import org.apache.commons.lang.ArrayUtils;
import org.openqa.selenium.WebElement;

import java.util.Arrays;
import java.util.List;

import static java.util.stream.Collectors.toList;

public class StatConditions {
  public static WebElementsCondition statBubblesExactOrder(Bubble... expectedBubbles) {
    if (ArrayUtils.isEmpty(expectedBubbles)) {
      throw new IllegalArgumentException("Expected bubbles cannot be empty");
    }

    return new WebElementsCondition() {
      @Override
      public CheckResult check(Driver driver, List<WebElement> elements) {
        if (elements.size() != expectedBubbles.length) {
          return CheckResult.rejected(
              String.format("Expected %d elements, but found %d", expectedBubbles.length, elements.size()),
              elements
          );
        }

        List<Bubble> actualBubbles = elements.stream()
            .map(el -> new Bubble(
                Color.fromRgb(el.getCssValue("background-color")).orElse(null),
                el.getText()))
            .collect(toList());

        boolean passed = true;
        for (int i = 0; i < elements.size(); i++) {
          if (passed) {
            Bubble expectedBubble = expectedBubbles[i];
            Bubble actualBubble = actualBubbles.get(i);
            passed = expectedBubble.equals(actualBubble);
          }
        }
        if (!passed) {
          return CheckResult.rejected(
              String.format("Ordered bubbles mismatch (expected: %s, actual: %s)", expectedBubbles, actualBubbles),
              actualBubbles
          );
        }
        return CheckResult.accepted();
      }

      @Override
      public String toString() {
        return Arrays.toString(expectedBubbles);
      }

    };
  }

  public static WebElementsCondition statBubblesAnyOrder(Bubble... expectedBubbles) {
    if (ArrayUtils.isEmpty(expectedBubbles)) {
      throw new IllegalArgumentException("Expected bubbles cannot be empty");
    }

    return new WebElementsCondition() {
      @Override
      public CheckResult check(Driver driver, List<WebElement> elements) {
        if (elements.size() != expectedBubbles.length) {
          return CheckResult.rejected(
              String.format("Expected %d elements, but found %d", expectedBubbles.length, elements.size()),
              elements
          );
        }

        List<Bubble> actualBubbles = elements.stream()
            .map(el -> new Bubble(
                Color.fromRgb(el.getCssValue("background-color")).orElse(null),
                el.getText()))
            .collect(toList());

        List<Bubble> expected = Arrays.asList(expectedBubbles);

        boolean passed = actualBubbles.containsAll(expected) && expected.containsAll(actualBubbles);

        if (!passed) {
          return CheckResult.rejected(
              String.format("Unordered bubble mismatch (expected: %s, actual: %s)", expected, actualBubbles),
              actualBubbles
          );
        }

        return CheckResult.accepted();
      }

      @Override
      public String toString() {
        return Arrays.toString(expectedBubbles);
      }
    };
  }

  public static WebElementsCondition statBubblesContainsAll(Bubble... expectedBubbles) {
    if (ArrayUtils.isEmpty(expectedBubbles)) {
      throw new IllegalArgumentException("Expected bubbles cannot be empty");
    }

    return new WebElementsCondition() {
      @Override
      public CheckResult check(Driver driver, List<WebElement> elements) {
        List<Bubble> actualBubbles = elements.stream()
            .map(el -> new Bubble(
                Color.fromRgb(el.getCssValue("background-color")).orElse(null),
                el.getText()))
            .collect(toList());

        List<Bubble> expected = Arrays.asList(expectedBubbles);

        boolean passed = actualBubbles.containsAll(expected);

        if (!passed) {
          return CheckResult.rejected(
              String.format("Missing expected bubbles. Expected: %s, Actual: %s", expected, actualBubbles),
              actualBubbles
          );
        }

        return CheckResult.accepted();
      }

      @Override
      public String toString() {
        return Arrays.toString(expectedBubbles);
      }
    };
  }
}