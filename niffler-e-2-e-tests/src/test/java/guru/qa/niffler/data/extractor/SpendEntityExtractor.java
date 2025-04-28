package guru.qa.niffler.data.extractor;

import guru.qa.niffler.common.values.CurrencyValues;
import guru.qa.niffler.data.entity.spend.CategoryEntity;
import guru.qa.niffler.data.entity.spend.SpendEntity;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;

import javax.annotation.Nullable;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

public class SpendEntityExtractor implements ResultSetExtractor<SpendEntity> {

  public static final SpendEntityExtractor instance = new SpendEntityExtractor();

  private SpendEntityExtractor() {
  }

  @Nullable
  @Override
  public SpendEntity extractData(ResultSet rs) throws SQLException, DataAccessException {
    SpendEntity result = new SpendEntity();
    UUID spendId = rs.getObject("id", UUID.class);
    if (spendId != null) {
      result.setId(spendId);
      result.setUsername(rs.getString("username"));
      result.setSpendDate(rs.getDate("spend_date"));
      result.setCurrency(CurrencyValues.valueOf(rs.getString("currency")));
      result.setAmount(rs.getDouble("amount"));
      result.setDescription(rs.getString("description"));

      CategoryEntity category = new CategoryEntity();
      category.setId(rs.getObject("category_id", UUID.class));
      category.setUsername(rs.getString("username"));
      category.setName(rs.getString("category_name"));
      category.setArchived(rs.getBoolean("archived"));

      result.setCategory(category);
      return result;
    } else {
      return null;
    }
  }
}