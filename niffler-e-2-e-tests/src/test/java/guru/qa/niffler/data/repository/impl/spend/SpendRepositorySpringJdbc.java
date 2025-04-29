package guru.qa.niffler.data.repository.impl.spend;

import guru.qa.niffler.config.Config;
import guru.qa.niffler.data.dao.impl.CategoryDaoSpringJdbc;
import guru.qa.niffler.data.dao.impl.SpendDaoSpringJdbc;
import guru.qa.niffler.data.dao.spend.CategoryDao;
import guru.qa.niffler.data.dao.spend.SpendDao;
import guru.qa.niffler.data.entity.spend.CategoryEntity;
import guru.qa.niffler.data.entity.spend.SpendEntity;
import guru.qa.niffler.data.extractor.SpendEntityExtractor;
import guru.qa.niffler.data.repository.SpendRepository;
import guru.qa.niffler.data.tpl.DataSources;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.Optional;
import java.util.UUID;

public class SpendRepositorySpringJdbc implements SpendRepository {
  private static final Config CFG = Config.getInstance();

  private final SpendDao spendDao = new SpendDaoSpringJdbc();
  private final CategoryDao categoryDao = new CategoryDaoSpringJdbc();

  private final JdbcTemplate jdbcTemplate;

  public SpendRepositorySpringJdbc() {
    this.jdbcTemplate = new JdbcTemplate(DataSources.dataSource(CFG.spendJdbcUrl()));
  }

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
    KeyHolder kh = new GeneratedKeyHolder();
    jdbcTemplate.update(con -> {
      PreparedStatement ps = con.prepareStatement(
          """
                UPDATE spend
                SET spend_date = ?, currency = ?, amount = ?, description = ?, category_id = ?
                WHERE id = ?
              """, Statement.RETURN_GENERATED_KEYS);
      ps.setDate(1, new java.sql.Date(spend.getSpendDate().getTime()));
      ps.setString(2, spend.getCurrency().name());
      ps.setDouble(3, spend.getAmount());
      ps.setString(4, spend.getDescription());
      ps.setObject(5, spend.getCategory().getId());
      ps.setObject(6, spend.getId());
      return ps;
    }, kh);

    UUID categoryId = spend.getCategory().getId();
    if (categoryId != null && categoryDao.findCategoryById(categoryId).isPresent()) {
      categoryDao.updateCategory(spend.getCategory());
    }

    return spend;
  }

  @Override
  public Optional<SpendEntity> findSpendById(UUID id) {
    return Optional.ofNullable(
        jdbcTemplate.query(
            """
                  SELECT s.*, c.name AS category_name, c.archived
                  FROM spend s JOIN category c
                  ON s.category_id = c.id
                  WHERE s.id = ?
                """,
            SpendEntityExtractor.instance,
            id
        )
    );
  }

  @Override
  public Optional<SpendEntity> findSpendByUserNameAndSpendDescription(String userName, String spendDescription) {
    return Optional.ofNullable(
        jdbcTemplate.query(
            """
                 SELECT s.*, c.name AS category_name, c.archived
                  FROM spend s JOIN category c
                  ON s.category_id = c.id
                  WHERE s.username = ? and s.description = ?
                """,
            SpendEntityExtractor.instance,
            userName,
            spendDescription
        )
    );
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
