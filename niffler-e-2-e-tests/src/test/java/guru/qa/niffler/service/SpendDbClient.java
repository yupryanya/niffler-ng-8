package guru.qa.niffler.service;

import guru.qa.niffler.config.Config;
import guru.qa.niffler.data.dao.impl.CategoryDaoJdbc;
import guru.qa.niffler.data.dao.impl.SpendDaoJdbc;
import guru.qa.niffler.data.entity.spend.CategoryEntity;
import guru.qa.niffler.data.entity.spend.SpendEntity;
import guru.qa.niffler.model.CategoryJson;
import guru.qa.niffler.model.SpendJson;

import java.sql.Connection;

import static guru.qa.niffler.data.Databases.transaction;

public class SpendDbClient {
  private static final Config CFG = Config.getInstance();

  public SpendJson createSpend(SpendJson spend) {
    return transaction(connection -> {
          SpendEntity spendEntity = SpendEntity.fromJson(spend);
          if (spendEntity.getCategory() != null) {
            CategoryDaoJdbc categoryDao = new CategoryDaoJdbc(connection);
            spendEntity.setCategory(
                categoryDao.findCategoryByUserNameAndCategoryName(
                    spendEntity.getUsername(),
                    spendEntity.getCategory().getName()
                ).orElseGet(() -> categoryDao.createCategory(spendEntity.getCategory()))
            );
          }
          return SpendJson.fromEntity(new SpendDaoJdbc(connection).createSpend(spendEntity));
        },
        CFG.spendJdbcUrl(),
        Connection.TRANSACTION_READ_COMMITTED
    );
  }

  public CategoryJson createCategory(CategoryJson categoryJson) {
    return transaction(connection -> {
          return CategoryJson.fromEntity(new CategoryDaoJdbc(connection).createCategory(CategoryEntity.fromJson(categoryJson)));
        },
        CFG.spendJdbcUrl(),
        Connection.TRANSACTION_READ_COMMITTED
    );
  }

  public CategoryJson updateCategory(CategoryJson categoryJson) {
    return transaction(connection -> {
          return CategoryJson.fromEntity(new CategoryDaoJdbc(connection).updateCategory(CategoryEntity.fromJson(categoryJson)));
        },
        CFG.spendJdbcUrl(),
        Connection.TRANSACTION_READ_COMMITTED
    );
  }
}