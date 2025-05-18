package guru.qa.niffler.page.components;

import com.codeborne.selenide.ElementsCollection;

import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Selenide.$$;

public class SpendTable {
  private final ElementsCollection tableRows = $$("#spendings tbody tr");

  public ElementsCollection getTableRows() {
    return tableRows;
  }

  public ElementsCollection getCellsBySpendDescription(String spendDescription) {
    return tableRows.findBy(text(spendDescription))
        .$$("td");
  }
}
