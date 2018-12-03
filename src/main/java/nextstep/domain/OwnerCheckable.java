package nextstep.domain;

import nextstep.UnAuthorizedException;

public interface OwnerCheckable {
    boolean isOwner(User user);

    default void hasAuthority(User loginUser) {
        if (!isOwner(loginUser)) {
            throw new UnAuthorizedException("사용자가 일치하지 않음");
        }
    }
}
