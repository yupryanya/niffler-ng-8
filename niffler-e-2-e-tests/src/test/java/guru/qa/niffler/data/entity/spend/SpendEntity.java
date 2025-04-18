package guru.qa.niffler.data.entity.spend;

import guru.qa.niffler.common.values.CurrencyValues;
import guru.qa.niffler.model.SpendJson;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;
import java.util.Date;
import java.util.UUID;

@Getter
@Setter
@ToString
public class SpendEntity implements Serializable {
  private UUID id;
  private String username;
  private CurrencyValues currency;
  private Date spendDate;
  private Double amount;
  private String description;
  private UUID categoryId;

  public static SpendEntity fromJson(SpendJson json) {
    SpendEntity entity = new SpendEntity();
    entity.setId(json.id());
    entity.setUsername(json.username());
    entity.setCurrency(json.currency());
    entity.setSpendDate(new java.sql.Date(json.spendDate().getTime()));
    entity.setAmount(json.amount());
    entity.setDescription(json.description());
    entity.setCategoryId(json.category().id());
    return entity;
  }
}