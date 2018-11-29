package nextstep.domain;

import nextstep.CannotDeleteException;
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

    /**
     * 답변 삭제
     * @param loginUser
     * @return
     * @throws CannotDeleteException
     */
    public List<DeleteHistory> deleteAnswers(User loginUser) throws CannotDeleteException {
        List<DeleteHistory> deleteHistories = new ArrayList<>();
        for (Answer answer : answers) {
            deleteHistories.add(answer.delete(loginUser));
        }

        return deleteHistories;
    }

    public void add(Answer answer) {
        answers.add(answer);
    }

    public List<Answer> getAnswers() {
        return answers;
    }

    public int size() {
        return answers.size();
    }

    public Answer get(int index) {
        return answers.get(index);
    }
}
