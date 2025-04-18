package guru.qa.niffler.data.entity.auth;

import guru.qa.niffler.common.values.AuthorityType;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;
import java.util.UUID;

@Getter
@Setter
@ToString
public class AuthAuthorityEntity implements Serializable {
  private UUID id;
  private UUID userId;
  private AuthorityType authority;
}
