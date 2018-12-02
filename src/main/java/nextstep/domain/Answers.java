package nextstep.domain;

import nextstep.CannotDeleteException;
import org.hibernate.annotations.Where;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;


@Embeddable
public class Answers {
    @OneToMany(mappedBy = "question", cascade = CascadeType.ALL)
    @Where(clause = "deleted = false")
    @OrderBy("id ASC")
    List<Answer> answers = new ArrayList<>();

    public Answers() {
    }

    public Answers(List<Answer> answers) {
        this.answers.addAll(answers);
    }

    public void add(Answer answer, Question question) {
        answer.toQuestion(question);
        this.answers.add(answer);
    }

    public boolean isEmptyAnswer(User loginUser) {
       return answers.stream().anyMatch(answer->!answer.isOwner(loginUser));
    }

    public DeleteHistories deleteAll(User loginUser) throws CannotDeleteException {
        List<DeleteHistory> deleteHistories = new ArrayList<>();
        for (Answer answer : answers) {
            deleteHistories.add(answer.delete(loginUser));
        }
        return new DeleteHistories(deleteHistories);
    }
}
