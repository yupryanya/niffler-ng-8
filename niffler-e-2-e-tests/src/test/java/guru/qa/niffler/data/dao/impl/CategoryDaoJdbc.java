package guru.qa.niffler.data.dao.impl;

import guru.qa.niffler.config.Config;
import guru.qa.niffler.data.Databases;
import guru.qa.niffler.data.dao.CategoryDao;
import guru.qa.niffler.data.entity.spend.CategoryEntity;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class CategoryDaoJdbc implements CategoryDao {
  private static final Config CFG = Config.getInstance();

  @Override
  public CategoryEntity createCategory(CategoryEntity category) {
    try (Connection connection = Databases.connection(CFG.spendJdbcUrl())) {
      try (PreparedStatement ps = connection.prepareStatement(
          "INSERT INTO category (name, username, archived) VALUES (?, ?, ?)",
          Statement.RETURN_GENERATED_KEYS)) {
        ps.setString(1, category.getName());
        ps.setString(2, category.getUsername());
        ps.setBoolean(3, category.isArchived());

        ps.executeUpdate();

        final UUID generatedId;
        try (ResultSet rs = ps.getGeneratedKeys()) {
          if (rs.next()) {
            generatedId = rs.getObject(1, UUID.class);
          } else {
            throw new SQLException("Failed to retrieve generated key");
          }
        }
        category.setId(generatedId);
      }
    } catch (SQLException e) {
      throw new RuntimeException("Failed to create spend", e);
    }
    return category;
  }

  @Override
  public Optional<CategoryEntity> findCategoryById(UUID id) {
    try (Connection connection = Databases.connection(CFG.spendJdbcUrl())) {
      try (PreparedStatement ps = connection.prepareStatement(
          "SELECT id, name, username, archived FROM category WHERE id = ?")) {
        ps.setObject(1, id);
        ps.execute();

        try (ResultSet rs = ps.executeQuery()) {
          if (rs.next()) {
            CategoryEntity ce = new CategoryEntity();
            ce.setId(rs.getObject("id", UUID.class));
            ce.setName(rs.getString("name"));
            ce.setUsername(rs.getString("username"));
            ce.setArchived(rs.getBoolean("archived"));
            return Optional.of(ce);
          }
        }
      }
    } catch (SQLException e) {
      throw new RuntimeException("Failed to find category by id", e);
    }
    return Optional.empty();
  }

  @Override
  public Optional<CategoryEntity> findCategoryByUserNameAndCategoryName(String userName, String categoryName) {
    try (Connection connection = Databases.connection(CFG.spendJdbcUrl())) {
      try (PreparedStatement ps = connection.prepareStatement(
          "SELECT id, name, username, archived FROM category WHERE username = ? AND name = ?")) {
        ps.setString(1, userName);
        ps.setString(2, categoryName);
        ps.execute();

        try (ResultSet rs = ps.executeQuery()) {
          if (rs.next()) {
            CategoryEntity ce = new CategoryEntity();
            ce.setId(rs.getObject("id", UUID.class));
            ce.setName(rs.getString("name"));
            ce.setUsername(rs.getString("username"));
            ce.setArchived(rs.getBoolean("archived"));
            return Optional.of(ce);
          }
        }
      }
    } catch (SQLException e) {
      throw new RuntimeException("Failed to find category by username and category name", e);
    }
    return Optional.empty();
  }

  @Override
  public List<CategoryEntity> findAllCategoriesByUserName(String userName) {
    try (Connection connection = Databases.connection(CFG.spendJdbcUrl())) {
      try (PreparedStatement ps = connection.prepareStatement(
          "SELECT id, name, username, archived FROM category WHERE username = ?")) {
        ps.setString(1, userName);
        ps.execute();

        try (ResultSet rs = ps.executeQuery()) {
          List<CategoryEntity> categories = new ArrayList<>();
          while (rs.next()) {
            CategoryEntity ce = new CategoryEntity();
            ce.setId(rs.getObject("id", UUID.class));
            ce.setName(rs.getString("name"));
            ce.setUsername(rs.getString("username"));
            ce.setArchived(rs.getBoolean("archived"));
            categories.add(ce);
          }
          return categories;
        }
      }
    } catch (SQLException e) {
      throw new RuntimeException("Failed to find all categories by username", e);
    }
  }

  @Override
  public void deleteCategory(CategoryEntity category) {
    try (Connection connection = Databases.connection(CFG.spendJdbcUrl())) {
      try (PreparedStatement ps = connection.prepareStatement(
          "DELETE FROM category WHERE id = ?")) {
        ps.setObject(1, category.getId());
        ps.executeUpdate();
      }
    } catch (SQLException e) {
      throw new RuntimeException("Failed to delete category", e);
    }
  }

  @Override
  public CategoryEntity updateCategory(CategoryEntity category) {
    try (Connection connection = Databases.connection(CFG.spendJdbcUrl())) {
      try (PreparedStatement ps = connection.prepareStatement(
          "UPDATE category SET name = ?, username = ?, archived = ? WHERE id = ?")) {
        ps.setString(1, category.getName());
        ps.setString(2, category.getUsername());
        ps.setBoolean(3, category.isArchived());
        ps.setObject(4, category.getId());

        ps.executeUpdate();
      }
    } catch (SQLException e) {
      throw new RuntimeException("Failed to update category", e);
    }
    return category;
  }
}