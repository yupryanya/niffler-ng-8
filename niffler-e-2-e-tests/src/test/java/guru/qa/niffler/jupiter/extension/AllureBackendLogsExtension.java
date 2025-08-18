package guru.qa.niffler.jupiter.extension;

import io.qameta.allure.Allure;
import io.qameta.allure.AllureLifecycle;
import io.qameta.allure.model.TestResult;
import lombok.SneakyThrows;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.UUID;

public class AllureBackendLogsExtension implements SuiteExtension {
  public static final String caseName = "Niffler backend logs";
  String[] services = {"niffler-auth", "niffler-currency", "niffler-gateway", "niffler-spend", "niffler-userdata"};

  @SneakyThrows
  @Override
  public void afterSuite() {
    if ("docker".equals(System.getProperty("test.env"))) {
      System.err.println("Skipping backend logs attachment in Docker environment");
    } else {
      AllureLifecycle allureLifecycle = Allure.getLifecycle();
      final String caseId = UUID.randomUUID().toString();
      allureLifecycle.scheduleTestCase(new TestResult().setUuid(caseId).setName(caseName));
      allureLifecycle.startTestCase(caseId);

      for (String service : services) {
        Path logPath = Path.of("/logs/" + service + "/app.log");
        if (Files.exists(logPath)) {
          allureLifecycle.addAttachment(
              service + " log",
              "text/html",
              ".log",
              Files.newInputStream(logPath)
          );
        } else {
          System.err.println("Log file not found: " + logPath);
        }
      }

      allureLifecycle.stopTestCase(caseId);
      allureLifecycle.writeTestCase(caseId);
    }
  }
}
