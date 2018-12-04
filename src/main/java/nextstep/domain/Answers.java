package nextstep.domain;

import nextstep.CannotDeleteException;
import org.hibernate.annotations.Where;

import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Embedded 를 사용하기 위해선 Embeddable 선언이 되어있어야 주입됨.
 * @id 속성을 가질 수 없음
 */
@Embeddable
public class Answers {

    @OneToMany(mappedBy = "question", cascade = CascadeType.ALL)
    @Where(clause = "deleted = false")
    @OrderBy("id ASC")
    private List<Answer> answers = new ArrayList<>();

    public List<DeleteHistory> deleteAllAnswer(User loginUser) throws CannotDeleteException {
        List<DeleteHistory> histories = new ArrayList();
        if (answers.isEmpty()) {
            return histories;
        }
        return this.answers.stream().map(e -> e.delete(loginUser)).collect(Collectors.toList());
    }

    public void addAnswer(Question question, Answer answer) {
        answer.toQuestion(question);
        answers.add(answer);
    }
}
