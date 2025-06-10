package guru.qa.niffler.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import guru.qa.niffler.common.values.CurrencyValues;

import java.util.Date;
import java.util.UUID;

import static guru.qa.niffler.utils.RandomDataUtils.newDescription;
import static guru.qa.niffler.utils.RandomDataUtils.randomAmount;

public record SpendJson(
    @JsonProperty("id")
    UUID id,
    @JsonProperty("spendDate")
    Date spendDate,
    @JsonProperty("category")
    CategoryJson category,
    @JsonProperty("currency")
    CurrencyValues currency,
    @JsonProperty("amount")
    Double amount,
    @JsonProperty("description")
    String description,
    @JsonProperty("username")
    String username) {

  public static SpendJson randomSpendWithUsername(String username) {
    return new SpendJson(
        null,
        new Date(),
        CategoryJson.generateRandomCategoryJsonWithUsername(username),
        CurrencyValues.RUB,
        randomAmount(),
        newDescription(),
        username
    );
  }

  public SpendJson withSpendDate(Date newDate) {
    return new SpendJson(
        this.id,
        newDate,
        this.category,
        this.currency,
        this.amount,
        this.description,
        this.username
    );
  }

  public SpendJson withDescription(String newDescription) {
    return new SpendJson(
        this.id,
        this.spendDate,
        this.category,
        this.currency,
        this.amount,
        newDescription,
        this.username
    );
  }
}
