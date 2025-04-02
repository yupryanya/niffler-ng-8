package guru.qa.niffler.jupiter;

import guru.qa.niffler.api.SpendApiClient;
import guru.qa.niffler.data.randomData.CategoryData;
import guru.qa.niffler.model.CategoryJson;
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
        AnnotationSupport.findAnnotation(context.getRequiredTestMethod(), Category.class)
                .ifPresent(anno -> {
                    CategoryJson category = spendApiClient.addCategory(new CategoryJson(
                            null,
                            CategoryData.newCategoryName(),
                            anno.username(),
                            false
                    ));

                    if (anno.archived()) {
                        category = spendApiClient.updateCategory(new CategoryJson(
                                category.id(),
                                category.name(),
                                category.username(),
                                true
                        ));
                    }

                    context.getStore(NAMESPACE).put(context.getUniqueId(), category);
                });
    }

    @Override
    public void afterTestExecution(ExtensionContext context) throws Exception {
        CategoryJson category = context.getStore(NAMESPACE).get(context.getUniqueId(), CategoryJson.class);
        if (!category.archived()) {
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
