package nextstep.domain.deletePolicy;

import nextstep.domain.DeletePolicy;
import nextstep.domain.Question;
import nextstep.domain.User;
import org.springframework.stereotype.Component;

@Component("deleteQuestionPolicy")
public class DefaultDeleteQuestionPolicy implements DeletePolicy<Question> {
    @Override
    public boolean canPermission(Question target, User user) {
        if (isOwner(target, user) && isAnswersOfOwner(target, user)) {
            return true;
        }
        return false;
    }

    private boolean isAnswersOfOwner(Question target, User user) {
        if (target.hasaAnswers() && target.isAnswersOfOwner(user)) {
            return false;
        }
        return true;
    }

    private boolean isOwner(Question target, User user) {
        return target.isOwner(user);
    }
}
