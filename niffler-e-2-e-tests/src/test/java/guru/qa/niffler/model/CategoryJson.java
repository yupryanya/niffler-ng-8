package guru.qa.niffler.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.UUID;

import static guru.qa.niffler.utils.RandomDataUtils.newCategoryName;

public record CategoryJson(
    @JsonProperty("id")
    UUID id,
    @JsonProperty("name")
    String name,
    @JsonProperty("username")
    String username,
    @JsonProperty("archived")
    boolean archived) {
  public static CategoryJson generateRandomCategoryJsonWithUsername(String username) {
    return new CategoryJson(
        null,
        newCategoryName(),
        username,
        false
    );
  }
}