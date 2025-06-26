package guru.qa.niffler.test.rest;

import guru.qa.niffler.jupiter.annotation.meta.RestTest;
import guru.qa.niffler.jupiter.extension.ApiLoginExtension;
import guru.qa.niffler.service.api.UserApiClient;
import guru.qa.niffler.service.api.gateway.GwUserApiClient;
import guru.qa.niffler.service.api.gateway.GwV2UserApiClient;
import org.junit.jupiter.api.extension.RegisterExtension;

@RestTest
public class BaseTestRest {
  @RegisterExtension
  static ApiLoginExtension apiLoginExtension = ApiLoginExtension.rest();

  final UserApiClient userApiClient = new UserApiClient();
  final GwUserApiClient gwUserApiClient = new GwUserApiClient();
  final GwV2UserApiClient gwV2UserApiClient = new GwV2UserApiClient();
}
