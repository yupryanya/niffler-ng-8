package guru.qa.niffler.data.entity.auth;

import guru.qa.niffler.model.UserJson;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.UUID;

@Getter
@Setter
@ToString
public class AuthUserEntity {
  private UUID id;
  private String username;
  private String password;
  private Boolean enabled;
  private Boolean accountNonExpired;
  private Boolean accountNonLocked;
  private Boolean credentialsNonExpired;

  private static final PasswordEncoder pe = PasswordEncoderFactories.createDelegatingPasswordEncoder();

  public static AuthUserEntity fromJson(UserJson user) {
    AuthUserEntity entity = new AuthUserEntity();
    entity.setUsername(user.username());
    entity.setPassword(pe.encode(user.password()));
    entity.setEnabled(true);
    entity.setAccountNonExpired(true);
    entity.setAccountNonLocked(true);
    entity.setCredentialsNonExpired(true);
    return entity;
  }
}
