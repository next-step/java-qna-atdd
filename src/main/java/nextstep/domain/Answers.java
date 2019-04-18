package nextstep.domain;

import nextstep.exception.CannotDeleteException;
import org.hibernate.annotations.Where;

import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Embeddable
public class Answers {
    @OneToMany(mappedBy = "question", cascade = CascadeType.ALL)
    @Where(clause = "deleted = false")
    @OrderBy("id ASC")
    private List<Answer> answers = new ArrayList<>();

    public Answers() { }

    public void add(Answer answer) {
        this.answers.add(answer);
    }

    public List<Answer> getAnswers() {
        return answers;
    }

    public boolean hasAnswer(Answer answer) {
        return answers.stream().anyMatch(savedAnswer -> savedAnswer.equals(answer));
    }

    public void delete(User loginUser) throws CannotDeleteException {
        for (Answer answer : answers) {
            answer.delete(loginUser);
        }
    }

    public List<DeleteHistory> createDeleteHistory() {
        return answers.stream()
                .map(answer -> new DeleteHistory(ContentType.ANSWER, answer.getId(), answer.getWriter()))
                .collect(Collectors.toList());
    }
}
