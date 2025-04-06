package guru.qa.niffler.page;

import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.Selenide;
import guru.qa.niffler.config.Config;

import static com.codeborne.selenide.Condition.*;
import static com.codeborne.selenide.Selectors.byLinkText;
import static com.codeborne.selenide.Selectors.byText;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$$;

public class AllPeoplePage {
    protected static final Config CFG = Config.getInstance();

    private static final String ALL_PEOPLE_TAB_LABEL = "All people";

    private final ElementsCollection allPeopleTableItems = $$("#all tr");

    private String getUrl() {
        return CFG.frontUrl() + "people/all";
    }

    public AllPeoplePage open() {
        return Selenide.open(getUrl(), AllPeoplePage.class)
                .verifyPageIsOpened();
    }

    public AllPeoplePage verifyPageIsOpened() {
        $(byLinkText(ALL_PEOPLE_TAB_LABEL))
                .shouldHave(attribute("aria-selected", "true"));
        return this;
    }

    public AllPeoplePage verifyOutcomingRequestTo(String outcome) {
        allPeopleTableItems
                .find(text(outcome))
                .$(byText("Waiting..."))
                .shouldBe(visible);
        return this;
    }
}