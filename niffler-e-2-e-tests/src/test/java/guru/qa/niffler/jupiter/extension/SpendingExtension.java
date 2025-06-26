package guru.qa.niffler.jupiter.extension;

import guru.qa.niffler.jupiter.annotation.Spend;
import guru.qa.niffler.jupiter.annotation.User;
import guru.qa.niffler.model.CategoryJson;
import guru.qa.niffler.model.SpendJson;
import guru.qa.niffler.model.UserJson;
import guru.qa.niffler.service.SpendClient;
import guru.qa.niffler.service.api.SpendApiClient;
import org.junit.jupiter.api.extension.*;
import org.junit.platform.commons.support.AnnotationSupport;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class SpendingExtension implements
    BeforeEachCallback,
    ParameterResolver {

  public static final ExtensionContext.Namespace NAMESPACE = ExtensionContext.Namespace.create(SpendingExtension.class);
  private final SpendClient spendClient = SpendClient.getInstance();

  @Override
  public void beforeEach(ExtensionContext context) {
    AnnotationSupport.findAnnotation(context.getRequiredTestMethod(), User.class)
        .ifPresent(user -> {
          if (user.spends() == null || user.spends().length == 0) {
            return;
          }

          UserJson contextUser = UserExtension.getContextUser();
          final String username = contextUser != null ? contextUser.username() : user.username();
          final List<SpendJson> spends = new ArrayList<>();

          for (Spend spend : user.spends()) {
            SpendJson spendJson = new SpendJson(
                null,
                new Date(),
                new CategoryJson(
                    null,
                    spend.category(),
                    username,
                    false
                ),
                spend.currency(),
                spend.amount(),
                spend.description(),
                username
            );
            SpendJson created = spendClient.createSpend(spendJson);
            spends.add(created);
          }
          if (contextUser != null) {
            contextUser.testData().spends().addAll(spends);
          } else {
            context.getStore(NAMESPACE).put(context.getUniqueId(), spends);
          }
        });
  }

  @Override
  public boolean supportsParameter(ParameterContext parameterContext, ExtensionContext extensionContext) throws ParameterResolutionException {
    return parameterContext.getParameter().getType().isAssignableFrom(SpendJson[].class);
  }

  @Override
  @SuppressWarnings("unchecked")
  public SpendJson[] resolveParameter(ParameterContext parameterContext, ExtensionContext extensionContext) throws ParameterResolutionException {
    return (SpendJson[]) extensionContext.getStore(SpendingExtension.NAMESPACE).get(extensionContext.getUniqueId(),
        List.class)
        .stream()
        .toArray(SpendJson[]::new);
  }
}