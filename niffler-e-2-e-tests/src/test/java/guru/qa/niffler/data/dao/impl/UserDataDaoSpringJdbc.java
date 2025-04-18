package guru.qa.niffler.data.dao.impl;

import guru.qa.niffler.data.dao.user.UserDataDao;
import guru.qa.niffler.data.entity.user.UserDataEntity;
import guru.qa.niffler.data.mapper.UserDataEntityRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;

import javax.sql.DataSource;
import java.sql.PreparedStatement;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class UserDataDaoSpringJdbc implements UserDataDao {
  private final JdbcTemplate jdbcTemplate;

  public UserDataDaoSpringJdbc(DataSource datasource) {
    this.jdbcTemplate = new JdbcTemplate(datasource);
  }

  @Override
  public UserDataEntity createUser(UserDataEntity user) {
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
    return Optional.empty();
  }

  @Override
  public void deleteUser(UserDataEntity user) {

  }

  @Override
  public List<UserDataEntity> findAll() {
    return jdbcTemplate.query(
        "SELECT * FROM public.user",
        UserDataEntityRowMapper.instance
    );
  }
}
