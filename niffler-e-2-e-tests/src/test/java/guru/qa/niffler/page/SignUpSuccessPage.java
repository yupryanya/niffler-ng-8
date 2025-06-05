package guru.qa.niffler.page;

import com.codeborne.selenide.SelenideElement;

import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Selenide.$;

public class SignUpSuccessPage {
    private final SelenideElement successMessage = $(".form__paragraph.form__paragraph_success");
    private final SelenideElement LoginButton = $("a[class='form_sign-in']");

    public static final String SUCCESSFULLY_REGISTERED = "Congratulations! You've registered!";

    public LoginPage clickLoginButton() {
        LoginButton.click();
        return new LoginPage();
    }

    public SignUpPage verifySuccessSignUpMessage() {
        successMessage.shouldHave(text(SUCCESSFULLY_REGISTERED));
        return new SignUpPage();
    }
}
