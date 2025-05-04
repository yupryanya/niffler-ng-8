package guru.qa.niffler.data.repository.impl.auth;

import guru.qa.niffler.common.values.AuthorityType;
import guru.qa.niffler.config.Config;
import guru.qa.niffler.data.entity.auth.AuthAuthorityEntity;
import guru.qa.niffler.data.entity.auth.AuthUserEntity;
import guru.qa.niffler.data.mapper.AuthUserEntityRowMapper;
import guru.qa.niffler.data.repository.AuthUserRepository;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static guru.qa.niffler.data.tpl.Connections.holder;

public class AuthUserRepositoryJdbc implements AuthUserRepository {
  private static final Config CFG = Config.getInstance();

  @Override
  public AuthUserEntity create(AuthUserEntity user) {
    try (PreparedStatement userPs = holder(CFG.authJdbcUrl()).connection().prepareStatement(
        "INSERT INTO public.user (username, password, enabled, account_non_expired, account_non_locked, credentials_non_expired)" +
            "VALUES (?, ?, ?, ?, ?, ?)", PreparedStatement.RETURN_GENERATED_KEYS);
         PreparedStatement authorityPs = holder(CFG.authJdbcUrl()).connection().prepareStatement(
             "INSERT INTO authority (user_id, authority) VALUES (?, ?)"
         )) {
      userPs.setString(1, user.getUsername());
      userPs.setString(2, user.getPassword());
      userPs.setBoolean(3, user.getEnabled());
      userPs.setBoolean(4, user.getAccountNonExpired());
      userPs.setBoolean(5, user.getAccountNonLocked());
      userPs.setBoolean(6, user.getCredentialsNonExpired());

      userPs.executeUpdate();

      final UUID generatedKey;
      try (ResultSet rs = userPs.getGeneratedKeys()) {
        if (rs.next()) {
          generatedKey = rs.getObject("id", UUID.class);
        } else {
          throw new SQLException("Failed to retrieve generated key");
        }
      }

      for (AuthAuthorityEntity ae : user.getAuthorities()) {
        authorityPs.setObject(1, generatedKey);
        authorityPs.setString(2, ae.getAuthority().name());
        authorityPs.addBatch();
      }

      authorityPs.executeBatch();

      user.setId(generatedKey);
      return user;
    } catch (SQLException e) {
      throw new RuntimeException("Failed to create authUser", e);
    }
  }

  @Override
  public AuthUserEntity update(AuthUserEntity authUser) {
    try (PreparedStatement updateUserPs = holder(CFG.authJdbcUrl()).connection().prepareStatement(
        "UPDATE public.user SET username = ?, password = ?, enabled = ?, account_non_expired = ?, " +
        "account_non_locked = ?, credentials_non_expired = ? WHERE id = ?");
         PreparedStatement deleteAuthoritiesPs = holder(CFG.authJdbcUrl()).connection().prepareStatement(
             "DELETE FROM authority WHERE user_id = ?");
         PreparedStatement insertAuthoritiesPs = holder(CFG.authJdbcUrl()).connection().prepareStatement(
             "INSERT INTO authority (user_id, authority) VALUES (?, ?)")
    ) {
      updateUserPs.setString(1, authUser.getUsername());
      updateUserPs.setString(2, authUser.getPassword());
      updateUserPs.setBoolean(3, authUser.getEnabled());
      updateUserPs.setBoolean(4, authUser.getAccountNonExpired());
      updateUserPs.setBoolean(5, authUser.getAccountNonLocked());
      updateUserPs.setBoolean(6, authUser.getCredentialsNonExpired());
      updateUserPs.setObject(7, authUser.getId());
      updateUserPs.executeUpdate();

      deleteAuthoritiesPs.setObject(1, authUser.getId());
      deleteAuthoritiesPs.executeUpdate();

      for (AuthAuthorityEntity ae : authUser.getAuthorities()) {
        insertAuthoritiesPs.setObject(1, authUser.getId());
        insertAuthoritiesPs.setString(2, ae.getAuthority().name());
        insertAuthoritiesPs.addBatch();
      }
      insertAuthoritiesPs.executeBatch();

      return authUser;
    } catch (SQLException e) {
      throw new RuntimeException("Failed to update authUser", e);
    }
  }

  @Override
  public Optional<AuthUserEntity> findById(String id) {
    try (PreparedStatement ps = holder(CFG.authJdbcUrl()).connection().prepareStatement(
        "SELECT * FROM public.user u JOIN authority a ON u.id = a.user_id WHERE id = ?")) {
      ps.setObject(1, id);
      ps.execute();
      List<AuthAuthorityEntity> authAuthorities = new ArrayList<>();
      AuthUserEntity user = null;
      try (ResultSet rs = ps.executeQuery()) {
        while (rs.next()) {
          if (user == null) {
            user = AuthUserEntityRowMapper.instance.mapRow(rs, 1);
          }
          AuthAuthorityEntity ae = new AuthAuthorityEntity();
          ae.setUser(user);
          ae.setId(rs.getObject("a.id", UUID.class));
          ae.setAuthority(AuthorityType.valueOf(rs.getString("authority")));
          authAuthorities.add(ae);
        }
      }
      if (user == null) {
        return Optional.empty();
      } else {
        user.setAuthorities(authAuthorities);
        return Optional.of(user);
      }
    } catch (SQLException e) {
      throw new RuntimeException("Failed to find user by id", e);
    }
  }

  @Override
  public Optional<AuthUserEntity> findByUsername(String username) {
    try (PreparedStatement ps = holder(CFG.authJdbcUrl()).connection().prepareStatement(
        "SELECT * FROM public.user u JOIN authority a ON u.id = a.user_id WHERE username = ?")) {
      ps.setString(1, username);
      ps.execute();
      List<AuthAuthorityEntity> authAuthorities = new ArrayList<>();
      AuthUserEntity user = null;
      try (ResultSet rs = ps.executeQuery()) {
        while (rs.next()) {
          if (user == null) {
            user = AuthUserEntityRowMapper.instance.mapRow(rs, 1);
          }
          AuthAuthorityEntity ae = new AuthAuthorityEntity();
          ae.setUser(user);
          ae.setId(rs.getObject("a.id", UUID.class));
          ae.setAuthority(AuthorityType.valueOf(rs.getString("authority")));
          authAuthorities.add(ae);
        }
      }
      if (user == null) {
        return Optional.empty();
      } else {
        user.setAuthorities(authAuthorities);
        return Optional.of(user);
      }
    } catch (SQLException e) {
      throw new RuntimeException("Failed to find user by username " + username, e);
    }
  }

  @Override
  public void remove(AuthUserEntity authUser) {
    try (PreparedStatement deleteAuthoritiesPs = holder(CFG.authJdbcUrl()).connection().prepareStatement(
        "DELETE FROM authority WHERE user_id = ?");
         PreparedStatement deleteUserPs = holder(CFG.authJdbcUrl()).connection().prepareStatement(
             "DELETE FROM public.user WHERE id = ?")) {
      deleteAuthoritiesPs.setObject(1, authUser.getId());
      deleteAuthoritiesPs.execute();

      deleteUserPs.setObject(1, authUser.getId());
      deleteUserPs.execute();
    } catch (SQLException e) {
      throw new RuntimeException("Failed to remove user", e);
    }
  }
}