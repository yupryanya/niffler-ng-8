package guru.qa.niffler.api;

import com.fasterxml.jackson.databind.JsonNode;
import retrofit2.Call;
import retrofit2.http.*;

public interface AuthApi {

  @GET("register")
  Call<Void> requestRegisterForm();

  @POST("register")
  @FormUrlEncoded
  Call<Void> register(
      @Field("username") String username,
      @Field("password") String password,
      @Field("passwordSubmit") String passwordSubmit,
      @Field("_csrf") String csrf);

  @GET("oauth2/authorize")
  Call<Void> preRequest(
      @Query("response_type") String responseType,
      @Query("client_id") String clientId,
      @Query("scope") String scope,
      @Query(value = "redirect_uri", encoded = true) String redirectUri,
      @Query("code_challenge") String codeChallenge,
      @Query("code_challenge_method") String codeChallengeMethod
  );

  @POST("login")
  @FormUrlEncoded
  Call<Void> login(
      @Field("_csrf") String csrf,
      @Field("username") String username,
      @Field("password") String password
  );

  @POST("oauth2/token")
  @FormUrlEncoded
  Call<JsonNode> token(
      @Field("code") String code,
      @Field(value = "redirect_uri", encoded = true) String redirectUri,
      @Field("code_verifier") String codeVerifier,
      @Field("grant_type") String grantType,
      @Field("client_id") String client
  );
}