package guru.qa.niffler.data.mapper;

import guru.qa.niffler.common.values.AuthorityType;
import guru.qa.niffler.data.entity.auth.AuthAuthorityEntity;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

public class AuthAuthorityEntityRowMapper implements RowMapper<AuthAuthorityEntity> {
  public static final AuthAuthorityEntityRowMapper instance = new AuthAuthorityEntityRowMapper();

  private AuthAuthorityEntityRowMapper() {
  }

  @Override
  public AuthAuthorityEntity mapRow(ResultSet rs, int rowNum) throws SQLException {
    AuthAuthorityEntity result = new AuthAuthorityEntity();
    result.setId(rs.getObject("id", UUID.class));
    result.setUser(null);
    result.setAuthority(AuthorityType.valueOf(rs.getString("authority")));
    return result;
  }
}