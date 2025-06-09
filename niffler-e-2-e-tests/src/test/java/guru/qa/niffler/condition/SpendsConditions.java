package guru.qa.niffler.condition;

import com.codeborne.selenide.CheckResult;
import com.codeborne.selenide.Driver;
import com.codeborne.selenide.WebElementsCondition;
import guru.qa.niffler.model.SpendJson;
import org.apache.commons.lang3.ArrayUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

@ParametersAreNonnullByDefault
public class SpendsConditions {
  public static @Nonnull WebElementsCondition spends(SpendJson... expectedSpends) {
    if (ArrayUtils.isEmpty(expectedSpends)) {
      throw new IllegalArgumentException("Expected spends cannot be null or empty");
    }

    return new WebElementsCondition() {
      @Override
      public CheckResult check(Driver driver, List<WebElement> elements) {
        if (elements.size() != expectedSpends.length) {
          return CheckResult.rejected(
              String.format("Expected %d spend rows, but found %d", expectedSpends.length, elements.size()),
              elements
          );
        }

        List<String> allMismatches = new ArrayList<>();
        List<List<String>> actualData = new ArrayList<>();

        for (int i = 0; i < elements.size(); i++) {
          List<String> actual = extractCellTexts(elements.get(i));
          List<String> expected = convertToExpectedList(expectedSpends[i]);

          actualData.add(actual);
          List<String> rowMismatches = new ArrayList<>();

          compare("category", i, expected.get(0), actual.get(0), rowMismatches);
          compare("amount", i, expected.get(1), actual.get(1), rowMismatches);
          compare("description", i, expected.get(2), actual.get(2), rowMismatches);
          compare("date", i, expected.get(3), actual.get(3), rowMismatches);

          allMismatches.addAll(rowMismatches);
        }

        if (!allMismatches.isEmpty()) {
          return CheckResult.rejected(String.join("\n", allMismatches), actualData);
        }

        return CheckResult.accepted();
      }

      private void compare(String field, int row, String expected, String actual, List<String> errors) {
        if (!expected.equals(actual)) {
          errors.add(String.format("Row %d: %s mismatch (expected: %s, actual: %s)", row, field, expected, actual));
        }
      }

      private List<String> extractCellTexts(WebElement row) {
        return row.findElements(By.tagName("td"))
            .stream()
            .skip(1)
            .limit(4)
            .map(WebElement::getText)
            .toList();
      }

      private List<String> convertToExpectedList(SpendJson spend) {
        return List.of(
            spend.category().name(),
            formatAmount(spend.amount(), spend.currency().getSymbol()),
            spend.description(),
            formatDate(spend.spendDate())
        );
      }

      private String formatAmount(Double amount, String currencySymbol) {
        return new DecimalFormat("0.##").format(amount) + " " + currencySymbol;
      }

      private String formatDate(Date date) {
        return new SimpleDateFormat("MMM dd, yyyy").format(date);
      }

      @Override
      public String toString() {
        List<List<String>> expectedFormatted = Arrays.stream(expectedSpends)
            .map(this::convertToExpectedList)
            .toList();

        return expectedFormatted.toString();
      }
    };
  }
}