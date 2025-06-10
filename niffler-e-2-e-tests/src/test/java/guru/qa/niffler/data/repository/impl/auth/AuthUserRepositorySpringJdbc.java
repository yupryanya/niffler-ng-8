package guru.qa.niffler.data.repository.impl.auth;

import guru.qa.niffler.config.Config;
import guru.qa.niffler.data.entity.auth.AuthUserEntity;
import guru.qa.niffler.data.extractor.AuthUserEntityExtractor;
import guru.qa.niffler.data.repository.AuthUserRepository;
import guru.qa.niffler.data.tpl.DataSources;
import io.qameta.allure.Step;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@ParametersAreNonnullByDefault
public class AuthUserRepositorySpringJdbc implements AuthUserRepository {
  private static final Config CFG = Config.getInstance();

  private final JdbcTemplate jdbcTemplate;

  public AuthUserRepositorySpringJdbc() {
    this.jdbcTemplate = new JdbcTemplate(DataSources.dataSource(CFG.authJdbcUrl()));
  }

  @Step("Create auth user with Spring JDBC")
  @Override
  public @Nonnull AuthUserEntity create(AuthUserEntity authUser) {
    KeyHolder keyHolder = new GeneratedKeyHolder();

    jdbcTemplate.update(connection -> {
      PreparedStatement ps = connection.prepareStatement(
          "INSERT INTO public.user (username, password, enabled, account_non_expired, account_non_locked, credentials_non_expired)" +
          "VALUES (?, ?, ?, ?, ?, ?)",
          PreparedStatement.RETURN_GENERATED_KEYS
      );
      ps.setString(1, authUser.getUsername());
      ps.setString(2, authUser.getPassword());
      ps.setBoolean(3, authUser.getEnabled());
      ps.setBoolean(4, authUser.getAccountNonExpired());
      ps.setBoolean(5, authUser.getAccountNonLocked());
      ps.setBoolean(6, authUser.getCredentialsNonExpired());
      return ps;
    }, keyHolder);

    Map<String, Object> keys = keyHolder.getKeys();
    if (keys == null || keys.get("id") == null) {
      throw new IllegalStateException("Failed to retrieve generated key");
    }

    final UUID generatedKey = (UUID) keys.get("id");
    authUser.setId(generatedKey);

    jdbcTemplate.batchUpdate(
        "INSERT INTO authority (user_id, authority) VALUES (?, ?)",
        new BatchPreparedStatementSetter() {
          @Override
          public void setValues(PreparedStatement ps, int i) throws SQLException {
            ps.setObject(1, authUser.getAuthorities().get(i).getUser().getId());
            ps.setString(2, authUser.getAuthorities().get(i).getAuthority().name());
          }

          @Override
          public int getBatchSize() {
            return authUser.getAuthorities().size();
          }
        }
    );
    return authUser;
  }

  @Step("Update auth user with Spring JDBC")
  @Override
  public @Nonnull AuthUserEntity update(AuthUserEntity authUser) {
    jdbcTemplate.update(
        """
            UPDATE public.user 
            SET username = ?, password = ?, enabled = ?, account_non_expired = ?, 
                account_non_locked = ?, credentials_non_expired = ? 
            WHERE id = ?
            """,
        authUser.getUsername(),
        authUser.getPassword(),
        authUser.getEnabled(),
        authUser.getAccountNonExpired(),
        authUser.getAccountNonLocked(),
        authUser.getCredentialsNonExpired(),
        authUser.getId()
    );

    jdbcTemplate.update(
        "DELETE FROM authority WHERE user_id = ?",
        authUser.getId()
    );

    jdbcTemplate.batchUpdate(
        "INSERT INTO authority (user_id, authority) VALUES (?, ?)",
        new BatchPreparedStatementSetter() {
          @Override
          public void setValues(PreparedStatement ps, int i) throws SQLException {
            ps.setObject(1, authUser.getId());
            ps.setString(2, authUser.getAuthorities().get(i).getAuthority().name());
          }

          @Override
          public int getBatchSize() {
            return authUser.getAuthorities().size();
          }
        }
    );

    return authUser;
  }

  @Step("Find auth user by ID with Spring JDBC")
  @Override
  public Optional<AuthUserEntity> findById(String id) {
    return Optional.ofNullable(
        jdbcTemplate.query(
            """
                   SELECT a.id AS authority_id, a.authority, a.user_id,
                   u.username, u.password, u.enabled, u.account_non_expired, u.account_non_locked, u.credentials_non_expired
                   FROM public.user u JOIN authority a ON u.id = a.user_id
                   WHERE u.id = ?
                """,
            AuthUserEntityExtractor.instance,
            id
        )
    );
  }

  @Step("Find auth user by username with Spring JDBC")
  @Override
  public Optional<AuthUserEntity> findByUsername(String username) {
    return Optional.ofNullable(
        jdbcTemplate.query(
            """
                   SELECT a.id AS authority_id, a.authority, a.user_id,
                   u.username, u.password, u.enabled, u.account_non_expired, u.account_non_locked, u.credentials_non_expired
                   FROM public.user u JOIN authority a ON u.id = a.user_id
                   WHERE u.username = ?
                """,
            AuthUserEntityExtractor.instance,
            username
        )
    );
  }

  @Step("Remove auth user with Spring JDBC")
  @Override
  public void remove(AuthUserEntity authUser) {
    jdbcTemplate.update(
        "DELETE FROM authority WHERE user_id = ?",
        authUser.getId()
    );
    jdbcTemplate.update(
        "DELETE FROM public.user WHERE id = ?",
        authUser.getId()
    );
  }
}
