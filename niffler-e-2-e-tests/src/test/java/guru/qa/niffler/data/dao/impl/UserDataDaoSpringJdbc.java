package guru.qa.niffler.data.dao.impl;

import guru.qa.niffler.config.Config;
import guru.qa.niffler.data.dao.user.UserDataDao;
import guru.qa.niffler.data.entity.user.UserDataEntity;
import guru.qa.niffler.data.mapper.UserDataEntityRowMapper;
import guru.qa.niffler.data.tpl.DataSources;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import java.sql.PreparedStatement;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@ParametersAreNonnullByDefault
public class UserDataDaoSpringJdbc implements UserDataDao {
  private static final Config CFG = Config.getInstance();

  private final JdbcTemplate jdbcTemplate;

  public UserDataDaoSpringJdbc() {
    this.jdbcTemplate = new JdbcTemplate(DataSources.dataSource(CFG.userdataJdbcUrl()));
  }

  @Override
  public @Nonnull UserDataEntity createUser(UserDataEntity user) {
    KeyHolder kh = new GeneratedKeyHolder();
    jdbcTemplate.update(connection -> {
          PreparedStatement ps = connection.prepareStatement(
              "INSERT INTO public.user (username, currency, firstname, surname, photo, photo_small, full_name)" +
              "VALUES (?, ?, ?, ?, ?, ?, ?)",
              PreparedStatement.RETURN_GENERATED_KEYS
          );
          ps.setString(1, user.getUsername());
          ps.setString(2, user.getCurrency().name());
          ps.setString(3, user.getFirstname());
          ps.setString(4, user.getSurname());
          ps.setBytes(5, user.getPhoto());
          ps.setBytes(6, user.getPhotoSmall());
          ps.setString(7, user.getFullname());
          return ps;
        },
        kh
    );
    final UUID generatedKey = (UUID) kh.getKeys().get("id");
    user.setId(generatedKey);
    return user;
  }

  @Override
  public Optional<UserDataEntity> findUserById(UUID id) {
    return Optional.ofNullable(
        jdbcTemplate.queryForObject(
            "SELECT * FROM public.user WHERE id = ?",
            UserDataEntityRowMapper.instance,
            id
        )
    );
  }

  @Override
  public Optional<UserDataEntity> findUserByUsername(String username) {
    return Optional.ofNullable(
        jdbcTemplate.queryForObject(
            "SELECT * FROM public.user WHERE username = ?",
            UserDataEntityRowMapper.instance,
            username
        )
    );
  }

  @Override
  public void deleteUser(UserDataEntity user) {
    jdbcTemplate.update(
        "DELETE FROM public.user WHERE id = ?",
        user.getId()
    );
  }

  @Override
  public @Nonnull List<UserDataEntity> findAll() {
    return jdbcTemplate.query(
        "SELECT * FROM public.user",
        UserDataEntityRowMapper.instance
    );
  }
}