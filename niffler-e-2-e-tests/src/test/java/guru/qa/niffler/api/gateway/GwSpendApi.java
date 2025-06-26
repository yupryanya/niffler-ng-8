package guru.qa.niffler.api.gateway;

import com.fasterxml.jackson.databind.JsonNode;
import guru.qa.niffler.model.CategoryJson;
import guru.qa.niffler.model.SpendJson;
import retrofit2.Call;
import retrofit2.http.*;

import javax.annotation.Nonnull;
import java.util.List;

public interface GwSpendApi {
  @GET("api/categories/all")
  Call<List<CategoryJson>> allCategories(@Header("Authorization") String bearerToken,
                                         @Query("excludeArchived") boolean excludeArchived);

  @POST("api/categories/add")
  Call<CategoryJson> addCategory(@Header("Authorization") String bearerToken,
                                 @Body CategoryJson category);

  @GET("api/currencies/all")
  Call<List<JsonNode>> allCurrencies(@Header("Authorization") String bearerToken);

  @POST("api/spends/add")
  Call<SpendJson> addSpend(@Header("Authorization") String bearerToken,
                           @Body SpendJson spend);

  @PATCH("api/spends/edit")
  Call<SpendJson> editSpend(@Header("Authorization") String bearerToken,
                            @Body SpendJson spend);

  @DELETE("api/spends/remove")
  Call<Void> removeSpends(@Header("Authorization") String bearerToken,
                          @Query("ids") @Nonnull List<String> ids);
}