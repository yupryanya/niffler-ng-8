package guru.qa.niffler.test.web;

import guru.qa.niffler.config.Config;
import guru.qa.niffler.jupiter.BrowserExtension;
import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith(BrowserExtension.class)
public class BaseTestWeb {
    protected static final Config CFG = Config.getInstance();

    protected static final String TEST_USER_NAME = "testuser";
    protected static final String TEST_USER_PASSWORD = "testpassword";
}
