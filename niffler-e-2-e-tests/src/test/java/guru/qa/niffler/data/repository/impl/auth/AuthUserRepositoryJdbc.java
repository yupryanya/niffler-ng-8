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
      throw new RuntimeException("Failed to find spend by id", e);
    }
  }
}