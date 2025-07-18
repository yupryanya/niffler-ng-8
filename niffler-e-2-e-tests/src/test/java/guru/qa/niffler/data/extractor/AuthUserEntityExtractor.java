package guru.qa.niffler.data.extractor;

import guru.qa.niffler.common.values.AuthorityType;
import guru.qa.niffler.data.entity.auth.AuthAuthorityEntity;
import guru.qa.niffler.data.entity.auth.AuthUserEntity;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class AuthUserEntityExtractor implements ResultSetExtractor<AuthUserEntity> {
  public static final AuthUserEntityExtractor instance = new AuthUserEntityExtractor();

  private AuthUserEntityExtractor() {
  }

  @Override
  public @Nullable AuthUserEntity extractData(@Nonnull ResultSet rs) throws SQLException, DataAccessException {
    Map<UUID, AuthUserEntity> authUsers = new ConcurrentHashMap<>();
    UUID userId = null;
    while (rs.next()) {
      userId = rs.getObject("user_id", UUID.class);
      AuthUserEntity user = authUsers.computeIfAbsent(userId, id -> {
            AuthUserEntity result = new AuthUserEntity();
            try {
              result.setId(rs.getObject("user_id", UUID.class));
              result.setUsername(rs.getString("username"));
              result.setPassword(rs.getString("password"));
              result.setEnabled(rs.getBoolean("enabled"));
              result.setAccountNonExpired(rs.getBoolean("account_non_expired"));
              result.setAccountNonLocked(rs.getBoolean("account_non_locked"));
              result.setCredentialsNonExpired(rs.getBoolean("credentials_non_expired"));
            } catch (SQLException e) {
              throw new RuntimeException("Error extracting AuthUserEntity from ResultSet", e);
            }
            return result;
          }
      );

      AuthAuthorityEntity authority = new AuthAuthorityEntity();
      authority.setId(rs.getObject("authority_id", UUID.class));
      authority.setAuthority(AuthorityType.valueOf(rs.getString("authority")));
      authority.setUser(user);
      user.getAuthorities().add(authority);
    }
    return authUsers.get(userId);
  }
}