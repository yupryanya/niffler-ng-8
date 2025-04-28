package guru.qa.niffler.data.repository.impl.spend;

import guru.qa.niffler.config.Config;
import guru.qa.niffler.data.dao.impl.CategoryDaoJdbc;
import guru.qa.niffler.data.dao.impl.SpendDaoJdbc;
import guru.qa.niffler.data.dao.spend.CategoryDao;
import guru.qa.niffler.data.dao.spend.SpendDao;
import guru.qa.niffler.data.entity.spend.CategoryEntity;
import guru.qa.niffler.data.entity.spend.SpendEntity;
import guru.qa.niffler.data.mapper.SpendEntityRowMapper;
import guru.qa.niffler.data.repository.SpendRepository;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;
import java.util.UUID;

import static guru.qa.niffler.data.tpl.Connections.holder;

public class SpendRepositoryJdbc implements SpendRepository {
  private static final Config CFG = Config.getInstance();

  private final SpendDao spendDao = new SpendDaoJdbc();
  private final CategoryDao categoryDao = new CategoryDaoJdbc();

  @Override
  public SpendEntity createSpend(SpendEntity spend) {
    UUID categoryId = spend.getCategory().getId();

    if (categoryId == null || categoryDao.findCategoryById(categoryId).isEmpty()) {
      spend.setCategory(categoryDao.createCategory(spend.getCategory()));
    }
    return spendDao.createSpend(spend);
  }

  @Override
  public SpendEntity updateSpend(SpendEntity spend) {
    try (PreparedStatement psSpend = holder(CFG.spendJdbcUrl()).connection().prepareStatement(
        "UPDATE spend SET spend_date = ?, currency = ?, amount = ?, description = ?, category_id = ? WHERE id = ?"
    )) {
      psSpend.setDate(1, new java.sql.Date(spend.getSpendDate().getTime()));
      psSpend.setString(2, spend.getCurrency().name());
      psSpend.setDouble(3, spend.getAmount());
      psSpend.setString(4, spend.getDescription());
      psSpend.setObject(5, spend.getCategory().getId());
      psSpend.setObject(6, spend.getId());
      psSpend.executeUpdate();

      if (spend.getCategory() != null) {
        categoryDao.updateCategory(spend.getCategory());
      }

      return spend;
    } catch (SQLException e) {
      throw new RuntimeException("Failed to update spend", e);
    }
  }

  @Override
  public Optional<SpendEntity> findSpendById(UUID id) {
    return spendDao.findSpendById(id).map(spend -> {
      categoryDao.findCategoryById(spend.getCategory().getId())
          .ifPresent(spend::setCategory);
      return spend;
    });
  }

  @Override
  public Optional<SpendEntity> findSpendByUserNameAndSpendDescription(String userName, String spendDescription) {
    try (PreparedStatement ps = holder(CFG.spendJdbcUrl()).connection().prepareStatement(
        "SELECT * FROM spend WHERE username = ? AND description = ?"
    )) {
      ps.setString(1, userName);
      ps.setString(2, spendDescription);
      ps.execute();
      try (ResultSet rs = ps.executeQuery()) {
        if (rs.next()) {
          SpendEntity spend = SpendEntityRowMapper.instance.mapRow(rs, rs.getRow());
          if (spend.getCategory() != null) {
            spend.setCategory(categoryDao.findCategoryById(spend.getCategory().getId()).orElse(null));
          }
          return Optional.ofNullable(spend);
        } else {
          return Optional.empty();
        }
      }
    } catch (SQLException e) {
      throw new RuntimeException("Failed to find spend by username and spend description", e);
    }
  }

  @Override
  public void removeSpend(SpendEntity spend) {
    spendDao.deleteSpend(spend);
  }

  @Override
  public CategoryEntity createCategory(CategoryEntity category) {
    return categoryDao.createCategory(category);
  }

  @Override
  public CategoryEntity updateCategory(CategoryEntity category) {
    return categoryDao.updateCategory(category);
  }

  @Override
  public Optional<CategoryEntity> findCategoryById(UUID id) {
    return categoryDao.findCategoryById(id);
  }

  @Override
  public Optional<CategoryEntity> findCategoryByUserNameAndCategoryName(String userName, String categoryName) {
    return categoryDao.findCategoryByUserNameAndCategoryName(userName, categoryName);
  }

  @Override
  public void removeCategory(CategoryEntity category) {
    categoryDao.deleteCategory(category);
  }
}