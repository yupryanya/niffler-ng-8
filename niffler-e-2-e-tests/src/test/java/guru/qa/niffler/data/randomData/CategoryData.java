package guru.qa.niffler.data.randomData;

import com.github.javafaker.Faker;

public class CategoryData {
    private static final Faker faker = new Faker();

    public static String newCategoryName() {
        return faker.commerce().department() + "-" + java.util.UUID.randomUUID();
    }

    public static String newDescription() {
        return faker.commerce().productName() + "-" + java.util.UUID.randomUUID();
    }
}
