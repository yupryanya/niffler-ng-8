package guru.qa.niffler.data.entity.user;

import guru.qa.niffler.common.values.FriendshipDbStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.proxy.HibernateProxy;

import java.io.Serializable;
import java.util.Date;
import java.util.Objects;

@Getter
@Setter
@Entity
@Table(name = "friendship")
@IdClass(FriendShipId.class)
public class FriendshipEntity implements Serializable {

  @Id
  @ManyToOne
  @JoinColumn(name = "requester_id", referencedColumnName = "id")
  private UserDataEntity requester;

  @Id
  @ManyToOne
  @JoinColumn(name = "addressee_id", referencedColumnName = "id")
  private UserDataEntity addressee;

  @Column(name = "created_date", columnDefinition = "DATE", nullable = false)
  private Date createdDate;

  @Enumerated(EnumType.STRING)
  private FriendshipDbStatus status;

  @Override
  public final boolean equals(Object o) {
    if (this == o) return true;
    if (o == null) return false;
    Class<?> oEffectiveClass = o instanceof HibernateProxy ? ((HibernateProxy) o).getHibernateLazyInitializer().getPersistentClass() : o.getClass();
    Class<?> thisEffectiveClass = this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass() : this.getClass();
    if (thisEffectiveClass != oEffectiveClass) return false;
    FriendshipEntity that = (FriendshipEntity) o;
    return getRequester() != null && Objects.equals(getRequester(), that.getRequester())
        && getAddressee() != null && Objects.equals(getAddressee(), that.getAddressee());
  }

  @Override
  public final int hashCode() {
    return Objects.hash(requester, addressee);
  }
}
