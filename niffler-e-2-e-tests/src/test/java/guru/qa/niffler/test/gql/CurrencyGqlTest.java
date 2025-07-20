package guru.qa.niffler.test.gql;

import com.apollographql.apollo.api.ApolloResponse;
import com.apollographql.java.client.ApolloCall;
import com.apollographql.java.rx2.Rx2Apollo;
import guru.qa.CurrenciesQuery;
import guru.qa.niffler.common.values.CurrencyValues;
import guru.qa.niffler.jupiter.annotation.ApiLogin;
import guru.qa.niffler.jupiter.annotation.Token;
import guru.qa.niffler.jupiter.annotation.User;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

public class CurrencyGqlTest extends BaseGqlTest {
  @User
  @ApiLogin
  @Test
  void allCurrenciesShouldBeReturnedFromCategory(@Token String token) {
    ApolloCall<CurrenciesQuery.Data> currenciesCall = apolloClient
        .query(new CurrenciesQuery())
        .addHttpHeader("authorization", token);
    ApolloResponse<CurrenciesQuery.Data> dataApolloResponse = Rx2Apollo.single(currenciesCall).blockingGet();
    final CurrenciesQuery.Data data = dataApolloResponse.dataOrThrow();
    List<CurrenciesQuery.Currency> all = data.currencies;

    List<String> actualCurrencies = all.stream()
        .map(currency -> currency.currency.rawValue)
        .collect(Collectors.toList());

    List<String> expectedCurrencies = Arrays.stream(CurrencyValues.values())
        .map(Enum::name)
        .collect(Collectors.toList());

    assertThat(actualCurrencies).containsExactlyInAnyOrderElementsOf(expectedCurrencies);
  }
}