package nextstep.domain;

import nextstep.CannotDeleteException;
import org.hibernate.annotations.Where;

import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Embeddable
public class Answers implements Serializable {

    @OneToMany(mappedBy = "question", cascade = CascadeType.ALL)
    @Where(clause = "deleted = false")
    @OrderBy("id ASC")
    private List<Answer> answers = new ArrayList<>();

    public Answers() {}

    void addAnswer(Answer answer) {
        answers.add(answer);
    }

    DeleteHistories deleteAll(User loginUser) throws CannotDeleteException {
        if (isAnswersOfOwner(loginUser)) {
            throw new CannotDeleteException("답변을 삭제할 수 없습니다.");
        }

        DeleteHistories histories = DeleteHistories.of();
        for (Answer answer : answers) {
            histories.add(answer.delete(loginUser));
        }
        return histories;
    }

    boolean hasaAnswers() {
        return !answers.isEmpty();
    }

    boolean isAnswersOfOwner(User loginUser) {
        return answers.stream().filter(answer -> !answer.isOwner(loginUser)).count() > 0;
    }
}
