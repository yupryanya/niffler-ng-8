package guru.qa.niffler.api.gatewayv2;

import guru.qa.niffler.model.UserJson;
import guru.qa.niffler.model.pageable.RestResponsePage;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Query;

import javax.annotation.Nullable;

public interface GwV2UserApi {
  @GET("api/v2/users/all")
  Call<RestResponsePage<UserJson>> allUsers(@Header("Authorization") String bearerToken,
                                            @Query("page") int page,
                                            @Query("size") int size,
                                            @Query("sort") String sort,
                                            @Nullable @Query("searchQuery") String searchQuery);

  @GET("api/v2/friends/all")
  Call<RestResponsePage<UserJson>> allFriends(@Header("Authorization") String bearerToken,
                                              @Query("page") int page,
                                              @Query("size") int size,
                                              @Query("sort") String sort,
                                              @Nullable @Query("searchQuery") String searchQuery);

}
