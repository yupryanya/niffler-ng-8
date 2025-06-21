package guru.qa.niffler.model;

import java.util.ArrayList;
import java.util.List;

public record TestData(
    String password,
    List<CategoryJson> categories,
    List<SpendJson> spends,
    List<UserJson> friends,
    List<UserJson> outcomeInvitations,
    List<UserJson> incomeInvitations
) {
  public static TestData emptyTestData() {
    return new TestData(
        null,
        new ArrayList<>(),
        new ArrayList<>(),
        new ArrayList<>(),
        new ArrayList<>(),
        new ArrayList<>()
    );
  }

  public TestData withPassword(String password) {
    return new TestData(
        password,
        this.categories,
        this.spends,
        this.friends,
        this.outcomeInvitations,
        this.incomeInvitations
    );
  }

  public TestData withCategories(List<CategoryJson> categories) {
    return new TestData(
        this.password,
        categories,
        this.spends,
        this.friends,
        this.outcomeInvitations,
        this.incomeInvitations
    );
  }

  public TestData withSpends(List<SpendJson> spends) {
    return new TestData(
        this.password,
        this.categories,
        spends,
        this.friends,
        this.outcomeInvitations,
        this.incomeInvitations
    );
  }

  public TestData withFriends(List<UserJson> friends) {
    return new TestData(
        this.password,
        this.categories,
        this.spends,
        friends,
        this.outcomeInvitations,
        this.incomeInvitations
    );
  }

  public TestData withOutcomeInvitations(List<UserJson> outcomeInvitations) {
    return new TestData(
        this.password,
        this.categories,
        this.spends,
        this.friends,
        outcomeInvitations,
        this.incomeInvitations
    );
  }

  public TestData withIncomeInvitations(List<UserJson> incomeInvitations) {
    return new TestData(
        this.password,
        this.categories,
        this.spends,
        this.friends,
        this.outcomeInvitations,
        incomeInvitations
    );
  }
}