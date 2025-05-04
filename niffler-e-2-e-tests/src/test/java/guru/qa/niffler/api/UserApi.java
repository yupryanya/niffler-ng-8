package guru.qa.niffler.api;

import guru.qa.niffler.model.UserJson;
import retrofit2.Call;
import retrofit2.http.*;

import java.util.List;

public interface UserApi {
  @GET("internal/users/current")
  Call<UserJson> getUser(
      @Query("username") String username
  );

  @POST("internal/users/update")
  Call<UserJson> updateUserInfo(
      @Body UserJson user
  );

  @GET("internal/users/all")
  Call<List<UserJson>> allUsers(
      @Query("username") String username,
      @Query("searchQuery") String searchQuery
  );

  @GET("internal/v2/users/all")
  Call<UserJson> allUsersPaged(
      @Query("username") String username,
      @Query("searchQuery") String searchQuery,
      @Query("page") int page,
      @Query("size") int size,
      @Query("sort") String sort
  );

  @GET("internal/friends/all")
  Call<List<UserJson>> friends(
      @Query("username") String username,
      @Query("searchQuery") String searchQuery
  );

  @GET("internal/v2/friends/all")
  Call<UserJson> friendsPaged(
      @Query("username") String username,
      @Query("searchQuery") String searchQuery,
      @Query("page") int page,
      @Query("size") int size,
      @Query("sort") String sort
  );

  @POST("internal/invitations/send")
  Call<UserJson> sendInvitation(
      @Query("username") String username,
      @Query("targetUsername") String targetUsername
  );

  @POST("internal/invitations/accept")
  Call<UserJson> acceptInvitation(
      @Query("username") String username,
      @Query("targetUsername") String targetUsername
  );

  @POST("internal/invitations/decline")
  Call<UserJson> declineInvitation(
      @Query("username") String username,
      @Query("targetUsername") String targetUsername
  );

  @DELETE("internal/friends/remove")
  Call<Void> removeFriend(
      @Query("username") String username,
      @Query("targetUsername") String targetUsername
  );
}