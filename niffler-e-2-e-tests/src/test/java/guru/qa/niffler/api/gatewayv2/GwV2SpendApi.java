package guru.qa.niffler.api.gatewayv2;

import com.fasterxml.jackson.databind.JsonNode;
import guru.qa.niffler.common.values.CurrencyValues;
import guru.qa.niffler.common.values.DataFilterValues;
import guru.qa.niffler.model.UserJson;
import guru.qa.niffler.model.pageable.RestResponsePage;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Query;

import javax.annotation.Nullable;

public interface GwV2SpendApi {
  @GET("api/v2/spends/all")
  Call<RestResponsePage<UserJson>> allSpends(@Header("Authorization") String bearerToken,
                                             @Query("page") int page,
                                             @Query("filterCurrency") String filterCurrency,
                                             @Query("filterPeriod") DataFilterValues filterPeriod,
                                             @Nullable @Query("searchQuery") String searchQuery);

  @GET("api/v2/stat/total")
  Call<RestResponsePage<JsonNode>> totalStat(@Header("Authorization") String bearerToken,
                                             @Query("statCurrency") CurrencyValues statCurrency,
                                             @Nullable @Query("filterCurrency") CurrencyValues filterCurrency,
                                             @Nullable @Query("filterPeriod") DataFilterValues filterPeriod);
}