package guru.qa.niffler.test.web;

import com.codeborne.selenide.Selenide;
import guru.qa.niffler.jupiter.annotation.Category;
import guru.qa.niffler.model.CategoryJson;
import guru.qa.niffler.page.LoginPage;
import guru.qa.niffler.page.ProfilePage;
import org.junit.jupiter.api.Test;

public class ProfileTest extends BaseTestWeb {
    @Category(
            username = TEST_USER_NAME,
            archived = false
    )
    @Test
    void activeCategoryShouldBePresentInCategoriesList(CategoryJson category) {
        Selenide.open(CFG.authUrl(), LoginPage.class)
                .doSuccessLogin(TEST_USER_NAME, TEST_USER_PASSWORD)
                .verifyMainPageIsOpened();
        Selenide.open(ProfilePage.URL, ProfilePage.class)
                .verifyCategoryIsDisplayed(category.name());
    }

    @Category(
            username = TEST_USER_NAME,
            archived = true
    )
    @Test
    void archivedCategoryShouldBePresentInCategoriesList(CategoryJson category) {
        Selenide.open(CFG.authUrl(), LoginPage.class)
                .doSuccessLogin(TEST_USER_NAME, TEST_USER_PASSWORD)
                .verifyMainPageIsOpened();
        Selenide.open(ProfilePage.URL, ProfilePage.class)
                .switchArchivedCategoriesToVisible()
                .verifyCategoryIsDisplayed(category.name());
    }
}