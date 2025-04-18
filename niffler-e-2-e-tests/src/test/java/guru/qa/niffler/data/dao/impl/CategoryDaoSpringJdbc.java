package guru.qa.niffler.data.dao.impl;

import guru.qa.niffler.data.dao.spend.CategoryDao;
import guru.qa.niffler.data.entity.spend.CategoryEntity;
import guru.qa.niffler.data.mapper.CategoryEntityRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;

import javax.sql.DataSource;
import java.sql.PreparedStatement;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class CategoryDaoSpringJdbc implements CategoryDao {
  private final JdbcTemplate jdbcTemplate;

  public CategoryDaoSpringJdbc(DataSource datasource) {
    this.jdbcTemplate = new JdbcTemplate(datasource);
  }

  @Override
  public CategoryEntity createCategory(CategoryEntity category) {
    KeyHolder kh = new GeneratedKeyHolder();
    jdbcTemplate.update(connection -> {
          PreparedStatement ps = connection.prepareStatement(
              "INSERT INTO category (name, username, archived) VALUES (?, ?, ?)",
              PreparedStatement.RETURN_GENERATED_KEYS
          );
          ps.setString(1, category.getName());
          ps.setString(2, category.getUsername());
          ps.setBoolean(3, category.isArchived());
          return ps;
        },
        kh
    );
    final UUID generatedKey = (UUID) kh.getKeys().get("id");
    category.setId(generatedKey);
    return category;
  }

  @Override
  public Optional<CategoryEntity> findCategoryById(UUID id) {
    return Optional.ofNullable(
        jdbcTemplate.queryForObject(
            "SELECT * FROM category WHERE id = ?",
            CategoryEntityRowMapper.instance,
            id
        )
    );
  }

  @Override
  public Optional<CategoryEntity> findCategoryByUserNameAndCategoryName(String userName, String categoryName) {
    return Optional.ofNullable(
        jdbcTemplate.queryForObject(
            "SELECT * FROM category WHERE userName = ? AND name = ?",
            CategoryEntityRowMapper.instance,
            userName,
            categoryName
        )
    );
  }

  @Override
  public List<CategoryEntity> findAllCategoriesByUserName(String userName) {
    return jdbcTemplate.query(
        "SELECT * FROM category WHERE userName = ?",
        CategoryEntityRowMapper.instance,
        userName
    );
  }

  @Override
  public void deleteCategory(CategoryEntity category) {
    jdbcTemplate.update(
        connection -> {
          PreparedStatement ps = connection.prepareStatement(
              "DELETE FROM category WHERE id = ?"
          );
          ps.setObject(1, category.getId());
          return ps;
        }
    );
  }

  @Override
  public CategoryEntity updateCategory(CategoryEntity categoryEntity) {
    jdbcTemplate.update(
        "UPDATE category SET name = ?, archived = ? WHERE id = ?",
        categoryEntity.getName(),
        categoryEntity.isArchived(),
        categoryEntity.getId()
    );
    return categoryEntity;
  }

  @Override
  public List<CategoryEntity> findAll() {
    return jdbcTemplate.query(
        "SELECT * FROM category",
        CategoryEntityRowMapper.instance
    );
  }
}