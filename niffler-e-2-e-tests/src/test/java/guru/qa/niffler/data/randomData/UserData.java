package guru.qa.niffler.data.randomData;

import com.github.javafaker.Faker;

public class UserData {
    private static final Faker faker = new Faker();

    public static String nonExistentUserName() {
        return faker.name().username();
    }

    public static String newValidPassword() {
        return faker.internet().password(8, 12, true, true);
    }

    public static String shortUsername() {
        return faker.name().username().substring(0, 2);
    }

    public static String longUsername() {
        return faker.lorem().characters(51);
    }

    public static String shortPassword() {
        return faker.internet().password(1, 2, true, true);
    }

    public static String longPassword() {
        return faker.internet().password(13, 20, true, true);
    }
}
