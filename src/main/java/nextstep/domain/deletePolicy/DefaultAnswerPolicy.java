package nextstep.domain.deletePolicy;

import nextstep.domain.Answer;
import nextstep.domain.DeletePolicy;
import nextstep.domain.User;

public class DefaultAnswerPolicy implements DeletePolicy<Answer> {
    @Override
    public boolean canPermission(Answer target, User user) {
        return target.isOwner(user);
    }
}
