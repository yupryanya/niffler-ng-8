package guru.qa.niffler.api.gateway;

import com.fasterxml.jackson.databind.JsonNode;
import guru.qa.niffler.model.UserJson;
import retrofit2.Call;
import retrofit2.http.*;

import javax.annotation.Nullable;
import java.util.List;

public interface GwUserApi {
  @GET("api/session/current")
  Call<JsonNode> currentSession(@Header("Authorization") String bearerToken);

  @GET("api/users/current")
  Call<UserJson> currentUser(@Header("Authorization") String bearerToken);

  @GET("api/users/all")
  Call<List<UserJson>> allUsers(@Header("Authorization") String bearerToken,
                                @Query("searchQuery") @Nullable String searchQuery);

  @POST("api/users/update")
  Call<UserJson> updateUser(@Header("Authorization") String bearerToken,
                            @Body UserJson user);

  @GET("api/friends/all")
  Call<List<UserJson>> allFriends(@Header("Authorization") String bearerToken,
                                  @Query("searchQuery") @Nullable String searchQuery);

  @DELETE("api/friends/remove")
  Call<Void> removeFriend(@Header("Authorization") String bearerToken,
                          @Query("username") @Nullable String targetUsername);

  @POST("api/invitations/send")
  Call<UserJson> sendInvitation(@Header("Authorization") String bearerToken,
                                @Body UserJson friend);

  @POST("api/invitations/accept")
  Call<UserJson> acceptInvitation(@Header("Authorization") String bearerToken,
                                  @Body UserJson friend);

  @POST("api/invitations/decline")
  Call<UserJson> declineInvitation(@Header("Authorization") String bearerToken,
                                   @Body UserJson friend);
}
