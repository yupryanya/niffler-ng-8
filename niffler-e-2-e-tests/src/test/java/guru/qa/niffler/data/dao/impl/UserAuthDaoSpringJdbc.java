package guru.qa.niffler.data.dao.impl;

import guru.qa.niffler.config.Config;
import guru.qa.niffler.data.dao.auth.UserAuthDao;
import guru.qa.niffler.data.entity.auth.AuthUserEntity;
import guru.qa.niffler.data.mapper.AuthUserEntityRowMapper;
import guru.qa.niffler.data.tpl.DataSources;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;

import javax.sql.DataSource;
import java.sql.PreparedStatement;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class UserAuthDaoSpringJdbc implements UserAuthDao {
  private static final Config CFG = Config.getInstance();

  private final JdbcTemplate jdbcTemplate;

  public UserAuthDaoSpringJdbc() {
    this.jdbcTemplate = new JdbcTemplate(DataSources.dataSource(CFG.authJdbcUrl()));  }

  @Override
  public AuthUserEntity createUserAuth(AuthUserEntity authUser) {
    var keyHolder = new GeneratedKeyHolder();
    jdbcTemplate.update(connection -> {
      var ps = connection.prepareStatement(
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
    authUser.setId((UUID) keyHolder.getKeys().get("id"));
    return authUser;
  }

  @Override
  public void deleteAuthUser(AuthUserEntity authUser) {
    jdbcTemplate.update("DELETE FROM public.user WHERE id = ?", authUser.getId());
  }

  @Override
  public Optional<AuthUserEntity> findUserById(String id) {
    return Optional.ofNullable(
        jdbcTemplate.queryForObject(
            "SELECT * FROM public.user WHERE id = ?",
            AuthUserEntityRowMapper.instance,
            id
        )
    );
  }

  @Override
  public List<AuthUserEntity> findAll() {
    return jdbcTemplate.query(
        "SELECT * FROM public.user",
        AuthUserEntityRowMapper.instance
    );
  }
}