package guru.qa.niffler.common.values;

import lombok.Getter;

  @Getter
  public enum CurrencyValues {
    RUB("₽"),
    USD("$"),
    EUR("€"),
    KZT("₸");

    private final String symbol;

    CurrencyValues(String symbol) {
      this.symbol = symbol;
    }
  }