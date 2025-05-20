package guru.qa.niffler.model.statistics;

import guru.qa.niffler.common.values.Color;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

@ToString
@Getter
@EqualsAndHashCode
@AllArgsConstructor
public class Bubble {
  Color color;
  String text;
}