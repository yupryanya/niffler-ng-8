package guru.qa.niffler.jupiter.extension;

import guru.qa.niffler.config.Config;
import guru.qa.niffler.data.jpa.EntityManagers;
import guru.qa.niffler.data.tpl.Connections;
import guru.qa.niffler.data.tpl.DataSources;
import guru.qa.niffler.service.db.UserDbClient;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.List;

public class DatabasesExtension implements SuiteExtension {
  protected static final Config CFG = Config.getInstance();

  @Override
  public void beforeSuite(ExtensionContext context) {
    truncateTables(CFG.authJdbcUrl());
    truncateTables(CFG.userdataJdbcUrl());
    truncateTables(CFG.spendJdbcUrl());

    //For manual testing purposes
//    new UserDbClient().createUser("admin", "admin");
  }

  @Override
  public void afterSuite() {
    Connections.closeAllConnections();
    EntityManagers.closeAllConnections();
  }

  private void truncateTables(String sourceUrl) {
    JdbcTemplate template = new JdbcTemplate(DataSources.dataSource(sourceUrl));
    List<String> tables = template.queryForList(
        "SELECT 'public.' || tablename FROM pg_tables " +
        "WHERE schemaname = 'public' AND tablename NOT IN ('flyway_schema_history')",
        String.class
    );
    String truncateSql = "TRUNCATE TABLE " + String.join(", ", tables) + " CASCADE;";
    template.execute(truncateSql);
  }
}