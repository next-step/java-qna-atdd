package nextstep.domain;

import nextstep.CannotDeleteException;
import org.hibernate.annotations.Where;

import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Embeddable
public class Answers {

    @OneToMany(mappedBy = "question", cascade = CascadeType.ALL)
    @Where(clause = "deleted = false")
    @OrderBy("id ASC")
    private List<Answer> answers = new ArrayList<>();

    public List<DeleteHistory> delete(User writer) throws CannotDeleteException {
        List<DeleteHistory> deleteHistories = new ArrayList<>();

        for (Answer answer : answers) {
            deleteHistories.add(answer.delete(writer));
        }

        return deleteHistories;
    }

    boolean allOwner(User writer) {
        return answers.stream()
                .allMatch(answer -> answer.isOwner(writer));
    }

    public void add(Answer answer) {
        answers.add(answer);
    }

    public int size() {
        return answers.size();
    }

    public boolean allDeleted() {
        return answers.stream()
                .allMatch(answer -> answer.isDeleted());
    }

    public List<Answer> getAnswers() {
        return Collections.unmodifiableList(answers);
    }
}
