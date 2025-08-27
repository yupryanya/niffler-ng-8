package guru.qa.niffler.api;

import guru.qa.jaxb.userdata.*;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface UserSoapApi {

  @Headers(value = {
      "Content-type: text/xml",
      "Accept-Charset: utf-8",
  })
  @POST("ws")
  Call<UserResponse> currentUser(
      @Body CurrentUserRequest currentUserRequest
  );

  @Headers(value = {
      "Content-type: text/xml",
      "Accept-Charset: utf-8",
  })
  @POST("ws")
  Call<UsersResponse> allUsers(
      @Body AllUsersRequest allUsersRequest
  );

  @Headers(value = {
      "Content-type: text/xml",
      "Accept-Charset: utf-8",
  })
  @POST("ws")
  Call<UsersResponse> allFriends(
      @Body FriendsRequest friendsRequest
  );

  @Headers(value = {
      "Content-type: text/xml",
      "Accept-Charset: utf-8",
  })
  @POST("ws")
  Call<UsersResponse> allFriends(
      @Body FriendsPageRequest friendsRequest
  );

  @Headers(value = {
      "Content-type: text/xml",
      "Accept-Charset: utf-8",
  })
  @POST("ws")
  Call<Void> removeFriend(
      @Body RemoveFriendRequest removeFriendRequest
  );

  @Headers(value = {
      "Content-type: text/xml",
      "Accept-Charset: utf-8",
  })
  @POST("ws")
  Call<UserResponse> declineInvitation(
      @Body DeclineInvitationRequest declineInvitationRequest
  );

  @Headers(value = {
      "Content-type: text/xml",
      "Accept-Charset: utf-8",
  })
  @POST("ws")
  Call<UserResponse> acceptInvitation(
      @Body AcceptInvitationRequest acceptInvitationRequest
  );

  @Headers(value = {
      "Content-type: text/xml",
      "Accept-Charset: utf-8",
  })
  @POST("ws")
  Call<UserResponse> sendInvitation(
      @Body SendInvitationRequest sendInvitationRequest
  );
}