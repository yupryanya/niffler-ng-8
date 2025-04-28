package guru.qa.niffler.service.db;

import guru.qa.niffler.config.Config;
import guru.qa.niffler.data.entity.spend.CategoryEntity;
import guru.qa.niffler.data.entity.spend.SpendEntity;
import guru.qa.niffler.data.repository.SpendRepository;
import guru.qa.niffler.data.repository.impl.spend.SpendRepositoryHibernate;
import guru.qa.niffler.data.tpl.JdbcTransactionTemplate;
import guru.qa.niffler.data.tpl.XaTransactionTemplate;
import guru.qa.niffler.model.CategoryJson;
import guru.qa.niffler.model.SpendJson;
import guru.qa.niffler.service.SpendClient;

public class SpendDbClient implements SpendClient {
  private static final Config CFG = Config.getInstance();

  // SpendRepository spend = new SpendRepositoryJdbc();
  // SpendRepository spend = new SpendRepositorySpringJdbc();

  SpendRepository spend = new SpendRepositoryHibernate();

  private final JdbcTransactionTemplate jdbcTxTemplate = new JdbcTransactionTemplate(
      CFG.spendJdbcUrl()
  );

  private final XaTransactionTemplate xaTransactionTemplate = new XaTransactionTemplate(
      CFG.spendJdbcUrl()
  );

  @Override
  public SpendJson createSpend(SpendJson spend) {
    return xaTransactionTemplate.execute(() -> {
          SpendEntity spendEntity = SpendEntity.fromJson(spend);
          CategoryEntity categoryEntity = new CategoryEntity();

          if (spend.category().name() != null) {
            categoryEntity = this.spend.findCategoryByUserNameAndCategoryName(spend.category().username(), spend.category().name())
                .orElseGet(() -> this.spend.createCategory(CategoryEntity.fromJson(spend.category())));
          } else {
            throw new IllegalArgumentException("Category cannot be null");
          }
          spendEntity.setCategory(categoryEntity);

          return SpendJson.fromEntity(
              this.spend.createSpend(spendEntity),
              categoryEntity
          );
        }
    );
  }

  @Override
  public CategoryJson createCategory(CategoryJson categoryJson) {
    return jdbcTxTemplate.execute(() -> {
          return CategoryJson.fromEntity(spend.createCategory(CategoryEntity.fromJson(categoryJson)));
        }
    );
  }

  @Override
  public CategoryJson updateCategory(CategoryJson categoryJson) {
    return jdbcTxTemplate.execute(() -> {
          return CategoryJson.fromEntity(spend.updateCategory(CategoryEntity.fromJson(categoryJson)));
        }
    );
  }
}