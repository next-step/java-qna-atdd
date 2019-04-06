package nextstep.domain;

import nextstep.UnAuthenticationException;
import org.hibernate.annotations.Where;

import javax.persistence.CascadeType;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import java.util.ArrayList;
import java.util.List;

public class Answers {
    @OneToMany(mappedBy = "question", cascade = CascadeType.ALL)
    @Where(clause = "deleted = false")
    @OrderBy("id ASC")
    private List<Answer> answers = new ArrayList<>();

    public Answers() {
    }

    public void add(Answer answer) {
        answers.add(answer);
    }

    public List<DeleteHistory> deleteAnswer(User loginUser) throws UnAuthenticationException {
        List<DeleteHistory> deleteHistories = new ArrayList<>();
        for (Answer answer : answers) {
            deleteHistories.add(answer.delete(loginUser));
        }
        return deleteHistories;

    }

}
