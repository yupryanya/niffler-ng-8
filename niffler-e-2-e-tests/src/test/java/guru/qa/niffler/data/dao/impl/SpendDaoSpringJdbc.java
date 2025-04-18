package guru.qa.niffler.data.dao.impl;

import guru.qa.niffler.data.dao.spend.SpendDao;
import guru.qa.niffler.data.entity.spend.SpendEntity;
import guru.qa.niffler.data.mapper.SpendEntityRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;

import javax.sql.DataSource;
import java.sql.PreparedStatement;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class SpendDaoSpringJdbc implements SpendDao {
  private final JdbcTemplate jdbcTemplate;

  public SpendDaoSpringJdbc(DataSource datasource) {
    this.jdbcTemplate = new JdbcTemplate(datasource);
  }

  @Override
  public SpendEntity createSpend(SpendEntity spend) {
    KeyHolder kh = new GeneratedKeyHolder();
    jdbcTemplate.update(connection -> {
          PreparedStatement ps = connection.prepareStatement(
              "INSERT INTO spend (username, spend_date, currency, amount, description, category_id) VALUES (?, ?, ?, ?, ?, ?)",
              PreparedStatement.RETURN_GENERATED_KEYS
          );
          ps.setString(1, spend.getUsername());
          ps.setObject(2, spend.getSpendDate());
          ps.setString(3, spend.getCurrency().name());
          ps.setDouble(4, spend.getAmount());
          ps.setString(5, spend.getDescription());
          ps.setObject(6, spend.getCategoryId());
          return ps;
        },
        kh
    );
    final UUID generatedKey = (UUID) kh.getKeys().get("id");
    spend.setId(generatedKey);
    return spend;
  }

  @Override
  public Optional<SpendEntity> findSpendById(UUID id) {
    return Optional.ofNullable(
        jdbcTemplate.queryForObject(
            "SELECT * FROM spend WHERE id = ?",
            SpendEntityRowMapper.instance,
            id
        )
    );
  }

  @Override
  public List<SpendEntity> findAllSpendsByUserName(String userName) {
    return jdbcTemplate.query(
        "SELECT * FROM spend WHERE username = ?",
        SpendEntityRowMapper.instance,
        userName
    );
  }

  @Override
  public void deleteSpend(SpendEntity spend) {
    jdbcTemplate.update(
        connection -> {
          PreparedStatement ps = connection.prepareStatement(
              "DELETE FROM spend WHERE id = ?"
          );
          ps.setObject(1, spend.getId());
          return ps;
        }
    );
  }

  @Override
  public List<SpendEntity> findAll() {
    return jdbcTemplate.query(
        "SELECT * FROM spend",
        SpendEntityRowMapper.instance
    );
  }
}