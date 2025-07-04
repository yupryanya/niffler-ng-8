package guru.qa.niffler.data.dao.impl;

import guru.qa.niffler.config.Config;
import guru.qa.niffler.data.dao.spend.CategoryDao;
import guru.qa.niffler.data.entity.spend.CategoryEntity;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static guru.qa.niffler.data.tpl.Connections.holder;

@ParametersAreNonnullByDefault
public class CategoryDaoJdbc implements CategoryDao {
  private static final Config CFG = Config.getInstance();

  @Override
  public @Nonnull CategoryEntity createCategory(CategoryEntity category) {
    try (PreparedStatement ps = holder(CFG.spendJdbcUrl()).connection().prepareStatement(
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
      return category;
    } catch (SQLException e) {
      throw new RuntimeException("Failed to create spend", e);
    }
  }

  @Override
  public Optional<CategoryEntity> findCategoryById(UUID id) {
    try (PreparedStatement ps = holder(CFG.spendJdbcUrl()).connection().prepareStatement(
        "SELECT * FROM category WHERE id = ?")) {
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
      return Optional.empty();
    } catch (SQLException e) {
      throw new RuntimeException("Failed to find category by id", e);
    }
  }

  @Override
  public Optional<CategoryEntity> findCategoryByUserNameAndCategoryName(String userName, String categoryName) {
    try (PreparedStatement ps = holder(CFG.spendJdbcUrl()).connection().prepareStatement(
        "SELECT * FROM category WHERE username = ? AND name = ?")) {
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
      return Optional.empty();
    } catch (SQLException e) {
      throw new RuntimeException("Failed to find category by username and category name", e);
    }
  }

  @Override
  public @Nonnull List<CategoryEntity> findAllCategoriesByUserName(String userName) {
    try (PreparedStatement ps = holder(CFG.spendJdbcUrl()).connection().prepareStatement(
        "SELECT id, * FROM category WHERE username = ?")) {
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
    } catch (SQLException e) {
      throw new RuntimeException("Failed to find all categories by username", e);
    }
  }

  @Override
  public void deleteCategory(CategoryEntity category) {
    try (PreparedStatement ps = holder(CFG.spendJdbcUrl()).connection().prepareStatement(
        "DELETE FROM category WHERE id = ?")) {
      ps.setObject(1, category.getId());
      ps.executeUpdate();
    } catch (SQLException e) {
      throw new RuntimeException("Failed to delete category", e);
    }
  }

  @Override
  public @Nonnull CategoryEntity updateCategory(CategoryEntity category) {
    try (PreparedStatement ps = holder(CFG.spendJdbcUrl()).connection().prepareStatement(
        "UPDATE category SET name = ?, username = ?, archived = ? WHERE id = ?")) {
      ps.setString(1, category.getName());
      ps.setString(2, category.getUsername());
      ps.setBoolean(3, category.isArchived());
      ps.setObject(4, category.getId());

      ps.executeUpdate();
      return category;
    } catch (SQLException e) {
      throw new RuntimeException("Failed to update category", e);
    }
  }

  @Override
  public @Nonnull List<CategoryEntity> findAll() {
    try (PreparedStatement ps = holder(CFG.spendJdbcUrl()).connection().prepareStatement(
        "SELECT * FROM category")) {
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
    } catch (SQLException e) {
      throw new RuntimeException("Failed to find all categories", e);
    }
  }
}