package guru.qa.niffler.data.extractor;

import guru.qa.niffler.common.values.CurrencyValues;
import guru.qa.niffler.common.values.FriendshipStatus;
import guru.qa.niffler.data.entity.user.FriendshipEntity;
import guru.qa.niffler.data.entity.user.UserDataEntity;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class UserDataEntityExtractor implements ResultSetExtractor<UserDataEntity> {
  public static final UserDataEntityExtractor instance = new UserDataEntityExtractor();

  private UserDataEntityExtractor() {
  }

  @Override
  public @Nullable UserDataEntity extractData(@Nonnull ResultSet rs) throws SQLException, DataAccessException {
    Map<UUID, UserDataEntity> users = new ConcurrentHashMap<>();
    UUID userId = null;

    while (rs.next()) {
      userId = rs.getObject("id", UUID.class);
      UserDataEntity user = users.computeIfAbsent(userId, id -> {
        UserDataEntity result = new UserDataEntity();
        try {
          result.setId(id);
          result.setUsername(rs.getString("username"));
          result.setFirstname(rs.getString("firstname"));
          result.setSurname(rs.getString("surname"));
          result.setFullname(rs.getString("full_name"));
          result.setPhoto(rs.getBytes("photo"));
          result.setPhotoSmall(rs.getBytes("photo_small"));
          result.setCurrency(CurrencyValues.valueOf(rs.getString("currency")));
          result.setFriendshipRequests(new ArrayList<>());
          result.setFriendshipAddressees(new ArrayList<>());
        } catch (SQLException e) {
          throw new RuntimeException("Error extracting UserDataEntity from ResultSet", e);
        }
        return result;
      });

      UUID requesterId = rs.getObject("requester_id", UUID.class);
      UUID addresseeId = rs.getObject("addressee_id", UUID.class);

      if (requesterId != null && addresseeId != null) {
        FriendshipEntity friendship = new FriendshipEntity();
        friendship.setRequester(createUserReference(requesterId));
        friendship.setAddressee(createUserReference(addresseeId));
        friendship.setCreatedDate(rs.getDate("created_date"));
        friendship.setStatus(FriendshipStatus.valueOf(rs.getString("status")));

        if (requesterId.equals(user.getId())) {
          user.getFriendshipRequests().add(friendship);
        } else if (addresseeId.equals(user.getId())) {
          user.getFriendshipAddressees().add(friendship);
        }
      }
    }

    return userId != null ? users.get(userId) : null;
  }

  private @Nonnull UserDataEntity createUserReference(@Nonnull UUID id) {
    UserDataEntity user = new UserDataEntity();
    user.setId(id);
    return user;
  }
}