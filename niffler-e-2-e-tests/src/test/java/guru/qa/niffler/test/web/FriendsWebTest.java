package guru.qa.niffler.test.web;

import guru.qa.niffler.jupiter.extension.UsersQueueExtension;
import guru.qa.niffler.jupiter.extension.UsersQueueExtension.StaticUser;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static guru.qa.niffler.jupiter.extension.UsersQueueExtension.UserType;
import static guru.qa.niffler.jupiter.extension.UsersQueueExtension.UserType.Type.*;

public class FriendsWebTest extends BaseTestWeb {
    @Test
    @ExtendWith(UsersQueueExtension.class)
    void friendShouldBePresentInFriendsTable(@UserType(WITH_FRIEND) StaticUser user) {
        login(user);
        friendsPage.open()
                .verifyFriendIsPresent(user.friend());
    }

    @Test
    @ExtendWith(UsersQueueExtension.class)
    void friendTableShouldBeEmptyForNewUser(@UserType(EMPTY) StaticUser user) {
        login(user);
        friendsPage.open()
                .verifyNoFriendsPresent();
    }

    @Test
    @ExtendWith(UsersQueueExtension.class)
    void incomeInviteShouldBePresentInAllPeoplesTable(@UserType(WITH_INCOME_REQUEST) StaticUser user) {
        login(user);
        friendsPage.open()
                .verifyIncomingRequestFrom(user.income());
    }

    @Test
    @ExtendWith(UsersQueueExtension.class)
    void outcomeInviteShouldBePresentInAllPeoplesTable(@UserType(WITH_OUTCOME_REQUEST) StaticUser user) {
        login(user);
        allPeoplePage.open()
                .verifyOutcomingRequestTo(user.outcome());
    }
}