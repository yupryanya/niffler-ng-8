package guru.qa.niffler.data.dao.impl;

import guru.qa.niffler.config.Config;
import guru.qa.niffler.data.dao.spend.SpendDao;
import guru.qa.niffler.data.entity.spend.SpendEntity;
import guru.qa.niffler.data.mapper.SpendEntityRowMapper;
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
public class SpendDaoSpringJdbc implements SpendDao {
  private static final Config CFG = Config.getInstance();

  @Override
  public @Nonnull SpendEntity createSpend(SpendEntity spend) {
    JdbcTemplate jdbcTemplate = new JdbcTemplate(DataSources.dataSource(CFG.spendJdbcUrl()));
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
          ps.setObject(6, spend.getCategory());
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
    JdbcTemplate jdbcTemplate = new JdbcTemplate(DataSources.dataSource(CFG.spendJdbcUrl()));
    return Optional.ofNullable(
        jdbcTemplate.queryForObject(
            "SELECT * FROM spend WHERE id = ?",
            SpendEntityRowMapper.instance,
            id
        )
    );
  }

  @Override
  public @Nonnull List<SpendEntity> findAllSpendsByUserName(String userName) {
    JdbcTemplate jdbcTemplate = new JdbcTemplate(DataSources.dataSource(CFG.spendJdbcUrl()));
    return jdbcTemplate.query(
        "SELECT * FROM spend WHERE username = ?",
        SpendEntityRowMapper.instance,
        userName
    );
  }

  @Override
  public void deleteSpend(SpendEntity spend) {
    JdbcTemplate jdbcTemplate = new JdbcTemplate(DataSources.dataSource(CFG.spendJdbcUrl()));
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
  public @Nonnull List<SpendEntity> findAll() {
    JdbcTemplate jdbcTemplate = new JdbcTemplate(DataSources.dataSource(CFG.spendJdbcUrl()));
    return jdbcTemplate.query(
        "SELECT * FROM spend",
        SpendEntityRowMapper.instance
    );
  }
}