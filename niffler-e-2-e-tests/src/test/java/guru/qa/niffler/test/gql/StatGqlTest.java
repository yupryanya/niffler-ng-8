package guru.qa.niffler.test.gql;

import com.apollographql.apollo.api.ApolloResponse;
import com.apollographql.java.client.ApolloCall;
import com.apollographql.java.rx2.Rx2Apollo;
import guru.qa.StatQuery;
import guru.qa.niffler.jupiter.annotation.ApiLogin;
import guru.qa.niffler.jupiter.annotation.Token;
import guru.qa.niffler.jupiter.annotation.User;
import org.junit.jupiter.api.Test;

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
}
