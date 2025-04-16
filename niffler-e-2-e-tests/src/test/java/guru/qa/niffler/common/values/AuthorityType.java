package guru.qa.niffler.common.values;

import lombok.Getter;

@Getter
public enum AuthorityType {
  READ("read"),
  WRITE("write");

  private final String value;

  AuthorityType(String value) {
    this.value = value;
  }
}
