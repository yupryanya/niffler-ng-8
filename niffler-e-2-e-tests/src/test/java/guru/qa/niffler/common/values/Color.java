package guru.qa.niffler.common.values;

import lombok.AllArgsConstructor;

import java.util.Arrays;
import java.util.Optional;

@AllArgsConstructor
public enum Color {
  yellow("rgba(255, 183, 3, 1)"),
  green("rgba(53, 173, 123, 1)");

  public final String rgb;

  public static Optional<Color> fromRgb(String rgb) {
    return Arrays.stream(values())
        .filter(color -> color.rgb.equals(rgb))
        .findFirst();
  }
}