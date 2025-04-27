package guru.qa.niffler.data.entity.user;

import guru.qa.niffler.common.values.CurrencyValues;
import guru.qa.niffler.common.values.FriendshipStatus;
import guru.qa.niffler.model.UserJson;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.proxy.HibernateProxy;

import java.io.Serializable;
import java.util.*;
import java.util.stream.Stream;

@Getter
@Setter
@Entity
@Table(name = "\"user\"")
public class UserDataEntity implements Serializable {
  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  @Column(name = "id", nullable = false, columnDefinition = "UUID default gen_random_uuid()")
  private UUID id;

  @Column(nullable = false, unique = true)
  private String username;

  @Column(nullable = false)
  @Enumerated(EnumType.STRING)
  private CurrencyValues currency;

  @Column()
  private String firstname;

  @Column()
  private String surname;

  @Column(name = "full_name")
  private String fullname;

  @Column(name = "photo", columnDefinition = "bytea")
  private byte[] photo;

  @Column(name = "photo_small", columnDefinition = "bytea")
  private byte[] photoSmall;

  @OneToMany(mappedBy = "requester", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
  private List<FriendshipEntity> friendshipRequests = new ArrayList<>();

  @OneToMany(mappedBy = "addressee", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
  private List<FriendshipEntity> friendshipAddressees = new ArrayList<>();

  public void addFriends(FriendshipStatus status, UserDataEntity... friends) {
    List<FriendshipEntity> friendsEntities = Stream.of(friends)
        .map(f -> {
          FriendshipEntity fe = new FriendshipEntity();
          fe.setRequester(this);
          fe.setAddressee(f);
          fe.setStatus(status);
          fe.setCreatedDate(new Date());
          return fe;
        }).toList();
    this.friendshipRequests.addAll(friendsEntities);
  }

  public void addInvitations(UserDataEntity... invitations) {
    List<FriendshipEntity> invitationsEntities = Stream.of(invitations)
        .map(i -> {
          FriendshipEntity fe = new FriendshipEntity();
          fe.setRequester(i);
          fe.setAddressee(this);
          fe.setStatus(FriendshipStatus.PENDING);
          fe.setCreatedDate(new Date());
          return fe;
        }).toList();
    this.friendshipAddressees.addAll(invitationsEntities);
  }

  public void removeFriends(UserDataEntity... friends) {
    List<UUID> idsToBeRemoved = Arrays.stream(friends).map(UserDataEntity::getId).toList();
    for (Iterator<FriendshipEntity> i = getFriendshipRequests().iterator(); i.hasNext(); ) {
      FriendshipEntity friendsEntity = i.next();
      if (idsToBeRemoved.contains(friendsEntity.getAddressee().getId())) {
        friendsEntity.setAddressee(null);
        i.remove();
      }
    }
  }

  public void removeInvites(UserDataEntity... invitations) {
    List<UUID> idsToBeRemoved = Arrays.stream(invitations).map(UserDataEntity::getId).toList();
    for (Iterator<FriendshipEntity> i = getFriendshipAddressees().iterator(); i.hasNext(); ) {
      FriendshipEntity friendsEntity = i.next();
      if (idsToBeRemoved.contains(friendsEntity.getRequester().getId())) {
        friendsEntity.setRequester(null);
        i.remove();
      }
    }
  }

  @Override
  public final boolean equals(Object o) {
    if (this == o) return true;
    if (o == null) return false;
    Class<?> oEffectiveClass = o instanceof HibernateProxy ? ((HibernateProxy) o).getHibernateLazyInitializer().getPersistentClass() : o.getClass();
    Class<?> thisEffectiveClass = this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass() : this.getClass();
    if (thisEffectiveClass != oEffectiveClass) return false;
    UserDataEntity that = (UserDataEntity) o;
    return getId() != null && Objects.equals(getId(), that.getId());
  }

  @Override
  public final int hashCode() {
    return this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass().hashCode() : getClass().hashCode();
  }

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