package guru.qa.niffler.test.gql;

import com.apollographql.apollo.api.ApolloResponse;
import com.apollographql.apollo.api.Error;
import com.apollographql.java.client.ApolloCall;
import com.apollographql.java.rx2.Rx2Apollo;
import guru.qa.FriendCategoriesQuery;
import guru.qa.UserQuery;
import guru.qa.niffler.jupiter.annotation.ApiLogin;
import guru.qa.niffler.jupiter.annotation.Token;
import guru.qa.niffler.jupiter.annotation.User;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

public class UserGqlTest extends BaseGqlTest {

  @User(friends = 1)
  @ApiLogin
  @Test
  void anotherUserCategoriesQueryShouldReturnError1(@Token String token) {
    final ApolloCall<FriendCategoriesQuery.Data> allPeopleCall = apolloClient
        .query(FriendCategoriesQuery.builder()
            .page(0)
            .size(1)
            .build())
        .addHttpHeader("authorization", token);

    final ApolloResponse<FriendCategoriesQuery.Data> response = Rx2Apollo.single(allPeopleCall).blockingGet();
    final List<Error> errors = Optional.ofNullable(response.errors).orElseGet(Collections::emptyList);

    assertThat(errors.getFirst().getMessage()).isEqualTo("Can`t query categories for another user");
  }

  @User
  @ApiLogin
  @Test
  void userQueryOverMaxDepthShouldReturnErrorMessageText(@Token String token) {
    System.out.println(token);
    final ApolloCall<UserQuery.Data> allPeopleCall = apolloClient.query(UserQuery.builder()
            .page(0)
            .size(10)
            .build())
        .addHttpHeader("authorization", token);

    final ApolloResponse<UserQuery.Data> response = Rx2Apollo.single(allPeopleCall).blockingGet();
    final List<Error> errors = Optional.ofNullable(response.errors).orElseGet(Collections::emptyList);

   assertThat(errors.getFirst().getMessage()).isEqualTo("Can`t fetch over 2 friends sub-queries");
  }
}
