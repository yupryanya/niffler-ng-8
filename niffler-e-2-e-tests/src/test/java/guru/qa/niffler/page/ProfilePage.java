package guru.qa.niffler.page;

import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.SelenideElement;
import guru.qa.niffler.config.Config;

import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$$;

public class ProfilePage {
    protected static final Config CFG = Config.getInstance();
    public static final String URL = CFG.frontUrl() + "profile";

    private final SelenideElement showArchivedCategoriesToggle = $("input[type='checkbox']");
    private final ElementsCollection categories = $$("div.MuiButtonBase-root[role='button']");

    public SelenideElement findCategory(String name) {
        return categories.findBy(text(name));
    }

    public ProfilePage verifyCategoryIsDisplayed(String name) {
        findCategory(name).should(visible);
        return this;
    }

    public ProfilePage switchArchivedCategoriesToVisible() {
        showArchivedCategoriesToggle.click();
        return this;
    }
}
