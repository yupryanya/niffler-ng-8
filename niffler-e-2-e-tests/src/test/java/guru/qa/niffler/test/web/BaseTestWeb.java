package guru.qa.niffler.test.web;

import guru.qa.niffler.config.Config;
import guru.qa.niffler.jupiter.annotation.meta.WebTest;
import guru.qa.niffler.page.MainPage;

@WebTest
public class BaseTestWeb {
  protected static final Config CFG = Config.getInstance();
  protected final MainPage mainPage = new MainPage();
}
