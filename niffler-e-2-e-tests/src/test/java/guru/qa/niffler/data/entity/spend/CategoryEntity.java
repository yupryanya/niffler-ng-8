package guru.qa.niffler.data.entity.spend;

import guru.qa.niffler.model.CategoryJson;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;
import java.util.UUID;

@Getter
@Setter
@ToString
public class CategoryEntity implements Serializable {
  private UUID id;
  private String name;
  private String username;
  private boolean archived;

  public static CategoryEntity fromJson(CategoryJson json) {
    CategoryEntity entity = new CategoryEntity();
    entity.setId(json.id());
    entity.setName(json.name());
    entity.setUsername(json.username());
    entity.setArchived(json.archived());
    return entity;
  }
}