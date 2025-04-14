package guru.qa.niffler.data.entity.auth;

import guru.qa.niffler.model.UserJson;
import lombok.Getter;
import lombok.Setter;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.UUID;

@Getter
@Setter
public class UserAuthEntity {
  private UUID id;
  private String username;
  private String password;
  private Boolean enabled;
  private Boolean accountNonExpired;
  private Boolean accountNonLocked;
  private Boolean credentialsNonExpired;

  private static final PasswordEncoder pe = PasswordEncoderFactories.createDelegatingPasswordEncoder();

  public static UserAuthEntity fromJson(UserJson user) {
    UserAuthEntity entity = new UserAuthEntity();
    entity.setUsername(user.username());
    entity.setPassword(pe.encode(user.password()));
    entity.setEnabled(true);
    entity.setAccountNonExpired(true);
    entity.setAccountNonLocked(true);
    entity.setCredentialsNonExpired(true);
    return entity;
  }
}
