package guru.qa.niffler.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import guru.qa.niffler.common.values.CurrencyValues;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.UUID;

public record UserJson(
    @JsonProperty("id")
    UUID id,
    @JsonProperty("username")
    String username,
    @JsonProperty("fullname")
    String fullname,
    @JsonProperty("currency")
    CurrencyValues currency,
    @JsonProperty("photo")
    String photo,
    @JsonProperty("photoSmall")
    String photoSmall,
    @JsonProperty("friendshipStatus")
    String friendshipStatus,
    @JsonIgnore
    TestData testData
) {
  public @Nonnull
  static UserJson generateUserJson(String username, String password) {
    return new UserJson(
        null,
        username,
        null,
        CurrencyValues.RUB,
        null,
        null,
        null,
        new TestData(
            password,
            null,
            null,
            null,
            null,
            null
        )
    );
  }

  public @Nonnull UserJson withPassword(String password) {
    return new UserJson(
        this.id,
        this.username,
        this.fullname,
        this.currency,
        this.photo,
        this.photoSmall,
        this.friendshipStatus,
        new TestData(
            password,
            this.testData.categories(),
            this.testData.spends(),
            this.testData.friends(),
            this.testData.outcomeInvitations(),
            this.testData.incomeInvitations()
        )
    );
  }

  public @Nonnull UserJson withTestData(TestData testData) {
    return new UserJson(
        this.id,
        this.username,
        this.fullname,
        this.currency,
        this.photo,
        this.photoSmall,
        this.friendshipStatus,
        testData
    );
  }

  public @Nonnull UserJson withEmptyTestData() {
    return new UserJson(
        this.id,
        this.username,
        this.fullname,
        this.currency,
        this.photo,
        this.photoSmall,
        this.friendshipStatus,
        new TestData(
            null,
            new ArrayList<>(),
            new ArrayList<>(),
            new ArrayList<>(),
            new ArrayList<>(),
            new ArrayList<>()
        )
    );
  }
}
