package guru.qa.niffler.data.repository.impl.userdata;

import guru.qa.niffler.common.values.FriendshipDbStatus;
import guru.qa.niffler.config.Config;
import guru.qa.niffler.data.entity.user.FriendshipEntity;
import guru.qa.niffler.data.entity.user.UserDataEntity;
import guru.qa.niffler.data.extractor.UserDataEntityExtractor;
import guru.qa.niffler.data.repository.UserDataRepository;
import guru.qa.niffler.data.tpl.DataSources;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.Optional;
import java.util.UUID;

public class UserDataRepositorySpringJdbc implements UserDataRepository {
  private static final Config CFG = Config.getInstance();

  private final JdbcTemplate jdbcTemplate;

  public UserDataRepositorySpringJdbc() {
    this.jdbcTemplate = new JdbcTemplate(DataSources.dataSource(CFG.userdataJdbcUrl()));
  }

  @Override
  public UserDataEntity create(UserDataEntity user) {
    KeyHolder keyHolder = new GeneratedKeyHolder();

    jdbcTemplate.update(connection -> {
      PreparedStatement ps = connection.prepareStatement(
          "INSERT INTO public.user (username, currency, firstname, surname, full_name, photo, photo_small) " +
              "VALUES (?, ?, ?, ?, ?, ?, ?)",
          Statement.RETURN_GENERATED_KEYS
      );
      ps.setString(1, user.getUsername());
      ps.setString(2, user.getCurrency().name());
      ps.setString(3, user.getFirstname());
      ps.setString(4, user.getSurname());
      ps.setString(5, user.getFullname());
      ps.setBytes(6, user.getPhoto());
      ps.setBytes(7, user.getPhotoSmall());
      return ps;
    }, keyHolder);

    UUID generatedId = (UUID) keyHolder.getKeys().get("id");
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
  }

  @Override
  public Optional<UserDataEntity> findById(UUID id) {
    return Optional.ofNullable(
        jdbcTemplate.query(
            """
                SELECT u.*,
                f.requester_id, f.addressee_id, f.created_date, f.status
                FROM public.user u LEFT JOIN public.friendship f
                ON u.id = f.requester_id OR u.id = f.addressee_id
                WHERE u.id = ?
                """,
            UserDataEntityExtractor.instance,
            id
        )
    );
  }

  //TODO: update friendship requests and addressees
  @Override
  public UserDataEntity update(UserDataEntity user) {
    jdbcTemplate.update(
        "UPDATE public.user SET currency = ?, firstname = ?, surname = ?, full_name = ?, photo = ?, photo_small = ? WHERE username = ?",
        user.getCurrency().name(),
        user.getFirstname(),
        user.getSurname(),
        user.getFullname(),
        user.getPhoto(),
        user.getPhotoSmall(),
        user.getUsername()
    );
    return user;
  }

  @Override
  public Optional<UserDataEntity> findByUsername(String username) {
    return Optional.ofNullable(
        jdbcTemplate.query(
            """
                SELECT u.*,
                f.requester_id, f.addressee_id, f.created_date, f.status
                FROM public.user u LEFT JOIN public.friendship f
                ON u.id = f.requester_id OR u.id = f.addressee_id
                WHERE u.username = ?
                """,
            UserDataEntityExtractor.instance,
            username
        )
    );
  }

  @Override
  public void addInvitation(UserDataEntity requester, UserDataEntity addressee) {
    FriendshipEntity request = new FriendshipEntity();
    request.setRequester(requester);
    request.setAddressee(addressee);
    request.setStatus(FriendshipDbStatus.PENDING);

    createFriendshipEntity(request);
  }

  @Override
  public void addFriendship(UserDataEntity friend1, UserDataEntity friend2) {
    FriendshipEntity friendship1 = new FriendshipEntity();
    friendship1.setRequester(friend1);
    friendship1.setAddressee(friend2);
    friendship1.setStatus(FriendshipDbStatus.ACCEPTED);

    FriendshipEntity friendship2 = new FriendshipEntity();
    friendship2.setRequester(friend2);
    friendship2.setAddressee(friend1);
    friendship2.setStatus(FriendshipDbStatus.ACCEPTED);

    createFriendshipEntity(friendship1);
    createFriendshipEntity(friendship2);
  }

  @Override
  public void remove(UserDataEntity user) {
    jdbcTemplate.update("DELETE FROM friendship WHERE requester_id = ? OR addressee_id = ?", user.getId(), user.getId());
    jdbcTemplate.update("DELETE FROM public.user WHERE id = ?", user.getId());
  }

  private FriendshipEntity createFriendshipEntity(FriendshipEntity friendship) {
    jdbcTemplate.update(
        "INSERT INTO friendship (requester_id, addressee_id, status, created_date) VALUES (?, ?, ?, ?)",
        friendship.getRequester().getId(),
        friendship.getAddressee().getId(),
        friendship.getStatus().name(),
        new Date(System.currentTimeMillis())
    );
    return friendship;
  }
}