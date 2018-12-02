package nextstep.domain;

import nextstep.UnAuthorizedException;
import org.junit.Test;
import support.test.BaseTest;

import java.util.Objects;

public class OwnerCheckableTest extends BaseTest {

    @Test(expected = UnAuthorizedException.class)
    public void hasAuthority() {
        HasOwnerTarget hasOwnerTarget = new HasOwnerTarget(basicUser);
        hasOwnerTarget.hasAuthority(anotherUser);
    }

    static class HasOwnerTarget implements OwnerCheckable {
        private User user;

        public HasOwnerTarget(User user) {
            this.user = user;
        }

        @Override
        public boolean isOwner(User anotherUser) {
            return Objects.equals(user, anotherUser);
        }
    }
}