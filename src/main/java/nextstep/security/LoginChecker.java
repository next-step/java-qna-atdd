package nextstep.security;

import nextstep.UnAuthenticationException;
import nextstep.domain.User;

public final class LoginChecker {

    public static void check(User loginUser) throws UnAuthenticationException {
        if (loginUser.isGuestUser()) {
            throw new UnAuthenticationException();
        }
    }

    private LoginChecker() {}
}
