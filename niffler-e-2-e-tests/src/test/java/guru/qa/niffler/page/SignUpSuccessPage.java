package guru.qa.niffler.page;

import com.codeborne.selenide.SelenideDriver;
import com.codeborne.selenide.SelenideElement;

import static com.codeborne.selenide.Condition.text;

public class SignUpSuccessPage extends BasePage{
    private final SelenideElement successMessage;
    private final SelenideElement loginButton;

    public static final String SUCCESSFULLY_REGISTERED = "Congratulations! You've registered!";

    public SignUpSuccessPage(SelenideDriver driver) {
        super(driver);
        this.successMessage = driver.$(".form__paragraph.form__paragraph_success");
        this.loginButton = driver.$("a[class='form_sign-in']");
    }

    public void clickLoginButton() {
        loginButton.click();
    }

    public void verifySuccessSignUpMessage() {
        successMessage.shouldHave(text(SUCCESSFULLY_REGISTERED));
    }
}
