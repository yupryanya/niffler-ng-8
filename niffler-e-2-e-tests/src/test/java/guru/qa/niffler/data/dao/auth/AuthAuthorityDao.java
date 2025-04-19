package guru.qa.niffler.data.dao.auth;

import guru.qa.niffler.data.entity.auth.AuthAuthorityEntity;

import java.util.List;

public interface AuthAuthorityDao {
  void createAuthAuthorities(AuthAuthorityEntity... authAuthority);

  void deleteAuthAuthority(AuthAuthorityEntity authAuthority);

  List<AuthAuthorityEntity> findAll();
}
