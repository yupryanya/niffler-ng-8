package guru.qa.niffler.model.statistics;

import guru.qa.niffler.common.values.Color;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

@ToString
@Getter
@EqualsAndHashCode
public class Bubble {
  Color color;
  String text;

  public Bubble(Color color, String text) {
    this.text = text;
    this.color = color;
  }
}