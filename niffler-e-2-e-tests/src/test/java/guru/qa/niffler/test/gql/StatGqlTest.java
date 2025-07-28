package guru.qa.niffler.test.gql;

import com.apollographql.apollo.api.ApolloResponse;
import com.apollographql.java.client.ApolloCall;
import com.apollographql.java.rx2.Rx2Apollo;
import guru.qa.StatQuery;
import guru.qa.niffler.common.values.CurrencyValues;
import guru.qa.niffler.jupiter.annotation.*;
import org.junit.jupiter.api.Test;

import static guru.qa.type.CurrencyValues.EUR;
import static guru.qa.type.CurrencyValues.USD;
import static org.assertj.core.api.Assertions.assertThat;

public class StatGqlTest extends BaseGqlTest {
  @User
  @ApiLogin
  @Test
  void newUserStatShouldBeEmpty(@Token String token) {
    ApolloCall<StatQuery.Data> statCall = apolloClient
        .query(StatQuery.builder()
            .statCurrency(null)
            .filterCurrency(null)
            .build())
        .addHttpHeader("authorization", token);

    ApolloResponse<StatQuery.Data> dataApolloResponse = Rx2Apollo.single(statCall).blockingGet();
    final StatQuery.Data data = dataApolloResponse.dataOrThrow();
    StatQuery.Stat result = data.stat;

    assertThat(result.total).isEqualTo(0.0);
  }

  @User(
      spends = {
          @Spend(category = "Grocery", description = "Bread", amount = 100.00, currency = CurrencyValues.RUB),
          @Spend(category = "DIY", description = "Hammer", amount = 30, currency = CurrencyValues.USD),
          @Spend(category = "Clothes", description = "Jeans", amount = 100, currency = CurrencyValues.USD),
          @Spend(category = "Footwear", description = "Sneakers", amount = 99.10, currency = CurrencyValues.EUR)
      }
  )
  @ApiLogin
  @Test
  void shouldReturnTotalForUsdSpendsWhenMultipleCurrenciesPresent(@Token String token) {
    ApolloCall<StatQuery.Data> statCall = apolloClient
        .query(StatQuery.builder()
            .filterCurrency(USD)
            .statCurrency(USD)
            .build())
        .addHttpHeader("authorization", token);

    ApolloResponse<StatQuery.Data> dataApolloResponse = Rx2Apollo.single(statCall).blockingGet();
    final StatQuery.Data data = dataApolloResponse.dataOrThrow();
    StatQuery.Stat result = data.stat;

    assertThat(result.total).isEqualTo(130.0);
  }

  @User(
      spends = {
          @Spend(category = "Grocery", description = "Bread", amount = 100.00, currency = CurrencyValues.RUB),
          @Spend(category = "DIY", description = "Hammer", amount = 30, currency = CurrencyValues.USD),
          @Spend(category = "Clothes", description = "Jeans", amount = 100, currency = CurrencyValues.USD),
          @Spend(category = "Footwear", description = "Sneakers", amount = 99.10, currency = CurrencyValues.EUR)
      }
  )
  @ApiLogin
  @Test
  void shouldReturnTotalForEurSpendsWhenMultipleCurrenciesPresent(@Token String token) {
    ApolloCall<StatQuery.Data> statCall = apolloClient
        .query(StatQuery.builder()
            .filterCurrency(EUR)
            .statCurrency(EUR)
            .build())
        .addHttpHeader("authorization", token);

    ApolloResponse<StatQuery.Data> dataApolloResponse = Rx2Apollo.single(statCall).blockingGet();
    final StatQuery.Data data = dataApolloResponse.dataOrThrow();
    StatQuery.Stat result = data.stat;

    assertThat(result.total).isEqualTo(99.10);
  }

  @User(
      spends = {
          @Spend(category = "Grocery", description = "Bread", amount = 100.00),
          @Spend(category = "Footwear", description = "Sneakers", amount = 99.10),
          @Spend(category = "DIY", description = "Hammer", amount = 599.10)
      },
      categories = {
          @Category(name = "DIY", archived = true),
          @Category(name = "Footwear", archived = true)
      }
  )
  @ApiLogin
  @Test
  void shouldReturnTotalForArchivedSpends(@Token String token) {
    ApolloCall<StatQuery.Data> statCall = apolloClient
        .query(StatQuery.builder()
            .filterCurrency(null)
            .statCurrency(null)
            .build())
        .addHttpHeader("authorization", token);

    ApolloResponse<StatQuery.Data> dataApolloResponse = Rx2Apollo.single(statCall).blockingGet();
    final StatQuery.Data data = dataApolloResponse.dataOrThrow();
    StatQuery.Stat result = data.stat;

    assertThat(result.statByCategories)
        .hasSize(2)
        .anySatisfy(cat -> {
          assertThat(cat.categoryName).isEqualTo("Archived");
          assertThat(cat.sum).isEqualTo(698.2);
        });
  }
}
