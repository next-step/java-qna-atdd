package nextstep.domain;

import org.hibernate.annotations.Where;

import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import java.util.ArrayList;
import java.util.List;

@Embeddable
public class Answers {

    @OneToMany(mappedBy = "question", cascade = CascadeType.ALL)
    @Where(clause = "deleted = false")
    @OrderBy("id ASC")
    private List<Answer> answers = new ArrayList<>();

    public Answers() {}

    public void addAnswer(Answer answer) {
        answers.add(answer);
    }

    public List<Answer> getAnswers() {
        return answers;
    }

    public boolean isMatchedWriter(User loginUser) {
        for(Answer answer : answers) {
            if(!loginUser.equals(answer.getWriter())) {
                return false;
            }
        }
        return true;
    }

    public List<DeleteHistory> deleteAllAnswer(User loginUser) {
        List<DeleteHistory> deleteHistories = new ArrayList<>();
        for(Answer answer : answers) {
            deleteHistories.add(answer.delete(loginUser));
        }
        return deleteHistories;
    }

}
