package guru.qa.niffler.jupiter.extension;

import guru.qa.niffler.jupiter.annotation.User;
import guru.qa.niffler.model.TestData;
import guru.qa.niffler.model.UserJson;
import guru.qa.niffler.service.UserClient;
import org.junit.jupiter.api.extension.*;
import org.junit.platform.commons.support.AnnotationSupport;

import javax.annotation.Nullable;
import java.util.Collections;
import java.util.List;

import static guru.qa.niffler.utils.RandomDataUtils.newValidPassword;
import static guru.qa.niffler.utils.RandomDataUtils.nonExistentUserName;

public class UserExtension implements
    ParameterResolver,
    BeforeEachCallback {

  public static final ExtensionContext.Namespace NAMESPACE = ExtensionContext.Namespace.create(UserExtension.class);
  private final UserClient userClient = UserClient.getInstance();

  @Override
  public void beforeEach(ExtensionContext context) {
    AnnotationSupport.findAnnotation(context.getRequiredTestMethod(), User.class)
        .ifPresent(user -> {
          final String username = !"".equals(user.username()) ? user.username() : nonExistentUserName();
          final String password = newValidPassword();
          final UserJson createdUser = userClient.createUser(username, password);
          List<UserJson> incomeInvitations = Collections.emptyList();
          List<UserJson> outcomeInvitations = Collections.emptyList();
          List<UserJson> friends = Collections.emptyList();

          if (user.incomeInvitations() > 0) {
            incomeInvitations = userClient.createIncomeInvitations(createdUser, user.incomeInvitations());
          }
          if (user.outcomeInvitations() > 0) {
            outcomeInvitations = userClient.createOutcomeInvitations(createdUser, user.outcomeInvitations());
          }
          if (user.friends() > 0) {
            friends = userClient.createFriends(createdUser, user.friends());
          }

          final TestData testData = TestData.emptyTestData()
              .withPassword(password)
              .withIncomeInvitations(incomeInvitations)
              .withOutcomeInvitations(outcomeInvitations)
              .withFriends(friends);

          setContextUser(createdUser.withTestData(testData));
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