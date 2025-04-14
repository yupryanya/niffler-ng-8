package guru.qa.niffler.data.dao.auth;

import guru.qa.niffler.data.entity.auth.AuthAuthorityEntity;

public interface AuthAuthorityDao {
  AuthAuthorityEntity createAuthAuthority(AuthAuthorityEntity authAuthority);

  void deleteAuthAuthority(AuthAuthorityEntity authAuthority);
}
