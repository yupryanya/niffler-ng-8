package guru.qa.niffler.service;

import guru.qa.niffler.data.dao.CategoryDao;
import guru.qa.niffler.data.dao.SpendDao;
import guru.qa.niffler.data.dao.impl.CategoryDaoJdbc;
import guru.qa.niffler.data.dao.impl.SpendDaoJdbc;
import guru.qa.niffler.data.entity.spend.CategoryEntity;
import guru.qa.niffler.data.entity.spend.SpendEntity;
import guru.qa.niffler.model.CategoryJson;
import guru.qa.niffler.model.SpendJson;

public class SpendDbClient {
  private final SpendDao spendDao = new SpendDaoJdbc();
  private final CategoryDao categoryDao = new CategoryDaoJdbc();

  public SpendJson createSpend(SpendJson spend) {
    SpendEntity spendEntity = SpendEntity.fromJson(spend);

    if (spendEntity.getCategory() != null) {
      spendEntity.setCategory(
          categoryDao.findCategoryByUserNameAndCategoryName(
              spendEntity.getUsername(),
              spendEntity.getCategory().getName()
          ).orElseGet(() -> categoryDao.createCategory(spendEntity.getCategory()))
      );
    }
    return SpendJson.fromEntity(spendDao.createSpend(spendEntity));
  }

  public CategoryJson createCategory(CategoryJson categoryJson) {
    return CategoryJson.fromEntity(
        categoryDao.createCategory(CategoryEntity.fromJson(categoryJson)));
  }

  public CategoryJson updateCategory(CategoryJson categoryJson) {
    return CategoryJson.fromEntity(
        categoryDao.updateCategory(CategoryEntity.fromJson(categoryJson)));
  }
}