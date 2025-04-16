package guru.qa.niffler.data.dao.impl;

import guru.qa.niffler.common.values.CurrencyValues;
import guru.qa.niffler.config.Config;
import guru.qa.niffler.data.dao.user.UserDataDao;
import guru.qa.niffler.data.entity.user.UserDataEntity;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Optional;
import java.util.UUID;

public class UserDataDaoJdbc implements UserDataDao {
  private static final Config CFG = Config.getInstance();

  private final Connection connection;

  public UserDataDaoJdbc(Connection connection) {
    this.connection = connection;
  }

  @Override
  public UserDataEntity createUser(UserDataEntity user) {
    try (PreparedStatement ps = connection.prepareStatement(
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
      return user;
    } catch (SQLException e) {
      throw new RuntimeException("Failed to create user", e);
    }

  }

  @Override
  public Optional<UserDataEntity> findUserById(UUID id) {
    try (PreparedStatement ps = connection.prepareStatement(
        "SELECT id, username, currency, firstname, surname, full_name, photo, photo_small FROM user WHERE id = ?")) {
      ps.setObject(1, id);
      ps.execute();

      try (var rs = ps.executeQuery()) {
        if (rs.next()) {
          UserDataEntity ue = new UserDataEntity();
          ue.setId(rs.getObject("id", UUID.class));
          ue.setUsername(rs.getString("username"));
          ue.setCurrency(CurrencyValues.valueOf(rs.getString("currency")));
          ue.setFirstname(rs.getString("firstname"));
          ue.setSurname(rs.getString("surname"));
          ue.setPhoto(rs.getBytes("photo"));
          ue.setPhotoSmall(rs.getBytes("photo_small"));
          ue.setFullname(rs.getString("full_name"));
          return Optional.of(ue);
        }
      }
      return Optional.empty();
    } catch (SQLException e) {
      throw new RuntimeException("Failed to find user by ID", e);
    }
  }

  @Override
  public Optional<UserDataEntity> findUserByUsername(String username) {
    try (PreparedStatement ps = connection.prepareStatement(
        "SELECT id, username, currency, firstname, surname, full_name, photo, photo_small FROM user WHERE username = ?")) {
      ps.setString(1, username);
      ps.execute();

      try (var rs = ps.executeQuery()) {
        if (rs.next()) {
          UserDataEntity ue = new UserDataEntity();
          ue.setId(rs.getObject("id", UUID.class));
          ue.setUsername(rs.getString("username"));
          ue.setCurrency(CurrencyValues.valueOf(rs.getString("currency")));
          ue.setFirstname(rs.getString("firstname"));
          ue.setSurname(rs.getString("surname"));
          ue.setPhoto(rs.getBytes("photo"));
          ue.setPhotoSmall(rs.getBytes("photo_small"));
          ue.setFullname(rs.getString("full_name"));
          return Optional.of(ue);
        }
      }
      return Optional.empty();
    } catch (SQLException e) {
      throw new RuntimeException("Failed to find user by username", e);
    }
  }

  @Override
  public void deleteUser(UserDataEntity user) {
    try (PreparedStatement ps = connection.prepareStatement(
        "DELETE FROM user WHERE id = ?")) {
      ps.setObject(1, user.getId());
      ps.executeUpdate();
    } catch (SQLException e) {
      throw new RuntimeException("Failed to delete user", e);
    }
  }
}