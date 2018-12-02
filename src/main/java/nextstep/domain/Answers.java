package nextstep.domain;

import org.hibernate.annotations.Where;

import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import java.util.ArrayList;
import java.util.List;

import static java.util.stream.Collectors.toList;

@Embeddable
public class Answers {

    @OneToMany(mappedBy = "question", cascade = CascadeType.ALL)
    @Where(clause = "deleted = false")
    @OrderBy("id ASC")
    private List<Answer> answers = new ArrayList<>();

    public List<Answer> getAnswers() {
        return answers.stream().filter(answer -> !answer.isDeleted()).collect(toList());
    }

    public void addAnswer(final Answer answer, final Question question) {
        answer.toQuestion(question);
        answers.add(answer);
    }

    public List<DeleteHistory> deleteAll(final User loginUser) {
        List<DeleteHistory> deleteHistories = new ArrayList<>();
        for (Answer answer : getAnswers()) {
            deleteHistories.add(answer.delete(loginUser));
        }
        return deleteHistories;
    }

    public boolean hasAnswers() {
        final List<Answer> answers = getAnswers();
        return !answers.isEmpty();
    }

}
