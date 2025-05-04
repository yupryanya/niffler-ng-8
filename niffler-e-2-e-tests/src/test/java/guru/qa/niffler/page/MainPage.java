package guru.qa.niffler.page;

import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.SelenideElement;

import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$$;

public class MainPage {
    private final ElementsCollection tableRows = $$("#spendings tbody tr");
    private final SelenideElement statistics = $("#stat");
    private final SelenideElement spends = $("#spendings");
    private final SelenideElement searchField = $("input[aria-label='search']");
    private final SelenideElement clearSearchButton = $("#input-clear");

    public MainPage searchSpendByDescription(String description) {
        if (clearSearchButton.isDisplayed()) {
            clearSearchButton.click();
        }
        searchField.setValue(description).pressEnter();
        return this;
    }

    public EditSpendingPage editSpending(String spendDescription) {
        searchSpendByDescription(spendDescription);
        tableRows.find(text(spendDescription))
                .$$("td")
                .get(5)
                .click();
        return new EditSpendingPage();
    }

    public void checkThatTableContains(String spendDescription) {
        searchSpendByDescription(spendDescription);
        tableRows.find(text(spendDescription))
                .should(visible);
    }

    public void verifyMainPageIsOpened() {
        statistics.should(visible);
        spends.should(visible);
    }
}