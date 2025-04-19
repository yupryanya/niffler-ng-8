package guru.qa.niffler.data.dao.impl;

import guru.qa.niffler.common.values.AuthorityType;
import guru.qa.niffler.config.Config;
import guru.qa.niffler.data.dao.auth.AuthAuthorityDao;
import guru.qa.niffler.data.entity.auth.AuthAuthorityEntity;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static guru.qa.niffler.data.tpl.Connections.holder;

public class AuthAuthorityDaoJdbc implements AuthAuthorityDao {
  private static final Config CFG = Config.getInstance();

  @Override
  public void createAuthAuthorities(AuthAuthorityEntity... authAuthority) {
    try (PreparedStatement ps = holder(CFG.authJdbcUrl()).connection().prepareStatement(
        "INSERT INTO authority (user_id, authority) VALUES (?, ?)")) {
      for (AuthAuthorityEntity ae : authAuthority) {
        ps.setObject(1, ae.getUserId());
        ps.setString(2, ae.getAuthority().name());
        ps.addBatch();
      }
      ps.executeBatch();
    } catch (SQLException e) {
      throw new RuntimeException("Failed to create AuthAuthorities", e);
    }
  }

  @Override
  public void deleteAuthAuthority(AuthAuthorityEntity authAuthority) {
    try (PreparedStatement ps = holder(CFG.authJdbcUrl()).connection().prepareStatement(
        "DELETE FROM authority WHERE id = ?")) {
      ps.setObject(1, authAuthority.getId());
      ps.executeUpdate();
    } catch (SQLException e) {
      throw new RuntimeException("Failed to delete AuthAuthority", e);
    }
  }

  @Override
  public List<AuthAuthorityEntity> findAll() {
    try (PreparedStatement ps = holder(CFG.authJdbcUrl()).connection().prepareStatement(
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