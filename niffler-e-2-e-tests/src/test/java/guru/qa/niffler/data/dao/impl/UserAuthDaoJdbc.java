package guru.qa.niffler.data.dao.impl;

import guru.qa.niffler.config.Config;
import guru.qa.niffler.data.dao.auth.UserAuthDao;
import guru.qa.niffler.data.entity.auth.AuthUserEntity;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static guru.qa.niffler.data.tpl.Connections.holder;

public class UserAuthDaoJdbc implements UserAuthDao {
  private static final Config CFG = Config.getInstance();

  @Override
  public AuthUserEntity createUserAuth(AuthUserEntity authUser) {
    try (PreparedStatement ps = holder(CFG.authJdbcUrl()).connection().prepareStatement(
        "INSERT INTO public.user (username, password, enabled, account_non_expired, account_non_locked, credentials_non_expired)" +
            "VALUES (?, ?, ?, ?, ?, ?)",
        PreparedStatement.RETURN_GENERATED_KEYS
    )) {
      ps.setString(1, authUser.getUsername());
      ps.setString(2, authUser.getPassword());
      ps.setBoolean(3, authUser.getEnabled());
      ps.setBoolean(4, authUser.getAccountNonExpired());
      ps.setBoolean(5, authUser.getAccountNonLocked());
      ps.setBoolean(6, authUser.getCredentialsNonExpired());

      ps.executeUpdate();

      final UUID generatedKey;
      try (ResultSet rs = ps.getGeneratedKeys()) {
        if (rs.next()) {
          generatedKey = rs.getObject("id", UUID.class);
        } else {
          throw new SQLException("Failed to retrieve generated key");
        }
      }
      authUser.setId(generatedKey);
      return authUser;
    } catch (SQLException e) {
      throw new RuntimeException("Failed to create authUser", e);
    }
  }

  @Override
  public void deleteAuthUser(AuthUserEntity authUser) {
    try (PreparedStatement ps = holder(CFG.authJdbcUrl()).connection().prepareStatement(
        "DELETE FROM user WHERE id = ?")) {
      ps.setObject(1, authUser.getId());
      ps.executeUpdate();
    } catch (SQLException e) {
      throw new RuntimeException("Failed to delete authUser", e);
    }
  }

  @Override
  public Optional<AuthUserEntity> findUserById(String id) {
    try (PreparedStatement ps = holder(CFG.authJdbcUrl()).connection().prepareStatement(
        "SELECT * FROM public.user WHERE id = ?")) {
      ps.setObject(1, id);
      ps.execute();

      try (ResultSet rs = ps.executeQuery()) {
        if (rs.next()) {
          AuthUserEntity ae = new AuthUserEntity();
          ae.setId(rs.getObject("id", UUID.class));
          ae.setUsername(rs.getString("username"));
          ae.setPassword(rs.getString("password"));
          ae.setEnabled(rs.getBoolean("enabled"));
          ae.setAccountNonExpired(rs.getBoolean("account_non_expired"));
          ae.setAccountNonLocked(rs.getBoolean("account_non_locked"));
          ae.setCredentialsNonExpired(rs.getBoolean("credentials_non_expired"));
          return Optional.of(ae);
        }
      }
      return Optional.empty();
    } catch (SQLException e) {
      throw new RuntimeException("Failed to find spend by id", e);
    }
  }

  @Override
  public List<AuthUserEntity> findAll() {
    try (PreparedStatement ps = holder(CFG.authJdbcUrl()).connection().prepareStatement(
        "SELECT * FROM public.user")) {
      ps.execute();

      try (ResultSet rs = ps.executeQuery()) {
        List<AuthUserEntity> authUsers = new ArrayList<>();
        while (rs.next()) {
          AuthUserEntity ae = new AuthUserEntity();
          ae.setId(rs.getObject("id", UUID.class));
          ae.setUsername(rs.getString("username"));
          ae.setPassword(rs.getString("password"));
          ae.setEnabled(rs.getBoolean("enabled"));
          ae.setAccountNonExpired(rs.getBoolean("account_non_expired"));
          ae.setAccountNonLocked(rs.getBoolean("account_non_locked"));
          ae.setCredentialsNonExpired(rs.getBoolean("credentials_non_expired"));
          authUsers.add(ae);
        }
        return authUsers;
      }
    } catch (SQLException e) {
      throw new RuntimeException("Failed to find all auth users", e);
    }
  }
}