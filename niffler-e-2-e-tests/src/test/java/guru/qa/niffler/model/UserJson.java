package guru.qa.niffler.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import guru.qa.niffler.common.values.CurrencyValues;

import java.util.ArrayList;
import java.util.UUID;

import static guru.qa.niffler.utils.RandomDataUtils.newValidPassword;
import static guru.qa.niffler.utils.RandomDataUtils.nonExistentUserName;

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
  public static UserJson generateRandomUserJson() {
    return new UserJson(
        null,
        nonExistentUserName(),
        null,
        CurrencyValues.RUB,
        null,
        null,
        null,
        new TestData(
            newValidPassword(),
            null,
            null,
            null,
            null,
            null
        )
    );
  }

  public static UserJson generateUserJson(String username, String password) {
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

  public UserJson withPassword(String password) {
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

  public UserJson withTestData(TestData testData) {
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

  public UserJson withEmptyTestData() {
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
