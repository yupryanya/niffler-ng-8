package guru.qa.niffler.jupiter.extension;

import org.apache.commons.lang3.time.StopWatch;
import org.junit.jupiter.api.extension.*;
import org.junit.platform.commons.support.AnnotationSupport;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.TimeUnit;

public class UsersQueueExtension implements
        BeforeTestExecutionCallback,
        AfterTestExecutionCallback,
        ParameterResolver {

    public static final ExtensionContext.Namespace NAMESPACE = ExtensionContext.Namespace.create(UsersQueueExtension.class);

    private static final Queue<StaticUser> EMPTY_USERS = new ConcurrentLinkedQueue<>();
    private static final Queue<StaticUser> WITH_FRIEND_USERS = new ConcurrentLinkedQueue<>();
    private static final Queue<StaticUser> WITH_INCOME_REQUEST_USERS = new ConcurrentLinkedQueue<>();
    private static final Queue<StaticUser> WITH_OUTCOME_REQUEST_USERS = new ConcurrentLinkedQueue<>();

    public record StaticUser(
            String username,
            String password,
            String friend,
            String income,
            String outcome) {
    }

    static {
        EMPTY_USERS.add(new StaticUser("testuser1", "testpassword", null, null, null));
        WITH_FRIEND_USERS.add(new StaticUser("testuser2", "testpassword", "testUser4", null, null));
        WITH_INCOME_REQUEST_USERS.add(new StaticUser("testuser3", "testpassword", null, "testuser4", null));
        WITH_OUTCOME_REQUEST_USERS.add(new StaticUser("testuser4", "testpassword", null, null, "testuser3"));
    }

    @Target(ElementType.PARAMETER)
    @Retention(RetentionPolicy.RUNTIME)
    public @interface UserType {
        Type value() default Type.EMPTY;

        enum Type {
            EMPTY, WITH_FRIEND, WITH_INCOME_REQUEST, WITH_OUTCOME_REQUEST
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public void beforeTestExecution(ExtensionContext context) {
        Arrays.stream(context.getRequiredTestMethod().getParameters())
                .filter(param -> AnnotationSupport.isAnnotated(param, UserType.class))
                .filter(param -> param.getType().equals(StaticUser.class))
                .map(param -> param.getAnnotation(UserType.class))
                .forEach(userType -> {
                    StopWatch stopWatch = StopWatch.createStarted();
                    StaticUser user = null;
                    while (user == null && stopWatch.getTime(TimeUnit.SECONDS) < 30) {
                        user = getQueue(userType).poll();
                    }
                    Optional.ofNullable(user).ifPresentOrElse(
                            u -> {
                                Map<UserType, StaticUser> userMap = (Map<UserType, StaticUser>) context.getStore(NAMESPACE)
                                        .getOrComputeIfAbsent(context.getUniqueId(), key -> new HashMap<>());
                                userMap.put(userType, u);
                            },
                            () -> {
                                throw new IllegalStateException("Can't obtain user after 30 seconds.");
                            });
                });
    }

    @Override
    @SuppressWarnings("unchecked")
    public void afterTestExecution(ExtensionContext context) {
        Map<UserType, StaticUser> users = context.getStore(NAMESPACE).get(context.getUniqueId(), Map.class);
        if (users != null) {
            users.forEach((key, value) -> getQueue(key).add(value));
        }
    }

    @Override
    public boolean supportsParameter(ParameterContext parameterContext, ExtensionContext extensionContext) throws ParameterResolutionException {
        return parameterContext.getParameter().getType().isAssignableFrom(StaticUser.class)
                && AnnotationSupport.isAnnotated(parameterContext.getParameter(), UserType.class);
    }

    @Override
    public StaticUser resolveParameter(ParameterContext parameterContext, ExtensionContext extensionContext) throws ParameterResolutionException {
        UserType userType = AnnotationSupport.findAnnotation(parameterContext.getParameter(), UserType.class)
                .orElseThrow(() -> new ParameterResolutionException("UserType annotation is missing"));
        return (StaticUser) extensionContext.getStore(NAMESPACE).get(extensionContext.getUniqueId(), Map.class).get(userType);
    }

    private Queue<StaticUser> getQueue(UserType userType) {
        return switch (userType.value()) {
            case EMPTY -> EMPTY_USERS;
            case WITH_FRIEND -> WITH_FRIEND_USERS;
            case WITH_INCOME_REQUEST -> WITH_INCOME_REQUEST_USERS;
            case WITH_OUTCOME_REQUEST -> WITH_OUTCOME_REQUEST_USERS;
        };
    }
}