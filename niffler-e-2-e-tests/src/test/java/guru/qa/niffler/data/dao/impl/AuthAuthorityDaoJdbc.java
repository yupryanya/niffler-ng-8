package guru.qa.niffler.data.dao.impl;

import guru.qa.niffler.common.values.AuthorityType;
import guru.qa.niffler.data.dao.auth.AuthAuthorityDao;
import guru.qa.niffler.data.entity.auth.AuthAuthorityEntity;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class AuthAuthorityDaoJdbc implements AuthAuthorityDao {
  private final Connection connection;

  public AuthAuthorityDaoJdbc(Connection connection) {
    this.connection = connection;
  }

  @Override
  public void createAuthAuthority(AuthAuthorityEntity authAuthority) {
    try (PreparedStatement ps = connection.prepareStatement(
        "INSERT INTO authority (user_id, authority) VALUES (?, ?)")) {
      ps.setObject(1, authAuthority.getUserId());
      ps.setString(2, authAuthority.getAuthority().name());

      ps.executeUpdate();
    } catch (SQLException e) {
      throw new RuntimeException("Failed to create AuthAuthority", e);
    }
  }

  @Override
  public void createAuthAuthorities(AuthAuthorityEntity... authAuthority) {

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

  @Override
  public List<AuthAuthorityEntity> findAll() {
    try (PreparedStatement ps = connection.prepareStatement(
        "SELECT * FROM authority")) {
      ps.execute();

      try (var rs = ps.executeQuery()) {
        List<AuthAuthorityEntity> authAuthorities = new ArrayList<>();
        while (rs.next()) {
          AuthAuthorityEntity ae = new AuthAuthorityEntity();
          ae.setId(rs.getObject("id", UUID.class));
          ae.setUserId(rs.getObject("user_id", UUID.class));
          ae.setAuthority(AuthorityType.valueOf(rs.getString("authority")));
          authAuthorities.add(ae);
        }
        return authAuthorities;
      }
    } catch (SQLException e) {
      throw new RuntimeException("Failed to find all AuthAuthorities", e);
    }
  }
}