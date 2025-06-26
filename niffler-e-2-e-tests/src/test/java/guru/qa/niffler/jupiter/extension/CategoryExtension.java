package guru.qa.niffler.jupiter.extension;

import guru.qa.niffler.jupiter.annotation.Category;
import guru.qa.niffler.jupiter.annotation.User;
import guru.qa.niffler.model.CategoryJson;
import guru.qa.niffler.model.UserJson;
import guru.qa.niffler.service.SpendClient;
import guru.qa.niffler.service.api.SpendApiClient;
import guru.qa.niffler.utils.RandomDataUtils;
import org.junit.jupiter.api.extension.*;
import org.junit.platform.commons.support.AnnotationSupport;

import java.util.ArrayList;
import java.util.List;

public class CategoryExtension implements
    ParameterResolver,
    BeforeEachCallback {

  public static final ExtensionContext.Namespace NAMESPACE = ExtensionContext.Namespace.create(CategoryExtension.class);
  private final SpendClient spendClient = SpendClient.getInstance();

  @Override
  public void beforeEach(ExtensionContext context) {
    AnnotationSupport.findAnnotation(context.getRequiredTestMethod(), User.class)
        .ifPresent(user -> {
          if (user.categories() == null || user.categories().length == 0) {
            return;
          }
          UserJson contextUser = UserExtension.getContextUser();
          final String username = contextUser != null ? contextUser.username() : user.username();

          final List<CategoryJson> categories = new ArrayList<>();

          for (Category category : user.categories()) {
            String categoryName = category.name().isEmpty() ? RandomDataUtils.newCategoryName() : category.name();

            CategoryJson categoryJson = new CategoryJson(
                null,
                categoryName,
                username,
                category.archived()
            );
            CategoryJson categoryCreated = spendClient.createCategory(categoryJson);
            if (category.archived()) {
              CategoryJson categoryUpdateJson = new CategoryJson(
                  categoryCreated.id(),
                  categoryName,
                  username,
                  category.archived()
              );
              categoryCreated = spendClient.updateCategory(categoryUpdateJson);
            }
            categories.add(categoryCreated);
          }

          if (contextUser != null) {
            contextUser.testData().categories().addAll(categories);
          } else {

            context.getStore(NAMESPACE).put(context.getUniqueId(), categories);
          }
        });
  }

  @Override
  public boolean supportsParameter(ParameterContext parameterContext, ExtensionContext extensionContext) throws ParameterResolutionException {
    return parameterContext.getParameter().getType().isAssignableFrom(CategoryJson.class);
  }

  @Override
  @SuppressWarnings("unchecked")
  public CategoryJson[] resolveParameter(ParameterContext parameterContext, ExtensionContext extensionContext) throws ParameterResolutionException {
    return (CategoryJson[]) extensionContext.getStore(CategoryExtension.NAMESPACE).get(extensionContext.getUniqueId(),
            List.class)
        .stream()
        .toArray(CategoryJson[]::new);
  }
}