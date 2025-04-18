package guru.qa.niffler.data.entity.user;

import guru.qa.niffler.common.values.CurrencyValues;
import guru.qa.niffler.model.UserJson;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;
import java.util.UUID;

@Getter
@Setter
@ToString
public class UserDataEntity implements Serializable {
  private UUID id;
  private String username;
  private CurrencyValues currency;
  private String firstname;
  private String surname;
  private String fullname;
  private byte[] photo;
  private byte[] photoSmall;

  public static UserDataEntity fromJson(UserJson user) {
    UserDataEntity entity = new UserDataEntity();
    entity.setUsername(user.username());
    entity.setCurrency(CurrencyValues.RUB);
    entity.setFirstname(null);
    entity.setSurname(null);
    entity.setFullname(null);
    entity.setPhoto(null);
    entity.setPhotoSmall(null);
    return entity;
  }
}