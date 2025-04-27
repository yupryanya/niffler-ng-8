package guru.qa.niffler.service;

import guru.qa.niffler.config.Config;
import guru.qa.niffler.data.dao.impl.CategoryDaoSpringJdbc;
import guru.qa.niffler.data.dao.impl.SpendDaoSpringJdbc;
import guru.qa.niffler.data.dao.spend.CategoryDao;
import guru.qa.niffler.data.dao.spend.SpendDao;
import guru.qa.niffler.data.entity.spend.CategoryEntity;
import guru.qa.niffler.data.entity.spend.SpendEntity;
import guru.qa.niffler.data.tpl.JdbcTransactionTemplate;
import guru.qa.niffler.data.tpl.XaTransactionTemplate;
import guru.qa.niffler.model.CategoryJson;
import guru.qa.niffler.model.SpendJson;

public class SpendDbClient {
  private static final Config CFG = Config.getInstance();

  SpendDao spendDao = new SpendDaoSpringJdbc();
  CategoryDao categoryDao = new CategoryDaoSpringJdbc();

  private final JdbcTransactionTemplate jdbcTxTemplate = new JdbcTransactionTemplate(
      CFG.spendJdbcUrl()
  );

  private final XaTransactionTemplate xaTxTemplate = new XaTransactionTemplate(
      CFG.spendJdbcUrl()
  );

  public SpendJson createSpend(SpendJson spend) {
    return xaTxTemplate.execute(() -> {
          SpendEntity spendEntity = SpendEntity.fromJson(spend);
          CategoryEntity categoryEntity = new CategoryEntity();

          if (spend.category().name() != null) {
            categoryEntity = categoryDao.findCategoryByUserNameAndCategoryName(spend.category().username(), spend.category().name())
                .orElseGet(() -> categoryDao.createCategory(CategoryEntity.fromJson(spend.category())));
          } else {
            throw new IllegalArgumentException("Category cannot be null");
          }
          spendEntity.setCategory(categoryEntity);

          return SpendJson.fromEntity(
              spendDao.createSpend(spendEntity),
              categoryEntity
          );
        }
    );
  }

  public CategoryJson createCategory(CategoryJson categoryJson) {
    return jdbcTxTemplate.execute(() -> {
          return CategoryJson.fromEntity(categoryDao.createCategory(CategoryEntity.fromJson(categoryJson)));
        }
    );
  }

  public CategoryJson updateCategory(CategoryJson categoryJson) {
    return jdbcTxTemplate.execute(() -> {
          return CategoryJson.fromEntity(categoryDao.updateCategory(CategoryEntity.fromJson(categoryJson)));
        }
    );
  }
}