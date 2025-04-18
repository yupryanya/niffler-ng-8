package guru.qa.niffler.data.dao.impl;

import guru.qa.niffler.data.dao.auth.AuthAuthorityDao;
import guru.qa.niffler.data.entity.auth.AuthAuthorityEntity;
import guru.qa.niffler.data.mapper.AuthAuthorityEntityRowMapper;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

public class AuthAuthorityDaoSpringJdbc implements AuthAuthorityDao {
  private final JdbcTemplate jdbcTemplate;

  public AuthAuthorityDaoSpringJdbc(DataSource dataSource) {
    this.jdbcTemplate = new JdbcTemplate(dataSource);
  }

  @Override
  public void createAuthAuthority(AuthAuthorityEntity authAuthority) {
    jdbcTemplate.update(
        connection -> {
          PreparedStatement ps = connection.prepareStatement(
              "INSERT INTO authority (user_id, authority) VALUES (?, ?)"
          );
          ps.setObject(1, authAuthority.getUserId());
          ps.setString(2, authAuthority.getAuthority().name());
          return ps;
        }
    );
  }

  @Override
  public void createAuthAuthorities(AuthAuthorityEntity... authAuthority) {
    jdbcTemplate.batchUpdate(
        "INSERT INTO authority (user_id, authority) VALUES (?, ?)",
        new BatchPreparedStatementSetter() {
          @Override
          public void setValues(PreparedStatement ps, int i) throws SQLException {
            ps.setObject(1, authAuthority[i].getUserId());
            ps.setString(2, authAuthority[i].getAuthority().name());
          }

          @Override
          public int getBatchSize() {
            return authAuthority.length;
          }
        }
    );
  }

  @Override
  public void deleteAuthAuthority(AuthAuthorityEntity authAuthority) {
    jdbcTemplate.update(
        connection -> {
          PreparedStatement ps = connection.prepareStatement(
              "DELETE FROM authority WHERE id = ?"
          );
          ps.setObject(1, authAuthority.getId());
          return ps;
        }
    );
  }

  @Override
  public List<AuthAuthorityEntity> findAll() {
    return jdbcTemplate.query(
        "SELECT * FROM authority",
        AuthAuthorityEntityRowMapper.instance
    );
  }
}