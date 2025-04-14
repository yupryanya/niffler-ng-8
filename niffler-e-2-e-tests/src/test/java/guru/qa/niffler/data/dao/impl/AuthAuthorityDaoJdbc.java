package guru.qa.niffler.data.dao.impl;

import guru.qa.niffler.data.dao.auth.AuthAuthorityDao;
import guru.qa.niffler.data.entity.auth.AuthAuthorityEntity;

import java.sql.*;
import java.util.UUID;

public class AuthAuthorityDaoJdbc implements AuthAuthorityDao {
  private final Connection connection;

  public AuthAuthorityDaoJdbc(Connection connection) {
    this.connection = connection;
  }

  @Override
  public AuthAuthorityEntity createAuthAuthority(AuthAuthorityEntity authAuthority) {
    try (PreparedStatement ps = connection.prepareStatement(
        "INSERT INTO authority (user_id, authority) VALUES (?, ?)",
        Statement.RETURN_GENERATED_KEYS)) {
      ps.setObject(1, authAuthority.getUserId());
      ps.setString(2, authAuthority.getAuthority());

      ps.executeUpdate();

      final UUID generatedId;
      try (ResultSet rs = ps.getGeneratedKeys()) {
        if (rs.next()) {
          generatedId = rs.getObject(1, UUID.class);
        } else {
          throw new SQLException("Failed to retrieve generated key");
        }
      }
      authAuthority.setId(generatedId);
      return authAuthority;
    } catch (SQLException e) {
      throw new RuntimeException("Failed to create AuthAuthority", e);
    }
  }

  @Override
  public void deleteAuthAuthority(AuthAuthorityEntity authAuthority) {
    try (PreparedStatement ps = connection.prepareStatement(
        "DELETE FROM authority WHERE id = ?")) {
      ps.setObject(1, authAuthority.getId());
      ps.executeUpdate();
    } catch (SQLException e) {
      throw new RuntimeException("Failed to delete AuthAuthority", e);
    }
  }
}
