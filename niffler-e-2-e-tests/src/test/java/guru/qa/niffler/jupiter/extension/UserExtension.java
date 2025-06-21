package guru.qa.niffler.jupiter.extension;

import guru.qa.niffler.jupiter.annotation.User;
import guru.qa.niffler.model.UserJson;
import guru.qa.niffler.service.UserClient;
import guru.qa.niffler.service.api.UserApiClient;
import org.junit.jupiter.api.extension.*;
import org.junit.platform.commons.support.AnnotationSupport;

import javax.annotation.Nullable;

import static guru.qa.niffler.utils.RandomDataUtils.newValidPassword;
import static guru.qa.niffler.utils.RandomDataUtils.nonExistentUserName;

public class UserExtension implements
    ParameterResolver,
    BeforeEachCallback {

  public static final ExtensionContext.Namespace NAMESPACE = ExtensionContext.Namespace.create(UserExtension.class);
  //  private final UserClient userClient = new UserDbClient();
  private final UserClient userClient = new UserApiClient();

  @Override
  public void beforeEach(ExtensionContext context) {
    AnnotationSupport.findAnnotation(context.getRequiredTestMethod(), User.class)
        .ifPresent(user -> {
          UserJson userJson;
          final String username = "".equals(user.username()) ? nonExistentUserName() : user.username();
          final String password = newValidPassword();

          userJson = userClient.createUser(username, password);

          if (user.incomeInvitations() > 0) {
            userClient.createIncomeInvitations(userJson, user.incomeInvitations());
          }
          if (user.outcomeInvitations() > 0) {
            userClient.createOutcomeInvitations(userJson, user.outcomeInvitations());
          }
          if (user.friends() > 0) {
            userClient.createFriends(userJson, user.friends());
          }
          setContextUser(userJson);
        });
  }

  @Override
  public boolean supportsParameter(ParameterContext parameterContext, ExtensionContext extensionContext) throws ParameterResolutionException {
    return parameterContext.getParameter().getType().isAssignableFrom(UserJson.class);
  }

  @Override
  public UserJson resolveParameter(ParameterContext parameterContext, ExtensionContext extensionContext) throws ParameterResolutionException {
    return getContextUser();
  }

  public static @Nullable UserJson getContextUser() {
    final ExtensionContext context = TestMethodContextExtension.context();
    return context.getStore(NAMESPACE).get(context.getUniqueId(), UserJson.class);
  }

  static void setContextUser(UserJson user) {
    final ExtensionContext context = TestMethodContextExtension.context();
    context.getStore(NAMESPACE).put(context.getUniqueId(), user);
  }
}