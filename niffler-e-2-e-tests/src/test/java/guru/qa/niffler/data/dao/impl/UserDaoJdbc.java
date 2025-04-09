package guru.qa.niffler.data.dao.impl;

import guru.qa.niffler.common.values.CurrencyValues;
import guru.qa.niffler.config.Config;
import guru.qa.niffler.data.Databases;
import guru.qa.niffler.data.dao.UserDao;
import guru.qa.niffler.data.entity.user.UserEntity;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Optional;
import java.util.UUID;

public class UserDaoJdbc implements UserDao {
  private static final Config CFG = Config.getInstance();

  @Override
  public UserEntity createUser(UserEntity user) {
    try (Connection connection = Databases.connection(CFG.userdataJdbcUrl())) {
      try (PreparedStatement ps = connection.prepareStatement(
          "INSERT INTO users (username, currency, firstname, surname, full_name, photo, photo_small) " +
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
      }
    } catch (SQLException e) {
      throw new RuntimeException("Failed to create user", e);
    }
    return user;
  }

  @Override
  public Optional<UserEntity> findUserById(UUID id) {
    try (Connection connection = Databases.connection(CFG.userdataJdbcUrl())) {
      try (PreparedStatement ps = connection.prepareStatement(
          "SELECT id, username, currency, firstname, surname, full_name, photo, photo_small FROM users WHERE id = ?")) {
        ps.setObject(1, id);
        ps.execute();

        try (var rs = ps.executeQuery()) {
          if (rs.next()) {
            UserEntity ue = new UserEntity();
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
      }
    } catch (SQLException e) {
      throw new RuntimeException("Failed to find user by ID", e);
    }
    return Optional.empty();
  }

  @Override
  public Optional<UserEntity> findUserByUsername(String username) {
    try (Connection connection = Databases.connection(CFG.userdataJdbcUrl())) {
      try (PreparedStatement ps = connection.prepareStatement(
          "SELECT id, username, currency, firstname, surname, full_name, photo, photo_small FROM users WHERE username = ?")) {
        ps.setString(1, username);
        ps.execute();

        try (var rs = ps.executeQuery()) {
          if (rs.next()) {
            UserEntity ue = new UserEntity();
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
      }
    } catch (SQLException e) {
      throw new RuntimeException("Failed to find user by username", e);
    }
    return Optional.empty();
  }

  @Override
  public void deleteUser(UserEntity user) {
    try (Connection connection = Databases.connection(CFG.userdataJdbcUrl())) {
      try (PreparedStatement ps = connection.prepareStatement(
          "DELETE FROM users WHERE id = ?")) {
        ps.setObject(1, user.getId());
        ps.executeUpdate();
      }
    } catch (SQLException e) {
      throw new RuntimeException("Failed to delete user", e);
    }
  }
}