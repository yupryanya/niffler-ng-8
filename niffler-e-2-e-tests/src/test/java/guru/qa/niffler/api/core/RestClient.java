package guru.qa.niffler.api.core;

import guru.qa.niffler.config.Config;
import io.qameta.allure.okhttp3.AllureOkHttp3;
import lombok.Getter;
import okhttp3.Interceptor;
import okhttp3.JavaNetCookieJar;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import org.apache.commons.lang.ArrayUtils;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;

import java.net.CookieManager;
import java.net.CookiePolicy;

import static okhttp3.logging.HttpLoggingInterceptor.Level;
import static okhttp3.logging.HttpLoggingInterceptor.Level.BODY;
import static retrofit2.Converter.Factory;

public class RestClient {
  protected static final Config CFG = Config.getInstance();

  private final OkHttpClient okHttpClient;
  @Getter
  protected final Retrofit retrofit;

  public RestClient(String baseUrl) {
    this(baseUrl, false, JacksonConverterFactory.create(), BODY);
  }

  public RestClient(String baseUrl, boolean followRedirect) {
    this(baseUrl, followRedirect, JacksonConverterFactory.create(), BODY);
  }

  public RestClient(String baseUrl,
                    boolean followRedirect,
                    Factory converterFactory,
                    Level loggingLevel,
                    Interceptor... interceptors) {

    final OkHttpClient.Builder builder = new OkHttpClient.Builder()
        .followRedirects(followRedirect);

    if (ArrayUtils.isNotEmpty(interceptors)) {
      for (Interceptor interceptor : interceptors) {
        builder.addNetworkInterceptor(interceptor);
      }
    }
    builder.addNetworkInterceptor(new AllureOkHttp3()
        .setRequestTemplate("request.ftl")
        .setResponseTemplate("response.ftl")
    );
    builder.addNetworkInterceptor(new HttpLoggingInterceptor()
        .setLevel(loggingLevel));
    builder.cookieJar(new JavaNetCookieJar(
        new CookieManager(
            ThreadSafeCookieStore.INSTANCE,
            CookiePolicy.ACCEPT_ALL
        )
    ));

    this.okHttpClient = builder.build();
    this.retrofit = new Retrofit.Builder()
        .baseUrl(baseUrl)
        .client(okHttpClient)
        .addConverterFactory(converterFactory)
        .build();
  }


  public class RestClientFactory {
    public static RestClient create(String baseUrl) {
      return new RestClient(baseUrl, false, JacksonConverterFactory.create(), BODY);
    }

    public static Retrofit retrofit(String baseUrl) {
      return create(baseUrl).getRetrofit();
    }
  }
}