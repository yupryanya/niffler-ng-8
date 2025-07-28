package guru.qa.niffler.test.grpc;

import com.google.protobuf.Empty;
import guru.qa.niffler.grpc.*;
import io.grpc.StatusRuntimeException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.*;

public class CurrencyGrpcTest extends BaseGrpcTest {

  @Test
  void shouldReturnAllSupportedCurrencies() {
    CurrencyResponse response = blockingStub.getAllCurrencies(Empty.getDefaultInstance());
    List<CurrencyValues> actualCurrencies = response.getAllCurrenciesList().stream()
        .map(Currency::getCurrency)
        .collect(Collectors.toList());

    assertThat(actualCurrencies)
        .doesNotContain(CurrencyValues.UNSPECIFIED)
        .containsExactlyInAnyOrder(CurrencyValues.RUB, CurrencyValues.USD, CurrencyValues.EUR, CurrencyValues.KZT);
  }

  @ParameterizedTest
  @MethodSource("providerForCurrencyConversions")
  void shouldCorrectlyConvertCurrencies(CurrencyValues spendCurrency, CurrencyValues desiredCurrency, double amount) {
    Map<CurrencyValues, Double> rates = fetchCurrencyRates();
    double expected = amount * rates.get(spendCurrency) / rates.get(desiredCurrency);

    CalculateRequest request = CalculateRequest.newBuilder()
        .setSpendCurrency(spendCurrency)
        .setDesiredCurrency(desiredCurrency)
        .setAmount(amount)
        .build();

    CalculateResponse response = blockingStub.calculateRate(request);

    assertThat(response.getCalculatedAmount())
        .isCloseTo(expected, within(0.01));
  }

  @Test
  void shouldReturnSameAmountWhenSpendAndDesiredCurrenciesMatch() {
    double amount = 123.45;

    CalculateRequest request = CalculateRequest.newBuilder()
        .setSpendCurrency(CurrencyValues.KZT)
        .setDesiredCurrency(CurrencyValues.KZT)
        .setAmount(amount)
        .build();

    CalculateResponse response = blockingStub.calculateRate(request);

    assertThat(response.getCalculatedAmount())
        .isEqualTo(amount);
  }

  @ParameterizedTest
  @MethodSource("providerForUnspecifiedCurrencies")
  void shouldThrowWhenCurrencyIsUnspecified(CurrencyValues spendCurrency, CurrencyValues desiredCurrency) {
    CalculateRequest request = CalculateRequest.newBuilder()
        .setSpendCurrency(spendCurrency)
        .setDesiredCurrency(desiredCurrency)
        .setAmount(100.0)
        .build();

    assertThatThrownBy(() -> blockingStub.calculateRate(request))
        .isInstanceOf(StatusRuntimeException.class)
        .hasMessageContaining("Application error processing RPC");
  }

  @Test
  void shouldThrowWhenAmountIsNegative() {
    CalculateRequest request = CalculateRequest.newBuilder()
        .setSpendCurrency(CurrencyValues.USD)
        .setDesiredCurrency(CurrencyValues.EUR)
        .setAmount(-10.0)
        .build();

    assertThatThrownBy(() -> blockingStub.calculateRate(request))
        .isInstanceOf(StatusRuntimeException.class);
  }

  @Test
  void shouldReturnZeroWhenAmountIsZero() {
    CalculateRequest request = CalculateRequest.newBuilder()
        .setSpendCurrency(CurrencyValues.EUR)
        .setDesiredCurrency(CurrencyValues.USD)
        .setAmount(0.0)
        .build();

    CalculateResponse response = blockingStub.calculateRate(request);

    assertThat(response.getCalculatedAmount()).isEqualTo(0.0);
  }

  private Map<CurrencyValues, Double> fetchCurrencyRates() {
    CurrencyResponse response = blockingStub.getAllCurrencies(Empty.getDefaultInstance());
    return response.getAllCurrenciesList().stream()
        .collect(Collectors.toMap(Currency::getCurrency, Currency::getCurrencyRate));
  }

  static Stream<Arguments> providerForUnspecifiedCurrencies() {
    return Stream.of(
        Arguments.of(CurrencyValues.UNSPECIFIED, CurrencyValues.USD),
        Arguments.of(CurrencyValues.RUB, CurrencyValues.UNSPECIFIED)
    );
  }

  static Stream<Arguments> providerForCurrencyConversions() {
    return Stream.of(
        Arguments.of(CurrencyValues.RUB, CurrencyValues.USD, 1000.0),
        Arguments.of(CurrencyValues.USD, CurrencyValues.EUR, 50.0),
        Arguments.of(CurrencyValues.KZT, CurrencyValues.RUB, 5000.0),
        Arguments.of(CurrencyValues.EUR, CurrencyValues.KZT, 200.0)
    );
  }
}