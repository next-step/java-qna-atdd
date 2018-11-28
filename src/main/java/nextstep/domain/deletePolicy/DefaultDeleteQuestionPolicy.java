package nextstep.domain.deletePolicy;

import nextstep.domain.DeletePolicy;
import nextstep.domain.Question;
import nextstep.domain.User;
import org.springframework.stereotype.Component;

@Component("deleteQuestionPolicy")
public class DefaultDeleteQuestionPolicy implements DeletePolicy<Question> {
    @Override
    public boolean canPermission(Question target, User user) {
        if (isOwner(target, user) && isAnswersOwner(target, user)) {
            return true;
        }
        return false;
    }

    private boolean isAnswersOwner(Question target, User user) {
        if (target.hasaAnswers() && hasaAnswerOfOtherUser(target, user)) {
            return false;
        }
        return true;
    }

    private boolean hasaAnswerOfOtherUser(Question target, User user) {
        return target.getAnswers().stream().filter(answer -> !answer.isOwner(user)).count() > 0;
    }

    private boolean isOwner(Question target, User user) {
        return target.isOwner(user);
    }
}
