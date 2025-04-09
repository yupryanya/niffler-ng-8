package guru.qa.niffler.jupiter.extension;

import guru.qa.niffler.api.SpendApiClient;
import guru.qa.niffler.jupiter.annotation.Category;
import guru.qa.niffler.jupiter.annotation.meta.User;
import guru.qa.niffler.model.CategoryJson;
import guru.qa.niffler.utils.RandomDataUtils;
import org.junit.jupiter.api.extension.*;
import org.junit.platform.commons.support.AnnotationSupport;

public class CategoryExtension implements
    ParameterResolver,
    BeforeEachCallback,
    AfterTestExecutionCallback {

  public static final ExtensionContext.Namespace NAMESPACE = ExtensionContext.Namespace.create(CategoryExtension.class);
  private final SpendApiClient spendApiClient = new SpendApiClient();

  @Override
  public void beforeEach(ExtensionContext context) {
    AnnotationSupport.findAnnotation(context.getRequiredTestMethod(), User.class)
        .ifPresent(user -> {
          if (user.categories() == null || user.categories().length == 0) {
            return;
          }
          Category category = user.categories()[0];
          CategoryJson categoryJson = spendApiClient.addCategory(new CategoryJson(
              null,
              RandomDataUtils.newCategoryName(),
              user.username(),
              false
          ));
          if (category.archived()) {
            categoryJson = spendApiClient.updateCategory(new CategoryJson(
                categoryJson.id(),
                categoryJson.name(),
                categoryJson.username(),
                true
            ));
          }
          context.getStore(NAMESPACE).put(context.getUniqueId(), categoryJson);
        });
  }

  @Override
  public void afterTestExecution(ExtensionContext context) throws Exception {
    CategoryJson category = context.getStore(NAMESPACE).get(context.getUniqueId(), CategoryJson.class);
    if (category != null && !category.archived()) {
      spendApiClient.updateCategory(new CategoryJson(
          category.id(),
          category.name(),
          category.username(),
          true
      ));
    }
  }

  @Override
  public boolean supportsParameter(ParameterContext parameterContext, ExtensionContext extensionContext) throws ParameterResolutionException {
    return parameterContext.getParameter().getType().isAssignableFrom(CategoryJson.class);
  }

  @Override
  public Object resolveParameter(ParameterContext parameterContext, ExtensionContext extensionContext) throws ParameterResolutionException {
    return extensionContext.getStore(CategoryExtension.NAMESPACE).get(extensionContext.getUniqueId(), CategoryJson.class);
  }
}