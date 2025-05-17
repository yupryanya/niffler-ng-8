package guru.qa.niffler.api.core;

import guru.qa.niffler.config.Config;
import io.qameta.allure.okhttp3.AllureOkHttp3;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;

public class RestClient {
  protected static final Config CFG = Config.getInstance();

  protected final OkHttpClient okHttpClient;
  protected final Retrofit retrofit;

  public RestClient(String baseUrl) {
    this(baseUrl, JacksonConverterFactory.create(), HttpLoggingInterceptor.Level.BODY);
  }

  public RestClient(String baseUrl, JacksonConverterFactory converterFactory, HttpLoggingInterceptor.Level logLevel) {
    this.okHttpClient = new OkHttpClient.Builder()
        .addNetworkInterceptor(new HttpLoggingInterceptor().setLevel(logLevel))
        .addNetworkInterceptor(
            new AllureOkHttp3()
                .setRequestTemplate("request.ftl")
                .setResponseTemplate("response.ftl")
        )
        .build();

    this.retrofit = new Retrofit.Builder()
        .baseUrl(baseUrl)
        .client(okHttpClient)
        .addConverterFactory(converterFactory)
        .build();
  }
}