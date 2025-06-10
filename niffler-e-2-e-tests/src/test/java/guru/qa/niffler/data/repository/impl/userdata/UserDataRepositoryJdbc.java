package guru.qa.niffler.data.repository.impl.userdata;

import guru.qa.niffler.common.values.FriendshipStatus;
import guru.qa.niffler.config.Config;
import guru.qa.niffler.data.entity.user.FriendshipEntity;
import guru.qa.niffler.data.entity.user.UserDataEntity;
import guru.qa.niffler.data.mapper.UserDataEntityRowMapper;
import guru.qa.niffler.data.repository.UserDataRepository;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static guru.qa.niffler.data.tpl.Connections.holder;

@ParametersAreNonnullByDefault
public class UserDataRepositoryJdbc implements UserDataRepository {
  private static final Config CFG = Config.getInstance();

  @Override
  public @Nonnull UserDataEntity create(UserDataEntity user) {
    try (PreparedStatement ps = holder(CFG.userdataJdbcUrl()).connection().prepareStatement(
        "INSERT INTO public.user (username, currency, firstname, surname, full_name, photo, photo_small) " +
            "VALUES (?, ?, ?, ?, ?, ?, ?)", PreparedStatement.RETURN_GENERATED_KEYS)) {
      ps.setString(1, user.getUsername());
      ps.setString(2, user.getCurrency().name());
      ps.setString(3, user.getFirstname());
      ps.setString(4, user.getSurname());
      ps.setString(5, user.getFullname());
      ps.setBytes(6, user.getPhoto());
      ps.setBytes(7, user.getPhotoSmall());

      ps.executeUpdate();

      final UUID generatedId;
      try (var rs = ps.getGeneratedKeys()) {
        if (rs.next()) {
          generatedId = rs.getObject(1, UUID.class);
        } else {
          throw new SQLException("Failed to retrieve generated key");
        }
      }
      user.setId(generatedId);

      if (user.getFriendshipRequests() != null && !user.getFriendshipRequests().isEmpty()) {
        for (FriendshipEntity fe : user.getFriendshipRequests()) {
          createFriendshipEntity(fe);
        }
      }

      if (user.getFriendshipAddressees() != null && !user.getFriendshipAddressees().isEmpty()) {
        for (FriendshipEntity fe : user.getFriendshipAddressees()) {
          createFriendshipEntity(fe);
        }
      }

      return user;
    } catch (SQLException e) {
      throw new RuntimeException("Failed to create user", e);
    }
  }

  @Override
  public Optional<UserDataEntity> findById(UUID id) {
    try (PreparedStatement ps = holder(CFG.userdataJdbcUrl()).connection().prepareStatement(
        """
            SELECT u.*, f.requester_id, f.addressee_id, f.created_date, f.status
            FROM public.user u
            LEFT JOIN public.friendship f ON u.id = f.requester_id OR u.id = f.addressee_id
            WHERE u.id = ?
            """
    )) {
      ps.setObject(1, id);

      UserDataEntity ue = null;
      List<FriendshipEntity> friendshipRequests = new ArrayList<>();
      List<FriendshipEntity> friendshipAddressees = new ArrayList<>();

      try (ResultSet rs = ps.executeQuery()) {
        while (rs.next()) {
          if (ue == null) {
            ue = UserDataEntityRowMapper.instance.mapRow(rs, 1);
          }

          UUID requesterId = rs.getObject("requester_id", UUID.class);
          UUID addresseeId = rs.getObject("addressee_id", UUID.class);
          if (requesterId != null && addresseeId != null) {
            FriendshipEntity fe = new FriendshipEntity();
            fe.setRequester(createUserReference(requesterId));
            fe.setAddressee(createUserReference(addresseeId));
            fe.setCreatedDate(rs.getDate("created_date"));
            fe.setStatus(FriendshipStatus.valueOf(rs.getString("status")));

            if (requesterId.equals(ue.getId())) {
              friendshipRequests.add(fe);
            } else if (addresseeId.equals(ue.getId())) {
              friendshipAddressees.add(fe);
            }
          }
        }
      }
      if (ue != null) {
        ue.setFriendshipRequests(friendshipRequests);
        ue.setFriendshipAddressees(friendshipAddressees);
        return Optional.of(ue);
      } else {
        return Optional.empty();
      }
    } catch (SQLException e) {
      throw new RuntimeException("Failed to find user by id", e);
    }
  }

  @Override
  public @Nonnull UserDataEntity update(UserDataEntity user) {
    if (user.getId() == null) {
      throw new IllegalArgumentException("User ID must not be null for update operation");
    }

    try (PreparedStatement ps = holder(CFG.userdataJdbcUrl()).connection().prepareStatement(
        "UPDATE public.user SET username = ?, currency = ?, firstname = ?, surname = ?, full_name = ?, photo = ?, photo_small = ? " +
            "WHERE id = ?")) {

      ps.setString(1, user.getUsername());
      ps.setString(2, user.getCurrency().name());
      ps.setString(3, user.getFirstname());
      ps.setString(4, user.getSurname());
      ps.setString(5, user.getFullname());
      ps.setBytes(6, user.getPhoto());
      ps.setBytes(7, user.getPhotoSmall());
      ps.setObject(8, user.getId());
      ps.executeUpdate();

      removeFriendshipEntities(user.getId());
      if (user.getFriendshipRequests() != null) {
        createFriendshipEntities(user.getFriendshipRequests());
      }

      if (user.getFriendshipAddressees() != null && !user.getFriendshipAddressees().isEmpty()) {
        createFriendshipEntities(user.getFriendshipAddressees());
      }

      return user;
    } catch (SQLException e) {
      throw new RuntimeException("Failed to update user", e);
    }
  }

  @Override
  public Optional<UserDataEntity> findByUsername(String username) {
    try (PreparedStatement ps = holder(CFG.userdataJdbcUrl()).connection().prepareStatement(
        """
            SELECT u.*, f.requester_id, f.addressee_id, f.created_date, f.status
            FROM public.user u
            LEFT JOIN public.friendship f ON u.id = f.requester_id OR u.id = f.addressee_id
            WHERE u.username = ?
            """
    )) {
      ps.setString(1, username);

      UserDataEntity ue = null;
      List<FriendshipEntity> friendshipRequests = new ArrayList<>();
      List<FriendshipEntity> friendshipAddressees = new ArrayList<>();

      try (ResultSet rs = ps.executeQuery()) {
        while (rs.next()) {
          if (ue == null) {
            ue = UserDataEntityRowMapper.instance.mapRow(rs, 1);
          }

          UUID requesterId = rs.getObject("requester_id", UUID.class);
          UUID addresseeId = rs.getObject("addressee_id", UUID.class);
          if (requesterId != null && addresseeId != null) {
            FriendshipEntity fe = new FriendshipEntity();
            fe.setRequester(createUserReference(requesterId));
            fe.setAddressee(createUserReference(addresseeId));
            fe.setCreatedDate(rs.getDate("created_date"));
            fe.setStatus(FriendshipStatus.valueOf(rs.getString("status")));

            if (requesterId.equals(ue.getId())) {
              friendshipRequests.add(fe);
            } else if (addresseeId.equals(ue.getId())) {
              friendshipAddressees.add(fe);
            }
          }
        }
      }
      if (ue != null) {
        ue.setFriendshipRequests(friendshipRequests);
        ue.setFriendshipAddressees(friendshipAddressees);
        return Optional.of(ue);
      } else {
        return Optional.empty();
      }
    } catch (SQLException e) {
      throw new RuntimeException("Failed to find user by id", e);
    }
  }

  @Override
  public void addInvitation(UserDataEntity requester, UserDataEntity addressee) {
    FriendshipEntity request = new FriendshipEntity();
    request.setRequester(requester);
    request.setAddressee(addressee);
    request.setStatus(FriendshipStatus.PENDING);

    createFriendshipEntity(request);
  }

  @Override
  public void addFriendship(UserDataEntity friend1, UserDataEntity friend2) {
    FriendshipEntity friendship1 = new FriendshipEntity();
    friendship1.setRequester(friend1);
    friendship1.setAddressee(friend2);
    friendship1.setStatus(FriendshipStatus.ACCEPTED);

    FriendshipEntity friendship2 = new FriendshipEntity();
    friendship2.setRequester(friend2);
    friendship2.setAddressee(friend1);
    friendship2.setStatus(FriendshipStatus.ACCEPTED);

    createFriendshipEntity(friendship1);
    createFriendshipEntity(friendship2);
  }

  @Override
  public void remove(UserDataEntity user) {
    try (var connection = holder(CFG.userdataJdbcUrl()).connection()) {
      removeFriendshipEntities(user.getId());

      try (var deleteUser = connection.prepareStatement(
          "DELETE FROM public.user WHERE id = ?")) {
        deleteUser.setObject(1, user.getId());
        deleteUser.executeUpdate();
      }
    } catch (SQLException e) {
      throw new RuntimeException("Failed to remove user", e);
    }
  }

  private void createFriendshipEntity(FriendshipEntity friendship) {
    try (PreparedStatement ps = holder(CFG.userdataJdbcUrl()).connection().prepareStatement(
        "INSERT INTO friendship (requester_id, addressee_id, status, created_date) " +
            "VALUES (?, ?, ?, ?)"
    )) {
      ps.setObject(1, friendship.getRequester().getId());
      ps.setObject(2, friendship.getAddressee().getId());
      ps.setString(3, friendship.getStatus().name());
      ps.setDate(4, new java.sql.Date(System.currentTimeMillis()));
      ps.executeUpdate();
    } catch (SQLException e) {
      throw new RuntimeException("Failed to create friendship entity", e);
    }
  }

  private void removeFriendshipEntities(UUID userId) {
    try (var connection = holder(CFG.userdataJdbcUrl()).connection();
         var ps = connection.prepareStatement(
             "DELETE FROM public.friendship WHERE requester_id = ? OR addressee_id = ?")) {

      ps.setObject(1, userId);
      ps.setObject(2, userId);
      ps.executeUpdate();
    } catch (SQLException e) {
      throw new RuntimeException("Failed to remove friendship entities", e);
    }
  }

  private void createFriendshipEntities(List<FriendshipEntity> friendships) {
    if (friendships != null) {
      friendships.forEach(this::createFriendshipEntity);
    }
  }

  private UserDataEntity createUserReference(UUID id) {
    UserDataEntity user = new UserDataEntity();
    user.setId(id);
    return user;
  }
}