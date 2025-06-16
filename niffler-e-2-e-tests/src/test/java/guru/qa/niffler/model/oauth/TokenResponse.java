package guru.qa.niffler.model.oauth;

import com.fasterxml.jackson.annotation.JsonProperty;

public record TokenResponse(
    @JsonProperty("access_token")
    String accessToken,
    @JsonProperty("expires_in")
    int expiresIn,
    @JsonProperty("id_token")
    String idToken,
    @JsonProperty("scope")
    String scope,
    @JsonProperty("token_type")
    String tokenType
) {
}